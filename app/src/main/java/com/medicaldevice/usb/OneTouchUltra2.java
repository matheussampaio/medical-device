package com.medicaldevice.usb;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.medicaldevice.event.ByteReceivedEvent;
import com.medicaldevice.event.CommandStartEvent;
import com.medicaldevice.event.DataReceivedEvent;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

@EBean
public class OneTouchUltra2 extends Device {

    private static final String TAG = "OneTouchUltra2";

    private ArrayList<Byte> arrayBytesReceived = new ArrayList<Byte>();
    private String lastCommand;
    private String strLines = "";
    private int lines = 0;

    public OneTouchUltra2(Context context) {
        super(context);
    }

    public void sendDMPCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x50"};

        arrayBytesReceived.clear();
        lines = 0;
        strLines = "";

        sendCommand("DMP", commandHexStringArray);
    }

    public void sendDMFCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x46"};

        sendCommand("DMF", commandHexStringArray);
    }

    public void sendDMATCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x40"};

        sendCommand("DM@", commandHexStringArray);
    }

    public void sendDMQuestionCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x3F"};

        sendCommand("DM?", commandHexStringArray);
    }

    public void sendDMSCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x53", "0x0D", "0x0D"};

        sendCommand("DMS", commandHexStringArray);
    }

    private void sendCommand(String command, String[] commandHexStringArray) {
        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent(command));

        lastCommand = command;

        sendBytes(commandByte);
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onByteReceivedEvent(ByteReceivedEvent event) {
        Log.d(TAG, "MainActivity.onByteReceivedEvent :: bytes = [" + Utils.bytesToHexString(event.getByte()) + "]");

        if (lastCommand.equals("DMP")) {
            handleCommandDMP(event);
        } else {
            String data = Utils.bytesToHexString(event.getByte(), true);
            EventBus.getDefault().post(new DataReceivedEvent(data));
        }
    }

    // TODO: Can be refactored.
    private void handleCommandDMP(ByteReceivedEvent event) {
        Log.d(TAG, "OneTouchUltra2.handleCommandDMP");
        arrayBytesReceived.add(event.getByte());

        int index = arrayBytesReceived.size();

        if (index > 2 && index <= 5) {
            strLines += Utils.bytesToHexString(event.getByte());
        } else if (index == 6) {
            lines = Integer.parseInt(Utils.hexToString(strLines), 10);
        }

        if (index == (33 + (61 * lines))) {
            byte[] bytes = new byte[arrayBytesReceived.size()];

            for (int i = 0; i < arrayBytesReceived.size(); i++) {
                bytes[i] = arrayBytesReceived.get(i);
            }

            String data = new String(bytes);

            EventBus.getDefault().post(new DataReceivedEvent(data));
        }
    }
}
