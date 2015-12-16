package com.es.spotaneous.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.es.spotaneous.objects.Events;

import org.apache.http.Header;
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
public class FetchAllEventsTask extends AsyncTask<String,Void,ArrayList<Events>> {

    private static final int NUMBER_RESULTS = 5000;
    private Context mContext;

    public FetchAllEventsTask (Context context){
        mContext = context;
    }

    @Override
    protected ArrayList<Events> doInBackground(String... strings) {
        String urlString =  Server.SERVER_ADDRESS+ "/"+Server.SERVER_EVENT+ "/?user_id=" + strings[0];
        Log.d("FetchAllEventsTask", urlString);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlString);
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");

        try {
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                return parseResponse(inputStream);
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return new ArrayList<>();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Events> parseResponse (InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = bufferedReader.readLine()) != null){
                result += line;
                Log.d("", line);
            }
            inputStream.close();

            Log.d("Result", result);
            JSONObject obj = new JSONObject(result);
            JSONObject info = obj.getJSONObject("info");
            JSONArray jsonArray = info.getJSONArray("results");
            ArrayList<Events> events = new ArrayList<>();


            for (int i=0;i<jsonArray.length();i++) {
                events.add(new Events(jsonArray.getJSONObject(i)));
            }
            return events;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Events> eventsArrayList) {
        super.onPostExecute(eventsArrayList);
    }
}