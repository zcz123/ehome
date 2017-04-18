package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;

public class OverTempProtectView extends LinearLayout{

	private String[] tempArray = { "40", "40.5","41","41.5","42","42.5","43","43.5","44","44.5",
			"45", "45.5", "46", "46.5", "47", "47.5", "48", "48.5", "49","49.5","50","50.5","51","51.5",
			"52","52.5","53","53.5","54","54.5","55","55.5","56","56.5","57","57.5","58","58.5","59",
			"59.5","60","60.5","61","61.5","62","62.5","63","63.5","64","64.5","65","65.5","66","66.5",
			"67","67.5","68","68.5","69","69.5","70","70.5","71","71.5","72","72.5","73","73.5","74","74.5","75"};

	private Context mContext;
	private WheelView mTempView = null;
    private NumberAdapter tempAdapter;
//    private String tempValue;
	private int temp;

	public OverTempProtectView(Context context) {
		super(context);
		this.mContext = context;
		inflate(mContext, R.layout.device_floorwarm_energy_saving_dialog, this);
		mTempView = (WheelView) findViewById(R.id.energy_saving_wheelView);

		mTempView.setScrollCycle(true);
		tempAdapter = new NumberAdapter(tempArray);
		mTempView.setAdapter(tempAdapter);
		mTempView.setSelection(0, true);
        ((WheelTextView)mTempView.getSelectedView()).setTextSize(30);
		mTempView.setOnItemSelectedListener(mListener);
		mTempView.setUnselectedAlpha(0.5f);
	}

	public void setSettingTempValue(String tempValue){
		for (int i=0;i<tempArray.length;i++){
			if(StringUtil.equals(tempValue , tempArray[i])){
				mTempView.setSelection(i, true);
			}
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
			temp = mTempView.getSelectedItemPosition();
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

	public String getSettingTempValue(){
		return tempArray[temp];
	}

}
