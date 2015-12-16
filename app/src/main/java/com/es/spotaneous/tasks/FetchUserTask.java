package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
public class FetchUserTask extends AsyncTask<String,Void,User> {

    private Context mContext;

    public FetchUserTask(Context context){
        mContext = context;
    }

    @Override
    protected User doInBackground(String... strings) {
        String idUser = "";
        if(strings.length!=0){
            idUser = strings[0];
        }
        String urlString =  "http://192.168.8.217:4150/auth/api/users/"+idUser;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlString);
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");

        try {
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.e("USERTASK", statusCode+"");
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                return parseResponse(inputStream);
            }
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return new User();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User parseResponse (InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            JSONObject obj = new JSONObject(result);
            return new User(obj.getJSONObject("user"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);
    }
}