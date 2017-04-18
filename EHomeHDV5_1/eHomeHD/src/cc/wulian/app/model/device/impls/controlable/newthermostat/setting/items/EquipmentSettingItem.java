package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.EquipmentSettingActivity;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class EquipmentSettingItem extends AbstractSettingItem{
	
	private boolean isEquipmentOpen = false;
	private ShowEquipmentDownViewListener equipmentDownViewListener;
	
	private static final int DRAWABLE_ERUIPMENT_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_ERUIPMENT_DOWN = R.drawable.thermost_setting_arrow_down;
	
	public EquipmentSettingItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Equipment Setting");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setEquipmentSetting();
	}
	
	public void setEquipmentDownViewListener(ShowEquipmentDownViewListener equipmentDownViewListener) {
		this.equipmentDownViewListener = equipmentDownViewListener;
	}

	public void setEquipmentSetting() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0,3, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_ERUIPMENT_DOWN);
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isEquipmentOpen){
			infoImageView.setBackgroundResource(DRAWABLE_ERUIPMENT_DOWN);
			isEquipmentOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_ERUIPMENT_UP);
			isEquipmentOpen = true;
		}
		
			equipmentDownViewListener.onViewOpenChangeed(isEquipmentOpen);
	}
	
	public interface ShowEquipmentDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
}
