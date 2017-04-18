package cc.wulian.smarthomev5.event;

public class GPSEvent
{
	public double mLatitude;
	public double mLongitude;

	public GPSEvent( double latitude, double longitude )
	{
		mLatitude = latitude;
		mLongitude = longitude;
	}
}
