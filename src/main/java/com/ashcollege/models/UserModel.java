package com.ashcollege.models;

import com.ashcollege.entities.User;

public class UserModel {
    private String username;
    private String email;

    public UserModel(String username, String email) {
        this.username = username;
        this.email = email;
    }


    public static UserModel from(User user) {
        return new UserModel(user.getUsername(), user.getEmail());
    }
}
