package com.example.bharathramh.Adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharathramh.StorageClassCollection.Event;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.bharathramh.xplore.R;
import com.example.google.ConstantsGoogle;
import com.example.support.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bharathramh on 4/28/15.
 */


public class EventsListViewAdapter extends ArrayAdapter {

    Context mContext;
    int mResource;
    ArrayList<Event> objects;

    public EventsListViewAdapter(Context context, int resource, ArrayList objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource , parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.imageViewEventsContainer);
            holder.startTime = (TextView) convertView.findViewById(R.id.startTimeEventContainer);
            holder.vicinity = (TextView) convertView.findViewById(R.id.vicinityEventContainer);
            holder.title = (TextView) convertView.findViewById(R.id.nameEventContainer);
            convertView.setTag(holder);
        }
        Event current = objects.get(position);
        holder = (Holder) convertView.getTag();
        ImageView icon = holder.icon;

        if(current.getImage_url()!=null && current.getImage_url()!="") {
            int ht_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mContext.getResources().getDisplayMetrics());
            int wt_px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mContext.getResources().getDisplayMetrics());
            Picasso.with(mContext).load(current.getImage_url()).resize(ht_px, wt_px).into(icon);
        }else{
            icon.setImageResource(R.drawable.no_image);
        }

        TextView title, startTime, vicinity;
        title = holder.title;
        startTime = holder.startTime;
        vicinity = holder.vicinity;

        title.setText(current.getName());

        if(current.getStart_time() != null && !current.getStart_time().equals("null")) {
            startTime.setText(current.getStart_time().toString());
        }
        if(current.getVenue_address() != null && !current.getVenue_address().equals("null")) {
            vicinity.setText("near " + current.getVenue_address());
        }
        return convertView;
    }

    public static class Holder{
        ImageView icon;
        TextView title, startTime, vicinity;
    }

}
