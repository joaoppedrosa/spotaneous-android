package com.es.spotaneous;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.es.spotaneous.fragments.MapaFragment;

public class MapActivity extends FragmentActivity {

    private final static String TAG = "MapActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        showFragment(new MapaFragment());
    }

    public void showFragment(Fragment f) {
        FragmentTransaction ft = this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f);
        ft.commitAllowingStateLoss();
    }
}
