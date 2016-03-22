package com.medicaldevice.event;

import java.util.ArrayList;

public class DataReceivedEvent {

    private ArrayList<Byte> bytesReceived;

    public DataReceivedEvent(ArrayList<Byte> bytes) {
        bytesReceived = bytes;
    }

    public ArrayList<Byte> getBytesReceived() {
        return bytesReceived;
    }
}
