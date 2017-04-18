package cc.wulian.app.model.device.impls.sensorable.sphy;

import java.util.Calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.sensorable.WL_48_Sphy;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.lidroid.xutils.ViewUtils;
import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class EditSphyFragment extends WulianFragment implements OnSeekBarChangeListener{

	private WheelView  mYears=null,mMonths=null,mDays=null,mHours=null, mMins = null,mSeconds=null;
	private int mSecond = 30;
	private SeekBar seekBar;
	
	private TextView verticalTimeView;
	private WL_48_Sphy SphyDevice;
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String EP = "ep";
	public static final String EPTYPE = "eptype";
	
	protected String type,gwID,devID,ep,epType;
	private String[] yearsArray=new String[21];
	
	private String[] monthsArray = {"01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	          "11", "12"};
	
	private String[] daysArray = { "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	          "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
	          "24", "25", "26", "27", "28", "29", "30", "31"};
	
	private String[] hoursArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	          "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
	
	private String[] minsArray = {"00","01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	          "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
	          "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
	          "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
	          "51", "52", "53", "54", "55", "56", "57", "58", "59"};
	
	private String[] secondsArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	          "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
	          "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
	          "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
	          "51", "52", "53", "54", "55", "56", "57", "58", "59"};
	  


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createYearsArray();
		initEditDevice();
		SphyDevice.onAttachView(mActivity);		
		initBar();
	}
	
	private void createYearsArray(){
		int currentYear=Calendar.getInstance().get(Calendar.YEAR);
		for(int i=0;i<yearsArray.length;i++){
			yearsArray[i]=String.valueOf(currentYear-(10-i));
		}
	}

	private void initEditDevice() {
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		ep=  getArguments().getString(EP);
		epType=getArguments().getString(EPTYPE);
		SphyDevice =(WL_48_Sphy) DeviceCache.getInstance(mActivity).getDeviceByID(mActivity, gwID, devID);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.device_sphy_setting,container,false);
		ViewUtils.inject(this, contentView);
		return contentView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		  verticalTimeView=(TextView)view.findViewById(R.id.vertical_time);
		  mYears= (WheelView)view.findViewById(R.id.sphy_set_wheel0);
		  mMonths= (WheelView)view.findViewById(R.id.sphy_set_wheel1);
		  mDays= (WheelView)view.findViewById(R.id.sphy_set_wheel2);
		  mHours = (WheelView)view.findViewById(R.id.sphy_set_wheel3);
	      mMins = (WheelView)view.findViewById(R.id.sphy_set_wheel4);
		  mSeconds= (WheelView)view.findViewById(R.id.sphy_set_wheel5);
	      
	      setWheel(mYears,yearsArray,10);
	      setWheel(mMonths,monthsArray,Calendar.getInstance().get(Calendar.MONTH));
	      setWheel(mDays,daysArray,Calendar.getInstance().get(Calendar.DAY_OF_MONTH)-1);
	      setWheel(mHours,hoursArray,Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
	      setWheel(mMins,minsArray,Calendar.getInstance().get(Calendar.MINUTE));	      
	      setWheel(mSeconds,secondsArray,Calendar.getInstance().get(Calendar.SECOND));
	     
	      seekBar=(SeekBar)view.findViewById(R.id.sphy_interval);
	      seekBar.setOnSeekBarChangeListener(this);
	}
	
	private void setWheel(WheelView view,String[] datas,int selectedPosition){
		view.setScrollCycle(true);
		view.setAdapter(new NumberAdapter(datas));
		view.setSelection(selectedPosition, true);
		((WheelTextView)view.getSelectedView()).setTextSize(17);
		view.setOnItemSelectedListener(mListener);	
		view.setUnselectedAlpha(0.5f);
	}
	
	private void initBar() {
		this.mActivity.resetActionMenu();
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayIconEnabled(true);
	    getSupportActionBar().setDisplayIconTextEnabled(false);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);
	    getSupportActionBar().setDisplayShowMenuEnabled(false);
	    getSupportActionBar().setDisplayShowMenuTextEnabled(true);
	    getSupportActionBar().setTitle(getResources().getString(R.string.device_type_48));
	    getSupportActionBar().setRightIconText(getResources().getString(R.string.set_save));
	    getSupportActionBar().setRightMenuClickListener(new ActionBarCompat.OnRightMenuClickListener()
	    {
	      public void onClick(View paramView)
	      {
//	    	  SphyDevice.createControlOrSetDeviceSendData(SphyDevice.DEVICE_OPERATION_CTRL, getTimeParam(), true);
//	    	  SphyDevice.createControlOrSetDeviceSendData(SphyDevice.DEVICE_OPERATION_CTRL, getIntervalParam(), true);
//	    	  
	    	  SphyDevice.controlDevice(ep, epType,getTimeParam());
	    	  SphyDevice.controlDevice(ep, epType,getIntervalParam());
	    	  mActivity.finish();
	      }
	    });
	}
	
	private String getTimeParam(){
		StringBuffer param=new StringBuffer();
		String year=yearsArray[mYears.getSelectedItemPosition()];
		param.append("1");	//cmdindex
		param.append(year.substring(2, year.length()));
		param.append(monthsArray[mMonths.getSelectedItemPosition()-1]);
		param.append(daysArray[mDays.getSelectedItemPosition()]);
		param.append(hoursArray[mHours.getSelectedItemPosition()]);
		param.append(minsArray[mMins.getSelectedItemPosition()]);
		param.append(secondsArray[mSeconds.getSelectedItemPosition()]);
		System.out.println(param.toString());
		return param.toString();
	}
	private String getIntervalParam(){
		StringBuffer param=new StringBuffer();
		param.append("2"); 	//cmdindex
		if(String.valueOf(seekBar.getProgress()).length()==1){
			param.append("0");
		}
		param.append(String.valueOf(seekBar.getProgress()));
		return param.toString();
	}
	
	private class NumberAdapter extends BaseAdapter{

		  int mHeight = 50;
	      String[] mData = null;
	      public NumberAdapter(String[] data) {
	          mHeight = (int) DisplayUtil.dip2Pix(mActivity, mHeight);
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
			
			public void setDatas(String[] datas){
				mData=datas;
				notifyDataSetChanged();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				 WheelTextView textView = null;

		            if (null == convertView) {
		                convertView = new WheelTextView(mActivity);
		                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
		                textView = (WheelTextView) convertView;
		                textView.setTextSize(15);
		                textView.setGravity(Gravity.CENTER);
		                textView.setTextColor(mActivity.getResources().getColor(R.color.black));
		            }	            
		            
		            String text = mData[position];
		            if (null == textView) {
		                textView = (WheelTextView) convertView;
		            }
		            
		            textView.setText(text);
		            return convertView;
			}
			
		}
	
	private void updateTime(int hour,int minute, int mSecond) {
		
	}
	
   private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(TosAdapterView<?> parent, View view,
				int position, long id) {
			((WheelTextView)view).setTextSize(17);
			  int index = StringUtil.toInteger(view.getTag().toString());
			  int count = parent.getChildCount();
			  if(index < count-1){
              ((WheelTextView)parent.getChildAt(index+1)).setTextSize(15);
			  }
			  if(index>0){
              ((WheelTextView)parent.getChildAt(index-1)).setTextSize(15);
			  }						 
			updateTime(mHours.getSelectedItemPosition(),mMins.getSelectedItemPosition(),mSecond);
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {
		}
		
	};



@Override
public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	verticalTimeView.setText(seekBar.getProgress()+"s");
}

@Override
public void onStartTrackingTouch(SeekBar arg0) {
	
}

@Override
public void onStopTrackingTouch(SeekBar arg0) {
	
}

}
