package cc.wulian.smarthomev5.service.location;

import android.location.Location;

public class LocationWrapper
{
	public Location mLocation;

	public LocationWrapper( Location location )
	{
		mLocation = location;
	}

	@Override
	public int hashCode(){
		int hash = new Double(mLocation.getLatitude()).hashCode()
				^ new Double(mLocation.getLongitude()).hashCode();
		return hash;
	}

	@Override
	public boolean equals( Object o ){
		if (o instanceof LocationWrapper){
			LocationWrapper wrapper = (LocationWrapper) o;
			if (wrapper.mLocation != null){
				return this.mLocation.getLatitude() == wrapper.mLocation.getLatitude()
						&& this.mLocation.getLongitude() == wrapper.mLocation.getLongitude();
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

}
