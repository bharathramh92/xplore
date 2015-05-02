package com.example.bharathramh.xplore;

import android.location.Address;
import android.os.AsyncTask;

import com.example.JSON.ForecastUtil;
import com.example.bharathramh.StorageClassCollection.Weather;
import com.example.support.RequestParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by bharathramh on 5/1/15.
 */
public class WeatherAsync  extends AsyncTask<Address, Void, Weather>{
    String baseUrl = "https://api.forecast.io/forecast";
    String forecastIOKey = "688c5e9500c871a9f9828c35aaa53f0b";

    weatherAsyncListener mListener;

    @Override
    protected void onPostExecute(Weather weather) {
        mListener.weatherReceived(weather);
        super.onPostExecute(weather);
    }

    public WeatherAsync(weatherAsyncListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected Weather doInBackground(Address... params) {
        String modifiedUrl = baseUrl+"/"+forecastIOKey + "/" + params[0].getLatitude() + ","+ params[0].getLongitude();
        try {
            HttpURLConnection con = RequestParams.setupConnection("GET", modifiedUrl, new HashMap<String, String>());
            if(con!= null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();

                while(line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }

                if(reader != null){
                    reader.close();
                }
                return ForecastUtil.parseForecast(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface weatherAsyncListener{
        public void weatherReceived(Weather weather);
    }
}
