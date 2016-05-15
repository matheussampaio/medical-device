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

    @SerializedName("cloudUpdateFlag")
    public boolean cloudUpdateFlag;

    public OTUData() {}

    public OTUData(long dateTime, int glucose, String serial, String unit, String userFlag, String mealComment, boolean cloudUpdateFlag ) {
        this.dateTime = dateTime;
        this.glucose = glucose;
        this.serial = serial;
        this.unit = unit;
        this.userFlag = userFlag;
        this.mealComment = mealComment;
        this.cloudUpdateFlag = cloudUpdateFlag;
    }

    public void setCloudUpdateFlag(boolean cloudUpdateFlag){
        this.cloudUpdateFlag = cloudUpdateFlag;
    }

    @Override
    public String toString() {
        return String.format("DateTime: %d, Glucose: %d, Serial: %s, UserFlag: %s, MealComment: %s, cloudUpdateFlag: %b", dateTime, glucose, serial, userFlag, mealComment, cloudUpdateFlag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OTUData otuData = (OTUData) o;

        if (dateTime != otuData.dateTime) return false;
        if (glucose != otuData.glucose) return false;
        if (!serial.equals(otuData.serial)) return false;
        if (!unit.equals(otuData.unit)) return false;
        if (!userFlag.equals(otuData.userFlag)) return false;
        return mealComment.equals(otuData.mealComment);

    }

    @Override
    public int hashCode() {
        int result = (int) (dateTime ^ (dateTime >>> 32));
        result = 31 * result + glucose;
        result = 31 * result + serial.hashCode();
        result = 31 * result + unit.hashCode();
        result = 31 * result + userFlag.hashCode();
        result = 31 * result + mealComment.hashCode();
        return result;
    }
}


