package com.medicaldevice.event;

/**
 * Class to handle the event of receiving a byte.
 */
public class ByteReceivedEvent {
    private final byte mByte;

    /**
     * Constructor of ByteReceivedEvent
     *
     * @param b byte of event
     */
    public ByteReceivedEvent(byte b) {
        mByte = b;
    }

    /**
     * Get byte received
     *
     * @return mByte
     */
    public byte getByte() {
        return mByte;
    }
}
