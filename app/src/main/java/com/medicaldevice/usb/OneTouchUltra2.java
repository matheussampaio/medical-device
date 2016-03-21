package com.medicaldevice.usb;

import android.content.Context;

import com.medicaldevice.event.CommandStartEvent;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

@EBean
public class OneTouchUltra2 extends Device {

    public OneTouchUltra2(Context context) {
        super(context);
    }

    public void sendDMPCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x50"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent("DMP"));

        sendBytes(commandByte);
    }

    public void sendDMFCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x46"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent("DMF"));

        sendBytes(commandByte);
    }

    public void sendDMATCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x40"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent("DM@"));

        sendBytes(commandByte);
    }

    public void DMQuestionCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x3F"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent("DM?"));

        sendBytes(commandByte);
    }

    public void sendDMSCommand() {
        String[] commandHexStringArray = {"0x11", "0x0D", "0x44", "0x4D", "0x53", "0x0D", "0x0D"};

        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent("DMS"));

        sendBytes(commandByte);
    }
}
