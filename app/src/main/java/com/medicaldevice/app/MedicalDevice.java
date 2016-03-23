package com.medicaldevice.app;

import com.orm.SugarApp;
import com.squareup.leakcanary.LeakCanary;

import org.androidannotations.annotations.EApplication;

@EApplication
public class MedicalDevice extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        initMemoryLeakAnalyse();
    }

    private void initMemoryLeakAnalyse() {
        LeakCanary.install(this);
    }
}