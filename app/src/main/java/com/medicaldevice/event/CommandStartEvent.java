package com.medicaldevice.event;

/**
 * Class to handle event of starting a command.
 */
public class CommandStartEvent {

    private final String mCommand;

    /**
     * Constructor of CommandStartEvent
     *
     * @param command the command started
     */
    public CommandStartEvent(String command) {
        mCommand = command;
    }

    /**
     * Get command start event
     *
     * @return mCommand
     */
    public String getCommand() {
        return mCommand;
    }
}
