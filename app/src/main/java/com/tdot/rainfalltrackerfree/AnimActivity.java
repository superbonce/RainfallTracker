package com.tdot.rainfalltrackerfree;


import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tdot.rainfalltrackerfree.R;
import com.tdot.rainfalltrackerfree.TouchImageView.OnTouchImageViewListener;
import com.tdot.rainfalltrackerfree.model.Constants;
import com.tdot.rainfalltrackerfree.model.CrossImageView;
import com.tdot.rainfalltrackerfree.model.DataStore;
import com.tdot.rainfalltrackerfree.model.SymbolData;
import com.tdot.rainfalltrackerfree.service.ImageService;
import com.tdot.rainfalltrackerfree.utils.DateSupport;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/*
 * TG Nov 14 logic for animation of frames
 */
public class AnimActivity extends Activity implements View.OnClickListener {
	
	  DataStore dS;
	  public int mode;
	  public int submode;
	  int direct;
	  int currentFrame;
	  int currentLayer;
	  int runSpeed=350;
	  SeekBar seekBar;
	  SeekBar.OnSeekBarChangeListener SBL=new SeekbarListener();
	  boolean _stop=false;
	  boolean touchSeek=false;
	  Handler handler=new Handler();
	  TextView tView;
	  TouchImageView rF;
	  TouchImageView mB;
	  int alpha=255;
	  int locX=0;
	  int locY=0;
	    
	  final Runnable r = new Runnable()
	  {
	      public void run() 
	      {
	          if(!_stop)
	          {
	          animate();
	          //handler.postDelayed(this,runSpeed);
	      
	          }}
	  };
	 
	  @Override
	  public boolean onTouchEvent(MotionEvent event)
	  {     
		  Log.i("MainActivity", "x = " + event.getX() + ", y = " + event.getY());
	      switch(event.getAction())
	      {
	        case MotionEvent.ACTION_DOWN:
	            Log.i("ANIM","DOWN");                        
	        break;         
	        case MotionEvent.ACTION_UP:
	        	Log.i("ANIM","up");   
	        	break;
	        case MotionEvent.ACTION_MOVE:
	            Log.i("ANIM","MOVE");                        
	        break;         
	  default:
		  break;
	        	
	      }
	      return super.onTouchEvent(event);
	  }
	  
	  @Override
	    protected void onDestroy()
	    {
           super.onDestroy();
           handler.removeCallbacks(r);
	    }
	  
	  @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.fragment_main);
	        //getActionBar().setDisplayHomeAsUpEnabled(true);
	        ActionBar ab = getActionBar(); 
			 ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#607d8b"));    
			           ab.setBackgroundDrawable(colorDrawable);

	        Intent intent = getIntent();
	        direct=(Integer) intent.getSerializableExtra("direct");
	        Log.i("ANIM","Direct "+direct);
	        if(direct==1)
	        {
	        	_stop=false;
		         handler.postDelayed(r,runSpeed);
	        }
	        dS=RainfallTrackerFreeApplication.getInstance().getDS();
	        mode=(Integer) intent.getSerializableExtra("mode");
	        submode=(Integer) intent.getSerializableExtra("submode");
	        if(mode==Constants.MODE_OB)
	        {
	          currentLayer=dS.layerNumberForObRain;
	        }
	        else
	        {
	        	if(submode==Constants.SUB_MODE_FC_RAIN)
	              currentLayer=dS.layerNumberForFcRain;	
	        	else
	        	  currentLayer=dS.layerNumberForFcMSLP;	
	        }
	        currentFrame=dS.getLayer(currentLayer).getNumFrames()-1;
	        seekBar=(SeekBar)findViewById(R.id.seekbar);
	        //Add a listener to listen SeekBar events
	       
	        if(seekBar!=null)
	        {
			  seekBar.setOnSeekBarChangeListener(SBL);
			  //Add max to seekBar
			  seekBar.setMax(dS.getLayer(currentLayer).getNumFrames()-1);
	        }
	        SharedPreferences preferences = PreferenceManager
	                .getDefaultSharedPreferences(this.getApplicationContext());
	        preferences.registerOnSharedPreferenceChangeListener(
	                mOnSettingUnitChangedListener);
	      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	  	  String newSelection=prefs.getString("prefs_map_key", "classic");
	  	  setBackground(newSelection);
	  	 String speedSelection=prefs.getString("prefs_speed_key", "Medium");
	  	  setSpeed(speedSelection);
	  	  tView = (TextView) findViewById(R.id.textView2);
	  	 rF=(TouchImageView) findViewById(R.id.imageView1);
		   mB=(TouchImageView) findViewById(R.id.imageView2);
		   rF.setOnTouchImageViewListener(new OnTouchImageViewListener() {
				
				@Override
				public void onMove() {
					mB.setZoom(rF);
					if(dS.lat != 0 && dS.lon != 0)
					   {
					     drawCross(dS.lat, dS.lon);
					   }
				}
				
			});
		  
    	showAd();
	  }
	  
	  @Override
		public void onBackPressed() {
		    // Do Here what ever you want do on back press;
			super.onBackPressed();
			overridePendingTransition(0,R.anim.pushtoright);
		}
	  
	  
	  private void showAd()
		{
			AdView adView = (AdView) this.findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
	        adView.loadAd(adRequest);
		}
	  
	  private void setBackground(String newSelection)
	  {
		    TouchImageView rF=(TouchImageView) findViewById(R.id.imageView1);
		    TouchImageView mB=(TouchImageView) findViewById(R.id.imageView2);
		    CrossImageView drawView = (CrossImageView) findViewById(R.id.widgetMap);
	    	if(newSelection.compareTo("classic")==0)
	    	{
	    	   mB.setImageResource(R.drawable.nature_1);
	    	   drawView.col=0xFF000000;
	    	}
	    	else if(newSelection.compareTo("retro")==0)
	    	{
	    		if(submode==Constants.SUB_MODE_FC_MSLP)
	    		{
	    		  mB.setImageResource(R.drawable.nature_1ow);
	    		  drawView.col=0xFF000000;
	    		}
	    		else
	    		{
	    		  mB.setImageResource(R.drawable.nature_1o);
		    	  drawView.col=0xFF33FF33;
	    		}
	    		
	    	}
	    	/*else if(newSelection.compareTo("diffused")==0)
	    	{
	    		mB.setImageResource(R.drawable.uksimplemapdiff);
	    	}
	    	else
	    	{
	    		mB.setImageResource(R.drawable.uksimplemaptiles);
	    	}  */
	    	 //Listen for movements and apply to map
	        
	  }
	  
	  private void setSpeed(String newSelection)
	  {
		  if(newSelection.compareTo("Slow")==0)
	    	{
			   runSpeed=450;
	    	}
	    	else if(newSelection.compareTo("Medium")==0)
	    	{
	    	   runSpeed=250;
	    	}
	    	else
	    	{
		       runSpeed=100;
	    	}
		  
	  }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu items for use in the action bar
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.main, menu);
	      return super.onCreateOptionsMenu(menu);
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	      // Handle presses on the action bar items
	      switch (item.getItemId()) {
	          /*case R.id.action_search:
	              Intent prefsIntent = new Intent(this, SearchActivity.class);
	                startActivity(prefsIntent);
	              return true;*/
	          case R.id.action_settings:
	        	  Intent prefsIntentS = new Intent(this, PreferencesActivity.class);
	                startActivity(prefsIntentS);
	              return true;
	          case android.R.id.home:
	              NavUtils.navigateUpFromSameTask(this);
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
	  
	  private final OnSharedPreferenceChangeListener mOnSettingUnitChangedListener = new OnSharedPreferenceChangeListener() { 
	        @Override
	        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	        {
	        	if(key.compareTo("prefs_map_key")==0)
				{
	        	String newSelection=sharedPreferences.getString("prefs_map_key", "classic");
	        	setBackground(newSelection);
				}
	        	else if((key.compareTo("prefs_speed_key")==0))
				{
					String newSelection=sharedPreferences.getString("prefs_speed_key", "Medium");
					setSpeed(newSelection);
				}
	        }
	  };
	  
	  public void onClick(View v) {
		 
		    final int id = v.getId();
		    switch (id) 
		    {
		    case R.id.textViewfxhelp:
		    	Log.i("ANIM","open browser");
		    	 Intent helpIntent = new Intent(Intent.ACTION_VIEW,
                 Uri.parse(Constants.HELP_WEBSITE));
		    	 try {  
		    		  // Start the activity  
		    		  startActivity(helpIntent);  
		    		} catch (ActivityNotFoundException e) {  
		    		  // Raise on activity not found  
		    		}  
		    	break;
		    case R.id.textView1:
		    	Log.i("Anim","tv touched");
		    break;
		    case R.id.imageView1:
		    	
		    if(_stop)
		    {
		    	_stop=false;
		         handler.postDelayed(r,runSpeed);
		    }
		    else
		    {
		    	_stop=true;
		    }
		    break;
	        default:
		    break;  	
		  }
		}
	  	  
	  @Override
	    public void onResume() {
		    super.onResume();

			//ImageView iI=(ImageView) findViewById(R.id.imageViewIcon);
		    	
		    if(mode==Constants.MODE_FC)
		    {
		    	//iI.setImageResource(R.drawable.comp);
		    	getActionBar().setIcon(R.drawable.roundiconcomp);
		    	//setTitle("Forecast rainfall");
		    	if(submode ==Constants.SUB_MODE_FC_RAIN)
		    	  getActionBar().setSubtitle("Forecast rainfall");
		    	else
		    	  getActionBar().setSubtitle("Surface pressure");

		    }
		    else
		    {
		    	//iI.setImageResource(R.drawable.radar);
		    	getActionBar().setIcon(R.drawable.roundiconradar);
		    	//setTitle("Actual rainfall");
		    	getActionBar().setSubtitle("Actual rainfall");
		    	
		    }	
		    	
		    currentFrame=dS.getLayer(currentLayer).getNumFrames()-1;
	        frameLoader(currentFrame);
	        
	        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
			{
	        	if(submode==Constants.SUB_MODE_FC_MSLP)
	        	{
	        		Log.i("Anim", "need to replace key");
	        		swapKey(true);
	        	}
	        	else
	        	{
	        		swapKey(false);
	        	}
			}
	  }
	  
	  private void swapKey(boolean swap)
		{
		    findViewById(R.id.ll3).setVisibility(View.GONE);
			if(swap)
			{
				findViewById(R.id.ll2).setVisibility(View.GONE);
				findViewById(R.id.ll1).setVisibility(View.VISIBLE);
				
	  		}
			else
			{
				findViewById(R.id.ll2).setVisibility(View.VISIBLE);
				findViewById(R.id.ll1).setVisibility(View.GONE);
			}
		}
	  
	  @Override
	  public void onWindowFocusChanged(boolean hasFocus) {
	   super.onWindowFocusChanged(hasFocus);
	   if(dS.lat != 0 && dS.lon != 0)
	   {
	     drawCross(dS.lat, dS.lon);
	   }
	  }
	  
	
	  private void frameLoader(int currentFrame)
	  {
	    	if(dS.getLayer(currentLayer).getBitmapCache(currentFrame) ==null)
	    	{
	    		String imageURL;
	    		if(mode == Constants.MODE_OB)
	    		{
	    		  if(Constants.MASTER)
	    		  {
	    	       imageURL = String.format(
                   Constants.IMAGE,
                   dS.getLayer(currentLayer).getIdentifier(),
                   dS.getLayer(currentLayer).getTimes().get(currentFrame)
                   );
	    		  }
	    		  else
	    		  {
	    			imageURL = Constants.COREALT+"frame"+currentFrame+".png";
	    		  }
	    		}
	    		else
	    		{
	    		   if(Constants.MASTER)
		    	   {
	   	    	     imageURL = String.format(
	                 Constants.IMAGEFC,
	                 dS.getLayer(currentLayer).getIdentifier(),
	                 dS.getLayer(currentLayer).getBaseTime(),
	                 dS.getLayer(currentLayer).getTimes().get(currentFrame)
	                 );
		    	   }
	    		   else
	    		   {
	    			 int sFrame=((dS.getLayer(currentLayer).getNumFrames()-currentFrame))-1;
	    			 if(submode==Constants.SUB_MODE_FC_RAIN)
	    			   imageURL = Constants.COREALT+"framef"+sFrame+".png"; 
	    			 else
	    			   imageURL = Constants.COREALT+"framefsxx"+sFrame+".png"; 
	    		   }
	   	        }	
	    	 Log.i("CALLOUT",imageURL);
	    	
	 	  	tView.setVisibility(View.VISIBLE);

	    	 tView.setText("LOADING FRAME "+(dS.getLayer(currentLayer).getNumFrames()-currentFrame)+" OF "+dS.getLayer(currentLayer).getNumFrames());
	    	 new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
	            .execute(imageURL);
	    	}
	    	else
	    	{
	    		tView.setVisibility(View.GONE);
	    		Log.i("CACHE","from cache frame "+currentFrame);
	    		ImageView iV=(ImageView) findViewById(R.id.imageView1);
	    		iV.setImageBitmap(dS.getLayer(currentLayer).getBitmapCache(currentFrame));
	    		updateTime();
	    		//checkPixel(iV);
		    	if(!touchSeek) setSeek(currentFrame);
	    	}	    			 
	  }
	  
	  public void setSeek(int value)
	  {
		  //backward of course
		  value=dS.getLayer(currentLayer).getNumFrames()-value;
		  Log.i("CACHE","value is "+value);
		  seekBar.setProgress(value-1);
	  }
	  	 
	  //Animate the frames
	  private void animate()
	  {
		  currentFrame--;
	      if(currentFrame <0)
	      {
	    	currentFrame=(dS.getLayer(currentLayer).getNumFrames()-1);
	      }
	      frameLoader(currentFrame);
	     //Put dwell on final frame
	      if(currentFrame==0)
	      {
	    	  handler.postDelayed(r, 1000);
	      }
	      else
	      {
	          handler.postDelayed(r,runSpeed);
	      }
	  }
	  
	 
		
		private class SeekbarListener implements SeekBar.OnSeekBarChangeListener
		{
			 public SeekbarListener() {
			       
			    }
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		    	//backward
		    	 progress=dS.getLayer(currentLayer).getNumFrames()-progress-1;
		    	 
		    	if(touchSeek)
		    	{
		    		currentFrame=progress;
		    		if(progress<dS.getLayer(currentLayer).getNumFrames())
		    		{
		    	      frameLoader(progress);
		    		}
		    	}
		    	

		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {

		         // Notify that the user has started a touch gesture.
		    	touchSeek=true;
		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		        // Notify that the user has finished a touch gesture.
		    	touchSeek=false;
		    }
		}
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		    ImageView bmImage;
		    private final WeakReference<ImageView> imageViewReference;


		    public DownloadImageTask(ImageView bmImage) {
		    	imageViewReference = new WeakReference<ImageView>(bmImage);
		    }

		    protected Bitmap doInBackground(String... urls) {
		        String urldisplay = urls[0];
		        Bitmap mIcon11 = null;
		        try {
		            InputStream in = new java.net.URL(urldisplay).openStream();
		            BitmapFactory.Options o = new BitmapFactory.Options();
		            o.inPurgeable = true;
                    o.inInputShareable=true;
                    if(mode==Constants.MODE_FC && submode==Constants.SUB_MODE_FC_RAIN) o.inSampleSize=2;
		            mIcon11 = BitmapFactory.decodeStream(in,null,o);
		            //Problem with image from server
		            if(mIcon11 == null)
		            {
		            	Resources res = getBaseContext().getResources();
			            int id = R.drawable.missing; 
			            o.inSampleSize=2;
			            Bitmap b = BitmapFactory.decodeResource(res, id, o);		            
			            mIcon11=b;
			            //b.recycle();
		            }
		        } catch (Exception e) {
		            //Log.e("Error", e.getMessage());
		            e.printStackTrace();
		            Log.i("ANIM","Error reading image at position"+currentFrame);
		            Resources res = getBaseContext().getResources();
		            int id = R.drawable.missing; 
		            BitmapFactory.Options o = new BitmapFactory.Options();
		            o.inSampleSize=2;
		            Bitmap b = BitmapFactory.decodeResource(res, id,o);		           
		            Log.i("ANIM",">"+b.getWidth()+" "+b.getHeight());
		            //dS.getLayer(currentLayer).setBitmapCache(currentFrame, b);
		            Log.i("ANIM","Put no image in that position");
		            mIcon11=b;
		            //b.recycle();
		        }
		        //smooth(mIcon11);
		        if(mode==Constants.MODE_OB)
			    {
		           //return highlightImage(smooth(mIcon11));
		           return highlightImage(smooth(mIcon11));
		        	//return smooth(mIcon11);
			    }
		        else if(mode==Constants.MODE_FC)
		        {
		        	if(submode ==Constants.SUB_MODE_FC_RAIN)
		        	{
		              return highlightImage(mIcon11);
		        	}
		           
		        }
		        
		        	return mIcon11;
		
		    }

		    protected void onPostExecute(Bitmap result) {
		         //bmImage.setImageBitmap(result);
		         if (imageViewReference != null && result != null) {
		             final ImageView imageView = imageViewReference.get();
		             if (imageView != null) {
		            	 //Log.i("ANIM","ABout to place bitmap in frame "+result.getHeight());
		                 imageView.setImageBitmap(result);
		            	 //TouchImageView img = (TouchImageView) findViewById(R.id.imageView1);
		                 //img.setImageBitmap(result);
		                 //img.invalidate();
		                 //Log.i("ANIM","alpha "+img.getAlpha());
		                 //BitmapDrawable bitmapDrawable = new BitmapDrawable(result);
		                 //img.setImageDrawable(getResources().getDrawable(R.drawable.roundiconradar));
		                 rF.setZoom((float) 1.0);
		                 
		             }
		         }

		         updateTime();
		    	 setSeek(currentFrame);
		    	 dS.getLayer(currentLayer).setBitmapCache(currentFrame, result);
		    	
		    	 currentFrame--;
		    	 if(currentFrame >= 0)
		    	 {	 
		    		 frameLoader(currentFrame);
		    	 }
		    	 else
		    	 {
		    		 handler.postDelayed(r, 1000);	
		    	 }
		    }
		}
		
		private void checkPixel(ImageView iV)
		{
			Bitmap bitmap = ((BitmapDrawable)iV.getDrawable()).getBitmap();
			if(locX<=bitmap.getWidth() && locY<=bitmap.getHeight() )
			{
			  int pixel = bitmap.getPixel(locX,locY);
			  //Now you can get each channel with:
			  Log.i("ANIM","check pixel col touched color: " + "#" + Integer.toHexString(pixel));
			}
			else
			{
				  Log.i("ANIM","x y b w b h "+locX+" "+locY+" "+bitmap.getWidth()+bitmap.getHeight());
			}
		}
		//Update time box
		private void updateTime()
		{
			
			 //TextView t=(TextView)findViewById(R.id.textView1); 
			 if(currentFrame>-1)
			 {
		     String ss=(String) dS.getLayer(currentLayer).getTimes().get(currentFrame);
		     if(mode==Constants.MODE_OB)
		     {
		       String[] separated = ss.split("T");
		       String x="";
		       
		       //Horrible cludge for now - add hour if in DST
		       if (DateSupport.inDST())
				{
		    	  if(separated[1].substring(0, 2).equals("00")) x="01";
				  if(separated[1].substring(0, 2).equals("01")) x="02";
				  if(separated[1].substring(0, 2).equals("02")) x="03";
				  if(separated[1].substring(0, 2).equals("03")) x="04";
				  if(separated[1].substring(0, 2).equals("04")) x="05";
		    	  if(separated[1].substring(0, 2).equals("05")) x="06";
				  if(separated[1].substring(0, 2).equals("06")) x="07";
				  if(separated[1].substring(0, 2).equals("07")) x="08";
				  if(separated[1].substring(0, 2).equals("08")) x="09";
			      if(separated[1].substring(0, 2).equals("09")) x="10";
				  if(separated[1].substring(0, 2).equals("10")) x="11";
			      if(separated[1].substring(0, 2).equals("11")) x="12";
			      if(separated[1].substring(0, 2).equals("12")) x="13";
		    	  if(separated[1].substring(0, 2).equals("13")) x="14";
				  if(separated[1].substring(0, 2).equals("14")) x="15";
			      if(separated[1].substring(0, 2).equals("15")) x="16";
			      if(separated[1].substring(0, 2).equals("16")) x="17";
		    	  if(separated[1].substring(0, 2).equals("17")) x="18";
			      if(separated[1].substring(0, 2).equals("18")) x="19";
		    	  if(separated[1].substring(0, 2).equals("19")) x="20";
		    	  if(separated[1].substring(0, 2).equals("20")) x="21";
		    	  if(separated[1].substring(0, 2).equals("21")) x="22";
		    	  if(separated[1].substring(0, 2).equals("22")) x="23";
		    	  if(separated[1].substring(0, 2).equals("23")) x="00";
		    	  setTitle(x+separated[1].substring(2, 5)); 
		    	  //setTitle(separated[1].substring(0, 2));
				}
		       else
		       {
		    	   setTitle(separated[1].substring(0, 5)); 
		       }
		
		     }
		     else
		     {
		    	 Log.i("ANIM "," currentLayer "+currentLayer+" basetime "+dS.getLayer(currentLayer).getBaseTime()+" "+dS.getLayer(currentLayer).getIdentifier());
		    	 String s=getTime(DateSupport.stringToDate(dS.getLayer(currentLayer).getBaseTime(), DateSupport.ISO8601_FULL_DATE_STRING),Integer.parseInt((String) dS.getLayer(currentLayer).getTimes().get(currentFrame)));
		    	 //t.setText(s); 
		    	 //getActionBar().setSubtitle(s);
		    	 setTitle(s);
		     }
			 }
		}
		
		private String getTime(Date d,int h)
		{
			String padH="",padM="";
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			Log.i("DATE ",d.toString());
			Log.i("DATE ",calendar.getTime().toString());
			
			calendar.add(Calendar.HOUR, h);
			if (DateSupport.inDST())
			{
				calendar.add(Calendar.HOUR, 1);
			}
			if(calendar.get(Calendar.HOUR_OF_DAY)<10) padH="0";
			if(calendar.get(Calendar.MINUTE)<10) padM="0";
			return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())+" "+padH+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+padM;
		}
		//New Location cross
		@SuppressLint("NewApi")
		private void drawCross(float lat, float lon)
		{
			
			CrossImageView drawView = (CrossImageView) findViewById(R.id.widgetMap);
			Log.i("DRAWSYMBOLS C ",drawView.getWidth()+" "+drawView.getHeight()+" sb ht "+this.getStatusBarHeight());
			TouchImageView drawViewDims = (TouchImageView) findViewById(R.id.imageView2);
			Log.i("DRAWSYMBOLS D ",drawViewDims.getImageWidth()+" "+drawViewDims.getImageHeight());
			//TouchImageView drawViewDims2 = (TouchImageView) findViewById(R.id.imageView1);
			//Log.i("DRAWSYMBOLS D2 ",drawViewDims2.getImageWidth()+" "+drawViewDims2.getImageHeight());
			Log.i("ANIM","ZOOM "+drawViewDims.getCurrentZoom());
			if(drawViewDims.getCurrentZoom()>=1.0)
			{
			if(drawViewDims.getCurrentZoom()<=1.0) drawView.zoom=drawViewDims.getCurrentZoom();
            PointF rF=drawViewDims.getScrollPosition();
            Log.i("POINT "," "+rF.x+" "+rF.y);
            RectF rR=drawViewDims.getZoomedRect();
            Log.i("RECT "," "+rR.left+" "+rR.right);
            Log.i("RECT"," "+rR.top+" "+rR.bottom);
            
            RectF bB=calcBBX(rR);
          
			
				float east=(lon-bB.left)/(bB.right-bB.left);
				float north=(lat-bB.bottom)/(bB.top-bB.bottom);
				//Log.i("AD",">"+east+" "+north+" "+(bB.right-bB.left));
				float posX=drawView.getWidth()*east;
				float posY=drawView.getHeight()*north;
				  //.i("AD",""+posX+" "+posY);
				
				  int pixX=(int) posX;
				  int pixY;
				  if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
				  {	
				      pixY=(int) ((int) (drawView.getHeight()-(int) posY)+(15*drawViewDims.getCurrentZoom()));
				  }
				  else
				  {
					  pixY=(int) ((int) (drawView.getHeight()-(int) posY)+(30*drawViewDims.getCurrentZoom()));  
				  }
				  if(posX>drawView.width || posY>drawView.height)
					{
						drawView.x1=0;
						drawView.x2=0;
						drawView.y2=0;
						drawView.y3=0;
						drawView.x4=0;
						drawView.y4=0;
					}
				  else
				  {
				  drawView.x1=pixX;
					drawView.x2=pixX;
					drawView.y2=drawView.height;
					drawView.y3=pixY;
					drawView.x4=drawView.width;
					drawView.y4=pixY;
				  }
					
			drawView.invalidate();
			}
			else
			{
				Log.i("DRAWCROSS","!");
				drawView.x1=0;
				drawView.x2=0;
				drawView.y2=0;
				drawView.y3=0;
				drawView.x4=0;
				drawView.y4=0;
				drawView.invalidate();	
			}
	
		}
		
		private RectF calcBBX(RectF rR)
		{
			RectF retR=new RectF();
			retR.left=Constants.east_start+(Constants.east_span*rR.left);
			retR.right=Constants.east_start+(Constants.east_span*rR.right);
			retR.top=(Constants.north_start+Constants.north_span)-(Constants.north_span*rR.top);
			retR.bottom=(Constants.north_start+Constants.north_span)-(Constants.north_span*rR.bottom);
			Log.i("BB2"," retRleft"+retR.left);
			Log.i("BB2"," retRrigh"+retR.right);
			Log.i("BB2"," retRbott"+retR.bottom);
			Log.i("BB2"," retRtop "+retR.top);
			return retR;			
		}
		//Location cross
		@SuppressLint("NewApi")
		private void drawCrossOLD(float lat, float lon)
		{
			
			//Here try to draw location			
			CrossImageView drawView = (CrossImageView) findViewById(R.id.widgetMap);
			TouchImageView drawViewDims = (TouchImageView) findViewById(R.id.imageView1);
			TextView tV = (TextView) findViewById(R.id.textView1);
			Log.i("DRAWCROSS","drawing"+drawViewDims.getCurrentZoom());
			Log.i("DRAWCROSS ",drawView.width+" "+drawView.height);
			if(drawViewDims.getCurrentZoom()==1.0)
			{
			float east=(lon-Constants.east_start)/Constants.east_span;
			float north=(lat-Constants.north_start)/Constants.north_span;
			float posX=drawView.width*east;
			float posY=drawView.height*north;
			Log.i("DRAWCROSS C ",drawView.width+" "+drawView.height+" sb ht "+this.getStatusBarHeight());
			Log.i("DRAWCROSS T ",drawViewDims.getWidth()+" "+drawViewDims.getHeight());
			alpha=alpha-1;
			//drawView.wid=cWid+1;
			if(alpha<20) alpha=255;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
			{
			  drawViewDims.setImageAlpha(alpha);
			}
			//if(cWid>10) cWid=2;
			drawView.x1=posX;
			drawView.x2=posX;
			drawView.y2=drawView.height;
			drawView.y3=drawView.height-posY+30;
			drawView.x4=drawView.width;
			drawView.y4=drawView.height-posY+30;
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
			{
				drawView.y3=drawView.y3-15;
				drawView.y4=drawView.y4-15;
			}
			//Just a check 
			if(posX>drawView.width || posY>drawView.height)
			{
				drawView.x1=0;
				drawView.x2=0;
				drawView.y2=0;
				drawView.y3=0;
				drawView.x4=0;
				drawView.y4=0;
			}
			drawView.invalidate();
			this.locX=(int) drawView.x1;
			this.locY=(int) drawView.y3;
			
			}
			else
			{
				Log.i("DRAWCROSS","!");
				drawView.x1=0;
				drawView.x2=0;
				drawView.y2=0;
				drawView.y3=0;
				drawView.x4=0;
				drawView.y4=0;
				drawView.invalidate();	
			}
		}

		public int getStatusBarHeight() {
			  int result = 0;
			  int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
			  if (resourceId > 0) {
			      result = getResources().getDimensionPixelSize(resourceId);
			  }
			  return result;
			}
        
		public Bitmap smooth(Bitmap src) {
			Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
			int[] pixels = new int[src.getHeight()*src.getWidth()];
			src.getPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
			int change=pixels[0];
			int col=0x0d000000;
			for (int i=0; i<src.getWidth()*src.getHeight(); i++)
				if(pixels[i]==change){
			    pixels[i] = col; } //Color.TRANSPARENT;}
			bmOut.setPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
			return bmOut;
		}
		
		public Bitmap highlightImage(Bitmap src) {
	        // create new bitmap, which will be painted and becomes result image
	        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
	        // setup canvas for painting
	        Canvas canvas = new Canvas(bmOut);
	        // setup default color
	        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
	        // create a blur paint for capturing alpha
	        Paint ptBlur = new Paint();
	        ptBlur.setMaskFilter(new BlurMaskFilter(7, Blur.OUTER));
	        int[] offsetXY = new int[2];
	        // capture alpha into a bitmap
	        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
	        // create a color paint
	        Paint ptAlphaColor = new Paint();
	        ptAlphaColor.setColor(0xFF0404fb);
	        // paint color for captured alpha region (bitmap)
	        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
	        // free memory
	        bmAlpha.recycle();

	        // paint the image source
	        canvas.drawBitmap(src, 0, 0, null);

	        // return out final image
	        return bmOut;
	    }
}
