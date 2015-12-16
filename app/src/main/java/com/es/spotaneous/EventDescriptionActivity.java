package com.es.spotaneous;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.es.spotaneous.adapters.ListViewEventsAdapter;
import com.es.spotaneous.fragments.EventosFragment;
import com.es.spotaneous.fragments.LoginFragment;
import com.es.spotaneous.fragments.MeusEventosFragment;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.radar.CustomWorldHelper;
import com.es.spotaneous.tasks.DeleteEventTask;
import com.es.spotaneous.tasks.DeleteUserAttendsEventTask;
import com.es.spotaneous.tasks.FetchEventUserHostsTask;
import com.es.spotaneous.tasks.FetchFriendsAttendingTask;
import com.es.spotaneous.tasks.UserAttendsEventTask;
import com.es.spotaneous.utils.SharedConfigs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventDescriptionActivity extends AppCompatActivity {


    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.friend1)
    ImageView friend1;
    @Bind(R.id.friend1name)
    TextView friend1name;
    @Bind(R.id.friend2)
    ImageView friend2;
    @Bind(R.id.friend2name)
    TextView friend2name;
    @Bind(R.id.friend3)
    ImageView friend3;
    @Bind(R.id.friend3name)
    TextView friend3name;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.cost)
    TextView cost;
    @Bind(R.id.date)
    TextView date_view;
    @Bind(R.id.hour)
    TextView hour;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.friendsattending)
    TextView friendsattending;
    @Bind(R.id.bottomButton)
    Button bottomButton;

    double lat;
    double log;

    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);
        ButterKnife.bind(this);

        /*  usar id estatico por agora  */
        idUser = SharedConfigs.getStringValue("iduser", getApplicationContext());
        //idUser = "3";

        Bundle b = getIntent().getExtras();
        if (!b.isEmpty()) {
            showInformation(b);
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void showInformation(Bundle b) {

        final String idEvent = b.getString("id");
        Log.d("id_event", idEvent);
        try {
            JSONObject jsonObj = new JSONObject(b.getString("host"));
            Log.d("Host id", jsonObj.getString("id"));
            Log.d("user id", idUser);
            Log.d("bundle", b.toString());


            FetchFriendsAttendingTask fetchFriendsAttendingTask = new FetchFriendsAttendingTask(getApplicationContext()) {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (result != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            Log.d("FetchFriendsAttending", jsonObj.toString());

                            JSONArray friends = jsonObj.getJSONArray("info");

                            if (friends.length() == 1) {
                                Log.d("json0", friends.getJSONObject(0).getString("photo"));
                                Glide.with(getApplicationContext()).load(friends.getJSONObject(0).getString("photo")).centerCrop().into(friend1);
                                friend1name.setText(friends.getJSONObject(0).getString("name"));
                                friendsattending.setVisibility(View.VISIBLE);
                            } else if (friends.length() == 2) {
                                Glide.with(getApplicationContext()).load(friends.getJSONObject(0).getString("photo")).centerCrop().into(friend1);
                                friend1name.setText(friends.getJSONObject(0).getString("name"));
                                Glide.with(getApplicationContext()).load(friends.getJSONObject(1).getString("photo")).centerCrop().into(friend2);
                                friend2name.setText(friends.getJSONObject(1).getString("name"));
                                friendsattending.setVisibility(View.VISIBLE);

                            } else {
                                Glide.with(getApplicationContext()).load(friends.getJSONObject(0).getString("photo")).centerCrop().into(friend1);
                                friend1name.setText(friends.getJSONObject(0).getString("name"));
                                Glide.with(getApplicationContext()).load(friends.getJSONObject(1).getString("photo")).centerCrop().into(friend2);
                                friend2name.setText(friends.getJSONObject(1).getString("name"));
                                Glide.with(getApplicationContext()).load(friends.getJSONObject(2).getString("photo")).centerCrop().into(friend3);
                                friend3name.setText(friends.getJSONObject(2).getString("name"));
                                friendsattending.setVisibility(View.VISIBLE);


                            }
                            //Glide.with(getApplicationContext()).load(b.getString("image")).centerCrop().into(friend2);
                            //Glide.with(getApplicationContext()).load(b.getString("image")).centerCrop().into(friend3);

                        } catch (JSONException e) {
                            Log.d("JSONException", e.toString());
                        }
                    }


                    //Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(menu);
                }
            };
            fetchFriendsAttendingTask.execute(idEvent, idUser);


            lat = b.getDouble("lat");
            log = b.getDouble("log");

            JSONArray attending = new JSONArray(b.getString("attending"));

            boolean attending_event = false;
            for (int i = 0; i < attending.length(); i++) {
                if (Integer.toString(attending.getInt(i)).equals(idUser)) {
                    attending_event = true;
                }
            }

            if (idUser.equals(jsonObj.getString("id"))) {
                bottomButton.setText("Eliminar Evento");
                bottomButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // delete event

                        DeleteEventTask deleteEventTask = new DeleteEventTask(getApplicationContext()) {
                            @Override
                            protected void onPostExecute(Void result) {
                                super.onPostExecute(null);
                                Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(menu);
                            }
                        };
                        deleteEventTask.execute(idEvent, idUser);
                        MeusEventosFragment.outdatedEventData();
                        EventosFragment.outdatedEventData();
                    }
                });
            } else if (attending_event) {
                bottomButton.setText("Sair do evento");
                bottomButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // remove user from attending list

                        DeleteUserAttendsEventTask deleteUserAttendsEventTask = new DeleteUserAttendsEventTask(getApplicationContext()) {
                            @Override
                            protected void onPostExecute(Void result) {
                                super.onPostExecute(null);
                                Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(menu);
                            }
                        };
                        deleteUserAttendsEventTask.execute(idEvent, idUser);
                        MeusEventosFragment.outdatedEventData();
                        EventosFragment.outdatedEventData();
                    }
                });
            } else {
                bottomButton.setText("Aderir ao evento");
                bottomButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // add user to attending list

                        UserAttendsEventTask userAttendsEventTask = new UserAttendsEventTask(getApplicationContext()) {
                            @Override
                            protected void onPostExecute(Void result) {
                                super.onPostExecute(null);
                                Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(menu);
                            }
                        };
                        userAttendsEventTask.execute(idEvent, idUser);
                        MeusEventosFragment.outdatedEventData();
                        EventosFragment.outdatedEventData();
                    }
                });
            }

            Glide.with(this).load(b.getString("image")).centerCrop().placeholder(R.drawable.spotaneous1).into(image);
            title.setText(b.getString("title"));
            String date = b.getString("dateb").split("T")[0];
            String time = b.getString("dateb").split("T")[1].split(":")[0] + ":" + b.getString("dateb").split("T")[1].split(":")[1] + "h";
            hour.setText(time);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date_obj = dateFormat.parse(date);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date_obj);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                Calendar cal_today = Calendar.getInstance();

                Calendar cal_tomorrow = Calendar.getInstance();
                cal_tomorrow.add(cal_tomorrow.DAY_OF_MONTH, 1);

                if (day == cal_today.get(Calendar.DAY_OF_MONTH)
                        && month == cal_today.get(Calendar.MONTH)
                        && year == cal_today.get(Calendar.YEAR)) {

                    date_view.setText("Today");


                    Log.e("day", new Integer(day).toString());
                    Log.e("month", new Integer(month).toString());
                    Log.e("year", new Integer(year).toString());

                    Log.d("day", new Integer(cal_today.get(Calendar.DAY_OF_MONTH)).toString());
                    Log.d("month", new Integer(cal_today.get(Calendar.MONTH)).toString());
                    Log.d("year", new Integer(cal_today.get(Calendar.YEAR)).toString());

                } else if (day == cal_tomorrow.get(Calendar.DAY_OF_MONTH)
                        && month == cal_tomorrow.get(Calendar.MONTH)
                        && year == cal_tomorrow.get(Calendar.YEAR)) {

                    date_view.setText("Tomorrow");


                    Log.e("day", new Integer(day).toString());
                    Log.e("month", new Integer(month).toString());
                    Log.e("year", new Integer(year).toString());

                    Log.d("day", new Integer(cal_tomorrow.get(Calendar.DAY_OF_MONTH)).toString());
                    Log.d("month", new Integer(cal_tomorrow.get(Calendar.MONTH)).toString());
                    Log.d("year", new Integer(cal_tomorrow.get(Calendar.YEAR)).toString());

                } else {
                    date_view.setText(date);
                }
            } catch (ParseException e) {
                date_view.setText(date);
            }

            description.setText(b.getString("description"));
            cost.setText(new Integer(b.getInt("cost")).toString() + "€");
            description.setText(b.getString("description"));
        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
        }

    }

    public void directions(View view) {
        if (CustomWorldHelper.getLatitude() != 0) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + CustomWorldHelper.getLatitude() + "," + CustomWorldHelper.getLongitude() + "&daddr=" + lat + "," + log));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Espere enquanto encontramos a sua localização!", Toast.LENGTH_SHORT).show();
        }
    }
}
