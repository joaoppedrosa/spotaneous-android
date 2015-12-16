package com.es.spotaneous.utils;

/**
 * Created by JoaoPedro on 01-11-2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class InternetConnectionReceiver extends BroadcastReceiver {
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;
    private static LinkedBlockingQueue<Runnable> runnableList = new LinkedBlockingQueue<>();
    private static List<Listener> listenerList = new ArrayList<>();
    private static boolean hasInternet;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return NETWORK_STATUS_WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return NETWORK_STATUS_MOBILE;
        }
        return NETWORK_STATUS_NOT_CONNECTED;
    }

    public static boolean hasInternetConnection() {
        return hasInternet;
    }

    public static void executeWithInternetConnection(Runnable runnable) {
        if (hasInternet) {
            runnable.run();
        } else {
            runnableList.add(runnable);
        }
    }

    public static void addInternetConnectionListener(Context context, Listener listener) {
        if (listener == null) {
            return;
        }
        listenerList.add(listener);
        if (getConnectivityStatus(context) == NETWORK_STATUS_NOT_CONNECTED) {
            listener.onNetworkDisconnected();
        } else {
            listener.onNetworkConnected();
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = getConnectivityStatus(context);
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (status == NETWORK_STATUS_NOT_CONNECTED) {
                // not connected
                hasInternet = false;
                LinkedList<Listener> aux = new LinkedList<>(listenerList);
                while (!aux.isEmpty()) {
                    aux.poll().onNetworkDisconnected();
                }
            } else {
                // connected
                hasInternet = true;
                while (!runnableList.isEmpty()) {
                    runnableList.poll().run();
                }
                LinkedList<Listener> aux = new LinkedList<>(listenerList);
                while (!aux.isEmpty()) {
                    aux.poll().onNetworkConnected();
                }
            }
        }
    }

    public static interface Listener {
        void onNetworkConnected();
        void onNetworkDisconnected();
    }
}