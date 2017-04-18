package cc.wulian.app.model.device.interfaces;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectSensorDeviceDataFragment;
import cc.wulian.smarthomev5.tools.SingleChooseManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class TemhumWheelView{

	private String[] tempValus = { "-10","-09", "-08", "-07", "-06", "-05", "-04", "-03", "-02", "-01","00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24", "25", "26", "27","28", "29", "30"};
	private String[] humValus = { "45", "46","47", "48", "49", "50", "51", "52", "53", "54", "55", 
			"56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68","69", "70"};
	
    private WheelView temp1 = null;
    private WheelView temp2 = null;
    private WheelView hum1 = null;
    private WheelView hum2 = null;

    private NumberAdapter temp1Adapter;
    private NumberAdapter temp2Adapter;
    private NumberAdapter hum1Adapter;
    private NumberAdapter hum2Adapter;
    private Context mContext;
	private LayoutInflater inflater;
    private String temp1Value;
    private String temp2Value;
    private String hum1Value;
    private String hum2Value;
    
    private LinearLayout rootView;
    private ImageView greaterTempImageView;
    private ImageView lessTempImageView;
    private ImageView greaterHumImageView;
    private ImageView lessHumImageView;
    private Button ensureButton;
    private static final String SPLIT_MORE = ">";
    private static final String DERAULT_VALUES = "10";
    private DeviceCache mDeviceCache ;
    private SingleChooseManager manager;
    private String values = "";
    private String describe = "";
    private String ep = "";
    private String epType = "";
    
	public TemhumWheelView(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mDeviceCache = DeviceCache.getInstance(context);
		rootView = (LinearLayout)inflater.inflate( R.layout.task_manager_temhum_view, null);
		
		greaterTempImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image1);
		lessTempImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image2);
		greaterHumImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image3);
		lessHumImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image4);
		
		manager= new SingleChooseManager(R.drawable.task_manager_select,R.drawable.task_manager_no_select);
		manager.addImageView(greaterTempImageView);
		manager.addImageView(lessTempImageView);
		manager.addImageView(greaterHumImageView);
		manager.addImageView(lessHumImageView);
		OnClickListener checkClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				manager.setChecked(v.getId());
			}
		};
		greaterTempImageView.setOnClickListener(checkClickListener);
		lessTempImageView.setOnClickListener(checkClickListener);
		greaterHumImageView.setOnClickListener(checkClickListener);
		lessHumImageView.setOnClickListener(checkClickListener);
		
		temp1 = (WheelView) rootView.findViewById(R.id.sensor_device_choose_wheel1);
		temp2 = (WheelView) rootView.findViewById(R.id.sensor_device_choose_wheel2);
		hum1 = (WheelView) rootView.findViewById(R.id.sensor_device_choose_wheel3);
		hum2 = (WheelView) rootView.findViewById(R.id.sensor_device_choose_wheel4);
		temp1.setScrollCycle(true);
		temp2.setScrollCycle(true);
		hum1.setScrollCycle(true);
		hum2.setScrollCycle(true);
		temp1Adapter = new NumberAdapter(tempValus);
		temp2Adapter = new NumberAdapter(tempValus);
		hum1Adapter = new NumberAdapter(humValus);
		hum2Adapter = new NumberAdapter(humValus);
		temp1.setAdapter(temp1Adapter);
		temp2.setAdapter(temp2Adapter);
		hum1.setAdapter(hum1Adapter);
		hum2.setAdapter(hum2Adapter);
		temp1.setSelection(StringUtil.toInteger(DERAULT_VALUES), true);
		temp2.setSelection(StringUtil.toInteger(DERAULT_VALUES), true);
		hum1.setSelection(StringUtil.toInteger(DERAULT_VALUES), true);
		hum2.setSelection(StringUtil.toInteger(DERAULT_VALUES), true);
        ((WheelTextView)temp1.getSelectedView()).setTextSize(20);
        ((WheelTextView)temp2.getSelectedView()).setTextSize(20);
        ((WheelTextView)hum1.getSelectedView()).setTextSize(20);
        ((WheelTextView)hum2.getSelectedView()).setTextSize(20);
        temp1.setOnItemSelectedListener(mListener);
        temp2.setOnItemSelectedListener(mListener);
        hum1.setOnItemSelectedListener(mListener);
        hum2.setOnItemSelectedListener(mListener);
        temp1.setUnselectedAlpha(0.5f);
        temp2.setUnselectedAlpha(0.5f);
        hum1.setUnselectedAlpha(0.5f);
        hum2.setUnselectedAlpha(0.5f);
        
        ensureButton = (Button) rootView.findViewById(R.id.house_keeper_task_scene_ensure);
        ensureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				updateDeviceChooseValue(temp1.getSelectedItemPosition(),temp2.getSelectedItemPosition(),hum1.getSelectedItemPosition(),hum2.getSelectedItemPosition());
				Activity activity = (Activity) mContext;
				activity.finish();
				HouseKeeperSelectSensorDeviceDataFragment.fireSelectDeviceDataListener(ep,epType,values,describe);
			}
		});
        
	}

	private void initTempHumView(String value,String describe,SingleChooseManager manager) {
		manager.setChecked(greaterTempImageView.getId());
		ep = WulianDevice.EP_14;
		epType = "1702";
		temp1.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
		temp2.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
		hum1.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
		hum2.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
		values = SPLIT_MORE + tempValus[StringUtil.toInteger(DERAULT_VALUES)];
		this.describe = "\u2103";
//		if(!StringUtil.isNullOrEmpty(value) && !StringUtil.isNullOrEmpty(describe)){
////			String[] deviceDatas = autoConditionInfo.getObject().split(SPLIT_MORE);
////			WulianDevice device = mDeviceCache.getDeviceByID(mContext, AccountManager.getAccountManger().getmCurrentInfo()
////					.getGwID(), deviceDatas[0]);
////			Sensorable sensor = (Sensorable) device;
////			String unit = sensor.unit(device.getDeviceInfo().getDevEPInfo().getEp(), device.getDeviceInfo().getDevEPInfo().getEpType());
//			String symbol = value.substring(0,1);
//			String valueData = value.substring(1);
//			if(StringUtil.equals(symbol, ">")){
//				if(StringUtil.equals(describe, WL_17_Temhum.UNIT_C)){
//					manager.setChecked(greaterTempImageView.getId());
//					temp1.setSelection(StringUtil.toInteger(valueData), true);
//					ep = WulianDevice.EP_14;
//					epType = "1702";
//				}else if(StringUtil.equals(describe, WL_17_Temhum.UNIT_RH)){
//					manager.setChecked(greaterHumImageView.getId());
//					hum1.setSelection(StringUtil.toInteger(valueData), true);
//					ep = WulianDevice.EP_14;
//					epType = "1703";
//				}
//			}else if(StringUtil.equals(symbol, "<")){
//				if(StringUtil.equals(describe, WL_17_Temhum.UNIT_C)){
//					manager.setChecked(lessTempImageView.getId());
//					temp2.setSelection(StringUtil.toInteger(valueData), true);
//					ep = WulianDevice.EP_14;
//					epType = "1702";
//				}else if(StringUtil.equals(describe, WL_17_Temhum.UNIT_RH)){
//					manager.setChecked(lessHumImageView.getId());
//					hum2.setSelection(StringUtil.toInteger(valueData), true);
//					ep = WulianDevice.EP_14;
//					epType = "1703";
//				}
//			}
//		}else{
//			manager.setChecked(greaterTempImageView.getId());
//			ep = WulianDevice.EP_14;
//			epType = "1702";
//			temp1.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
//			temp2.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
//			hum1.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
//			hum2.setSelection(StringUtil.toInteger(DERAULT_VALUES),true);
//			values = SPLIT_MORE + tempValus[StringUtil.toInteger(DERAULT_VALUES)];
//		}
	}

	private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(TosAdapterView<?> parent, View view,
				int position, long id) {
			((WheelTextView)view).setTextSize(20);
			  int index = StringUtil.toInteger(view.getTag().toString());
			  int count = parent.getChildCount();
			  if(index < count-1){
                ((WheelTextView)parent.getChildAt(index+1)).setTextSize(14);
			  }
			  if(index>0){
                ((WheelTextView)parent.getChildAt(index-1)).setTextSize(14);
			  }
			  if(temp1.getId() == parent.getId()){
				  manager.setChecked(greaterTempImageView.getId());
			  }else if(temp2.getId() == parent.getId()){
				  manager.setChecked(lessTempImageView.getId());
			  }else if(hum1.getId() == parent.getId()){
				  manager.setChecked(greaterHumImageView.getId());
			  }else if(hum2.getId() == parent.getId()){
				  manager.setChecked(lessHumImageView.getId());
			  }
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {
		}
		
	};
	private void updateDeviceChooseValue(int temp1,int temp2, int hum1, int hum2) {
		temp1Value = tempValus[temp1];
		temp2Value = tempValus[temp2];
		hum1Value = humValus[hum1];
		hum2Value = humValus[hum2];
		if(manager.getCheckID() == lessTempImageView.getId()){
			values = "<" + temp2Value;
			describe = "\u2103";
			ep = WulianDevice.EP_14;
			epType = "1702";
		}else if(manager.getCheckID() == greaterTempImageView.getId()){
			values = ">" + temp1Value;
			describe = "\u2103";
			ep = WulianDevice.EP_14;
			epType = "1702";
		}else if(manager.getCheckID() == lessHumImageView.getId()){
			values = "<" + hum2Value;
			describe = "%";
			ep = WulianDevice.EP_15;
			epType = "1703";
		}else if(manager.getCheckID() == greaterHumImageView.getId()){
			values = ">" + hum1Value;
			describe = "%";
			ep = WulianDevice.EP_15;
			epType = "1703";
		}
	}
	
	public void setmSensorDeviceValues(String value,String describe){
		this.values = value;
		this.describe = describe;
		initTempHumView(value,describe,manager);
	}

//	public AutoConditionInfo getAutoConditionInfo() {
//		if(autoConditionInfo == null){
//			autoConditionInfo = new AutoConditionInfo();
//		}
//		return autoConditionInfo;
//	}
	
	private class NumberAdapter extends BaseAdapter{

		int mHeight = 30;
        String[] mData = null;
        public NumberAdapter(String[] data) {
            mHeight = (int) DisplayUtil.dip2Pix(mContext, mHeight);
            this.mData = data;
        }
        
		@Override
		public int getCount() {
			 return (null != mData) ? mData.length : 0;
		}

		@Override
		public String getItem(int arg0) {
			return mData[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 WheelTextView textView = null;

	            if (null == convertView) {
	                convertView = new WheelTextView(mContext);
	                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
	                textView = (WheelTextView) convertView;
	                textView.setTextSize(14);
	                textView.setGravity(Gravity.CENTER);
	                textView.setTextColor(mContext.getResources().getColor(R.color.black));
	            }
	            
	            
	            String text = mData[position];
	            if (null == textView) {
	                textView = (WheelTextView) convertView;
	            }
	            
	            textView.setText(text);
	            return convertView;
		}
		
	}
	public View getView(){
		return rootView;
	}



	
}
