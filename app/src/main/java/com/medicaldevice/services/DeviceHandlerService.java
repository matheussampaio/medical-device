package com.medicaldevice.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.app.NotificationCompat;

import com.medicaldevice.R;
import com.medicaldevice.event.CommandEndEvent;
import com.medicaldevice.event.InitEvent;
import com.medicaldevice.screen.MainActivity;
import com.medicaldevice.usb.OneTouchUltra2;
import com.medicaldevice.utils.Logger;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.support.app.AbstractIntentService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EIntentService
public class DeviceHandlerService extends AbstractIntentService {

    @Bean
    OneTouchUltra2 mOneTouchUltra2;

    @SystemService
    UsbManager mUsbManager;

    @SystemService
    NotificationManager mNotificationManager;

    public DeviceHandlerService() {
        super("DeviceHandlerService");
    }

    @ServiceAction
    void sync(UsbDevice device) {
        Logger.d("DeviceHandlerService::sync");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setContentTitle("Medical Device")
            .setStyle(new NotificationCompat.BigTextStyle().bigText("Synchronizing data..."))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("Synchronizing data...");

        mBuilder.setContentIntent(contentIntent);

        startForeground(42, mBuilder.build());

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

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

}
