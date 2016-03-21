package com.medicaldevice.event;

public class CommandStartEvent {

    private final String mCommand;

    public CommandStartEvent(String command) {
        mCommand = command;
    }

    public String getCommand() {
        return mCommand;
    }
}
