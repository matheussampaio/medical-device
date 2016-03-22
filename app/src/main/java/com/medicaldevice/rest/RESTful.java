package com.medicaldevice.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTful {

    private static final String URL_BASE = "http://medical-device-server.herokuapp.com/api/";
    private final ApiService mRESTful;

    private RESTful() {

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

         mRESTful = retrofit.create(ApiService.class);
    }
}
