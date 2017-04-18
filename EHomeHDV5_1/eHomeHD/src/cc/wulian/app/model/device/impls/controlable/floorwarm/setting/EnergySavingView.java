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

public class EnergySavingView extends LinearLayout{

	private String[] tempArray = { "10", "10.5","11", "11.5", "12", "12.5", "13", "13.5", "14", "14.5",
			"15", "15.5", "16", "16.5", "17", "17.5", "18", "18.5", "19","19.5","20","20.5","21"};

	private Context mContext;
	private WheelView mTempView = null;
    private NumberAdapter tempAdapter;
//    private String tempValue;
	private int temp;

	public EnergySavingView(Context context) {
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
