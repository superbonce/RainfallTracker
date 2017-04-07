package com.tdot.rainfalltrackerfree;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tdot.rainfalltrackerfree.R;
import com.tdot.rainfalltrackerfree.TouchImageView.OnTouchImageViewListener;
import com.tdot.rainfalltrackerfree.model.Constants;
import com.tdot.rainfalltrackerfree.model.CrossImageView;
import com.tdot.rainfalltrackerfree.model.DataStore;
import com.tdot.rainfalltrackerfree.model.SymbolData;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * TG Feb 15 logic for symbols onto map
 */
public class AnimActivityDraw extends Activity implements View.OnClickListener {
	
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
			 
	        dS=RainfallTrackerFreeApplication.getInstance().getDS();
	       
	        currentFrame=0;
	        seekBar=(SeekBar)findViewById(R.id.seekbar);
	        //Add a listener to listen SeekBar events
	       
	        if(seekBar!=null)
	        {
			  seekBar.setOnSeekBarChangeListener(SBL);
			  //Add max to seekBar
			  seekBar.setMax(dS.getSymbolData().get(0).values.size()-1);
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
	  	 tView.setVisibility(View.GONE);
	  	 rF=(TouchImageView) findViewById(R.id.imageView1);
		   mB=(TouchImageView) findViewById(R.id.imageView2);
		   mB.setOnTouchImageViewListener(new OnTouchImageViewListener() {
				
				@Override
				public void onMove() {
					Log.i("ANIM","Move ");
					drawSymbols(currentFrame);
					
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
		    	
		 
		    break;
	        default:
		    break;  	
		  }
		}
	  	  
	  @Override
	    public void onResume() {
		    super.onResume();

		
		    	getActionBar().setIcon(R.drawable.roundiconperc);
		    
		    	  getActionBar().setSubtitle("Chance of rain");
		    	
	        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
			{
	        	
	        		swapKey();
	        	
			}
	  }
	  
	  private void swapKey()
		{
		   //310013 Manchester
		   //351351 Edinburgh
		   //310001 Norwich
		   //310069 Exeter
		   //310009 Glasgow
		   //310169 York
		   //310124 Reading
		   //352241 Leeds
		   //310037 Brighton
		   //310034 Bournemouth
		   //350758 Cardiff
		   //310016 Plymouth
		   //353720 Stornoway
		   //352046 jog
		   //310218 penzance
		   //350347 belfast
		   //310002 brum
		   
		  String[] monthsArray = { "London","Manchester", "Edinburgh", "Norwich", "Exeter", "Glasgow", "York", "Reading",
				  "Leeds", "Brighton", "Bournemouth", "Cardiff", "Plymouth", "Stornoway", "John O Groats",
				  "Penzance","Belfast","Birmingham"};

				findViewById(R.id.ll2).setVisibility(View.GONE);
				findViewById(R.id.ll1).setVisibility(View.GONE);
				findViewById(R.id.ll3).setVisibility(View.VISIBLE);
				ListView monthsListView = (ListView) findViewById(R.id.custom_list);
                for(int i=0;i<monthsArray.length;i++)
                {
                	monthsArray[i]=dS.getSymbolData().get(i).getPOP(currentFrame)+"% "+monthsArray[i];
                	if(Integer.parseInt(dS.getSymbolData().get(i).getPOP(currentFrame))<10)
                	{
                		monthsArray[i]="0"+monthsArray[i];
                	}
                }
                Arrays.sort(monthsArray,Collections.reverseOrder());
				// this-The current activity context.
				// Second param is the resource Id for list layout row item
				// Third param is input array 
				ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, monthsArray);
				monthsListView.setAdapter(arrayAdapter);
			
		}
	  
	  @Override
	  public void onWindowFocusChanged(boolean hasFocus) {
	   super.onWindowFocusChanged(hasFocus);
	   
	   drawSymbols(currentFrame);
	  }
	  
	
	
	  
	  public void setSeek(int value)
	  {
		  //backward of course
		  value=dS.getLayer(currentLayer).getNumFrames()-value;
		  Log.i("CACHE","value is "+value);
		  seekBar.setProgress(value-1);
	  }
	  	 
	 		
		private class SeekbarListener implements SeekBar.OnSeekBarChangeListener
		{
			 public SeekbarListener() {
			       
			    }
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		    	Log.i("ANIMDRAW"," slider "+progress);
		    	currentFrame=progress;
		    	drawSymbols(currentFrame);
		    	updateTime();
		    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
				{
		    		swapKey();
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
				
		//Update time box
		private void updateTime()
		{
			setTitle(dS.getSymbolData().get(0).times.get(currentFrame));
			
		}
		
		private String getTime(Date d,int h)
		{
			String padH="",padM="";
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			Log.i("DATE ",d.toString());
			Log.i("DATE ",calendar.getTime().toString());
			
			calendar.add(Calendar.HOUR, h);
			if(calendar.get(Calendar.HOUR_OF_DAY)<10) padH="0";
			if(calendar.get(Calendar.MINUTE)<10) padM="0";
			return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())+" "+padH+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+padM;
		}
		@SuppressLint("NewApi")
		private void drawSymbols(int cF)
		{
			List<SymbolData> symbols = dS.getSymbolData();
			CrossImageView drawView = (CrossImageView) findViewById(R.id.widgetMap);
			Log.i("DRAWSYMBOLS C ",drawView.getWidth()+" "+drawView.getHeight()+" sb ht "+this.getStatusBarHeight());
			TouchImageView drawViewDims = (TouchImageView) findViewById(R.id.imageView2);
			Log.i("DRAWSYMBOLS D ",drawViewDims.getImageWidth()+" "+drawViewDims.getImageHeight());
			//TouchImageView drawViewDims2 = (TouchImageView) findViewById(R.id.imageView1);
			//Log.i("DRAWSYMBOLS D2 ",drawViewDims2.getImageWidth()+" "+drawViewDims2.getImageHeight());
			Log.i("ANIM","ZOOM "+drawViewDims.getCurrentZoom());
			if(drawViewDims.getCurrentZoom()<=1.0) drawView.zoom=drawViewDims.getCurrentZoom();
            PointF rF=drawViewDims.getScrollPosition();
            Log.i("POINT "," "+rF.x+" "+rF.y);
            RectF rR=drawViewDims.getZoomedRect();
            Log.i("RECT "," "+rR.left+" "+rR.right);
            Log.i("RECT"," "+rR.top+" "+rR.bottom);
            
            RectF bB=calcBBX(rR);
            //calcBBX(rR);
            /*
            if(drawView.getHeight()<1)
            {
            	Toast toast=new Toast(this);
			    TextView  tv=new TextView(this);
	            // set the TextView properties like color, size etc
	            tv.setTextColor(Color.BLACK);
	            tv.setTextSize(26); 
	            tv.setBackgroundColor(Color.WHITE);
                tv.setText("If no symbols are shown on the map tap the screen to display them");
	            tv.setGravity(Gravity.CENTER_VERTICAL);
	            toast.setView(tv);
	            toast.setDuration(Toast.LENGTH_LONG);
			    toast.show();
            	
            }
            else
            {*/
			for (SymbolData item : symbols) {
			    //Log.i("AD",""+item.posX);
			    //float east=(item.posY-Constants.east_start)/Constants.east_span;
				//float north=(item.posX-Constants.north_start)/Constants.north_span;
				float east=(item.posY-bB.left)/(bB.right-bB.left);
				float north=(item.posX-bB.bottom)/(bB.top-bB.bottom);
				//Log.i("AD",">"+east+" "+north+" "+(bB.right-bB.left));
				float posX=drawView.getWidth()*east;
				float posY=drawView.getHeight()*north;
				  //.i("AD",""+posX+" "+posY);
				  item.pixX=(int) posX;		 
				  item.pixY=(int) ((int) (drawView.getHeight()-(int) posY)+(30*drawViewDims.getCurrentZoom()));
			//}
			drawView.cF=currentFrame;
			drawView.symbols=symbols;
			drawView.invalidate();
            }
			
			
		}
		
		private RectF calcBB(RectF rR)
		{
			RectF retR=new RectF();
			retR.left=Constants.east_start+((Constants.east_start+Constants.east_span)*rR.left);
			retR.right=(Constants.east_start+Constants.east_span)*rR.right;
			retR.top=(Constants.north_start+Constants.north_span)-(Constants.north_span*rR.top);
			retR.bottom=(Constants.north_start+Constants.north_span)-(Constants.north_span*rR.bottom);;
			Log.i("BB"," retRleft"+retR.left);
			Log.i("BB"," retRrigh"+retR.right);
			Log.i("BB"," retRbott"+retR.bottom);
			Log.i("BB"," retRtop "+retR.top);
			return retR;			
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
		private void drawCross(float lat, float lon)
		{
			
			//Here try to draw location			
			CrossImageView drawView = (CrossImageView) findViewById(R.id.widgetMap);
			TouchImageView drawViewDims = (TouchImageView) findViewById(R.id.imageView2);
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
			drawSymbols(currentFrame);
		}

		public int getStatusBarHeight() {
			  int result = 0;
			  int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
			  if (resourceId > 0) {
			      result = getResources().getDimensionPixelSize(resourceId);
			  }
			  return result;
			}
        
	
}
