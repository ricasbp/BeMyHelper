package com.example.bemyhelper;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public abstract  class MapFragment extends Fragment {

    protected View view;
    protected GoogleMap googleMap;
    protected UserViewModel userViewModel;
    protected User currentUser;
    protected SharedPreferences mPrefs;
    protected LocationManager locationManager;
    protected SupportMapFragment supportMapFragment;
    protected FusedLocationProviderClient client;
    protected Map<Marker, Helper> markers = new HashMap<>();
    protected int counter = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getActivity().getPreferences(MODE_PRIVATE);
    }

    public void InitializeOnCreateView(){
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = (User) userViewModel.getSelectedItem().getValue();

        // Initialize map fragment
        supportMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        InitializeView();

    }

    protected void InitializeView(){
        supportMapFragment.getMapAsync(googleMap -> {
            Toast.makeText(getActivity(), "A carregar o mapa!",
                    Toast.LENGTH_LONG).show();
            this.googleMap = googleMap;
            setLocationListener();
        });
    }

    protected void setLocationListener() {
        while(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        LocationListener locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                supportMapFragment.getMapAsync(googleMap -> {

                    googleMap.clear();

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    currentUser.setLastLocation(location.getLatitude(), location.getLongitude());
                    Bitmap bmp = createUserBitmap(currentUser.getImageUrl());

                    // LatLng latLng = new LatLng(38.725935, -9.139217);
                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title("I am here")
                            .anchor(0.5f, 0.907f);

                    if (bmp != null) {
                        options.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                    }

                    googleMap.addMarker(options);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    assinalarHelpersNoMapa();
                });

            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, locationListenerGPS);

    }

    protected void assinalarHelpersNoMapa() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Marcar os Helpers no Mapa
        db.collection("users")
                .whereEqualTo("isHelper", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String username = Objects.requireNonNull(document.get("name")).toString();
                                String email = Objects.requireNonNull(document.get("email")).toString();
                                String phoneNumber = Objects.requireNonNull(document.get("phoneNumber")).toString();
                                String password = Objects.requireNonNull(document.get("password")).toString();
                                int age = Objects.requireNonNull(document.getLong("age")).intValue();
                                double latitude = Objects.requireNonNull(document.getDouble("latitude"));
                                double longitude = Objects.requireNonNull(document.getDouble("longitude"));
                                String imageUrl = document.get("imageUrl").toString();
                                boolean disabilitiesVisability = true;
                                Object disabilitiesRaw = document.get("disabilities");
                                List<Disabilities> disabilities = new ArrayList<>();
                                if(disabilitiesRaw != null){
                                    for(String d : (List<String>)disabilitiesRaw){
                                        disabilities.add(Disabilities.valueOf(d));

                                    }
                                }
                                Bitmap bmp = createUserBitmap(imageUrl);
                                Helper helper = new Helper(username, email, phoneNumber, password,age, disabilitiesVisability, disabilities, latitude, longitude, imageUrl);
                                MarkerOptions options = new MarkerOptions().position(helper.getLastLocation()).title("Helper: " + username);

                                if (bmp != null) {
                                    options.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                }

                                Marker m = googleMap.addMarker(options);
                                markers.put(m, (Helper) helper);
                            }

                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    Helper helper = markers.get(marker);

                                    for (Map.Entry<Marker, Helper> entry : markers.entrySet()) {
                                        System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
                                    }

                                    if(helper != null){
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                        prefsEditor.putString("imageUrl", helper.getImageUrl());
                                        prefsEditor.putString("helperName", helper.getName());
                                        prefsEditor.putString("helperEmail", helper.getEmail());
                                        prefsEditor.putString("helperDistance", "Por definir");
                                        prefsEditor.apply();

                                        HelperProfileFragment helperProfile = new HelperProfileFragment(reference -> {

                                        });

                                        helperProfile.show(getActivity().getSupportFragmentManager(), helperProfile.getTag());
                                    }
                                    return false;
                                }
                            });
                        } else {
                            Log.d("AQUI", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    protected Bitmap createUserBitmap(String profile) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();

            URL imageUrl = new URL(profile);
            Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(supportMapFragment != null){
            counter = 0;
            InitializeView();
        }
    }

}