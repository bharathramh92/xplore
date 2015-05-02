package com.example.bharathramh.xplore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.google.ConstantsGoogle;
import com.example.storeData.LocalParse;
import com.example.support.RequestParams;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.HashMap;


public class FavouriteActivity extends ActionBarActivity implements GooglePlaceDetailsFragment.OnGooPlDetailsInteractionListener,
        LocalParse.LocalParseRetreivalInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleDataFragment.GoogleDataFragListener{

    private GoogleApiClient mGoogleApiClient;
    ArrayList<GooglePlacesCS> data;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        LocalParse.retreiveAllPlaceIds(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void retrieved(final ArrayList<String> placeIds) {
        data = new ArrayList<>();
        if(placeIds.size() == 0){
            Toast.makeText(this, getResources().getString(R.string.favourite_nil_message), Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.d("gooplacedetails", "Places found: " + placeIds.toString());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading_data));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        for(String x : placeIds) {

            Places.GeoDataApi.getPlaceById(mGoogleApiClient, x)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                if (places.getCount() > 0) {
                                    GooglePlacesCS obj = new GooglePlacesCS();
                                    Place place = places.get(0);
                                    obj.setPlaceId(place.getId());
                                    obj.setName(place.getName().toString());
                                    obj.setRating(Double.parseDouble(place.getRating() + ""));
                                    obj.setPriceLevel(place.getPriceLevel());
                                    obj.setWebsiteUri(place.getWebsiteUri());
                                    obj.setPhoneNumber(place.getPhoneNumber().toString());
                                    obj.setVicinity(place.getAddress().toString());
                                    Log.d("gooplacedetails", "Place found: " + place.getName());
                                    Log.d("gooplacedetails", "obj: " + obj.toString());
                                    data.add(obj);
                                    if(data.size() == placeIds.size()){
                                        GoogleDataFragment restFrag = GoogleDataFragment.instance(data);
                                        progressDialog.dismiss();
                                        getSupportFragmentManager().beginTransaction().
                                                replace(R.id.favContainer, restFrag, "Favourites")
                                                .commit();
                                    }
                                }
                            }
                            places.release();
                        }
                    });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onGoogleDetailsSelected(GooglePlacesCS data) {
        if(data != null) {
            GooglePlaceDetailsFragment goo = GooglePlaceDetailsFragment.instanceOf(data, true);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.favContainer, goo, MainActivity.PLACE_DETAILS_GOOGLE)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void unfavourited(String place_id) {
        GoogleDataFragment goo =
                (GoogleDataFragment) getSupportFragmentManager().findFragmentByTag("Favourites");
        if(goo != null){
            goo.remove(place_id);
        }
    }
    public boolean hasNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = cm.getActiveNetworkInfo();
        if(nwInfo!=null &&nwInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!hasNetwork()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Data not enabled");
            builder.setMessage("Would you like to enable the Internet connection");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
