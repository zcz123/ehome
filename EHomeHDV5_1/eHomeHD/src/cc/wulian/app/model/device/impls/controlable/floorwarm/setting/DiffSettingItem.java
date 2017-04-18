package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.yuantuo.customview.ui.WLDialog;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class DiffSettingItem extends AbstractSettingItem {
	
	private String gwID;
	private String devID;
	private String ep;
	private String epType;

	private String diffTemp;
	private String diffSettingText = "";
	private Map<String, String>  diffTempMap;
	private WLDialog diffSettingDialog;
	private DiffSettingView diffSettingView;
	
	private String[] diffTempKeyArray = { "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
            "11", "12", "13", "14", "15", "16", "17", "18", "19"};
	private String[] diffTempCValueArray = { "1℃", "1.5℃","2℃", "2.5℃", "3℃", "3.5℃", "4℃", "4.5℃", "5℃", "5.5℃",
			"6℃", "6.5℃", "7℃", "7.5℃", "8℃", "8.5℃", "9℃", "9.5℃", "10℃"};
	
	
	public DiffSettingItem(Context context) {
		super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.AP_diff_set));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setDiffSetting();
		initDiffTempMap();
	}
	
	private void initDiffTempMap(){
		diffTempMap = new HashMap<>();
		for (int i = 0; i < diffTempKeyArray.length; i++) {
			diffTempMap.put(diffTempKeyArray[i], diffTempCValueArray[i]);
		}
	}

	public void setDiffSettingData(String gwID,String devID,String ep,String epType){
		this.gwID = gwID;
		this.devID = devID;
		this.ep = ep;
		this.epType = epType;
	}

	public void setDiffSettingTemp(String diffSettingTemp) {
		this.diffTemp = diffSettingTemp;
		setDiffSettingText(diffTemp);
	}
	
	private void setDiffSettingText(String temp){
		if(!StringUtil.isNullOrEmpty(temp)){
			diffSettingText = diffTempMap.get(FloorWarmUtil.hexStr2Str(temp));
		}
		infoTextView.setText(diffSettingText);
	}
	
	
	public void setDiffSetting() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setLayoutParams(params);
		infoTextView.setSingleLine(false);
		infoTextView.setTextColor(Color.parseColor("#737373"));
		infoTextView.setText(diffSettingText);
	}

	@Override
	public void doSomethingAboutSystem() {
		showDiffSettingDialog();
	}
	
	private void showDiffSettingDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.AP_diff_set));
		builder.setContentView(createViewDiff());
		builder.setPositiveButton(mContext.getResources().getString(R.string.common_ok));
		builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel));
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				String temp = diffSettingView.getSettingTempValue();
				String tempCmd = getTempCmd(temp);
				Log.i("difftemp","temp:"+temp+",tempCmd:"+tempCmd);
				SendMessage.sendControlDevMsg(gwID, devID, ep, epType, FloorWarmUtil.DIFF_SETTING_CMD+tempCmd);

			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		diffSettingDialog = builder.create();
		diffSettingDialog.show();
	}

	private View createViewDiff(){
		diffSettingView = new DiffSettingView(mContext);
		diffSettingView.setSettingTempValue(diffTempMap.get(FloorWarmUtil.hexStr2Str(diffTemp)));
		return diffSettingView;
	}

	private String getTempCmd(String temp){
		String tempCmd = "";
		for (Map.Entry entry : diffTempMap.entrySet()){
			if(StringUtil.equals( (String)entry.getValue() , temp)){
				tempCmd = (String)entry.getKey();
			}
		}
		return tempCmd;
	}

}
