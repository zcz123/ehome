package cc.wulian.smarthomev5.thirdparty.uei_yaokan;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.Toast;


public class YkanIRInterfaceImpl{

	private String TAG = YkanIRInterfaceImpl.class.getSimpleName();
	
	private Context ctx;
	
	private String domain = "city.sun-cam.com.cn"; 

	private String url_prefix = "http://" + domain + "/open/m2.php?";

	private HttpUtil httpUtil;
	
	private YkanSDKManager sdkManager;
	
	public YkanIRInterfaceImpl(){
		sdkManager =  YkanSDKManager.getInstance();
		if(sdkManager == null){
			Toast.makeText(ctx, "没有调用  YkanSDKManager.init(Context  ctx,String appID)方法，请先执行init()", Toast.LENGTH_SHORT).show();
			return;
		}
		ctx = sdkManager.getContext();
		httpUtil = new HttpUtil(ctx);
	}

	private String getPostUrl(String url_sufx) {
		return url_prefix + url_sufx;
	}
	/**
	 * 根据运营商ID获取频道列表
	 * @param paramPid 运营商ID
	 * @return
	 */
	public String getAllChannelsByPid(String paramPid, int zip) {
		String result="";
		String func="c=cl";
		String url = getPostUrl(func);
		List<String> params=new ArrayList<String>();
		params.add(paramPid);
		params.add("zip="+zip);
		params.add("pn=1");
		params.add("ps=0");
		result=httpUtil.postMethod(url, params);
		return result;
	}

	public String HttpUtil_postMethod(String func,List<String> params){
		String url = getPostUrl(func);
		String result=httpUtil.postMethod(url, params);
		return result;
	}

}
