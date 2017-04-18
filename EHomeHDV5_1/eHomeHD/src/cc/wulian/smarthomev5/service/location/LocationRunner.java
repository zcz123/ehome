package cc.wulian.smarthomev5.service.location;

import java.util.List;
import java.util.Map;

import android.location.Location;
import cc.wulian.smarthomev5.collect.Maps;
import cc.wulian.smarthomev5.utils.LogUtil;

public class LocationRunner
{
	public static interface LocationHintAction
	{
		public void runHintAction(Location originalLocation, boolean isFaraway);
	}

	public static final String LOCATION_TYPE_ENTER = "enter";
	public static final String LOCATION_TYPE_LEAVE = "leave";

	private static final int LOCATION_RUN_INTERVAL = 5 * 60 * 1000;

	private final Map<LocationWrapper, Map<String, LocationHintAction>> mLocationsMap = Maps.newHashMap();
	private final Map<LocationWrapper, Map<String, Long>> mHintedAction = Maps.newHashMap();

	private Location mLastLocation;

	private static LocationRunner mInstance = null;

	public static LocationRunner getInstance(){
		if (mInstance == null){
			synchronized (LocationRunner.class){
				if (mInstance == null) mInstance = new LocationRunner();
			}
		}
		return mInstance;
	}

	private LocationRunner()
	{
	}

	public void addEnterHintLocation( double originalLatitude, double originalLongtitude,
			LocationHintAction enterAction ){
		addHintLocation(originalLatitude, originalLongtitude, enterAction, null);
	}

	public void addLeaveHintLocation( double originalLatitude, double originalLongtitude,
			LocationHintAction leaveAction ){
		addHintLocation(originalLatitude, originalLongtitude, null, leaveAction);
	}

	public void removeEnterLocation( double originalLatitude, double originalLongtitude ){
		removeHintLocation(originalLatitude, originalLongtitude, false, true);
	}

	public void removeLeaveLocation( double originalLatitude, double originalLongtitude ){
		removeHintLocation(originalLatitude, originalLongtitude, false, false);
	}

	public void removeAllLocationHintAction(){
		synchronized (mLocationsMap){
			mLocationsMap.clear();
		}
	}

	public void updateLocation( Location location ){
		mLastLocation = location;
	}

	public void runLocationAction( List<LocationWrapper> hintOriginalLocations, Location currentLocation ){
		LogUtil.logWarn(this.toString() + "---->>>runLocationAction--->start");// LOG
		for (LocationWrapper originalWrapper : hintOriginalLocations){
			Map<String, Long> lastRunTimeMap = mHintedAction.get(originalWrapper);
			long lastRunTime;
			long nowTime = System.currentTimeMillis();

			Map<String, LocationHintAction> locationActionMap = mLocationsMap.get(originalWrapper);
			LogUtil.logWarn(this.toString() + "---->>>runLocationAction--->locationActionMap--->" + locationActionMap);// LOG
			if (locationActionMap != null){
				int result = isLocationFarawayOriginal(originalWrapper.mLocation, currentLocation);
				boolean isFaraway = result == 1;
				LogUtil.logWarn(this.toString() + "---->>>runLocationAction--->isFaraway---->" + result);// LOG
				if (result == 0) continue;

				String key = isFaraway ? LOCATION_TYPE_LEAVE : LOCATION_TYPE_ENTER;

				if (lastRunTimeMap == null){
					lastRunTimeMap = Maps.newHashMap();
					mHintedAction.put(originalWrapper, lastRunTimeMap);
				}

				Long lastRunLong = lastRunTimeMap.get(key);
				lastRunTime = lastRunLong == null ? nowTime : lastRunLong.longValue();
				
				LogUtil.logWarn(this.toString() + "---->>>runLocationAction--->runTimeInteval---->" + (nowTime - lastRunTime) + " ms");// LOG
				if ((nowTime - lastRunTime) < LOCATION_RUN_INTERVAL) continue;
				
				LocationHintAction action = locationActionMap.get(key);
				if (action != null){
					action.runHintAction(originalWrapper.mLocation, isFaraway);
					lastRunTimeMap.put(key, lastRunTime);
				}
			}
		}
	}

	private void addHintLocation( double originalLatitude, double originalLongitude,
			LocationHintAction enterAction, LocationHintAction leaveAction ){
		synchronized (mLocationsMap){
			Location originalLocation = LocationConvertUtil.doubleToLocation(originalLatitude, originalLongitude);

			Map<String, LocationHintAction> locationActionMap = mLocationsMap.get(new LocationWrapper(originalLocation));
			if (locationActionMap == null){
				locationActionMap = Maps.newHashMap();
				mLocationsMap.put(new LocationWrapper(originalLocation), locationActionMap);
			}

			if (enterAction != null){
				locationActionMap.put(LOCATION_TYPE_ENTER, enterAction);
			}

			if (leaveAction != null){
				locationActionMap.put(LOCATION_TYPE_LEAVE, leaveAction);
			}
		}
	}

	private void removeHintLocation( double originalLatitude, double originalLongitude,
			boolean allRemove, boolean enter ){
		synchronized (mLocationsMap){
			Location location = LocationConvertUtil.doubleToLocation(originalLatitude, originalLongitude);

			Map<String, LocationHintAction> locationMap = mLocationsMap.get(new LocationWrapper(location));
			boolean nullMap = locationMap == null || locationMap.isEmpty();
			if (nullMap) return;

			if (allRemove){
				mLocationsMap.remove(new LocationWrapper(location));
				return;
			}

			locationMap.remove(enter ? LOCATION_TYPE_ENTER : LOCATION_TYPE_LEAVE);
		}
	}

	private int isLocationFarawayOriginal( Location originalLocation, Location location ){
		if (mLastLocation == null) return 0;

		double curDistance = location.distanceTo(originalLocation) / 1E6;
		double lastDistance = mLastLocation.distanceTo(originalLocation) / 1E6;

		double result = curDistance - lastDistance;

		int data = 0;
		if (result > 0){
			data = 1;
		}
		else if (result < 0){
			data = -1;
		}
		else{
			data = 0;
		}
		return data;
	}

	// TODO try use coordinate find the move direction
	private boolean isLocationFarawayOriginal2( Location originalLocation, Location location ){
		if (mLastLocation == null) return false;

		double distance = location.distanceTo(mLastLocation) / 1E6;
		double area = (Math.PI * distance) / 2;

		double curX = location.getLatitude();
		double lastX = mLastLocation.getLatitude();

		double curY = location.getLongitude();
		double lastY = mLastLocation.getLongitude();

		double newCurX = location.getLatitude() - mLastLocation.getLatitude();
		double newCurY = location.getLongitude() - mLastLocation.getLongitude();

		return false;
	}
}