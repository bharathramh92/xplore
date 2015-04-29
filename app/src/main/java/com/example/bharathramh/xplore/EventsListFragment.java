package com.example.bharathramh.xplore;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bharathramh.Adapters.EventsListViewAdapter;
import com.example.bharathramh.StorageClassCollection.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EventsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static EventsListFragment instance(ArrayList result){
        EventsListFragment f = new EventsListFragment();
        Bundle _data = new Bundle();
        _data.putSerializable("events", result);
        f.setArguments(_data);
        return f;
    }

    public EventsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.eventsListView);
        ArrayList<Event> events = (ArrayList<Event>) getArguments().getSerializable("events");
        EventsListViewAdapter adapter = new EventsListViewAdapter(getActivity(), R.layout.events_container, events);
        listView.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnEventsFragInteractionListener(uri);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void OnEventsFragInteractionListener(Uri uri);
    }

}
