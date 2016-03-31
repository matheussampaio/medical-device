package com.medicaldevice.model;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class OTUData extends SugarRecord {

    @SerializedName("dateTime")
    public long dateTime;

    @SerializedName("glucose")
    public int glucose;

    @SerializedName("serial")
    public String serial;

    @SerializedName("unit")
    public String unit;

    @SerializedName("userFlag")
    public String userFlag;

    @SerializedName("mealComment")
    public String mealComment;

    public OTUData() {}

    public OTUData(long dateTime, int glucose, String serial, String unit, String userFlag, String mealComment) {
        this.dateTime = dateTime;
        this.glucose = glucose;
        this.serial = serial;
        this.unit = unit;
        this.userFlag = userFlag;
        this.mealComment = mealComment;
    }

    @Override
    public String toString() {
        return String.format("DateTime: %d, Glucose: %d, Serial: %s, UserFlag: %s, MealComment: %s", dateTime, glucose, serial, userFlag, mealComment);
    }
}


