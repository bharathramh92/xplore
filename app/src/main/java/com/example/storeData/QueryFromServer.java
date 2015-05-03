package com.example.storeData;

import android.util.Log;

import com.example.bharathramh.xplore.R;
import com.example.google.ConstantsGoogle;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharathramh on 4/19/15.
 */
public class QueryFromServer {

    public static List<ParseObject> friendsNearBy(Double mLatitude, Double mLongitude, ArrayList<Long> friendsId, int radius){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ConstantsParse.TABLE_NAME_LOCATION_DATA);
        final ParseGeoPoint loctn = new ParseGeoPoint(mLatitude, mLongitude);
        query.whereWithinKilometers(ConstantsParse.LOCATION, loctn, radius);
        query.whereContainedIn(ConstantsParse.Id, friendsId);

        try {
            List<ParseObject> scoreList = query.find();
            Log.d("score", "Retrieved " + scoreList.size() + " scores");
            Log.d("score", "Retrieved " + scoreList.toString());
            return  scoreList;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("score", "Error: " + e.getMessage());
            return null;
        }




    }

}
