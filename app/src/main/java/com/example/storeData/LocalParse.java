package com.example.storeData;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by bharathramh on 4/30/15.
 */
public class LocalParse {
    public static String FAV_CLASS_NAME= "favourites";
    public static String PLACE_ID= "place_id";

    public static void saveFavourites(String place_id, final LocalParseInterface mListener){
        ParseObject fav = new ParseObject(FAV_CLASS_NAME);
        fav.put(PLACE_ID, place_id);
        fav.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mListener.favStored();
            }
        });
    }

    public static void isIdPresent(final String place_id, final LocalParseInterface mListener, final boolean removeOrAdd){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(FAV_CLASS_NAME);
        query.fromLocalDatastore();
        query.whereContains(PLACE_ID, place_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(parseObjects != null && parseObjects.size()> 0){
                    mListener.favourite(true, place_id, removeOrAdd);
                }else{
                    mListener.favourite(false, place_id, removeOrAdd);
                }
            }
        });
    }

    public static void removePlaceId(String place_id,final LocalParseInterface mListener ){
//        unpinInBackground();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(FAV_CLASS_NAME);
        query.fromLocalDatastore();
        query.whereContains(PLACE_ID, place_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(parseObjects != null && parseObjects.size()> 0){
                    for(ParseObject x : parseObjects){
                        x.unpinInBackground();
                    }
                    mListener.favRemoved();
                }
            }
        });
    }

    public interface LocalParseInterface{
        public void favStored();
        public void favRemoved();
        public void favourite(boolean isPresent, String place_id, boolean removeOrAdd);
    }
}
