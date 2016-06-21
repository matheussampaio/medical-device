package com.medicaldevice.event;

/**
 * Class to handle event of initialization
 */
public class InitEvent {

    private final boolean mResult;
    private final String mMessage;

    /**
     * Constructor of InitEvent
     *
     * @param result  true on success false on failure
     * @param message error message
     */
    public InitEvent(boolean result, String message) {
        mResult = result;
        mMessage = message;
    }

    /**
     * Constructor of InitEvent
     *
     * @param result  true on success false on failure
     */
    public InitEvent(boolean result) {
        mResult = result;
        mMessage = null;
    }

    /**
     * Get result
     *
     * @return true or false
     */
    public boolean getResult() {
        return mResult;
    }

    /**
     * Get message
     *
     * @return error message
     */
    public String getMessage() {
        return mMessage;
    }
}
