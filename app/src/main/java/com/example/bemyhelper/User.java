package com.example.bemyhelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public abstract class User {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private int age;
    private Boolean isHelper;
    private double latitude;
    private double longitude;
    private Boolean areDisabilitiesPublic;
    private List<Disabilities> disabilities;
    private String imageUrl;
    private HelpRequest lastHelpHistoryItemClicked;


    public User(String name, String email, String phoneNumber, String password, int age, Boolean areDisabilitiesPublic, List<Disabilities> disabilities, double latitude, double longitude, String imageUrl){

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.age = age;
        this.isHelper = false;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.areDisabilitiesPublic = areDisabilitiesPublic;
        this.disabilities = disabilities;
    }

    protected void SetHelper(){
        this.isHelper = true;
    }



    public void UpdateTypeOfUser(){
        this.isHelper = !this.isHelper;
        tryUpdate();
    }

    public void setLastLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        tryUpdate();
    }

    public LatLng getLastLocation() {
        return new LatLng(latitude, longitude);
    }

    // Update the user on the DB
    public void tryUpdate(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document("User:" + email.hashCode());

        docRef.get().addOnCompleteListener(task -> {
            Map<String, Object> user = task.getResult().getData();
            user.put("name", name);
            user.put("email", email);
            user.put("password", password);
            user.put("phoneNumber", phoneNumber);
            user.put("age", age);
            user.put("isHelper", this.isHelper);
            user.put("disabilitiesVisability",areDisabilitiesPublic );
            user.put("disabilities", disabilities );
            user.put("latitude", latitude);
            user.put("longitude", longitude);
            user.put("imageUrl", imageUrl);

            CollectionReference users = db.collection("users");
            users.document("User:" + email.hashCode()).set(user);
        });


    }

    public HelpRequest getLastHelpHistoryItemClicked() {
        return this.lastHelpHistoryItemClicked;
    }

    public boolean getIsHelper(){
        return isHelper;
    }

    public List<Disabilities> getDisabilities(){
        return this.disabilities;
    }

    public String getName(){
        return new String(name);
    }

    public String getEmail(){
        return this.email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public Boolean getHelper() {
        return isHelper;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void UpdateDisabilities(List<Disabilities> disabilitiesList) {
        this.disabilities = disabilitiesList;
    }

    public void UpdateName(String name) {
        this.name = name;
    }

    public void UpdateEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHelper(Boolean helper) {
        isHelper = helper;
    }

    public void setLastHelpHistoryItemClicked(HelpRequest h) {
        this.lastHelpHistoryItemClicked = h;
    }
}
