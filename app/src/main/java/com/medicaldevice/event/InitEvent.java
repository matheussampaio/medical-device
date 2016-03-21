package com.medicaldevice.event;

public class InitEvent {

    private final boolean mResult;
    private final String mMessage;

    public InitEvent(boolean result, String message) {
        mResult = result;
        mMessage = message;
    }

    public InitEvent(boolean result) {
        mResult = result;
        mMessage = null;
    }

    public boolean getResult() {
        return mResult;
    }

    public String getMessage() {
        return mMessage;
    }
}
