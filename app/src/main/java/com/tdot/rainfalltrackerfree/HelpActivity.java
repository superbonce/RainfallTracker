package com.tdot.rainfalltrackerfree;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.tdot.rainfalltrackerfree.R;

public class HelpActivity extends Activity implements View.OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help);
	    WindowManager.LayoutParams params = getWindow().getAttributes();  
        params.height = 700;     
        this.getWindow().setAttributes(params);  

    }
	@Override
	protected void onResume()
	{
	    super.onResume();
	}
	@Override
	public void onClick(View v) {
	  super.onBackPressed();
	}
	 
}

