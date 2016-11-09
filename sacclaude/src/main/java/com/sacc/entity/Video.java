package com.sacc.entity;

import com.google.cloud.storage.BlobId;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import com.sacc.entity.FORMAT;

import java.util.Date;

/**
 * Created by djoé on 28/10/2016.
 */
@Entity
public class Video {

    @Id private String name;
    private int duration;
    private FORMAT format;
    private boolean isConverted;
    private String userId;
    @Parent private Key<User> user;
    @Index private Date date;
    private BlobId blobId;
    private STATUS status;
    private SLA sla;

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

    public Key<User> getUser() {
        return user;
    }

    public void setUser(Key<User> user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BlobId getBlobId() {
        return blobId;
    }

    public void setBlobId(BlobId blobId) {
        this.blobId = blobId;
    }

    public SLA getSla() {
        return sla;
    }

    public void setSla(SLA sla) {
        this.sla = sla;
    }
}
