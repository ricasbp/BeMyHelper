package com.example.bemyhelper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


public class RequestDetailsFragment extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    View view;

    private FirebaseFirestore db;
    private SharedPreferences mPrefs;
    TextView name;
    TextView email;
    TextView distance;

    Button confirmAssistanceButton;

    private UserViewModel userViewModel;
    private User currentUser;

    private OnAskForAssistance onAskForAssistance;

    public RequestDetailsFragment(OnAskForAssistance onAskForAssistance){
        this.onAskForAssistance = onAskForAssistance;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        String helperId = mPrefs.getString("helper", "");
        db = FirebaseFirestore.getInstance();

        if (!helperId.equals("")) {
            db.collection("users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(helperId)) {
                                    name = view.findViewById(R.id.profile_helper_preview_name);
                                    email = view.findViewById(R.id.profile_helper_preview_email);
                                    distance = view.findViewById(R.id.profile_helper_preview_distance);
                                    name.setText((CharSequence) document.getData().get("name"));
                                    email.setText((CharSequence) document.getData().get("email"));
                                    distance.setText("Por definir");

                                    // Define a imagem de perfil do utilizador
                                    String imageUrl = document.get("imageUrl").toString();
                                    ImageView profilePic = view.findViewById(R.id.profile_image);
                                    if (!imageUrl.equals("fotodeperfil")) {
                                        Picasso.with(getContext()).load(imageUrl).into(profilePic);
                                    }

                                }
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    });
        }

        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return  bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_details, container, false);
        return view;
    }

    public View getView() {
        return this.view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);


        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = (User)userViewModel.getSelectedItem().getValue();
        confirmAssistanceButton = view.findViewById(R.id.ConfirmAssistanceButton);

        confirmAssistanceButton.setOnClickListener(view1 -> {
            confirmAssistanceButton.getText();
            DataBaseConnection.getInstance().getUserAndOperation(email.getText().toString(), u -> {
                EditText editText = getView().findViewById(R.id.descriptionHelp);

                onAskForAssistance.apply(
                        DataBaseConnection.getInstance().putHelpRequest(new HelpRequest(u, currentUser, editText.getText().toString(), currentUser.getImageUrl(), u.getImageUrl()))
                );

            });


            dismiss();
        });

        LinearLayout layout = bottomSheetDialog.findViewById(R.id.HelperProfileDetails);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    interface OnAskForAssistance{
        void apply(DatabaseReference reference);
    }

}