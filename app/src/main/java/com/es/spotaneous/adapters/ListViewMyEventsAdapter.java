package com.es.spotaneous.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.es.spotaneous.R;
import com.es.spotaneous.objects.Events;

import java.util.ArrayList;

/**
 * Created by JoaoPedro on 29-09-2015.
 */
public class ListViewMyEventsAdapter extends ArrayAdapter<Events> {
    Context context;
    int layoutResourceId;
    ArrayList<Events> data = new ArrayList<>();

    public ListViewMyEventsAdapter(Context context, int layoutResourceId, ArrayList<Events> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new Holder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.price = (TextView) row.findViewById(R.id.price);
            holder.address = (TextView) row.findViewById(R.id.address);
            holder.datetime = (TextView) row.findViewById(R.id.datetime);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        Events item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText(item.getCost()+"€");
        if(!item.getDistance().equals("null")){
            holder.address.setText(roundeDistance(Double.valueOf(item.getDistance()))+ " m");
        }
        holder.datetime.setText(item.getDateB());
        Glide.with(context.getApplicationContext()).load(item.getImage()).centerCrop().into(holder.image);

        return row;
    }

    static class Holder {
        TextView title;
        TextView price;
        TextView address;
        TextView datetime;
        ImageView image;
    }

    private int roundeDistance(double number){
        number = Math.round(number * 100);
        number = number/100;
        return (int) Math.round(number);

    }
}