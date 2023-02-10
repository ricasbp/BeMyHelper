package com.example.bemyhelper;

import android.os.Bundle;
import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileFragment extends Fragment {


    private View view;

    private UserViewModel userViewModel;

    private User currentUser;
    private Button createAccount;
    private EditText name;
    private EditText email;
    private EditText phone;

    LinearLayout myDisabilitiesLayout;
    LinearLayout otherDisabilitiesLayout;

    private Map<View, Disabilities> disabilitiesRelatedMap;

    private Map<View, Disabilities> disabilitiesUnrelatedMap;


    @Override
    public void onDetach() {
        Button refreshButton = getActivity().findViewById(R.id.refreshButtonHome);
        if(refreshButton != null) {
            refreshButton.setVisibility(View.VISIBLE);
        }
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        disabilitiesRelatedMap = new HashMap<>();
        disabilitiesUnrelatedMap = new HashMap<>();
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_profile, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = (User) userViewModel.getSelectedItem().getValue();

        //Hide "refresh" button
        Button refreshButton = getActivity().findViewById(R.id.refreshButtonHome);
        if(refreshButton != null && currentUser.getIsHelper()) {
            refreshButton.setVisibility(View.INVISIBLE);
        }

        //Preencher caixas com dados do user
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        String userName = userViewModel.getSelectedItem().getValue().getName();
        TextView displayName = this.view.findViewById(R.id.profileName);
        displayName.setText(userName);


        String userEmail = userViewModel.getSelectedItem().getValue().getEmail();
        TextView displayEmail = this.view.findViewById(R.id.displayEmail);
        displayEmail.setText(userEmail);




        name = this.view.findViewById(R.id.name_edit_Text);
        name.setText(userName);

        //User can't change email
        email = this.view.findViewById(R.id.emailEdit);
        email.setText(userEmail);
        email.setVisibility(View.GONE);

        View textViewEmail = this.view.findViewById(R.id.textviewemail);
        textViewEmail.setVisibility(View.GONE);

        String userphoneNumber = userViewModel.getSelectedItem().getValue().getPhoneNumber();
        phone = this.view.findViewById(R.id.name_edit_text_telemovel);
        phone.setText(userphoneNumber);

        String imageUrl = userViewModel.getSelectedItem().getValue().getImageUrl();
        ImageView profilePic = this.view.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(imageUrl).into(profilePic);


        myDisabilitiesLayout = this.view.findViewById(R.id.disabilitiesHolder);
        otherDisabilitiesLayout = this.view.findViewById(R.id.outrasDisabilitiesHolder);


        for (Disabilities disability: userViewModel.getSelectedItem().getValue().getDisabilities()) {

            View disabilityView = AddRelatedDisabilityOption(R.layout.sample_disability_element, myDisabilitiesLayout, disability);
            disabilitiesRelatedMap.put(disabilityView, disability);

            disabilityView.findViewById(R.id.disabilityChange).setOnClickListener(button -> {
                OnRemoveDisability(disabilityView);
            });

        }


        for (Disabilities disability : Disabilities.values()) {
            if(!disabilitiesRelatedMap.containsValue(disability)){

                View disabilityView = AddUnrelatedDisabilityOption(R.layout.sample_disability_element_add, otherDisabilitiesLayout, disability);
                disabilitiesUnrelatedMap.put(disabilityView, disability);

                disabilityView.findViewById(R.id.disabilityChange).setOnClickListener(button -> {
                    OnAddDisability(disabilityView);
                });

            }
        }

        Button confirmarAlteracoes = this.view.findViewById(R.id.guardar_alteracoes);

        if (confirmarAlteracoes != null){
            confirmarAlteracoes.setOnClickListener(view -> {

                List<Disabilities> disabilitiesList = new ArrayList<>();
                for(Disabilities d: disabilitiesRelatedMap.values()){
                    disabilitiesList.add(d);
                }

                User user = userViewModel.getSelectedItem().getValue();

                user.UpdateName(String.valueOf(this.name.getText()));
                user.UpdateEmail(String.valueOf(this.email.getText()));
                user.setPhoneNumber(String.valueOf(this.phone.getText()));
                user.UpdateDisabilities(disabilitiesList);

                user.tryUpdate();
            });
        }
        return this.view;
    }


    private void OnAddDisability(View view){

        Disabilities d = disabilitiesUnrelatedMap.remove(view);
        ((ViewManager) view.getParent()).removeView(view);
        AddRelatedDisabilityOption(R.layout.sample_disability_element, myDisabilitiesLayout, d);

    }

    private void OnRemoveDisability(View view){

        Disabilities d = disabilitiesRelatedMap.remove(view);
        ((ViewManager)view.getParent()).removeView(view);
        AddUnrelatedDisabilityOption(R.layout.sample_disability_element_add, otherDisabilitiesLayout ,d);

    }

    private View AddRelatedDisabilityOption(int id, ViewGroup viewGroup, Disabilities d){

        View view = LayoutInflater.from(getContext()).inflate(id, viewGroup, false);
        ((TextView)view.findViewById(R.id.disabilityName)).setText(d.toString());
        viewGroup.addView(view);

        disabilitiesRelatedMap.put(view, d);

        view.findViewById(R.id.disabilityChange).setOnClickListener(button -> {
            OnRemoveDisability((View) button.getParent());
        });

        return view;
    }

    private View AddUnrelatedDisabilityOption(int id, ViewGroup viewGroup, Disabilities d){
        View view = LayoutInflater.from(getContext()).inflate(id, viewGroup, false);
        ((TextView)view.findViewById(R.id.disabilityName)).setText(d.toString());
        viewGroup.addView(view);

        disabilitiesUnrelatedMap.put(view, d);

        view.findViewById(R.id.disabilityChange).setOnClickListener(button -> {
            OnAddDisability((View) button.getParent());
        });

        return view;
    }


}