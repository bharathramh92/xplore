package com.example.bharathramh.Adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharathramh.StorageClassCollection.GooglePlacesCS;
import com.example.bharathramh.xplore.R;
import com.example.google.ConstantsGoogle;
import com.example.support.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bharathramh on 4/15/15.
 */
public class GoogleDataListViewAdapter extends ArrayAdapter {

    Context mContext;
    int mResource;
    ArrayList<GooglePlacesCS> objects;

    public GoogleDataListViewAdapter(Context context, int resource, ArrayList objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource , parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.imageViewRestaurantContainer);
            holder.rating = (TextView) convertView.findViewById(R.id.ratingRestaurantContainer);
            holder.vicinity = (TextView) convertView.findViewById(R.id.vicinityRestaurantContainer);
            holder.title = (TextView) convertView.findViewById(R.id.nameRestaurantContainer);
            convertView.setTag(holder);
        }
        GooglePlacesCS current = objects.get(position);
        holder = (Holder) convertView.getTag();
        ImageView icon = holder.icon;
        if(current.getPhotoReference()!=null && current.getPhotoReference()!="") {
            HashMap<String, String> tmp = new HashMap<>();
            tmp.put("key",ConstantsGoogle.GOOGLE_KEY);
            tmp.put("photoreference", current.getPhotoReference());
            tmp.put("maxheight","200");
            String photoUrlS = RequestParams.getEncodedUrl(ConstantsGoogle.GOOGLE_API_PLACES_PHOTO, tmp);
            tmp.put("maxheight","600");
            String photoUrlL = RequestParams.getEncodedUrl(ConstantsGoogle.GOOGLE_API_PLACES_PHOTO, tmp);
            current.setLargePhotoURL(photoUrlL);
            Log.d("restAdapter", photoUrlS);
            int ht_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mContext.getResources().getDisplayMetrics());
            int wt_px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, mContext.getResources().getDisplayMetrics());
            current.setSmallPhotoURL(photoUrlS);
            Picasso.with(mContext).load(photoUrlS).resize(ht_px,wt_px).into(icon);
        }else{
            icon.setImageResource(R.drawable.no_image);
        }

        TextView title, rating, vicinity;
        title = holder.title;
        rating = holder.rating;
        vicinity = holder.vicinity;

        title.setText(current.getName());
        String s;
        if(current.getRating()!=null){
            s = current.getRating().toString();
        }else{
            s="Not Available";
        }
        rating.setText("Rating : "+s);
        vicinity.setText("near "+current.getVicinity());
        return convertView;
    }

    public static class Holder{
        ImageView icon;
        TextView title, rating, vicinity;
    }

}
