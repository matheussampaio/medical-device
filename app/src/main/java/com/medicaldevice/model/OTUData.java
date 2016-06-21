package com.medicaldevice.model;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Class OTUData extending SugarRecord which is a database ORM
 */
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

    /**
     * Constructor of OTUData
     */
    public OTUData() {}

    /**
     * Constructor of OTUData
     *
     * @param dateTime         time of measure
     * @param glucose          level of glucose
     * @param serial           device serial
     * @param unit             unit
     * @param userFlag         user flag
     * @param mealComment      meal comment
     * @param cloudUpdateFlag  true if cloud is updated false if not
     */
    public OTUData(long dateTime, int glucose, String serial, String unit, String userFlag, String mealComment, boolean cloudUpdateFlag ) {
        this.dateTime = dateTime;
        this.glucose = glucose;
        this.serial = serial;
        this.unit = unit;
        this.userFlag = userFlag;
        this.mealComment = mealComment;
        this.cloudUpdateFlag = cloudUpdateFlag;
    }

    /**
     * Set cloudUpdateFlag
     *
     * @param cloudUpdateFlag true if cloud is updated false if not
     */
    public void setCloudUpdateFlag(boolean cloudUpdateFlag){
        this.cloudUpdateFlag = cloudUpdateFlag;
    }

    /**
     * Override toString method to show all properties of OTUData class
     *
     * @return message with all properties
     */
    @Override
    public String toString() {
        return String.format("DateTime: %d, Glucose: %d, Serial: %s, UserFlag: %s, MealComment: %s, cloudUpdateFlag: %b", dateTime, glucose, serial, userFlag, mealComment, cloudUpdateFlag);
    }

    /**
     * Check if object o is equals to our object
     *
     * @param o OTUData object
     *
     * @return true if equals false if not
     */
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

    /**
     * OTUData hash code
     *
     * @return hash code
     */
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


