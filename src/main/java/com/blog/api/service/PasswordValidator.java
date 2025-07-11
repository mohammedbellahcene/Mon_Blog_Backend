package com.blog.api.service;

public class PasswordValidator {
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{12,}$";

    public static boolean isStrong(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }
} 