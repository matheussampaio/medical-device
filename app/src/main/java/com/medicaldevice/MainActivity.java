package com.medicaldevice;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.UiThread;

import java.util.HashMap;
import java.util.Iterator;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.outputTxtView)
    TextView mOutputTextView;

    @ViewById(R.id.initBtn)
    Button mInitButton;

    @ViewById(R.id.closeBtn)
    Button mCloseButton;

    @ViewById(R.id.dmsBtn)
    Button mDmsButton;

    @ViewById(R.id.dmatBtn)
    Button mDmatButton;

    @ViewById(R.id.dmfBtn)
    Button mDmfButton;

    @ViewById(R.id.dmquestionBtn)
    Button mDmquestionButton;

    @ViewById(R.id.dmpBtn)
    Button mDmpButton;

    private UsbManager mUsbManager;
    private UsbInterface mUsbInterface;
    private UsbDeviceConnection mConnection;
    private byte[] COMMAND_DMS;
    private UsbEndpoint mEndpointIn;
    private UsbEndpoint mEndpointOut;

    private final String TAG = "MEDICAL_DEVICE";
    private UsbEndpoint mEndpoint0;
    private UsbEndpoint mEndpoint1;
    private UsbEndpoint mEndpoint2;
    private UsbDevice mDevice;

    // USB control commands
    private static final int SET_LINE_REQUEST_TYPE = 0x21;
    private static final int SET_LINE_REQUEST = 0x20;
    private static final int BREAK_REQUEST_TYPE = 0x21;
    private static final int BREAK_REQUEST = 0x23;
    private static final int BREAK_OFF = 0x0000;
    private static final int GET_LINE_REQUEST_TYPE = 0xa1;
    private static final int GET_LINE_REQUEST = 0x21;
    private static final int VENDOR_WRITE_REQUEST_TYPE = 0x40;
    private static final int VENDOR_WRITE_REQUEST = 0x01;
    private static final int VENDOR_READ_REQUEST_TYPE = 0xc0;
    private static final int VENDOR_READ_REQUEST = 0x01;
    private static final int SET_CONTROL_REQUEST_TYPE = 0x21;
    private static final int SET_CONTROL_REQUEST = 0x22;

    // RS232 Line constants
    private static final int CONTROL_DTR = 0x01;
    private static final int CONTROL_RTS = 0x02;
    private static final int UART_DCD = 0x01;
    private static final int UART_DSR = 0x02;
    private static final int UART_RING = 0x08;
    private static final int UART_CTS = 0x80;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    void USBDeviceAttached() {
        Log.d(TAG, "MainActivity.USBDeviceAttached");
    }

    @AfterViews
    void updateButtons() {
        mOutputTextView.setText("");
    }

    @Click(R.id.initBtn)
    void initButtonClick() {
        Log.d(TAG, "MainActivity.initButtonClick");

        if (init()) {
            showCommands();

            mInitButton.setVisibility(View.INVISIBLE);
            mCloseButton.setVisibility(View.VISIBLE);

            read();
        }
    }

    @Click(R.id.closeBtn)
    void closeButtonClick() {
        Log.d(TAG, "MainActivity.closeButtonClick");

        if (mConnection != null ) {
            mConnection.releaseInterface(mUsbInterface);
            mConnection.close();

            mConnection = null;
        }

        mInitButton.setVisibility(View.VISIBLE);
        mCloseButton.setVisibility(View.INVISIBLE);

        hideCommands();
    }

    @UiThread
    void showCommands() {
        Log.d(TAG, "MainActivity.showCommands");
        mDmsButton.setVisibility(View.VISIBLE);
        mDmpButton.setVisibility(View.VISIBLE);
        mDmatButton.setVisibility(View.VISIBLE);
        mDmquestionButton.setVisibility(View.VISIBLE);
        mDmfButton.setVisibility(View.VISIBLE);
    }

    @UiThread
    void hideCommands() {
        Log.d(TAG, "MainActivity.hideCommands");
        mDmsButton.setVisibility(View.INVISIBLE);
    }

    @Click(R.id.dmsBtn)
    void dmsButtonClick() {
        Log.d(TAG, "MainActivity.dmsButtonClick");

        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x53", "0x0D", "0x0D"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        sendBytes(commandByte);
    }

    @Click(R.id.dmpBtn)
    void dmpButtonClick() {
        Log.d(TAG, "MainActivity.dmpButtonClick");

        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x50"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        sendBytes(commandByte);
    }

    @Click(R.id.dmfBtn)
    void dmfButtonClick() {
        Log.d(TAG, "MainActivity.dmfButtonClick");

        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x46"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        sendBytes(commandByte);
    }

    @Click(R.id.dmatBtn)
    void dmatButtonClick() {
        Log.d(TAG, "MainActivity.dmatButtonClick");

        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x40"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        sendBytes(commandByte);
    }

    @Click(R.id.dmquestionBtn)
    void dmquestionButtonClick() {
        Log.d(TAG, "MainActivity.dmquestionButtonClick");

        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x3F"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        sendBytes(commandByte);
    }

    @UiThread
    void addOutputText(String text) {
        Log.d(TAG, "MainActivity.addOutputText :: text = [" + text + "]");

        mOutputTextView.append(text + " ");
    }

    private UsbDevice getDevice() {
        Log.d(TAG, "MainActivity.getDevice");

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceIterator.hasNext()) {
            return deviceIterator.next();
        }

        return null;
    }

    private boolean init() {
        Log.d(TAG, "MainActivity.init");

        mDevice = getDevice();

        if (mDevice == null) {
            Log.d(TAG, "Getting device failed");
            return false;
        }

        Log.d(TAG, "Device Name: " + mDevice.getDeviceName());
        Log.d(TAG, "VendorID: " + mDevice.getVendorId());
        Log.d(TAG, "ProductID: " + mDevice.getProductId());

        mUsbInterface = mDevice.getInterface(0);

        if (mUsbInterface == null) {
            Log.e(TAG, "Getting interface failed.");
            return false;
        }

        // endpoint addr 0x81 = input interrupt
        mEndpoint0 = mUsbInterface.getEndpoint(0);
        if ((mEndpoint0.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) || (mEndpoint0.getDirection() != UsbConstants.USB_DIR_IN)) {
            Log.e(TAG, "Getting endpoint 0 (control) failed!");
            return false;
        }

        // endpoint addr 0x2 = output bulk
        mEndpoint1 = mUsbInterface.getEndpoint(1);
        if ((mEndpoint1.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) || (mEndpoint1.getDirection() != UsbConstants.USB_DIR_OUT)) {
            Log.e(TAG, "Getting endpoint 1 (output) failed!");
            return false;
        }

        // endpoint addr 0x83 = input bulk
        mEndpoint2 = mUsbInterface.getEndpoint(2);
        if ((mEndpoint2.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) || (mEndpoint2.getDirection() != UsbConstants.USB_DIR_IN)) {
            Log.e(TAG, "Getting endpoint 2 (input) failed!");
            return false;
        }

        mConnection = mUsbManager.openDevice(mDevice);
        if (mConnection == null) {
            Log.e(TAG, "Getting DeviceConnection failed!");
            return false;
        }

        if (!mConnection.claimInterface(mUsbInterface, true)) {
            Log.e(TAG, "Exclusive interface access failed!");
            return false;
        }

        // Initialization of PL2303 according to linux pl2303.c driver
        byte[] buffer = new byte[1];
        sendControlTransfer(1, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, 0x8484, 0, buffer, 1, 100);
        sendControlTransfer(2, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 0x0404, 0, null, 0, 100);
        sendControlTransfer(3, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, 0x8484, 0, buffer, 1, 100);
        sendControlTransfer(4, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, 0x8383, 0, buffer, 1, 100);
        sendControlTransfer(5, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, 0x8484, 0, buffer, 1, 100);
        sendControlTransfer(6, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 0x0404, 1, null, 0, 100);
        sendControlTransfer(7, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, 0x8484, 0, buffer, 1, 100);
        sendControlTransfer(8, VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, 0x8383, 0, buffer, 1, 100);
        sendControlTransfer(9, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 0, 1, null, 0, 100);
        sendControlTransfer(10, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 1, 0, null, 0, 100);
        sendControlTransfer(11, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 2, 0x44, null, 0, 100);

        // setup PL2303
        byte[] settings = new byte[7];
        // get configuration
        sendControlTransfer(12, GET_LINE_REQUEST_TYPE, GET_LINE_REQUEST, 0, 0, settings, settings.length, 100);

        // baud rate = 9600
        int baud = 9600;
        settings[0]=(byte) (baud & 0xff);
        settings[1]=(byte) ((baud >> 8) & 0xff);
        settings[2]=(byte) ((baud >> 16) & 0xff);
        settings[3]=(byte) ((baud >> 24) & 0xff);

        // stop bit 1
        settings[4] = 0;

        // parity none
        settings[5] = 0;

        // data bits 8
        settings[6] = 8;

        // set configuration
        sendControlTransfer(14, SET_LINE_REQUEST_TYPE, SET_LINE_REQUEST, 0, 0, settings, settings.length, 100);

        sendControlTransfer(15, GET_LINE_REQUEST_TYPE, GET_LINE_REQUEST, 0, 0, settings, settings.length, 100);
        // Disable BreakControl
        sendControlTransfer(16, BREAK_REQUEST_TYPE, BREAK_REQUEST, BREAK_OFF, 0, null, 0, 100);
        // set flow control off
        sendControlTransfer(17, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 0x0, 0x0, null, 0, 100);

        return true;
    }

    @Background
    void read() {
        Log.d(TAG, "MainActivity.read");

        byte buffer[] = new byte[mEndpoint2.getMaxPacketSize()];
        byte[] tmp = new byte[1];

        while (mConnection != null) {
            int size = sendBulkTransfer(2, mEndpoint2, buffer, buffer.length, 0);

            for (int k = 0; k < size; k++) {
                tmp[0] = buffer[k];
                addOutputText(Utils.bytesToHexString(tmp));
            }
        }
    }

    @Background
    void sendBytes(byte[] bytes) {
        Log.d(TAG, "MainActivity.sendBytes");
        sendBulkTransfer(1, mEndpoint1, bytes, bytes != null ? bytes.length : 0, 100);
    }

    private int sendBulkTransfer(int id, UsbEndpoint endpoint, byte[] buffer, int length, int timeout) {
        Log.d(TAG, "MainActivity.sendBulkTransfer");

        int result = mConnection.bulkTransfer(endpoint, buffer, length, timeout);

        if (result < 0) {
            Log.e(TAG, String.format("bulkTransfer %d failed!", id));
        } else {
            Log.i(TAG, String.format("bulkTransfer %d :: result = %d", id, result));
        }

        if (buffer != null) {
            Log.d(TAG, "Utils.bytesToBinaryString(buffer) = " + Utils.bytesToBinaryString(buffer));
        }

        return result;
    }

    private void sendControlTransfer(int id, int vendorReadRequestType, int vendorReadRequest, int value, int index, byte[] buffer, int length, int timeout) {
        Log.d(TAG, "MainActivity.sendControlTransfer");

        int result = mConnection.controlTransfer(vendorReadRequestType, vendorReadRequest, value, index, buffer, length, timeout);

        if (result < 0) {
            Log.e(TAG, String.format("controlTransfer %d failed!", id));
        } else {
            Log.i(TAG, String.format("controlTransfer %d :: result = %d", id, result));
        }

        if (buffer != null) {
            Log.d(TAG, "Utils.bytesToBinaryString(buffer) = " + Utils.bytesToBinaryString(buffer));
        }
    }
}
