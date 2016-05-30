package com.medicaldevice.services;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.medicaldevice.event.CommandEndEvent;
import com.medicaldevice.event.InitEvent;
import com.medicaldevice.usb.OneTouchUltra2;
import com.medicaldevice.utils.Logger;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Iterator;

@EService
public class DeviceHandlerService extends AbstractIntentService {

    @Bean
    OneTouchUltra2 mOneTouchUltra2;

    @SystemService
    UsbManager mUsbManager;

    public DeviceHandlerService() {
        super("DeviceHandlerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.d("DeviceHandlerService::onHandleIntent");

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        UsbDevice device = getFirstDevice();

        mOneTouchUltra2.init(device);
    }

    @Subscribe
    void onInitEvent(InitEvent event) {
        Logger.d("DeviceHandlerService::onInitEvent: " + event.getResult());

        if (event.getResult() && mOneTouchUltra2.isInitialized()) {
            mOneTouchUltra2.sendDMPCommand();
        } else {
            Logger.e(event.getMessage());
            stop();
        }
    }

    @Subscribe
    void onCommandEndEvent(CommandEndEvent event) {
        Logger.d("DeviceHandlerService::onCommandEndEvent: " + event.getCommand());
        stop();
    }

    private void stop() {
        Logger.d("DeviceHandlerService::stop");

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (mOneTouchUltra2.isInitialized()) {
            mOneTouchUltra2.close();
        }

        stopSelf();
    }

    private UsbDevice getFirstDevice() {
        Logger.d("MainActivity::getDevice");

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        UsbDevice device = null;

        if (deviceIterator.hasNext()) {
            device = deviceIterator.next();
        }

        return device;
    }



}
