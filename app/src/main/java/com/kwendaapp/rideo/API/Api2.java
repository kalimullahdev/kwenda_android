package com.kwendaapp.rideo.API;

import com.kwendaapp.rideo.Models.ChatResponse;
import com.kwendaapp.rideo.Models.CityResponse;
import com.kwendaapp.rideo.Models.ConstDataResponse;
import com.kwendaapp.rideo.Models.Errorresponse;
import com.kwendaapp.rideo.Models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api2 {
    @FormUrlEncoded
    @POST("staffloginbyphone")
    Call<UserResponse> userlogin(
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("deletefav")
    Call<Errorresponse> deletefav(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("paymentdone")
    Call<Errorresponse> paymentdone(
            @Field("id") String id
    );

    @GET("getconstdata")
    Call<ConstDataResponse> getconstdata();

    @GET("getcity")
    Call<CityResponse> getcity();

    @FormUrlEncoded
    @POST("addchat")
    Call<Errorresponse> addchat(
            @Field("booking_id") String booking_id,
            @Field("uid") String uid,
            @Field("pid") String pid,
            @Field("message") String message,
            @Field("type") String type
    );

    @GET("getchat/{booking_id}")
    Call<ChatResponse> getchat(
            @Path("booking_id") String booking_id
    );

}
