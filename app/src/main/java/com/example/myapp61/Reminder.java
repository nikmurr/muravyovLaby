package com.example.myapp61;

import java.time.ZonedDateTime;

public class Reminder {
    private int id;
    private String title;
    private String text;
    private ZonedDateTime zonedDateTime ;

    public Reminder(int id, String title, String text, ZonedDateTime zonedDateTime) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.zonedDateTime = zonedDateTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ZonedDateTime getReminderDate() {
        return zonedDateTime;
    }

    public void setReminderDate(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
