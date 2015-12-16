package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.es.spotaneous.objects.Events;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Admin on 10-11-2015.
 */
public class FetchEventsByIDTask extends AsyncTask<String,Void,Events> {

    private Context mContext;

    public FetchEventsByIDTask(Context context){
        mContext = context;
    }

    @Override
    protected Events doInBackground(String... strings) {
        if(strings!=null){
            return null;
        }

        String idEvent = strings[0];
        String urlString =  Server.SERVER_ADDRESS+"/"+Server.SERVER_EVENT+"/"+idEvent;
        Log.d("Fetch Events", urlString);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlString);
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");

        try {
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.e("EVENT ID", statusCode+"");
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                return parseResponse(inputStream);
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return new Events();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Events parseResponse (InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            JSONObject obj = new JSONObject(result);
            JSONArray jsonArray = obj.getJSONArray("results");
            ArrayList<Events> events = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++) {
                events.add(new Events(jsonArray.getJSONObject(i)));
            }
            return events.get(0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Events event) {
        super.onPostExecute(event);
    }
}