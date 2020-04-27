package com.example.dynatracetestapp;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("currently")
    private Currently currently;

    public Currently getCurrentlyResponse() {
        return currently;
    }

    @Override
    public String toString() {
        return currently.toString();
    }
}