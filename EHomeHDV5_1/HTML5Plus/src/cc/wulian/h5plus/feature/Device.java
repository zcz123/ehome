package cc.wulian.h5plus.feature;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.webkit.JavascriptInterface;
import cc.wulian.h5plus.view.H5PlusWebView;

public class Device {

	public String osName="Android";
	
	@JavascriptInterface
	public String getImei(H5PlusWebView webView,String data){
		TelephonyManager manager= (TelephonyManager) webView.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}
	
	@JavascriptInterface
	public String getOsName(H5PlusWebView webView,String data){
		return this.osName;
	}
	
	@JavascriptInterface
	public String getPhoneModel(H5PlusWebView webView,String data){
		return android.os.Build.MODEL;
	}
}
