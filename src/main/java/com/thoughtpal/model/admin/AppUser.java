package com.thoughtpal.model.admin;


import lombok.Data;

/**
 * Created by robertwood on 6/30/16.
 */
@Data
public class AppUser {

    //@Id
    private String  id;

    private String  userName;
    private String  email;
    private String  firstName;
    private String  lastName;

    public AppUser(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    /*
    @Override
    public String toString() {
        return "AppUser{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }*/
}
