package com.medicaldevice.app;

import com.orhanobut.logger.Logger;
import com.orm.SugarApp;

import org.androidannotations.annotations.EApplication;

@EApplication
public class MedicalDevice extends SugarApp {

    public static final String MEDICAL_DEVICE = "MEDICAL-DEVICE";

    @Override
    public void onCreate() {
        super.onCreate();

        initLogger();
    }

    private void initLogger() {
        Logger.init(MEDICAL_DEVICE);
    }
}