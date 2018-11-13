package io.zentechhotelbooker.models;

public class Payments {

    //database fields for the database
    String uid;
    String user_name;
    String room_number;
    String room_type;
    String card_number;
    String c_holder_name;
    String card_month;
    String card_year;
    String card_cvc;

    String price;
    String payment_method;
    String mobile_number;
    String imageUrl;

    // Columns for food Served in room
    private String breakfastServed;
    private String lunchServed;
    private String supperServed;


    //default constructor
    public Payments(){}

    //constructor with two or more parameters
    public Payments(String uid, String user_name,
                    String room_number,
                    String room_type,
                    String price,String mobile_number,
                    String breakfastServed,
                    String lunchServed,
                    String supperServed){
        this.uid = uid;
        this.user_name = user_name;
        this.room_number = room_number;
        this.room_type = room_type;
        this.price = price;
        this.mobile_number = mobile_number;
        this.breakfastServed = breakfastServed;
        this.lunchServed = lunchServed;
        this.supperServed = supperServed;
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

    //Getter and Setter method for Room Number
    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }
    public String getRoom_number() {
        return room_number;
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

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getC_holder_name() {
        return c_holder_name;
    }

    public void setC_holder_name(String c_holder_name) {
        this.c_holder_name = c_holder_name;
    }

    public String getCard_month() {
        return card_month;
    }

    public void setCard_month(String card_month) {
        this.card_month = card_month;
    }

    public String getCard_year() {
        return card_year;
    }

    public void setCard_year(String card_year) {
        this.card_year = card_year;
    }

    public String getCard_cvc() {
        return card_cvc;
    }

    public void setCard_cvc(String card_cvc) {
        this.card_cvc = card_cvc;
    }
}
