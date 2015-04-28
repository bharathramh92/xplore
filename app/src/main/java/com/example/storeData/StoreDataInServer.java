package com.example.storeData;

import android.os.Bundle;
import android.util.Log;

import com.example.bharathramh.xplore.constants;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by bharathramh on 4/12/15.
 */
public class StoreDataInServer {



    public static void UpdateLocationData(final long id, final Double mLatitude, final Double mLongitude, final String name){


        final ParseGeoPoint loctn = new ParseGeoPoint(mLatitude, mLongitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ConstantsParse.TABLE_NAME_LOCATION_DATA);
        query.whereEqualTo(ConstantsParse.Id, id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    if(userList!=null && userList.size()==0){
                    //new user
                        Log.d("storeInServer", "new user");
                        ParseObject loc = new ParseObject(ConstantsParse.TABLE_NAME_LOCATION_DATA);
                        loc.put(ConstantsParse.LOCATION, loctn);
                        loc.put(ConstantsParse.NAME, name);
                        loc.put(ConstantsParse.Id, id);
                        loc.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Log.d("storeInServer", "location updated ");
                                }else{
                                    Log.d("storeInServer", "location not updated ");
                                    e.printStackTrace();
                                }
                            }
                        });

                    }else{
                        //old user
                        Log.d("storeInServer", "old user");

                        userList.get(0).put(ConstantsParse.LOCATION, loctn);
                        userList.get(0).put(ConstantsParse.NAME, name);
                        userList.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Log.d("storeInServer", "location updated ");
                                }else{
                                    Log.d("storeInServer", "location not updated ");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    Log.d("score", "Retrieved " + userList.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });



    }

}
