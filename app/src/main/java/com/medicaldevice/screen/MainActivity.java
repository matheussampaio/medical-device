package com.medicaldevice.screen;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.medicaldevice.R;
import com.medicaldevice.event.CloseEvent;
import com.medicaldevice.event.CommandStartEvent;
import com.medicaldevice.event.DataReceivedEvent;
import com.medicaldevice.event.InitEvent;
import com.medicaldevice.usb.OTUData;
import com.medicaldevice.usb.OneTouchUltra2;
import com.medicaldevice.utils.Logger;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.outputTxtView)
    TextView mOutputTextView;

    @ViewById(R.id.permissionBtn)
    Button mPermissionButton;

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

    private static final String ACTION_USB_PERMISSION = "com.medicaldevice.USB_PERMISSION";
    private final String TAG = "MEDICAL_DEVICE";

    @Bean
    OneTouchUltra2 mOneTouchUltra2;
    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mOneTouchUltra2.register();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissionButton();
    }

    @Override
    public void onStop() {
        mOneTouchUltra2.close();
        EventBus.getDefault().unregister(this);
        mOneTouchUltra2.unregister();
        super.onStop();
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    void USBDeviceAttached() {
        Logger.d(TAG, "MainActivity.USBDeviceAttached");

        requestPermission();
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_DETACHED")
    void USBDeviceDetached() {
        Logger.d(TAG, "MainActivity.USBDeviceDetached");

        mOneTouchUltra2.close();
    }

    @Receiver(actions = ACTION_USB_PERMISSION)
    void USBPermission() {
        Logger.d(TAG, "MainActivity.USBPermission");

        checkPermissionButton();
    }

    @Click(R.id.permissionBtn)
    void permissionButtonClick() {
        Logger.d(TAG, "MainActivity.permissionButtonClick");

        requestPermission();
    }

    @Click(R.id.initBtn)
    void initButtonClick() {
        Logger.d(TAG, "MainActivity.initButtonClick");

        mOneTouchUltra2.init(getDevice());
    }

    @Click(R.id.closeBtn)
    void closeButtonClick() {
        Logger.d(TAG, "MainActivity.closeButtonClick");

        mOneTouchUltra2.close();
    }

    @Click(R.id.dmsBtn)
    void dmsButtonClick() {
        Logger.d(TAG, "MainActivity.dmsButtonClick");

        mOneTouchUltra2.sendDMSCommand();
    }

    @Click(R.id.dmpBtn)
    void dmpButtonClick() {
        Logger.d(TAG, "MainActivity.dmpButtonClick");

        mOneTouchUltra2.sendDMPCommand();
    }

    @Click(R.id.dmfBtn)
    void dmfButtonClick() {
        Logger.d(TAG, "MainActivity.dmfButtonClick");

        mOneTouchUltra2.sendDMFCommand();
    }

    @Click(R.id.dmatBtn)
    void dmatButtonClick() {
        Logger.d(TAG, "MainActivity.dmatButtonClick");

        mOneTouchUltra2.sendDMATCommand();
    }

    @Click(R.id.dmquestionBtn)
    void dmquestionButtonClick() {
        Logger.d(TAG, "MainActivity.dmquestionButtonClick");
        mOutputTextView.setText("");
        //mOneTouchUltra2.sendDMQuestionCommand();
        appendOutputView("------- Stored Values ------ \n");
        appendOutputView("  Time        glucose \n");
        appendOutputView("----------------------------- \n");
        Iterator<OTUData> data = OTUData.findAll(OTUData.class);
        while(data.hasNext()) {
            OTUData d = (OTUData)data.next();
            appendOutputView(d.dateTime+"     "+d.glucose+"\n");
        }
    }

    @UiThread
    void showPemissionButton(boolean show) {
        if (show) {
            mPermissionButton.setVisibility(View.VISIBLE);
        } else {
            mPermissionButton.setVisibility(View.INVISIBLE);
        }
    }

    @UiThread
    void showInitButton(boolean show) {
        if (show) {
            mInitButton.setVisibility(View.VISIBLE);
        } else {
            mInitButton.setVisibility(View.INVISIBLE);
        }
    }

    @UiThread
    void showCommands() {
        Logger.d(TAG, "MainActivity.showCommands");
        mDmsButton.setVisibility(View.VISIBLE);
        mDmpButton.setVisibility(View.VISIBLE);
        mDmatButton.setVisibility(View.VISIBLE);
        mDmquestionButton.setVisibility(View.VISIBLE);
        mDmfButton.setVisibility(View.VISIBLE);
    }

    @UiThread
    void hideCommands() {
        Logger.d(TAG, "MainActivity.hideCommands");
        mDmsButton.setVisibility(View.INVISIBLE);
        mDmpButton.setVisibility(View.INVISIBLE);
        mDmatButton.setVisibility(View.INVISIBLE);
        mDmquestionButton.setVisibility(View.INVISIBLE);
        mDmfButton.setVisibility(View.INVISIBLE);
    }

    @UiThread
    void clearOutputView() {
        mOutputTextView.setText("");
    }

    @UiThread
    void appendOutputView(String text) {
        mOutputTextView.append(text);
    }


    @Subscribe
    public void onInitEvent(InitEvent event) {
        Logger.d(TAG, "MainActivity.onInitEvent");

        if (event.getResult()) {
            showCommands();

            mInitButton.setVisibility(View.INVISIBLE);
            mPermissionButton.setVisibility(View.INVISIBLE);
            mCloseButton.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onCloseEvent(CloseEvent event) {
        Logger.d(TAG, "MainActivity.onCloseEvent");

        if (event.getResult()) {
            mCloseButton.setVisibility(View.INVISIBLE);

            checkPermissionButton();
            hideCommands();

            mOutputTextView.setText("");
        }
    }



    @Subscribe
    public void onCommandStartEvent(CommandStartEvent event) {
        Logger.d(TAG, "MainActivity.onCommandStartEvent");

        clearOutputView();
        appendOutputView(event.getCommand() + "\n");
    }

    private void requestPermission() {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        UsbDevice device = getDevice();

        if (device != null) {
            mUsbManager.requestPermission(device, permissionIntent);
        }
    }

    private UsbDevice getDevice() {
        Logger.d(TAG, "MainActivity.getDevice");

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceIterator.hasNext()) {
            return deviceIterator.next();
        }

        Toast.makeText(this, "No device attached.", Toast.LENGTH_SHORT).show();

        return null;
    }

    private boolean hasDevicePermission() {
        UsbDevice device = getDevice();

        if (device != null) {
            return mUsbManager.hasPermission(device);
        }

        return false;
    }

    private void checkPermissionButton() {
        if (hasDevicePermission()) {
            showInitButton(true);
            showPemissionButton(false);
        } else {
            showInitButton(false);
            showPemissionButton(true);
        }
    }

    @Subscribe
    public void onDataReceivedEvent(DataReceivedEvent event) {
        Logger.d(TAG, "MainActivity.onDataReceivedEvent");
        appendOutputView("----------- RAW Data -------------- \n");
        appendOutputView(event.getData());
        appendOutputView("---------- Processed Data --------------- \n");
        // FIXME: this call is here for simplicity, but should be handled in OneTouchUltra2.
        parseData(event.getData());
    }
    // TODO: parse the data string to OTUData objects and check for new values.
    public void parseData(String data) {

        boolean header = true;
        int numRecords = 0;
        int newRecords = 0;
        int index = 0;
        String sDate = "";
        int recordsCount = new Long(OTUData.count(OTUData.class)).intValue();
        appendOutputView("Records in DB : "+recordsCount+"\n");
        List<OTUData> newOTUData = new ArrayList<OTUData>();
        numRecords = new Integer(data.substring(2, 5)).intValue();
            StringTokenizer completeString = new StringTokenizer(data, "P");
            while (completeString.hasMoreTokens()) {
                if(numRecords<=recordsCount)
                    break;
                while (numRecords > recordsCount) {
                    OTUData meterData = new OTUData();
                    String row = completeString.nextToken();
                    row = row.replace("\"", "").trim();
                    StringTokenizer rowString = new StringTokenizer(row, ",");
                    while (rowString.hasMoreTokens()) {
                        String v = rowString.nextToken().trim();
                        if (!v.isEmpty()) {
                            if (header) {
                                if (index == 0) {
                                    appendOutputView("Records in device : " + numRecords+"\n");
                                } else if (index == 1) {
                                    meterData.serial=v;
                                    appendOutputView("Serial = " + v+"\n");
                                } else if (index == 2) {
                                    meterData.unit=v;
                                    appendOutputView("Units = " + v+"\n");
                                }
                            } else {
                                if (index == 0)
                                    appendOutputView("Day of the Week = " + v+"\n");
                                else if (index == 1) {
                                    sDate = v;
                                    appendOutputView("Date = " + v+"\n");
                                } else if (index == 2) {
                                    appendOutputView("Time = " + v+"\n");
                                    meterData.dateTime = Utils.getDate(sDate, v);
                                } else if (index == 3) {
                                    meterData.glucose = (new Integer(v.replace("*", "").trim()).intValue());
                                    appendOutputView("Reading = " + v.replace("*", "").trim()+"\n");
                                } else if (index == 4) {
                                    appendOutputView("User Flag = " + v.trim()+"\n");
                                    meterData.userFlag= v;
                                } else if (index == 5) {
                                    appendOutputView("Food Code= " + v.trim()+"\n");
                                    meterData.mealComment = v;
                                }
                            }
                            index++;
                        }
                    }
                    appendOutputView("----------------------------------\n");
                    if(!header) {
                        meterData.save();
                        numRecords--;
                    }
                    header = false;
                    index = 0;

                }
            }
        appendOutputView("\n ----------------------------------\n");
        long count = OTUData.count(OTUData.class);
        appendOutputView("Total Count - "+count);
        }


}
