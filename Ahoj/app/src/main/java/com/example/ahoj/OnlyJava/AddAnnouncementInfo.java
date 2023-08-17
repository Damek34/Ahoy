package com.example.ahoj.OnlyJava;

import java.util.Date;

public class AddAnnouncementInfo {
    public String time_and_date;
    public String CompanyName;
    public String announcement_description;
    public Date announcement_duration;
    public String announcement_additional;
    public String countryName;
    public String organizer;

    public AddAnnouncementInfo(String time_and_date, String companyName, String announcement_description, Date announcement_duration, String announcement_additional, String countryName, String organizer) {
        this.time_and_date = time_and_date;
        CompanyName = companyName;
        this.announcement_description = announcement_description;
        this.announcement_duration = announcement_duration;
        this.announcement_additional = announcement_additional;
        this.countryName = countryName;
        this.organizer = organizer;
    }
}
