package com.tdot.rainfalltrackerfree.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class URLConnector {
	
	 private static String convertStreamToString(InputStream is) {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                is.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }
	 
	 
	 public static String connect(String url) throws IOException
	 {
		   String result = null;
		   URL theurl = new URL(url);
		   HttpURLConnection urlConnection = (HttpURLConnection) theurl.openConnection();
		   try {
			 urlConnection.setConnectTimeout(5000);
		     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		     result= convertStreamToString(in);
		     Log.i("XML Feed",result);
		   }
		    finally {
		     urlConnection.disconnect();		     
		   }	
		   return result;
	 }
	 
	 /* OLD WAY */
	 public static String connectX(String url)
	    {
	        String result = null;
	        HttpClient httpclient = new DefaultHttpClient();
	        Log.i("XML Feed",url);
	        // Prepare a request object
	        HttpGet httpget = new HttpGet(url); 
	 
	        // Execute the request
	        HttpResponse response;
	        try {
	            response = httpclient.execute(httpget);
	            // Examine the response status
	            Log.i("XML Feed",response.getStatusLine().toString());
	 
	            // Get hold of the response entity
	            HttpEntity entity = response.getEntity();
	            // If the response does not enclose an entity, there is no need
	            // to worry about connection release
	 
	            if (entity != null) {
	 
	                // Convert feed
	                InputStream instream = entity.getContent();
	                result= convertStreamToString(instream);
	                Log.i("XML Feed",result);
	 
	                // Closing the input stream will trigger connection release
	                instream.close();
	            }
	 
	 
	        } catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
			return result; 
	    }

}
