package com.example.bemyhelper;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeTypeOfUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeTypeOfUserFragment extends Fragment {


    private UserViewModel userViewModel;
    private User currentUser;


    public ChangeTypeOfUserFragment() {
        // Required empty public constructor
    }


    public static ChangeTypeOfUserFragment newInstance(String param1, String param2) {
        ChangeTypeOfUserFragment fragment = new ChangeTypeOfUserFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_change_type_of_user, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = (User)userViewModel.getSelectedItem().getValue();

        TextView typeOfUser = (TextView) v.findViewById(R.id.textView);
        typeOfUser.setText("isHelper: " + currentUser.getIsHelper());


        Button buttonBecomeHelper = v.findViewById(R.id.updateTypeOfUser);
        buttonBecomeHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser.UpdateTypeOfUser();
                String texto = "isHelper: " + currentUser.getIsHelper();

                typeOfUser.setText(texto);


                NavController navController = Navigation.findNavController(getActivity(), R.id.navHostFragment);
                navController.navigateUp();


            }

        });



        return v;
    }
}