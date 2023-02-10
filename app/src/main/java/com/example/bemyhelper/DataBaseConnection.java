package com.example.bemyhelper;

import static android.content.ContentValues.TAG;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import android.content.Context;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.callback.Callback;
import javax.xml.transform.Result;

public class DataBaseConnection {

    private static DataBaseConnection connection;
    private DatabaseReference databaseReference;
    private FirebaseFirestore db;
    private FirebaseFunctions mFunctions;



    public DataBaseConnection(){
        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://bemyhelper-de977-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        mFunctions = FirebaseFunctions.getInstance();
    }

    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    interface UserCallback {
        void isUserExist(User u);
    }

    interface HelpRequestCallBack{
        void onDataChanged();
    }

    public static DataBaseConnection getInstance()
    {
        if (connection == null){
            connection = new DataBaseConnection();
        }
        return connection;
    }

    public DatabaseReference putHelpRequest(HelpRequest request){

        int hash = (request.getHelper().getEmail() + request.getDisabled().getEmail() + request.getRequestTime()).hashCode();
        DatabaseReference requestRef = databaseReference.child("requests").child(String.valueOf(hash));

        requestRef.child("helper").setValue(request.getHelper().getEmail());
        requestRef.child("user").setValue(request.getDisabled().getEmail());
        requestRef.child("date").setValue(request.getRequestTime());
        requestRef.child("description").setValue(request.getDescription());
        requestRef.child("image").setValue(request.getImageUrl());


        requestRef.child("imageHelper").setValue(request.getImageUrlHelper());
        requestRef.child("reviewDescription").setValue(request.getReviewDescription());
        requestRef.child("reviewStars").setValue(request.getReviewStars());

        requestRef.child("state").setValue(request.getState());


        return requestRef.child("state");
    }

    public DatabaseReference acceptHelpRequest(String hash) {

        DatabaseReference requestRef = databaseReference.child("requests").child(hash);
        requestRef.child("state").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().getValue().toString().equals(HelpRequest.HelpRequestState.Started.toString())){
                    requestRef.child("state").setValue(HelpRequest.HelpRequestState.Accepted);
                }
            }
        });

        return requestRef.child("state");
    }

    public void updateHelpRequest(String hash, HelpRequest hr) {

        DatabaseReference requestRef = databaseReference.child("requests").child(hash);
        requestRef.child("state").get().addOnCompleteListener(task -> {

        });

        requestRef.child("reviewDescription").setValue(hr.getReviewDescription());
        requestRef.child("reviewStars").setValue(hr.getReviewStars());

    }


    public void getUserAndOperation(String email, UserCallback userCallback){
        DocumentReference docRef = db.collection("users").document("User:" + email.hashCode());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Log.d("USER ", String.valueOf(document.exists()));
                if (document.exists()) {
                    Map<String, Object> userDocument = document.getData();
                    String name = userDocument.get("name").toString();
                    String email1 = userDocument.get("email").toString();
                    String password = userDocument.get("password").toString();
                    String phoneNumber = userDocument.get("phoneNumber").toString();
                    int age = Integer.parseInt(userDocument.get("age").toString());
                    boolean disabilitiesVisability = true;
                    List<String> disabilitiesRaw = (List<String>) document.get("disabilities");

                    Boolean isHelper = (Boolean) userDocument.get("isHelper");
                    List<Disabilities> disabilities = new ArrayList<>();
                    if(!isHelper){
                        for(String s : disabilitiesRaw){
                            disabilities.add(Disabilities.valueOf(s));
                        }
                    }


                    double latitude = Double.parseDouble(userDocument.get("latitude").toString());
                    double longitude = Double.parseDouble(userDocument.get("longitude").toString());

                    String imageUrl = userDocument.get("imageUrl").toString();

                    User user = null;
                    if(isHelper){
                         user = new Helper( name, email1, phoneNumber, password,  age, disabilitiesVisability, disabilities,   latitude,  longitude, imageUrl);
                    } else{
                         user = new DisabledUser( name, email1, phoneNumber,  password,  age,  true, disabilities, latitude,  longitude, imageUrl);
                    }

                    userCallback.isUserExist(user);
                }

            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    interface RequestsCallback {
        void isRequestExist(HelpRequest helpRequest);
    }

    public void getHelpRequestsAndOperation(RequestsCallback requestsCallback){
        DatabaseReference requestRef = databaseReference.child("requests");

        requestRef.get().addOnCompleteListener(task -> {


            if (task.isSuccessful()) {
                for (DataSnapshot document : task.getResult().getChildren()) {
                    HelpRequest request = new HelpRequest();

                    String helper = document.child("helper").getValue().toString();
                    Log.d("helper TAG", helper);
                    getUserAndOperation(helper, user1 -> {

                        request.setHelper(user1);
                        String disabled = document.child("user").getValue().toString();

                        Log.d("disabled TAG", disabled);
                        getUserAndOperation(disabled, user2 -> {


                            request.setDisabled(user2);
                            String data = document.child("date").getValue().toString();
                            request.setDate(data);
                            String description = document.child("description").getValue().toString();
                            request.setDescription(description);
                            String imageUrl = document.child("image").getValue().toString();
                            request.setImageUrl(imageUrl);

                            String imageHelper = document.child("imageHelper").getValue().toString();
                            request.setImageUrlHelper(imageHelper);

                            String reviewDescription = document.child("reviewDescription").getValue().toString();
                            request.setReviewDescription(reviewDescription);

                            String state = document.child("state").getValue().toString();
                            request.setState(HelpRequest.HelpRequestState.valueOf(state));

                            double reviewStars = Double.parseDouble(document.child("reviewStars").getValue().toString());
                            request.setReviewStars(reviewStars);



                            Log.d("USER TAG", request.getDisabled().getName());

                            requestsCallback.isRequestExist(request);

                        });
                    });
                }
            }
        });
    }
}
