package io.zentechhotelbooker.models;

public class Payments {

    //database fields for the database
    String uid;
    String user_name;
    String room_type;
    String price;
    String payment_method;
    String mobile_number;
    String imageUrl;


    //default constructor
    public Payments(){}

    //constructor with two or more parameters
    public Payments(String uid, String user_name, String room_type,String price,String mobile_number){
        this.uid = uid;
        this.user_name = user_name;
        this.room_type = room_type;
        this.price = price;
        this.mobile_number = mobile_number;
    }

    //Getter and Setter method for Uid
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return uid;
    }

    //Getter and Setter method for Username
    public void setUser_name(String user_name){
        this.user_name = user_name;
    }
    public String getUser_name() {
        return user_name;
    }

    //Getter and Setter method for Room number
    public void setRoom_type(String room_type){
        this.room_type = room_type;
    }
    public String getRoom_type(){
        return room_type;
    }

    //Getter and Setter method for price
    public void setPrice(String price){
        this.price = price;
    }
    public String getPrice(){
        return price;
    }

    //Getter and Setter method for Payment method
    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
    public String getPayment_method() {
        return payment_method;
    }

    //Getter and Setter method for mobile number
    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
    public String getMobile_number() {
        return mobile_number;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }
}
