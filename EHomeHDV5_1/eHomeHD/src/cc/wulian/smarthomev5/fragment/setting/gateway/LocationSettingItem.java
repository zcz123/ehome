package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.LocationSettingActivity;
import cc.wulian.smarthomev5.activity.SwitchAccountActivity;
import cc.wulian.smarthomev5.event.GatewayCityEvent;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.URLConstants;

public class LocationSettingItem extends AbstractSettingItem {

	private final AccountManager mAccountManger = AccountManager.getAccountManger();
	private String cityIDData; //缓存cityID
	private String cityData;   //缓存city
	private String cityIDGw;   //网关cityID
	private String city;
	private String cityID;

	public LocationSettingItem(Context context) {
		super(context, R.drawable.icon_gateway_location, context.getResources()
				.getString(R.string.gateway_dream_flower_position_set));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setCity();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,20, 0);
		infoTextView.setLayoutParams(params);
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setText(city);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.arrow_cutover_gateway);

	}

	public void setCity(){
		getCityFromData();
		cityIDGw = mAccountManger.getmCurrentInfo().getGwCityID();
		if(!StringUtil.isNullOrEmpty(cityIDGw)){
			if(!StringUtil.isNullOrEmpty(cityIDData)){
				if(!StringUtil.equals(cityIDGw , cityIDData)){
					cityID = cityIDGw;
					postWuLianCloudGwLocation(cityID);
				}else{
					if(!StringUtil.isNullOrEmpty(cityData)){
						city = cityData;
					}
				}
			}else{
				cityID = cityIDGw;
				postWuLianCloudGwLocation(cityID);
			}
		}else{
			if(!StringUtil.isNullOrEmpty(cityData)){
				city = cityData;
			}
		}
	}

	private void getCityFromData(){
		String cityJson = SmarthomeFeatureImpl.getData("gw_location_city_info");
		if(!StringUtil.isNullOrEmpty(cityJson)){
			JSONObject jsonObject = JSON.parseObject(cityJson);
			cityData = jsonObject.getString("city");
			cityIDData = jsonObject.getString("cityID");
		}

	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){
				infoTextView.setText(city);
			}
		}
	};

	private void postWuLianCloudGwLocation(final String cityId){
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				String testUri = URLConstants.AMS_URLBASE_VALUE;
				String url = URLConstants.AMS_URLBASE_VALUE + URLConstants.AMS_PATH+"/user/access";
				Map<String,String> headerMap = new HashMap<String,String>();
				headerMap.put("cmd","getAreaInfo");
				com.alibaba.fastjson.JSONObject bodyObject = new com.alibaba.fastjson.JSONObject();
				bodyObject.put("level","city");
				bodyObject.put("cityId",cityId);
				byte[] body = bodyObject.toJSONString().getBytes();
				com.alibaba.fastjson.JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);
				String responseBody = result.getString("body");

				if(!StringUtil.isNullOrEmpty(responseBody)){
					com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
					com.alibaba.fastjson.JSONArray jsonArray = JSON.parseObject(responseBody).getJSONArray("records");
					String cityInfo = jsonArray.getString(0);
					String province = JSON.parseObject(cityInfo).getString("province");
					city = JSON.parseObject(cityInfo).getString("cityName");
					jsonObject.put("province",province);
					jsonObject.put("city",city);
					jsonObject.put("cityID",cityId);
					SmarthomeFeatureImpl.setData("gw_location_city_info",jsonObject.toJSONString());
					handler.sendEmptyMessage(0);
				}
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		startActivity();
	}

	private void startActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, LocationSettingActivity.class);
		mContext.startActivity(intent);
	}

}
