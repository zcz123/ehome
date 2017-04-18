package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import java.io.Serializable;


public class LocationEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cityId;
	private String countryCode;
	private String eCityName;
	private String eProvince;
	
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String geteCityName() {
		return eCityName;
	}
	public void seteCityName(String eCityName) {
		this.eCityName = eCityName;
	}
	public String geteProvince() {
		return eProvince;
	}
	public void seteProvince(String eProvince) {
		this.eProvince = eProvince;
	}
	
	
}
