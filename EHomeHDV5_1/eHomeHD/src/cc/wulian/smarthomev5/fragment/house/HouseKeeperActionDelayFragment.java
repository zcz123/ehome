package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cc.wulian.app.model.device.DesktopCameraDevice;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionSelectDeviceActivity;
import cc.wulian.smarthomev5.adapter.house.AutoActionTaskAdapter;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionSelectDeviceFragment.AddLinkDeviceListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment.SelectControlDeviceDataListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.DragListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class HouseKeeperActionDelayFragment extends WulianFragment implements
		OnClickListener {

	private final String CHOOSE_TYPE_DELAY = "choose_type_delay";
	private final String CHOOSE_TYPE_DELAY_CANCEL = "choose_type_delay_cancel";
	private final String CHOOSE_TYPE_NO_DELAY = "choose_type_no_delay";
	public static AutoActionInfo actionInfo;
	private String currentChooseType = CHOOSE_TYPE_NO_DELAY;

	@ViewInject(R.id.device_setting_delay_text_item_ll)
	private LinearLayout settingDelayTextItemLinearlayout;
	@ViewInject(R.id.device_setting_delay_ll)
	private LinearLayout settingDelayLinearlayout;
	@ViewInject(R.id.device_setting_delay_cancel_ll)
	private LinearLayout settingDelayCancelLinearlayout;
	@ViewInject(R.id.device_setting_no_delay_ll)
	private LinearLayout settingNoDelayLinearlayout;
	@ViewInject(R.id.device_setting_delay_minute_text)
	private TextView settingDelayMinuteText;
	@ViewInject(R.id.device_setting_delay_sencond_text)
	private TextView settingDelaySecondText;
	@ViewInject(R.id.device_setting_delay_iv)
	private ImageView deviceSettingDelayImagView;
	@ViewInject(R.id.device_setting_delay_cancel_iv)
	private ImageView deviceSettingDelayCancelImagView;
	@ViewInject(R.id.device_setting_no_delay_iv)
	private ImageView deviceSettingNoDelayImagView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.house_keeper_action_delay_activity, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
//		actionInfo = (AutoActionInfo) getArguments().get("actionInfo");
		initBar();
		initListener();
		initView();
	}

	private void initView() {
		String cancelDelay=actionInfo.getCancelDelay();
		String delay=actionInfo.getDelay();
		if(cancelDelay!=null&&cancelDelay.equals("1")){
			deviceSettingDelayCancelImagView.setImageDrawable(mActivity
					.getResources().getDrawable(R.drawable.home_yes_select));
		}else if(delay!=null&& Integer.parseInt(delay)>0){
			int delayMinute=Integer.parseInt(actionInfo.getDelay())/60;
			int delaySendcos=Integer.parseInt(actionInfo.getDelay())%60;
			settingDelayMinuteText.setText(delayMinute + "m");
			settingDelaySecondText.setText(delaySendcos + "s");
			deviceSettingDelayImagView.setImageDrawable(mActivity
					.getResources().getDrawable(R.drawable.home_yes_select));
		}else{
			deviceSettingNoDelayImagView.setImageDrawable(mActivity
					.getResources().getDrawable(R.drawable.home_yes_select));
		}
	}

	private void initListener() {
		settingDelayLinearlayout.setOnClickListener(this);
		settingNoDelayLinearlayout.setOnClickListener(this);
		settingDelayCancelLinearlayout.setOnClickListener(this);
		settingDelayTextItemLinearlayout.setOnClickListener(this);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(null);
		getSupportActionBar().setRightIconText(R.string.set_sound_notification_bell_prompt_choose_complete);
		getSupportActionBar().setTitle(R.string.housekeeper_set_the_delay_time);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						switch (currentChooseType) {
						case CHOOSE_TYPE_DELAY:
							HouseKeeperActionTaskFragment.isSaveTask = true;
							actionInfo.setCancelDelay(null);
							break;
						case CHOOSE_TYPE_DELAY_CANCEL:
							HouseKeeperActionTaskFragment.isSaveTask = true;
							actionInfo.setCancelDelay("1");
							actionInfo.setDelay(null);
							break;
						case CHOOSE_TYPE_NO_DELAY:
							actionInfo.setCancelDelay(null);
							actionInfo.setDelay(null);
							break;
						}
						mActivity.finish();
					}
				});
	}

	@Override
	public void onClick(View arg0) {
		initAllItemImagViewStatus();
		switch (arg0.getId()) {
		case R.id.device_setting_delay_ll:
			currentChooseType = CHOOSE_TYPE_DELAY;
			deviceSettingDelayImagView.setImageDrawable(mActivity
					.getResources().getDrawable(R.drawable.home_yes_select));
			break;
		case R.id.device_setting_delay_cancel_ll:
			currentChooseType = CHOOSE_TYPE_DELAY_CANCEL;
			deviceSettingDelayCancelImagView.setImageDrawable(mActivity
					.getResources().getDrawable(R.drawable.home_yes_select));
			break;
		case R.id.device_setting_no_delay_ll:
			currentChooseType = CHOOSE_TYPE_NO_DELAY;
			deviceSettingNoDelayImagView.setImageDrawable(mActivity
					.getResources().getDrawable(R.drawable.home_yes_select));
			break;
		case R.id.device_setting_delay_text_item_ll:
			showSelectDelayTimeDialog(actionInfo, settingDelayMinuteText,
					settingDelaySecondText);
			break;
		}
	}

	private void initAllItemImagViewStatus() {
		deviceSettingDelayImagView.setImageDrawable(mActivity.getResources()
				.getDrawable(R.drawable.home_not_select));
		deviceSettingDelayCancelImagView.setImageDrawable(mActivity
				.getResources().getDrawable(R.drawable.home_not_select));
		deviceSettingNoDelayImagView.setImageDrawable(mActivity.getResources()
				.getDrawable(R.drawable.home_not_select));
	}

	private void showSelectDelayTimeDialog(final AutoActionInfo info,
			final TextView minute, final TextView seconds) {
		WLDialog delayTimeDialog;
		final HouseKeeperTaskDelayTimeView saskDelayTimeView = new HouseKeeperTaskDelayTimeView(
				mActivity, info.getDelay());
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(R.string.house_rule_add_new_task_delay_time);
		builder.setContentView(saskDelayTimeView);
		builder.setNegativeButton(mActivity.getResources().getString(
				R.string.cancel));
		builder.setPositiveButton(mActivity.getResources().getString(
				R.string.common_ok));
		builder.setCancelOnTouchOutSide(false);
		builder.setListener(new MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				HouseKeeperActionTaskFragment.isSaveTask = true;
				int delayMinute = saskDelayTimeView.getSettingMinuesTime();
				int delaySendcos = saskDelayTimeView.getSettingSecondsTime();
				info.setDelay((delayMinute * 60 + delaySendcos) + "");
				minute.setText(delayMinute + "m");
				seconds.setText(delaySendcos + "s");
				currentChooseType = CHOOSE_TYPE_DELAY;
				deviceSettingDelayImagView.setImageDrawable(mActivity
						.getResources().getDrawable(R.drawable.home_yes_select));
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		delayTimeDialog = builder.create();
		delayTimeDialog.show();
	}

}
