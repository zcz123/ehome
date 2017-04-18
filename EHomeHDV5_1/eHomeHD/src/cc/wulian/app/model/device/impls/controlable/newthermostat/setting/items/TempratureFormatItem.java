package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class TempratureFormatItem extends AbstractSettingItem{
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private boolean isFormatC = true;
	
	private static final String FORMAT_CMD_C = "50";
	private static final String FORMAT_CMD_F= "51";
	
	private static final String FORMAT_F= "01";
	private static final String FORMAT_C= "00";
	
	private static final int DRAWABLE_FORMAT_F= R.drawable.thermost_setting_format_f;
	private static final int DRAWABLE_FORMAT_C = R.drawable.thermost_setting_format_c;

	
	
	public TempratureFormatItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Temprature Format");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setTempratureFormat();
	}

	public void setFormatData(String gwId,String devId,String ep,String epType){
		mGwId = gwId;
		mDevId = devId;
		mEp = ep;
		mEpType = epType;
	}
	
	//判断  温标 是 C 还是 F
	public void setIsFormatC(String formatData){
		if(StringUtil.equals(formatData, FORMAT_C)){
			isFormatC = true;
		}
		if(StringUtil.equals(formatData, FORMAT_F)){
			isFormatC = false;
		}
		setFormatImage(isFormatC);
	}

	private void setFormatImage(boolean isFormatC){
		if(isFormatC){
			infoImageView.setBackgroundResource(DRAWABLE_FORMAT_C);
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_FORMAT_F);
		}
	}
	
	
	public void setTempratureFormat() {
		nameTextView.setTextColor(Color.parseColor("#3e3e3e"));
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		nameTextView.setLayoutParams(params);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
//		infoImageView.setBackgroundResource(DRAWABLE_FORMAT_C);
		
		infoImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(isFormatC){
					SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FORMAT_CMD_F);
				}else{
					SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FORMAT_CMD_C);
				}
				
			}
		});;
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
