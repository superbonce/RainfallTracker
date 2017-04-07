package com.tdot.rainfalltrackerfree.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.tdot.rainfalltrackerfree.service.ParseCapabilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/*
 * TG Nov 14 Main storage
 */

public class DataStore implements Parcelable
{
	/**
	 * 
	 */
	String[] days =
        {"Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	private static final long serialVersionUID = 1L;
	
	String xml;
	ArrayList<?> times=new ArrayList();
	ArrayList<LayerDescriptor> layers=new ArrayList<LayerDescriptor>(Constants.MAX_LAYERS);
	ArrayList<SymbolData> symbols=new ArrayList();
	ParseCapabilities pC=new ParseCapabilities();
	public float lat=0;
	public float lon=0;
	public int layerNumberForObRain;
	public int layerNumberForFcRain;
	public int layerNumberForFcMSLP;
	public long loadedTimeFC;
	public long loadedTimeFX;
	public long loadedTimeOB;
	public long loadedTimePROB;
	
	public DataStore()
	{
		//Empty layer descriptors
		layers.add(new LayerDescriptor());
		layers.add(new LayerDescriptor());	
		layers.add(new LayerDescriptor());
	}
	
	public List<SymbolData> getSymbolData()
	{
		return symbols;
	}
	
	public void setSymbolData(String value)
	{
		String[] lines = value.split(System.getProperty("line.separator"));
		
		 for (int i = 0; i < lines.length; i++) { 
			 Log.i("DS","decode lines "+lines[i]); 
			 String x[]=lines[i].split(",");
			 Log.i("DS","time "+x[0]);
			 String sT=x[0].replace("Z", "-");
			 String t[]=sT.split("-");
			 Log.i("DS","time "+t[0]+" "+t[1]+" "+t[2]);
			 Calendar c1 = Calendar.getInstance();
			 c1.set(Integer.parseInt(t[0]),Integer.parseInt(t[1])-1, Integer.parseInt(t[2]));  
			 SymbolData sD=new SymbolData(Float.parseFloat(x[1]),Float.parseFloat(x[2]));
			 sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1],x[3]);
			 sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1]+" night",x[4]);
             c1.add(Calendar.HOUR, 24);
             sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1],x[5]);
			 sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1]+" night",x[6]);
			 c1.add(Calendar.HOUR, 24);
             sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1],x[7]);
			 sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1]+" night",x[8]);
			 c1.add(Calendar.HOUR, 24);
             sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1],x[9]);
			 sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1]+" night",x[10]);
			 c1.add(Calendar.HOUR, 24);
             sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1],x[11]);
			 sD.addElement(" "+days[c1.get(Calendar.DAY_OF_WEEK)-1]+" night",x[12]);
          
			 this.symbols.add(sD);
		 }
		
	}
	public void setXML(String value,int mode, int submode) throws ParserConfigurationException, SAXException, IOException
	{
		
		if(value.contains("datapoint.metoffice.gov.uk"))
		{
		
		  if(mode==Constants.MODE_OB)
		  {
		    xml=value;		
			pC.parse(value, this);
		  }				
		  else
		  {
			xml=value;
			//Now parse
			pC.parsefc(value, this, submode);	
		  }
		}
		else
		  throw new SAXException();
	}
	public int getNumLayers()
	{
		return layers.size();
	}
	public String getXML()
	{
		return xml;		
	}
	public void addLayer(LayerDescriptor value,int mode)
	{
		layers.set(mode,value);
	}
	public LayerDescriptor getLayer(int i)
	{
		return (LayerDescriptor) layers.get(i) ;
	}
	public void setTimes(ArrayList<?> value)
	{
		times=value;
	}
	public ArrayList<?> getTimes()
	{
		return times;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.xml);
		dest.writeList(times);
		dest.writeList(layers);
		dest.writeFloat(lat);
		dest.writeFloat(lon);
		dest.writeInt(layerNumberForObRain);
		dest.writeInt(layerNumberForFcRain);
		dest.writeLong(loadedTimeFC);
		dest.writeLong(loadedTimeOB);	
	}
}
