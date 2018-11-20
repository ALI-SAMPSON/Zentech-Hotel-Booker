package io.zentechhotelbooker.models;

public class Reservations {

    //database fields for the database
    String uid;
    String user_name;
    String mobile_number;
    String imageUrl;
    String room_number;
    String room_type;
    String price;

    public Reservations(){}

    public Reservations(String uid,String user_name,
                        String mobile_number,String imageUrl, String room_number,String room_type,String price){

        this.uid = uid;
        this.user_name = user_name;
        this.mobile_number = mobile_number;
        this.imageUrl = imageUrl;
        this.room_number = room_number;
        this.room_type = room_type;
        this.price = price;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
