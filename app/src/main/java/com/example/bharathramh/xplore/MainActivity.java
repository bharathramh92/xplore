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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bharathramh.StorageClassCollection.Event;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.google.PlacesGoogleAsync;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements EventsAsyncTask.EventsListener,
        EventsListFragment.OnFragmentInteractionListener,
        FaceBookLogin.OnFragmentInteractionListener,
        RestaurantFragment.RestaurantFragListener,
        PlacesGoogleAsync.GooglePlacesInterface, MainViewFragment.MainViewOnFragmentInteractionListener
        {
            public static String HOME_FRAG="Hotels";
            public static String EATERIES_FRAG="Eateries";
            public static String ATTRACTIONS_FRAG="Attractions";
            public static String HOTELS_FRAG="Hotels";
            public static String MOVIES_FRAG="Movies";
            public static String FACEBOOK_FRAG="Friends NearBy";
            public static String EVENTS_FRAG = "Events";

            ArrayList<GooglePlacesCS> data = null;
            Bundle userData;
            private String[] mItemTitles;
            private DrawerLayout mDrawerLayout;
            private ListView mDrawerList;
            Address currentSearchLocation;
            private ActionBarDrawerToggle mDrawerToggle;
            public String currentFragment, nextFragment;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                mItemTitles = getResources().getStringArray(R.array.itemArray);
    //            String[] mItemTitles = {"Eateries", "Hotels", "Attractions", "Friends NearBy", "Events", "Movies"};
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerList = (ListView) findViewById(R.id.left_drawer);
                mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mItemTitles));
                mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

                ActionBar actionBar = getSupportActionBar();
                actionBar.hide();
                currentFragment = HOME_FRAG;
                getSupportFragmentManager().beginTransaction().
                        add(R.id.mainContainer, new MainViewFragment(), currentFragment)
                        .commit();
                nextFragment = "";

            }



             public class DrawerItemClickListener implements ListView.OnItemClickListener {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ItemSelected(parent, view, position, id);
                     mDrawerLayout.closeDrawer(mDrawerList);
                 }
             }

             private void ItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String selectedOption = parent.getItemAtPosition(position).toString();



                 if(currentSearchLocation != null) {

                     if(!currentFragment.equals(HOME_FRAG) && !currentFragment.equals(selectedOption)){
                         Log.d("mainactivity", "back stack count "+ getSupportFragmentManager().getBackStackEntryCount());
                         if(getSupportFragmentManager().getBackStackEntryCount()>0){
                             Log.d("mainactivity", "popping "+ currentFragment);
                             getSupportFragmentManager().popBackStackImmediate();
                         }
                     }

                     switch (selectedOption) {

                         case "Home":
                             if(getSupportFragmentManager().getBackStackEntryCount()>0){
                                 Log.d("mainactivity", "popping "+ currentFragment);
                                 getSupportFragmentManager().popBackStackImmediate();
                             }
                             break;

                         case "Eateries":
                             if (currentFragment.equals(EATERIES_FRAG)){
                                break;
                         }
                             PlacesGoogleAsync placesAsyncE = new PlacesGoogleAsync(MainActivity.this, this, currentSearchLocation);
                             //first search for restaurants
                             placesAsyncE.execute(constants.GOOGLE_EATERIES, EATERIES_FRAG);
                             nextFragment = EATERIES_FRAG;
                             break;
                         case "Hotels":
                             if (currentFragment.equals(HOTELS_FRAG)){
                                 break;
                             }
                             PlacesGoogleAsync placesAsyncH = new PlacesGoogleAsync(MainActivity.this, this, currentSearchLocation);
                             placesAsyncH.execute(constants.GOOGLE_HOTEL, HOTELS_FRAG);
                             nextFragment = HOTELS_FRAG;
                             break;
                         case "Attractions":
                             if (currentFragment.equals(ATTRACTIONS_FRAG)){
                                 break;
                             }
                             PlacesGoogleAsync placesAsyncA = new PlacesGoogleAsync(MainActivity.this, this, currentSearchLocation);
                             placesAsyncA.execute(constants.GOOGLE_ATTRACTIONS, ATTRACTIONS_FRAG);
                             nextFragment = ATTRACTIONS_FRAG;
                             break;
                         case "Friends NearBy":
                             if (currentFragment.equals(FACEBOOK_FRAG)){
                                 break;
                             }
                             AlertDialog.Builder builder = new AlertDialog.Builder(this);
                             builder.setTitle("Approval");
                             builder.setMessage("This feature uses facebook and " +
                                     "we will store your location information in our servers." +
                                     "Press OK if you agree.");
                             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     FaceBookLogin f = FaceBookLogin.instance(currentSearchLocation);
                                     currentFragment = FACEBOOK_FRAG;
                                     getSupportFragmentManager().beginTransaction().
                                             replace(R.id.mainContainer, f, currentFragment)
                                             .addToBackStack(null)
                                             .commit();
                                     nextFragment = FACEBOOK_FRAG;
                                 }
                             });
                             builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {

                                 }
                             });

                             AlertDialog alertDialog = builder.create();
                             alertDialog.show();
                             break;
                         case "Events":
                             if (currentFragment.equals(EVENTS_FRAG)){
                                 break;
                             }
                             EventsAsyncTask eventsAsync = new EventsAsyncTask(MainActivity.this, currentSearchLocation);
                             eventsAsync.execute(constants.EVENTS_CATEGORY);
                             nextFragment = EVENTS_FRAG;
                             break;
                         case "Movies":
                             if (currentFragment.equals(MOVIES_FRAG)){
                                 break;
                             }
                             PlacesGoogleAsync placesAsyncM = new PlacesGoogleAsync(MainActivity.this, this, currentSearchLocation);
                             placesAsyncM.execute(constants.GOOGLE_MOVIES, MOVIES_FRAG);
                             nextFragment = MOVIES_FRAG;
                             break;
                     }


                 }
             }


             @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_main, menu);
                return true;
            }


             @Override
             public void itemSelectedFromMainFrag(AdapterView<?> parent, View view, int position, long id) {
                ItemSelected(parent, view, position, id);
             }

             @Override
             public void updateCurrentLocation(Address currectSearchLocation) {
                 this.currentSearchLocation = currectSearchLocation;
             }



             @Override
             public void placesQueryListener(ArrayList<GooglePlacesCS> result, String statusCode, String nextTokenString, String from) {
                 if(result!=null && statusCode.equals("OK") && result.size()>0) {
                     Log.d("mainActivity", result.toString());
                     Log.d("mainActivity", "status Code" + statusCode + "nextToken" + nextTokenString);
                     RestaurantFragment restFrag = RestaurantFragment.instance(result);
                     currentFragment = from;
                     getSupportFragmentManager().beginTransaction().
                             replace(R.id.mainContainer, restFrag, currentFragment)
                             .addToBackStack(null)
                             .commit();
                 }else if(!statusCode.equals("OK")){
                     try{
                         if(statusCode.equals("OVER_QUERY_LIMIT") || statusCode.equals("REQUEST_DENIED") || statusCode.equals("INVALID_REQUEST")){
                             Log.d("mainFrag", statusCode);
                             Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
                         }else if(statusCode.equals("ZERO_RESULTS")){
                             Log.d("mainFrag", "ZERO_RESULTS");
                             Toast.makeText(this, "No results for the given location", Toast.LENGTH_SHORT).show();
                         }else{
                             Log.d("mainfrag", statusCode + " is the status code");
                         }

                     }catch (Exception e){
                         Log.d("mainActivity", "Error while calling error method in main frag");
                         e.printStackTrace();
                     }
                 }

             }

             /*@Override
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
        *//*
                       *//**//**//**//* Bundle b = new Bundle();
                        b.putString("statusCode", statusCode);
                        ((MainViewFragment)(getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG)))
                                .setArguments(b);


                        Log.d("mainactivity", "frag count "+getFragmentManager().getBackStackEntryCount());
        //                ((MainViewFragment)(getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG))).ErrorOccured(statusCode);
                        Log.d("mainActivity", "Calling error method in main frag");
                        ((MainViewFragment)getFragmentManager().findFragmentByTag(MainActivity.MAIN_FRAG)).ErrorOccured(statusCode);*//*
                    }catch (Exception e){
                        Log.d("mainActivity", "Error while calling error method in main frag");
                        e.printStackTrace();
                    }
                }
            }*/

           /* public void showToast(String statusCode){
                if(statusCode.equals("OVER_QUERY_LIMIT") || statusCode.equals("REQUEST_DENIED") || statusCode.equals("INVALID_REQUEST")){
                    Log.d("mainFrag", statusCode);
                    Toast.makeText(MainActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }else if(statusCode.equals("ZERO_RESULTS")){
                    Log.d("mainFrag", "ZERO_RESULTS");
                    Toast.makeText(MainActivity.this, "No results for the given location", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("mainfrag", statusCode + " is the status code");
                }
            }*/

            @Override
            public void onBackPressed() {
                if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                    mDrawerLayout.closeDrawer(mDrawerList);
                }else{
                    if(getSupportFragmentManager().getBackStackEntryCount()>0){
                        currentFragment = HOME_FRAG;
                        mDrawerLayout.closeDrawer(mDrawerList);
                        getSupportFragmentManager().popBackStack();
                    }else{
                        super.onBackPressed();
                    }
                }
            }

            @Override
            public void eventsDataRetrieved(ArrayList<Event> eventsList) {

             if(eventsList != null && eventsList.size() >0){
                 Log.d("mainviewfrag", eventsList.toString());
                 EventsListFragment f = EventsListFragment.instance(eventsList);
                 currentFragment = EVENTS_FRAG;
                 getSupportFragmentManager().beginTransaction().
                         replace(R.id.mainContainer, f , MainActivity.EVENTS_FRAG)
                         .addToBackStack(null)
                         .commit();
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