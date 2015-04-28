package com.example.bharathramh.xplore;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by bharathramh on 4/13/15.
 */
public class MainDetailsListAdapter extends ArrayAdapter<String> {
    public MainDetailsListAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
    }
}
