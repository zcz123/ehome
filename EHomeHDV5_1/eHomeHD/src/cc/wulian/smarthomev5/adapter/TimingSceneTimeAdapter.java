package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
/**
 * 
* @ClassName: TimingSceneTimeAdapter 
* @Description: TODO(列表中数据显示) 
* @author ylz
* @date 2015-3-25 下午2:22:39 
*
 */
public class TimingSceneTimeAdapter extends WLBaseAdapter<TimingSceneEntity>
{	 
	public TimingSceneTimeAdapter( Context context, List<TimingSceneEntity> data,SceneInfo mCurrentInfo )
	{
		super(context, data);
	}
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.fragment_timingscene_time_list_item_parent, parent, false);
	}
	@Override
	protected void bindView( Context context, View view, int pos, TimingSceneEntity item ) {
		final String SPLIT_REGULAR = ",";
		final String REPEAT_ON = "1";
		final String REPEAT_OFF = "0";
		int[] mWeekValues = new int[] {
			    R.string.Sunday,
				R.string.Monday,
				R.string.Tuesday,
				R.string.Wednesday,
				R.string.Thursday,
				R.string.Friday,
				R.string.Saturday };
		  
		LinearLayout editLayout = (LinearLayout) view.findViewById(R.id.linearLayout_operation);
		editLayout.removeAllViews();
		editLayout.setOrientation(LinearLayout.HORIZONTAL);
		Resources resources = context.getResources();
		
		/**
		 * 动态生成星期数据显示
		 * 通过向linearLayout_operation中依次添加weekDay
		 */
		
		String[] splits = item.getWeekDay().split(SPLIT_REGULAR);
		int weekdayNum = 0;
		
		for (int i = 0; i < splits.length; i++) {
			if (splits[i].equals(REPEAT_ON)) {
				final TextView weekDay = new TextView(mContext);
				weekdayNum++;
				weekDay.setText(resources.getString(mWeekValues[i])+" ");
				weekDay.setTextSize(15);
				weekDay.setPadding(10, 1, 10, 1);
				editLayout.addView(weekDay);
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
		timingSceneTime.setText(item.getTime().substring(0, 5));
	}
}
