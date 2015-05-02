package com.example.bharathramh.StorageClassCollection;

/**
 * Created by bharathramh on 5/1/15.
 */
public class Weather {
    private double temperature;
    private String summary;

    @Override
    public String toString() {
        return "Weather{" +
                "temperature=" + temperature +
                ", summary='" + summary + '\'' +
                '}';
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
