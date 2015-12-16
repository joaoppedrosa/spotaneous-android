package com.es.spotaneous.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.es.spotaneous.EditDadosActivity;
import com.es.spotaneous.EventDescriptionActivity;
import com.es.spotaneous.LoginActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.adapters.ListViewEventsAdapter;
import com.es.spotaneous.objects.Events;
import com.es.spotaneous.objects.User;
import com.es.spotaneous.tasks.FetchAllEventsTask;
import com.es.spotaneous.tasks.FetchUserTask;
import com.es.spotaneous.utils.SharedConfigs;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import su.levenetc.android.textsurface.Text;

public class ProfileFragment extends Fragment {

    private Context context;
    private String idUser;
    private CircleImageView imageView;
    private TextView name;
    private User user = null;
    String image = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        idUser = SharedConfigs.getStringValue("iduser", context);

        imageView = (CircleImageView) view.findViewById(R.id.profile_image);
        name = (TextView) view.findViewById(R.id.username);
        addInformation();

        Button b = (Button) view.findViewById(R.id.editDados);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditDadosActivity.class);
                startActivity(intent);
            }
        });
        Button elim = (Button) view.findViewById(R.id.eleminate);
        elim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Utilizador eliminado com sucesso!", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedConfigs.addStringValue("iduser", "", getActivity());
                        SharedPreferences preferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
                        preferences.edit().remove("user_data").commit();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, 2000);
            }
        });
        return view;
    }

    private void addInformation() {

        SharedPreferences prefs = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("user_data", "");
        this.user = gson.fromJson(json, User.class);

        FetchUserTask fetchUserTask = new FetchUserTask(context) {
            @Override
            protected void onPostExecute(final User u) {
                super.onPostExecute(u);
                //image = u.getPhoto();
                Picasso.with(context).load(u.getPhoto()).placeholder(R.drawable.placeholder).into(imageView);
                name.setText(u.getName());

                SharedPreferences prefs = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String user_json = gson.toJson(u);
                prefs.edit().putString("user_data", user_json).apply();
            }
        };

        if (this.user == null) {
            fetchUserTask.execute(idUser);
        } else {
            Picasso.with(context).load(this.user.getPhoto()).placeholder(R.drawable.placeholder).into(imageView);
            name.setText(this.user.getName());
        }
    }
}
