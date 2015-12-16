package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.es.spotaneous.objects.Events;
import com.es.spotaneous.objects.User;

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
public class FetchUserNearEventTask extends AsyncTask<String,Void,ArrayList<User>> {

    private static final int NUMBER_RESULTS = 5000;
    private Context mContext;

    public FetchUserNearEventTask(Context context){
        mContext = context;
    }

    @Override
    protected ArrayList<User> doInBackground(String... strings) {
        String idEvent = "";
        if(strings.length!=0){
            idEvent = strings[0];
        }

        String urlString =  Server.SERVER_ADDRESS+ "/api/";
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

    private ArrayList<User> parseResponse (InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            JSONObject obj = new JSONObject(result);
            JSONArray jsonArray = obj.getJSONArray("results");
            ArrayList<User> users = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++) {
                users.add(new User(jsonArray.getJSONObject(i)));
            }
            return users;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<User> userArrayList) {
        super.onPostExecute(userArrayList);
    }
}