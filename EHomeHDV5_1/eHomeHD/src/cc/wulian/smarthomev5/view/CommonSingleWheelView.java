package cc.wulian.smarthomev5.view;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosAdapterView.OnItemSelectedListener;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class CommonSingleWheelView extends LinearLayout{

	
	private Context mContext;
	private WheelView valuesWheel = null;
    private NumberAdapter valuesAdapter;
    
	public CommonSingleWheelView(Context context,String[] valuesStr,String unit,String pos) {
		super(context);
		this.mContext = context;
		inflate(mContext, R.layout.task_manager_human_inductor_select_values_dialog, this);
		valuesWheel = (WheelView) findViewById(R.id.sensor_device_choose_wheel_values);
		TextView unitText = (TextView) findViewById(R.id.sensor_device_choose_wheel_values_unit);
		unitText.setText(unit);
		valuesWheel.setScrollCycle(true);
		valuesAdapter = new NumberAdapter(valuesStr);
		valuesWheel.setAdapter(valuesAdapter);
		valuesWheel.setSelection(StringUtil.toInteger(pos), true);
		((WheelTextView)valuesWheel.getSelectedView()).setTextSize(30);
		valuesWheel.setOnItemSelectedListener(new OnItemSelectedListener() {

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
			}

			@Override
			public void onNothingSelected(TosAdapterView<?> parent) {
				
			}
		});
		valuesWheel.setUnselectedAlpha(0.5f);
	}
	
	public WheelView getValuesWheel() {
		return valuesWheel;
	}

	public void setValuesWheel(WheelView valuesWheel) {
		this.valuesWheel = valuesWheel;
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
