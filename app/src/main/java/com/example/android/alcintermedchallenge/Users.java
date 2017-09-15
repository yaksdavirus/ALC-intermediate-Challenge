package com.example.android.alcintermedchallenge;

/**
 * Created by yaksdavirus on 13-Sep-17.
 */
public class Users {
    public String avatar_url;
    public String login;
    public String html_url;

    public Users(String serverProfilePicUrl, String userName, String dUrl) {
        this.avatar_url = serverProfilePicUrl;
        this.login = userName;
        this.html_url = dUrl;
    }
}