package com.kwendaapp.rideo.Models;

public class Chat {
    private final String type;
    private final String message;

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Chat(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
