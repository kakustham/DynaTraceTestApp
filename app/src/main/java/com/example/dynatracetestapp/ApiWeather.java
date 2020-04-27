package com.example.dynatracetestapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiWeather {

    @GET("{latitude},{longitude}")
    Call<Weather> getWeather(@Path("latitude") String latitude,
                             @Path("longitude") String longitude);
}
