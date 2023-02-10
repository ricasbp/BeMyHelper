package com.example.bemyhelper;

import java.util.List;
import java.util.Set;

public class DisabledUser extends User{

    private Boolean areDisabilitiesPublic;

    public DisabledUser(String name, String email, String phoneNumber, String password, int age, Boolean areDisabilitiesPublic, List<Disabilities> disabilites, double latitude, double longitude, String imageUrl) {
        super(name, email, phoneNumber, password, age, areDisabilitiesPublic, disabilites, latitude, longitude, imageUrl);
        this.areDisabilitiesPublic = areDisabilitiesPublic;
    }
}
