package com.sacc.entity;

import com.googlecode.objectify.annotation.Entity;
import com.sacc.entity.FORMAT;

/**
 * Created by djoé on 28/10/2016.
 */
@Entity
public class Video {

    private String name;
    private int duration;
    private FORMAT format;
    private boolean isConverted;
    private String userId;
    private STATUS status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public FORMAT getFormat() {
        return format;
    }

    public void setFormat(FORMAT format) {
        this.format = format;
    }


    public boolean isConverted() {
        return isConverted;
    }

    public void setConverted(boolean converted) {
        isConverted = converted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
