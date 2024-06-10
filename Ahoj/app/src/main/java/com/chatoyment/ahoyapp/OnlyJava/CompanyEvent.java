package com.chatoyment.ahoyapp.OnlyJava;

import java.util.Date;

public class CompanyEvent {
    public String date_and_time;
    public Date duration;
    public String country;

    public CompanyEvent(String date_and_time, Date duration, String country) {
        this.date_and_time = date_and_time;
        this.duration = duration;
        this.country = country;
    }
}
