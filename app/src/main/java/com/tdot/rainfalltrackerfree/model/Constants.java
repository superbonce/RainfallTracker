package com.tdot.rainfalltrackerfree.model;
/*
 * Tony Gowland Nov 14
 */
public class Constants {
	public static final String MOKEY="&key=9e616dbc-ded9-4ea2-b871-a13be4cdcb2d";
	public static final String CORE="http://datapoint.metoffice.gov.uk/public/data/";   
	public static final String COREALT="http://www.tdot.co.uk/images/"; 
	public static final String COREALTSYMBOLS="http://www.tdot.co.uk/images/summary.txt"; 
	public static final String CAPABILITIES="http://datapoint.metoffice.gov.uk/public/data/layer/wxobs/all/xml/capabilities?";	
	public static final String FC_CAPABILITIES="http://datapoint.metoffice.gov.uk/public/data/layer/forecast/all/xml/capabilities?";
	public static String IMAGE= CORE+"layer/wxobs/%s/png?TIME=%sZ"+MOKEY;
	public static String IMAGEFC= CORE+"layer/wxfcs/%s/png?RUN=%sZ&FORECAST=%s"+MOKEY;
	public static String AUTOCOMPLETE="https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyCsO0-9kcscYJbbTm9MSSNrWpihBTUOxLc&types=(cities)&input='%s'";
    public static String HELP_WEBSITE="http://www.metoffice.gov.uk/learning/learn-about-the-weather/synoptic-weather-chart";
	public static float east_span=17;
	public static float east_start=-12;
	public static float north_span=13;
	public static float north_start=48;
	
	public static int MAX_LAYERS=3;
	
	public static int MODE_OB=0;
	public static int MODE_FC=1;
	public static int MODE_SYMBOLS=2;
	
	public static int SUB_MODE_FC_RAIN=1;
	public static int SUB_MODE_FC_MSLP=2;
	
	public static boolean MODE_FROM_DATAPOINT=true;
	public static boolean MODE_FROM_RG=false;
	public static boolean MASTER=MODE_FROM_RG;
	
	public static String REQUIRED_FIELD="Rainfall";
	public static String REQUIRED_FIELD2="Pressure";
	
	public static long FC_INTERVAL=900000; //5 minutes
	public static long OB_INTERVAL=300000; //15 minutes
	
	public static String returnCaps(boolean fc)
	{
		String ret="";
		if(MASTER)
		{
			if(fc)
				ret=Constants.FC_CAPABILITIES+Constants.MOKEY;
			else
				ret=Constants.CAPABILITIES+Constants.MOKEY;
		}
		else
		{
			if(fc)
				ret=Constants.COREALT+"fccapabilities.xml";
			else
				ret=Constants.COREALT+"obcapabilities.xml";
		}
		return ret;
	}
	
	
}
