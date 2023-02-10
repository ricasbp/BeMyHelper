package com.example.bemyhelper;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupFragment extends Fragment {

    private View view;

    private UserViewModel userViewModel;
    private Button createAccount;
    private EditText name;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private EditText age;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button addImage;
    private ImageView profilePic;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;


    private Map<CheckBox, Disabilities> disabilitiesCheckboxes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        disabilitiesCheckboxes = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        this.view =  inflater.inflate(R.layout.fragment_signup, container, false);

        this.db = FirebaseFirestore.getInstance();

        this.name = this.view.findViewById(R.id.name_edit_Text);
        this.email = this.view.findViewById(R.id.email_edit_text);
        this.phoneNumber = this.view.findViewById(R.id.name_edit_TextTelemovel);
        this.password = this.view.findViewById(R.id.password_edit_text);
        this.age = this.view.findViewById(R.id.age_edit_text);

        this.addImage = this.view.findViewById(R.id.addImage);
        this.profilePic = this.view.findViewById(R.id.profile_image);
        this.mStorageRef = FirebaseStorage.getInstance().getReference("profilePictures");

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        // https://stackoverflow.com/questions/42608060/android-hide-an-element
        // Android: hide an element [duplicate]
        CheckBox checkBoxSim = this.view.findViewById(R.id.checkBoxSim);
        CheckBox checkBoxNao = this.view.findViewById(R.id.checkBoxNao);


        if (checkBoxSim != null){
            checkBoxSim.setOnClickListener(view -> {
                if(checkBoxSim.isChecked()){
                    System.out.println(view);
                    LinearLayout LinearLayoutCheckBoxes = this.view.findViewById(R.id.LinearLayoutCheckBoxes);
                    LinearLayoutCheckBoxes.setVisibility(View.VISIBLE);
                }else{
                    LinearLayout LinearLayoutCheckBoxes =  this.view.findViewById(R.id.LinearLayoutCheckBoxes);
                    LinearLayoutCheckBoxes.setVisibility(View.GONE);
                }

                if(checkBoxNao.isChecked()){
                    checkBoxNao.toggle();
                }
            });
        }// Fazer caso se a pessoa deselecionar

        if (checkBoxNao != null){
            checkBoxNao.setOnClickListener(view -> {
                if(checkBoxSim.isChecked()){
                    checkBoxSim.toggle();

                    LinearLayout LinearLayoutCheckBoxes = this.view.findViewById(R.id.LinearLayoutCheckBoxes);
                    LinearLayoutCheckBoxes.setVisibility(View.GONE);
                }
            });
        }

        Button btnSignUp = this.view.findViewById(R.id.signup_button);

        if (btnSignUp != null){
            btnSignUp.setOnClickListener(view -> {
                TrySignup(
                        this.name.getText().toString(),
                        this.email.getText().toString(),
                        this.password.getText().toString(),
                        this.phoneNumber.getText().toString(),
                        Integer.parseInt(this.age.getText().toString())
                );
            });
        }

        ImageButton back = this.view.findViewById(R.id.botaovoltar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        initializeCheckboxes();
        return this.view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this.getActivity()).load(mImageUri).into(profilePic);
            // ou utilizar esta linha sem o picasso profilePic.setImageURI(mImageUri);

            Toast.makeText(requireContext(), "Foto carregada com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void initializeCheckboxes() {

        CheckBox c1 = view.findViewById(R.id.checkBoxDificuldade1);
        CheckBox c2 = view.findViewById(R.id.checkBoxDificuldade2);
        disabilitiesCheckboxes.put(c1, Disabilities.SEVERE_MOTOR_DIFFICULTY);
        disabilitiesCheckboxes.put(c2, Disabilities.SLIGHT_MOTOR_DIFFICULTY);

        checkBoxToggleListener(c1 , c2);

        CheckBox c3 = view.findViewById(R.id.checkBoxDificuldade3);
        CheckBox c4 = view.findViewById(R.id.checkBoxDificuldade4);
        disabilitiesCheckboxes.put(c3, Disabilities.SEVERE_AUDITIVE_DIFFICULTY);
        disabilitiesCheckboxes.put(c4, Disabilities.SLIGHT_AUDITIVE_DIFFICULTY);

        checkBoxToggleListener(c3 , c4);

        CheckBox c5 = view.findViewById(R.id.checkBoxDificuldade5);
        CheckBox c6 = view.findViewById(R.id.checkBoxDificuldade6);
        disabilitiesCheckboxes.put(c5, Disabilities.SEVERE_VISUAL_DIFFICULTY);
        disabilitiesCheckboxes.put(c6, Disabilities.SLIGHT_VISUAL_DIFFICULTY);

        checkBoxToggleListener(c5 , c6);

        CheckBox c7 = view.findViewById(R.id.checkBoxDificuldade7);
        CheckBox c8 = view.findViewById(R.id.checkBoxDificuldade8);
        disabilitiesCheckboxes.put(c7, Disabilities.SEVERE_COGNITIVE_DIFFICULTY);
        disabilitiesCheckboxes.put(c8, Disabilities.SLIGHT_COGNITIVE_DIFFICULTY);

        checkBoxToggleListener(c7 , c8);

        CheckBox c9 = view.findViewById(R.id.checkBoxDificuldade9);
        disabilitiesCheckboxes.put(c9, Disabilities.COMMUNICATION_DIFFICULTY);
    }

    private void checkBoxToggleListener(CheckBox c1, CheckBox c2){
        c1.setOnClickListener(view -> {
            if(((CheckBox)view).isChecked()){
                c2.setChecked(false);
            }
        });

        c2.setOnClickListener(view -> {
            if(((CheckBox)view).isChecked()){
                c1.setChecked(false);
            }
        });
    }

    private List<Disabilities> GetDisabilityList(){

        List<Disabilities> disabilitiesList = new ArrayList<>();

        for(CheckBox cb: disabilitiesCheckboxes.keySet()){

            if(cb.isChecked()){
                disabilitiesList.add(disabilitiesCheckboxes.get(cb));
            }
        }

        return disabilitiesList;

    }



    public void TrySignup(String name, String email, String password, String phoneNumber, int age){
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phoneNumber", phoneNumber);
        user.put("password", password);
        user.put("age", age);
        user.put("isHelper", false);
        user.put("latitude", (double) -1);
        user.put("longitude", (double) -1);
        user.put("disabilitiesVisability", true);
        user.put("disabilities", GetDisabilityList());
        user.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/bemyhelper-de977.appspot.com/o/imagemDefault.png?alt=media&token=9ef8a378-3aec-4443-ae1e-de93f0af11f5");

        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user.put("imageUrl", uri.toString());
                            CollectionReference users = db.collection("users");
                            users.document("User:" + email.hashCode()).set(user);
                            Signup(user);
                        }
                    });
                }
            });
        }else{
            CollectionReference users = db.collection("users");
            users.document("User:" + email.hashCode()).set(user);
            Signup(user);
        }



    }

    public void Signup( Map<String, Object> user){
        userViewModel.selectItem(new DisabledUser(user.get("name").toString(), user.get("email").toString(),user.get("phoneNumber").toString(), user.get("password").toString(), (int)user.get("age"), true, GetDisabilityList(), (double) user.get("latitude"), (double) user.get("longitude"), user.get("imageUrl").toString()));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.MainFragment, Home.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

}