package cc.wulian.smarthomev5.fragment.scene;

import java.util.Calendar;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.RepeatWeekDayView;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class TimingSceneView extends LinearLayout
{
	 private String[] hoursArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
	 private String[] minsArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
	            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
	            "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
	            "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
	            "51", "52", "53", "54", "55", "56", "57", "58", "59"};
	

	private TimingSceneEntity mTimingScene ;
	private final RepeatWeekDayView mRepeatWeekDayView;
    private WheelView mHours = null;
    private WheelView mMins = null;
    private int mSecond = 30;

    private NumberAdapter hourAdapter;
    private NumberAdapter minAdapter;
    private Context mContext;
	public TimingSceneView( Context context )
	{
		super(context, null);
		this.mContext = context;
		inflate(mContext, R.layout.common_timer_scene_view, this);
		mHours = (WheelView) findViewById(R.id.wheel1);
        mMins = (WheelView) findViewById(R.id.wheel2);
		mRepeatWeekDayView = (RepeatWeekDayView) findViewById(R.id.task_details_timer_weekday);
		mHours.setScrollCycle(true);
        mMins.setScrollCycle(true);
        hourAdapter = new NumberAdapter(hoursArray);
        minAdapter = new NumberAdapter(minsArray);
        mHours.setAdapter(hourAdapter);
        mMins.setAdapter(minAdapter);
        mHours.setSelection(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), true);
        mMins.setSelection(Calendar.getInstance().get(Calendar.MINUTE), true);
        ((WheelTextView)mHours.getSelectedView()).setTextSize(30);
        ((WheelTextView)mMins.getSelectedView()).setTextSize(30);
        mHours.setOnItemSelectedListener(mListener);
        mMins.setOnItemSelectedListener(mListener);
        mHours.setUnselectedAlpha(0.5f);
        mMins.setUnselectedAlpha(0.5f);
//        Calendar c = Calendar.getInstance();
//        mTimingScene.time = (StringUtil.appendLeft(c.get(Calendar.HOUR_OF_DAY)+"", 2, '0') + ":" + StringUtil.appendLeft(c.get(Calendar.MINUTE)+"", 2, '0') + ":" + StringUtil.appendLeft(mSecond+"", 2, '0'));
//        updateTime(c.get(Calendar.HOUR_OF_DAY)  ,c.get(Calendar.MINUTE),mSecond);
		mRepeatWeekDayView.setOnRepeatWeekChangedListener(mOnRepeatWeekChangedListener);

	}
	private final RepeatWeekDayView.OnRepeatWeekChangedListener mOnRepeatWeekChangedListener = new RepeatWeekDayView.OnRepeatWeekChangedListener()
	{
		@Override
		public void onWeekDayChanged( RepeatWeekDayView weekView, String weekDay ) {
			String oldWeek = getTimingScene().weekDay;
			if (!TextUtils.equals(oldWeek, weekDay)) {
				updateWeek(weekDay);
			}
		}
	};
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
			  
			updateTime(mHours.getSelectedItemPosition(),mMins.getSelectedItemPosition(),mSecond);
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {
//			Calendar c = Calendar.getInstance();
//			updateTime(c.get(Calendar.HOUR_OF_DAY)  ,c.get(Calendar.MINUTE),mSecond);
		}
		
	};
	private void updateTime(int hour,int minute, int mSecond) {
		this.mTimingScene.time =getTimeFromHourAndMinute(hour, minute, mSecond);
	}
	
	private String getTimeFromHourAndMinute(int hour,int minute, int mSecond){
		return StringUtil.appendLeft(hour+"", 2, '0') + ":" + StringUtil.appendLeft(minute+"", 2, '0') + ":" + StringUtil.appendLeft(mSecond+"", 2, '0') ;
	}
	
	public TimingSceneEntity getTimingScene() {
		if(StringUtil.isNullOrEmpty(mTimingScene.time)){
			mTimingScene.time = getTimeFromHourAndMinute(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), mSecond);
		}
		return mTimingScene;
	}
	
	public void setmTimingScene(TimingSceneEntity timingScene ){
		mTimingScene = timingScene;
		initTime(timingScene.time);
		initWeek(timingScene.weekDay);
	}

	private void updateWeek(String newValue){
		this.mTimingScene.weekDay = newValue;
	}
	private void setHourAndMinuteFromTime(String time){
		if(StringUtil.isNullOrEmpty(time)){
			return ;
		}
		String times[] = time.split(":");
		if(times.length <2)
			return ;
		int hour = StringUtil.toInteger(times[0]);
		int minute = StringUtil.toInteger(times[1]);
		mHours.setSelection(hour, true);
		mMins.setSelection(minute,true);
	}
	
	private void initWeek(String newValue) {
		if (!StringUtil.isNullOrEmpty((String) newValue)) {
			mRepeatWeekDayView.setRepeatWeekDay((String) newValue);
		}
		else {
			mRepeatWeekDayView.setRepeatWeekDayDefault();
		}
	}

	private void initTime(String newValue) {
		if (!StringUtil.isNullOrEmpty(newValue)) {
			setHourAndMinuteFromTime(newValue);
		}
		else {
			mHours.setSelection(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			mMins.setSelection(Calendar.getInstance().get(Calendar.MINUTE));
		}
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
	            
	            textView.setText(text);
	            return convertView;
		}
		
	}
}