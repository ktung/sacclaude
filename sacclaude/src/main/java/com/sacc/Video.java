package com.sacc;

enum FORMAT {AVI, MPEG4, MKV, OGG, FLV};
/**
 * Created by djoé on 28/10/2016.
 */
public class Video {

    private String name;
    private int duration;
    private FORMAT format;

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


}
