package cc.wulian.smarthomev5.fragment.setting.flower.items;

import java.util.Calendar;
import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.AddOrEditFlowerTimeShowActivity;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.fragment.setting.flower.AddOrEditFlowerShowTimeFragment;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class TimePickerPop extends PopupWindow implements View.OnClickListener
{	
  private View contentView,parent;
  private Activity context; 
  private WheelView mHours = null;
  private WheelView mMins = null;
  private FrameLayout hoursLayout,minsLayout;
  private LinearLayout tabBar; 
  private ImageView hoursLine,minsLine;
  private int mSecond = 30;
  private TextView title,submitView,hoursTip,minsTip;
  private NumberAdapter hourAdapter,minAdapter;
  private TimingFlowerEntity mTimingScene ;
  private int width,height;
  private onItemClickListener itemClickListener;
  
  private String[] hoursArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
          "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
  private String[] minsArray = { "00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
          "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
          "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
          "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
          "51", "52", "53", "54", "55", "56", "57", "58", "59"};
  
  public TimePickerPop(Activity context,int width,int height)
  {
    this.context = context;
    this.width=width;
    this.height=height;
    init();
  }

  public void resetStyle(String[] hours,String[] mins,boolean isPicker){  //不设置参数就用默认的
	  if(hours!=null && hours.length>0){
		  this.hoursArray=hours;
		  hourAdapter.setDatas(hoursArray);
		  mHours.setSelection(0);
		  ((WheelTextView)mHours.getSelectedView()).setTextSize(23);	      
	  }else{
		  hideHours();
	  }
	  if(mins!=null && mins.length>0){
		  this.minsArray=mins;
		  minAdapter.setDatas(minsArray);
		  mMins.setSelection(0);
		  ((WheelTextView)mMins.getSelectedView()).setTextSize(23);
	  }else{
		  hideMins();
	  }
	  if(!isPicker)hideTitleAndTabbar();
	  update();
  }
  
  private void hideTitleAndTabbar(){
	  title.setVisibility(View.GONE);
	  tabBar.setVisibility(View.GONE);
	  hoursTip.setVisibility(View.GONE);
	  minsTip.setVisibility(View.GONE);
  }
  
  private void hideHours(){
	  hoursLayout.setVisibility(View.GONE);
	  hoursLine.setVisibility(View.GONE);     
  }
  
  private void hideMins(){
	  minsLayout.setVisibility(View.GONE);
      minsLine.setVisibility(View.GONE);
  }
  
  private void init()
  {
    this.contentView = LayoutInflater.from(this.context).inflate(R.layout.time_picker_pop, null);
    setContentView(this.contentView);
    initWdiget(this.contentView);
    setWidth(this.width);
    setHeight(this.height);
    setFocusable(true);
    setTouchable(true);
    setBackgroundDrawable(new ColorDrawable(0));
    update();
  }

  private void initWdiget(View view)
  {
	  view.findViewById(R.id.cancle).setOnClickListener(this);
	  submitView=(TextView) view.findViewById(R.id.submit);
      mHours = (WheelView)view.findViewById(R.id.wheel1);
      mMins = (WheelView)view.findViewById(R.id.wheel2);
      hoursLayout=(FrameLayout)view.findViewById(R.id.hours_layout);
      minsLayout=(FrameLayout)view.findViewById(R.id.mins_layout);
      title=(TextView)view.findViewById(R.id.title);
      hoursTip=(TextView)view.findViewById(R.id.hours_tip);
      minsTip=(TextView)view.findViewById(R.id.mins_tip);
      tabBar=(LinearLayout)view.findViewById(R.id.tab_bar);
      hoursLine=(ImageView)view.findViewById(R.id.hours_line);
      minsLine=(ImageView)view.findViewById(R.id.mins_line);
      mHours.setScrollCycle(true);
      mMins.setScrollCycle(true);
      hourAdapter = new NumberAdapter(hoursArray);
      minAdapter = new NumberAdapter(minsArray);
      mHours.setAdapter(hourAdapter);
      mMins.setAdapter(minAdapter);
      mHours.setSelection(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), true);
      mMins.setSelection(Calendar.getInstance().get(Calendar.MINUTE), true);
      ((WheelTextView)mHours.getSelectedView()).setTextSize(23);
      ((WheelTextView)mMins.getSelectedView()).setTextSize(23);
      mHours.setOnItemSelectedListener(mListener);
      mHours.setOnItemClickListener(mClickListener);
     
      mMins.setOnItemSelectedListener(mListener);
      mMins.setOnItemClickListener(mClickListener);
      mHours.setUnselectedAlpha(0.5f);
      mMins.setUnselectedAlpha(0.5f);
      updateTime(mHours.getSelectedItemPosition(),mMins.getSelectedItemPosition(),mSecond);
  }

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    case R.id.cancle:
      dismiss();
      break;    
    }   
  }
  
	public TimingFlowerEntity getTimingScene() {
		if(StringUtil.isNullOrEmpty(mTimingScene.getTime())){
			mTimingScene.setTime(getTimeFromHourAndMinute(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), mSecond));
		}
		return mTimingScene;
	}
  
	public void setmTimingScene(TimingFlowerEntity timingScene ){
		if(timingScene==null){
			mTimingScene=new TimingFlowerEntity();
		}else{
			mTimingScene = timingScene;
		}		
		initTime(timingScene.getTime());
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
	
  private class NumberAdapter extends BaseAdapter{

	  int mHeight = 50;
      String[] mData = null;
      public NumberAdapter(String[] data) {
          mHeight = (int) DisplayUtil.dip2Pix(context, mHeight);
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
	                convertView = new WheelTextView(context);
	                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
	                textView = (WheelTextView) convertView;
	                textView.setTextSize(20);
	                textView.setGravity(Gravity.CENTER);
	                textView.setTextColor(context.getResources().getColor(R.color.black));
	            }	            
	            
	            String text = mData[position];
	            if (null == textView) {
	                textView = (WheelTextView) convertView;
	            }
	            
	            textView.setText(text);
	            return convertView;
		}
		
	}
  
  TosAdapterView.OnItemClickListener mClickListener=new TosAdapterView.OnItemClickListener(){

	@Override
	public void onItemClick(TosAdapterView<?> parent, View view, int position,long id) {
		if(itemClickListener!=null)itemClickListener.doWhatOnItemClick(position);
	}	  
  };
  
  private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(TosAdapterView<?> parent, View view,
				int position, long id) {
			((WheelTextView)view).setTextSize(23);
			  int index = StringUtil.toInteger(view.getTag().toString());
			  int count = parent.getChildCount();
			  if(index < count-1){
              ((WheelTextView)parent.getChildAt(index+1)).setTextSize(20);
			  }
			  if(index>0){
              ((WheelTextView)parent.getChildAt(index-1)).setTextSize(20);
			  }		
			  if(itemClickListener!=null)itemClickListener.doWhatOnItemClick(position);
			updateTime(mHours.getSelectedItemPosition(),mMins.getSelectedItemPosition(),mSecond);
		}

		@Override
		public void onNothingSelected(TosAdapterView<?> parent) {
		}
		
	};
	
	public View getParentView()
	{
		return parent;
	}
	
	public void showAtLocation(View parent, int gravity, int x, int y) {	
		this.parent=parent;
		super.showAtLocation(parent, gravity, x, y);		
	};	
	
	private void updateTime(int hour,int minute, int mSecond) {
		if(mTimingScene==null)mTimingScene=new TimingFlowerEntity();
		this.mTimingScene.setTime(getTimeFromHourAndMinute(hour, minute, mSecond));
	}
	
	private String getTimeFromHourAndMinute(int hour,int minute, int mSecond){
		return StringUtil.appendLeft(hour+"", 2, '0') + ":" + StringUtil.appendLeft(minute+"", 2, '0');
	}
  
	
	public void setOnItemClickListener(onItemClickListener listener){
		this.itemClickListener=listener;
	}
	
	public void setOnSubmitListener(final onSubmitListener listener){
		submitView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				listener.doWhatOnSubmit(arg0,mHours.getSelectedItemPosition(),mMins.getSelectedItemPosition());				
			}
		});
	}
	
	public interface onSubmitListener{
		public void doWhatOnSubmit(View view,int hourSelectedposition,int minSelectedposition);
	}
	
	public interface onItemClickListener{
		public void doWhatOnItemClick(int position);
	}
	
	public void setTitle(String titleTxt){
		title.setText(titleTxt);
	}
}