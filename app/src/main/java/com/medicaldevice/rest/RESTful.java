package com.medicaldevice.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RESTFUL class to communicate with cloud service
 */
public class RESTful {

    private static final String URL_BASE = "http://medical-device-server.herokuapp.com/api/";
    private static ApiService instance = null;

    /**
     * Constructor of RESTful
     */
    private RESTful() {}

    /**
     * Singleton implementation
     *
     * @return RESTful instance
     */
    public static ApiService getInstance() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiService.class);
        }

        return instance;
    }
}
