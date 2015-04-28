package com.example.bharathramh.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharathramh.StorageClassCollection.FacebookFriendsData;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.bharathramh.xplore.R;
import com.example.google.ConstantsGoogle;
import com.example.support.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bharathramh on 4/19/15.
 */
public class NearByFriendsAdapter extends ArrayAdapter {

    Context mContext;
    int mResource;
    ArrayList<FacebookFriendsData> objects;

    public NearByFriendsAdapter(Context context, int resource, ArrayList objects) {
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
            holder.icon = (ImageView) convertView.findViewById(R.id.fbfriendsPicture);
            holder.name = (TextView) convertView.findViewById(R.id.fbfriendsName);
            holder.lastSeen = (TextView) convertView.findViewById(R.id.fbfriendsLastUpdate);
            convertView.setTag(holder);
        }
        FacebookFriendsData current = objects.get(position);
        holder = (Holder) convertView.getTag();
        ImageView icon = holder.icon;


        TextView name, lastSeen;

        name = holder.name;
        lastSeen = holder.lastSeen;

        name.setText(current.getName());

        lastSeen.setText("LastSeen at "+ current.getLastUpdated()+"");

        String photoUrl =  "https://graph.facebook.com/"+current.getId()+"/picture?height=400&width=400";

        Picasso.with(mContext).load(photoUrl).into(icon);

        return convertView;
    }

    public static class Holder{
        ImageView icon;
        TextView name, lastSeen;
    }

}
