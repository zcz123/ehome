package cc.wulian.app.model.device.impls.controlable.led;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class SettingColorTimeView extends LinearLayout{

	private String[] mMsArray = {  "000","100","200", "300", "400", "500", "600", "700", "800", "900","000","100","200","300","400","500","600","700","800","900"};
	private String[] mSecArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
	            "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
	            "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
	            "51", "52", "53", "54", "55", "56", "57", "58", "59"};
	
	private Context mContext;
	private WheelView mMs = null;
	private WheelView mSeconds = null;
	private NumberAdapter MsAdapter;
	private NumberAdapter SeconsAdapter;
	private int ms, s;
	private int se, mss;
	private int time;
	 
	public int getTime() {
		return time;
	}


	public void setTime(int time) {
		this.time = time;
        if(!StringUtil.isNullOrEmpty(String.valueOf(time))){
        	s = this.time / 1000;
            ms = this.time - (s * 1000);
            
            mSeconds.setSelection(s,true);
            mMs.setSelection(ms / 100,true);
            ((WheelTextView)mMs.getSelectedView()).setTextSize(30);
            ((WheelTextView)mSeconds.getSelectedView()).setTextSize(30);
        }
	}
	public SettingColorTimeView(Context context) {
		super(context);
		this.mContext = context;
		inflate(mContext, R.layout.device_led_bright_setting_layout, this);
		mMs = (WheelView) findViewById(R.id.wheel2);
		mSeconds = (WheelView) findViewById(R.id.wheel1);
		
		mMs.setScrollCycle(true);
		mSeconds.setScrollCycle(true);
		MsAdapter = new NumberAdapter(mMsArray);
		SeconsAdapter = new NumberAdapter(mSecArray);
        mMs.setAdapter(MsAdapter);
        mSeconds.setAdapter(SeconsAdapter);
        mMs.setSelection(0, true);
        mSeconds.setSelection(0, true);
        ((WheelTextView)mMs.getSelectedView()).setTextSize(30);
        ((WheelTextView)mSeconds.getSelectedView()).setTextSize(30);
        mMs.setOnItemSelectedListener(mListener);
        mSeconds.setOnItemSelectedListener(mListener);
        mMs.setUnselectedAlpha(0.5f);
        mSeconds.setUnselectedAlpha(0.5f);
	}

	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}


	private class NumberAdapter extends BaseAdapter{

		int mHeight = 50;
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
	                textView.setTextSize(20);
	                textView.setGravity(Gravity.CENTER);
	                textView.setTextColor(mContext.getResources().getColor(R.color.black));
	            }
	            
	            
	            String text = mData[position];
	            if (null == textView) {
	                textView = (WheelTextView) convertView;
	            }
	            textView.setTextSize(20);
	            textView.setText(text);
	            return convertView;
		}
		
	}
	
	private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(TosAdapterView<?> parent, View view,
				int position, long id) {
			((WheelTextView)view).setTextSize(30);
			  int index = StringUtil.toInteger(view.getTag().toString());
			  int count = parent.getChildCount();
			  if(index < count-1){
              ((WheelTextView)parent.getChildAt(index+1)).setTextSize(20);
			  }
			  if(index>0){
              ((WheelTextView)parent.getChildAt(index-1)).setTextSize(20);
			  }
			  se = mSeconds.getSelectedItemPosition();
			  mss = StringUtil.toInteger(mMsArray[mMs.getSelectedItemPosition()]); 
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {
			
		}
		
	};

	
	private String add0( int time ) {
		String str_m = String.valueOf(time);
		String str = "00000";
		str_m = str.substring(0, 5 - str_m.length()) + str_m;
		return str_m;
	}
	public String getSettingTime(){
		return add0(se * 1000 + mss);
	}
}
