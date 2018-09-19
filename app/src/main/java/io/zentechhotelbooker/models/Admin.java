package io.zentechhotelbooker.models;

public class Admin {

    //class variables to be used as fields for the database
    private String email;
    //private String password;

    //default constructor
    public Admin(){
    }

    //constructor with two or more parameters
    public Admin(String email){
        this.email = email;
    }

    //Getter and Setter method for Email
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }

    /*
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    */

}
