package com.kwendaapp.rideo.Models;

import java.util.List;

public class ConstDataResponse {
    private final List<ConstData> data;

    public List<ConstData> getData() {
        return data;
    }

    public ConstDataResponse(List<ConstData> data) {
        this.data = data;
    }
}
