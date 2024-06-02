package com.ashcollege.utils;

import com.ashcollege.entities.User;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

import static com.ashcollege.utils.Errors.*;

public class Validator {



    public static boolean validateUser(User user, String confirmPassword) {
        return validateUser(user) && isConfirmPassword(user.getPassword(), confirmPassword);
    }
    public static boolean validateUser(User user) {
        return validUsername(user.getUsername()) && validPassword(user.getPassword()) &&
                isValidEmailLib(user.getEmail());
    }

    public static boolean validChanges(String item, String value) {
        boolean isValid = false;
        if (Objects.equals(item, "password")) {
            if (!value.equals("")) {
                if (validPassword(value)) {
                    isValid = true;
                }
            }
            else {
                isValid = true;
            }
        }
        if (Objects.equals(item,"email")){
            if (!value.equals("")) {
                if (isValidEmailLib(value)) {
                    isValid = true;
                }
            }
            else {
                isValid = true;
            }

        }
        return isValid;
    }

    private static boolean isConfirmPassword (String password , String confirmPassword) {
        //   System.out.println(password + "      " + confirmPassword);
        System.out.println(Objects.equals(password, confirmPassword));
        return  (Objects.equals(password, confirmPassword));
    }

    public static boolean isValidEmailLib(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean validPassword(String password) {
        if (password.length() >= 6) {
            for (int i = 0; i < password.length(); i++) {
                if (Character.isUpperCase(password.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean validUsername(String username) {
        boolean valid = false;
        if (username != null && username.length() > 0) {
            valid = true;
        }
        return valid;
    }

    public static String getValidateError(User user, String confirmPassword) {
        if (!validUsername(user.getUsername())){
            return ERROR_SIGN_UP_NO_USERNAME;
        }
        if (!validPassword(user.getPassword())){
            return ERROR_SIGN_UP_NO_PASSWORD;
        }
        if (!isConfirmPassword(user.getPassword(), confirmPassword)) {
            return ERROR_SIGN_UP_CONFIRM_PASSWORD;
        }
        return ERROR_SIGN_UP_NO_EMAIL;
    }
}
