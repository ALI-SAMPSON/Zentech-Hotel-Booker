package io.zentechhotelbooker.models;

public class Rooms {

    //database fields for the database
    private String room_image;
    private String room_number;
    private String price;

    //default constructor
    public Rooms(){}

    //constructor with two or more parameters
    public Rooms(String room_image,String room_number,String price){
        this.room_image = room_image;
        this.room_number = room_number;
        this.price = price;
    }

    //Getter and Setter method for Room Image
    public void setRoom_image(String room_image){
        this.room_image = room_image;
    }
    public String getRoom_image(){
        return room_image;
    }

    //Getter and Setter method for Room number
    public void setRoom_number(String room_number){
        this.room_number = room_number;
    }
    public String getRoom_number(){
        return room_number;
    }

    //Getter and Setter method for Room price
    public void setPrice(String price){
        this.price = price;
    }
    public String getPrice(){
        return price;
    }

}
