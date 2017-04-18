package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class TimingSceneItem extends AbstractSettingItem{
	private TimingSceneGroupEntity timingGroup = TimingSceneManager.getInstance().getDefaultGroup();
	private AccountManager accountManager = AccountManager.getAccountManger();
	private Preference preference = Preference.getPreferences();
	private boolean isOpen;
	public TimingSceneItem(Context context) {
		super(context,R.drawable.setting_timing_scene,R.string.scene_info_timing_scene);
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		final boolean isUpgrade = preference.getBoolean(
				IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
		if(!isUpgrade){
			if(timingGroup.isUseable()){
				infoImageView.setBackgroundResource(R.drawable.toggle_btn_checked);
				isOpen = true;
			}else{
				infoImageView.setBackgroundResource(R.drawable.toggle_btn_unchecked);
				isOpen = false;
			}
		}else{
			boolean status = preference.getBoolean(accountManager.getmCurrentInfo().getGwID() + "1" + IPreferenceKey.P_KEY_HOUSE_RULE_TIMING_STATUS, true);
			if(status){
				infoImageView.setBackgroundResource(R.drawable.toggle_btn_checked);
				isOpen = true;
			}else{
				infoImageView.setBackgroundResource(R.drawable.toggle_btn_unchecked);
				isOpen = false;
			}
		}
		
		
		infoImageView.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_TIMING)) {
					return;
				}

				if(isOpen){
					String status = CmdUtil.SCENE_UNUSE;
					showEditDeviceIrInfoDialog(status,isUpgrade);
				}else{
					String status = CmdUtil.SCENE_USING;
					if(isUpgrade){
						NetSDK.sendSetRuleTimerSceneMsg(accountManager.getmCurrentInfo().getGwID(), "set", "1", null, status);
					}else{
						NetSDK.sendSetTimerSceneMsg(accountManager.getmCurrentInfo().getGwID(), CmdUtil.MODE_SWITCH, timingGroup.groupID, timingGroup.groupName, status, null);
					}
					infoImageView.setBackgroundResource(R.drawable.toggle_btn_checked);
					isOpen = true;
				}
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}

	public void showEditDeviceIrInfoDialog(final String status, final boolean isUpgrade){
		View ContentView = View.inflate(mContext,R.layout.setting_timing_scene_switch_dialog, null);
		final TextView closeTimingDialogText= (TextView) ContentView.findViewById(R.id.setting_timing_scene_text_dialog);
		closeTimingDialogText.setText(mContext.getString(R.string.set_timing_secne_title_content));
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getString(R.string.set_timing_secne_title_dialog))
		.setContentView(ContentView)
		 .setPositiveButton(android.R.string.ok)
		 .setNegativeButton(android.R.string.cancel)
		 .setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				infoImageView.setBackgroundResource(R.drawable.toggle_btn_unchecked);
				isOpen = false;
				if(isUpgrade){
					NetSDK.sendSetRuleTimerSceneMsg(accountManager.getmCurrentInfo().getGwID(), "set", "1", null, status);
				}else{
					NetSDK.sendSetTimerSceneMsg(accountManager.getmCurrentInfo().getGwID(), CmdUtil.MODE_SWITCH, timingGroup.groupID, timingGroup.groupName, status, null);
				}
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
			}
		});
		WLDialog dialog = builder.create();
		dialog.show();
	}
}
