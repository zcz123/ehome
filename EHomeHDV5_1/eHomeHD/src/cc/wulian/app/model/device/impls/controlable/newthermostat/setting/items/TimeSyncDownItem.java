package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class TimeSyncDownItem extends AbstractSettingItem{
	private final String TAG = getClass().getSimpleName();
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String syncTime;
	
	private TextView tvSyncTextView;
	private TextView tvSystemTime;
	private TextView tvSyncTime;
	private Button syncBtn;
	private static final String TIME_SYNC_CMD = "7";
	
	public static final SimpleDateFormat TIMESTAMP_DF = new SimpleDateFormat("HH:mm MM dd,yyyy");
	public static final SimpleDateFormat TIMESTAMP_CMD = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final String SYNC_BTN_TEXT = "synchron";
	private static final String SYNC_VIEW_TEXT = "The operation will make the thermostat achieve time "
			+ "synchronization with your cell phone";
	
	public TimeSyncDownItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Time Setting");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setTimeSyncDown();
	}
	
	public void setTimeSyncData(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}
	
	public void setDeviceTime(String time){
		if(!StringUtil.isNullOrEmpty(time)){
			String mYear = "20"+ hexStr2Str(time.substring(0, 2));
			String mMonth = hexStr2Str(time.substring(2, 4));
			String mDay = hexStr2Str(time.substring(4, 6));
			String mHour = hexStr2Str(time.substring(6, 8));
			String mMin = hexStr2Str(time.substring(8, 10));
			syncTime =mHour+":"+mMin+" "+mMonth+" "+mDay+","+mYear;
			tvSyncTime.setText(syncTime);
		}
		tvSystemTime.setText(getSystemTime());
	}
	
	private String getSystemTime(){
		return TIMESTAMP_DF.format(new Date());
	}
	
	public void setTimeSyncDown() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.device_thermostat82_setting_timesync, null);
		tvSyncTextView = (TextView) view.findViewById(R.id.thermost_setting_sync_tv3);
		tvSyncTime = (TextView) view.findViewById(R.id.thermost_setting_sync_tv1);
		tvSystemTime = (TextView) view.findViewById(R.id.thermost_setting_sync_tv2);
		syncBtn = (Button) view.findViewById(R.id.thermost_setting_sync_btn);
		syncBtn.setText(SYNC_BTN_TEXT);
		tvSyncTextView.setText(SYNC_VIEW_TEXT);
		syncBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String currentTime = TIMESTAMP_CMD.format(new Date());
				String timeCmd = currentTime.substring(2,currentTime.length());
				Log.i("thermostatsyncTime2", timeCmd);
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, TIME_SYNC_CMD+timeCmd);
				//tvSyncTime.setText(getSystemTime());
				tvSystemTime.setText(getSystemTime());
			}
		});
		
	}
	
	private String getTimeCmd(String time){
		StringBuilder cmdBuilder = new StringBuilder();
		String sendTime = time.substring(2,time.length());
		String year = str2HexStr(sendTime.substring(0, 2));
		String month = str2HexStr(sendTime.substring(2, 4));
		String day = str2HexStr(sendTime.substring(4, 6));
		String hour = str2HexStr(sendTime.substring(6, 8));
		String min = str2HexStr(sendTime.substring(8, 10));
		cmdBuilder.append(year).append(month).append(day).append(hour).append(min);
		return cmdBuilder.toString();
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
	
	 private String hexStr2Str(String hexStr) {  
	        StringBuilder strBuilder = new StringBuilder();
	        String str = Integer.parseInt(hexStr,16)+"";
	        if(str.length() == 1){
	        	strBuilder.append("0");
	        }
	        strBuilder.append(str);
	        return strBuilder.toString(); 
	    } 
	
	 private String str2HexStr(String str){
		 StringBuilder strBuilder = new StringBuilder();
		 int i = Integer.parseInt(str);
		 String s =Integer.toHexString(i);
		 if(s.length() == 1){
			 strBuilder.append("0");
	        }
		 strBuilder.append(s);
		 return strBuilder.toString().toUpperCase();
	 }
	 
}
