package com.example.bharathramh.xplore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;

import com.example.JSON.EventsJSONUtil;
import com.example.bharathramh.StorageClassCollection.Event;
import com.example.google.ConstantsGoogle;
import com.example.support.RequestParams;

public class EventsAsyncTask extends AsyncTask<String, Void, String>{
    EventsListener mListener;
    Address location;
    Context mContext;
    ProgressDialog progressDialog;
	
	public EventsAsyncTask(EventsListener mListener, Context mCon, Address loc) {
		super();
		this.mListener = mListener;
        this.location = loc;
        this.mContext = mCon;
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

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String radius = mySharedPreferences.getString(mContext.getResources().getString(R.string.shared_pref_radius_key), ConstantsGoogle.DEFAULT_RADIUS);

        String searchTerm = params[0];
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        if(location.hasLatitude() == true && location.hasLongitude() == true){
            HashMap<String, String> hMap = new HashMap<>();
            hMap.put(ConstantsGoogle.LOCATION , location.getLatitude()+","+location.getLongitude());
            hMap.put(constants.APP_KEY_STRING, constants.EVENTS_APP_KEY);
            hMap.put("within" , radius);
            hMap.put("units", "km");
            hMap.put("category", params[0]);
            hMap.put("sort_order", "popularity");
            hMap.put("image_sizes","large");
            Log.d("eventsasync", hMap.toString());
            try {
                HttpURLConnection conPlaces = RequestParams.setupConnection("GET", constants.EVENTS_BASE_URL, hMap);
                String temp = "";
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader( new InputStreamReader(conPlaces.getInputStream()));
                while ((temp = reader.readLine()) != null) {
                    sb.append(temp);
                    Log.d("eventsasync" , temp);
                }

                return sb.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		mListener.eventsDataRetrieved(EventsJSONUtil.DecodeEvents(result));
        super.onPostExecute(result);
	}
	
	public interface EventsListener{
		public void eventsDataRetrieved(ArrayList<Event> eventsList);
	}
}
