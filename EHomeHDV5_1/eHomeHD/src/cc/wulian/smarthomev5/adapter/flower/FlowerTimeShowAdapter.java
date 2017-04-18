package cc.wulian.smarthomev5.adapter.flower;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.DateUtil;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class FlowerTimeShowAdapter extends WLBaseAdapter<TimingFlowerEntity>
{	
	  public FlowerTimeShowAdapter(Context context, List<TimingFlowerEntity> list)
	  {
	    super(context, list);
	  }

	  protected View newView(Context context, LayoutInflater inflater, ViewGroup paramViewGroup, int paramInt)
	  {
	    return inflater.inflate(R.layout.fragment_timingscene_time_list_item_parent, paramViewGroup, false);
	  }
	  
	  protected void bindView(Context context, View view, int position, TimingFlowerEntity item)
	  {
		  final String REPEAT_ON = "1";
		  int[] mWeekValues = new int[] {
				R.string.scene_sun,
				R.string.scene_mon,
				R.string.scene_tue,
				R.string.scene_wed,
				R.string.scene_thu,
				R.string.scene_fri,
				R.string.scene_sat };
			  
			LinearLayout editLayout = (LinearLayout) view.findViewById(R.id.linearLayout_operation);
			LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			editLayout.removeAllViews();
			editLayout.setOrientation(LinearLayout.HORIZONTAL);
			Resources resources = context.getResources();
			
			char[] arrayOfChar = DateUtil.changeWeekOrder(DateUtil.Hexconvert2LocalWeekday(item.getWeekDay())).replace(",","").toCharArray();
			int weekdayNum = 0;
			
			for (int i = 0; i < arrayOfChar.length; i++) {
				if (String.valueOf(arrayOfChar[i]).equals(REPEAT_ON)) {
					final TextView weekDay = new TextView(mContext);
					weekdayNum++;
					weekDay.setText(resources.getString(mWeekValues[i])+" ");
					weekDay.setTextSize(15);
					weekDay.setPadding(10, 1, 10, 1);
					editLayout.addView(weekDay,layoutParams);
				}
			}
			/**
			 * 若判断选中日期数为0是显示：未设置日期，点击设置
			 */
			if(weekdayNum==0){
				final TextView noweekDay = new TextView(mContext);
				noweekDay.setText(resources.getString(R.string.scene_no_weekday_bind));
				noweekDay.setTextSize(15);
				noweekDay.setPadding(10, 1, 10, 1);
				editLayout.addView(noweekDay);
			}
			
			TextView timingSceneTime = (TextView) view.findViewById(R.id.timing_scene_time_textview);
			timingSceneTime.setText(switchTime(item.getTime()));

	  } 
	 
		private String switchTime(String time)
		{
			String startTime=time.substring(0, 2) + ":" + time.substring(2, 4);			
			String endTime=getTimeByDifference(startTime.replace(":", ""),time.substring(4,time.length()));
			if(StringUtil.toInteger(endTime.replace(":",""))<StringUtil.toInteger(startTime.replace(":",""))){
				endTime+=mContext.getResources().getString(R.string.gateway_dream_flower_time_show_next_day);
			}
		    return startTime+"~"+endTime;
		}
		
		 //根据差值获取另一个时间  例如  "11:30" 差值为1080; -> "17:30"  ;  "17:30"  差值为1080 -> "11:30";
	    private String getTimeByDifference(String startTxt,String difference ){
    
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
	             
	        return buff.toString();
	    }

}