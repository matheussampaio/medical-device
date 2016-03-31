package com.medicaldevice.usb;

import android.content.Context;

import com.google.common.primitives.Bytes;
import com.medicaldevice.event.ByteReceivedEvent;
import com.medicaldevice.event.CommandStartEvent;
import com.medicaldevice.event.DataReceivedEvent;
import com.medicaldevice.model.OTUData;
import com.medicaldevice.rest.RESTful;
import com.medicaldevice.utils.Logger;
import com.medicaldevice.utils.Utils;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
//        Logger.d(TAG, "OneTouchUltra2.onByteReceivedEvent :: bytes = [" + Utils.bytesToHexString(event.getByte()) + "]");

        if (COMMAND_DMP.equalsIgnoreCase(lastCommand)) {
            handleCommandDMP(event);
        } else {
            String data = Utils.bytesToHexString(event.getByte(), true);
            EventBus.getDefault().post(new DataReceivedEvent(data));
        }
    }

    private void handleCommandDMP(ByteReceivedEvent event) {
//        Logger.d(TAG, "OneTouchUltra2.handleCommandDMP");
        arrayBytesReceived.add(event.getByte());

        int index = arrayBytesReceived.size();

        if (index > 2 && index <= 5) {
            strLines += Utils.bytesToHexString(event.getByte());
        } else if (index == 6) {
            lines = Integer.parseInt(Utils.hexToString(strLines), 10);
        }

        if (index == (33 + (61 * lines))) {
            String data = new String(Bytes.toArray(arrayBytesReceived));

            parseData(data);
        }
    }

    public void parseData(String data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("mm/dd/yy HH:mm:ss");
        String[] dataArray = data.split("\n");
        String output = "";
        ArrayList<OTUData> otuEntries = new ArrayList<>();

        String headerRegex = "P (\\d{3}),\"(\\w*)\",\"(.*) \" (.*)";
        String entryRegex = "P \"(\\w{3})\",\"(.*)\",\"(.*)   \",\"  (\\d{3}) \",\"(\\D)\",\"(\\d{2})\"(.*)";

        Pattern headerPattern = Pattern.compile(headerRegex);
        Pattern entryPattern = Pattern.compile(entryRegex);

        Matcher headerMatcher = headerPattern.matcher(dataArray[0]);

        if (headerMatcher.find()) {
            int entries = Integer.parseInt(headerMatcher.group(1));
            String serial = headerMatcher.group(2);
            String unit = headerMatcher.group(3);

            Logger.d(String.format("entries: %d - serial: %s - unit: %s", entries, serial, unit));

            for (int i = 1; i < dataArray.length; i++) {

                Matcher em = entryPattern.matcher(dataArray[i]);

                if (em.find()) {

                    String date = em.group(2);
                    String time = em.group(3);
                    int glucose = Integer.parseInt(em.group(4));
                    String userFlag = em.group(5);
                    String mealComment = em.group(6);

                    DateTime dt = formatter.parseDateTime(String.format("%s %s", date, time));

                    long dateTime = dt.getMillis() / 1000;

                    OTUData otudata = new OTUData(dateTime, glucose, serial, unit, userFlag, mealComment);

                    otuEntries.add(otudata);

                    output += "----------------\n";
                    output += otudata.toString() + "\n";
                    output += "----------------\n";

                } else {
                    Logger.e(String.format("entry don't match: %s", dataArray[i]));
                }
            }

        } else {
            Logger.e(String.format("header don't match: %s", dataArray[0]));
        }

        output += String.format("Total Count: %d\n", otuEntries.size());

        EventBus.getDefault().post(new DataReceivedEvent(output));

        postEntries(otuEntries);

    }

    public void postEntries(ArrayList<OTUData> otuEntries) {
        for (OTUData otuentrie : otuEntries) {
            Call<OTUData> call = RESTful.getInstance().postOTUData(otuentrie);
            call.enqueue(new Callback<OTUData>() {

                @Override
                public void onResponse(Call<OTUData> call, Response<OTUData> response) {
                    Logger.d(String.format("status: %d", response.code()));
                }

                @Override
                public void onFailure(Call<OTUData> call, Throwable t) {
                    Logger.e("failure", t);
                }
            });
        }
    }

}
