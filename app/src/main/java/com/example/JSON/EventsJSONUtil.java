package com.example.JSON;

import android.util.Log;

import com.example.bharathramh.StorageClassCollection.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bharathramh on 4/28/15.
 */
public class EventsJSONUtil {
        public static ArrayList<Event> DecodeEvents(String in) {
            ArrayList<Event> eventsList = new ArrayList<Event>();
            try {
                JSONObject root = new JSONObject(in);
                JSONArray eventsJSONArray;
//                JSONObject eventsRootJSONObject = root.getJSONObject("events");
                try {
                    eventsJSONArray = root.getJSONObject("events").getJSONArray("event");
                }catch (JSONException e){
                    Log.d("eventsutils", "Exception, events or event not found in json");
                    return null;
                }
                for (int i = 0; i < eventsJSONArray.length(); i++) {
                    JSONObject eventJSONObject = (JSONObject) eventsJSONArray.get(i);
                    Event event = new Event();
                    event.setName(eventJSONObject.getString("title"));
                    event.setStart_time(eventJSONObject.getString("start_time"));
                    event.setCity_name(eventJSONObject.getString("city_name"));
                    event.setVenue_address(eventJSONObject.getString("venue_address"));
                    event.setUrl(eventJSONObject.getString("url"));
                    try{
                        event.setImage_url(eventJSONObject.getJSONObject("image").getJSONObject("large").getString("url"));
                    }catch (JSONException e){
                        Log.d("eventsJsonUtils", "image not found for "+event.getName());
                    }
                    eventsList.add(event);
                }
                return eventsList;
            }catch (Exception e){
                Log.d("eventsutils", "Exception");
            }
            return null;
        }

}
