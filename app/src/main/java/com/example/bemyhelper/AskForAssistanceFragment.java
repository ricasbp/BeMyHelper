package com.example.bemyhelper;

import static android.content.Context.MODE_PRIVATE;
import static android.location.Location.distanceBetween;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AskForAssistanceFragment extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    View view;

    private SharedPreferences mPrefs;
    private FirebaseFirestore db;
    private ArrayList<User> users;

    private User currentUser;
    private UserViewModel userViewModel;


    private ValueEventListener valueEventListener;
    private RequestDetailsFragment.OnAskForAssistance onAskForAssistance;

    public AskForAssistanceFragment(RequestDetailsFragment.OnAskForAssistance onAskForAssistance){
        this.onAskForAssistance = onAskForAssistance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        users = new ArrayList<>();


        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = (User) userViewModel.getSelectedItem().getValue();

        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return  bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ask_for_assistance, container, false);

        LinearLayout layout = view.findViewById(R.id.profile_helper_container);
        Random rd = new Random();
        int num_profiles = 10;

        View[] profiles = new View[num_profiles];


        // ir buscar utilizadores e verificar se são helpers
        db.collection("users")
                .whereEqualTo("isHelper", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.get("name").toString();
                            String email = document.get("email").toString();
                            String phoneNumber = document.get("phoneNumber").toString();
                            String password = document.get("password").toString();
                            int age = Objects.requireNonNull(document.getLong("age")).intValue();

                            boolean disabilitiesVisability = true;
                            Object disabilitiesRaw = (Object) document.get("disabilities");
                            List<Disabilities> disabilities = new ArrayList<>();
                            if(disabilitiesRaw != null){
                                for(String s: (List<String>)disabilitiesRaw){
                                    disabilities.add(Disabilities.valueOf(s));
                                }
                            }

                            View v = inflater.inflate(R.layout.profile_helper_preview, layout, false );
                            TextView name = v.findViewById(R.id.profile_helper_preview_name);
                            ImageView profilePic = v.findViewById(R.id.profile_image);

                            TextView distance = v.findViewById(R.id.profile_helper_preview_distance);


                            Double latitude = document.get("latitude", Double.class);
                            Double longitude = document.get("longitude", Double.class);

                            //Calculate distance between them
                            LatLng currentUserLocation = currentUser.getLastLocation();
                            LatLng currentHelperLocation = new LatLng(latitude, longitude);

                            Location tempUser = new Location(LocationManager.GPS_PROVIDER);
                            tempUser.setLatitude(currentUserLocation.latitude);
                            tempUser.setLongitude(currentUserLocation.longitude);

                            Location tempHelper = new Location(LocationManager.GPS_PROVIDER);
                            tempHelper.setLatitude(currentHelperLocation.latitude);
                            tempHelper.setLongitude(currentHelperLocation.longitude);

                            float distanceFloat = tempHelper.distanceTo(tempUser);

                            int distanceFinal = (int) distanceFloat;


                            distance.setText(distanceFinal +  " m");




                            // Define a imagem de perfil do utilizador
                            String imageUrl = document.get("imageUrl").toString();
                            if (!imageUrl.equals("fotodeperfil")) {
                                Picasso.with(getContext()).load(imageUrl).into(profilePic);
                            }

                            User helper = new Helper(username,email, phoneNumber,password,age, disabilitiesVisability, disabilities,latitude, longitude, imageUrl);
                            name.setText(helper.getName());
                            layout.addView(v);
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dismiss();
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    prefsEditor.putString("helper", document.getId());
                                    prefsEditor.apply();

                                    RequestDetailsFragment helper = new RequestDetailsFragment(onAskForAssistance);
                                    helper.show(requireActivity().getSupportFragmentManager(), helper.getTag());
                                }
                            });
                        }


                    } else {
                        Log.d("AQUI", "Error getting documents: ", task.getException());
                    }

                });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        CoordinatorLayout layout = bottomSheetDialog.findViewById(R.id.AskForAssistanceLayout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
    }
}