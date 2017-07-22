package com.thoughtpal.model.admin;


import lombok.Data;

/**
 * Created by robertwood on 4/30/16.
 */
@Data
public class Workspace {

    //@Id
    private String  id;

    private String  ownerId;
    private String  name;

    public Workspace(String ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }

    /*
    @Override
    public String toString() {
        return "Workspace{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }*/
}
