package com.es.spotaneous;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatRadioButton;
import com.es.spotaneous.fragments.EventosFragment;
import com.es.spotaneous.fragments.MeusEventosFragment;
import com.es.spotaneous.fragments.TagsFragment;
import com.es.spotaneous.tasks.FetchCreateEventTask;
import com.es.spotaneous.utils.SharedConfigs;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;



public class NewEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private static final int IMAGE_PICKER_SELECT = 999;
    private String dateValue;
    private String timeValue;
    private double lat;
    private double log;
    private static String pathImage;

    @Bind(R.id.title) EditText title;
    @Bind(R.id.description) EditText description;
    @Bind(R.id.cost) EditText cost;
    @Bind(R.id.maxpeople) EditText maxPeople;
    @Bind(R.id.radio1) FlatRadioButton radio1;
    @Bind(R.id.radio2) FlatRadioButton radio2;
    @Bind(R.id.image) CircleImageView image;
    @Bind(R.id.datepicker) Button datepicker;
    @Bind(R.id.timepicker) Button timepicker;
    @Bind(R.id.datepickerFim) Button datepickerFim;
    @Bind(R.id.timepickerFim) Button timepickerFim;
    @Bind(R.id.locationtext) TextView location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        ButterKnife.bind(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);
            }
        });
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateValue = "inicio";
                createViewDate();
            }
        });
        timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeValue = "inicio";
                createViewTime();
            }
        });
        datepickerFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateValue = "fim";
                createViewDate();
            }
        });
        timepickerFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeValue = "fim";
                createViewTime();
            }
        });

        showFragment(new TagsFragment());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getDouble("latitude");
            log = extras.getDouble("longitude");
            location.setText(String.format("%.2f", lat) + "," + String.format("%.2f", log));
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        if(dateValue.equals("inicio")){
            datepicker.setText(dayOfMonth+"-"+monthOfYear+"-"+year);
            datepicker.setBackgroundColor(getResources().getColor(R.color.logo_color_1));
        }else{
            datepickerFim.setText(dayOfMonth+"-"+monthOfYear+"-"+year);
            datepickerFim.setBackgroundColor(getResources().getColor(R.color.logo_color_1));
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        if(timeValue.equals("inicio")) {
            timepicker.setText(hourOfDay + ":" + minute);
            timepicker.setBackgroundColor(getResources().getColor(R.color.logo_color_1));
        }else{
            timepickerFim.setText(hourOfDay + ":" + minute);
            timepickerFim.setBackgroundColor(getResources().getColor(R.color.logo_color_1));
        }
    }

    private void createViewTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),true
        );
        tpd.setThemeDark(true);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.setAccentColor(getResources().getColor(R.color.logo_color_1));
        tpd.setTitle("TimePicker Title");

        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    private void createViewDate(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(true);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.setAccentColor(getResources().getColor(R.color.logo_color_1));
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if(tpd != null) tpd.setOnTimeSetListener(this);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_SELECT  && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = getBitmapFromCameraData(data, this);
            image.setImageBitmap(bitmap);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        pathImage = picturePath;
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    public void addEvent(View view) {
        String dataFim = datepickerFim.getText().toString();
        String dataInicio = datepicker.getText().toString();
        String timeFim = timepickerFim.getText().toString();
        String timeInicio = timepicker.getText().toString();
        String desc = description.getText().toString();
        String tit = title.getText().toString();
        String c = cost.getText().toString();
        String max = maxPeople.getText().toString();
        ArrayList<String> listInterests = SharedConfigs.getList("listtags", this);
        Log.e("List", listInterests.toString()+"");
        String type;
        if(radio1.isChecked()){
            type = "PUB";
        }else{
            type = "PRIV";
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("longitude", log);
            obj.put("latitude", lat);
            obj.put("title", tit);
            obj.put("subtitle", "");
            obj.put("description", desc);
            obj.put("beginning", dataInicio + "T" + timeInicio+"Z");
            obj.put("end", dataFim+"T"+timeFim+"Z");
            obj.put("cost", Integer.parseInt(c));
            obj.put("host", Integer.parseInt(SharedConfigs.getStringValue("iduser", getApplicationContext())));
            obj.put("type", type);
            obj.put("min_people",0);
            obj.put("max_people", Integer.parseInt(max));
//            ArrayList<String> listtags = SharedConfigs.getList("listtags", NewEventActivity.this);
//            JSONArray array = new JSONArray(listtags);
//            obj.put("interest",array);
            obj.put("interest",1);

        } catch (JSONException e) {
            //obj.put("image", pathImage);
            e.printStackTrace();
        }
        Log.e("OBJECT JSON", obj.toString()+"");

        //TESTAR SALVAR EVENTO NO SERVIDOR
        FetchCreateEventTask createEventTask = new FetchCreateEventTask(this);
        createEventTask.execute(obj);
        MeusEventosFragment.outdatedEventData();
        EventosFragment.outdatedEventData();
        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(menu);
    }

    public void showFragment(Fragment f) {
        FragmentTransaction ft = this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f);
        ft.commitAllowingStateLoss( );
    }
}
