package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Admin on 10-11-2015.
 */
public class FetchDeleteUserTask extends AsyncTask<String,Integer,Void> {

    protected StatusLine statusLine;
    private Context mContext;

    public FetchDeleteUserTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (strings==null) {
            return null;
        }

        String idUser = strings[0];
        String urlString = Server.SERVER_ADDRESS+"/"+Server.SERVER_USER+"/"+idUser;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(urlString);
        httpDelete.setHeader("Content-Type", "application/json");
        httpDelete.setHeader("Accept", "application/json");

        try {
//            StringEntity se = new StringEntity(params[0].toString(), HTTP.UTF_8);
//            httppost.setEntity(se);
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpDelete);
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