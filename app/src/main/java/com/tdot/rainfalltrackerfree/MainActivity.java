package com.tdot.rainfalltrackerfree;



import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.splunk.mint.Mint;
import com.tdot.rainfalltrackerfree.R;
import com.tdot.rainfalltrackerfree.model.Constants;
import com.tdot.rainfalltrackerfree.model.CrossImageView;
import com.tdot.rainfalltrackerfree.model.DataStore;
import com.tdot.rainfalltrackerfree.service.ImageService;
import com.tdot.rainfalltrackerfree.service.NicerLocationManager;
import com.tdot.rainfalltrackerfree.service.URLConnector;
import com.tdot.rainfalltrackerfree.service.NicerLocationManager.NicerLocationListener;
import com.tdot.rainfalltrackerfree.utils.DeviceSupport;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.Secure;

public class MainActivity extends Activity implements View.OnClickListener {

	DataStore dS=new DataStore();
	private Context mApp=this.getBaseContext();
	private int mode=Constants.MODE_OB;
	private int submode;
	Thread thread;
	private int direct=0;
	private ProgressBar spinner;
	private ImageView chevob=null;
	private ImageView chevfc=null;
	private ImageView chevfx=null;
	private boolean once=true;
	private boolean notOrientationChange=true;
	static final String HAS_RUN = "hasRun";
	static final String MODEL_DATA ="DataObject";
	Animation animb;

	

	String myVersionName = "not available"; // initialize String
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getApplicationContext(); // or activity.getApplicationContext()
		setContentView(R.layout.splash);
		ActionBar ab = getActionBar(); 
		ab.setBackgroundDrawable(null);
		setTitle("");
		Log.i("MAIN ",VERSION.SDK_INT+" "+VERSION_CODES.JELLY_BEAN);
		Log.i("MAIN",Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
		 
		Mint.disableNetworkMonitoring();
		Mint.initAndStartSession(MainActivity.this, "875ca848");
		
		  //You can start the app and go straight to the view screens
		if (savedInstanceState == null) 
		 {
		  Log.i("MAIN","No saved instance");
		  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	  	  String newSelection=prefs.getString("prefs_appstart_key", "On home screen");
	  	  if(newSelection.compareTo("On home screen")!=0)
	  	  {
	  		Button button;
	  		if(newSelection.compareTo("On actual rainfall")==0)
	  		{
	  	      button = (Button)findViewById(R.id.button1);
	  		}
	  		else
	  		{
		  	  button = (Button)findViewById(R.id.button2);
		  	}	
	  	    button.performClick();
	  	  }
		 }
		else
		{
			 Log.i("MAIN","Saved instance found");
			 //dS=(DataStore) savedInstanceState.getParcelable(MODEL_DATA);
			 dS=RainfallTrackerFreeApplication.getInstance().getDS();
			 //Log.i("MAIN","timeload "+dS.loadedTimeOB);
		}
	  	
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    // TODO Auto-generated method stub
		notOrientationChange=false;
	    super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current game state
	    savedInstanceState.putBoolean(HAS_RUN, true);	 
	    //savedInstanceState.putParcelable(MODEL_DATA, dS);
	    Log.i("MAIN","Saved instances");
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onBackPressed() {
	    // Do Here what ever you want do on back press;
		super.onBackPressed();
	}
	 @Override
	    protected void onResume()
	    {
	        super.onResume();
	        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		  	Boolean locOn=prefs.getBoolean("prefs_loc_key", true);
            Log.i("MAIN","Build "+DeviceSupport.getDeviceName());
            if(!DeviceSupport.getDeviceName().contains("AMAZON"))
            {
	          if(locOn) getUserCurrentLocation();
	          else
	          {
	        	dS.lat=0;
	        	dS.lon=0;
	          }
            }
	        resetChevrons();
	    }
	 
	 private class DownloadCapabilitiesTask extends AsyncTask<String, Integer, Long> {
		 
	     protected Long doInBackground(String... urls) {
	       URLConnector dataIn=new URLConnector();
	       if(mode==Constants.MODE_OB)
    	   {
    	     try {
				xmlLoaded(dataIn.connect(Constants.returnCaps(false)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				showToast(" Problem connecting to Data service ");
				e.printStackTrace();
			} 
    	   }
	       else if(mode==Constants.MODE_SYMBOLS)
	       {
	    	   Log.i("MAIN","Load symbols csv");
	    	   try {
				xmlSymbolsLoaded(dataIn.connect(Constants.COREALTSYMBOLS));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }
    	   else
    	   {
    	     try {
				xmlLoaded(dataIn.connect(Constants.returnCaps(true)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				showToast(" Problem connecting to Data service ");
				e.printStackTrace();
			}   
    	   }
      	   return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(Long result) {
	    	
	    	spinner.setVisibility(View.GONE);
	    	if(result ==null)
	    	{
	    		resetChevrons();
	    	}
	    	
	         Log.i("Downloaded ", result + " bytes");
	     }
	 }

	 private void resetChevrons()
	 {
		 if(chevob!=null)
	    	{
	    	  chevob.setVisibility(View.VISIBLE);
	    	}
	    	if(chevfc != null)
	    	{
	    	  chevfc.setVisibility(View.VISIBLE);
	    	}
	    	if(chevfx != null)
	    	{
	    	  chevfx.setVisibility(View.VISIBLE);
	    	}
	 }
	 
	 private void getUserCurrentLocation()
	    {

	        // SitesProviderHelper.deleteSite(mApp,String.valueOf(Consts.BLANK_SITE_ID_NO_LOCATION));

	        // find current location to parse forecast
	        NicerLocationManager locationMgr = new NicerLocationManager(this.getApplicationContext());
	        if (locationMgr.isAnyLocationServicesAvailble())
	        {
	            Log.i("MAIN","retrieving current location...");

	            // get current location
	            locationMgr.getBestGuessLocation(1000,
	                    new NicerLocationListener() {

	                @Override
	                public void locationLoaded(final Location location)
	                {
	                    // parse the current site forecast for users current
	                    // location in the background
	                    //LocationForecastSetup currentForecast = new LocationForecastSetup();
	                    //currentForecast.execute(location);

	                    // QLog.i("location loaded. finding nearest location...");
	                    //
	                    // // find nearest weather location
	                    // Site nearestLocation =
	                    // Utils.findNearestSite(mApp, location);
	                    //
	                    // // insert the new current user location site
	                    // SitesProviderHelper.addSavedSite(mApp,
	                    // Long.valueOf(nearestLocation.getmSiteId()), true);
	                    //
	                    Log.i("MAIN","location found "+location.getLatitude()+" "+location.getLongitude());
	                    dS.lat=(float) location.getLatitude();
	                    dS.lon=(float) location.getLongitude();
	                    //TextView versionText = (TextView) findViewById(R.id.info_area_string);
	                    //versionText.setText("lat  " + location.getLatitude()+" lon "+location.getLongitude());
	                    
	                    //
	                    // /*
	                    // * add blank site if user has no saved sites. a blank site
	                    // * forecast is also added in the WeatherService class
	                    // * (runWeatherService method) to display correctly in the
	                    // * view pager.this is later removed when a user adds a
	                    // site
	                    // * and added again when user removes last site.
	                    // */
	                    // SitesProviderHelper.addBlankSavedSite(mApp);
	                    //
	                    // // re-order sites so current location is first
	                    // SitesProviderHelper.setSiteOrder(mApp,
	                    // nearestLocation.getmSiteId(), "0");
	                    // SitesProviderHelper.setSiteOrder(mApp,
	                    // Consts.BLANK_SITE_ID, "1");

	                }

	                @Override
	                public void error()
	                {
	                    // give option to change location settings or select a
	                    // location manually
	                    Log.e("MAIN","Error finding best guess location");
                        dS.lat=0;
                        dS.lon=0;
                        if(once)
                        {
	                    //promptSetLocationService(MainActivity.this);
                        }
                        once=false;
	                }

	                @Override
	                public void onFinished()
	                {
	                    //runUpdateService(false, false);
	                	Log.i("MAIN","onFinished");
	                }
	            });

	        }
	        else
	        {
	          
	        	dS.lat=0;
                dS.lon=0;
	                
	        }

	    }
	 public void xmlSymbolsLoaded(String loadedXML)
	    {		 		 
	    	Log.i("XML","symbols xml has loaded now parse");
	    	dS.setSymbolData(loadedXML);
	    	Intent intent = new Intent().setClass(this, AnimActivityDraw.class);
	    	RainfallTrackerFreeApplication.getInstance().setDS(dS);
	    	dS.loadedTimePROB=Calendar.getInstance().getTime().getTime();
	        this.startActivity(intent);
	        overridePendingTransition(R.anim.slidefromright,R.anim.fader);
	    }
		
	 public void xmlLoaded(String loadedXML)
	    {
		 		 
	    	Log.i("XML","has loaded now parse");
	    	if(loadedXML!=null)
	    	{
	    		try
	    		{
	    	dS.setXML(loadedXML,mode,submode);	
	    		}
	    		catch (ParserConfigurationException e) {
	    	        System.out.println("XML parse error: " + e.getMessage());
	    	    } catch (SAXException e) {
	    	        System.out.println(">Wrong XML file structure: " + e.getMessage());
	    	        this.showToast("Problem contacting data services");
	    	        return;
	    	    } catch (IOException e) {
	    	        System.out.println("I/O exeption: " + e.getMessage());
	    	    }
	    	//Store a date of load 
	    	if(mode==Constants.MODE_FC)
	    	{
	    		if(submode == Constants.SUB_MODE_FC_RAIN)
	    	   dS.loadedTimeFC=Calendar.getInstance().getTime().getTime();
	    		else
	    	   dS.loadedTimeFX=Calendar.getInstance().getTime().getTime();
	    	}
	    	else
	    	{
		       dS.loadedTimeOB=Calendar.getInstance().getTime().getTime();
		    }	
	    	Intent intent = new Intent().setClass(this, AnimActivity.class);
	    	RainfallTrackerFreeApplication.getInstance().setDS(dS);
	    	intent.putExtra("direct", direct);
	        intent.putExtra("mode", mode);
	        intent.putExtra("submode", submode);
	        this.startActivity(intent);
	        overridePendingTransition(R.anim.slidefromright,R.anim.fader);

	        
	    	}
	    	else
	    	{
	    		showToast("Problem contacting data services"); 
	    	}
	        
	    }
	 
	 public void showToast(final String toastMessage)
	 {
	     runOnUiThread(new Runnable() {
	         public void run()
	         {
	        	    Toast toast=new Toast(MainActivity.this);
				    TextView  tv=new TextView(MainActivity.this);
		            // set the TextView properties like color, size etc
		            tv.setTextColor(Color.RED);
		            tv.setTextSize(16); 
		            tv.setBackgroundColor(Color.WHITE);
	                tv.setText(toastMessage);
		            tv.setGravity(Gravity.CENTER_VERTICAL);
		            toast.setView(tv);
		            toast.setDuration(Toast.LENGTH_SHORT);
				    toast.show();
	         }
	     });
	 }

	 @Override
	 public boolean onTouchEvent(MotionEvent event){ 
	         
	     int action = MotionEventCompat.getActionMasked(event);
	     String DEBUG_TAG="X";
	     switch(action) {
	         case (MotionEvent.ACTION_DOWN) :
	             Log.d(DEBUG_TAG,"Action was DOWN");
	         //findViewById(R.id.rootView1).setBackgroundColor(0xaa607d8b);
	             return true;
	         case (MotionEvent.ACTION_MOVE) :
	             Log.d(DEBUG_TAG,"Action was MOVE");
	             return true;
	         case (MotionEvent.ACTION_UP) :
	             Log.d(DEBUG_TAG,"Action was UP");
	         //findViewById(R.id.rootView1).setBackgroundResource(R.drawable.raininlights);
	             return true;
	         case (MotionEvent.ACTION_CANCEL) :
	             Log.d(DEBUG_TAG,"Action was CANCEL");
	             return true;
	         case (MotionEvent.ACTION_OUTSIDE) :
	             Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
	                     "of current screen element");
	             return true;      
	         default : 
	             return super.onTouchEvent(event);
	     }      
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toplevel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		 // Handle presses on the action bar items
	      switch (item.getItemId()) {
	          
	          case R.id.action_settings:
	        	  Intent prefsIntentS = new Intent(this, PreferencesActivity.class);
	                startActivity(prefsIntentS);
	              return true;
	          case R.id.action_about:
	        	  Intent aboutIntent = new Intent(this, AboutActivity.class);
	        	  startActivity(aboutIntent);
	        	  overridePendingTransition(R.anim.fadein,R.anim.fader);
	              return true;
	          case R.id.action_help:
	        	  Intent helpIntent = new Intent(this, HelpActivity.class);
	        	  startActivity(helpIntent);
	        	  overridePendingTransition(R.anim.fadein,R.anim.fader);
	              return true;
	          default:
	              return super.onOptionsItemSelected(item);
	      }
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		
		ConnectivityManager cm =
		        (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		Log.i("MAIN"," is connected "+isConnected);
		
		if(isConnected)
		{
	    final int id = v.getId();
	    boolean doLoad=false;
	    long now=Calendar.getInstance().getTime().getTime();
	    switch (id) 
	    {
	    case R.id.textView11:	
	    	v.setVisibility(View.GONE);
	    	findViewById(R.id.textView12).setVisibility(View.VISIBLE);
	    	findViewById(R.id.textView8).setVisibility(View.VISIBLE);
	    	findViewById(R.id.textView81).setVisibility(View.VISIBLE);
	    	findViewById(R.id.textView9).setVisibility(View.VISIBLE);
	    	findViewById(R.id.textView10).setVisibility(View.VISIBLE);
	    	Animation anim = AnimationUtils.loadAnimation(this, R.anim.slidefromright);
	    	findViewById(R.id.textView12).startAnimation(anim);
	    	findViewById(R.id.textView8).startAnimation(anim);
	    	findViewById(R.id.textView81).startAnimation(anim);
	    	findViewById(R.id.textView9).startAnimation(anim);
	    	findViewById(R.id.textView10).startAnimation(anim);
	    	return;
		case R.id.textView12:	
	    	v.setVisibility(View.GONE);
	    	findViewById(R.id.textView11).setVisibility(View.VISIBLE);
	    	
	    	animb = AnimationUtils.loadAnimation(this, R.anim.slidefromright);
	    	Animation animc = AnimationUtils.loadAnimation(this, R.anim.pushtoright);
	    	animc.setAnimationListener(animationSlideInLeftListener);
	    	findViewById(R.id.textView8).startAnimation(animc);
	    	findViewById(R.id.textView81).startAnimation(animc);
	    	findViewById(R.id.textView9).startAnimation(animc);
	    	findViewById(R.id.textView10).startAnimation(animc);
	    	return;
		case R.id.button1:
	    case R.id.layout1:
	    case R.id.imagechevronob:
	    	mode=Constants.MODE_OB;
	    	Log.i("Anim"," > "+(now-dS.loadedTimeOB));
	    	if(now - dS.loadedTimeOB > Constants.OB_INTERVAL)
	    	{
	    	  spinner = (ProgressBar)findViewById(R.id.progressBar1);
              spinner.setVisibility(View.VISIBLE);
              chevob = (ImageView)findViewById(R.id.imagechevronob);
              chevob.setVisibility(View.GONE);
              doLoad=true;
	    	}
	    	
	    	
	    break;
	    case R.id.button11:
	    case R.id.layout0:
	    case R.id.imagechevronprobfc:	    	
	    	mode=Constants.MODE_SYMBOLS;
	    	if(now - dS.loadedTimePROB > Constants.FC_INTERVAL)
	    	{
	    	doLoad=true;	    	
	    	spinner = (ProgressBar)findViewById(R.id.progressBar1a);
            spinner.setVisibility(View.VISIBLE);
            chevob = (ImageView)findViewById(R.id.imagechevronprobfc);
            chevob.setVisibility(View.GONE);
	    	}
	    	break;
	    	
	    case R.id.button2:
	    case R.id.layout2:
	    case R.id.imagechevronfc:
	    	mode=Constants.MODE_FC;
	    	submode=Constants.SUB_MODE_FC_RAIN;
	    	if(now - dS.loadedTimeFC > Constants.FC_INTERVAL)
	    	{
	    	  spinner = (ProgressBar)findViewById(R.id.progressBar2);
              spinner.setVisibility(View.VISIBLE);
              chevfc = (ImageView)findViewById(R.id.imagechevronfc);
              chevfc.setVisibility(View.GONE);
              doLoad=true;
	    	}	    
	    break;
	    case R.id.button3:
	    case R.id.layout3:
	    case R.id.imagechevronfx:
	    	mode=Constants.MODE_FC;
	    	submode=Constants.SUB_MODE_FC_MSLP;
	    	if(now - dS.loadedTimeFX > Constants.FC_INTERVAL)
	    	{
	    	  spinner = (ProgressBar)findViewById(R.id.progressBar3);
              spinner.setVisibility(View.VISIBLE);
              chevfc = (ImageView)findViewById(R.id.imagechevronfx);
              chevfc.setVisibility(View.GONE);
              doLoad=true;
	    	}	    
	    break;
	    
        default:
	    break;  	
	  }
	    if(doLoad)
	    {
	      direct=0;
	      DownloadCapabilitiesTask dct=new DownloadCapabilitiesTask();
	      dct.execute("ignore");
	    }
	    else
	    {
	    	direct=1;
	    	Log.i("Anim","Need to go direct"+direct+" "+mode);
	    	//Only go direct if some data is loaded 
	    	//It should be but occasionally it gets here with no data 
	    	if(mode==Constants.MODE_SYMBOLS)
	    	{
	    		Intent intent = new Intent().setClass(this, AnimActivityDraw.class);
		        this.startActivity(intent);
		        overridePendingTransition(R.anim.slidefromright,R.anim.fader);	
	    	}
	    	else
	    	{
	    	if(isData(mode))
	    	 {
	    	  Intent intent = new Intent().setClass(this, AnimActivity.class);
	          intent.putExtra("direct", direct);
	          intent.putExtra("mode", mode);
	          intent.putExtra("submode", submode);
	          Log.i("MAIN","mode "+mode);
	          this.startActivity(intent);
	          overridePendingTransition(R.anim.slidefromright,R.anim.fader);
	    	 }
	    	}
	    }
		}//Connected
		else
		{
            showToast(" Check internet connection ! ");      
		}
	}
	
	//
	 AnimationListener animationSlideInLeftListener
	 = new AnimationListener(){

	  @Override
	  public void onAnimationEnd(Animation animation) {
	   // TODO Auto-generated method stub
		  findViewById(R.id.textView8).setVisibility(View.GONE);
		  findViewById(R.id.textView81).setVisibility(View.GONE);
	    	findViewById(R.id.textView9).setVisibility(View.GONE);
	    	findViewById(R.id.textView10).setVisibility(View.GONE);	    	
	    	findViewById(R.id.textView11).startAnimation(animb);
	  }

	  @Override
	  public void onAnimationRepeat(Animation animation) {
	   // TODO Auto-generated method stub
	   
	  }

	  @Override
	  public void onAnimationStart(Animation animation) {
	   // TODO Auto-generated method stub
	   
	  }};
	//
	
	private boolean isData(int mode)
	{
		boolean status=false;
		if(mode == Constants.MODE_OB)
		{
			if(dS.getLayer(dS.layerNumberForObRain).getNumFrames()>0) status=true;
		}
		else
		{
			if(submode==Constants.SUB_MODE_FC_RAIN)
			{
			  if(dS.getLayer(dS.layerNumberForFcRain).getNumFrames()>0) status=true;
			}
			else
			{
			  if(dS.getLayer(dS.layerNumberForFcMSLP).getNumFrames()>0) status=true;
			}
		}
		return status;
		
	}
}
