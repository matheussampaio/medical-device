package com.medicaldevice.usb;

public enum OTUMealCommentENUM {
    NO_COMMENT(0),
    NOT_ENOUGH_FOOD(1),
    TOO_MUCH_FOOD(2),
    MILD_EXERCISE(3),
    HARD_EXERCISE(4),
    MEDICATION(5),
    STRESS(6),
    ILLNESS(7),
    FEEL_HYPO(8),
    MENSES(9),
    VACATION(10),
    OTHER(11);

    private int value;

    private OTUMealCommentENUM(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

