package com.medicaldevice.event;

public class CloseEvent {

    private final boolean mResult;

    public CloseEvent(boolean result) {
        mResult = result;
    }

    public boolean getResult() {
        return mResult;
    }
}
