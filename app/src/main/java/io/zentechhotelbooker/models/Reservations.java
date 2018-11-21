package io.zentechhotelbooker.models;

public class Reservations {

    //database fields for the database
    String uid;
    String user_name;
    String mobile_number;
    String user_image_url;
    String room_image_url;
    String room_number;
    String room_type;
    String room_price;

    public Reservations(){}

    public Reservations(String uid,String user_name,
                        String mobile_number,String user_image_url,
                        String room_image_url, String room_number,
                        String room_type,String room_price){

        this.uid = uid;
        this.user_name = user_name;
        this.mobile_number = mobile_number;
        this.user_image_url = user_image_url;
        this.room_image_url = room_image_url;
        this.room_number = room_number;
        this.room_type = room_type;
        this.room_price = room_price;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getUser_image_url() {
        return user_image_url;
    }

    public void setUser_image_url(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public String getRoom_image_url() {
        return room_image_url;
    }

    public void setRoom_image_url(String room_image_url) {
        this.room_image_url = room_image_url;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getRoom_price() {
        return room_price;
    }

    public void setRoom_price(String room_price) {
        this.room_price = room_price;
    }
}
