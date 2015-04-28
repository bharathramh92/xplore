package com.example.bharathramh.xplore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.tv.TvInputService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;


import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.JSON.FacebookFriendsJSONUtils;
import com.example.bharathramh.Adapters.NearByFriendsAdapter;
import com.example.bharathramh.Adapters.RestaurantListViewAdapter;
import com.example.bharathramh.StorageClassCollection.FacebookFriendsData;
import com.example.storeData.QueryFromServer;
import com.example.storeData.StoreDataInServer;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FaceBookLogin.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FaceBookLogin extends Fragment implements PopulateNearByFriendsAsync.nearByFriendsListener{

    private OnFragmentInteractionListener mListener;
    LoginButton loginButton;
    CallbackManager callbackManager;
    AccessToken successToken;
    AccessTokenTracker tracker;
    ProfileTracker profileTracker;
    LocationManager mLocationManager;
    LocationListener mLocListener;
    Location mLocation;
    Boolean  viewCreated = false, friendsDataRetrieved = false;


    public void friendsListReq(){
        Profile profile = Profile.getCurrentProfile();

        if(profile!=null){
            Log.d("facebooklogin", "Accessing from id: "+profile.getId());
        }

        GraphRequest request ;

        request = GraphRequest.newMyFriendsRequest(successToken, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                if(jsonArray!=null){
                    ArrayList<FacebookFriendsData> data =  FacebookFriendsJSONUtils.formatFriendsData(jsonArray.toString());
                    Log.d("facebookLogin", "friends data is " + jsonArray.toString() );
                    ArrayList<Long> fbIds = new ArrayList<Long>();
                    for(FacebookFriendsData temp : data){
                        fbIds.add(temp.getId());
                    }
                    Address queryAddress = getArguments().getParcelable("location");
                    Log.d("facebookLogin", "querying for "+ queryAddress.getLatitude()+" " +queryAddress.getLongitude()+ " "+fbIds);
                    Bundle b = new Bundle();
                    b.putDouble("latitude",queryAddress.getLatitude() );
                    b.putDouble("longitude",queryAddress.getLongitude() );
                    b.putSerializable("fbIds", fbIds);
                    b.putParcelable("AccessToken", successToken);
                    new PopulateNearByFriendsAsync(FaceBookLogin.this).execute(b);

                }
            }
        });
        request.executeAsync();

    }

    public FaceBookLogin() {
        // Required empty public constructor
    }


    public static FaceBookLogin instance(Address address){
        FaceBookLogin f = new FaceBookLogin();
        Bundle _data = new Bundle();
        _data.putParcelable("location", address);
        f.setArguments(_data);
        return f;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewCreated = true;
        dispName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_face_book_login, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

public void dispName(){
    try {
        TextView tx = (TextView) getView().findViewById(R.id.facebookTextView);
        if(Profile.getCurrentProfile()!=null) {
            tx.setText("Welcome, " + Profile.getCurrentProfile().getName());
        }else{
            tx.setText("Welcome");
        }
    }catch (Exception e){
        e.printStackTrace();
    }
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> s = new ArrayList<>();
        s.add("user_friends");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, s, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {

                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {

                    dispName();
//                    successToken = user.getSessionToken();
//                    successToken = user.get
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
//                    successToken = user.getSessionToken();

                    dispName();
                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });


        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        /* FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
*/


       tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken old, AccessToken accessToken) {
                Log.d("facebooklogin", "access changed "+ accessToken);
                successToken = accessToken;
                Profile.fetchProfileForCurrentAccessToken();
                Log.d("facebookLogin", "profile is  "+ Profile.getCurrentProfile());
                friendsListReq();
            }
        };

        tracker.startTracking();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged( Profile oldProfile, Profile currentProfile) {

                Log.d("facebooklogin", "profile is "+ currentProfile + "getcurrent profile is "+ Profile.getCurrentProfile());
                if(currentProfile!=null){
                    Log.d("facebookloginProfilePic",currentProfile.getProfilePictureUri(100,100)+" is the pic");
                }else{

                    Log.d("facebooklogin", "profile was null");
                }

            }
        };

        profileTracker.startTracking();


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tracker.stopTracking();
        profileTracker.stopTracking();
        mListener = null;
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

        if(!hasNetwork()){
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
                    getActivity().getSupportFragmentManager().beginTransaction().remove(FaceBookLogin.this).commit();
                    getFragmentManager().popBackStack();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        dispName();

        Log.d("facebookLogin", "onResume");

        String currentProvider = LocationManager.NETWORK_PROVIDER;
        if(currentProvider==null || currentProvider.equals("")){
            currentProvider = LocationManager.GPS_PROVIDER;
            if(currentProvider==null || currentProvider.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("InCompatible device");
                builder.setMessage("This feature is not available for you. " +
                        "Your device does not have a location provider.");
                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(FaceBookLogin.this).commit();
                        getFragmentManager().popBackStack();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            }


//        GPS_PROVIDER
        if(!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

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

            mLocListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    mLocation = location;
                    Log.d("facebookLoginLoc", "location change " + location);
                    if(Profile.getCurrentProfile()!=null){
                        try {
                            Log.d("facebookLoginLoc", "Profile is "+Profile.getCurrentProfile().getName());
                            Log.d("facebookLoginLoc", "trying to save "+Long.parseLong(Profile.getCurrentProfile().getId())+" "+ mLocation.getLatitude()+" "+ mLocation.getLongitude()+" "+ Profile.getCurrentProfile().getName());

                            StoreDataInServer.UpdateLocationData(Long.parseLong(Profile.getCurrentProfile().getId()), mLocation.getLatitude(), mLocation.getLongitude(), Profile.getCurrentProfile().getName());

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("facebookLoginLoc", "Error while saving data");
                        }
                    }else{
                        Log.d("facebookLoginLoc", "Error while saving data, profile could be null "+Profile.getCurrentProfile());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            Log.d("facebookLoginLoc", "using "+currentProvider);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 0, 25000, mLocListener);


        }
    }

    @Override
    public void nearByFriendsDataRetrieved(ArrayList data) {
        if(data!=null && data.size()>0){
            Log.d("facebookLogindata", data.toString());
            ListView listView = (ListView) getView().findViewById(R.id.facebookFriendsLocationListView);
            NearByFriendsAdapter adapter = new NearByFriendsAdapter(getActivity(), R.layout.friends_nearby_details_container, data);
            listView.setAdapter(adapter);

        }else{

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
    }

}
