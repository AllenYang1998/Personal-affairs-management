package com.example.a29230.myapplication;

public class Contact {
    private String id;
    private String display_name;
    private String number;

    public Contact(String id,String display_name,String number){
         this.id = id;
         this.display_name = display_name;
         this.number = number;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplay_name(String display_name){
        this.display_name = display_name;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public String getId(){
        return id;
    }

    public String getDisplay_name(){
        return display_name;
    }

    public String getNumber(){
        return number;
    }
}
