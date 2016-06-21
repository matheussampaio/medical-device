package com.medicaldevice.event;

/**
 * Class to handle the event of closing.
 */
public class CloseEvent {
    private final boolean mResult;

    /**
     * Constructor of CloseEvent
     *
     * @param result true if success false if failure
     */
    public CloseEvent(boolean result) {
        mResult = result;
    }

    /**
     * Get result of closing event
     *
     * @return mResult
     */
    public boolean getResult() {
        return mResult;
    }
}
