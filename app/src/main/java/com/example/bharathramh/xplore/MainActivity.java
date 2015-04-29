package com.example.bharathramh.xplore;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.google.PlacesGoogleAsync;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements EventsListFragment.OnFragmentInteractionListener,
        FaceBookLogin.OnFragmentInteractionListener,
        RestaurantFragment.RestaurantFragListener,
        PlacesGoogleAsync.GooglePlacesInterface, MainViewFragment.MainViewOnFragmentInteractionListener
         {

    public static String SIGN_IN_FRAG="signInFrag";
    public static String CODE_VER="codeVeriFrag";

    public static String MAIN_FRAG="mainViewFragTag";
    public static String RESTAURANT_FRAG="restaurantFragTag";
    public static String FACEBOOK_FRAG="facebookFragTag";

    ArrayList<GooglePlacesCS> data = null;
    Bundle userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getSupportFragmentManager().beginTransaction().
                add(R.id.mainContainer, new MainViewFragment(), MainActivity.MAIN_FRAG)
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public void callRestFrag(ArrayList<GooglePlacesCS> result) {
        RestaurantFragment restFrag = RestaurantFragment.instance(result);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.mainContainer, restFrag, MainActivity.RESTAURANT_FRAG)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void callFriendsActivity(Address location) {

        FaceBookLogin f = FaceBookLogin.instance(location);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.mainContainer, f , MainActivity.FACEBOOK_FRAG)
                .addToBackStack(null)
                .commit();
    }

     @Override
     public void callEventsFrag(ArrayList result) {
         EventsListFragment f = EventsListFragment.instance(result);

         getSupportFragmentManager().beginTransaction().
                 replace(R.id.mainContainer, f , MainActivity.FACEBOOK_FRAG)
                 .addToBackStack(null)
                 .commit();
     }


             @Override
    public void placesQueryListener(ArrayList<GooglePlacesCS> result, String statusCode, String nextTokenString) {
        if(result!=null && statusCode.equals("OK") && result.size()>0) {
            Log.d("mainActivity", result.toString());
            Log.d("mainActivity", "status Code" + statusCode + "nextToken" + nextTokenString);

            RestaurantFragment restFrag = RestaurantFragment.instance(result);

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.mainContainer, restFrag, MainActivity.RESTAURANT_FRAG)
                    .addToBackStack(null)
                    .commit();

//            Log.d("main",getFragmentManager().getBackStackEntryCount()+"");

            data = result;


        }else if(!statusCode.equals("OK")){
            try{

                showToast(statusCode);

//                Toast.makeText(MainActivity.this, "here", Toast.LENGTH_SHORT).show();
//                (getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG).onResume();
//                getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentByTag(MAIN_FRAG));
/*
               *//**//* Bundle b = new Bundle();
                b.putString("statusCode", statusCode);
                ((MainViewFragment)(getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG)))
                        .setArguments(b);


                Log.d("mainactivity", "frag count "+getFragmentManager().getBackStackEntryCount());
//                ((MainViewFragment)(getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG))).ErrorOccured(statusCode);
                Log.d("mainActivity", "Calling error method in main frag");
                ((MainViewFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG)).ErrorOccured(statusCode);*/
            }catch (Exception e){
                Log.d("mainActivity", "Error while calling error method in main frag");
                e.printStackTrace();
            }
        }
    }

    public void showToast(String statusCode){
        if(statusCode.equals("OVER_QUERY_LIMIT") || statusCode.equals("REQUEST_DENIED") || statusCode.equals("INVALID_REQUEST")){
            Log.d("mainFrag", statusCode);
            Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
        }else if(statusCode.equals("ZERO_RESULTS")){
            Log.d("mainFrag", "ZERO_RESULTS");
            Toast.makeText(MainActivity.this, "No results for the given location", Toast.LENGTH_SHORT).show();
        }else{
            Log.d("mainfrag", statusCode + " is the status code");
        }
    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onRestaurantFragmentInteraction(Uri uri) {

    }


     @Override
     public void OnEventsFragInteractionListener(Uri uri) {

     }
}

/*
Status code description
OK indicates that no errors occurred; the place was successfully detected and at least one result was returned.
ZERO_RESULTS indicates that the search was successful but returned no results. This may occur if the search was passed a latlng in a remote location.
OVER_QUERY_LIMIT indicates that you are over your quota.
REQUEST_DENIED indicates that your request was denied, generally because of lack of an invalid key parameter.
INVALID_REQUEST generally indicates that a required query parameter (location or radius) is missing.*/