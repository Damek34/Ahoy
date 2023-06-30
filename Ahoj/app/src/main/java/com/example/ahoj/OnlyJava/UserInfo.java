package com.example.ahoj.OnlyJava;

import java.util.Date;

public class UserInfo {
    public String nick;
    public String email;
    public int points;

    String last_login;

    public UserInfo(String nick, String email, int points, String last_login) {
        this.nick = nick;
        this.email = email;
        this.points = points;
        this.last_login = last_login;
    }
}
