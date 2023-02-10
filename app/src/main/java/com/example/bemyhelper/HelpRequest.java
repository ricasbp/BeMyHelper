package com.example.bemyhelper;

import android.os.Build;

import java.time.LocalDateTime;
import java.util.Date;

public class HelpRequest {

    private String imageUrlHelper;
    private User helper;

    private User disabled;

    private String requestTime;

    private String description;

    private String imageUrl;

    private HelpRequestState state;

    private String reviewDescription;
    private double reviewStars;

    @Override
    public String toString() {
        LocalDateTime a = null;
        String result = "";
        String data = "Date: " + requestTime.split("GMT")[0];
        String helper = this.helper.getName();
        String disabled = this.disabled.getName();
        String status = "Status: " + "Finished";
        String descricao = "Description: " + description;
        result = data + System.lineSeparator() + "Participants: " + helper + ", " + disabled + System.lineSeparator() + descricao
                    + System.lineSeparator() + status;



        return result;
    }

    public HelpRequest(){
        state = HelpRequestState.Started;
        this.reviewDescription = "N/A";
        this.reviewStars = -1.1;
    }

    public HelpRequest(User helper, User disabled, String description, String imageUrl, String imageUrlHelper){
        this.helper = helper;
        this.disabled = disabled;
        this.description = description;

        //Tue Jan 17 18:37:55 GMT+00:00 2023
        requestTime = new Date().toString();

        //Modify String to: Jan 17 18:37:55
        StringBuilder sb = new StringBuilder();
        sb.append(requestTime, 8, 10);
        sb.append(" ");
        sb.append(requestTime, 4, 7);
        sb.append(" ");
        sb.append(requestTime, 11, 19);

        requestTime = sb.toString();

        this.imageUrl = imageUrl;
        this.imageUrlHelper = imageUrlHelper;

        state = HelpRequestState.Started;
        this.reviewDescription = "N/A";
        this.reviewStars = -1.1;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public void setState(HelpRequestState state){
        this.state = state;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewStars(double f) {
        this.reviewStars = f;
    }

    public double getReviewStars() {
        return this.reviewStars;
    }

    public void setImageUrl(String s) {
        this.imageUrl = s;
    }

    public void setImageUrlHelper(String s) {
        this.imageUrlHelper = s;
    }

    public String getImageUrlHelper() {
        return this.imageUrlHelper;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setHelper(User u) {
        this.helper = u;
    }

    public void setDisabled(User u) {
        this.disabled = u;
    }

    public void setDescription(String s) {
        this.description = s;
    }

    public void setDate(String d) {
        this.requestTime = d;
    }

    public User getHelper(){
        return this.helper;
    }

    public User getDisabled(){
        return this.disabled;
    }

    public String getDescription(){
        return this.description;
    }

    public String getRequestTime(){
        return this.requestTime;
    }

    public HelpRequestState getState(){
        return state;
    }

    public enum HelpRequestState{
        Started,
        Accepted,
        Finished,
    }

}
