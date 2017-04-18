package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;

public class SystemTypeSettingFragment extends WulianFragment implements View.OnClickListener{

	private final String TAG = getClass().getSimpleName();

	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private String mReturnId;
	private String mSystemType;

	private DeviceCache cache;
	private AbstractDevice device;

	@ViewInject(R.id.floorheating_setting_systemtype_elec_layout)
	private LinearLayout elecLayout;
	@ViewInject(R.id.floorheating_setting_systemtype_water_layout)
	private LinearLayout waterLayout;
	@ViewInject(R.id.floorheating_setting_systemtype_elec_iv)
	private ImageView  elecImageView;
	@ViewInject(R.id.floorheating_setting_systemtype_water_iv)
	private ImageView waterImageView;

	private WLDialog selectDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("SystemTypeSettingInfo");
		mGwId = bundle.getString(FloorWarmUtil.GWID);
		mDevId = bundle.getString(FloorWarmUtil.DEVID);
		mEp = bundle.getString(FloorWarmUtil.EP);
		mEpType = bundle.getString(FloorWarmUtil.EPTYPE);
		mSystemType = bundle.getString("systemType");

		cache= DeviceCache.getInstance(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.AP_select_program));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_floorwarm_setting_systemtype, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		elecLayout.setOnClickListener(this);
		waterLayout.setOnClickListener(this);

	}

	@Override
	public void onShow() {
		super.onShow();
		initBar();
		loadData();
	}

	@Override
	public void onResume() {
		super.onResume();
		initBar();
		loadData();
	}

	private void loadData(){
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				initView();
			}
		});
	}

	private boolean isFloorWater(){
		if(StringUtil.equals(mSystemType, FloorWarmUtil.SYSTEM_TYPE_WATER_TAG)){
			return true;
		}else{
			return false;
		}
	}
	private void initView(){
		if(!StringUtil.isNullOrEmpty(mSystemType)){
			if (isFloorWater()){
				waterImageView.setVisibility(View.VISIBLE);
				elecImageView.setVisibility(View.GONE);
			}else{
				waterImageView.setVisibility(View.GONE);
				elecImageView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.floorheating_setting_systemtype_elec_layout:
				if(isFloorWater()){
					showElectDialog();
				}
				break;
			case R.id.floorheating_setting_systemtype_water_layout:
				if(!isFloorWater()){
					showWaterDialog();
				}
				break;
			default:
				break;
		}
	}

	private void showWaterDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mActivity.getResources().getString(R.string.operation_title))
				.setSubTitleText(null)
				.setMessage(mApplication.getResources().getString(R.string.AP_real_chooseWater))
				.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel))
				.setPositiveButton(mActivity.getResources().getString(R.string.common_ok))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View view) {
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.SYSTEM_TYPE_WATER_CMD);
					}
					@Override
					public void onClickNegative(View view) {

					}
				});
		selectDialog = builder.create();
		selectDialog.show();
	}

	private void showElectDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mActivity.getResources().getString(R.string.operation_title))
				.setSubTitleText(null)
				.setMessage(mApplication.getResources().getString(R.string.AP_real_chooseWarm))
				.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel))
				.setPositiveButton(mActivity.getResources().getString(R.string.common_ok))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View view) {
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.SYSTEM_TYPE_ELECT_CMD);
					}
					@Override
					public void onClickNegative(View view) {

					}
				});
		selectDialog = builder.create();
		selectDialog.show();
	}

	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		handleEpData(mEpData);
		isFloorWater();
		initView();
	}

	private void handleEpData(String epData) {

		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 28){
				mReturnId = epData.substring(0, 2);
				mSystemType = epData.substring(26, 28);
			}
			if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.SYSTEM_TYPE_TAG)){
				mReturnId = epData.substring(0, 2);
				mSystemType = epData.substring(2, 4);
			}
		}
	}

}
