package com.tdot.rainfalltrackerfree.service;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Makes dealing with locations nice as pie!
 * 
 * @author matt.colliss, scott.bown
 * @category API
 */
public class NicerLocationManager {
    public static final long NUM_OF_ATTEMPTS_TO_GET_LOCATION = 10;
    private static final int MIN_TIME_MS = 1000;
    private final Context mContext;
    private final LocationManager mLocationManager;

    //private Location mLastGpsLocation = null;
    private Location mLastNetworkLocation = null;
    private BestGuessTask mTask;

    /**
     * Construct a new LocationManger.<br />
     * Requires context to grab the LOCATION_SERVICE from.
     * 
     * @param context
     * @category constructor
     */
    public NicerLocationManager(final Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Use to acertain if location services are avalible or not. i.e. are
     * location services turned off on the phone, GPS or Wireless?
     * 
     * @return true if a location based service is available, false if not
     */
    public boolean isAnyLocationServicesAvailbleAll()
    {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        return false;
    }

    /* TG no auto location loading in this version */
    public boolean isAnyLocationServicesAvailbleKINDLEFIRE()
    {
        return false;
    }

    public boolean isAnyLocationServicesAvailble()
    {
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            return true;
        }
        return false;
    }

    /**
     * Will take the time allowed and return (async) the most accurate location
     * it can find in the period set.
     * 
     * @param timeAllowedMillis
     * @param nicerLocationListener
     */
    public void getBestGuessLocation(final long timeAllowedMillis, final NicerLocationListener nicerLocationListener) {
        getBestGuessLocation(timeAllowedMillis, 1000, nicerLocationListener);
    }

    /**
     * Will take the time allowed and accuracy required and return (async) early
     * if an accuracy equal to or higher than the passed in param specified or
     * the best it can get within the time specified
     * 
     * @param timeAllowedMillis
     *            - how long this method call can take before returning a best
     *            guess via the passed in listener
     * @param metersAccuracy
     *            - to how many meters the result should be accurate to
     * @param nicerLocationListener
     *            - for callbacks when the method returns
     */
    public void getBestGuessLocation(long timeAllowedMillis, final int metersAccuracy,
            final NicerLocationListener nicerLocationListener) {
        if (timeAllowedMillis < MIN_TIME_MS) {
            Log.w("GPS","timeAllowedMillis is smaller than the min time allowed of " + MIN_TIME_MS);
            timeAllowedMillis = MIN_TIME_MS + 1;
        }

        // start GPS updates - throws exception if permission not set
        //if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        //{
        //    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_MS,
        //            1, mSingleShotGpsListener);
        //}
        // start network updates - throws exception if permission not set
        try {
        	if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_MS, 1,
                    mSingleShotNetworkListener);
        	}
        } catch (SecurityException e) {
            // no permission for network - do nothing
            Log.d("GPS","Cannot get netowrk location - coarse location permission not set");
        }

        mTask = new BestGuessTask(timeAllowedMillis, metersAccuracy, nicerLocationListener);
        mTask.execute();
    }

    /**
     * Cancels the current query for nearest location
     * 
     * @return <code>false</code> if the query could not be cancelled or is
     *         <code>null</code>, <code>true</code> otherwise
     */
    public boolean cancel() {
        return mTask == null ? false : mTask.cancel(true);
    }

    private class BestGuessTask extends AsyncTask<Void, Void, Location> {
        private final long mTimeAllowedMillis;
        private final NicerLocationListener mListener;
        private final int mMetersAccuracy;

        public BestGuessTask(final long timeAllowedMillis, final int metersAccuracy,
                final NicerLocationListener nicerLocationListener) {
            mTimeAllowedMillis = timeAllowedMillis;
            mListener = nicerLocationListener;
            mMetersAccuracy = metersAccuracy;
        }

        @Override
        protected Location doInBackground(final Void... params) {
            Location location;

            // sleep for allowed amount of time
            try {
                // divide sleep time by number of attempts, if accuracy of
                // location within range return the location
                if (mMetersAccuracy != 0) {
                    long interval = mTimeAllowedMillis / NUM_OF_ATTEMPTS_TO_GET_LOCATION;
                    for (int i = 0; NUM_OF_ATTEMPTS_TO_GET_LOCATION <= i; i++) {
                        Thread.sleep(interval);
                        // check for location within range
                        location = getMostRecentLastKnownLocation();
                        float accuracy = location.getAccuracy();
                        if (accuracy < mMetersAccuracy) {
                            // within accuracy break out of loop early
                            break;
                        }
                    }

                } else {
                    Thread.sleep(mTimeAllowedMillis);
                    // dont get any thing and wil just return last known
                    // location
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TGmLocationManager.removeUpdates(mSingleShotGpsListener);
            mLocationManager.removeUpdates(mSingleShotNetworkListener);

            /*
             * if (null != mLastGpsLocation) { QLog.v(mLastGpsLocation.getProvider() + " " +
             * mLastGpsLocation.getLatitude() + " " + mLastGpsLocation.getLongitude()); }
             */
            if (null != mLastNetworkLocation) {
                Log.v("GPS",mLastNetworkLocation.getProvider() + " " + mLastNetworkLocation.getLatitude() + " "
                        + mLastNetworkLocation.getLongitude());
            }
            /*
             * if (null != mLastGpsLocation) { return mLastGpsLocation; }
             */
            if (null != mLastNetworkLocation) {
                return mLastNetworkLocation;
            }

            return getMostRecentLastKnownLocation();
        }

        @Override
        protected void onPostExecute(final Location result) {
            super.onPostExecute(result);
            if (null == result) {
                mListener.error();
                mListener.onFinished();
            } else {
                mListener.locationLoaded(result);
                mListener.onFinished();
            }
        }
    }

    /*
    private final LocationListener mSingleShotGpsListener = new LocationListener() {
        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        }

        @Override
        public void onProviderEnabled(final String provider) {
        }

        @Override
        public void onProviderDisabled(final String provider) {
        }

        @Override
        public void onLocationChanged(final Location location) {
            mLastGpsLocation = location;
            mLocationManager.removeUpdates(mSingleShotGpsListener);
        }
    };*/
    private final LocationListener mSingleShotNetworkListener = new LocationListener() {
        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        }

        @Override
        public void onProviderEnabled(final String provider) {
        }

        @Override
        public void onProviderDisabled(final String provider) {
        }

        @Override
        public void onLocationChanged(final Location location) {
            mLastNetworkLocation = location;
            mLocationManager.removeUpdates(mSingleShotNetworkListener);
        }
    };

    public Location getMostRecentLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);

        Location location = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            Location locationI = mLocationManager.getLastKnownLocation(providers.get(i));
            if (locationI != null) {
                if (location == null || location.getTime() < locationI.getTime()) {
                    location = locationI;
                }
            }
        }

        if (location != null) {
            Log.v("GPS","lat = " + location.getLatitude() + " long " + location.getLongitude());
        } else {
            Log.v("GPS","LKL was null");
        }

        return location;
    }

    /**
     * Interface for location listener, pass to a
     * {@link NicerLocationManager#getBestGuessLocation(long, NicerLocationListener)}
     * to get call backs from the location service
     * 
     * @author Matt Collis, Scott Bown
     * @category API
     * 
     */
    public interface NicerLocationListener {
        /**
         * On location loaded by the manager.
         * 
         * @param location
         * @category internal
         */
        public void locationLoaded(Location location);

        /**
         * Fired in an error occured, the task will fail after this has been
         * fired. So react accordingly
         * 
         * @category internal
         */
        public void error();

        /**
         * Fired finally regardless of whether successful of not
         * 
         * @category internal
         */
        public void onFinished();
    }
}

