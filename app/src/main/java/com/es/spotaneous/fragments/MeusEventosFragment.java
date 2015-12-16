package com.es.spotaneous.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.es.spotaneous.EventDescriptionActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.adapters.ListViewEventsAdapter;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.tasks.FetchEventUserHostsTask;
import com.es.spotaneous.tasks.FetchEventsUserAttendingTask;
import com.es.spotaneous.utils.SharedConfigs;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MeusEventosFragment extends Fragment {

    private ListViewEventsAdapter adapter;
    private Context context;

    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private Button criados;
    private Button aderidos;

    private static char list_type;

    private static ArrayList<Events> attending_list = null;
    private static ArrayList<Events> host_list = null;

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_meus_eventos, container, false);
        context = getContext();

        listView = (ListView) view.findViewById(R.id.listView);


        list_type = 'c';


        animatedCircleLoadingView = (AnimatedCircleLoadingView) view.findViewById(R.id.circle_loading_view);

        //Log.e("attending_list", attending_list.toString());
        //Log.e("host_list", host_list.toString());

        if (attending_list == null || host_list == null) {
            animatedCircleLoadingView.startIndeterminate();
        }

        criados = (Button) view.findViewById(R.id.criados);
        aderidos = (Button) view.findViewById(R.id.aderidos);

        populateList();


        criados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_type = 'c';

                aderidos.setBackgroundColor(getActivity().getResources().getColor(R.color.logo_color_2));
                criados.setBackgroundColor(getActivity().getResources().getColor(R.color.new_red_color));

                populateList();
            }
        });

        aderidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_type = 'a';

                criados.setBackgroundColor(getActivity().getResources().getColor(R.color.logo_color_2));
                aderidos.setBackgroundColor(getActivity().getResources().getColor(R.color.new_red_color));

                populateList();

            }
        });

        return view;
    }


    public void populateList() {
        Log.e("LISTTYPE", "" + list_type);

        FetchEventsUserAttendingTask fetchEventsUserAttendingTask = new FetchEventsUserAttendingTask(context) {
            @Override
            protected void onPostExecute(final ArrayList<Events> list) {
                super.onPostExecute(list);
                if (list != null) {
                    attending_list = list;
                    if (list_type == 'a') {
                        animatedCircleLoadingView.stopOk();
                        adapter = new ListViewEventsAdapter(context, R.layout.row_layout, attending_list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Events event = attending_list.get(position);
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
                        animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    animatedCircleLoadingView.stopFailure();
                }
            }
        };

        FetchEventUserHostsTask fetchEventsUserHostsTask = new FetchEventUserHostsTask(context) {
            @Override
            protected void onPostExecute(final ArrayList<Events> list) {
                super.onPostExecute(list);
                if (list != null) {
                    host_list = list;
                    if (list_type == 'c') {
                        animatedCircleLoadingView.stopOk();
                        adapter = new ListViewEventsAdapter(context, R.layout.row_layout, host_list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Events event = host_list.get(position);
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
                        animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    animatedCircleLoadingView.stopFailure();
                }

            }
        };

        if (host_list != null)
            Log.e("HostList", host_list.toString());
        else
            Log.e("HostList", "null");
        if (attending_list != null)
            Log.e("AttendingList", attending_list.toString());
        else
            Log.e("AttendingList", "null");


        if (host_list == null || attending_list == null) {
            fetchEventsUserHostsTask.execute(SharedConfigs.getStringValue("iduser", getContext()));
            fetchEventsUserAttendingTask.execute(SharedConfigs.getStringValue("iduser", getContext()));
            return;
        }

        if (list_type == 'c') {
            adapter = new ListViewEventsAdapter(context, R.layout.row_layout, host_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Events event = host_list.get(position);
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
        } else if (list_type == 'a') {
            adapter = new ListViewEventsAdapter(context, R.layout.row_layout, attending_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Events event = attending_list.get(position);
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


    public static void outdatedEventData(){
        attending_list = null;
        host_list = null;
    }
}
