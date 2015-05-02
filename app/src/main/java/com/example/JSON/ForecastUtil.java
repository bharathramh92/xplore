package com.example.JSON;

import com.example.bharathramh.StorageClassCollection.Weather;

import org.json.JSONException;
import org.json.JSONObject;

public class ForecastUtil {
		public static Weather parseForecast(String in){
            Weather forecast = new Weather();

			try{
				JSONObject root = new JSONObject(in);
				JSONObject currentForecastRootJSONObject = root.getJSONObject("currently");
				forecast.setTemperature(currentForecastRootJSONObject.getDouble("temperature"));
				forecast.setSummary(currentForecastRootJSONObject.getString("summary"));
			} catch(Exception E){
				System.out.println("Exception in reading data from JSON stream");
				return null;
			}
			return forecast;

	}
}
