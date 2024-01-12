package com.example.prochatver1.Models;

import java.util.ArrayList;

public class User_status {
    private String name;
    private long lastUpdate;
    private ArrayList<Status> statuses;
    private String profileImage;

    public User_status(String name, long lastUpdate, ArrayList<Status> statuses, String profileImage) {
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.statuses = statuses;
        this.profileImage = profileImage;
    }
    public User_status(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getProfileImage(){
        return profileImage;
    }
    public void setProfileImage(String profileImage){
        this.profileImage= this.profileImage;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
