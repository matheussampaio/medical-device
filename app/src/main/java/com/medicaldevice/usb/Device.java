package com.medicaldevice.usb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.util.Log;

import com.medicaldevice.event.ByteReceivedEvent;
import com.medicaldevice.event.CloseEvent;
import com.medicaldevice.event.InitEvent;
import com.medicaldevice.utils.Logger;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;


@EBean
public class Device {
    /**
     * Used this code as reference:
     * https://github.com/wburgers/Z-Droid/blob/master/src/de/hallenbeck/indiserver/communication_drivers/PL2303driver.java
     */

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

    private final String TAG = "Device";
    protected final Context mContext;

    private UsbDevice mDevice;
    private UsbInterface mUsbInterface;
    private UsbEndpoint mEndpoint0;
    private UsbEndpoint mEndpoint1;
    private UsbEndpoint mEndpoint2;
    private UsbDeviceConnection mConnection;
    private UsbManager mUsbManager;


    public Device(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }
    public boolean init(UsbDevice device) {
        Logger.d(TAG, "Device.init");

        mDevice = device;

        if (mDevice == null) {
            Log.d(TAG, "Getting device failed.");
            EventBus.getDefault().post(new InitEvent(false, "Getting device failed."));
            return false;
        } else if (!mUsbManager.hasPermission(mDevice)) {
            Log.d(TAG, "Don't have permission.");
            EventBus.getDefault().post(new InitEvent(false, "Don' have permission."));
            return false;
        }


        Logger.d(TAG, "Device Name: " + mDevice.getDeviceName());
        Logger.d(TAG, "VendorID: " + mDevice.getVendorId());
        Logger.d(TAG, "ProductID: " + mDevice.getProductId());

        mUsbInterface = mDevice.getInterface(0);

        if (mUsbInterface == null) {
            Logger.e(TAG, "Getting interface failed.");
            EventBus.getDefault().post(new InitEvent(false, "Getting interface failed."));
            return false;
        }

        // endpoint addr 0x81 = input interrupt
        mEndpoint0 = mUsbInterface.getEndpoint(0);
        if ((mEndpoint0.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) || (mEndpoint0.getDirection() != UsbConstants.USB_DIR_IN)) {
            Logger.e(TAG, "Getting endpoint 0 (control) failed!");
            EventBus.getDefault().post(new InitEvent(false, "Getting endpoint 0 (control) failed!"));
            return false;
        }

        // endpoint addr 0x2 = output bulk
        mEndpoint1 = mUsbInterface.getEndpoint(1);
        if ((mEndpoint1.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) || (mEndpoint1.getDirection() != UsbConstants.USB_DIR_OUT)) {
            Logger.e(TAG, "Getting endpoint 1 (output) failed!");
            EventBus.getDefault().post(new InitEvent(false, "Getting endpoint 1 (output) failed!"));
            return false;
        }

        // endpoint addr 0x83 = input bulk
        mEndpoint2 = mUsbInterface.getEndpoint(2);
        if ((mEndpoint2.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) || (mEndpoint2.getDirection() != UsbConstants.USB_DIR_IN)) {
            Logger.e(TAG, "Getting endpoint 2 (input) failed!");
            EventBus.getDefault().post(new InitEvent(false, "Getting endpoint 2 (input) failed!"));
            return false;
        }

        mConnection = mUsbManager.openDevice(mDevice);
        if (mConnection == null) {
            Logger.e(TAG, "Getting DeviceConnection failed!");
            EventBus.getDefault().post(new InitEvent(false, "Getting DeviceConnection failed!"));
            return false;
        }

        if (!mConnection.claimInterface(mUsbInterface, true)) {
            Logger.e(TAG, "Exclusive interface access failed!");
            EventBus.getDefault().post(new InitEvent(false, "Exclusive interface access failed!"));
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
        settings[0] = (byte) (baud & 0xff);
        settings[1] = (byte) ((baud >> 8) & 0xff);
        settings[2] = (byte) ((baud >> 16) & 0xff);
        settings[3] = (byte) ((baud >> 24) & 0xff);

        // stop bit 1
        settings[4] = 0;

        // parity none
        settings[5] = 0;

        // data bits 8
        settings[6] = 8;

        // set configuration
        sendControlTransfer(14, SET_LINE_REQUEST_TYPE, SET_LINE_REQUEST, 0, 0, settings, settings.length, 100);

        // disable breakcontrol
        sendControlTransfer(15, BREAK_REQUEST_TYPE, BREAK_REQUEST, BREAK_OFF, 0, null, 0, 100);

        // set flow control off
        sendControlTransfer(16, VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, 0x0, 0x0, null, 0, 100);

        read();

        EventBus.getDefault().post(new InitEvent(true));

        return true;
    }

    public void close() {
        Logger.d(TAG, "Device.close");

        if (mConnection != null) {
            mConnection.releaseInterface(mUsbInterface);
            mConnection.close();

            mConnection = null;
        }

        EventBus.getDefault().post(new CloseEvent(true));
    }



    private int sendBulkTransfer(int id, UsbEndpoint endpoint, byte[] buffer, int length, int timeout) {
//        Logger.d(TAG, "Device.sendBulkTransfer");

        int result = mConnection.bulkTransfer(endpoint, buffer, length, timeout);

//        if (result < 0) {
//            Logger.e(TAG, String.format("bulkTransfer %d failed!", id));
//        } else {
//            Logger.i(TAG, String.format("bulkTransfer %d :: result = %d", id, result));
//        }

//        if (buffer != null) {
//            Logger.d(TAG, "Utils.bytesToBinaryString(buffer) = " + Utils.bytesToBinaryString(buffer));
//        }

        return result;
    }

    private void sendControlTransfer(int id, int vendorReadRequestType, int vendorReadRequest, int value, int index, byte[] buffer, int length, int timeout) {
        Log.d(TAG, "Device.sendControlTransfer");

        int result = mConnection.controlTransfer(vendorReadRequestType, vendorReadRequest, value, index, buffer, length, timeout);

        if (result < 0) {
            Logger.e(TAG, String.format("controlTransfer %d failed!", id));
        } else {
            Logger.i(TAG, String.format("controlTransfer %d :: result = %d", id, result));
        }

        if (buffer != null) {
            Logger.d(TAG, "Utils.bytesToBinaryString(buffer) = " + Utils.bytesToBinaryString(buffer));
        }
    }

    @Background
    public void read() {
        Logger.d(TAG, "Device.read");

        byte buffer[] = new byte[mEndpoint2.getMaxPacketSize()];

        while (mConnection != null) {
            int size = sendBulkTransfer(2, mEndpoint2, buffer, buffer.length, 0);

            for (int k = 0; k < size; k++) {
                EventBus.getDefault().post(new ByteReceivedEvent(buffer[k]));
            }
        }
    }

    @Background
    void sendBytes(byte[] bytes) {
        Logger.d(TAG, "Device.sendBytes");
        sendBulkTransfer(1, mEndpoint1, bytes, bytes != null ? bytes.length : 0, 100);
    }
}
