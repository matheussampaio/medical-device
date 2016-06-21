package com.medicaldevice.event;

/**
 * Class to handle event of ending a command.
 */
public class CommandEndEvent {

    private final String mCommand;

    /**
     * Constructor CommandEndEvent
     *
     * @param command the command ended
     */
    public CommandEndEvent(String command) {
        mCommand = command;
    }

    /**
     * Get command end event
     *
     * @return mCommand
     */
    public String getCommand() {
        return mCommand;
    }
}
