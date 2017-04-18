package cc.wulian.smarthomev5.fragment.monitor;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.adapter.MonitorAreaInfoAdapter;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.AccountManager;

public class CLOUD_3_MonitorView extends AbstractMonitorView {
	private EditText monitoruidEditText;
	private EditText monitorCameraNameEditText;
	private EditText monitorPwdEditText;
	private Button monitorEditButton;
	private Button cameraButton;
	private LinearLayout monitorSearchLayout;
	private  Button searchButton;
	private Button deleteButton;
	private String mUid;
	private String mCamName;
	private String mPassWord;
	private int mPort;
	private String mLinkageDevice;
	private Spinner monitorAreaNameSpinner;
	private MonitorAreaInfoAdapter mMonitorAreaInfoAdapter;

	private CameraDao cameraDao = CameraDao.getInstance();
	protected CameraInfo mCameraInfo;

	public CLOUD_3_MonitorView(BaseActivity context, CameraInfo info) {
		super(context, info);
	}

	@Override
	public View onCreateView() {
		if(cameraInfo.isForSetting){
			view = inflater.inflate(R.layout.monitor_cloud1_setview_setting, null);
		}else {
			view = inflater.inflate(R.layout.monitor_cloud1_setview, null);
		}
		return view;
	}

	@Override
	public void onViewCreated() {
		monitorAreaNameSpinner = (Spinner) view
				.findViewById(R.id.monitor_Areaname_Choose);
		mMonitorAreaInfoAdapter = new MonitorAreaInfoAdapter(mContext,
				AreaGroupManager.getInstance().getDeviceAreaEnties());
		monitorAreaNameSpinner.setAdapter(mMonitorAreaInfoAdapter);
		monitorAreaNameSpinner
				.setOnItemSelectedListener(new OnitemSelectedListener1());

		monitoruidEditText = (EditText) view
				.findViewById(R.id.monitorUIDEditText);
		monitorPwdEditText = (EditText) view
				.findViewById(R.id.monitorPwdEditText);
		monitorCameraNameEditText = (EditText) view
				.findViewById(R.id.monitorCameraNameEditText);
		monitorSearchLayout = (LinearLayout) view
				.findViewById(R.id.monitor_fast_getUid_layout);
		monitorSearchLayout.setVisibility(View.GONE);
		// cameraButton = (Button) view.findViewById(R.id.cameraSearchButton);
		// monitorEditButton.setOnClickListener(new OnClickListenerCamSearch());

		if (cameraInfo.getCamType() == -1 || cameraInfo.getCamId() == -1) {
			monitorAreaNameSpinner.setSelection(0);
		} else {
			monitorAreaNameSpinner.setSelection(mMonitorAreaInfoAdapter
					.getPositionByAreaID(cameraInfo.getAreaID()));
			monitoruidEditText.setText(cameraInfo.getUid());
			monitorPwdEditText.setText(cameraInfo.getPassword());
			monitorCameraNameEditText.setText(cameraInfo.getUsername());
		}
		monitorEditButton = (Button) view
				.findViewById(R.id.monitor_edit_cloud1_button);
		monitorEditButton.setOnClickListener(new OnClickListenerImp());
		if(cameraInfo.isForSetting){
			deleteButton = (Button) view.findViewById(R.id.btn_monitor_cloud_delete);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CameraDao().delete(cameraInfo);//删除摄像机
					mContext.finish();
				}
			});
		}
	}

	public class OnClickListenerCamSearch implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 二维码扫描
			// mScanBarcode.initiateScan();
		}
	}


	public class OnClickListenerImp implements OnClickListener {
		@Override
		public void onClick(View v) {
			getValueFromView();
			if (whetherAllEditTextFilled()) {
				if (cameraInfo.camId == -1) {
					cameraDao.insert(cameraInfo);
				} else {
					cameraDao.update(cameraInfo);
				}
				((Activity) mContext).finish();
			}
		}
	}

	private class OnitemSelectedListener1 implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {
			monitorAreaNameSpinner.setSelection(position);
			DeviceAreaEntity item = mMonitorAreaInfoAdapter.getItem(position);
			cameraInfo.setAreaID(item.getRoomID());
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
		}

	}

	private void getValueFromView() {
		String mUid = monitoruidEditText.getText().toString().trim();
		String mCamName = monitorCameraNameEditText.getText().toString().trim();
		String mPassWord = monitorPwdEditText.getText().toString().trim();

		cameraInfo.setGwId(AccountManager.getAccountManger().getmCurrentInfo()
				.getGwID());
		cameraInfo.setIconId(0);
		cameraInfo.setCamType(CameraInfo.CAMERA_TYPE_CLOUD_3);
		cameraInfo.setUid(mUid);
		cameraInfo.setHost("");
		cameraInfo.setPort(0);
		cameraInfo.setUsername(mCamName);
		cameraInfo.setPassword(mPassWord);
		cameraInfo.setBindDev("");

	}

	private boolean whetherAllEditTextFilled() {
		final TextView uidText = monitoruidEditText;
		final TextView camNameText = monitorCameraNameEditText;
		// final TextView hostText = monitorHostEditText;
		final TextView passText = monitorPwdEditText;

		// before we judge all input values, reset view error hint
		uidText.setError(null);
		camNameText.setError(null);
		// hostText.setError(null);
		passText.setError(null);

		boolean allFilled = true;
		TextView errorView = null;
		if (StringUtil.isNullOrEmpty(cameraInfo.getUid())) {
			allFilled = false;
			errorView = uidText;
		} else if (StringUtil.isNullOrEmpty(cameraInfo.getCamName())) {
			allFilled = false;
			errorView = camNameText;
		} else if (StringUtil.isNullOrEmpty(cameraInfo.getPassword())) {
			allFilled = false;
			errorView = passText;
		}

		if (errorView != null) {
			errorView.requestFocus();
			errorView.setError(mContext.getResources().getString(
					R.string.home_monitor_cloud_1_not_null));
		}
		return allFilled;
	}
}
