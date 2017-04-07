package com.tdot.rainfalltrackerfree.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class DateSupport
{

	 public static final String ISO8601_FULL_DATE_STRING = "yyyy-MM-dd'T'HH:mm:ss";
	 
	 public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	 
	 private static DateFormat stringToDateTemp;
	 
	 public static Date stringToDate(final String stringToConvert, final String dateFormat)
	    {
	        stringToDateTemp = new SimpleDateFormat(dateFormat);
	        return stringToDate(stringToConvert, stringToDateTemp);
	    }
	 public static Date stringToDate(final String stringToConvert, final DateFormat formatter)
	    {
	        try
	        {
	          
	            formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));
	           
	            return formatter.parse(stringToConvert);
	        }
	        catch (Exception e)
	        {
	            Log.w("TIME","String to date ParseException :", e);
	            return null;
	        }
	    }
	 /*
	     * Use only for GMT 0 - DETERMINE IF IN DST
	     */
	    public static boolean inDST()
	    {
	        TimeZone tz = TimeZone.getTimeZone("Europe/London");
	        return tz.inDaylightTime(new Date());
	    }
}
    

