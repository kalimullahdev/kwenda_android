package com.kwendaapp.rideo.Retrofit;


import com.kwendaapp.rideo.Helper.URLHelper;

import retrofit2.Retrofit;


public class RetrofitClient {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getLiveTrackingClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .build();
        }
        return retrofit;
    }
}
