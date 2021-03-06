package com.example.bharathramh.xplore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
//import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bharathramh.Adapters.MainListViewAdapter;
import com.example.bharathramh.StorageClassCollection.Weather;
import com.example.google.GoogleGeoLocAsync;
import com.example.storeData.LocalParse;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainViewFragment.MainViewOnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainViewFragment extends Fragment implements WeatherAsync.weatherAsyncListener,
        GoogleGeoLocAsync.GoogleGeoLocListener{

    public static int NETWORK_TIME_OUT_MILLIS = 10*1000;
    public static int GPS_TIME_OUT_MILLIS = 60*1000;

    private MainViewOnFragmentInteractionListener mListener;

    LocationManager mLocationManager;
    LocationListener mLocListener;
    EditText searchET;
    ListView listView;
    TextView currectPlaceText;
    Address currectSearchLocation;
    boolean firstTime;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ProgressDialog progressDialog;
    String currentProvider;
    TextView tempTextView;
    FrameLayout locDetailsFrame;
    boolean locationReceived;

    public static String[] mainItemsLists;
//    = {"Eateries", "Hotels", "Attractions", "Friends", "Events", "Movies"};
    String searchLocation="";
    String dispText;
    ImageView search, gpsImg, optionsImg;
    private Weather weather;

    public MainViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        firstTime = true;
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) getActivity().findViewById(R.id.left_drawer);
        mainItemsLists = getResources().getStringArray(R.array.mainMenuItemArray);
    }


    public void getLocation(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        //        GPS_PROVIDER
        if(!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Location access not enabled");
            builder.setMessage("Would you like to enable Location access");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

        }else{
            locationReceived = false;
            mLocListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("mainviewfrag", "on loc changed " + location);
                    progressDialog.dismiss();
                    locationReceived = true;
                    Address address = new Address(Locale.getDefault());
                    address.setLatitude(location.getLatitude());
                    address.setLongitude(location.getLongitude());
                    showDataAfterLocRetrieval(address, true);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("mainviewfrag", "status changed "+provider + status);
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d("mainviewfrag", "on provider enabled "+provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d("mainviewfrag", "provider disabled");
                    if (currentProvider.equals(LocationManager.NETWORK_PROVIDER)){
                        mLocationManager.removeUpdates(mLocListener);
                        Looper myLooper = Looper.myLooper();
                        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocListener, null);
                        currentProvider = (LocationManager.GPS_PROVIDER);
                        final Handler myHandler = new Handler(myLooper);
                        myHandler.postDelayed(new Runnable() {
                            public void run() {
                                if(!locationReceived) {
                                    Log.d("mainviewfrag", "gps timed out");
                                    Toast.makeText(getActivity(), getResources().
                                            getString(R.string.gps_time_out), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                                mLocationManager.removeUpdates(mLocListener);
                            }
                        }, GPS_TIME_OUT_MILLIS);
                    }else if (currentProvider.equals(LocationManager.GPS_PROVIDER)){
                        Toast.makeText(getActivity(), getResources().
                                getString(R.string.gps_time_out), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            };

//            Log.d("facebookLoginLoc", "using "+currentProvider);
            Log.d("mainviewfrag" , mLocationManager.getProviders(true) + " ");
//            List<String> providers = mLocationManager.getProviders(true);
//            for (String provider : providers) {
//                mLocationManager.requestSingleUpdate(provider, mLocListener, null);
//            }

            Looper myLooper = Looper.myLooper();
            try {
                /*if(mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {*/
                    currentProvider = LocationManager.NETWORK_PROVIDER;
                    mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocListener, null);
                    final Handler myHandler = new Handler(myLooper);
                    myHandler.postDelayed(new Runnable() {
                        public void run() {

                            if (!locationReceived && !currentProvider.equals(LocationManager.GPS_PROVIDER)) {

                                mLocationManager.removeUpdates(mLocListener);
                                Looper myLooper = Looper.myLooper();
                                mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocListener, null);
                                currentProvider = (LocationManager.GPS_PROVIDER);
                                final Handler myHandler = new Handler(myLooper);
                                myHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        if (!locationReceived && currentProvider.equals(LocationManager.GPS_PROVIDER)) {
                                            Log.d("mainviewfrag", "gps timed out");
                                            Toast.makeText(getActivity(), getResources().
                                                    getString(R.string.gps_time_out), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                        mLocationManager.removeUpdates(mLocListener);
                                    }
                                }, GPS_TIME_OUT_MILLIS);
                                Log.d("mainviewfrag", "network timed out");
                            }
                        }
                    }, NETWORK_TIME_OUT_MILLIS);
                /*}else if(mLocationManager.getProvider(LocationManager.GPS_PROVIDER) != null){
                    Looper myLooper1 = Looper.myLooper();
                    mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocListener, null);

                    currentProvider = (LocationManager.GPS_PROVIDER);
                    final Handler myHandler = new Handler(myLooper1);
                    myHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!locationReceived && currentProvider.equals(LocationManager.GPS_PROVIDER)) {
                                Log.d("mainviewfrag", "gps timed out");
                                Toast.makeText(getActivity(), getResources().
                                        getString(R.string.gps_time_out), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                            mLocationManager.removeUpdates(mLocListener);
                        }
                    }, GPS_TIME_OUT_MILLIS);
                    Log.d("mainviewfrag", "network timed out");
                }*/


//            mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                progressDialog.setMessage(getActivity().getResources().getString(R.string.loading_location));
                progressDialog.show();
            }catch (Exception e){
                e.printStackTrace();
                if(currentProvider.equals(LocationManager.NETWORK_PROVIDER)){

                    mLocationManager.removeUpdates(mLocListener);
                    Looper myLooper2 = Looper.myLooper();
                    mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocListener, null);
                    currentProvider = (LocationManager.GPS_PROVIDER);
                    final Handler myHandler = new Handler(myLooper2);
                    myHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!locationReceived && currentProvider.equals(LocationManager.GPS_PROVIDER)) {
                                Log.d("mainviewfrag", "gps timed out");
                                Toast.makeText(getActivity(), getResources().
                                        getString(R.string.gps_time_out), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                            mLocationManager.removeUpdates(mLocListener);
                        }
                    }, GPS_TIME_OUT_MILLIS);

                }else {
                    Toast.makeText(getActivity(), "No Location Providers Present", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    public void searchOnClick(){
        searchLocation = searchET.getText().toString();

        if(!searchLocation.equals("")){
            tempTextView.setText("");
            if(Geocoder.isPresent()) {
                new GoogleGeoLocAsync(MainViewFragment.this, getActivity()).execute(searchLocation);
            }else{
                Toast.makeText(getActivity(), "Geocoder not present for the device", Toast.LENGTH_SHORT).show();
            }
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) getView().findViewById(R.id.mainlistview);
        MainListViewAdapter adapter = new MainListViewAdapter(getActivity(), R.layout.main_items_container_list_view, mainItemsLists);
        listView.setAdapter(adapter);



//        {"Restaurant", "Hotels", "Attractions", "Friends NearBy", "Events", "Movies"};
        searchET = (EditText) getView().findViewById(R.id.searchLocation);
        currectPlaceText = (TextView) getView().findViewById(R.id.currentPlace);

        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    searchOnClick();
                }
                return false;
            }
        });

        search = (ImageView) getView().findViewById(R.id.searchImage);
        gpsImg = (ImageView) getView().findViewById(R.id.gpsImage);
        optionsImg = (ImageView) getView().findViewById(R.id.options);

        optionsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        });

        gpsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnClick();
            }
        });


        if(firstTime || currectSearchLocation == null) {
            listView.setVisibility(View.GONE);
            currectPlaceText.setVisibility(View.GONE);
            locDetailsFrame.setVisibility(View.GONE);
            firstTime = false;
        }else{
            currectPlaceText.setText(dispText);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("mainviewfragONlist", "item " + position + " clicked");
//                mListener.MainViewFragChoice(mainItemsLists[position], currectSearchLocation);
                Log.d("mainviewfrag", "obj is " + parent.getItemAtPosition(position));

                mListener.itemSelectedFromMainFrag(parent, view, position, id);

            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_view, container, false);
        locDetailsFrame = (FrameLayout) view.findViewById(R.id.mainViewLocDetails);
        tempTextView = (TextView) view.findViewById(R.id.tempText);
        if(weather != null){
            tempTextView.setText("Its "+ weather.getSummary() + " and is "+ weather.getTemperature() + "F");
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MainViewOnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void geoLocListener(Address searchLocation) {
        showDataAfterLocRetrieval(searchLocation, false);
    }

    public void showDataAfterLocRetrieval(Address searchLocation, boolean nearMe){
        if(searchLocation!=null && searchLocation.hasLatitude() && searchLocation.hasLongitude()){
//                (searchLocation.getLocality() != null || searchLocation.getCountryName()!=null)){
            new WeatherAsync(MainViewFragment.this).execute(searchLocation);
            searchET.setText("");
            if (nearMe){
                dispText = "Near Me";
            }else {
                dispText = (searchLocation.getLocality() != null) ? searchLocation.getLocality() + ", " : "";
                dispText = dispText + ((searchLocation.getCountryName() != null) ? searchLocation.getCountryName() : "");
            }
            currectPlaceText.setText(dispText);
            currectSearchLocation = searchLocation;
            mListener.updateCurrentLocation(currectSearchLocation);
            listView.setVisibility(View.VISIBLE);
            currectPlaceText.setVisibility(View.VISIBLE);
            locDetailsFrame.setVisibility(View.VISIBLE);
        }else {
            if(searchLocation!=null){
                Log.d("mainViewFrag", searchLocation.toString());
            }
            Toast.makeText(getActivity(), "No location found for the given place", Toast.LENGTH_SHORT).show();
        }

    }



    public boolean hasNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void weatherReceived(Weather weather) {
        this.weather = weather;
        if(weather != null){
            Log.d("mainViewFrag", weather.toString());
            tempTextView.setText("Its "+ weather.getSummary() + " and is "+ weather.getTemperature() + "F");
        }else{
            Log.d("mainViewFrag", "weather was null");
        }
    }


    public interface MainViewOnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void itemSelectedFromMainFrag(AdapterView<?> parent, View view, int position, long id);
        public void updateCurrentLocation(Address currectSearchLocation);
//        public void callRestFrag(ArrayList<GooglePlacesCS> result);
//        public void callFriendsActivity(Address location);
//        public void callEventsFrag(ArrayList result);
    }

}