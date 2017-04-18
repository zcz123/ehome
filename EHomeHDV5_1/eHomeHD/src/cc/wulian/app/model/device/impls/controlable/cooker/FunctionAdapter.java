package cc.wulian.app.model.device.impls.controlable.cooker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.cooker.ElectricCookerFunctionView.CookerFunctionentity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;

public class FunctionAdapter extends WLBaseAdapter<CookerFunctionentity>{
	
	
	private int selectedPosition = 0;
	public FunctionAdapter(Context context, ArrayList<CookerFunctionentity> functionList) {
		super(context, functionList);
	}
	
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.device_cooker_function_textview, null);
	}
	@Override
	protected void bindView(Context context, View view, int pos, CookerFunctionentity item) {
		super.bindView(context, view, pos, item);
		TextView textView = (TextView) view.findViewById(R.id.device_cooker_textview);
		textView.setText(item.getFunctionName());
		if(selectedPosition==pos){
			
			textView.setTextColor(mContext.getResources().getColor(R.color.holo_green_light));
		}else{
			textView.setTextColor(mContext.getResources().getColor(R.color.progress_text_black));
		}
	}

	public void setSelection(int position) {
		selectedPosition = position;
	}

}
