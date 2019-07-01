package com.example.a29230.myapplication;

import org.litepal.crud.LitePalSupport;

public class Record extends LitePalSupport {

    private int id;
    private String title;
    private String date;
    private String text;
    private String remind_date;
    private String remind_time;
    private String eventID;
    private String is_remind;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRemind_date(String remind_date){
        this.remind_date = remind_date;
    }

    public void setRemind_time(String remind_time){
        this.remind_time = remind_time;
    }

    public void setEventID(String eventID){
        this.eventID = eventID;
    }

    public void setIs_remind(String is_remind){
        this.is_remind = is_remind;
    }

    public int getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDate(){
        return date;
    }

    public String getText() {
        return text;
    }

    public String getRemind_date(){
        return remind_date;
    }

    public String getRemind_time(){
        return remind_time;
    }

    public String getEventID(){
        return eventID;
    }

    public String getIs_remind(){
        return is_remind;
    }
}
