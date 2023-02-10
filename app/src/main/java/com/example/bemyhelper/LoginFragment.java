package com.example.bemyhelper;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginFragment extends Fragment {

    private View view;
    private FirebaseFirestore db;

    private Button login;
    private Button login2User;
    private Button login3Helper;

    private EditText email;
    private EditText password;
    private TextView createAccount;

    private TextView wrongLogin;

    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_login, container, false);
        this.db = FirebaseFirestore.getInstance();

        this.login = this.view.findViewById(R.id.login_button);
        this.email = this.view.findViewById(R.id.email_edit_text);
        this.password = this.view.findViewById(R.id.password_edit_text);

        email.setOnClickListener(view -> {
            wrongLogin.setVisibility(View.INVISIBLE);
        });

        password.setOnClickListener(view -> {
            wrongLogin.setVisibility(View.INVISIBLE);
        });

        this.createAccount = this.view.findViewById(R.id.create_account_text);

        this.createAccount.setOnClickListener(view -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.MainFragment, SignupFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // name can be null
                    .commit();
        });

        this.wrongLogin = this.view.findViewById(R.id.WrongLogin);
        wrongLogin.setVisibility(View.INVISIBLE);


        this.login.setOnClickListener(view -> {
            this.TryLogin(this.email.getText().toString(), this.password.getText().toString());
        });


        this.login2User = this.view.findViewById(R.id.login_button2);

        this.login2User.setOnClickListener(view -> {
            this.TryLogin("lauren@gmail.com", "123456789");

        });

        this.login3Helper = this.view.findViewById(R.id.login_button3);

        this.login3Helper.setOnClickListener(view -> {
            this.TryLogin("dwight@gmail.com", "123456789");
        });

        login2User.setVisibility(View.GONE);
        login3Helper.setVisibility(View.GONE);

        ImageButton back = this.view.findViewById(R.id.botaovoltar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });





        return this.view;
    }

    public void TryLogin(String email, String password){

        DocumentReference docRef = db.collection("users").document("User:" + email.hashCode());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> userDocument = document.getData();

                        try {
                            String password_BD  = userDocument.get("password").toString();
                            if(password_BD.equals(password)){
                                Log.d("testeTuga", " " + "entrei");
                                Login(userDocument);
                            }else{
                                WrongLogin();

                            }

                        }catch (NullPointerException e){
                            WrongLogin();

                        }

                    } else {
                        WrongLogin();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void WrongLogin(){
        wrongLogin.setVisibility(View.VISIBLE);
        this.password.setText("");
    }


    public void Login(Map<String, Object> userDocument){

        try {
            String name = userDocument.get("name").toString();
            String email = userDocument.get("email").toString();
            String password = userDocument.get("password").toString();
            String phoneNumber = userDocument.get("phoneNumber").toString();
            int age = Integer.parseInt(userDocument.get("age").toString());
            boolean disabilitiesVisability = true;
            Object disabilitiesRaw = userDocument.get("disabilities");
            List<Disabilities> disabilities = new ArrayList<>();
            if(disabilitiesRaw != null){
                for(String d : (List<String>)disabilitiesRaw){
                    disabilities.add(Disabilities.valueOf(d));

                }
            }


            double latitude = Double.parseDouble(userDocument.get("latitude").toString());
            double longitude = Double.parseDouble(userDocument.get("longitude").toString());

            String imageUrl = userDocument.get("imageUrl").toString();

            Boolean isHelper = (Boolean) userDocument.get("isHelper");

            if (isHelper){
                Helper user = new Helper(name, email, phoneNumber, password, age, disabilitiesVisability, disabilities, latitude, longitude, imageUrl);
                userViewModel.selectItem(user);
            }else{
                DisabledUser user = new DisabledUser(name, email, phoneNumber, password, age, false, disabilities, latitude, longitude, imageUrl);
                userViewModel.selectItem(user);
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.MainFragment, Home.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // name can be null
                    .commit();

        }catch (NullPointerException e){

            e.getStackTrace();
            e.toString();
            //TODO: Não deixar fazer login se algum atributo estiver em falta
            Log.d(e.toString(), " " + "NullPointerException");

        }

    }

}