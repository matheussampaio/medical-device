package com.medicaldevice.services;

import android.app.IntentService;
import android.content.Intent;

import com.medicaldevice.utils.Logger;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.Receiver;

@EIntentService
public class DeviceHandlerService extends IntentService {

    private static final java.lang.String TAG = "DeviceHandlerService";

    public DeviceHandlerService() {
        super("DeviceHandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do nothing here
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    void USBDeviceAttached() {
        Logger.d(TAG, "DeviceHandlerService.USBDeviceAttached");
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_DETACHED")
    void USBDeviceDetached() {
        Logger.d(TAG, "DeviceHandlerService.USBDeviceDetached");
    }
}
