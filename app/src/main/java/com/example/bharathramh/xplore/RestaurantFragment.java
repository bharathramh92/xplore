package com.example.bharathramh.xplore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bharathramh.Adapters.RestaurantListViewAdapter;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;

import java.util.ArrayList;


/*
* *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantFragment.onRestaurantFragmentInteraction} interface
 * to handle interaction events.
 */
public class RestaurantFragment extends Fragment {

    private RestaurantFragListener mListener;
    ArrayList<GooglePlacesCS> data;
    RestaurantListViewAdapter adapter;
    ListView listView;
    Activity rootActivity;


    public RestaurantFragment() {
        // Required empty public constructor
    }


    public static RestaurantFragment instance(ArrayList<GooglePlacesCS> result){
        RestaurantFragment f = new RestaurantFragment();
        Bundle _data = new Bundle();
        _data.putSerializable("mylist", result);
        f.setArguments(_data);



         return f;
    }


    public void setData(ArrayList data){
        this.data = data;
        Log.d("restFrag", data.toString());
        if(data!=null){

        }else{
            Log.d("restFrag", "data is null");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        Log.d("restFrag", "onactivity created");

        listView = (ListView) rootActivity.findViewById(R.id.restaurantListView);
        adapter = new RestaurantListViewAdapter(rootActivity, R.layout.restaurant_list_container, (ArrayList) getArguments().getSerializable("mylist"));
        listView.setAdapter(adapter);

        /*try {
            listView = (ListView) getView().findViewById(R.id.restaurantListView);
            adapter = new RestaurantListViewAdapter(getActivity(), R.layout.restaurant_list_container, data);
            listView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        rootActivity = getActivity();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRestaurantFragmentInteraction(uri);
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
                    getActivity().getSupportFragmentManager().beginTransaction().remove(RestaurantFragment.this).commit();
                    getFragmentManager().popBackStack();
                }
            });
            AlertDialog alertDialogNetwork = builder.create();
            alertDialogNetwork.show();
        }
    }

    @Override
    public void onPause() {
        Log.d("restFrag", "paused");

        super.onPause();

    }

    @Override
    public void onDestroy() {
        Log.d("restFrag", "destroy");
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RestaurantFragListener) activity;
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


    public interface RestaurantFragListener {
        // TODO: Update argument type and name
        public void onRestaurantFragmentInteraction(Uri uri);

    }
}
