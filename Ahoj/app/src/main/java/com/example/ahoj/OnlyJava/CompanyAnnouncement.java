package com.example.ahoj.OnlyJava;

import java.util.Date;

public class CompanyAnnouncement {
    public String date_and_time;
    public Date duration;
    public String country;

    public CompanyAnnouncement(String date_and_time, Date duration, String country) {
        this.date_and_time = date_and_time;
        this.duration = duration;
        this.country = country;
    }
}
