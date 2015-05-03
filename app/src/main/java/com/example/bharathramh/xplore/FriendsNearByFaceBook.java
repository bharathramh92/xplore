package com.example.bharathramh.xplore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.JSON.FacebookFriendsJSONUtils;
import com.example.bharathramh.Adapters.NearByFriendsAdapter;
import com.example.bharathramh.StorageClassCollection.FacebookFriendsData;
import com.example.storeData.StoreDataInServer;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


import org.json.JSONArray;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsNearByFaceBook.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FriendsNearByFaceBook extends Fragment implements PopulateNearByFriendsAsync.nearByFriendsListener{

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
    LoginButton facebookLogBtn;
    TextView notLoggedInText;
    ListView listView;
    ArrayList<FacebookFriendsData> data;
    NearByFriendsAdapter adapter;
    ProgressDialog progressDialog;

    public void friendsListReq(){
        Profile profile = Profile.getCurrentProfile();

        if(profile!=null){
            Log.d("facebooklogin", "Accessing from id: "+profile.getId());
        }else{
            return;
        }

        GraphRequest request ;
        request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
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
                    b.putParcelable("AccessToken", AccessToken.getCurrentAccessToken());
                    new PopulateNearByFriendsAsync(FriendsNearByFaceBook.this, getActivity()).execute(b);

                }
            }
        });
        request.executeAsync();
        notLoggedInText.setText(getResources().getString(R.string.facebook_loading_data));
        notLoggedInText.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.loading_friends));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.d("facebooklogin", "here");

    }

    public FriendsNearByFaceBook() {
        // Required empty public constructor
    }


    public static FriendsNearByFaceBook instance(Address address){
        FriendsNearByFaceBook f = new FriendsNearByFaceBook();
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
        View view = inflater.inflate(R.layout.fragment_friends_near_by_face_book, container, false);
        notLoggedInText = (TextView) view.findViewById(R.id.notLoggedInTextView);
        listView = (ListView) view.findViewById(R.id.facebookFriendsLocationListView);

        listView.setVisibility(View.GONE);

        LoginButton facebookLogBtn = (LoginButton) view.findViewById(R.id.login_button);
        facebookLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Profile.getCurrentProfile() == null) {
                    Log.d("facebookLoginCV", "Login pressed");
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(FriendsNearByFaceBook.this,
                            new ArrayList<String>() {{add("user_friends");}}, new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException err) {
                                    if (user == null) {
                                        notLoggedInText.setVisibility(View.VISIBLE);
                                        notLoggedInText.setText(getResources().getString(R.string.facebook_not_logged_in_msg));
                                        listView.setVisibility(View.GONE);
                                        if(data != null){
                                            data.clear();
                                        }
                                        if(adapter!=null){
                                            adapter.notifyDataSetChanged();
                                        }

                                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                    } else if (user.isNew()) {

//                    successToken = user.getSessionToken();
//                    successToken = user.get
                                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                                    } else {
//                    successToken = user.getSessionToken();

                                        Log.d("MyApp", "User logged in through Facebook!");
                                    }
                                    dispName();
                                }
                            });

                }else{
                    Log.d("facebookLoginCV", "Logout pressed here");

                }
            }
        });
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
            friendsListReq();
        }else{
            tx.setText("Welcome");
            Log.d("facebookLogin", "disp name profile was null");
        }
    }catch (Exception e){
        e.printStackTrace();
    }
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        /* FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
*/          tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken old, AccessToken accessToken) {
                Log.d("facebookloginoncreate", "access changed "+ accessToken);
                successToken = accessToken;
                Profile.fetchProfileForCurrentAccessToken();
                Log.d("facebookloginoncreate", "profile is  "+ Profile.getCurrentProfile());

            }
        };

        tracker.startTracking();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged( Profile oldProfile, Profile currentProfile) {

                Log.d("facebookloginoncreate", "profile is "+ currentProfile + "getcurrent profile is "+ Profile.getCurrentProfile());
                if(currentProfile!=null){
                    Log.d("facebookloginoncreate",currentProfile.getProfilePictureUri(100,100)+" is the pic");
                }else{
                    Log.d("facebookloginoncreate", "profile was null");
                    notLoggedInText.setVisibility(View.VISIBLE);
                    notLoggedInText.setText(getResources().getString(R.string.facebook_not_logged_in_msg));
                    listView.setVisibility(View.GONE);
                    ParseUser.logOut();
                    dispName();
                }

            }
        };

        profileTracker.startTracking();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("facebookLogin", "onactivityresult " + requestCode+ " "+ resultCode + " "+ data);
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
    public void onPause() {
        Log.d("mainviewfrag", "onPause");
        super.onPause();

    }

    @Override
    public void nearByFriendsDataRetrieved(ArrayList dataFB) {
        progressDialog.dismiss();
        this.data = dataFB;
        if(data!=null && data.size()>0){
            Log.d("facebookLogindata", data.toString());
            notLoggedInText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter = new NearByFriendsAdapter(getActivity(), R.layout.friends_nearby_details_container, data);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                        if (versionCode >= 3002850) {
                            Uri uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/" + data.get(position).getId());
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        } else {
                            // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+data.get(position).getId())));
                        }
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+data.get(position).getId()));
//                        startActivity(intent);
                    } catch (Exception e) {
                        Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + data.get(position).getId()));
                        startActivity(intent);
                    }
                }
            });


        }else{
            notLoggedInText.setText(getResources().getString(R.string.facebook_no_friends_present));
            Log.d("facebookLoginData", "Data was null");
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
                    getActivity().getSupportFragmentManager().beginTransaction().remove(FriendsNearByFaceBook.this).commit();
                    getFragmentManager().popBackStack();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

//        dispName();

        Log.d("facebookLogin", "onResume");

        /*String currentProvider = LocationManager.NETWORK_PROVIDER;
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
        }*/


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

//            Log.d("facebookLoginLoc", "using "+currentProvider);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 0, 25000, mLocListener);


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
