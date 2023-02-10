package com.example.bemyhelper;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;


public class HelpReviewFragment extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    View view;

    private UserViewModel userViewModel;
    private User currentUser;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return  bottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = userViewModel.getSelectedItem().getValue();

        view = inflater.inflate(R.layout.fragment_help_review, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        LinearLayout layout = bottomSheetDialog.findViewById(R.id.HelpReviewFragment);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        RatingBar simpleRatingBar = (RatingBar) getView().findViewById(R.id.simpleRatingBar); // initiate a rating bar
        simpleRatingBar.setNumStars(5); // set total number of stars

        HelpRequest hr = currentUser.getLastHelpHistoryItemClicked();
        String nomeDaPessoaQueVaiSerAvaliada = hr.getHelper().getName();

        String imageUrl = hr.getImageUrlHelper();
        ImageView profilePic = this.view.findViewById(R.id.profile_image_review);
        Picasso.with(getContext()).load(imageUrl).into(profilePic);

        TextView tv1 = view.findViewById(R.id.profile_helper_review_name);
        tv1.setText(nomeDaPessoaQueVaiSerAvaliada);




        Button submitButton = (Button) getView().findViewById(R.id.submitButton);
        EditText descricao = view.findViewById(R.id.descriptionReview);
        if(currentUser.getIsHelper() || !hr.getReviewDescription().equals("N/A")) {
            submitButton.setVisibility(View.INVISIBLE);
            descricao.setVisibility(View.INVISIBLE);

            if(!hr.getReviewDescription().equals("N/A")) {
                TextView tvestrelas = view.findViewById(R.id.estrelas);
                tvestrelas.setText("Rating: " + hr.getReviewStars());

                TextView tvReview = view.findViewById(R.id.review);
                tvReview.setText("Review: " + hr.getReviewDescription());

                simpleRatingBar.setIsIndicator(true);
                simpleRatingBar.setRating((float) hr.getReviewStars());

            }

        }




        // perform click event on button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpRequest request = currentUser.getLastHelpHistoryItemClicked();

                String descricaoString = descricao.getText().toString();
                float ratingFloat = simpleRatingBar.getRating();




                request.setReviewDescription(descricaoString);
                request.setReviewStars(ratingFloat);

                // get values and then displayed in a toast
                String totalStars = "Total Stars:: " + simpleRatingBar.getNumStars();
                String rating = "Rating :: " + simpleRatingBar.getRating();


                int hash = (request.getHelper().getEmail() + request.getDisabled().getEmail() + request.getRequestTime()).hashCode();

                DataBaseConnection.getInstance().updateHelpRequest(String.valueOf(hash), request);
                Toast.makeText(getActivity().getApplicationContext(), "Avaliado com: " + rating + "/" + totalStars, Toast.LENGTH_LONG).show();



            }
        });

    }
}