package com.es.spotaneous.dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.es.spotaneous.MainActivity;
import com.es.spotaneous.NewEventActivity;
import com.es.spotaneous.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapDialog extends FragmentActivity implements OnMapReadyCallback {

    private static final double LAT = 40.641356;
    private static final double LOG = -8.654369;
    private Button insertlocation;
    private TextView locationpick;
    private GoogleMap map;
    private LatLng latlong = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map_dialog);

        insertlocation = (Button) findViewById(R.id.locationbutton);
        locationpick = (TextView) findViewById(R.id.locationtext);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                locationpick.setText(latLng.latitude+","+latLng.longitude);
                latlong = latLng;
                insertMarker(latLng);
                insertlocation.setEnabled(true);
            }
        });

        insertlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(insertlocation.isEnabled()){
                    if (latlong != null) {
                        pickDone(latlong.latitude, latlong.longitude);
                    } else {
                        Toast.makeText(MapDialog.this, "Escolha uma localização!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MapDialog.this, "Escolha uma localização!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        insertlocation.setEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(LAT, LOG)).zoom(12).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public void insertMarker(LatLng latLng){
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng).title("Localização do evento"));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void pickDone(double lat, double lon){
        Intent i = new Intent(MapDialog.this, NewEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", lat);
        bundle.putDouble("longitude", lon);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }
}
