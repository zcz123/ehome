package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.app.model.device.impls.controlable.fancoil.program.FanCoilProgramActivity;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class ProgramSettingFragment extends WulianFragment{

	private final String TAG = getClass().getSimpleName();
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	
	private DeviceCache cache;
	private AbstractDevice device;

	private TextView programHeatTv;
	private TextView programCoolTv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("ProgramSettingInfo");
		mGwId = bundle.getString(FanCoilUtil.GWID);
		mDevId = bundle.getString(FanCoilUtil.DEVID);
		mEp = bundle.getString(FanCoilUtil.EP);
		mEpType = bundle.getString(FanCoilUtil.EPTYPE);
		cache=DeviceCache.getInstance(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mActivity.getResources().getString(R.string.AP_program_mode));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_fancoil_setting_program, container, false);
		programHeatTv = (TextView) rootView.findViewById(R.id.fancoil_program_set_heat_tv);
		programCoolTv = (TextView) rootView.findViewById(R.id.fancoil_program_set_cool_tv);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		programHeatTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				jumpToProgramActivity(FanCoilUtil.MODE_HEAT);
			}
		});
		
		programCoolTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				jumpToProgramActivity(FanCoilUtil.MODE_COOL);
			}
		});
	}

	private void jumpToProgramActivity(String mode){
		Intent intent  = new Intent(mActivity, FanCoilProgramActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FanCoilUtil.GWID, mGwId);
		bundle.putString(FanCoilUtil.DEVID, mDevId);
		bundle.putString(FanCoilUtil.EP, mEp);
		bundle.putString(FanCoilUtil.EPTYPE, mEpType);
		bundle.putString("programMode" , mode);
		intent.putExtra("FanCoilProgramFragmentInfo", bundle);
		mActivity.startActivity(intent);
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
	}

	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		handleEpData(mEpData);
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 40){
				
			}
			
		}
	}
	
	
}
