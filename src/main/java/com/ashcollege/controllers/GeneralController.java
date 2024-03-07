package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.Client;
import com.ashcollege.entities.Product;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.responses.ProductsResponse;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
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


    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String username, String password, String email) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                User user = dbUtils.login(username, password, email);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
                } else {
                    errorCode = ERROR_LOGIN_WRONG_CREDS;
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
        return basicResponse;
    }

    @RequestMapping(value = "add-user", method = {RequestMethod.GET, RequestMethod.POST})
    public boolean addUser(String username, String password, String email) {
        boolean valid = false;
        User userToAdd = null;
        if (validUsername(username) && validPassword(password)&& isValidEmail(email)) {
            valid = true;
        }
            if (valid) {
                userToAdd = new User(username, password, email);
            }
            return dbUtils.addUser(userToAdd);
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');

        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }




    public boolean validPassword(String password) {
        if (password.length() >= 6) {
            for (int i = 0; i < password.length(); i++) {
                if (Character.isUpperCase(password.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }



    public boolean validUsername(String username) {
        boolean valid = false;
        if (username != null && username.length() > 0) {
            valid = true;
        }
        return valid;
    }









    @RequestMapping (value = "get-users")
    public List<User> getUsers () {
        return dbUtils.getAllUsers();
    }





    @RequestMapping(value = "test")
    public Client test (String firstName, String newFirstName) {
        Client client = persist.getClientByFirstName(firstName);
        client.setFirstName(newFirstName);
        client.setLastName(newFirstName);
        persist.save(client);
        return client;
    }

    @RequestMapping(value = "/start-streaming")
    public SseEmitter streaming () {
        SseEmitter emitter = new SseEmitter();
        clients.add(emitter);
        return emitter;
    }

}
