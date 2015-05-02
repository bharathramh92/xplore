package com.example.google;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.JSON.GoogleJSONUtils;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.bharathramh.xplore.R;
import com.example.support.RequestParams;
import com.google.android.gms.location.places.Places;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bharathramh on 4/14/15.
 * First index should be location, second should the search type. eg. restaurant
 */
public class PlacesGoogleAsync extends AsyncTask<String, Void, String> implements GoogleJSONUtils.GoogleUtilsListener{

    GooglePlacesInterface mListener;
    Context mContext;
    Address location;
    String statusCode=""; String nextPageToken="";
    String from;
    ProgressDialog progressDialog;

    public PlacesGoogleAsync(GooglePlacesInterface listener, Context context, Address loc) {
        this.mListener = (GooglePlacesInterface) listener;
        this.mContext = context;
        this.location = loc;
    }

    @Override
    protected void onPostExecute(String sb) {
        progressDialog.dismiss();
        if(sb!=null) {
            mListener.placesQueryListener(GoogleJSONUtils.JSONGooglePlaceDecode(sb.toString(), this), this.statusCode, this.nextPageToken, from);
        }else{
            mListener.placesQueryListener(null, "REQUEST_DENIED", "", from);
        }
        super.onPostExecute(sb);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.loading_data));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String searchTerm = params[0];

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String radius = mySharedPreferences.getString(mContext.getResources().getString(R.string.shared_pref_radius_key), ConstantsGoogle.DEFAULT_RADIUS);

        if(location.hasLatitude() == true && location.hasLongitude() == true){
            HashMap<String, String> hMap = new HashMap<>();
            hMap.put(ConstantsGoogle.LOCATION , location.getLatitude()+","+location.getLongitude());
            hMap.put(ConstantsGoogle.KEY, ConstantsGoogle.GOOGLE_KEY);
            hMap.put(ConstantsGoogle.TYPES , searchTerm);
            hMap.put(ConstantsGoogle.RADIUS, ""+radius);
            hMap.put("rankby" , "prominence");
//            hMap.put("rankby" , "distance");

            try {
                HttpURLConnection conPlaces = RequestParams.setupConnection("GET", ConstantsGoogle.GOOGLE_API_PLACES_URL,hMap );
                String temp = "";
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader( new InputStreamReader(conPlaces.getInputStream()));
                while ((temp = reader.readLine()) != null) {
                    sb.append(temp);
//                    Log.d("placesgoogle" , temp);
                }
                this.from = params[1];
                return sb.toString();


//                        mListener.placesQueryListener(GoogleJSONUtils.JSONGooglePlaceDecode())
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    return null;
    }

    @Override
    public void statusCodes(String statusCode, String nextPageToken) {
        Log.d("placesGoogle", "status code is "+ statusCode+ "nextPageToken is "+ nextPageToken);
        this.statusCode = statusCode;
        this.nextPageToken = nextPageToken;
    }


    public interface GooglePlacesInterface{
        public void placesQueryListener(ArrayList<GooglePlacesCS> result,String statusCode, String nextTokenString, String from);
//        public void nextToken();
    }
}


