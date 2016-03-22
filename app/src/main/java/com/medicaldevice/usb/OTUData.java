package com.medicaldevice.usb;

import com.orm.SugarRecord;

public class OTUData extends SugarRecord {
    String date;
    String time;
    String glucose;
    String serial;
    String unit;
    String userFlag;
    String mealComment;

    public OTUData() {

    }

    public OTUData(String date, String time, String glucose, String serial, String unit, String userFlag, String mealComment) {
        this.date = date;
        this.time = time;
        this.glucose = glucose;
        this.serial = serial;
        this.unit = unit;
        this.userFlag = userFlag;
        this.mealComment = mealComment;
    }
}


