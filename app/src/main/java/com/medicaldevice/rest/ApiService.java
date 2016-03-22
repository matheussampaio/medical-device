package com.medicaldevice.rest;

import com.medicaldevice.usb.OTUData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("otudata")
    Call<List<OTUData>> listOTUData();
}
