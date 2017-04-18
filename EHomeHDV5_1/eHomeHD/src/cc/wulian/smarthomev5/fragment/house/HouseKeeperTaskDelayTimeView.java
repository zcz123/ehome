package cc.wulian.smarthomev5.fragment.house;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.cooker.WL_E2_Electric_cooker;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class HouseKeeperTaskDelayTimeView extends LinearLayout{

	private String[] minsArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
            "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
            "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59" , "60",
            "61", "62", "63", "64", "65", "66", "67", "68", "69" , "70",
            "71", "72", "73", "74", "75", "76", "77", "78", "79" , "80",
            "81", "82", "83", "84", "85", "86", "87", "88", "89" , "90",
            "91", "92", "93", "94", "95", "96", "97", "98", "99"};

	private String[] secondArray={ "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
			"24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
			"38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
			"51", "52", "53", "54", "55", "56", "57", "58", "59" };
	
	private Context mContext;
	private WheelView mSeconds = null;
    private WheelView mMins = null;
    private TextView mSecondsText;
    private TextView mMinuesText;
    private NumberAdapter secondsAdapter;
    private NumberAdapter minAdapter;
//    private int h, m;
    private int seconds, minues;
    private String delay;
    
	public HouseKeeperTaskDelayTimeView(Context context,String delay) {
		super(context);
		this.mContext = context;
		this.delay = delay;
		inflate(mContext, R.layout.task_manager_delay_time_view, this);
		mMins = (WheelView) findViewById(R.id.wheel1);
		mSeconds = (WheelView) findViewById(R.id.wheel2);
		mSecondsText = (TextView) findViewById(R.id.textView_minues);
		mMinuesText = (TextView) findViewById(R.id.texView_hour);
		mSecondsText.setText(mContext.getString(R.string.device_adjust_second_common));
		mMinuesText.setText(mContext.getString(R.string.device_adjust_minutes_common));
		
		mMins.setScrollCycle(true);
		mSeconds.setScrollCycle(true);
		secondsAdapter = new NumberAdapter(secondArray);
		minAdapter = new NumberAdapter(minsArray);
		mSeconds.setAdapter(secondsAdapter);
        mMins.setAdapter(minAdapter);
        if(!StringUtil.isNullOrEmpty(delay)){
        	mSeconds.setSelection(getSelectSecond(delay), true);
        	mMins.setSelection(getSelectMinute(delay), true);
        }else{
        	mSeconds.setSelection(0, true);
        	mMins.setSelection(0, true);
        }
        ((WheelTextView)mSeconds.getSelectedView()).setTextSize(30);
        ((WheelTextView)mMins.getSelectedView()).setTextSize(30);
        mSeconds.setOnItemSelectedListener(mListener);
        mMins.setOnItemSelectedListener(mListener);
        mSeconds.setUnselectedAlpha(0.5f);
        mMins.setUnselectedAlpha(0.5f);
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
			  seconds = mSeconds.getSelectedItemPosition();
			  minues = mMins.getSelectedItemPosition();
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {
			
		}
		
	};
	
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
	
//	private String add0( int time ) {
//		String str_m = String.valueOf(time);
//		String str = "00000";
//		str_m = str.substring(0, 5 - str_m.length()) + str_m;
//		return str_m;
//	}
	public int getSettingSecondsTime(){
		return seconds;
	}
	public int getSettingMinuesTime(){
		return minues;
	}
	
	private int getSelectMinute(String delayTime){
		return Integer.parseInt(delayTime)/60;
	}
	
	private int getSelectSecond(String delayTime){
		return Integer.parseInt(delayTime)%60;
	}
}
