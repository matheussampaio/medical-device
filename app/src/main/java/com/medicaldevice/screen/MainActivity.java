package com.medicaldevice.screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.medicaldevice.R;
import com.medicaldevice.event.ByteReceivedEvent;
import com.medicaldevice.event.CloseEvent;
import com.medicaldevice.event.CommandStartEvent;
import com.medicaldevice.event.InitEvent;
import com.medicaldevice.usb.OneTouchUltra2;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    private final String TAG = "MEDICAL_DEVICE";

    @Bean
    OneTouchUltra2 mOneTouchUltra2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_ATTACHED")
    void USBDeviceAttached() {
        Log.d(TAG, "MainActivity.USBDeviceAttached");

        mOneTouchUltra2.init();
    }

    @Receiver(actions = "android.hardware.usb.action.USB_DEVICE_DETACHED")
    void USBDeviceDetached() {
        Log.d(TAG, "MainActivity.USBDeviceDetached");

        mOneTouchUltra2.close();
    }

    @Click(R.id.initBtn)
    void initButtonClick() {
        Log.d(TAG, "MainActivity.initButtonClick");

        mOneTouchUltra2.init();
    }

    @Click(R.id.closeBtn)
    void closeButtonClick() {
        Log.d(TAG, "MainActivity.closeButtonClick");

        mOneTouchUltra2.close();
    }

    @Click(R.id.dmsBtn)
    void dmsButtonClick() {
        Log.d(TAG, "MainActivity.dmsButtonClick");

        mOneTouchUltra2.sendDMSCommand();
    }

    @Click(R.id.dmpBtn)
    void dmpButtonClick() {
        Log.d(TAG, "MainActivity.dmpButtonClick");

        mOneTouchUltra2.sendDMPCommand();
    }

    @Click(R.id.dmfBtn)
    void dmfButtonClick() {
        Log.d(TAG, "MainActivity.dmfButtonClick");

        mOneTouchUltra2.sendDMFCommand();
    }

    @Click(R.id.dmatBtn)
    void dmatButtonClick() {
        Log.d(TAG, "MainActivity.dmatButtonClick");

        mOneTouchUltra2.sendDMATCommand();
    }

    @Click(R.id.dmquestionBtn)
    void dmquestionButtonClick() {
        Log.d(TAG, "MainActivity.dmquestionButtonClick");

        mOneTouchUltra2.DMQuestionCommand();
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
    public void onByteReceivedEvent(ByteReceivedEvent event) {
        Log.d(TAG, "MainActivity.onByteReceivedEvent :: bytes = [" + Utils.bytesToHexString(event.getBytes()) + "]");

        appendOutputView(Utils.bytesToHexString(event.getBytes()) + " ");
    }

    @Subscribe
    public void onInitEvent(InitEvent event) {
        Log.d(TAG, "MainActivity.onInitEvent");

        if (event.getResult()) {
            showCommands();

            mInitButton.setVisibility(View.INVISIBLE);
            mCloseButton.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onCloseEvent(CloseEvent event) {
        Log.d(TAG, "MainActivity.onCloseEvent");

        if (event.getResult()) {
            mInitButton.setVisibility(View.VISIBLE);
            mCloseButton.setVisibility(View.INVISIBLE);

            hideCommands();

            mOutputTextView.setText("");
        }
    }

    @Subscribe
    public void onCommandStartEvent(CommandStartEvent event) {
        Log.d(TAG, "MainActivity.onCommandStartEvent");

        clearOutputView();
        appendOutputView(event.getCommand() + "\n");
    }

}
