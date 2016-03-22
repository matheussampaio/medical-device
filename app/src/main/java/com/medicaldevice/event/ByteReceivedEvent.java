package com.medicaldevice.event;

public class ByteReceivedEvent {
    private final byte mByte;

    public ByteReceivedEvent(byte b) {
        mByte = b;
    }

    public byte getByte() {
        return mByte;
    }
}
