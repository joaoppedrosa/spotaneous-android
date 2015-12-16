package com.es.spotaneous.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.es.spotaneous.R;
import com.es.spotaneous.objects.Events;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import su.levenetc.android.textsurface.Text;

/**
 * Created by JoaoPedro on 29-09-2015.
 */
public class ListViewEventsAdapter extends ArrayAdapter<Events> {
    Context context;
    int layoutResourceId;
    ArrayList<Events> data = new ArrayList<>();

    public ListViewEventsAdapter(Context context, int layoutResourceId, ArrayList<Events> data) {
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
        String datetime = item.getDateB();
        String date = datetime.split("T")[0];
        String time = datetime.split("T")[1];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
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
                    && year == cal_today.get(Calendar.YEAR)){

                holder.datetime.setText("Today");


                Log.e("day", new Integer(day).toString());
                Log.e("month", new Integer(month).toString());
                Log.e("year", new Integer(year).toString());

                Log.d("day", new Integer(cal_today.get(Calendar.DAY_OF_MONTH)).toString());
                Log.d("month", new Integer(cal_today.get(Calendar.MONTH)).toString());
                Log.d("year", new Integer(cal_today.get(Calendar.YEAR)).toString());

            }
            else if (day == cal_tomorrow.get(Calendar.DAY_OF_MONTH)
                    && month == cal_tomorrow.get(Calendar.MONTH)
                    && year == cal_tomorrow.get(Calendar.YEAR)){

                holder.datetime.setText("Tomorrow");


                Log.e("day", new Integer(day).toString());
                Log.e("month", new Integer(month).toString());
                Log.e("year", new Integer(year).toString());

                Log.d("day", new Integer(cal_tomorrow.get(Calendar.DAY_OF_MONTH)).toString());
                Log.d("month", new Integer(cal_tomorrow.get(Calendar.MONTH)).toString());
                Log.d("year", new Integer(cal_tomorrow.get(Calendar.YEAR)).toString());

            }
            else{
                holder.datetime.setText(date);
            }
        }
        catch (ParseException e){
            holder.datetime.setText(date);
        }
        Picasso.with(context.getApplicationContext()).load(item.getImage()).placeholder(R.drawable.spotaneous2).into(holder.image);

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