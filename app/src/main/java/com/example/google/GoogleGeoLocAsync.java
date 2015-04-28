package com.example.google;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharathramh on 4/17/15.
 */
public class GoogleGeoLocAsync extends AsyncTask<String , Void, Address> {

    GoogleGeoLocListener mListener;
    Context mContext;

    public GoogleGeoLocAsync (GoogleGeoLocListener listener, Context context){
        this.mListener = (GoogleGeoLocListener) listener;
        this.mContext = context;
    }

    @Override
    protected void onPostExecute(Address address) {
//        Log.d("geoLocAsync", address.toString());
        mListener.geoLocListener(address);

        super.onPostExecute(address);
    }

    @Override
    protected Address doInBackground(String... params) {
        try

        {
            Geocoder gc = new Geocoder(mContext);
            List<Address> address = null;
            address = gc.getFromLocationName(params[0], 1);
            if (address != null && address.size() > 0) {
                return address.get(0);
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public interface GoogleGeoLocListener {
        public void geoLocListener(Address searchLocation);
//        public void nextToken();
    }
}
