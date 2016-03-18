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

    private byte[] bytes;
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;
    private UsbManager mUsbManager;
    private String COMMAND_DMP = "DMP";

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
    void startButtonClick(Button button) {
        System.out.println("MainActivity.startButtonClick");

        activate();
    }

    private void activate() {
        System.out.println("MainActivity.activate");

        System.out.println("Intent action " + getIntent().getAction());

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        bytes = COMMAND_DMP.getBytes();

        while(deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            UsbInterface intf = device.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            connection.claimInterface(intf, forceClaim);
            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
        }
    }
}
