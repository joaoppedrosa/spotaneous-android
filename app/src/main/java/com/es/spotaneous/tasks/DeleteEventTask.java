
/**
 * Created by ivosilva on 28/11/15.
 */

package com.es.spotaneous.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.es.spotaneous.MainActivity;
import com.es.spotaneous.utils.SharedConfigs;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
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
public class DeleteEventTask extends AsyncTask<String, Void, Void> {

    protected StatusLine statusLine;
    private Context mContext;

    public DeleteEventTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (strings == null) {
            return null;
        }

        String idEvent = strings[0];
        String idUser = strings[1];
        // http://192.168.8.217:4188/joinUserToEvent/
        String urlString = Server.SERVER_ADDRESS + "/" + Server.SERVER_EVENT_DELETE + "/?event_id=" + idEvent;
        Log.d("DeleteUserEvent", urlString);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(urlString);
        httpDelete.setHeader("Content-Type", "application/json");
        httpDelete.setHeader("Accept", "application/json");

        try {

            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httpDelete);
            statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            InputStream inputStream = httpResponse.getEntity().getContent();
            if (statusCode == HttpStatus.SC_OK) {
                Log.d("DeleteUserToEvent", "200");
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}