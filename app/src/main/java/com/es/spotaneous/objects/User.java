package com.es.spotaneous.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 10-11-2015.
 */
public class User {

    String email;
    String id;
    String name;
    String photo;
    String regID;

    public User(){}

    public User(String email, String id, String name, String photo, String regID) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.regID = regID;
    }

    public User(JSONObject obj) throws JSONException {
        this.email = obj.getString("email");
        this.id = obj.getString("id");
        this.name = obj.getString("name");
        this.photo = obj.getString("photo");
        this.regID = obj.getString("regID");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }
}
