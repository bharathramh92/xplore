package com.example.bharathramh.xplore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
//import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bharathramh.Adapters.MainListViewAdapter;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.google.GoogleGeoLocAsync;
import com.example.google.PlacesGoogleAsync;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainViewFragment.MainViewOnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainViewFragment extends Fragment implements GoogleGeoLocAsync.GoogleGeoLocListener, PlacesGoogleAsync.GooglePlacesInterface{

    private MainViewOnFragmentInteractionListener mListener;

    EditText searchET;
    ListView listView;
    TextView currectPlaceText;
    Address currectSearchLocation;

    public static String[] mainItemsLists = {"Eateries", "Hotels", "Attractions", "Friends NearBy", "Events", "Movies"};
    String searchLocation="";
    String dispText;
//

    public MainViewFragment() {
        // Required empty public constructor
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
                    searchLocation = searchET.getText().toString();

                    if(!searchLocation.equals("")){
                        new GoogleGeoLocAsync(MainViewFragment.this, getActivity()).execute(searchLocation);
                    }
                }
                return false;
            }
        });

        if(searchLocation.equals("")) {
            listView.setVisibility(View.GONE);
            currectPlaceText.setVisibility(View.GONE);
        }else{
            currectPlaceText.setText(dispText);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("mainviewfragONlist", "item " + position + " clicked");
//                mListener.MainViewFragChoice(mainItemsLists[position], currectSearchLocation);


                if(position==0) {
                    PlacesGoogleAsync placesAsync = new PlacesGoogleAsync(MainViewFragment.this , getActivity(),currectSearchLocation);
                    //first search for restaurants
                    placesAsync.execute(constants.GOOGLE_EATERIES);
                }else if(position==1){
                    PlacesGoogleAsync placesAsync = new PlacesGoogleAsync(MainViewFragment.this , getActivity(),currectSearchLocation);
                    placesAsync.execute(constants.GOOGLE_HOTEL);
                }else if(position==2){
                    PlacesGoogleAsync placesAsync = new PlacesGoogleAsync(MainViewFragment.this , getActivity(),currectSearchLocation);
                    placesAsync.execute(constants.GOOGLE_ATTRACTIONS);
                }else if(position==3){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Approval");
                    builder.setMessage("This feature uses facebook and " +
                            "we will store your location information in our servers." +
                            "Press OK if you agree.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.callFriendsActivity(currectSearchLocation);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else if(position==5){
//                    PlacesGoogleAsync placesAsync = new PlacesGoogleAsync(MainViewFragment.this , getActivity(),currectSearchLocation);
//                    placesAsync.execute(constants.GOOGLE_MOVIES);
                }
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_view, container, false);
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
        if(searchLocation!=null && searchLocation.hasLatitude() && searchLocation.hasLongitude() &&
                (searchLocation.getLocality() != null || searchLocation.getCountryName()!=null)){
            searchET.setText("");

            dispText = (searchLocation.getLocality()!=null)?searchLocation.getLocality()+", ":"";
            dispText = dispText + ((searchLocation.getCountryName()!=null)?searchLocation.getCountryName():"");
            currectPlaceText.setText(dispText);

            listView.setVisibility(View.VISIBLE);
            currectPlaceText.setVisibility(View.VISIBLE);
            currectSearchLocation = searchLocation;
        }else {
            if(searchLocation!=null){
                Log.d("mainViewFrag", searchLocation.toString());
            }
            Toast.makeText(getActivity(), "No location found for the given place", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void placesQueryListener(ArrayList<GooglePlacesCS> result, String statusCode, String nextTokenString) {
            if(result!=null && statusCode.equals("OK") && result.size()>0) {
                Log.d("mainActivity", result.toString());
                Log.d("mainActivity", "status Code" + statusCode + "nextToken" + nextTokenString);

                mListener.callRestFrag(result);
            }else if(!statusCode.equals("OK")){
                try{
                    if(statusCode.equals("OVER_QUERY_LIMIT") || statusCode.equals("REQUEST_DENIED") || statusCode.equals("INVALID_REQUEST")){
                        Log.d("mainFrag", statusCode);
                        Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
                    }else if(statusCode.equals("ZERO_RESULTS")){
                        Log.d("mainFrag", "ZERO_RESULTS");
                        Toast.makeText(getActivity(), "No results for the given location", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("mainfrag", statusCode + " is the status code");
                    }

                }catch (Exception e){
                    Log.d("mainActivity", "Error while calling error method in main frag");
                    e.printStackTrace();
                }
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
                    getActivity().getSupportFragmentManager().beginTransaction().remove(MainViewFragment.this).commit();
                    getFragmentManager().popBackStack();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

        public interface MainViewOnFragmentInteractionListener {
        // TODO: Update argument type and name

        public void callRestFrag(ArrayList<GooglePlacesCS> result);
        public void callFriendsActivity(Address location);
    }

}
