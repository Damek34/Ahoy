package com.chatoyment.ahoyapp.OnlyJava;

public class SendEventReportToAhoyModeratorsInfo {
    public String date_and_time;
    public Boolean isSocialMode;
    public String country;

    public SendEventReportToAhoyModeratorsInfo(String date_and_time, Boolean isSocialMode, String country) {
        this.date_and_time = date_and_time;
        this.isSocialMode = isSocialMode;
        this.country = country;
    }
}
