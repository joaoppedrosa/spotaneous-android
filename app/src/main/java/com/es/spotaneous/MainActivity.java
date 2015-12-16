package com.es.spotaneous;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.es.spotaneous.adapters.ListViewEventsAdapter;
import com.es.spotaneous.dialog.MapDialog;
import com.es.spotaneous.fragments.ChooseViewFragment;
import com.es.spotaneous.fragments.EventosFragment;
import com.es.spotaneous.fragments.MapaFragment;
import com.es.spotaneous.fragments.MeusEventosFragment;
import com.es.spotaneous.fragments.ProfileFragment;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.radar.CustomWorldHelper;
import com.es.spotaneous.tasks.FetchAllEventsTask;
import com.es.spotaneous.tasks.FetchLocationTask;
import com.es.spotaneous.tasks.FetchReIDTask;
import com.es.spotaneous.utils.BestLocationListener;
import com.es.spotaneous.utils.BestLocationProvider;
import com.es.spotaneous.utils.InternetConnectionReceiver;
import com.es.spotaneous.utils.SharedConfigs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends FragmentActivity{

    /**TABS VIEWPAGER**/
    private static final int EVENTOS_TAB = 0;
    private static final int MEUS_EVENTOS_TAB = 1;
    private static final int MAPA_TAB = 2;
    private static final int PERFIL_TAB = 3;

    private static final String TAG = "MainActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "1024798166199";
    private static final String REDIG = "regid_in_server";

    private Button buttonEvents;
    private Button buttonMeusEvents;
    private Button buttonMap;
    private Button buttonProfile;
    private ViewPager viewPager;
    private MyPagerAdapter adapterViewPager;
    private Fragment currentFragment;
    private GoogleCloudMessaging gcm;
    private String regid;
    private FrameLayout container;
    private LinearLayout bottomactionbar;
    private Menu menu;
    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;
    private boolean haveLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InternetConnectionReceiver.addInternetConnectionListener(this, new InternetConnectionReceiver.Listener() {
            @Override
            public void onNetworkConnected() {
                GCMConfiguration();
                initViews();
                showViewPager();
            }

            @Override
            public void onNetworkDisconnected() {
                internetStateDialog();
            }
        });
    }

    private void internetStateDialog(){
        MaterialDialog mMaterialDialog = new MaterialDialog(this)
                .setTitle("Ligação à internet")
                .setCanceledOnTouchOutside(false)
                .setMessage("Ligue-se à internet para conseguir usufruir da aplicação!");
        mMaterialDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            createNewEvents();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createNewEvents(){
        Intent intent = new Intent(this, MapDialog.class);
        startActivity(intent);
//        showFragment(new NewEventFragment());
    }

    public void onEventosClick(View view) {
        viewPager.setCurrentItem(EVENTOS_TAB);
        currentFragment = adapterViewPager.getItem(EVENTOS_TAB);
        buttonEvents.setBackgroundColor(getResources().getColor(R.color.new_red_color));
        removeStyleButton(buttonMap);
        removeStyleButton(buttonMeusEvents);
        removeStyleButton(buttonProfile);
    }

    public void onMeusEventosClick(View view) {
        viewPager.setCurrentItem(MEUS_EVENTOS_TAB);
        currentFragment = adapterViewPager.getItem(MEUS_EVENTOS_TAB);
        buttonMeusEvents.setBackgroundColor(getResources().getColor(R.color.new_red_color));
        removeStyleButton(buttonEvents);
        removeStyleButton(buttonMap);
        removeStyleButton(buttonProfile);
    }

    public void onMapaClick(View view) {
        viewPager.setCurrentItem(MAPA_TAB);
        currentFragment = adapterViewPager.getItem(MAPA_TAB);
        buttonMap.setBackgroundColor(getResources().getColor(R.color.new_red_color));
        removeStyleButton(buttonEvents);
        removeStyleButton(buttonMeusEvents);
        removeStyleButton(buttonProfile);
    }

    public void onProfileClick(View view) {
        viewPager.setCurrentItem(PERFIL_TAB);
        currentFragment = adapterViewPager.getItem(PERFIL_TAB);
        buttonProfile.setBackgroundColor(getResources().getColor(R.color.new_red_color));
        removeStyleButton(buttonMap);
        removeStyleButton(buttonMeusEvents);
        removeStyleButton(buttonEvents);
    }

    /**Remove the styles to the buttons on the bottom menu**/
    public void removeStyleButton(Button b) {
        b.setBackgroundColor(Color.parseColor("#455A64"));
    }


    /**
     * Show the correct fragment in the container
     **/
    public void showFragment(Fragment f) {
        showContainer();
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, f);

        if (currentFragment != null) {
            ft.addToBackStack(currentFragment.getTag());
        }
        ft.commit();
        currentFragment = f;
    }

    public void showContainer(){
        container.setVisibility(View.VISIBLE);
        bottomactionbar.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        if(menu!=null) {
            MenuItem item = menu.findItem(R.id.action_add);
            item.setVisible(false);
        }
    }

    public void showViewPager(){
        container.setVisibility(View.GONE);
        bottomactionbar.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        if(menu!=null){
            MenuItem item = menu.findItem(R.id.action_add);
            item.setVisible(true);
        }
    }

    private void restyleTabsForFragment(Fragment f) {
        if (f instanceof EventosFragment) {
            buttonEvents.setBackgroundColor(getResources().getColor(R.color.new_red_color));
            removeStyleButton(buttonMap);
            removeStyleButton(buttonMeusEvents);
            removeStyleButton(buttonProfile);
        }else if (f instanceof MeusEventosFragment){
            viewPager.setCurrentItem(MEUS_EVENTOS_TAB);
            currentFragment = adapterViewPager.getItem(MEUS_EVENTOS_TAB);
            buttonMeusEvents.setBackgroundColor(getResources().getColor(R.color.new_red_color));
            removeStyleButton(buttonEvents);
            removeStyleButton(buttonMap);
            removeStyleButton(buttonProfile);
        }else if (f instanceof ChooseViewFragment){
            viewPager.setCurrentItem(MAPA_TAB);
            currentFragment = adapterViewPager.getItem(MAPA_TAB);
            buttonMap.setBackgroundColor(getResources().getColor(R.color.new_red_color));
            removeStyleButton(buttonEvents);
            removeStyleButton(buttonMeusEvents);
            removeStyleButton(buttonProfile);
        }else if (f instanceof ProfileFragment){
            viewPager.setCurrentItem(PERFIL_TAB);
            currentFragment = adapterViewPager.getItem(PERFIL_TAB);
            buttonProfile.setBackgroundColor(getResources().getColor(R.color.new_red_color));
            removeStyleButton(buttonMap);
            removeStyleButton(buttonMeusEvents);
            removeStyleButton(buttonEvents);
        }
    }

    /**
     * Check connection state
     */
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


    /**
     * Check the state of GooglePlay Services and get de registation id
     */
    public void GCMConfiguration() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            getRegId();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    public static String getDeviceIDD(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }


    /**
     * Get registration id from the device
     */
    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    setRegid(regid);
                    SharedConfigs.addStringValue("regid", regid, MainActivity.this);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    SharedConfigs.addStringValue("regid", "null", MainActivity.this);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("MESSAGE GCM", msg + "");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                if(!prefs.getString("regid", "").equals("")){
                    if(!prefs.getString("regid", "").equals("null")){
                        JSONObject jObj = new JSONObject();
                        try {
                            jObj.accumulate("id", SharedConfigs.getStringValue("iduser", MainActivity.this));
                            jObj.accumulate("regID", prefs.getString("regid", ""));
                            Log.e("JSON", jObj.toString());
                            FetchReIDTask fetchReIDTask = new FetchReIDTask(MainActivity.this);
                            fetchReIDTask.execute(jObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.execute(null, null, null);
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    /**Adapter to the ViewPager*/
    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case EVENTOS_TAB:
                    return new EventosFragment();
                case MEUS_EVENTOS_TAB:
                    return new MeusEventosFragment();
                case MAPA_TAB:
                    return new ChooseViewFragment();
                case PERFIL_TAB:
                    return new ProfileFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    private void initViews(){
        buttonEvents = (Button) findViewById(R.id.buttonEventos);
        buttonMeusEvents = (Button) findViewById(R.id.buttonMeusEventos);
        buttonMap = (Button) findViewById(R.id.buttonMap);
        buttonProfile = (Button) findViewById(R.id.buttonProfile);

        container = (FrameLayout) findViewById(R.id.container);
        viewPager = (ViewPager) findViewById(R.id.pager);
        bottomactionbar = (LinearLayout) findViewById(R.id.bottomactionbar);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Fragment f = adapterViewPager.getItem(viewPager.getCurrentItem());
                currentFragment = f;
                switch (position) {
                    case EVENTOS_TAB:
                        restyleTabsForFragment(f);
                        break;
                    case MEUS_EVENTOS_TAB:
                        restyleTabsForFragment(f);
                        break;
                    case MAPA_TAB:
                        restyleTabsForFragment(f);
                        break;
                    case PERFIL_TAB:
                        restyleTabsForFragment(f);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        initLocation();
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        initLocation();
        mBestLocationProvider.stopLocationUpdates();
        super.onPause();
    }

    private void initLocation(){
        if(mBestLocationListener == null){
            mBestLocationListener = new BestLocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "onStatusChanged PROVIDER:" + provider + " STATUS:" + String.valueOf(status));
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "onProviderEnabled PROVIDER:" + provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "onProviderDisabled PROVIDER:" + provider);
                }

                @Override
                public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {
                    Log.w(TAG, "onLocationUpdateTimeoutExceeded PROVIDER:" + type);
                }

                @Override
                public void onLocationUpdate(Location location, BestLocationProvider.LocationType type,
                                             boolean isFresh) {
                    if(!haveLocation){
                        JSONObject obj = new JSONObject();
                        ArrayList<String> listInterests = SharedConfigs.getList("listtags", MainActivity.this);
                        try {
                            obj.put("id", Integer.valueOf(SharedConfigs.getStringValue("iduser", MainActivity.this)));
                            obj.put("latitude", location.getLatitude());
                            obj.put("longitude", location.getLongitude());
                            JSONArray array = new JSONArray(listInterests);
                            obj.put("interests", array);
                            Log.e("LOCATION TASK", obj.toString());
                            FetchLocationTask fetchLocationTask = new FetchLocationTask(MainActivity.this);
                            fetchLocationTask.execute(obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedConfigs.addStringValue("lat", String.valueOf(location.getLatitude()), MainActivity.this);
                        SharedConfigs.addStringValue("long", String.valueOf(location.getLongitude()), MainActivity.this);

                        CustomWorldHelper.getValuesToRadar(location.getLatitude(),location.getLongitude(),MainActivity.this);
                        haveLocation = true;
                        Log.e(TAG, "onLocationUpdate TYPE:" + type + " Location:" + mBestLocationProvider.locationToString(location));
                    }
                }
            };

            if(mBestLocationProvider == null){
                mBestLocationProvider = new BestLocationProvider(this, true, true, 10000, 1000, 2, 0);
            }
        }
    }
}
