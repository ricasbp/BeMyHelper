package com.example.bemyhelper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

public class LoginSignupFragment extends Fragment {

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DataBaseConnection.getInstance();

        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_login_signup, container, false);


        Button btnLogin = this.view.findViewById(R.id.login_button);


        if (btnLogin != null){
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NotNull View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.MainFragment, LoginFragment.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                }
            });
        }


        Button btnSignUp = this.view.findViewById(R.id.signup_button);

        if (btnSignUp != null){
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NotNull View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.MainFragment, SignupFragment.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                }
            });
        }
      return this.view;

    }
}