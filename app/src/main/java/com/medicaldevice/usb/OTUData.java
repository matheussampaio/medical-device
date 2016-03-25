package com.medicaldevice.usb;

import com.orm.SugarRecord;

public class OTUData extends SugarRecord {
    public String date;
    public  String time;
    public long dateTime;
    public int glucose;
    public String serial;
    public String unit;
    public String userFlag;
    public String mealComment;

    public OTUData() {

    }

    public OTUData(long dateTime, int glucose, String serial, String unit, String userFlag, String mealComment) {
        this.dateTime = dateTime;
        this.glucose = glucose;
        this.serial = serial;
        this.unit = unit;
        this.userFlag = userFlag;
        this.mealComment = mealComment;
    }
}


