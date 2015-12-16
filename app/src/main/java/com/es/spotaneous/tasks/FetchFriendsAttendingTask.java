package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.es.spotaneous.MainActivity;
import com.es.spotaneous.utils.SharedConfigs;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Admin on 10-11-2015.
 */
public class FetchFriendsAttendingTask extends AsyncTask<String, Void, String> {

    protected StatusLine statusLine;
    private Context mContext;

    public FetchFriendsAttendingTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings == null) {
            return null;
        }

        String idEvent = strings[0];
        String idUser = strings[1];
        // http://192.168.8.217:4188/joinUserToEvent/
        String urlString = Server.SERVER_ADDRESS + "/" + Server.SERVER_FRIENDS_ATTENDING + "/?user_id=" + idUser + "&event_id=" + idEvent;
        Log.d("friendsAttending", urlString);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGost = new HttpGet(urlString);
        httpGost.setHeader("Content-Type", "application/json");
        httpGost.setHeader("Accept", "application/json");

        try {

            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpGost);
            statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            InputStream inputStream = httpResponse.getEntity().getContent();
            if (statusCode == HttpStatus.SC_OK) {
                String result = "";
                String line = "";
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                    Log.d("", line);
                }
                inputStream.close();
                Log.d("friends", "200");
                return result;
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}