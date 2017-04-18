package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location.LocationHttpManager;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;

public class LocationItem extends AbstractSettingItem{

	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private boolean isLocationOpen = false;
	private ShowLocationDownViewListener locationDownViewListener;
	
	private static final int DRAWABLE_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_DOWN = R.drawable.thermost_setting_arrow_down;

	public LocationItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Location");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setLocation();
		getLocationCityId();
	}

	public void setLocationItem(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}
	
	public void setLocationDownViewListener(ShowLocationDownViewListener locationDownViewListener) {
		this.locationDownViewListener = locationDownViewListener;
	}

	private void getLocationCityId(){
		final String token = SmarthomeFeatureImpl.getData("token");
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				String cityID= LocationHttpManager.getCloudCityId(mGwId,token);
				getLocationCity(cityID);
			}
		});

	}

	private void getLocationCity(final String cityId){
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				if(!StringUtil.isNullOrEmpty(cityId)){
					String city = LocationHttpManager.getCloudLocation(cityId);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = city;
					handler.sendMessage(msg);
				}
			}
		});

	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){
				String city = (String) msg.obj;
				infoTextView.setText(city);
			}
		}
	};

	public void setLocation() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoTextView.setText("");
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_DOWN);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isLocationOpen){
			infoImageView.setBackgroundResource(DRAWABLE_DOWN);
			isLocationOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_UP);
			isLocationOpen = true;
		}
		
		locationDownViewListener.onViewOpenChangeed(isLocationOpen);
	}
	
	public interface ShowLocationDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
	
	
}
