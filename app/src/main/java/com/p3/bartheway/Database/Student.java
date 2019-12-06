package com.p3.bartheway.Database;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {

    @Expose
    @SerializedName("title") private String title;
    @Expose
    @SerializedName("studentName") private String studentName;
    @Expose
    @SerializedName("studentEmail") private String studentEmail;
    @Expose
    @SerializedName("studentNumber") private int studentNumber;
    @Expose
    @SerializedName("card_uid") private long card_uid;
    @Expose
    @SerializedName("success") private Boolean success;
    @Expose
    @SerializedName("message") private String message;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    public long getCard_uid() {
        return card_uid;
    }

    public void setCard_uid(long card_uid) {
        this.card_uid = card_uid;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
