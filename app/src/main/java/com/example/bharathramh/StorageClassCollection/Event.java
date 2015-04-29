package com.example.bharathramh.StorageClassCollection;

import java.io.Serializable;

/**
 * Created by bharathramh on 4/28/15.
 */
public class Event implements Serializable{
    String name, start_time, city_name, venue_address, url, image_url;

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", start_time='" + start_time + '\'' +
                ", city_name='" + city_name + '\'' +
                ", venue_address='" + venue_address + '\'' +
                ", url='" + url + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getVenue_address() {
        return venue_address;
    }

    public void setVenue_address(String venue_address) {
        this.venue_address = venue_address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
