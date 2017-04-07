package com.tdot.rainfalltrackerfree;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.tdot.rainfalltrackerfree.R;

public class AboutActivity extends Activity implements View.OnClickListener{

	String myVersionName = "not available"; // initialize String
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		Context context = getApplicationContext(); // or activity.getApplicationContext()
		PackageManager packageManager = context.getPackageManager();
		String packageName = context.getPackageName();
		setContentView(R.layout.about);
	    WindowManager.LayoutParams params = getWindow().getAttributes();  
        params.height = 800;  
   
        this.getWindow().setAttributes(params);  
		
		
		try {
		    myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
		    TextView tv = (TextView)findViewById(R.id.textView1about);
			tv.setText("Version "+myVersionName);
		} catch (PackageManager.NameNotFoundException e) {
		    e.printStackTrace();
		}
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
	public static void maxinumDialogWindowHeight(Window window) {
	    WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
	    layout.copyFrom(window.getAttributes());
	    layout.height = WindowManager.LayoutParams.FILL_PARENT;
	    window.setAttributes(layout);
	}
	 
}
