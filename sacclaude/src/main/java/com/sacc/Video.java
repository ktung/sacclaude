package com.sacc;

import com.sacc.entity.FORMAT;

/**
 * Created by djoé on 28/10/2016.
 */
public class Video {

    private String name;
    private int duration;
    private FORMAT format;
    private boolean isCompressed;

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


    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }
}
