package com.example.bharathramh.StorageClassCollection;

import java.io.Serializable;

/**
 * Created by bharathramh on 4/14/15.
 */
public class GooglePlacesCS implements Serializable{

    private Double lat, lng, rating;
    private String photoReference, vicinity, name, placeId;
    private int priceLevel;

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
}
