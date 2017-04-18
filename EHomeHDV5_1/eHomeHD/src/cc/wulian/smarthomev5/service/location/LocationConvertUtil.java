package cc.wulian.smarthomev5.service.location;

import android.location.Location;

public class LocationConvertUtil
{
	public static Location doubleToLocation( double latitude, double longitude ){
		Location location = new Location("");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;
	}
}
