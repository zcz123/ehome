package cc.wulian.smarthomev5.fragment.setting.sxgateway;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.CommondDeviceConfigurationEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLToast;

public class SXHeartFragment extends WulianFragment implements OnClickListener {

	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();

	@ViewInject(R.id.sxgateway_setting_switch)
	private ToggleButton mSwitchToggleButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "3", null, null, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		initBar();
		View rootView = inflater.inflate(R.layout.sx_gateway_heart_tool,
				container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSwitchToggleButton.setOnClickListener(this);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.controlCenter_Intimate_tool));
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.sxgateway_setting_switch:
			boolean isCheck = mSwitchToggleButton.isChecked();
			onCheckedChanged(isCheck);
			break;
		default:
			break;
		}
	}
	public void onCheckedChanged(boolean isChecked) {
		if (isChecked) {
			String jsonDate = "{\"switch\":" + "\"" + "1" + "\"" + "}";   //{"switch":"1"}
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null,
					"light", jsonDate);
		} else {
			String jsonDate2 = "{\"switch\":" + "\"" + "0" + "\"" + "}";
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null,
					"light", jsonDate2);
			WLToast.showToastWithAnimation(getActivity(),
					getResources().getString(R.string.controlcenter_intimate_tool_lights_closed_hint), Toast.LENGTH_SHORT);
		}
	}

	public void onEventMainThread(CommondDeviceConfigurationEvent event) {
		JSONObject object = JSON.parseObject(event.data);

		String jsonObject_str = object.getString("switch");
		if (jsonObject_str.equals("1")) {
			mSwitchToggleButton.setChecked(true);
		} else if (jsonObject_str.equals("0")) {
			mSwitchToggleButton.setChecked(false);
		}
	}
}
