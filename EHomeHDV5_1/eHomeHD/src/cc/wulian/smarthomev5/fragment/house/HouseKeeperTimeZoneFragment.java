package cc.wulian.smarthomev5.fragment.house;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.SelectTimeZoneActiviey;
import cc.wulian.smarthomev5.activity.house.HousekeeperSelectTimeZoneActivity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.house.HousekeeperSelectTimeZomeFragment.SelectZoneListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;

public class HouseKeeperTimeZoneFragment extends WulianFragment{

	private LinearLayout synchronizeLayout;
	private TextView synchronizeTextView;
	private TextView zoneSettingRemind;
	private TextView currentZoneTextView;
	private TextView zoneSettingTextView;
	private LinearLayout currentZoneLayout;
	private static ZoneSettingListener zoneSettingListener;
	private String zoneID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_timezone_set, container,false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		synchronizeTextView = (TextView) view.findViewById(R.id.gateway_timezone_setting_autoset_text);
		zoneSettingRemind = (TextView) view.findViewById(R.id.house_upgrade_timezone_remind);
		currentZoneTextView = (TextView) view.findViewById(R.id.setting_curr_zone_tv);
		zoneSettingTextView = (TextView) view.findViewById(R.id.timezone_setting_textview);
		synchronizeLayout = (LinearLayout) view.findViewById(R.id.account_manager_switch_account_item_selector_layout);
		synchronizeLayout.setVisibility(View.GONE);
		synchronizeTextView.setVisibility(View.GONE);
		zoneSettingRemind.setVisibility(View.VISIBLE);
		zoneSettingTextView.setText(mActivity.getResources().getString(R.string.house_rule_upgrade_timezone_setting));
		currentZoneLayout=(LinearLayout) view.findViewById(R.id.go_zone_list);
		currentZoneTextView.setText(getTimeZoneName(getGMTOffSet(System.currentTimeMillis())));
//		currentZoneTextView.setText(getTimeZoneName(null));
		currentZoneLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				HousekeeperSelectTimeZomeFragment.setConditionListener(new SelectZoneListener() {
					
					@Override
					public void onSelectZoneListenerChanged(String zoneId) {
						zoneID = zoneId;
						currentZoneTextView.setText(getTimeZoneName(zoneID));
					}
				});
				Intent intent=new Intent(mActivity,HousekeeperSelectTimeZoneActivity.class);
				startActivity(intent);
			}
		});
	}

	private String getGMTOffSet(long time){
		TimeZone zone = TimeZone.getDefault();
		String zoneID = zone.getID();
		TimeZone currentZone = zone.getTimeZone(zoneID);
		long zoneOffset = currentZone.getOffset(time);
		long zoneHour = zoneOffset/60/60/1000;//时区，东时区数字为正，西时区为负
		long zoneMinute = (zoneOffset -(zoneHour*60*60*1000))/1000/60;
		String zoneHourStr = "";
		if(zoneHour > 0){
			zoneHourStr = "+" + zoneHour;
		}else if(zoneHour == 0 ){
			zoneHourStr = zoneHour + "";
		}else{
			zoneMinute = -zoneMinute;
			zoneHourStr = zoneHour + "";
		}
		if(zoneMinute >= 30){
			zoneSettingRemind.setText(mActivity.getResources().getString(R.string.house_rule_upgrade_timezone_setting_half_remind));
		}
		return zoneHourStr;
	}
	
	private String getTimeZoneName(String zoneIndex){
		String zoneName=null;
		if(zoneIndex!=null&&zoneIndex.length()>0){
			zoneName=getZone().get(zoneIndex);
		}
		if(zoneName==null){
			zoneName=getZone().get(getLocalTimeZoneIndex());
		}
		return zoneName;
	}	

	// 获取本地时区编号
	private String getLocalTimeZoneIndex() {
		TimeZone zone = TimeZone.getDefault();
		long zoneOffset=zone.getOffset(System.currentTimeMillis());
		int zoneOffsetHours = (int) (zoneOffset / 60 / 60 / 1000);
		return zoneOffsetHours > 0 ? "+" + String.valueOf(zoneOffsetHours)
				: String.valueOf(zoneOffsetHours);
	}
	
	private void initBar(){
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.cancel);
		getSupportActionBar().setTitle(R.string.house_rule_upgrade_timezone_setting);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(R.string.house_rule_upgrade_timezone_setting_next_steps);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						if(zoneSettingListener != null){
							zoneSettingListener.onZoneSettingListenerBacked(false,zoneID);
						}
						mActivity.finish();
					}	
		});
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				if(zoneSettingListener != null){
					zoneSettingListener.onZoneSettingListenerBacked(true,null);
				}
				mActivity.finish();
			}
		});
	}

	private Map<String, String> getZone(){ 
		Map<String,String> zoneMap=new HashMap<String, String>();
		zoneMap.put("-11", "GMT-11:00");
		zoneMap.put("-10", "GMT-10:00");
		zoneMap.put("-9", "GMT-9:00");
		zoneMap.put("-8", "GMT-8:00");
		zoneMap.put("-7", "GMT-7:00");
		zoneMap.put("-6", "GMT-6:00");
		zoneMap.put("-5", "GMT-5:00");
		zoneMap.put("-4", "GMT-4:00");
		zoneMap.put("-3", "GMT-3:00");
		zoneMap.put("-2", "GMT-2:00");
		zoneMap.put("-1", "GMT-1:00");
		zoneMap.put("0", "GMT 0:00");
		zoneMap.put("+1", "GMT+1:00");
		zoneMap.put("+2", "GMT+2:00");
		zoneMap.put("+3", "GMT+3:00");
		zoneMap.put("+4", "GMT+4:00");
		zoneMap.put("+5", "GMT+5:00");
		zoneMap.put("+6", "GMT+6:00");
		zoneMap.put("+7", "GMT+7:00");
		zoneMap.put("+8", "GMT+8:00");
		zoneMap.put("+9", "GMT+9:00");
		zoneMap.put("+10", "GMT+10:00");
		zoneMap.put("+11", "GMT+11:00");
		zoneMap.put("+12", "GMT+12:00");
		return zoneMap;
	}
	// 从服务端获取数据
	private void getDatas() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String gwID = AccountManager.getAccountManger().getmCurrentInfo()
						.getGwID();
				SendMessage.sendGetTimeZoneConfigMsg(gwID);
				mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						zoneSettingRemind.setText(mActivity.getResources().getString(R.string.house_rule_upgrade_timezone_setting_remind));
					}
				});
			}
		}).start();
	}

	// 通过时区编号获取时区名称
	private String getTimeZoneIdByIndex(String index) {
		Iterator<Map.Entry<String,String>> iterator=getZone().entrySet().iterator();
		String zoneName="";
		while(iterator.hasNext()){
			Map.Entry<String,String> entry=iterator.next();
			if(entry.getValue().equals(index)){
				zoneName=entry.getKey();
				break;
			}
		}
		return zoneName;
	}

//	public void onEventMainThread(FlowerEvent event) {
//		mDialogManager.dimissDialog(SET_TIME_KEY, 0);
//		if (FlowerEvent.ACTION_FLOWER_TIMEZONE_GET.equals(event.getAction())
//				|| FlowerEvent.ACTION_FLOWER_TIMEZONE_SET.equals(event.getAction())) {
//			if (event.getData() != null) {
//				try {
//					String zoneIndex=event.getData().getString(ConstUtil.KEY_ZONE_ID);		
//					currentZoneTextView.setText(getTimeZoneName(zoneIndex));
//				} catch (Exception e) {
//				}
//			}
//		}
//	}
	
	public static void setZoneSettingListener(ZoneSettingListener zoneSettingListener){
		HouseKeeperTimeZoneFragment.zoneSettingListener = zoneSettingListener;
	}
	public interface ZoneSettingListener{
		public void onZoneSettingListenerBacked(boolean isback, String zoneID);
	}
}
