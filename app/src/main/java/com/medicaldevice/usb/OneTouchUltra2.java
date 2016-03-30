package com.medicaldevice.usb;

import android.content.Context;

import com.google.common.primitives.Bytes;
import com.medicaldevice.event.ByteReceivedEvent;
import com.medicaldevice.event.CommandStartEvent;
import com.medicaldevice.event.DataReceivedEvent;
import com.medicaldevice.utils.Logger;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

@EBean
public class OneTouchUltra2 extends Device {

    private static final String TAG = "OneTouchUltra2";

    // Commands Ids
    public static final String COMMAND_DMP = "DMP";
    public static final String COMMAND_DMF = "DMF";
    public static final String COMMAND_DMS = "DMS";
    public static final String COMMAND_DMAT = "DM@";
    public static final String COMMAND_DMQUESTION = "DM?";

    // Commands Hex Array
    public static final String[] COMMAND_DMP_DATA = {"0x11", "0x0D", "0x44", "0x4D", "0x50"};
    public static final String[] COMMAND_DMF_DATA = {"0x11", "0x0D", "0x44", "0x4D", "0x46"};
    public static final String[] COMMAND_DMAT_DATA = {"0x11", "0x0D", "0x44", "0x4D", "0x40"};
    public static final String[] COMMAND_DMQUESTION_DATA = {"0x11", "0x0D", "0x44", "0x4D", "0x3F"};
    public static final String[] COMMAND_DMS_DATA =  {"0x11", "0x0D", "0x44", "0x4D", "0x53", "0x0D", "0x0D"};

    private ArrayList<Byte> arrayBytesReceived = new ArrayList<>();
    private String lastCommand;
    private String strLines = "";
    private int lines = 0;

    public OneTouchUltra2(Context context) {
        super(context);
    }

    public void sendDMPCommand() {
        Logger.d(TAG, "OneTouchUltra2.sendDMPCommand");
        arrayBytesReceived.clear();
        lines = 0;
        strLines = "";

        sendCommand(COMMAND_DMP, COMMAND_DMP_DATA);
    }

    public void sendDMFCommand() {
        Logger.d(TAG, "OneTouchUltra2.sendDMFCommand");
        sendCommand(COMMAND_DMF, COMMAND_DMF_DATA);
    }

    public void sendDMATCommand() {
        Logger.d(TAG, "OneTouchUltra2.sendDMATCommand");
        sendCommand(COMMAND_DMAT, COMMAND_DMAT_DATA);
    }

    public void sendDMQuestionCommand() {
        Logger.d(TAG, "OneTouchUltra2.sendDMQuestionCommand");
        sendCommand(COMMAND_DMQUESTION, COMMAND_DMQUESTION_DATA);
    }

    public void sendDMSCommand() {
        Logger.d(TAG, "OneTouchUltra2.sendDMSCommand");
        sendCommand(COMMAND_DMS, COMMAND_DMS_DATA);
    }

    private void sendCommand(String command, String[] commandHexStringArray) {
        Logger.d(TAG, "OneTouchUltra2.sendCommand");
        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent(command));

        lastCommand = command;

        sendBytes(commandByte);
    }

    public void register() {
        Logger.d(TAG, "OneTouchUltra2.register");
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        Logger.d(TAG, "OneTouchUltra2.unregister");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onByteReceivedEvent(ByteReceivedEvent event) {
        Logger.d(TAG, "OneTouchUltra2.onByteReceivedEvent :: bytes = [" + Utils.bytesToHexString(event.getByte()) + "]");

        if (COMMAND_DMP.equalsIgnoreCase(lastCommand)) {
            handleCommandDMP(event);
        } else {
            String data = Utils.bytesToHexString(event.getByte(), true);
            EventBus.getDefault().post(new DataReceivedEvent(data));
        }
    }

    private void handleCommandDMP(ByteReceivedEvent event) {
        Logger.d(TAG, "OneTouchUltra2.handleCommandDMP");
        arrayBytesReceived.add(event.getByte());

        int index = arrayBytesReceived.size();

        if (index > 2 && index <= 5) {
            strLines += Utils.bytesToHexString(event.getByte());
        } else if (index == 6) {
            lines = Integer.parseInt(Utils.hexToString(strLines), 10);
        }

        if (index == (33 + (61 * lines))) {
            String data = new String(Bytes.toArray(arrayBytesReceived));

            EventBus.getDefault().post(new DataReceivedEvent(data));
        }
    }
}
