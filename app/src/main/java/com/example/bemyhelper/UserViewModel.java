package com.example.bemyhelper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<User> currentUser = new MutableLiveData<User>();
    public void selectItem(User user) {
        currentUser.setValue(user);
    }
    public LiveData<User> getSelectedItem() {
        return currentUser;
    }

}
