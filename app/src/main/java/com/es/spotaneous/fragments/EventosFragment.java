package com.es.spotaneous.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.es.spotaneous.EventDescriptionActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.adapters.ListViewEventsAdapter;
import com.es.spotaneous.dialog.MapDialog;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.tasks.FetchAllEventsTask;
import com.es.spotaneous.utils.SharedConfigs;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ShowHideOnScroll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventosFragment extends Fragment {

    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private ListViewEventsAdapter adapter;
    private Context context;

    private static ArrayList<Events> event_list = null;


    public EventosFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        context = getActivity();
        animatedCircleLoadingView = (AnimatedCircleLoadingView) view.findViewById(R.id.circle_loading_view);
        animatedCircleLoadingView.startIndeterminate();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnTouchListener(new ShowHideOnScroll(fab));


        /**IVO - EXEMPLO PARA RECEBER OS DADOS DA ASYNCKTASK**/

        FetchAllEventsTask fetchAllEventsTask = new FetchAllEventsTask(context) {
            @Override
            protected void onPostExecute(final ArrayList<Events> list) {
                super.onPostExecute(list);
                if (list != null) {
                    event_list = list;
                    Log.d("EVENTS", list.toString());
                    animatedCircleLoadingView.setVisibility(View.GONE);
                    adapter = new ListViewEventsAdapter(context, R.layout.row_layout, list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Events event = list.get(position);
                            Intent intent = new Intent(context, EventDescriptionActivity.class);
                            Bundle b = new Bundle();
                            b.putString("user_id", SharedConfigs.getStringValue("iduser", getContext()));
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
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        if (event_list == null) {
            fetchAllEventsTask.execute(SharedConfigs.getStringValue("iduser", getContext()));
        } else {
            animatedCircleLoadingView.setVisibility(View.GONE);
            adapter = new ListViewEventsAdapter(context, R.layout.row_layout, event_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Events event = event_list.get(position);
                    Intent intent = new Intent(context, EventDescriptionActivity.class);
                    Bundle b = new Bundle();
                    b.putString("user_id", SharedConfigs.getStringValue("iduser", getContext()));
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
                    startActivity(intent);
                }
            });


        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapDialog.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public static void outdatedEventData(){
        event_list = null;
    }
}
