package com.thoughtpal.model;

import org.springframework.data.annotation.Id;

/**
 * Created by robertwood on 4/30/16.
 */
public class Workspace {

    @Id
    private String  id;

    private String  ownerId;
    private String  name;

    public Workspace(String ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
