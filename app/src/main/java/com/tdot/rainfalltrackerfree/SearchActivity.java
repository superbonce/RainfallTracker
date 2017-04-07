package com.tdot.rainfalltrackerfree;

import com.tdot.rainfalltrackerfree.R;
import com.tdot.rainfalltrackerfree.model.DataStore;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SearchActivity extends Activity {
	
	EditText Et;
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.search_main);
			/*if (savedInstanceState == null) {
				getFragmentManager().beginTransaction()
						.add(R.id.container, new PlaceholderFragment()).commit();
			}*/
			Et = (EditText) findViewById(R.id.editText1);
			if(Et!=null)
			{
			Et.addTextChangedListener(new com.tdot.rainfalltrackerfree.service.MyTextWatcher(this,Et));
			}
	  }
	  
	 
	  public static class PlaceholderFragment extends Fragment {

			public PlaceholderFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.search_main, container,
						false);
				return rootView;
			}
		}
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu items for use in the action bar
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.main, menu);
	      return super.onCreateOptionsMenu(menu);
	  }

}
