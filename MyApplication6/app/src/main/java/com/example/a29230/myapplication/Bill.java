package com.example.a29230.myapplication;

import org.litepal.crud.LitePalSupport;

public class Bill extends LitePalSupport {

    private int id;
    private String cost;
    private String date;
    private String describe;

    public void setId(int id){
        this.id = id;
    }

    public void setCost(String cost){
        this.cost = cost;
    }

    public void  setDate(String date){
        this.date = date;
    }

    public void setDescribe(String describe){
        this.describe =describe;
    }

    public int getId(){
        return id;
    }

    public String getCost(){
        return cost;
    }

    public String getDate(){
        return date;
    }

    public String getDescribe(){
        return describe;
    }
}
