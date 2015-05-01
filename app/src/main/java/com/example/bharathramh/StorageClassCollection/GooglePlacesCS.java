package com.example.bharathramh.StorageClassCollection;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by bharathramh on 4/14/15.
 */
public class GooglePlacesCS implements Serializable, Parcelable{

    private Double lat, lng, rating;
    private String photoReference;
    private String vicinity;
    private String name;
    private String placeId;
    private Uri websiteUri;

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String phoneNumber;
    private int priceLevel;
    private String smallPhotoURL;

    public String getLargePhotoURL() {
        return largePhotoURL;
    }

    public void setLargePhotoURL(String largePhotoURL) {
        this.largePhotoURL = largePhotoURL;
    }

    private String largePhotoURL;

    public String getSmallPhotoURL() {
        return smallPhotoURL;
    }

    public void setSmallPhotoURL(String smallPhotoURL) {
        this.smallPhotoURL = smallPhotoURL;
    }



    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public GooglePlacesCS(){

    }


    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "GooglePlacesCS{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", rating=" + rating +
                ", photoReference='" + photoReference + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", priceLevel=" + priceLevel +
                '}';
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getRating() {

        return rating;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
