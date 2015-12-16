package com.es.spotaneous.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.es.spotaneous.ARActivity;
import com.es.spotaneous.MapActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.radar.CustomWorldHelper;


public class ChooseViewFragment extends Fragment {

    public  ChooseViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choose_view, container, false);

        ImageButton map = (ImageButton) v.findViewById(R.id.mapaButton);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });
        ImageButton ar = (ImageButton) v.findViewById(R.id.arButton);
        ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CustomWorldHelper.getLatitude()!=0){
                    PackageManager packageManager = getActivity().getPackageManager();
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS) && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)){
                        Intent intent = new Intent(getActivity(), ARActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), "Não tem o sensor necessário para usar esta funcionalidade!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Espere enquanto encontramos a sua localização!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return v;
    }
}
