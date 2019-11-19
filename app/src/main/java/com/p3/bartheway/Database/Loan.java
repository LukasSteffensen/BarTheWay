package com.p3.bartheway.Database;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class Loan {

    @Expose
    @SerializedName("title") private String title;
    @Expose
    @SerializedName("card_uid") private int card_uid;
    @Expose
    @SerializedName("timestamp") private Timestamp timestamp;
    @Expose
    @SerializedName("returned") private byte returned;
    @Expose
    @SerializedName("success") private boolean success;
    @Expose
    @SerializedName("message") private String message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCard_uid() {
        return card_uid;
    }

    public void setCard_uid(int card_uid) {
        this.card_uid = card_uid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public byte getReturned() {
        return returned;
    }

    public void setReturned(byte returned) {
        this.returned = returned;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
