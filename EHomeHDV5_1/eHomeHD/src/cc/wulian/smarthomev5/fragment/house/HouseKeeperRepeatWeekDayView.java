package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class HouseKeeperRepeatWeekDayView extends LinearLayout implements OnCheckedChangeListener{

	public static interface OnRepeatWeekChangedListener
	{
		public void onWeekDayChanged( HouseKeeperRepeatWeekDayView weekView, String weekDay );
	}
	
	private static final String REPEAT_ON = "1";
	private static final String REPEAT_OFF = "0";
	private static final String REPEAT_DEFAULT = "00000000";
	
	private final List<CompoundButton> mButtons = new ArrayList<CompoundButton>();
	private final SparseArrayCompat<Boolean> mCheckedArray = new SparseArrayCompat<Boolean>();
	
	private OnRepeatWeekChangedListener mChangedListener;
	private TypedArray tArray;
	private int length;
	
	public HouseKeeperRepeatWeekDayView( Context context )
	{
		this(context, null);
	}

	public HouseKeeperRepeatWeekDayView( Context context, AttributeSet attrs )
	{
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public HouseKeeperRepeatWeekDayView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);

		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.house_keeper_choose_weekday, this, true);
		
		if(isInEditMode()) return;
		
		tArray = getResources().obtainTypedArray(R.array.houseKeeperRepeatWeekDayIds);
		length = tArray.length();
		
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		if(id == mButtons.get(0).getId()){
			for(int i = 0; i < length; i++){
				mCheckedArray.put(mButtons.get(i).getId(), isChecked);
			}
		}else{
			mCheckedArray.put(id, isChecked);
			boolean isCheck = true;
			for(int i = 1; i < length; i++){
				if(!mCheckedArray.get(mButtons.get(i).getId())){
					isCheck = false;
				}
			}
			mCheckedArray.put(mButtons.get(0).getId(), isCheck);
		}
		onWeekdayStateChanged();
	}

	
	private void onWeekdayStateChanged(){
		if(mChangedListener != null){
			mChangedListener.onWeekDayChanged(this, getRepeatWeekDay());
		}
	}
	
	
	public void setRepeatWeekDayDefault(){
		setRepeatWeekDay(REPEAT_DEFAULT);
	}
	
	
	/**
	 * format: 01111111
	 * 
	 * @param source
	 */
	public void setRepeatWeekDay( String weekday ){
		if (!StringUtil.isNullOrEmpty(weekday)){
			for (int i = 0; i < weekday.length(); i++){
				mButtons.get(i).setChecked(REPEAT_ON.equals(weekday.substring(i, i+1)));
			}
		}
	}
	private String getRepeatWeekDay(){
		StringBuilder sb = new StringBuilder();
		int size = mButtons.size();
		for(int i = 0; i <size; i++){
			CompoundButton button = mButtons.get(i);
			sb.append(mCheckedArray.get(button.getId()) ? REPEAT_ON : REPEAT_OFF);
		}
		return sb.toString();
	}
}
