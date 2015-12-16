package com.es.spotaneous.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JoaoPedro on 29-09-2015.
 */
public class Events {

    private String id;
    private String title;
    private String subtitle;
    private String description;
    private String smallDescription;
    private String image;
    private String dateB;
    private String dateE;
    private String interest;
    private ArrayList<String> addUsers;
    private double latitude;
    private double longitude;
    private int cost;
    private int min_attending;
    private int max_attending;
    private boolean isPrivate;
    private String distance;
    private String host;

    public Events(){}

    public Events(String title,String dateB, int cost, String image){
        this.title = title;
        this.dateB= dateB;
        this.cost = cost;
        this.image = image;
    }

    public Events(JSONObject obj) throws JSONException {
        this.id = obj.getString("id");
        this.title = obj.getString("title");
        this.latitude = obj.getJSONObject("location").getJSONArray("coordinates").getDouble(1);
        this.longitude = obj.getJSONObject("location").getJSONArray("coordinates").getDouble(0);
        //this.subtitle = obj.getString("subtitle");
        this.description = obj.getString("description");
        //this.smallDescription = obj.getString("small_description");
        this.dateB = obj.getString("beginning");
        this.distance = obj.getString("distance");
        this.host = obj.getString("host");
        this.image = obj.getString("image");
        //this.dateE = obj.getString("end");
        this.cost = obj.getInt("cost");
        //this.isPrivate = obj.getBoolean("type");
        //this.max_attending = obj.getInt("max_people");
        this.min_attending = obj.getInt("min_people");
        this.interest = obj.getString("interest");


        JSONArray jsonArray = obj.getJSONArray("attending");
        this.addUsers = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            this.addUsers.add(Integer.toString(jsonArray.getJSONObject(i).getInt("id")));
        }
        Log.d("addUsers", this.addUsers.toString());
    }

    public Events(String title, String subtitle, String description, String smallDescription, String image, String dateB, String dateE, String interest, ArrayList<String> addUsers, double latitude, double longitude, int cost, int min_attending, int max_attending, boolean isPrivate) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.smallDescription = smallDescription;
        this.image = image;
        this.dateB = dateB;
        this.dateE = dateE;
        this.interest = interest;
        this.addUsers = addUsers;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cost = cost;
        this.min_attending = min_attending;
        this.max_attending = max_attending;
        this.isPrivate = isPrivate;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getSmallDescription() {
        return smallDescription;
    }

    public void setSmallDescription(String smallDescription) {
        this.smallDescription = smallDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDateB() {
        return dateB;
    }

    public void setDateB(String dateB) {
        this.dateB = dateB;
    }

    public String getDateE() {
        return dateE;
    }

    public void setDateE(String dateE) {
        this.dateE = dateE;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public ArrayList<String> getAddUsers() {
        Log.d("attending", this.addUsers.toString());
        return this.addUsers;
    }

    public void setAddUsers(ArrayList<String> addUsers) {
        this.addUsers = addUsers;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getMin_attending() {
        return min_attending;
    }

    public void setMin_attending(int min_attending) {
        this.min_attending = min_attending;
    }

    public int getMax_attending() {
        return max_attending;
    }

    public void setMax_attending(int max_attending) {
        this.max_attending = max_attending;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
