package io.zentechhotelbooker.models;

public class Admin {

    //class variables to be used as fields for the database
    private String uid;
    private String username;
    private String email;
    private String password;

    //default constructor
    public Admin(){
    }

    //constructor with two or more parameters
    public Admin(String uid,String username,String email, String password){
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    //Getter and Setter method for Uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //Getter and Setter method for Email
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
