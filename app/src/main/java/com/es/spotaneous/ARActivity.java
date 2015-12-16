package com.es.spotaneous;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.opengl.util.LowPassFilter;
import com.beyondar.android.util.ImageUtils;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.BeyondarObjectList;
import com.beyondar.android.world.World;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.radar.CustomWorldHelper;
import com.es.spotaneous.radar.GenRadarManager;
import com.es.spotaneous.radar.GenRadarPoint;
import com.es.spotaneous.utils.SharedConfigs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ARActivity extends FragmentActivity implements OnClickBeyondarObjectListener {

    private static final String TMP_IMAGE_PREFIX = "viewImage_";
    private static final int SEARCH_DISTANCE = 1000;

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;

    //GenRadar API
    private List<GenRadarPoint> genRadarPoints;
    private GenRadarManager mGenRadarManager;
    private List<GenRadarPoint> mGenRadarPoints;
    private GenRadarPoint mCentralGenRadarPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cleanTempFolder();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ar);

        mBeyondarFragment = (BeyondarFragmentSupport) getFragmentManager().findFragmentById(R.id.beyondarFragment);
        mWorld = CustomWorldHelper.getSharedWorld();

        if(mWorld!=null){
            if(mWorld.getBeyondarObjectLists().get(0).size()<=1){
                new AlertDialog.Builder(this)
                        .setTitle("Eventos")
                        .setMessage("Nenhum evento encontrado")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.icon)
                        .show();
            }
        }

        initGenRadar();
        mBeyondarFragment.setPullCloserDistance(SEARCH_DISTANCE);
        mBeyondarFragment.setPushAwayDistance(20);
        mBeyondarFragment.setMaxDistanceToRender(SEARCH_DISTANCE);
        mBeyondarFragment.setDistanceFactor(SEARCH_DISTANCE);
        mBeyondarFragment.setSensorDelay(SensorManager.SENSOR_DELAY_NORMAL);

        mBeyondarFragment.setWorld(mWorld);

        replaceImagesByStaticViews(mWorld);
        printAll();
        mBeyondarFragment.setOnClickBeyondarObjectListener(ARActivity.this);

        LowPassFilter.ALPHA = 0.045f;
    }

    /**Init for the Radar View**/
    private void initGenRadar() {
        // Generate the Radar Points
        mGenRadarPoints = generateGenRadarPoints();
        // Create the central Radar Point which will be used by GenRadar API
        mCentralGenRadarPoint = new GenRadarPoint("Center Point", mWorld.getLatitude(), mWorld.getLongitude(), 0, 0, 3.2f, Color.RED);
        // Initialize the GenRadarManager
        // The last two params are same as declared width and height of container and top parent layout in xml layout file
        mGenRadarManager = new GenRadarManager(this, (LinearLayout) findViewById(R.id.container), 80, 80);
        // Let the GenRadarManager to do the radar-initialization process
        mGenRadarManager.initAndUpdateRadarWithPoints(mCentralGenRadarPoint, mGenRadarPoints);

    }

    private List<GenRadarPoint> generateGenRadarPoints(){
        genRadarPoints = CustomWorldHelper.getGenRadarPoints();
        return genRadarPoints;
    }

    private void printAll(){
        for (BeyondarObjectList beyondarList : mWorld.getBeyondarObjectLists()) {
            for (BeyondarObject beyondarObject : beyondarList) {
                Log.e("OBJ", beyondarObject.getName() + " - " + roundeDistance(beyondarObject.getDistanceFromUser()) + " metros");
            }
        }
    }

    /**Replace marker for view in the camera view**/
    private void replaceImagesByStaticViews(World world) {
        String path = getTmpPath();

        for (BeyondarObjectList beyondarList : world.getBeyondarObjectLists()) {
            for (BeyondarObject beyondarObject : beyondarList) {
                View view = getLayoutInflater().inflate(R.layout.ar_object, null);
                TextView textView = (TextView) view.findViewById(R.id.geoObjectName);
                TextView textDistance = (TextView) view.findViewById(R.id.distance);
                textView.setText(beyondarObject.getName());


                float distance = 0;
                for (GenRadarPoint g : genRadarPoints) {
                    if(g.getLocationName().equals(beyondarObject.getName())){
                        distance = getDistanceBetweenPoints(g.getLat(),g.getLng());
                    }
                }
                textDistance.setText(roundeDistance(beyondarObject.getDistanceFromUser()) + " m");
                try {
                    String imageName = TMP_IMAGE_PREFIX + beyondarObject.getName() + ".png";
                    ImageUtils.storeView(view, path, imageName);

                    beyondarObject.setImageUri(path + imageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the path to store temporally the images. Remember that you need to
     * set WRITE_EXTERNAL_STORAGE permission in your manifest in order to
     * write/read the storage
     */
    private String getTmpPath() {
        return getExternalFilesDir(null).getAbsoluteFile() + "/tmp/";
    }

    /** Clean all the generated files */
    private void cleanTempFolder() {
        File tmpFolder = new File(getTmpPath());
        if (tmpFolder.isDirectory()) {
            String[] children = tmpFolder.list();
            for (int i = 0; i < children.length; i++) {
                if (children[i].startsWith(TMP_IMAGE_PREFIX)) {
                    new File(tmpFolder, children[i]).delete();
                }
            }
        }
    }

    private float getDistanceBetweenPoints(double lat, double lng){
        Location locationA = new Location("point A");
        locationA.setLatitude(lat);
        locationA.setLongitude(lng);
        Location locationB = new Location("point B");
        locationB.setLatitude(mWorld.getLatitude());
        locationB.setLongitude(mWorld.getLongitude());
        float distance = locationA.distanceTo(locationB);
        return distance;
    }

    private double roundeDistance(double number){
        number = Math.round(number * 100);
        number = number/100;
        return number;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGenRadarManager != null){
            mGenRadarManager.registerListeners();
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> arrayList) {
        if(haveNetworkConnection()){
            if(!arrayList.isEmpty()){
                String id = String.valueOf(arrayList.get(0).getId());
                ArrayList<Events> eventses = CustomWorldHelper.getEventsArrayList();
                for (Events event : eventses) {
                    if(event.getId().equals(id)){
                        Intent intent = new Intent(ARActivity.this, EventDescriptionActivity.class);
                        Bundle b = new Bundle();
                        b.putString("user_id", SharedConfigs.getStringValue("iduser", getApplicationContext()));
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
        }
    }
}
