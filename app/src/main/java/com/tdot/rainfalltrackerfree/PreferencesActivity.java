package com.tdot.rainfalltrackerfree;


import com.tdot.rainfalltrackerfree.R;
import com.tdot.rainfalltrackerfree.service.SettingsFragment;
import com.tdot.rainfalltrackerfree.utils.DeviceSupport;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/*
 * TG Nov 14 Prefs
 */

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	SharedPreferences.OnSharedPreferenceChangeListener listener =
		    new SharedPreferences.OnSharedPreferenceChangeListener() {
		  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				Preference usedPref = findPreference(key);
				String newSelection;
				Log.i("PREF",key);
				if(key.compareTo("prefs_map_key")==0)
				{
				    newSelection=prefs.getString("prefs_map_key", "classic");
				    usedPref.setTitle(newSelection);
				}
				else if((key.compareTo("prefs_appstart_key")==0))
				{
					newSelection=prefs.getString("prefs_appstart_key", "On home screen");
				    usedPref.setTitle(newSelection);
				}	
				else if((key.compareTo("prefs_speed_key")==0))
				{
					newSelection=prefs.getString("prefs_speed_key", "Medium");
				    usedPref.setTitle(newSelection);
				}
		  }
		};
		
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
	 {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preferences);
	    
	    if(DeviceSupport.getDeviceName().contains("AMAZON"))
        {
	      CheckBoxPreference cb = (CheckBoxPreference) findPreference(getString(R.string.pref_loc_key));
	      cb.setEnabled(false);
	      cb.setChecked(false);
        }
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    //Map
	    Preference appPrefMap = findPreference(this.getString(R.string.pref_map_key));
	    String newSelection=prefs.getString("prefs_map_key", "classic");
        appPrefMap.setTitle(newSelection);
        //Start up
        Preference appPrefappstart = findPreference(this.getString(R.string.pref_appstart_key));
	    String newSelectionStart =prefs.getString("prefs_appstart_key", "main screen");
        appPrefappstart.setTitle(newSelectionStart);
        //Speed
        Preference appPrefappspeed = findPreference(this.getString(R.string.pref_speed_key));
	    String newSelectionSpeed =prefs.getString("prefs_speed_key", "Medium");
        appPrefappspeed.setTitle(newSelectionSpeed);
        
	    prefs.registerOnSharedPreferenceChangeListener(listener);
	}
	 
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key)
	{
		Preference usedPref = findPreference(key);
		usedPref.setTitle(sharedPreferences.getString(key, ""));		
	}
}
