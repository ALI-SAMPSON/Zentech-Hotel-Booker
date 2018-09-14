package io.zentechhotelbooker.models;

public class Admin {

    //class variables to be used as fields for the database
    private String email;

    //default constructor
    public Admin(){
    }

    //constructor with two or more parameters
    public Admin(String email){
        this.email = email;
    }

    //Getter and Setter method for Username
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }

}
