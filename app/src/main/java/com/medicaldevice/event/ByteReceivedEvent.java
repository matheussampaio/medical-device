package com.medicaldevice.event;

public class ByteReceivedEvent {
    public final byte[] mBytes;

    public ByteReceivedEvent(byte[] bytes) {
        mBytes = bytes;
    }

    public ByteReceivedEvent(byte b) {
        mBytes = new byte[1];

        mBytes[0] = b;
    }

    public byte[] getBytes() {
        return mBytes;
    }
}
