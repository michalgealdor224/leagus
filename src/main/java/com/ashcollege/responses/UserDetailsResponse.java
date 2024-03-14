package com.ashcollege.responses;

import com.ashcollege.entities.User;

public class UserDetailsResponse extends BasicResponse {
    private User user;

    public User getUser() {
        return user;
    }

    public UserDetailsResponse(boolean success, String errorCode, User user) {
        super(success, errorCode);
        this.user = user;
    }

}
