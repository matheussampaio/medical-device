package com.medicaldevice.rest;

import com.medicaldevice.model.OTUData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Cloud interface
 */
public interface ApiService {

    @Headers({
        "content-type: application/json"
    })
    @POST("otudata")
    Call<OTUData> postOTUData(@Body OTUData otudata);
}
