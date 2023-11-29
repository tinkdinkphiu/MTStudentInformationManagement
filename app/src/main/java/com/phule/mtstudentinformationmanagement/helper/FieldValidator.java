package com.phule.mtstudentinformationmanagement.helper;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldValidator {
    private Context context;

    public FieldValidator(Context context) {
        this.context = context;
    }
    public boolean isValidCode(String code) {
        return code.length() == 8;
    }
    public boolean isValidName(String name) {
        String regex = "^[a-zA-Z\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
    public boolean isValidIntegerField(String numberField) {
        try {
            int number = Integer.parseInt(numberField);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidTextField(String textField) {
        return textField.length() > 0;
    }
    public boolean isValidPhone(String phone) {
        return phone.matches("[0-9]+") && phone.length() == 10;
    }
    public boolean isValidEmail(String email) {
        if(email.length() <= 0) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean isValidPassword(String password) {
        return password.length() > 6;
    }
}
