package edu.ilstu.it275.medicaldevice;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;

import java.util.HashMap;
import java.util.Iterator;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    protected void USBDeviceAttached() {
        System.out.println("MainActivity.USBDeviceAttached");

        activate();
    }

    @Click(R.id.startButton)
    void startButtonClick() {
        System.out.println("MainActivity.startButtonClick");

        activate();
    }

    private void activate() {
        System.out.println("MainActivity.activate");

        System.out.println("Intent action " + getIntent().getAction());

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            System.out.println("Vendor id: " + device.getVendorId());
            System.out.println("Device protocol: " + device.getDeviceProtocol());
            System.out.println("Product id: " + device.getProductId());
            System.out.println("Class: " + device.getDeviceClass());
            System.out.println("Subclass: " + device.getDeviceSubclass());
        }
    }
}
