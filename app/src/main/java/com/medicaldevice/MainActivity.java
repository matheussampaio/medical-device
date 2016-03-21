package com.medicaldevice;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private UsbManager mUsbManager;
    private UsbInterface mUsbInterface;
    private UsbDeviceConnection mConnection;
    private byte[] COMMAND_DMP = "DMP".getBytes();
    private UsbEndpoint mEndpoint;

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

        if (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            System.out.println("Vendor id: " + device.getVendorId());
            System.out.println("Device protocol: " + device.getDeviceProtocol());
            System.out.println("Product id: " + device.getProductId());
            System.out.println("Class: " + device.getDeviceClass());
            System.out.println("Subclass: " + device.getDeviceSubclass());

            mUsbInterface = device.getInterface(0);
            mEndpoint = mUsbInterface.getEndpoint(0);

            mConnection = mUsbManager.openDevice(device);
            mConnection.claimInterface(mUsbInterface, true);
            mConnection.bulkTransfer(mEndpoint, COMMAND_DMP, COMMAND_DMP.length, 10);

            UsbRequest mUsbRequest = new UsbRequest();
            mUsbRequest.initialize(mConnection, mEndpoint);
            ByteBuffer command = ByteBuffer.wrap(COMMAND_DMP);
            mUsbRequest.queue(command, command.capacity());

            UsbRequest usbRequestWait = mConnection.requestWait();

            System.out.println("client data: " + usbRequestWait.getClientData());

            usbRequestWait.close();
        }
    }
}
