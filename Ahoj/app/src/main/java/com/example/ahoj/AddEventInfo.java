package com.example.ahoj;

public class AddEventInfo {
    public String time_and_date;
    public String event_name;
    public String event_description;
    public String event_localization;
    public String event_company_name;


    public AddEventInfo(String time_and_date, String event_name, String event_description, String event_localization, String event_company_name) {
        this.time_and_date = time_and_date;
        this.event_name = event_name;
        this.event_description = event_description;
        this.event_localization = event_localization;
        this.event_company_name = event_company_name;
    }
}
