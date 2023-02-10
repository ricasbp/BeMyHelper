package com.example.bemyhelper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ConfirmHelpRequestFragment extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    private View view;
    private SharedPreferences mPrefs;
    private FirebaseFirestore db;
    private HelpRequest request;
    private OnConfirmHelpRequest onConfirmHelpRequest;

    public ConfirmHelpRequestFragment(HelpRequest request){
        this.request = request;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        setShowsDialog(false);
        mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return  bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_marker_user_request, container, false);


        //IMAGEM DE PERFIL
        ImageView profileImage = view.findViewById(R.id.profileImage);
        Picasso.with(getContext()).load(request.getImageUrl()).into(profileImage);

        TextView pessoaQuePediuAjuda = view.findViewById(R.id.pessoa_que_pediu_ajuda);
        pessoaQuePediuAjuda.setText(request.getDisabled().getName());


        TextView dataTextView = view.findViewById(R.id.request_timestamp);
        dataTextView.setText(request.getRequestTime());

        LinearLayout disabilities = view.findViewById(R.id.disabilitiesHolder);

        int disabilitesCount = 0;


        for(Disabilities d: request.getDisabled().getDisabilities()){
            TextView disabilityTextView = new TextView(getContext());
            disabilityTextView.setText(d.toString());
            disabilities.addView(disabilityTextView);
            disabilitesCount++;
        }
        if(disabilitesCount == 0){
            TextView disabilityTextView = new TextView(getContext());
            disabilityTextView.setText("N/A");
            disabilities.addView(disabilityTextView);
        }

        TextView descriptionTextView = view.findViewById(R.id.request_description);
        descriptionTextView.setText("\"" + request.getDescription() + "\"");
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        Button confirmButton = view.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(view -> {
            int hash = (request.getHelper().getEmail() + request.getDisabled().getEmail() + request.getRequestTime()).hashCode();

            DatabaseReference reference = DataBaseConnection.getInstance().acceptHelpRequest(String.valueOf(hash));

            if(this.onConfirmHelpRequest != null){
                onConfirmHelpRequest.apply(reference);
            }

            dismiss();

        });


        return view;
    }


    public void addOnConfirmHelpRequest(OnConfirmHelpRequest onConfirmHelpRequest){
        this.onConfirmHelpRequest = onConfirmHelpRequest;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        LinearLayout layout = bottomSheetDialog.findViewById(R.id.bottomMarkerContent);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
    }

    interface OnConfirmHelpRequest{
        void apply(DatabaseReference reference);
    }

}