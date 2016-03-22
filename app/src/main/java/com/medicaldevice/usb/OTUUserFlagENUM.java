package com.medicaldevice.usb;

public enum OTUUserFlagENUM {
    NONE("N"),
    BEFORE_MEAL("B"),
    AFTER_MEAL("A");

    private String value;

    private OTUUserFlagENUM(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
