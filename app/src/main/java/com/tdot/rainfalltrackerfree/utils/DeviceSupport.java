package com.tdot.rainfalltrackerfree.utils;

import android.os.Build;

public class DeviceSupport {
	
	public static String getDeviceName() {
		   String manufacturer = Build.MANUFACTURER;
		   String model = Build.MODEL;
		   if (model.startsWith(manufacturer)) {
		      return model.toUpperCase();
		   } else {
		      return manufacturer.toUpperCase() + " " + model.toUpperCase();
		   }
		}

}
