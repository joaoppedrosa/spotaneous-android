package com.es.spotaneous.objects;

/**
 * Created by JoaoPedro on 03-11-2015.
 */
public class Notification {

    String type;
    String title;
    String subtitle;
    String description;
    String interest;
    String lat;
    String log;
    String cost;
    String begining;
    String user;

    public Notification(String type, String title, String subtitle, String description, String interest, String lat, String log, String cost, String begining, String user) {
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.interest = interest;
        this.lat = lat;
        this.log = log;
        this.cost = cost;
        this.begining = begining;
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getBegining() {
        return begining;
    }

    public void setBegining(String begining) {
        this.begining = begining;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
