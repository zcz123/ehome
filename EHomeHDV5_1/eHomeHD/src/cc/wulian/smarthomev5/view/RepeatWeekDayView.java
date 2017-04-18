package cc.wulian.smarthomev5.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class RepeatWeekDayView extends FrameLayout implements OnCheckedChangeListener
{
	public static interface OnRepeatWeekChangedListener
	{
		public void onWeekDayChanged( RepeatWeekDayView weekView, String weekDay );
	}

	private static final String REGULAR_EXPRESSION = "([0|1]([,]{0,1})){7}";
	private static final String SPLIT_REGULAR = ",";
	private static final String REPEAT_ON = "1";
	private static final String REPEAT_OFF = "0";
	private static final String REPEAT_DEFAULT = "0,0,0,0,0,0,0";

	private final List<CompoundButton> mButtons = new ArrayList<CompoundButton>();
	private final SparseArrayCompat<Boolean> mCheckedArray = new SparseArrayCompat<Boolean>();

	private static int[] mWeekValues = new int[]{
											R.string.scene_sun,
											R.string.scene_mon,
											R.string.scene_tue,
											R.string.scene_wed,
											R.string.scene_thu,
											R.string.scene_fri,
											R.string.scene_sat
	};

	private OnRepeatWeekChangedListener mChangedListener;

	public RepeatWeekDayView( Context context )
	{
		this(context, null);
	}

	public RepeatWeekDayView( Context context, AttributeSet attrs )
	{
		this(context, attrs, 0);
	}

	public RepeatWeekDayView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);

		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.common_content_weekday, this, true);
		
		if(isInEditMode()) return;
		
		TypedArray tArray = getResources().obtainTypedArray(R.array.repeatWeekDayIds);
		int length = tArray.length();
		for (int i = 0; i < length; i++){
			int id = tArray.getResourceId(i, 0);
			CompoundButton button = (CompoundButton) findViewById(id);
			button.setOnCheckedChangeListener(this);
			mCheckedArray.put(id, false);
			mButtons.add(button);
		}
		tArray.recycle();
	}

	public void setOnRepeatWeekChangedListener( OnRepeatWeekChangedListener listener ){
		mChangedListener = listener;
	}

	@Override
	public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ){
		int id = buttonView.getId();
		mCheckedArray.put(id, isChecked);
		onWeekdayStateChanged();
	}

	private void onWeekdayStateChanged(){
		if (mChangedListener != null){
			mChangedListener.onWeekDayChanged(this, getRepeatWeekDay());
		}
	}

	/**
	 * format: 0,0,0,0,0,0,0
	 * 
	 * @param source
	 */
	public void setRepeatWeekDay( String weekday ){
		if (!StringUtil.isNullOrEmpty(weekday) && weekday.matches(REGULAR_EXPRESSION)){
			String[] splits = weekday.split(SPLIT_REGULAR);
			for (int i = 0; i < splits.length; i++){
				mButtons.get(i).setChecked(REPEAT_ON.equals(splits[i]));
			}
		}
	}
	
	public void setRepeatWeekDayDefault(){
		setRepeatWeekDay(REPEAT_DEFAULT);
	}

	/**
	 * format: 0,0,0,0,0,0,0,0
	 * 
	 * @return
	 */
	public String getRepeatWeekDay(){
		StringBuilder sb = new StringBuilder();
		int size = mButtons.size();
		for (int i = 0; i < size; i++){
			CompoundButton button = mButtons.get(i);
			sb.append(mCheckedArray.get(button.getId()) ? REPEAT_ON : REPEAT_OFF);

			if (i != size - 1) sb.append(SPLIT_REGULAR);
		}
		return sb.toString();
	}

	public static String repeatWeekDay2String(Context context, String weekday ){
		Resources resources = context.getResources();
		StringBuilder sb = new StringBuilder();
		int count = 0;
		if (weekday.matches(REGULAR_EXPRESSION)){
			String[] splits = weekday.split(SPLIT_REGULAR);
			int length = splits.length;
			for (int i = 0; i < length; i++){
				if (REPEAT_ON.equals(splits[i])){
					sb.append(resources.getString(mWeekValues[i]));
					sb.append(" ");
					count++;
				}
			}
		}
		if (count == 0) return resources.getString(R.string.scene_time_picker_result_no_day_hint);
		if (count == 7) return resources.getString(R.string.scene_time_picker_result_everyday_hint);
		return sb.toString();
	}
}