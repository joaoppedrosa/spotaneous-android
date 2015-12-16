package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Admin on 10-11-2015.
 */
public class FetchCreateEventTask extends AsyncTask<JSONObject,Integer,Void> {

    protected StatusLine statusLine;
    private Context mContext;

    public FetchCreateEventTask (Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(JSONObject... params) {
        if (params==null) {
            return null;
        }
        String urlString = Server.SERVER_ADDRESS+"/"+Server.SERVER_EVENT_POST+"/";
        Log.d("urlString", urlString);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlString);
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Accept", "application/json");

        try {
            StringEntity se = new StringEntity(params[0].toString(), HTTP.UTF_8);
            httppost.setEntity(se);
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
            statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            InputStream inputStream = httpResponse.getEntity().getContent();
            if (statusCode == HttpStatus.SC_OK) {
            }
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            }
            else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}