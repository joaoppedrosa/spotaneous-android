package com.es.spotaneous.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.es.spotaneous.EventDescriptionActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.adapters.ListViewEventsAdapter;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.radar.CustomWorldHelper;
import com.es.spotaneous.tasks.FetchAllEventsTask;
import com.es.spotaneous.utils.SharedConfigs;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapaFragment extends Fragment{

    private static final double LAT = 40.641356;
    private static final double LOG = -8.654369;

    private ArrayList<Events> eventsArrayList;
    private ProgressBar progress;
    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);

        progress = (ProgressBar) v.findViewById(R.id.progress);
        progress.setIndeterminate(true);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(LAT, LOG)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        addMarkers();
        // Perform any camera updates here
        return v;
    }

    private void addMarkers(){
        ArrayList<Events> events = CustomWorldHelper.getEventsArrayList();
        if(events!=null){
            insert(events);
        }else{
            FetchAllEventsTask fetchAllEventsTask = new FetchAllEventsTask(getActivity()) {
                @Override
                protected void onPostExecute(final ArrayList<Events> list) {
                    super.onPostExecute(list);
                    insert(list);
                }
            };
            fetchAllEventsTask.execute(SharedConfigs.getStringValue("iduser", getContext()));
        }
    }

    private void insert(ArrayList<Events> list){
        eventsArrayList = list;
        for (Events e : list) {
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(e.getLatitude(), e.getLongitude())).title(e.getTitle()).snippet(e.getDescription());
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            googleMap.addMarker(marker);
        }
        progress.setVisibility(View.GONE);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (Events event : eventsArrayList) {
                    if(marker.getSnippet().equals(event.getDescription())){
                        Intent intent = new Intent(getActivity(), EventDescriptionActivity.class);
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
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
