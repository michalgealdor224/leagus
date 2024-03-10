package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.Client;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.Validator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;

    private List<SseEmitter> clients = new ArrayList<>();


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test() {
        return "Hello From Server";
    }


    @RequestMapping(value = "get-data", method = {RequestMethod.GET, RequestMethod.POST})
    public User getData () {
        return dbUtils.getUserById(4);
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String username, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        String errorCode = null;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                User user = dbUtils.login(username, password);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
                } else {
                    errorCode = ERROR_LOGIN_USER_NOT_FOUND;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        System.out.println(basicResponse.isSuccess());
        return basicResponse;
    }

    @RequestMapping(value = "add-user", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse addUser(String username, String password,String confirmPassword, String email) {
        BasicResponse response;
        User userToAdd = new User(username, password, email);
        if (Validator.validateUser(userToAdd, confirmPassword)) {
            boolean success = dbUtils.addUser(userToAdd);
            response = new BasicResponse(success, success ? null : ERROR_SIGN_UP_USERNAME_TAKEN);
        } else {
            response = new BasicResponse(false, Validator.getValidateError(userToAdd, confirmPassword));
        }
        return response;
    }


    @RequestMapping(value = "get-users")
    public List<User> getUsers() {
        return dbUtils.getAllUsers();
    }


    @RequestMapping(value = "test")
    public Client test(String firstName, String newFirstName) {
        Client client = persist.getClientByFirstName(firstName);
        client.setFirstName(newFirstName);
        client.setLastName(newFirstName);
        persist.save(client);
        return client;
    }

    @RequestMapping(value = "/start-streaming")
    public SseEmitter streaming() {
        SseEmitter emitter = new SseEmitter();
        clients.add(emitter);
        return emitter;
    }

}
