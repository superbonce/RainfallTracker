package com.tdot.rainfalltrackerfree;


import com.tdot.rainfalltrackerfree.model.DataStore;

import android.app.Application;


public final class RainfallTrackerFreeApplication extends Application {
	
	private static RainfallTrackerFreeApplication mSelf;
	private DataStore dS=new DataStore();
	
	 @Override
	    public void onCreate()
	    {
	        super.onCreate();
	    }
	 
	  public RainfallTrackerFreeApplication()
	    {
	        mSelf = this;
	    }

	
	public static RainfallTrackerFreeApplication getInstance()
    {
        return mSelf;
    }
	
	public void setDS(DataStore value)
	{
		dS=value;
	}
	
	public DataStore getDS()
	{
		return dS;
	}

}
