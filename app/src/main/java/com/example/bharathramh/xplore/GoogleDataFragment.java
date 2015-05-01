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
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.bharathramh.Adapters.GoogleDataListViewAdapter;
import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;

import java.util.ArrayList;


/*
* *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoogleDataFragment.onGoogleDataFragmentInteraction} interface
 * to handle interaction events.
 */
public class GoogleDataFragment extends Fragment {

    private GoogleDataFragListener mListener;
    ArrayList<GooglePlacesCS> data;
    GoogleDataListViewAdapter adapter;
    ListView listView;
    Activity rootActivity;


    public GoogleDataFragment() {
        // Required empty public constructor
    }


    public static GoogleDataFragment instance(ArrayList<GooglePlacesCS> result){
        GoogleDataFragment f = new GoogleDataFragment();
        Bundle _data = new Bundle();
        _data.putSerializable("mylist", result);
        f.setArguments(_data);



         return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = (ArrayList) getArguments().getSerializable("mylist");

        Log.d("restFrag", "onactivity created");

        listView = (ListView) rootActivity.findViewById(R.id.restaurantListView);
        adapter = new GoogleDataListViewAdapter(rootActivity, R.layout.google_data_list_container, data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onGoogleDetailsSelected(data.get(position));
            }
        });
        /*try {
            listView = (ListView) getView().findViewById(R.id.restaurantListView);
            adapter = new GoogleDataListViewAdapter(getActivity(), R.layout.google_data_list_container, data);
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
            mListener.onGoogleDataFragmentInteraction(uri);
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
                    getActivity().getSupportFragmentManager().beginTransaction().remove(GoogleDataFragment.this).commit();
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
            mListener = (GoogleDataFragListener) activity;
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


    public interface GoogleDataFragListener {
        // TODO: Update argument type and name
        public void onGoogleDataFragmentInteraction(Uri uri);
        public void onGoogleDetailsSelected(GooglePlacesCS data);
    }
}
