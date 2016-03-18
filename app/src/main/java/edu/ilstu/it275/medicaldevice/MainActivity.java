package edu.ilstu.it275.medicaldevice;

import android.content.Context;
import android.hardware.usb.UsbDevice;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("MainActivity.onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("MainActivity.onResume");

        activate();
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    protected void USBDeviceAttached() {
        System.out.println("MainActivity.USBDeviceAttached");
    }

    @Click(R.id.startButton)
    void startButtonClick(Button button) {
        System.out.println("MainActivity.startButtonClick");

        activate();
    }


    private void activate() {
        System.out.println("MainActivity.activate");

        System.out.println("Intent action " + getIntent().getAction());

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();

            System.out.println(device.getDeviceName());
        }
    }


}
