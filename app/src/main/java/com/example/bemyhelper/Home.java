package com.example.bemyhelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;


public class Home extends Fragment {

    //private View view;
    private UserViewModel userViewModel;
    private User currentUser;
    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.view = inflater.inflate(R.layout.fragment_home, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = userViewModel.getSelectedItem().getValue();

        return this.view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        //Navegacao pelo sidemenu
        final DrawerLayout drawerLayout = this.view.findViewById(R.id.drawerLayout);

        this.view.findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = this.view.findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        //Change profile name && Type of user
        View headerLayout = navigationView.inflateHeaderView(R.layout.layout_navigation_header);

        TextView name = headerLayout.findViewById(R.id.NavigationHeaderName);
        name.setText(currentUser.getName());

        TextView typeOfUser = headerLayout.findViewById(R.id.TypeOfUser);
        typeOfUser.setText(currentUser.getIsHelper() ? "Helper" : "User");


        // Ir buscar à bd a foto de perfil
        ImageView profilePic = headerLayout.findViewById(R.id.imageProfile);

        // Define a imagem de perfil do utilizador
        Picasso.with(getContext()).load(currentUser.getImageUrl()).into(profilePic);

        NavController navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public void onResume() {
        super.onResume();
    }


}

