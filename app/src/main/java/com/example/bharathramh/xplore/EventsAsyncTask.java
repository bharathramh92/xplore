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

import android.location.Address;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.util.Log;

import com.example.JSON.EventsJSONUtil;
import com.example.bharathramh.StorageClassCollection.Event;
import com.example.google.ConstantsGoogle;
import com.example.support.RequestParams;

public class EventsAsyncTask extends AsyncTask<String, Void, String>{
    EventsListener mListener;
    Address location;
	
	public EventsAsyncTask(EventsListener mListener, Address loc) {
		super();
		this.mListener = mListener;
        this.location = loc;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub

        String searchTerm = params[0];
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        if(location.hasLatitude() == true && location.hasLongitude() == true){
            HashMap<String, String> hMap = new HashMap<>();
            hMap.put(ConstantsGoogle.LOCATION , location.getLatitude()+","+location.getLongitude());
            hMap.put(constants.APP_KEY_STRING, constants.EVENTS_APP_KEY);
            hMap.put("within" , ConstantsGoogle.RADIUS_IN_METERS/1000+"");
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
		super.onPostExecute(result);

		mListener.eventsDataRetrieved(EventsJSONUtil.DecodeEvents(result));
	}
	
	public interface EventsListener{
		public void eventsDataRetrieved(ArrayList<Event> eventsList);
	}
}