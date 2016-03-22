package com.medicaldevice.event;

public class DataReceivedEvent {

    private String mData;

    public DataReceivedEvent(String data) {
        mData = data;
    }

    public String getData() {
        return mData;
    }
}
