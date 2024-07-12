package com.chatoyment.ahoyapp.OnlyJava;

import java.util.Date;

public class AddCompetitionInfo {
    public String date_and_time;
    public String title;
    public String organizer;
    public String organizer_email;
    public String reward;
    public String description;
    public Date duration;
    public String country;
    public String when_results;
    public String who_can_take_part;
    public String where_results;
    public String additional;
    public boolean age_restricted;


    public AddCompetitionInfo(String date_and_time, String title, String organizer, String organizer_email, String reward, String description, Date duration, String country, String when_results, String who_can_take_part, String where_results, String additional, boolean age_restricted) {
        this.date_and_time = date_and_time;
        this.title = title;
        this.organizer = organizer;
        this.organizer_email = organizer_email;
        this.reward = reward;
        this.description = description;
        this.duration = duration;
        this.country = country;
        this.when_results = when_results;
        this.who_can_take_part = who_can_take_part;
        this.where_results = where_results;
        this.additional = additional;
        this.age_restricted = age_restricted;
    }
}
