package com.example.bemyhelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MapManagerFragment extends Fragment {

    private View view;
    private UserViewModel userViewModel;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = userViewModel.getSelectedItem().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.view = inflater.inflate(R.layout.fragment_map_manager, container, false);

        return this.view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


        if (currentUser.getIsHelper()){

            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.mapHolder, MapHelperFragment.class, null)
                    .commit();


        }else{

            fragmentManager.beginTransaction()
                    .add(R.id.mapHolder, MapUserFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}