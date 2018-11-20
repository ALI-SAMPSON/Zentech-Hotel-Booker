package io.zentechhotelbooker.models;

public class Users {

    //class variables to be used as fields for the database
    private String uid;
    private String email;
    private String username;
    private String gender;
    private String mobile_number;
    private String imageUrl;

    //default constructor
    public Users(){
    }

    //constructor with two or more parameters
    public Users(String uid, String email, String username, String gender, String mobile_number){
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.mobile_number = mobile_number;

    }

    //Getter and Setter method for Uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    //Getter and Setter method for Username
    public void setEmail(String email){
        this.email= email;
    }

    public String getEmail(){
        return email;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    //Getter and Setter method for Gender
    public void setGender(String gender){
        this.gender = gender;
    }
    public String getGender(){
        return gender;
    }

    //Getter and Setter method for Mobile Number
    public void setMobile_number(String mobile_number){
        this.mobile_number = mobile_number;
    }

    public String getMobile_number(){
        return mobile_number;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

}
