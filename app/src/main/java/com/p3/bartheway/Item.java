package com.p3.bartheway;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @Expose
    @SerializedName("title") private String title;
    @Expose
    @SerializedName("language") private String language;
    @Expose
    @SerializedName("description") private String description;
    @Expose
    @SerializedName("minPlayers") private int minPlayers;
    @Expose
    @SerializedName("maxPlayers") private int maxPlayers;
    @Expose
    @SerializedName("card_uid") private int card_uid;
    @Expose
    @SerializedName("duration") private String duration;
    @Expose
    @SerializedName("year") private int year;
    @Expose
    @SerializedName("success") private Boolean success;
    @Expose
    @SerializedName("message") private String message;


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCardUid() {
        return card_uid;
    }

    public void setCardUid(int cardUid) {
        this.card_uid = cardUid;
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
