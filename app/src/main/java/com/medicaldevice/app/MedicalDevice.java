package com.medicaldevice.app;

import com.orm.SugarApp;

import net.danlew.android.joda.JodaTimeAndroid;

import org.androidannotations.annotations.EApplication;

@EApplication
public class MedicalDevice extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
    }
}