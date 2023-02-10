package com.example.bemyhelper;

import java.util.List;

public class Helper extends User {

    public Helper(String name, String email,String phoneNumber, String password, int age, Boolean areDisabilitiesPublic, List<Disabilities> disabilities, double latitude, double longitude, String imageUrl) {
        super(name, email, phoneNumber, password, age, areDisabilitiesPublic, disabilities, latitude, longitude, imageUrl);
        super.SetHelper();
    }
}