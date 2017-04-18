package cc.wulian.smarthomev5.fragment.setting.flower;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneView;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.fragment.setting.flower.items.HorizontalWeekDayView;
import cc.wulian.smarthomev5.fragment.setting.flower.items.TimePickerPop;
import cc.wulian.smarthomev5.fragment.setting.flower.items.TimePickerPop.onSubmitListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DateUtil;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AddOrEditFlowerShowTimeFragment extends WulianFragment implements View.OnClickListener, onSubmitListener
{
  public static final String FLOWER_TIME_SHOW_SERIAL = "flower_time_show_serial";
  private static final String SHOW_DIALOG_KEY = "Add_Edit_FlowerTime_show_dialog_key";
  private TimePickerPop mPopupWindow;
  private int position;
  private TextView startTxtView,endTxtView ,timeEndHintView;
  private FlowerManager timingSceneGroup = FlowerManager.getInstance();
  private String startTxt,endTxt, difference="0000";
  private HorizontalWeekDayView weekdayView;
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Bundle bundle = getArguments();
    if (bundle != null)this.position = bundle.getInt(FLOWER_TIME_SHOW_SERIAL);
    DisplayMetrics metrics=new DisplayMetrics();
    ((WindowManager)mActivity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
    this.mPopupWindow = new TimePickerPop(getActivity(),(int) (0.8 * metrics.widthPixels),(int) (0.6 * metrics.heightPixels));
    mPopupWindow.setOnSubmitListener(this);
    initBar();
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle)
  {
    View localView = inflater.inflate(R.layout.add_or_edit_flower_time_show, viewGroup, false);
    return localView;
  }

  public void onViewCreated(View view, @Nullable Bundle bundle)
  {
    super.onViewCreated(view, bundle);
    startTxtView = ((TextView)view.findViewById(R.id.time_start_txt));
    endTxtView = ((TextView)view.findViewById(R.id.time_end_txt));
    timeEndHintView= ((TextView)view.findViewById(R.id.time_end_hint_View));
    weekdayView=(HorizontalWeekDayView)view.findViewById(R.id.show_time_weekday);
    View startLayout = view.findViewById(R.id.time_start_layout);
    View endLayout = view.findViewById(R.id.time_end_layout);
    if (this.position >= 0){
    	String time=(timingSceneGroup.getFlowerTimingEntities(FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME).get(this.position)).getTime();
    	String weekday=(timingSceneGroup.getFlowerTimingEntities(FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME).get(this.position)).getWeekDay();
    	if(time!=null&&time.length()>=8){
    		startTxt=time.substring(0,4);    		
    		difference=time.substring(4,time.length());      		
    	}    	
  	    weekdayView.setWeekday(weekday);
    }else{
    	startTxt=DateUtil.getHourAndMinu(mActivity, System.currentTimeMillis()).replace(":", "");
    	difference="0000";
    }    
    startTxtView.setText(startTxt.substring(0,2)+":"+startTxt.substring(2,4));    
	endTxtView.setText(getTimeByDifference());
    startLayout.setOnClickListener(this);
    endLayout.setOnClickListener(this);
  }
  
  private void initBar()
  {
    this.mActivity.resetActionMenu();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayIconEnabled(false);
    getSupportActionBar().setDisplayIconTextEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setDisplayShowMenuEnabled(false);
    getSupportActionBar().setDisplayShowMenuTextEnabled(true);
    getSupportActionBar().setIconText(mApplication.getResources().getString(R.string.cancel));
    getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.gateway_dream_flower_time_show));
    getSupportActionBar().setRightIconText(mApplication.getResources().getString(R.string.set_save));
    getSupportActionBar().setRightMenuClickListener(new ActionBarCompat.OnRightMenuClickListener()
    {
      public void onClick(View paramView)
      {
        List<TimingFlowerEntity> newList = timingSceneGroup.getNewTimingEntities(FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME);
        if (position >= 0)
        {
          TimingFlowerEntity entity = newList.get(position);
          entity.setTime(startTxt+difference);
          entity.setWeekDay(DateUtil.BinaryToHex(DateUtil.changeWeekOrder(weekdayView.getWeekday())));
          newList.set(position, entity);
        }else{         
          TimingFlowerEntity entity = new TimingFlowerEntity();
          entity.setTime(startTxt+difference);
          entity.setWeekDay(DateUtil.BinaryToHex(DateUtil.changeWeekOrder(weekdayView.getWeekday())));  //转换成服务器星期 在转成16进制
          newList.add(entity);          
        }
        JsonTool.SetFlowerShowTiming(CmdUtil.FLOWER_SET_SHOW_TIME, newList);
        mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
      }
    });
  }

  public void onClick(View paramView)
  {
    if (this.mPopupWindow.isShowing()) this.mPopupWindow.dismiss();
    switch (paramView.getId())
    {
    case R.id.time_start_layout:
      this.mPopupWindow.showAtLocation(startTxtView, Gravity.CENTER, 0, 0);
      break;
    case R.id.time_end_layout:
    	this.mPopupWindow.showAtLocation(endTxtView, Gravity.CENTER, 0, 0);
    	break;
    }  
  }
  
  public void onEventMainThread(FlowerEvent event)
  {
    this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
    if(FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME.equals(event.getAction())){
    	this.mActivity.finish();
    }
  }

	private void setTimeViewTxt() {
		if(mPopupWindow.getParentView().getId()==startTxtView.getId()){
			startTxt=mPopupWindow.getTimingScene().getTime();
			startTxtView.setText(startTxt);			
			startTxt=startTxt.replace(":", "");
			getTimeDifference();
		}else if(mPopupWindow.getParentView().getId()==endTxtView.getId()){
			endTxt=mPopupWindow.getTimingScene().getTime();
			endTxtView.setText(endTxt);
			endTxt=endTxt.replace(":", "");
			getTimeDifference();
		}
	}
	
    //获取两个时间的分钟差 例如  "11:30";  "17:30"; 差值为360  "17:30";  "11:30"; 差值为1080 差值始终为正  一天 1440分钟
    private String getTimeDifference(){
       
        String hint=mApplication.getResources().getString(R.string.gateway_dream_flower_time_show_end_time_hint);
        long diff=0;
        Calendar start=Calendar.getInstance();
        Calendar end=Calendar.getInstance();

        start.set(Calendar.HOUR_OF_DAY,StringUtil.toInteger(startTxt.substring(0, 2)));
        start.set(Calendar.MINUTE, StringUtil.toInteger(startTxt.substring(2, 4)));

        end.set(Calendar.HOUR_OF_DAY,StringUtil.toInteger(endTxt.substring(0, 2)));
        end.set(Calendar.MINUTE,StringUtil.toInteger(endTxt.substring(2, 4)));
        diff= (end.getTime().getTime() - start.getTime().getTime())/(1000*60);
        if (diff<0){
        	hint=mApplication.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow);
            diff=24*60-(Math.abs(diff)+1);
        }
        timeEndHintView.setText(hint);
        difference=String.valueOf(diff);
        StringBuffer buff=new StringBuffer();
        if(difference.length()<4){
        	for(int i=0;i<4-difference.length();i++){
        		buff.append("0");
        	}
        }
        difference=buff.append(difference).toString();
        return difference;
    }
    
    //根据差值获取另一个时间  例如  "11:30" 差值为1080; -> "17:30"  ;  "17:30"  差值为1080 -> "11:30";
    private String getTimeByDifference(){
        String hint=mApplication.getResources().getString(R.string.gateway_dream_flower_time_show_end_time_hint);
        
        Calendar end=Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY,StringUtil.toInteger(startTxt.substring(0,2)));
        end.set(Calendar.MINUTE,StringUtil.toInteger(startTxt.substring(2,4)));
        end.add(Calendar.MINUTE,StringUtil.toInteger(difference));
       
        String hour=String.valueOf(end.get(Calendar.HOUR_OF_DAY));
        String mins=String.valueOf(end.get(Calendar.MINUTE));
        StringBuffer buff=new StringBuffer();
        if(hour.length()<=1){
        	buff.append("0");
        }
        buff.append(hour).append(":");
        if(mins.length()<=1){
        	buff.append("0");
        }
        buff.append(mins);
        endTxt=buff.toString().replace(":", "");
        if(StringUtil.toInteger(endTxt)<StringUtil.toInteger(startTxt)){
        	hint=mApplication.getResources().getString(R.string.house_rule_add_new_condition_select_time_end_tomorrow);
        }
        timeEndHintView.setText(hint);
        return buff.toString();
    }
    
	@Override
	public void doWhatOnSubmit(View view,int hourSelectedposition,int minSelectedposition) {
		setTimeViewTxt();
		mPopupWindow.dismiss();
	}

}
