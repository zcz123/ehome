package cc.wulian.smarthomev5.fragment.setting.minigateway;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniSetTimePeroidActivity;
import cc.wulian.smarthomev5.activity.minigateway.MiniTimeVoiceActivity;
import cc.wulian.smarthomev5.event.CommondDeviceConfigurationEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;

public class SetNowTimeFragment extends WulianFragment implements
		OnClickListener {
	private String MINI_GATEWAY_NOW_TIME_KEY = "mini_gateway_now_time_key";
	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();

	@ViewInject(R.id.now_time_period_set_linearlayout)
	private LinearLayout mPeriodSetLinearLayout;
	@ViewInject(R.id.now_time_voice_set_linearlayout)
	private LinearLayout mTimeVoiceSetLinearLayout;
	@ViewInject(R.id.now_time_switch)
	private ToggleButton mSwitchSetToggleButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initBar();
		View rootView = inflater.inflate(R.layout.now_time, container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPeriodSetLinearLayout.setOnClickListener(this);
		mTimeVoiceSetLinearLayout.setOnClickListener(this);
		mSwitchSetToggleButton.setOnClickListener(this);
		mDialogManager.showDialog(MINI_GATEWAY_NOW_TIME_KEY, mActivity, null,
				null);
		
		//判断是否连接网关
		if((NetSDK.isConnected(info.getGwID()))){
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "3", null, "clock_enabled", null);
		}else{
			WLToast.showToastWithAnimation(getActivity(), getResources().getString(R.string.login_gateway_login_failed_hint),
					Toast.LENGTH_SHORT);
			getActivity().finish();
		}
			
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.miniGW_Chime));
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent();
		switch (arg0.getId()) {
		case R.id.now_time_period_set_linearlayout:
			intent.setClass(mActivity, MiniSetTimePeroidActivity.class);
			mActivity.startActivity(intent);
			break;
		case R.id.now_time_voice_set_linearlayout:
			intent.setClass(mActivity, MiniTimeVoiceActivity.class);
			mActivity.startActivity(intent);
			break;
		case R.id.now_time_switch:
			mDialogManager.showDialog(MINI_GATEWAY_NOW_TIME_KEY, mActivity,
					null, null);
			boolean isCheck = mSwitchSetToggleButton.isChecked();
			onCheckedChanged(isCheck);
			break;
		}
	}

	public void onCheckedChanged(boolean isChecked) {
		if (isChecked) {
			String jsonDate = "{\"e\":" + "\"" + "1" + "\"" + "}";
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null, "clock_enabled", jsonDate);
		} else {
			String jsonDate2 = "{\"e\":" + "\"" + "0" + "\"" + "}";
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null, "clock_enabled", jsonDate2);
		}
	}

	public void onEventMainThread(CommondDeviceConfigurationEvent event) {
		mDialogManager.dimissDialog(MINI_GATEWAY_NOW_TIME_KEY, 0);
		JSONObject object = JSON.parseObject(event.data);
		
		String jsonObject_str = object.getString("e");
		if (jsonObject_str.equals("1")) {
			mSwitchSetToggleButton.setChecked(true);
		} else if (jsonObject_str.equals("0")) {
			mSwitchSetToggleButton.setChecked(false);
		}
	}
}
