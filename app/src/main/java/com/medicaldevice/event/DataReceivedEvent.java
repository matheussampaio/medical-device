package com.medicaldevice.event;

/**
 * Class to handle event of receiving data
 */
public class DataReceivedEvent {

    private String mData;

    /**
     * Constructor of DataReceivedEvent
     *
     * @param data the data received
     */
    public DataReceivedEvent(String data) {
        mData = data;
    }

    /**
     * Get received data
     *
     * @return mData
     */
    public String getData() {
        return mData;
    }
}
