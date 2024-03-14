package com.ashcollege.entities;


import com.github.javafaker.Faker;

import java.util.Objects;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String secret;


    private String createSecret() {
        Faker faker = new Faker();
        String secret = faker.regexify("[a-zA-Z0-9]{10}"); 
        return secret;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.secret = createSecret();
    }

    public User() {

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSameUsername (String username) {
        return this.username.equals(username);
    }

    public boolean isSameCreds (String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void updateDetails(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
