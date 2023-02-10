package com.example.bemyhelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class MapUserFragment extends MapFragment{

    private LayoutInflater inflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize view
        this.view = inflater.inflate(R.layout.fragment_map, container, false);


        this.inflater = inflater;

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = (User) userViewModel.getSelectedItem().getValue();

        // Initialize map fragment
        supportMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
        });
        // Return view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //Botao Bottom
        Button buttonAskForAssistance = this.view.findViewById(R.id.buttonShow);
        ///Show "ask for assistance" button
        if(buttonAskForAssistance != null){
            buttonAskForAssistance.setVisibility(View.VISIBLE);


            View googleMap = super.view.findViewById(R.id.google_map);


            View alertStart = inflater.inflate(R.layout.layout_help_request_alert_user, (ViewGroup) googleMap, false );
            TextView alertStartTimer = alertStart.findViewById(R.id.alertTimer);

            View alertAccepted = inflater.inflate(R.layout.layout_help_request_alert_helper, (ViewGroup) googleMap, false );
            TextView alertAcceptedTimer = alertAccepted.findViewById(R.id.alertTimer);

            Button stop = alertAccepted.findViewById(R.id.stop_help_request);

            Button btn = alertStart.findViewById(R.id.cancel_help_request);
            btn.setOnClickListener(v -> {
                ((ViewGroup) googleMap).removeView(alertStart);
                buttonAskForAssistance.setVisibility(View.VISIBLE);
            });


            AskForAssistanceFragment askForAssistanceFragment = new AskForAssistanceFragment(reference -> {

                stop.setOnClickListener(view1 -> {
                    reference.setValue(HelpRequest.HelpRequestState.Finished);
                });

                reference.get().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Object value1 = task1.getResult().getValue();
                        HelpRequestListener listener = new HelpRequestListener((OnDataChange) -> {
                            reference.get().addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()){
                                    Object value2 = task2.getResult().getValue();
                                    if(!value2.equals(value1)){
                                            ((ViewGroup) googleMap).removeView(alertStart);
                                            ((ViewGroup) googleMap).addView(alertAccepted);


                                            OnDataChange.apply();

                                            HelpRequestListener listener2 = new HelpRequestListener((OnDataChange2) -> {
                                                reference.get().addOnCompleteListener(task3 -> {
                                                    if(task3.isSuccessful()){
                                                        Object value3 = task3.getResult().getValue();
                                                        if(!value3.equals(value2)){
                                                            ((ViewGroup) googleMap).removeView(alertAccepted);
                                                            buttonAskForAssistance.setVisibility(View.VISIBLE);
                                                            OnDataChange2.apply();
                                                        }
                                                    }
                                                });
                                            },1, alertAcceptedTimer, 100);

                                            listener2.start();

                                            buttonAskForAssistance.setVisibility(View.INVISIBLE);


                                    }
                                }
                            });
                        }, 1, alertStartTimer, 100);
                        try {
                            ((ViewGroup) googleMap).addView(alertStart);
                            buttonAskForAssistance.setVisibility(View.INVISIBLE);
                        }catch (Exception e){
                            Log.d("ERROR", e.getStackTrace().toString());
                        }

                        listener.start();
                    }
                });





            });
            buttonAskForAssistance.setOnClickListener(view1 -> askForAssistanceFragment.show(getActivity().getSupportFragmentManager(), askForAssistanceFragment.getTag()));

        }
    }




}
