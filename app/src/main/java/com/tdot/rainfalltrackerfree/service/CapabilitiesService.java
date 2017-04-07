package com.tdot.rainfalltrackerfree.service;

import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;


	public class CapabilitiesService extends Service {
		 
	    private static final String DEBUG_TAG = "CapabilitiesService";
	    private DownloaderTask tutorialDownloader;
	 
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        // TBD
	        return Service.START_FLAG_REDELIVERY;
	    }
	 
	    @Override
	    public IBinder onBind(Intent intent) {
	        return null;
	    }
	 
	    private class DownloaderTask extends AsyncTask<URL, Void, Boolean> {
	 	 
	        @Override
	        protected Boolean doInBackground(URL... params) {
	            return true;
	        }
	 
	        private boolean xmlParse(URL downloadPath) {
	            return true;
	        }
	 
	    }
	 
	}
