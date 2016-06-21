package com.medicaldevice.screen;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;

import com.medicaldevice.R;
import com.medicaldevice.services.DeviceHandlerService_;
import com.medicaldevice.utils.Logger;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Main Activity class
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "com.medicaldevice.USB_PERMISSION";

    @SystemService
    UsbManager mUsbManager;

    private UsbDevice mDevice;

    /**
     * USB device attached event handler
     */
    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    void USBDeviceAttached() {
        Logger.d("MainActivity::USBDeviceAttached");

        onSyncButtonClick();
    }

    /**
     * On sync button click event handler
     */
    @Click(R.id.syncBtn)
    void onSyncButtonClick() {
        Logger.d("MainActivity::onSyncButtonClick");

        mDevice = getFirstDevice();

        if (mDevice != null) {
            if (hasPermission()) {
                sync();
            } else {
                requestPermission();
            }
        } else {
            Logger.e("device not attached");
        }
    }

    /**
     * Synchronize data
     */
    private void sync() {
        Logger.d("MainActivity::sync");

        DeviceHandlerService_
            .intent(getApplication())
            .sync(mDevice)
            .start();
    }

    /**
     * USB device detached event handler
     */
    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_DETACHED")
    void USBDeviceDetached() {
        Logger.d("MainActivity::USBDeviceDetached");
    }

    /**
     * USB permission request event handler
     */
    @Receiver(actions = ACTION_USB_PERMISSION)
    void USBPermission() {
        Logger.d("MainActivity::USBPermission");

        if (hasPermission()) {
            sync();

            // permission denied, stop
        } else {
            Logger.e("permission denied.");
        }
    }

    /**
     * Check if has permission to access device
     *
     * @return true if has false if not
     */
    private boolean hasPermission() {
        Logger.d("MainActivity::hasPermission");
        return mDevice != null && mUsbManager.hasPermission(mDevice);
    }

    /**
     * Request user permission to access device
     */
    private void requestPermission() {
        Logger.d("MainActivity::requestPermission");

        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        mUsbManager.requestPermission(mDevice, permissionIntent);
    }

    /**
     * Get first device
     *
     * @return first device
     */
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
