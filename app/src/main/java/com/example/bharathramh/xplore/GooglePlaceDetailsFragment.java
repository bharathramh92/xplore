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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.storeData.LocalParse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GooglePlaceDetailsFragment.OnGooPlDetailsInteractionListener} interface
 * to handle interaction events.
 */
public class GooglePlaceDetailsFragment extends Fragment implements LocalParse.LocalParseInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GooglePlacesCS data;
    private GoogleApiClient mGoogleApiClient;
    private OnGooPlDetailsInteractionListener mListener;
    private TextView nameTV, addressTV, ratingTV, priceTV;
    private ImageView placeImage, weblogo, phonelogo, favlogo;

    public static GooglePlaceDetailsFragment instanceOf(GooglePlacesCS data){

        GooglePlaceDetailsFragment det = new GooglePlaceDetailsFragment();
        Bundle b = new Bundle();
        b.putSerializable("data", data);
        det.setArguments(b);
        return det;
    }

    public static GooglePlaceDetailsFragment instanceOf(GooglePlacesCS data, boolean fav){

        GooglePlaceDetailsFragment det = new GooglePlaceDetailsFragment();
        Bundle b = new Bundle();
        b.putBoolean("fav", fav);
        b.putSerializable("data", data);
        det.setArguments(b);
        return det;
    }

    public GooglePlaceDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.data = (GooglePlacesCS) getArguments().getSerializable("data");
        Log.d("gooplacedetails", data.toString()+" is the data");

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, data.getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            if(places.getCount()>0) {
                                Place place = places.get(0);
                                data.setWebsiteUri(place.getWebsiteUri());
                                data.setPhoneNumber(place.getPhoneNumber().toString());
                                data.setVicinity(place.getAddress().toString());
                                Log.d("gooplacedetails", "Place found: " + place.getName());
                                dispDetails();
                            }
                        }
                        places.release();
                    }
                });
        LocalParse.isIdPresent(data.getPlaceId(), this, false);
    }

    public void dispDetails(){
        LinearLayout l = (LinearLayout) getView().findViewById(R.id.gpdLinear);
//        private TextView typeTV, nameTV, websiteTV, addressTV, phoneTV, ratingTV, priceTV;\
//        int ht_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getActivity().getResources().getDisplayMetrics());
//        int wt_px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getActivity().getResources().getDisplayMetrics());
        String photoUrl ;
//        if(data.getLargePhotoURL() != null) {
        photoUrl = data.getLargePhotoURL();
//        }else{
//            photoUrl = "http://maps.googleapis.com/maps/api/streetview" +
//                    "?size=400x400&fov=90&heading=235&pitch=10&sensor=false&location" + data.getLat()+",%20"+data.getLng();
//        }
        if(photoUrl != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Picasso.with(getActivity()).load(photoUrl).
                    resize(metrics.widthPixels, metrics.heightPixels * 5 / 10).
                    into(placeImage);
        }else{
            placeImage.setImageResource(R.drawable.no_image);
        }

        nameTV.setText(data.getName().toString());

        weblogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("gpd", data.getWebsiteUri() + " is the web url");
                if(data.getWebsiteUri() != null && !data.getWebsiteUri().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, data.getWebsiteUri());
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.web_url_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(data.getVicinity() != null){
            addressTV.setText("Location :" + data.getVicinity().toString());
        }else{
            l.removeView(addressTV);
        }

        phonelogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("gpd", data.getPhoneNumber() + " is the phone number");
                if(data.getPhoneNumber() != null && !data.getPhoneNumber().equals("")) {
                    String number = "tel:" + data.getPhoneNumber().trim();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.phone_number_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });

        String rat = data.getRating() == null ? "Not available" : data.getRating().toString();
        ratingTV.setText("Rating :" +rat);

//        0 — Free
//        1 — Inexpensive
//        2 — Moderate
//        3 — Expensive
//        4 — Very Expensive
        String priceLevel = "";
        switch (data.getPriceLevel()){
            case 0:
                priceLevel = "Free";
                break;
            case 1:
                priceLevel = "Inexpensive";
                break;
            case 2:
                priceLevel = "Moderate";
                break;
            case 3:
                priceLevel = "Expensive";
                break;
            case 4:
                priceLevel = "Very Expensive";
                break;
            default:
                priceLevel = "Not found";
                break;
        }
        priceTV.setText("Price level :" + priceLevel);
        favlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalParse.isIdPresent(data.getPlaceId(), GooglePlaceDetailsFragment.this, true);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_place_details, container, false);
        nameTV = (TextView) view.findViewById(R.id.gpdName);
        weblogo = (ImageView) view.findViewById(R.id.gpdWebsiteLogo);
        addressTV = (TextView) view.findViewById(R.id.gpdAddress);
        phonelogo = (ImageView) view.findViewById(R.id.gpdPhoneNumberLogo);
        ratingTV = (TextView) view.findViewById(R.id.gpdRating);
        priceTV = (TextView) view.findViewById(R.id.gpdPriceLevel);
        placeImage = (ImageView) view.findViewById(R.id.gpdImage);
        favlogo = (ImageView) view.findViewById(R.id.favouriteLogo);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGooPlDetailsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public void onDestroy() {
        if(getArguments().getBoolean("fav") && !data.isFavFlag()){
         mListener.unfavourited(data.getPlaceId());
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void onConnected(Bundle bundle) {
        Log.d("gooplacedetails", "onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("gooplacedetails", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("gooplacedetails", "onConnectionFailed");
    }

    @Override
    public void favStored() {
        favlogo.setImageResource(R.drawable.ic_favorited);
        data.setFavFlag(true);
    }

    @Override
    public void favRemoved() {
        favlogo.setImageResource(R.drawable.ic_not_favorited);
        data.setFavFlag(false);
    }



    @Override
    public void favourite(boolean isPresent, String place_id, boolean removeOrAdd) {
        if(isPresent){
            if(removeOrAdd) {
                LocalParse.removePlaceId(place_id, GooglePlaceDetailsFragment.this);
            }else{
                favlogo.setImageResource(R.drawable.ic_favorited);
            }
        }else{
            if(removeOrAdd) {
                //storing a fav
                LocalParse.saveFavourites(data.getPlaceId(), GooglePlaceDetailsFragment.this);
            }else{
                favlogo.setImageResource(R.drawable.ic_not_favorited);
            }
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
    public interface OnGooPlDetailsInteractionListener {
        // TODO: Update argument type and name
        public void unfavourited(String place_id);
    }

}
