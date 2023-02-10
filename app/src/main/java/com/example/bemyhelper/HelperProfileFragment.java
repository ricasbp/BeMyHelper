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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class HelperProfileFragment extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    View view;

    private FirebaseFirestore db;
    private SharedPreferences mPrefs;
    TextView name;
    TextView email;
    TextView distance;

    private RequestDetailsFragment.OnAskForAssistance onAskForAssistance;

    public HelperProfileFragment(RequestDetailsFragment.OnAskForAssistance onAskForAssistance){
        this.onAskForAssistance = onAskForAssistance;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mPrefs = getActivity().getPreferences(MODE_PRIVATE);

        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return  bottomSheetDialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_helper_profile, container, false);

        mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        String helperName = mPrefs.getString("helperName", "");
        String helperEmail = mPrefs.getString("helperEmail", "");
        String helperDistance = mPrefs.getString("helperDistance", "");
        String imageUrl = mPrefs.getString("imageUrl", "");


        TextView name = view.findViewById(R.id.profile_helper_preview_name);
        TextView email = view.findViewById(R.id.profile_helper_preview_email);
        TextView distance = view.findViewById(R.id.profile_helper_preview_distance);
        ImageView profileImage = view.findViewById(R.id.profile_image);

        name.setText(helperName);
        email.setText(helperEmail);
        distance.setText(helperDistance);
        Picasso.with(this.getActivity()).load(imageUrl).into(profileImage);


        View buttonAskForAssitanceInHelperProfile = this.view.findViewById(R.id.buttonAskForAssitanceInHelperProfile);

        buttonAskForAssitanceInHelperProfile.setVisibility(View.GONE);

        buttonAskForAssitanceInHelperProfile.setOnClickListener(view -> {
            RequestDetailsFragment helper = new RequestDetailsFragment(onAskForAssistance);
            helper.show(requireActivity().getSupportFragmentManager(), helper.getTag());
        });

        return this.view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        LinearLayout layout = bottomSheetDialog.findViewById(R.id.HelperProfileDetails);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
    }


}