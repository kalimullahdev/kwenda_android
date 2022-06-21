package com.kwendaapp.rideo.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class City implements Parcelable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public String getClng() {
        return clng;
    }

    public void setClng(String clng) {
        this.clng = clng;
    }

    public int id;
    public String cname;
    public String clat;
    public String clng;


    public City(Parcel in) {
        id = in.readInt();
        cname = in.readString();
        clat = in.readString();
        clng = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public City(JSONObject obj) {
        try {
            setId(obj.getInt("id"));
            setCname(obj.getString("city_name"));
            setClat(obj.getString("latitude"));
            setClng(obj.getString("longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(cname);
        dest.writeString(clat);
        dest.writeString(clng);
    }

    @Override
    public String toString() {
//        return "Driver{" +
//                "id='" + id + '\'' +
//                "cname='" + cname + '\'' +
//                ", clat='" + clat + '\'' +
//                ", clng='" + clng + '\'' +
//                '}';

        return "{\"id\":" + id + ",\"city_name\":" + "\"" + cname + "\"" + ",\"latitude\":" + clat + ",\"longitude\":" + clng + "}";
    }
}
