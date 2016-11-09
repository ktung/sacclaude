package com.sacc.entity;

import com.googlecode.objectify.annotation.*;

/**
 * Created by djoé on 04/11/2016.
 */
@Entity
public class User {
    @Id
    private String id;

    private SLA sla;

    public User()
    {
        id = "NonMemberUser";
        sla = SLA.BRONZE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SLA getSla() {
        return sla;
    }

    public void setSla(SLA sla) {
        this.sla = sla;
    }
}
