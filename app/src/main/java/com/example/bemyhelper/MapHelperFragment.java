package com.example.bemyhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MapHelperFragment extends MapFragment{

    Button refreshButton;

    Map<Marker, HelpRequest>  markerList;

    private LayoutInflater inflater;

    private LocationListener locationListenerGPS;

    private MarkerOptions userOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerList = new HashMap<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize view
        this.view = inflater.inflate(R.layout.fragment_home_helpers, container, false);
        this.inflater = inflater;
        super.InitializeOnCreateView();
        // Return view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        refreshButton = this.view.findViewById(R.id.refreshButtonHome);
        refreshButton.setOnClickListener(view1 -> {

            googleMap.clear();
            markerList.clear();

            DataBaseConnection.getInstance().getHelpRequestsAndOperation(h -> {
                if (h.getHelper().getEmail().equals(currentUser.getEmail()) && !h.getState().equals(HelpRequest.HelpRequestState.Finished)) {


                    String texto = "Help request from " + h.getDisabled().getName() ;


                    String imageUrl = h.getDisabled().getImageUrl();
                    Bitmap bmp = createUserBitmap(imageUrl);

                    MarkerOptions options = new MarkerOptions()
                            .position(h.getDisabled().getLastLocation())
                            .title(texto);

                    if (bmp != null) {
                        options.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                    }

                    Marker marker = googleMap.addMarker(options);
                    markerList.put(marker, h);

                    int hash = (h.getHelper().getEmail() + h.getDisabled().getEmail() + h.getRequestTime()).hashCode();

                    marker.setTag(hash);
                }

                if(userOptions != null){
                    googleMap.addMarker(userOptions);
                }

            });
        });

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        supportMapFragment.getMapAsync(googleMap -> {
            Toast.makeText(getActivity(), "A carregar o mapa!",
                    Toast.LENGTH_LONG).show();
            this.googleMap = googleMap;
            setLocationListener();
        });

    }

    public void setLocationListener() {
        while(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        this.locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                supportMapFragment.getMapAsync(googleMap -> {
                    googleMap.clear();
                    refreshButton.callOnClick();

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentUser.setLastLocation(location.getLatitude(), location.getLongitude());

                    String imageUrl = currentUser.getImageUrl();
                    Bitmap bmp = createUserBitmap(imageUrl);

                    //LatLng latLng = new LatLng(38.725935, -9.139217);
                    userOptions = new MarkerOptions()
                            .position(latLng)
                            .title("You are Here!");

                    if (bmp != null) {
                        userOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                    }


                    googleMap.addMarker(userOptions);


                    if(counter == 0) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        counter++;
                    }


                    View maps = view.findViewById(R.id.google_map);


                    View alert = inflater.inflate(R.layout.layout_help_request_alert_helper, (ViewGroup) maps, false );
                    TextView alertTimer = alert.findViewById(R.id.alertTimer);

                    Button stop = alert.findViewById(R.id.stop_help_request);

                    googleMap.setOnMarkerClickListener(marker -> {

                        if(markerList.containsKey(marker)) {
                            ConfirmHelpRequestFragment confirmHelpRequestFragment = new ConfirmHelpRequestFragment(markerList.get(marker));
                            confirmHelpRequestFragment.addOnConfirmHelpRequest((reference) -> {

                                stop.setOnClickListener(view1 -> {
                                    reference.setValue(HelpRequest.HelpRequestState.Finished);
                                });

                                HelpRequestListener listener = new HelpRequestListener((OnDataChange) -> {
                                    reference.get().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            Object value = task2.getResult().getValue();
                                            if(value.equals(HelpRequest.HelpRequestState.Finished.toString())){
                                                ((ViewGroup) maps).removeView(alert);
                                                OnDataChange.apply();
                                            }
                                        }
                                    });
                                }, 1, alertTimer, 100);
                                try {
                                    ((ViewGroup) maps).addView(alert);
                                }catch (Exception e){
                                    Log.d("ERROR", e.getStackTrace().toString());
                                }
                                listener.start();

                            });

                            confirmHelpRequestFragment.show(getActivity().getSupportFragmentManager(), confirmHelpRequestFragment.getTag());
                        }

                        return false;
                    });
                });
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, locationListenerGPS);

    }



}
