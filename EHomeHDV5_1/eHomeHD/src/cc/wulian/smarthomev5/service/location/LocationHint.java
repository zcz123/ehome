package cc.wulian.smarthomev5.service.location;

import java.util.List;

import android.location.Location;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.utils.LogUtil;

public class LocationHint
{
	private static final int LOCATION_MAX_DISTANCE = 200;

	private static LocationHint mIntance = null;

	public static LocationHint getIntance(){
		if (mIntance == null){
			synchronized (LocationHint.class){
				if (mIntance == null) mIntance = new LocationHint();
			}
		}
		return mIntance;
	}

	private final List<LocationWrapper> mLocations = Lists.newArrayList();

	private LocationHint()
	{
	}

	public void addHintOriginalLocation( double latitude, double longitude ){
		Location location = new Location("");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		addHintOriginalLocation(location);
	}

	public void addHintOriginalLocation( Location location ){
		synchronized (mLocations){
			int index = mLocations.indexOf(new LocationWrapper(location));
			if (index != -1){
				mLocations.set(index, new LocationWrapper(location));
			}
			else{
				mLocations.add(new LocationWrapper(location));
			}
		}
	}

	public void removeHintOriginalLocation( double latitude, double longitude ){
		Location location = LocationConvertUtil.doubleToLocation(latitude, longitude);
		removeHintOriginalLocation(location);
	}

	public void removeHintOriginalLocation( Location location ){
		synchronized (mLocations){
			mLocations.remove(new LocationWrapper(location));
		}
	}

	public void removeAllHintOriginalLocation(){
		synchronized (mLocations){
			mLocations.clear();
		}
	}

	public List<LocationWrapper> compareHintLocation( double latitude, double longitude ){
		Location location = LocationConvertUtil.doubleToLocation(latitude, longitude);
		return compareHintLocation(location);
	}

	public List<LocationWrapper> compareHintLocation( Location location ){
		return _compareHintLocation(location);
	}

	private List<LocationWrapper> _compareHintLocation( Location location ){
		if (location == null) return null;

		LogUtil.logWarn(this.toString() + "---->>>_compareHintLocation--->start");// LOG

		List<LocationWrapper> result = Lists.newArrayList();
		for (LocationWrapper originalLocation : mLocations){
			double distance = location.distanceTo(originalLocation.mLocation) / 1E6;
			LogUtil.logWarn(this.toString() + "---->>>_compareHintLocation--->distance-->" + distance
					+ "originalLocation:[" + originalLocation + "]---location:" + location);// LOG
			if (distance <= LOCATION_MAX_DISTANCE){
				result.add(new LocationWrapper(location));
			}
		}
		return result;
	}
}
