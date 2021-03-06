package com.example.bharathramh.xplore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.bharathramh.StorageClassCollection.FacebookFriendsData;
import com.example.google.ConstantsGoogle;
import com.example.storeData.ConstantsParse;
import com.example.storeData.QueryFromServer;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharathramh on 4/19/15.
 */
public class PopulateNearByFriendsAsync extends AsyncTask<Bundle, Void, ArrayList> {

    nearByFriendsListener mListener;
    int radius;
    public PopulateNearByFriendsAsync(nearByFriendsListener mListener, Context mContext) {
        this.mListener = mListener;
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        radius = Integer.parseInt(mySharedPreferences.getString(mContext.getResources().getString(R.string.shared_pref_radius_key), ConstantsGoogle.DEFAULT_RADIUS))/1000;

    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        mListener.nearByFriendsDataRetrieved(arrayList);


    }

    @Override
    protected ArrayList doInBackground(Bundle... params) {
        List<ParseObject> dataPO = QueryFromServer.friendsNearBy(params[0].getDouble("latitude"),
                params[0].getDouble("longitude"), (ArrayList) params[0].getSerializable("fbIds"), radius);
        AccessToken successToken = params[0].getParcelable("AccessToken");
        ArrayList<FacebookFriendsData> data = new ArrayList<>();
        if(dataPO==null || dataPO.size()==0){
            Log.d("populateFriends", "data was null");
            return null;
        }
        for(ParseObject temp : dataPO){
            FacebookFriendsData obj = new FacebookFriendsData();
            obj.setId(temp.getLong(ConstantsParse.Id));
            obj.setName(temp.getString(ConstantsParse.NAME));
            obj.setLastUpdated(temp.getCreatedAt());

            data.add(obj);
        }
        Log.d("populateFriends", "data was "+data.toString());
        return data;
    }

    public interface nearByFriendsListener{
        public void nearByFriendsDataRetrieved(ArrayList data);
    }

}
