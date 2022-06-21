package com.kwendaapp.rideo.Models;

public class UserResponse {
    private final boolean error;
    private final String message;
    private final User user;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public UserResponse(boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }
}
