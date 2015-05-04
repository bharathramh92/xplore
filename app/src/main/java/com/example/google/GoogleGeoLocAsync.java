package com.example.google;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.bharathramh.xplore.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharathramh on 4/17/15.
 */
public class GoogleGeoLocAsync extends AsyncTask<String , Void, Address> {

    GoogleGeoLocListener mListener;
    Context mContext;
    ProgressDialog progressDialog;

    public GoogleGeoLocAsync (GoogleGeoLocListener listener, Context context){
        this.mListener = (GoogleGeoLocListener) listener;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.loading_location));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Address address) {
//        Log.d("geoLocAsync", address.toString());
        progressDialog.dismiss();
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
