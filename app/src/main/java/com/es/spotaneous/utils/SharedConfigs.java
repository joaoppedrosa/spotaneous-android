package com.es.spotaneous.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JoaoPedro on 27-10-2015.
 */
public class SharedConfigs {

    private final static String PREFS_NAME = "spotaneous_preferences";

    public static void addStringValue(String key, String value, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringValue(String key, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, "");
    }

    public static void addBooleanValue(String key, boolean value, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanValue(String key, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, false);
    }

    public static void addIntegerValue(String key, int value, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getIntegerValue(String key, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, 0);
    }

    public static void addList(String key, ArrayList<String> value, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonText = gson.toJson(value);
        editor.putString(key, jsonText);
        editor.commit();
    }

    public static ArrayList<String> getList(String key, Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String jsonText = settings.getString(key, null);
        String[] text = gson.fromJson(jsonText, String[].class);
        if(text == null){
            return  new ArrayList<>();
        }
        else{
            return new ArrayList<>(Arrays.asList(text));
        }
    }
}
