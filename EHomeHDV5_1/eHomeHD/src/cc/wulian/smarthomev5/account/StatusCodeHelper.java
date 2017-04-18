package cc.wulian.smarthomev5.account;

import java.util.HashMap;

import android.content.Context;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;

public class StatusCodeHelper {

	private HashMap<Integer, String> statusCodeMap = new HashMap<>();

	private static StatusCodeHelper instance = null;

	private StatusCodeHelper() {
		Context c = MainApplication.getApplication();

		statusCodeMap.put(2000, c.getString(R.string.html_map_2000_error));
		statusCodeMap.put(2001, c.getString(R.string.html_map_2001_error));
		statusCodeMap.put(2002, c.getString(R.string.html_map_2002_error));
		statusCodeMap.put(2005, c.getString(R.string.html_map_2005_error));
		statusCodeMap.put(2006, c.getString(R.string.html_map_2006_error));
		statusCodeMap.put(2007, c.getString(R.string.html_map_2007_error));
		statusCodeMap.put(2008, c.getString(R.string.html_map_2008_error));
		statusCodeMap.put(2009, c.getString(R.string.html_map_2000_error));
		statusCodeMap.put(2101, c.getString(R.string.html_map_2101_error));
		statusCodeMap.put(2103, c.getString(R.string.html_map_2103_error));
		statusCodeMap.put(2107, c.getString(R.string.html_map_2107_error));
		statusCodeMap.put(2205, c.getString(R.string.html_map_2205_error));
		statusCodeMap.put(2900, c.getString(R.string.html_map_2900_error));
		statusCodeMap.put(2910, c.getString(R.string.html_map_2910_error));
		statusCodeMap.put(2915, c.getString(R.string.html_map_2915_error));
		statusCodeMap.put(2916, c.getString(R.string.html_map_2916_error));
		statusCodeMap.put(3005, c.getString(R.string.html_map_3005_error));
		statusCodeMap.put(3010, c.getString(R.string.html_map_2010_error));
		statusCodeMap.put(3011, c.getString(R.string.html_map_3011_error));
		statusCodeMap.put(3012, c.getString(R.string.html_map_3012_error));
		statusCodeMap.put(3055, c.getString(R.string.html_map_3055_error));
		statusCodeMap.put(404, "Auhentication failed, invalid identification info.");
	}

	public static StatusCodeHelper getInstance() {
		if (instance == null) {
			instance = new StatusCodeHelper();
		}
		return instance;
	}

	public String mapping(Integer statusCode) {
		if (statusCodeMap.containsKey(statusCode)) {
			return statusCodeMap.get(statusCode);
		}
		return null;
	}

	public String mapping(Integer statusCode, String defaultHint) {
		if (statusCodeMap.containsKey(statusCode)) {
			return statusCodeMap.get(statusCode);
		}
		return defaultHint + statusCode;
	}
}
