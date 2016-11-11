package com.sacc.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing a request to convert a video
 * Created by lpotages on 04/11/16.
 */

public class ConversionRequest {
    private String name;
    private int duration;

    private SLA sla;
    private String mailAddress;

    private List<FORMAT> convertTypes;


    public ConversionRequest(){
        convertTypes = new ArrayList<>();
    }

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

    public SLA getSla() {
        return sla;
    }

    public void setSla(SLA sla) {
        this.sla = sla;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public List<FORMAT> getConvertTypes() {
        return convertTypes;
    }

    public void setConvertTypes(List<FORMAT> convertTypes) {
        this.convertTypes = convertTypes;
    }

    public void addConvertType(FORMAT format){
        convertTypes.add(format);
    }

    @Override
    public String toString() {
        return "ConversionRequest{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", sla=" + sla +
                ", mailAddress='" + mailAddress + '\'' +
                ", convertTypes=" + convertTypes +
                '}';
    }


}
