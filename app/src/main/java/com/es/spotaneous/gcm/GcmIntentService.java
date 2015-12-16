package com.es.spotaneous.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.es.spotaneous.EventDescriptionActivity;
import com.es.spotaneous.MainActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.objects.Notification;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by JoaoPedro on 06-10-2015.
 */
public class GcmIntentService extends IntentService {

    private static final String TYPE = "type_of_notify";
    private static final String INFO = "event";
    private static final String EVENT = "event_id";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Log.e("EXTRA", extras.toString().toString());
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        String type = extras.getString(TYPE);
        String json = extras.getString(INFO);
        JSONObject jsonObj = null;
        Events e = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e1) {
            Log.e("NOTIFICATIONS ERROR", "CREATE JSONOBJECT");
            Log.e("NOTIFICATON ERROR", e1.getMessage(),e1);
            e1.printStackTrace();
        }
        try {
            e = new Events(jsonObj);
        } catch (JSONException e1) {
            Log.e("NOTIFICATIONS ERROR", "CREATE EVENTS OBJECT");
            Log.e("NOTIFICATON ERROR", e1.getMessage(),e1);
            e1.printStackTrace();
        }

        String eventid = extras.getString(EVENT);
        String message = "";
        Intent i = null;
        if(type != null){
            if(e!=null){
                switch (type){
                    case "delete":
                        message = "O evento "+e.getTitle()+" foi eliminado pelo seu criador!";
                        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                        } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                            i = new Intent(getApplicationContext(), MainActivity.class);
                            sendNotification(e,message,i);
                        }
                        break;
                    case "create":
                        message = "Evento criado: "+e.getTitle();
                        if (!extras.isEmpty()) {
                            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                                i = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                                sendNotification(e,message,i);
                            }
                        }
                        break;
                    case "join":
                        message = "Alguém se juntou ao evento "+e.getTitle();
                        if (!extras.isEmpty()) {
                            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                                i = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                                sendNotification(e,message,i);
                            }
                        }
                        break;
                    case "update":
                        message = e.getTitle() + " actualizado!";
                        if (!extras.isEmpty()) {
                            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                                i = new Intent(getApplicationContext(), EventDescriptionActivity.class);
                                sendNotification(e,message,i);
                            }
                        }
                        break;
                }
            }
        }
    }

    private void sendNotification(Events event, String message, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent;
        Bundle b = new Bundle();
        b.putString("id", event.getId());
        b.putString("title", event.getTitle());
        b.putString("dateb", event.getDateB());
        b.putInt("cost", event.getCost());
        b.putString("host", event.getHost());
        b.putString("image", event.getImage());
        b.putString("description", event.getDescription());
        b.putString("attending", event.getAddUsers().toString());
        b.putDouble("lat", event.getLatitude());
        b.putDouble("log", event.getLongitude());
        intent.putExtras(b);

        contentIntent = PendingIntent.getActivity(getBaseContext(), currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(event.getTitle())
                .setContentText(message);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static int currentTimeMillis() {
        return (int) System.currentTimeMillis();
    }
}
