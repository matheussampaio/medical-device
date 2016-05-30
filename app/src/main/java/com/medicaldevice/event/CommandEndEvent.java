package com.medicaldevice.event;

public class CommandEndEvent {

    private final String mCommand;

    public CommandEndEvent(String command) {
        mCommand = command;
    }

    public String getCommand() {
        return mCommand;
    }
}
