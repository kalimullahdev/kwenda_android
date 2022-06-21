package com.kwendaapp.rideo.Models;

public class Errorresponse {
    private final boolean error;

    public boolean isError() {
        return error;
    }

    public Errorresponse(boolean error) {
        this.error = error;
    }
}
