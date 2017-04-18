package cc.wulian.smarthomev5.fragment.setting.minigateway;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.CommondDeviceConfigurationEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;

public class MiniTimeVoiceFragment extends WulianFragment implements
		OnClickListener {

	private String MINI_GATEWAY_TIME_VOICE_KEY = "mini_gateway_time_voice_key";

	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();

	@ViewInject(R.id.setting_manager_item_set_voice_yy)
	private LinearLayout setVoiceYYLinearLayout;
	@ViewInject(R.id.setting_manager_item_set_voice_dd)
	private LinearLayout setVoiceDDLinearLayout;
	@ViewInject(R.id.setting_manager_item_set_voice_bd)
	private LinearLayout setVoiceBDLinearLayout;
	@ViewInject(R.id.setting_manager_item_set_voice_bg)
	private LinearLayout setVoiceBGLinearLayout;
	private int currentNumber = 0;

	@ViewInject(R.id.setting_time_voice_image_yy)
	private ImageView setting_time_voice_imageView1;
	@ViewInject(R.id.setting_time_voice_image_dd)
	private ImageView setting_time_voice_imageView2;
	@ViewInject(R.id.setting_time_voice_image_bd)
	private ImageView setting_time_voice_imageView3;
	@ViewInject(R.id.setting_time_voice_image_bg)
	private ImageView setting_time_voice_imageView4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initBar();

		View rootView = inflater.inflate(R.layout.time_voice, container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		setVoiceYYLinearLayout.setOnClickListener(this);
		setVoiceBDLinearLayout.setOnClickListener(this);
		setVoiceBGLinearLayout.setOnClickListener(this);
		setVoiceDDLinearLayout.setOnClickListener(this);
		mDialogManager.showDialog(MINI_GATEWAY_TIME_VOICE_KEY, mActivity, null,
				null);
		NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "3", null,
				null, null);

	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.miniGW_Chime_sound));
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.setting_manager_item_set_voice_yy:
			mDialogManager.showDialog(MINI_GATEWAY_TIME_VOICE_KEY, mActivity,
					null, null);
			String jsonDate = "{\"t\":" + "\"" + "1" + "\"" + "}";
			// String jsonDateString = CompressUtil.compressString(jsonDate);
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null,
					"clock_voice", jsonDate);
			break;
		case R.id.setting_manager_item_set_voice_dd:
			mDialogManager.showDialog(MINI_GATEWAY_TIME_VOICE_KEY, mActivity,
					null, null);
			String jsonDate2 = "{\"t\":" + "\"" + "2" + "\"" + "}";
			// String jsonDateString2 = CompressUtil.compressString(jsonDate2);
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null,
					"clock_voice", jsonDate2);
			break;
		case R.id.setting_manager_item_set_voice_bd:
			mDialogManager.showDialog(MINI_GATEWAY_TIME_VOICE_KEY, mActivity,
					null, null);
			String jsonDate3 = "{\"t\":" + "\"" + "3" + "\"" + "}";
			// String jsonDateString3 = CompressUtil.compressString(jsonDate3);
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null,
					"clock_voice", jsonDate3);
			break;
		case R.id.setting_manager_item_set_voice_bg:
			mDialogManager.showDialog(MINI_GATEWAY_TIME_VOICE_KEY, mActivity,
					null, null);
			String jsonDate4 = "{\"t\":" + "\"" + "4" + "\"" + "}";
			// String jsonDateString4 = CompressUtil.compressString(jsonDate4);
			NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "2", null,
					"clock_voice", jsonDate4);
			break;
		}
	}

	public void onEventMainThread(CommondDeviceConfigurationEvent event) {
		mDialogManager.dimissDialog(MINI_GATEWAY_TIME_VOICE_KEY, 0);
		// String data_v = event.jsonObject.getString("v");
		// String deCompress_v = CompressUtil.deCompressString(data_v);
		// String deCompress_v = data_v;
		// JSONObject jsonObject = (JSONObject) JSON.parse(deCompress_v);
		// String jsonObject_str = jsonObject.getString("t");

		JSONObject object = JSON.parseObject(event.data);
		String jsonObject_str = object.getString("t");
		System.out.println("--------------" + event.data);

		if ("1".equals(jsonObject_str)) {
			setting_time_voice_imageView1.setVisibility(View.VISIBLE);
			setting_time_voice_imageView2.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView3.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView4.setVisibility(View.INVISIBLE);
		} else if ("2".equals(jsonObject_str)) {
			setting_time_voice_imageView2.setVisibility(View.VISIBLE);
			setting_time_voice_imageView1.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView3.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView4.setVisibility(View.INVISIBLE);
		} else if ("3".equals(jsonObject_str)) {
			setting_time_voice_imageView3.setVisibility(View.VISIBLE);
			setting_time_voice_imageView1.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView2.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView4.setVisibility(View.INVISIBLE);

		} else if ("4".equals(jsonObject_str)) {
			setting_time_voice_imageView4.setVisibility(View.VISIBLE);
			setting_time_voice_imageView1.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView2.setVisibility(View.INVISIBLE);
			setting_time_voice_imageView3.setVisibility(View.INVISIBLE);
		}

	}

}
