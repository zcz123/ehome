package cc.wulian.app.model.device.impls.controlable.metalswitch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SendMessage;

public class SettingFragment extends WulianFragment {

	private final String TAG = getClass().getSimpleName();
	View rootView ;
	@ViewInject(R.id.metalswitch_mode_setting_layout_01)
	private LinearLayout modeLayout01;
	@ViewInject(R.id.metalswitch_mode_setting_layout_02)
	private LinearLayout modeLayout02;
	@ViewInject(R.id.metalswitch_mode_setting_layout_03)
	private LinearLayout modeLayout03;
	@ViewInject(R.id.metalswitch_mode_setting_tv_01)
	private TextView tvMode01;
	@ViewInject(R.id.metalswitch_mode_setting_tv_02)
	private TextView tvMode02;
	@ViewInject(R.id.metalswitch_mode_setting_tv_03)
	private TextView tvMode03;

	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private String mSwitchMode14;
	private String mSwitchMode15;
	private String mSwitchMode16;
	private static final String PRE_LOAD_KEY = "switch_dialog_key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments().getBundle("SettingFragmentInfo");
		mGwId = bundle.getString("gwID");
		mDevId = bundle.getString("devID");
		mEp = bundle.getString("ep");
		mEpType = bundle.getString("epType");
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("返回");
		getSupportActionBar().setTitle("设置");
	}

	private void initLayout(){
		if(!StringUtil.isNullOrEmpty(mEpType) && StringUtil.equals(mEpType,"Ao")){
			modeLayout03.setVisibility(View.VISIBLE);
		}else{
			modeLayout03.setVisibility(View.GONE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.device_an_switch_setting, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initLayout();
		modeLayout01.setOnClickListener(new SettingClickListener());
		modeLayout02.setOnClickListener(new SettingClickListener());
		modeLayout03.setOnClickListener(new SettingClickListener());
	}

	@Override
	public void onShow() {
		super.onShow();
		initBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		initBar();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, "000");
	}

	private void initView(){
		if(!StringUtil.isNullOrEmpty(mSwitchMode14)){
			if (mDialogManager.containsDialog(PRE_LOAD_KEY)) {
				mDialogManager.dimissDialog(PRE_LOAD_KEY, 0);
			}
			if(StringUtil.equals(mSwitchMode14 ,AbstractMetalSwitch.SWTICH_MODE_TURN)){
				tvMode01.setText("开关模式");
			}else if(StringUtil.equals(mSwitchMode14 ,AbstractMetalSwitch.SWTICH_MODE_SCENCE)){
				tvMode01.setText("场景模式");
			}else if(StringUtil.equals(mSwitchMode14 ,AbstractMetalSwitch.SWTICH_MODE_BIND)){
				tvMode01.setText("绑定模式");
			}
		}
		if(!StringUtil.isNullOrEmpty(mSwitchMode15)){
			if (mDialogManager.containsDialog(PRE_LOAD_KEY)) {
				mDialogManager.dimissDialog(PRE_LOAD_KEY, 0);
			}
			if(StringUtil.equals(mSwitchMode15 ,AbstractMetalSwitch.SWTICH_MODE_TURN)){
				tvMode02.setText("开关模式");
			}else if(StringUtil.equals(mSwitchMode15 ,AbstractMetalSwitch.SWTICH_MODE_SCENCE)){
				tvMode02.setText("场景模式");
			}else if(StringUtil.equals(mSwitchMode15 ,AbstractMetalSwitch.SWTICH_MODE_BIND)){
				tvMode02.setText("绑定模式");
			}
		}

		if(!StringUtil.isNullOrEmpty(mSwitchMode16)){
			if (mDialogManager.containsDialog(PRE_LOAD_KEY)) {
				mDialogManager.dimissDialog(PRE_LOAD_KEY, 0);
			}
			if(StringUtil.equals(mSwitchMode16 ,AbstractMetalSwitch.SWTICH_MODE_TURN)){
				tvMode03.setText("开关模式");
			}else if(StringUtil.equals(mSwitchMode16 ,AbstractMetalSwitch.SWTICH_MODE_SCENCE)){
				tvMode03.setText("场景模式");
			}else if(StringUtil.equals(mSwitchMode16 ,AbstractMetalSwitch.SWTICH_MODE_BIND)){
				tvMode03.setText("绑定模式");
			}
		}
	}

	class SettingClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.metalswitch_mode_setting_layout_01:
					Intent intent1 = getSettingIntent("14",AbstractMetalSwitch.SWTICH_TYPE_01);
					mActivity.startActivity(intent1);
					break;
				case R.id.metalswitch_mode_setting_layout_02:
					Intent intent2 =getSettingIntent("15",AbstractMetalSwitch.SWTICH_TYPE_02);
					mActivity.startActivity(intent2);
					break;
				case R.id.metalswitch_mode_setting_layout_03:
					Intent intent3 =getSettingIntent("16",AbstractMetalSwitch.SWTICH_TYPE_03);
					mActivity.startActivity(intent3);
					break;
				default:
					break;
			}
		}
	}

	public Intent getSettingIntent(String ep , String switchType) {
		Intent intent = new Intent(mActivity, ModeSettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("gwID", mGwId);
		bundle.putString("devID", mDevId);
		bundle.putString("ep", ep);
		bundle.putString("epType", mEpType);
		bundle.putString("switchType",switchType);
		intent.putExtra("ModeSettingFragmentInfo", bundle);
		return intent;
	}

	public void onEventMainThread(DeviceEvent event){
		mEpData= event.deviceInfo.getDevEPInfo().getEpData();
		if(!StringUtil.isNullOrEmpty(mEpData)){
			handleEpData(mEpData);
		}
		initView();
	}

	private void handleEpData(String epData) {
		Log.d("---ccc---", "mEpData-settingfragment:::"+epData);
		if(epData.length() == 6){
			if(StringUtil.equals(epData.substring(2 ,4) ,"01")){
				if(StringUtil.equals(epData.substring(0 ,2) ,"00")){
					if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_TURN)){
						mSwitchMode14 = AbstractMetalSwitch.SWTICH_MODE_TURN;
					}else if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_SCENCE)){
						mSwitchMode14 = AbstractMetalSwitch.SWTICH_MODE_SCENCE;
					}else if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_BIND)){
						mSwitchMode14 = AbstractMetalSwitch.SWTICH_MODE_BIND;
					}
				}
			}else if(StringUtil.equals(epData.substring(2 ,4) ,"02")){
				if(StringUtil.equals(epData.substring(0 ,2) ,"00")){
					if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_TURN)){
						mSwitchMode15 = AbstractMetalSwitch.SWTICH_MODE_TURN;
					}else if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_SCENCE)){
						mSwitchMode15 = AbstractMetalSwitch.SWTICH_MODE_SCENCE;
					}else if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_BIND)){
						mSwitchMode15 = AbstractMetalSwitch.SWTICH_MODE_BIND;
					}
				}
			}else if(StringUtil.equals(epData.substring(2 ,4) ,"03")){
				if(StringUtil.equals(epData.substring(0 ,2) ,"00")){
					if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_TURN)){
						mSwitchMode16 = AbstractMetalSwitch.SWTICH_MODE_TURN;
					}else if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_SCENCE)){
						mSwitchMode16 = AbstractMetalSwitch.SWTICH_MODE_SCENCE;
					}else if(StringUtil.equals(epData.substring(4 ,6) ,AbstractMetalSwitch.SWTICH_MODE_BIND)){
						mSwitchMode16 = AbstractMetalSwitch.SWTICH_MODE_BIND;
					}
				}
			}
		}
	}


}
