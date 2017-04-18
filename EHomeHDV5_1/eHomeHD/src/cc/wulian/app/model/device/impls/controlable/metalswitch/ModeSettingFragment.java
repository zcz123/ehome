package cc.wulian.app.model.device.impls.controlable.metalswitch;

import android.os.Bundle;
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
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SendMessage;

public class ModeSettingFragment extends WulianFragment {

	private final String TAG = getClass().getSimpleName();
	View rootView ;
	@ViewInject(R.id.metalswitch_mode_setting_turn_layout)
	private LinearLayout turnLayout;
	@ViewInject(R.id.metalswitch_mode_setting_scence_layout)
	private LinearLayout scenceLayout;
	@ViewInject(R.id.metalswitch_mode_setting_bind_layout)
	private LinearLayout bindLayout;
	@ViewInject(R.id.metalswitch_mode_setting_turn_iv)
	private ImageView turnSelectImage;
	@ViewInject(R.id.metalswitch_mode_setting_scence_tv)
	private TextView tvScenceName;
	@ViewInject(R.id.metalswitch_mode_setting_bind_tv)
	private TextView tvBindName;

	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private String mSwitchType;
	private String mSwitchMode;
	protected Map<String, SceneInfo> bindScenesMap;
	protected Map<String, DeviceInfo> bindDevicesMap;
	//查询命令
	private String queryCmd;
	//开关模式命令
	private String switchModeTurnCmd ;
	//场景模式命令
	private String switchModeScenceCmd ;
	//绑定模式命令
	private String switchModeBindCmd ;
	private static final String PRE_LOAD_KEY = "switch_dialog_key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments().getBundle("ModeSettingFragmentInfo");
		mGwId = bundle.getString("gwID");
		mDevId = bundle.getString("devID");
		mEp = bundle.getString("ep");
		mEpType = bundle.getString("epType");
		mSwitchType = bundle.getString("switchType");
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("返回");
		getSupportActionBar().setTitle("模式选择");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.device_am_switch_setting, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		turnLayout.setOnClickListener(new SettingClickListener());
		scenceLayout.setOnClickListener(new SettingClickListener());
		bindLayout.setOnClickListener(new SettingClickListener());
		tvScenceName.setOnClickListener(new SettingClickListener());
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
		initSetCmd();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, queryCmd);


	}

	private void initSetCmd(){
		if(!StringUtil.isNullOrEmpty(mSwitchType)){
			if(StringUtil.equals(mSwitchType ,AbstractMetalSwitch.SWTICH_TYPE_01)){
				queryCmd = "010";
				switchModeTurnCmd = "011";
				switchModeScenceCmd = "012";
				switchModeBindCmd = "013";
			}else if(StringUtil.equals(mSwitchType ,AbstractMetalSwitch.SWTICH_TYPE_02)){
				queryCmd = "020";
				switchModeTurnCmd = "021";
				switchModeScenceCmd = "022";
				switchModeBindCmd = "023";
			}else if(StringUtil.equals(mSwitchType ,AbstractMetalSwitch.SWTICH_TYPE_03)){
				queryCmd = "030";
				switchModeTurnCmd = "031";
				switchModeScenceCmd = "032";
				switchModeBindCmd = "033";
			}
		}
	}

	private void initView(){
		if(!StringUtil.isNullOrEmpty(mSwitchMode)){
			if (mDialogManager.containsDialog(PRE_LOAD_KEY)) {
				mDialogManager.dimissDialog(PRE_LOAD_KEY, 0);
			}
			if(StringUtil.equals(mSwitchMode ,WL_Am_switch_1.SWTICH_MODE_TURN)){
				setModeTurnSelected();
			}else if(StringUtil.equals(mSwitchMode ,WL_Am_switch_1.SWTICH_MODE_SCENCE)){
				if(isScenceModeChanged){
					showScenceDialog();
					isScenceModeChanged = false;
				}
				setModeScenceSelected();
			}else if(StringUtil.equals(mSwitchMode ,WL_Am_switch_1.SWTICH_MODE_BIND)){
				setModeBindSelected();
			}
		}
	}

	private boolean isScenceModeChanged = false;
	class SettingClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.metalswitch_mode_setting_turn_layout:
					if(!StringUtil.equals(mSwitchMode ,WL_Am_switch_1.SWTICH_MODE_TURN)){
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, switchModeTurnCmd);
						mDialogManager.showDialog(PRE_LOAD_KEY,mActivity,null,null);
					}
					break;
				case R.id.metalswitch_mode_setting_scence_layout:
					if(!StringUtil.equals(mSwitchMode ,WL_Am_switch_1.SWTICH_MODE_SCENCE)){
						isScenceModeChanged = true;
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, switchModeScenceCmd);
						mDialogManager.showDialog(PRE_LOAD_KEY,mActivity,null,null);
					}
					break;
				case R.id.metalswitch_mode_setting_bind_layout:
					if(!StringUtil.equals(mSwitchMode ,WL_Am_switch_1.SWTICH_MODE_BIND)){
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, switchModeBindCmd);
						mDialogManager.showDialog(PRE_LOAD_KEY,mActivity,null,null);
					}
					break;
				case R.id.metalswitch_mode_setting_scence_tv:
					showScenceDialog();
					break;
			}
		}
	}

	private void setModeTurnSelected(){
		turnSelectImage.setVisibility(View.VISIBLE);
		tvScenceName.setVisibility(View.GONE);
		tvBindName.setVisibility(View.INVISIBLE);
	}

	private void setModeScenceSelected(){
		turnSelectImage.setVisibility(View.GONE);
		tvScenceName.setVisibility(View.VISIBLE);
		tvBindName.setVisibility(View.INVISIBLE);
		String scenceName = "";
		getBindScenesMap();
		if (bindScenesMap.containsKey(mEp)) {
			SceneInfo sceneInfo = bindScenesMap.get(mEp);
			if(sceneInfo != null){
				scenceName = sceneInfo.getName();
				if(StringUtil.isNullOrEmpty(scenceName)){
					scenceName = getString(cc.wulian.app.model.device.R.string.device_no_bind_scene);;
				}
			}
		}else{
			scenceName = getString(cc.wulian.app.model.device.R.string.device_no_bind_scene);;
		}
		tvScenceName.setText(scenceName);
	}

	private void setModeBindSelected(){
		turnSelectImage.setVisibility(View.GONE);
		tvScenceName.setVisibility(View.GONE);
		tvBindName.setVisibility(View.VISIBLE);
		tvBindName.setText("绑定");
	}

	private void showScenceDialog(){
		final SceneList sceneList = new SceneList(mActivity, true);
		sceneList
				.setOnSceneListItemClickListener(new SceneList.OnSceneListItemClickListener() {

					@Override
					public void onSceneListItemClicked(
							SceneList list, int pos, SceneInfo info) {
						tvScenceName.setText(info.getName());
						bindScenesMap.put(mEp, info);
						JsonTool.uploadBindList(mActivity,
								bindScenesMap, bindDevicesMap,
								mGwId, mDevId, mEpType);
						sceneList.dismiss();
					}
				});
		sceneList.show(rootView);
	}


	public void onEventMainThread(DeviceEvent event){
		mEpData= event.deviceInfo.getDevEPInfo().getEpData();
		if(!StringUtil.isNullOrEmpty(mEpData)){
			handleEpData(mEpData);
		}
		initView();
	}

	private void handleEpData(String epData) {

		if(mEpData.length() == 6){
			if(StringUtil.equals(mEpData.substring(2 ,4) ,"01")){
				if(StringUtil.equals(mEpData.substring(0 ,2) ,"00")){
					if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_TURN)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_TURN;
					}else if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_SCENCE)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_SCENCE;
					}else if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_BIND)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_BIND;
					}
				}

			}
			if(StringUtil.equals(mEpData.substring(2 ,4) ,"02")){
				if(StringUtil.equals(mEpData.substring(0 ,2) ,"00")){
					if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_TURN)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_TURN;
					}else if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_SCENCE)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_SCENCE;
					}else if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_BIND)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_BIND;
					}
				}

			}

			if(StringUtil.equals(mEpData.substring(2 ,4) ,"03")){
				if(StringUtil.equals(mEpData.substring(0 ,2) ,"00")){
					if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_TURN)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_TURN;
					}else if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_SCENCE)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_SCENCE;
					}else if(StringUtil.equals(mEpData.substring(4 ,6) ,WL_Am_switch_1.SWTICH_MODE_BIND)){
						mSwitchMode = AbstractMetalSwitch.SWTICH_MODE_BIND;
					}
				}

			}
		}
	}

	protected void getBindScenesMap() {
		bindScenesMap = MainApplication.getApplication().bindSceneInfoMap
				.get(mGwId + mDevId);
		if (bindScenesMap == null) {
			bindScenesMap = new HashMap<String, SceneInfo>();
		}
	}

}
