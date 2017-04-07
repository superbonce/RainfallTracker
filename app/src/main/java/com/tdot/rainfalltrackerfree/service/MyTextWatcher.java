package com.tdot.rainfalltrackerfree.service;
import com.tdot.rainfalltrackerfree.AnimActivity;
import com.tdot.rainfalltrackerfree.model.Constants;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
public class MyTextWatcher implements TextWatcher {
	
	private Context mContext;
	EditText mEdittextview;
	String searchOn="";
	
	public MyTextWatcher(Context context, EditText edittextview) {
	    super();
	    this.mContext = context;
	    this.mEdittextview= edittextview;
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		Log.i("TEXT","brf tx change");
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.i("TEXT","on tx change"+s.toString());
		searchOn=s.toString();

		 Thread thread = new Thread(new Runnable(){
		     @Override
		     public void run() {
		         try {
		        	 URLConnector dataIn=new URLConnector();
		        	 String acURL = String.format(
		                     Constants.AUTOCOMPLETE,
		                     searchOn
		             );
		  	    	 Log.i("CALLOUT",acURL);
		     		xmlLoaded(dataIn.connect(acURL));
		         } catch (Exception e) {
		             e.printStackTrace();
		         }
		     }
		 });

		 thread.start(); 
	}
	
	 public void xmlLoaded(String loadedXML)
	    {
	    	Log.i("XML","google xml has loaded now parse"+loadedXML);
	    	
	        
	    }

	@Override
	public void afterTextChanged(Editable s) {
		Log.i("TEXT","After tx change");
		
	}

}
