package com.tdot.rainfalltrackerfree.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * TG Nov 14 Stores a layer in cache
 */
public class LayerDescriptor implements Parcelable {

	String name;
	String identifier;
	String baseTime; //FC only
	ArrayList times=new ArrayList();
	List<Object> cache =  new ArrayList<Object>();
	public void setName(String value)
	{
        name=value;		
	}
	public int getNumFrames()
	{
		return times.size();
	}
	public void resetCache(int numFrames)
	{
		for(int i=0;i<numFrames;i++)
		{
			cache.add(i, null);
		}
	}
	public void setBitmapCache(int frame, Bitmap bitmap)
	{
		if(frame>=0)
		{
		cache.set(frame, bitmap);
		}
	}
	public Bitmap getBitmapCache(int frame)
	{
		return (Bitmap) cache.get(frame);
	}
	public String getName()
	{
		return name;		
	}
	public String getIdentifier()
	{
		return identifier;		
	}
	public void setIdentifier(String value)
	{
        identifier=value;		
	}
	public void setTimes(ArrayList value)
	{
		times=value;
	}
	public ArrayList getTimes()
	{
		return times;
	}
	public String getBaseTime()
	{
		return baseTime;		
	}
	public void setBaseTime(String value)
	{
        baseTime=value;		
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(identifier);
		dest.writeString(baseTime); 
		dest.writeList(times);
		dest.writeList(cache);
	}
	
}
