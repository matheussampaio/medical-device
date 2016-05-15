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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
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
    public static final String[] COMMAND_DMS_DATA = {"0x11", "0x0D", "0x44", "0x4D", "0x53", "0x0D", "0x0D"};
    private static final int CODE_SUCCESS = 200;

    // variables to track and handle the data received
    private ArrayList<Byte> arrayBytesReceived = new ArrayList<>();
    private String lastCommand;
    private String strLines = "";
    private int lines = 0;

    public OneTouchUltra2(Context context) {
        super(context);
    }

    public void sendDMPCommand() {
        Logger.d(TAG, "OneTouchUltra2::sendDMPCommand");
        arrayBytesReceived.clear();
        lines = 0;
        strLines = "";

        sendCommand(COMMAND_DMP, COMMAND_DMP_DATA);
    }

    public void sendDMFCommand() {
        Logger.d(TAG, "OneTouchUltra2::sendDMFCommand");
        sendCommand(COMMAND_DMF, COMMAND_DMF_DATA);
    }

    public void sendDMATCommand() {
        Logger.d(TAG, "OneTouchUltra2::sendDMATCommand");
        sendCommand(COMMAND_DMAT, COMMAND_DMAT_DATA);
    }

    public void sendDMQuestionCommand() {
        Logger.d(TAG, "OneTouchUltra2.sendDMQuestionCommand");
        sendCommand(COMMAND_DMQUESTION, COMMAND_DMQUESTION_DATA);
    }

    public void sendDMSCommand() {
        Logger.d(TAG, "OneTouchUltra2::sendDMSCommand");
        sendCommand(COMMAND_DMS, COMMAND_DMS_DATA);
    }

    public void register() {
        Logger.d(TAG, "OneTouchUltra2::register");
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        Logger.d(TAG, "OneTouchUltra2::unregister");
        EventBus.getDefault().unregister(this);
    }

    public void sendEntriesToCloud() {
        Logger.d(TAG, "OneTouchUltra2::sendEntriesToCloud");

        if (!Utils.isInternetAvailable(mContext)) {
            Logger.e("Internet not available");
        } else {
            List<OTUData> otuEntries = OTUData.listAll(OTUData.class);

            Logger.i("Total entries: " + otuEntries.size());

            for (OTUData entry : otuEntries) {
                if (!entry.cloudUpdateFlag) {
                    sendEntryToCloud(entry);
                }
            }
        }
    }

    @Subscribe
    public void onByteReceivedEvent(ByteReceivedEvent event) {
        if (COMMAND_DMP.equalsIgnoreCase(lastCommand)) {
            handleCommandDMP(event);
        } else {
            String data = Utils.bytesToHexString(event.getByte(), true);
            EventBus.getDefault().post(new DataReceivedEvent(data));
        }
    }

    private void sendCommand(String command, String[] commandHexStringArray) {
        Logger.d(TAG, "OneTouchUltra2::sendCommand");
        byte[] commandByte = Utils.hexStringToByteArray(commandHexStringArray);

        EventBus.getDefault().post(new CommandStartEvent(command));

        lastCommand = command;

        sendBytes(commandByte);
    }

    private void handleCommandDMP(ByteReceivedEvent event) {
        arrayBytesReceived.add(event.getByte());

        int index = arrayBytesReceived.size();

        if (index > 2 && index <= 5) {
            strLines += Utils.bytesToHexString(event.getByte());
        } else if (index == 6) {
            lines = Integer.parseInt(Utils.hexToString(strLines), 10);
        }

        if (index == (33 + (61 * lines))) {
            String data = new String(Bytes.toArray(arrayBytesReceived));

            List<OTUData> entries = parseData(data);
            saveNewEntriesToDatabase(entries);
            sendEntriesToCloud();
        }
    }

    private ArrayList<OTUData> parseData(String data) {
        Logger.d(TAG, "OneTouchUltra2::parseData");
        DateTimeFormatter formatter = DateTimeFormat.forPattern("mm/dd/yy HH:mm:ss");
        String[] dataArray = data.split("\n");

        ArrayList<OTUData> deviceEntries = new ArrayList<>();

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

                    OTUData otudata = new OTUData(dateTime, glucose, serial, unit, userFlag, mealComment, false);

                    deviceEntries.add(otudata);

                } else {
                    Logger.e(String.format("entry don't match: %s", dataArray[i]));
                }
            }

        } else {
            Logger.e(String.format("header don't match: %s", dataArray[0]));
        }

        return deviceEntries;
    }

    private void saveNewEntriesToDatabase(List<OTUData> entries) {
        Logger.d(TAG, "OneTouchUltra2::saveNewEntriesToDatabase");
        List<OTUData> databaseEntries = OTUData.listAll(OTUData.class);

        for (OTUData entry : entries) {
            if (!databaseEntries.contains(entry)) {
                Logger.i("New otu entry: " + entry.toString());
                entry.save();
            }
        }
    }

    private void sendEntryToCloud(final OTUData entry) {
        Logger.d(TAG, "OneTouchUltra2::sendEntryToCloud");
        Call<OTUData> call = RESTful.getInstance().postOTUData(entry);
        call.enqueue(new Callback<OTUData>() {
            @Override
            public void onResponse(Call<OTUData> call, Response<OTUData> response) {
                Logger.d("Send Entry Response Code: " + response.code());

                if (response.code() == CODE_SUCCESS) {
                    Logger.d(String.format("Entry saved. Status: %d", response.code()));
                    entry.setCloudUpdateFlag(true);
                    entry.save();
                } else if (response.code() == 400) {
                    ResponseBody responseBody = response.errorBody();

                    try {
                        JSONObject body = new JSONObject(responseBody.string());

                        if (body.has("code") && body.getInt("code") == 11000) {
                            Logger.i("Duplicated entry.");

                            entry.setCloudUpdateFlag(true);
                            entry.save();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OTUData> call, Throwable t) {
                Logger.e("failure", t);
            }
        });
    }
}
