package cc.wulian.smarthomev5.service.location;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.event.GPSEvent;
import cc.wulian.smarthomev5.service.location.LocationRunner.LocationHintAction;
import cc.wulian.smarthomev5.utils.LogUtil;
import de.greenrobot.event.EventBus;

public class LocationClient
{
	private static LocationClient mInstance;

	public static LocationClient getInstance(){
		if (mInstance == null){
			synchronized (LocationClient.class){
				if (mInstance == null) mInstance = new LocationClient();
			}
		}
		return mInstance;
	}

	private final MainApplication mApplication;

	private final LocationManager mLocationManager;
	private LocationClientOption mOption;

	private boolean mStarted;

	private final LocationType mAGPSLocationType;

	private final LocationHint mLocationHint;
	private final LocationRunner mLocationRunner;

	private LocationClient()
	{
		mApplication = MainApplication.getApplication();
		mLocationManager = (LocationManager) mApplication.getSystemService(Context.LOCATION_SERVICE);

		mLocationHint = LocationHint.getIntance();
		mLocationRunner = LocationRunner.getInstance();

		mOption = new LocationClientOption();

		mAGPSLocationType = new A_GPSLocationType(this);
	}

	public void setLocOption( LocationClientOption option ){
		mOption = option;
	}

	public LocationClientOption getClientOption(){
		return mOption;
	}

	public void start(){
		if (!mOption.isOpenGps()){
			return;
		}

		if (!mStarted){
			LogUtil.logWarn("LocationClient---->>>start");// LOG
			mStarted = true;
			boolean aGpsUsable = canLocation();
			if (aGpsUsable){
				mAGPSLocationType.requestLocationUpdates();
			}
			else{
				LogUtil.logWarn("LocationClient---->>>aGpsUsable--->false");// LOG
			}
		}
	}

	public boolean canLocation(){
		return mAGPSLocationType.canLocation();
	}

	public void stop(){
		if (mStarted){
			mStarted = false;
			mAGPSLocationType.removeLocationUpdates();
			LogUtil.logWarn("LocationClient---->>>stop");
		}
	}

	public boolean isStarted(){
		return mStarted;
	}

	public void restart(){
		if (!mOption.isOpenGps()) {
			return;
		}

		LogUtil.logWarn("LocationClient---->>>restart");
		if (isStarted()){
			stop();
		}
		start();
	}

	private static interface LocationType
	{
		void requestLocationUpdates();

		void removeLocationUpdates();

		Location getLocation();

		boolean canLocation();
	}

	private static class A_GPSLocationType implements LocationType, LocationListener
	{
		private static final int CHECK_INTERVAL = 1000 * 30;

		protected LocationClient mLocationClient;
		protected final LocationManager mLM;

		protected Location mCurrentLocation;

		public A_GPSLocationType( LocationClient lc )
		{
			mLocationClient = lc;
			mLM = lc.mLocationManager;
		}

		@Override
		public Location getLocation(){
			return mCurrentLocation;
		}

		@Override
		public void requestLocationUpdates(){
			this.updateLocation(mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER), false);
			Criteria c = new Criteria();
			c.setAccuracy(Criteria.ACCURACY_COARSE);
			c.setBearingAccuracy(Criteria.ACCURACY_LOW);
			c.setAltitudeRequired(false);
			c.setPowerRequirement(Criteria.POWER_LOW);
			String provider = mLM.getBestProvider(c, true);
			if (mLM.isProviderEnabled(provider)) {
				mLM.requestLocationUpdates(provider, CHECK_INTERVAL, 0, this);
			} 
		}

		protected boolean isBetterLocation( Location location, Location currentBestLocation ){
			if (currentBestLocation == null){
				// A new location is always better than no location
				return true;
			}

			//if new location is null，currentBestLocation still is the better
			if(location==null){
				return false;
			}
			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
			boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location,
			// use the new location
			// because the user has likely moved
			if (isSignificantlyNewer){
				return true;
				// If the new location is more than two minutes older, it must
				// be worse
			}
			else if (isSignificantlyOlder){ return false; }

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(),
					currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and
			// accuracy
			if (isMoreAccurate){
				return true;
			}
			else if (isNewer && !isLessAccurate){
				return true;
			}
			else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider){ return true; }
			return false;
		}

		/** Checks whether two providers are the same */
		private boolean isSameProvider( String provider1, String provider2 ){
			if (provider1 == null){ return provider2 == null; }
			return provider1.equals(provider2);
		}

		public void updateLocation( Location location, boolean force ){
			LogUtil.logWarn(this.toString() + "---->>>updateLocation--->" + location);// LOG

			if (mCurrentLocation != null){
				if (isBetterLocation(location, mCurrentLocation)){
					mCurrentLocation = location;
					LogUtil.logWarn(this.toString() + "---->>>isBetterLocation");// LOG
				}
				else{
					LogUtil.logWarn(this.toString() + "---->>>isBadLocation");// LOG
				}
			}
			else{
				mCurrentLocation = location;
			}

			mLocationClient.updateLocation(mCurrentLocation);
			if (mCurrentLocation != null){
				try {
				Log.d("Location", "Location::requestLocationUpdates:: " + mCurrentLocation.getLatitude()
						+ ":" + mCurrentLocation.getLongitude()+":"+mCurrentLocation.getProvider()+"  "+mCurrentLocation.getExtras());
				} catch (Throwable e) {
					Log.e("", "", e);
				}
				EventBus.getDefault().post(
						new GPSEvent(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

				if (!mLocationClient.getClientOption().isLocationNotify()){
					LogUtil.logWarn("Location::not start compareHintLocation");// LOG
					return;
				}

				List<LocationWrapper> result = mLocationClient.compareHintLocation(mCurrentLocation);
				if (result != null && !result.isEmpty()){
					mLocationClient.runLocationAction(result, mCurrentLocation);
				}
				else{
					LogUtil.logWarn("Location::compareHintLocation--->stop--->empty data");// LOG
				}
			}
		}

		@Override
		public void removeLocationUpdates(){
			mLM.removeUpdates(this);
		}

		@Override
		public boolean canLocation(){
			boolean gps = mLM.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean network = mLM.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			return gps | network;
		}

		@Override
		public void onLocationChanged( Location location ){
			updateLocation(location, false);
		}

		@Override
		public void onStatusChanged( String provider, int status, Bundle extras ){
			LogUtil.logWarn("Location:::onStatusChanged: " + provider + "::" + status);
		}

		@Override
		public void onProviderEnabled( String provider ){
			LogUtil.logWarn("Location:::onProviderEnabled: " + provider);
		}

		@Override
		public void onProviderDisabled( String provider ){
			LogUtil.logWarn("Location:::onProviderDisabled: " + provider);
		}
	}

	/*private static class A_GPSLocationType extends AbstractLocationType
	{

		private Criteria mCriteria;

		public A_GPSLocationType( LocationClient lc )
		{
			super(lc);
		}

		@Override
		public String initProvider(){
			mCriteria = new Criteria();

			mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
			mCriteria.setAltitudeRequired(false);
			mCriteria.setBearingRequired(false);
			mCriteria.setCostAllowed(true);
			mCriteria.setPowerRequirement(Criteria.POWER_LOW);
			mCriteria.setSpeedRequired(false);
			return mLM.getBestProvider(mCriteria, true);
		}

		@Override
		public void requestLocationUpdates(){
			Location location = getBestLocation();
			updateLocation(location, true);
		}

		private Location getBestLocation(){
			Location gpslocation = mLM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location networkLocation = mLM.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (gpslocation == null){
				return networkLocation;
			}
			if (networkLocation == null){ 
				return gpslocation; 
			}

			long old = System.currentTimeMillis() - mLocationClient.getClientOption().getScanSpan();
			boolean gpsIsOld = gpslocation.getTime() < old;
			boolean networkIsOld = networkLocation.getTime() < old;
			if (!gpsIsOld){ 
				return gpslocation; 
			}
			if (!networkIsOld){ 
				return networkLocation; 
			}
			if (gpslocation.getTime() > networkLocation.getTime()){
				return gpslocation;
			}
			else{
				return networkLocation;
			}
		}
	}*/

	public void addHintOriginalLocation( double latitude, double longitude ){
		mLocationHint.addHintOriginalLocation(latitude, longitude);
	}

	public void addHintOriginalLocation( Location location ){
		mLocationHint.addHintOriginalLocation(location);
	}

	public void removeHintOriginalLocation( double latitude, double longitude ){
		mLocationHint.removeHintOriginalLocation(latitude, longitude);
	}

	public void removeHintOriginalLocation( Location location ){
		mLocationHint.removeHintOriginalLocation(location);
	}

	public List<LocationWrapper> compareHintLocation( double latitude, double longitude ){
		return mLocationHint.compareHintLocation(latitude, longitude);
	}

	public List<LocationWrapper> compareHintLocation( Location location ){
		return mLocationHint.compareHintLocation(location);
	}

	public void addEnterHintLocation( double originalLatitude, double originalLongtitude,
			LocationHintAction enterAction ){
		mLocationRunner.addEnterHintLocation(originalLatitude, originalLongtitude, enterAction);
	}

	public void addLeaveHintLocation( double originalLatitude, double originalLongtitude,
			LocationHintAction leaveAction ){
		mLocationRunner.addLeaveHintLocation(originalLatitude, originalLongtitude, leaveAction);
	}

	public void removeEnterLocation( double originalLatitude, double originalLongtitude ){
		mLocationRunner.removeEnterLocation(originalLatitude, originalLongtitude);
	}

	public void removeLeaveLocation( double originalLatitude, double originalLongtitude ){
		mLocationRunner.removeLeaveLocation(originalLatitude, originalLongtitude);
	}

	public void runLocationAction( List<LocationWrapper> locations, Location currentLocation ){
		mLocationRunner.runLocationAction(locations, currentLocation);
	}

	public void updateLocation( Location location ){
		mLocationRunner.updateLocation(location);
	}
}
