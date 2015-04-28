/*
Assignment : HomeWork 4
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.util.Log;

public class RequestParams {
	static String base_Url,methodCall;
//	StringBuilder insideStruct = new StringBuilder();
//	HashMap<String, String> values= new HashMap<String, String>();
    static HashMap<String, String> values;

	public static String getEncodedParams(){
		
		StringBuilder sb = new StringBuilder();
		String tempValue;
		for(String CurrentKey:values.keySet()){
			try {
				tempValue=URLEncoder.encode(values.get(CurrentKey), "UTF-8") ;
				sb.append(CurrentKey+"="+tempValue+"&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(values.keySet().size()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		
		
		return sb.toString();
	}
	
	
	public static String getEncodedUrl(){
		return base_Url+"?"+getEncodedParams();
	}

    public static String getEncodedUrl(String baseUrl, HashMap<String, String> valuesMap){
        base_Url= baseUrl;
        values = valuesMap;
        return base_Url+"?"+getEncodedParams();
    }
	
	@SuppressLint("LongLogTag")
    public static HttpURLConnection setupConnection(String method,String baseUrl, HashMap<String, String> valuesMap) throws IOException{

        base_Url= baseUrl;
        methodCall=method;
        values = valuesMap;

        if(!(methodCall.equals("GET") || methodCall.equals("POST"))){
            Log.d("ReqParamMethod", methodCall+" invoked is invalid method. It should be either GET or POST");
            return null;
        }

		Log.d("ReqParamMethod","values in hashmap are "+values.toString()+" and note that it will be LIFO");
		Log.d("ReqParamMethod","Requesting the following url "+ getEncodedUrl());
		if(methodCall.equals("GET")){
			URL url = new URL(getEncodedUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			if(con.getResponseCode()==HttpURLConnection.HTTP_OK){
				Log.d("ReqParamMethod","Connection established");
				return con;
			}
			else{
				Log.d("Request Params Set Up Connection","Connection not established");
			}
		}else if(methodCall.equals("POST")){
			URL url = new URL(baseUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(getEncodedParams());
			writer.close();
			return con;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "RequestParams [baseUrl=" + base_Url + ", method=" + methodCall
				+ ", values=" + values + "]";
	}
}
