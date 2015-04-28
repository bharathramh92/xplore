package com.example.JSON;

import android.util.Log;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bharathramh on 4/14/15.
 */
public class GoogleJSONUtils {

    public static ArrayList<GooglePlacesCS> JSONGooglePlaceDecode(String in, GoogleUtilsListener context){

        GoogleUtilsListener mListener = context;

        ArrayList<GooglePlacesCS> data = null;

        try {
            JSONObject root = new JSONObject(in);
            String nextPageToken="";
            try{
                nextPageToken = root.getString("next_page_token");
            }catch (JSONException e){
                Log.d("jsonUtils" , "No next page");
                e.printStackTrace();
            }
            String statusCode = root.getString("status");
            mListener.statusCodes(statusCode, nextPageToken);
            if(!statusCode.equals("OK")){
                return null;
            }
            JSONArray array = root.getJSONArray("results");
            GooglePlacesCS gpcs ;
            data = new ArrayList<>();
            for(int i=0 ; i<array.length() ; ++i){
                gpcs = new GooglePlacesCS();
                try {
                    JSONObject current = array.getJSONObject(i);
                    gpcs.setName(current.getString("name"));
                    gpcs.setLat(current.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    gpcs.setLng(current.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    try{
                        gpcs.setRating(current.getDouble("rating"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    gpcs.setVicinity(current.getString("vicinity"));
                    try{
                        gpcs.setPriceLevel(current.getInt("price_level"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    try {
                        gpcs.setPhotoReference(current.getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
                    }catch (JSONException e){
                        Log.d("googleUtilsJSON" , "Exception caught in photos");
                        e.printStackTrace();
                    }
                    data.add(gpcs);
                    Log.d("googleJSONutils", gpcs.toString() + " is the content");
                }catch (JSONException e){
                    Log.d("googleUtilsJSON" , "Exception caught");
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }

    public interface GoogleUtilsListener{
        public void statusCodes(String statusCode, String NextPageToken);
    }

}

