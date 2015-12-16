package com.es.spotaneous.radar;

/**
 * Created by Admin on 19-11-2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.es.spotaneous.MainActivity;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.tasks.FetchAllEventsTask;
import com.es.spotaneous.utils.SharedConfigs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CustomWorldHelper {

    private static boolean haveLocation = false;
    private static World sharedWorld;
    private static ArrayList<Events> eventsArrayList = null;
    private static List<GenRadarPoint> genRadarPoints = new ArrayList<>();

    private static double latitude = 0;
    private static double longitude = 0;


    public static void getValuesToRadar(double lat, double lng, final Context context){
        latitude = lat;
        longitude = lng;

        FetchAllEventsTask fetchAllEventsTask = new FetchAllEventsTask(context) {
            @Override
            protected void onPostExecute(final ArrayList<Events> list) {
                super.onPostExecute(list);
                if(list!=null){
                    if(!list.isEmpty()){
                        eventsArrayList = list;
                        generateObjects(context);
                    }
                }
            }
        };
        fetchAllEventsTask.execute(SharedConfigs.getStringValue("iduser", context));
    }


    public static World generateObjects(Context context) {
        if (sharedWorld != null) {
            return sharedWorld;
        }
        // Default value of radius for a Radar Point
        float radius = 3.2f;
        sharedWorld = new World(context);
        sharedWorld.setGeoPosition(latitude,longitude);

        if(eventsArrayList!=null){
            for (Events poi : eventsArrayList) {
                GeoObject go = new GeoObject(Long.valueOf(poi.getId()));
                go.setGeoPosition(poi.getLatitude(), poi.getLongitude());
                go.setName(poi.getTitle());
                go.faceToCamera(true);
                go.setDistanceFromUser(Double.valueOf(poi.getDistance()));
                genRadarPoints.add(new GenRadarPoint(poi.getId(), poi.getLatitude(), poi.getLongitude(), 0, 0, radius, Color.BLUE));
                sharedWorld.addBeyondarObject(go);
            }
        }
        haveLocation = true;
        setSharedWorld(sharedWorld);
        return sharedWorld;
    }


    public static boolean isHaveLocation() {
        return haveLocation;
    }

    public static World getSharedWorld() {
        if(sharedWorld==null){
            return null;
        }
        return sharedWorld;
    }

    public static void setSharedWorld(World sharedWorld) {
        CustomWorldHelper.sharedWorld = sharedWorld;
    }

    public static ArrayList<Events> getEventsArrayList() {
        return eventsArrayList;
    }

    public static void setEventsArrayList(ArrayList<Events> eventsArrayList) {
        CustomWorldHelper.eventsArrayList = eventsArrayList;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        CustomWorldHelper.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        CustomWorldHelper.longitude = longitude;
    }

    public static List<GenRadarPoint> getGenRadarPoints() {
        return genRadarPoints;
    }

    public static void setGenRadarPoints(List<GenRadarPoint> genRadarPoints) {
        CustomWorldHelper.genRadarPoints = genRadarPoints;
    }

    private static float getDistanceBetweenPoints(double lat, double lng){
        Location locationA = new Location("point A");
        locationA.setLatitude(lat);
        locationA.setLongitude(lng);
        Location locationB = new Location("point B");
        locationB.setLatitude(latitude);
        locationB.setLongitude(longitude);
        float distance = locationA.distanceTo(locationB);
        return distance;
    }

    static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
