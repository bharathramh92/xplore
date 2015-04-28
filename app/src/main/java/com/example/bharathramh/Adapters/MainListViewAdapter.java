package com.example.bharathramh.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bharathramh.xplore.R;

import java.util.Objects;

/**
 * Created by bharathramh on 4/15/15.
 */
public class MainListViewAdapter extends ArrayAdapter {

    Context mContext;
    int mResource;
    String[] mObjects;

    public MainListViewAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.mainItemsTextView);
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        TextView tx = holder.textView;
        tx.setText(mObjects[position]);
        return convertView;
    }

    public static class Holder{
        TextView textView;
    }


}
