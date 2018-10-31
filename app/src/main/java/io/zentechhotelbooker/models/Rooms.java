package io.zentechhotelbooker.models;

import com.google.firebase.database.Exclude;

public class Rooms {

    //database fields for the database
    private String roomImage_url;
    private String roomNumber;
    private String roomType;
    private String roomPrice;
    // Columns for food Served in room
    private String breakfastServed;
    private String lunchServed;
    private String supperServed;
    // key to store the key reference of the data to delete
    private String key;
    private String user_ImageUrl;

    //default constructor
    public Rooms(){}

    //constructor with two or more parameters
    public Rooms(String roomImage_url,String roomNumber,String roomType,String roomPrice,
                 String breakfastServed, String lunchServed, String supperServed){
        this.roomImage_url = roomImage_url;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.breakfastServed = breakfastServed;
        this.lunchServed = lunchServed;
        this.supperServed = supperServed;
    }

    //Getter and Setter method for Room Image
    public void setRoomImage_url(String roomImage_url){
        this.roomImage_url = roomImage_url;
    }
    public String getRoomImage_url(){
        return roomImage_url;
    }

    //Getter and Setter method for Room number
    public void setRoomNumber(String roomNumber){
        this.roomNumber = roomNumber;
    }
    public String getRoomNumber(){
        return roomNumber;
    }

    //Getter and Setter method for Room Type
    public void setRoomType(String roomType){
        this.roomType = roomType;
    }
    public String getRoomType(){
        return roomType;
    }

    //Getter and Setter method for Room price
    public void setRoomPrice(String roomPrice){
        this.roomPrice = roomPrice;
    }
    public String getRoomPrice(){
        return roomPrice;
    }

    public void setBreakfastServed(String breakfastServed){
        this.breakfastServed = breakfastServed;
    }
    public String getBreakfastServed(){
        return breakfastServed;
    }

    public void setLunchServed(String lunchServed) {
        this.lunchServed = lunchServed;
    }

    public String getLunchServed() {
        return lunchServed;
    }

    public void setSupperServed(String supperServed) {
        this.supperServed = supperServed;
    }

    public String getSupperServed() {
        return supperServed;
    }

    @Exclude
    public void setKey(String key){
        this.key = key;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setUser_ImageUrl(String user_ImageUrl){
        this.user_ImageUrl = user_ImageUrl;
    }

    public String getUser_ImageUrl(){
        return user_ImageUrl;
    }
}
