package cc.wulian.app.model.device.impls.controlable.cooker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class ElectricCookerFunctionView {

	private Context mContext;
	private GridView gridView;
	private ArrayList<CookerFunctionentity> functionList = new ArrayList<CookerFunctionentity>();
	
	private LayoutInflater inflater;
	private LinearLayout mFunctionLayout;
	private String cookerFunction;
	private int functionPosition;
	public View mFunctionView(){
		return mFunctionLayout;
	}
	
	public ElectricCookerFunctionView(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mFunctionLayout = (LinearLayout) inflater.inflate(R.layout.device_cooker_function_gridview, null);
		
		gridView = (GridView) mFunctionLayout.findViewById(R.id.device_cooker_gridview);
		
		mFunction();
		
		final FunctionAdapter functionAdapter = new FunctionAdapter(mContext, functionList);
		gridView.setAdapter(functionAdapter);
		cookerFunction = WL_E2_Electric_cooker.cookerFunction;
		if(!StringUtil.isNullOrEmpty(cookerFunction)){
			functionAdapter.setSelection(StringUtil.toInteger(cookerFunction) - 1);
		}
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				functionAdapter.setSelection(position);
				functionPosition = position + 1;
				functionAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public String getFunctionPosition(){
		return String.valueOf(functionPosition);
	}
	
	public static class CookerFunctionentity{
		private String functionID;
		private String functionName;
		
		public String getFunctionID() {
			return functionID;
		}
		public void setFunctionID(String functionID) {
			this.functionID = functionID;
		}
		public String getFunctionName() {
			return functionName;
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}
	}
	
	private void mFunction(){
		CookerFunctionentity cookerFunctionentity0 = new CookerFunctionentity();
		cookerFunctionentity0.setFunctionID("0");
		cookerFunctionentity0.setFunctionName(mContext.getResources().getString(R.string.device_pure_rice_cooked));
		
		CookerFunctionentity cookerFunctionentity1 = new CookerFunctionentity();
		cookerFunctionentity1.setFunctionID("1");
		cookerFunctionentity1.setFunctionName(mContext.getResources().getString(R.string.device_quick_cooking_rice));
		
		CookerFunctionentity cookerFunctionentity2 = new CookerFunctionentity();
		cookerFunctionentity2.setFunctionID("2");
		cookerFunctionentity2.setFunctionName(mContext.getResources().getString(R.string.device_grain_of_rice));
		
		CookerFunctionentity cookerFunctionentity3 = new CookerFunctionentity();
		cookerFunctionentity3.setFunctionID("3");
		cookerFunctionentity3.setFunctionName(mContext.getResources().getString(R.string.device_cooker_ribs));
		
		CookerFunctionentity cookerFunctionentity4 = new CookerFunctionentity();
		cookerFunctionentity4.setFunctionID("4");
		cookerFunctionentity4.setFunctionName(mContext.getResources().getString(R.string.device_cooker_soup));
		
		CookerFunctionentity cookerFunctionentity5 = new CookerFunctionentity();
		cookerFunctionentity5.setFunctionID("5");
		cookerFunctionentity5.setFunctionName(mContext.getResources().getString(R.string.device_cooker_cake));
		
		CookerFunctionentity cookerFunctionentity6 = new CookerFunctionentity();
		cookerFunctionentity6.setFunctionID("6");
		cookerFunctionentity6.setFunctionName(mContext.getResources().getString(R.string.device_cooker_chickens));
		
		CookerFunctionentity cookerFunctionentity7 = new CookerFunctionentity();
		cookerFunctionentity7.setFunctionID("7");
		cookerFunctionentity7.setFunctionName(mContext.getResources().getString(R.string.device_cooker_beef));
		
		CookerFunctionentity cookerFunctionentity8 = new CookerFunctionentity();
		cookerFunctionentity8.setFunctionID("8");
		cookerFunctionentity8.setFunctionName(mContext.getResources().getString(R.string.device_cooker_bean));
		
		functionList.add(cookerFunctionentity0);
		functionList.add(cookerFunctionentity1);
		functionList.add(cookerFunctionentity2);
		functionList.add(cookerFunctionentity3);
		functionList.add(cookerFunctionentity4);
		functionList.add(cookerFunctionentity5);
		functionList.add(cookerFunctionentity6);
		functionList.add(cookerFunctionentity7);
		functionList.add(cookerFunctionentity8);
	}
}

