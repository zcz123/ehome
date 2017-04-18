package cc.wulian.smarthomev5.fragment.monitor;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class DVR4MonitorView extends AbstractMonitorView {

	int camType;

	private EditText monitorNameEditText;
	private EditText monitorUserEditText;
	private EditText monitorHostEditText;
	private EditText monitorPwdEditText;
	private EditText monitorPortEditText;

	private Button monitorEditButton;
	private Button btnDelete;
	private ImageView monitor4ImageView;
	private ImageView monitor8ImageView;

	private LinearLayout camera_choose;
	private LinearLayout monitor4LinearLayout;
	private LinearLayout monitor8LinearLayout;

	private Spinner monitorAreaNameSpinner;
	private MonitorAreaInfoAdapter mMonitorAreaInfoAdapter;

	private int mPort;

	private CameraDao cameraDao = CameraDao.getInstance();
	protected CameraInfo mCameraInfo;

	public DVR4MonitorView(BaseActivity context, CameraInfo info) {
		super(context, info);
	}

	@Override
	public View onCreateView() {
		if(cameraInfo.isForSetting){
			view = inflater.inflate(R.layout.monitor_ip_setview_setting, null);
		}else {
			view = inflater.inflate(R.layout.monitor_ip_setview, null);
		}
		return view;
	}

	@Override
	public void onViewCreated() {

		camera_choose = (LinearLayout) view
				.findViewById(R.id.camera_type_choose);
		camera_choose.setVisibility(View.VISIBLE);

		monitor4LinearLayout = (LinearLayout) view
				.findViewById(R.id.camera_type_dvr_4);
		monitor8LinearLayout = (LinearLayout) view
				.findViewById(R.id.camera_type_dvr_8);

		monitorAreaNameSpinner = (Spinner) view
				.findViewById(R.id.monitor_Areaname_Choose);
		mMonitorAreaInfoAdapter = new MonitorAreaInfoAdapter(mContext,
				AreaGroupManager.getInstance().getDeviceAreaEnties());
		monitorAreaNameSpinner.setAdapter(mMonitorAreaInfoAdapter);
//		monitorAreaNameSpinner
//				.setSelection(mMonitorAreaInfoAdapter.getCount() - 1);
		monitorAreaNameSpinner
				.setOnItemSelectedListener(new OnitemSelectedListener1());

		monitorNameEditText = (EditText) view
				.findViewById(R.id.monitorNameEditText);
		monitorUserEditText = (EditText) view
				.findViewById(R.id.monitorUserEditText);
		monitorHostEditText = (EditText) view
				.findViewById(R.id.monitorHostEditText);
		monitorPwdEditText = (EditText) view
				.findViewById(R.id.monitorPwdEditText);
		monitorPortEditText = (EditText) view
				.findViewById(R.id.monitorPortEditText);
		monitor4ImageView = (ImageView) view.findViewById(R.id.dvr_4_imageView);
		monitor8ImageView = (ImageView) view.findViewById(R.id.dvr_8_imageView);

		if (cameraInfo.getCamType() == -1 || cameraInfo.getCamId() == -1) {
			monitorAreaNameSpinner.setSelection(0);
		} else {
			monitorAreaNameSpinner.setSelection(mMonitorAreaInfoAdapter
					.getPositionByAreaID(cameraInfo.getAreaID()));
			monitorNameEditText.setText(cameraInfo.getCamName());
			monitorUserEditText.setText(cameraInfo.getUsername());
			monitorHostEditText.setText(cameraInfo.getHost());
			monitorPwdEditText.setText(cameraInfo.getPassword());
			monitorPortEditText.setText(cameraInfo.getPort() + "");
		}

		if (cameraInfo.getCamType() == 8) {
			camType = CameraInfo.CAMERA_TYPE_DVR_8;
			monitor4ImageView.setVisibility(View.INVISIBLE);
			monitor8ImageView.setVisibility(View.VISIBLE);
		} else {
			camType = CameraInfo.CAMERA_TYPE_DVR_4;
			monitor4ImageView.setVisibility(View.VISIBLE);
			monitor8ImageView.setVisibility(View.INVISIBLE);
		}

		monitorEditButton = (Button) view
				.findViewById(R.id.monitor_edit_ip_button);

		monitorEditButton.setOnClickListener(new OnClickListenerImp());
		monitor4LinearLayout.setOnClickListener(new OnClickListenerImp4());
		monitor8LinearLayout.setOnClickListener(new OnClickListenerImp8());
		if(cameraInfo.isForSetting){
			btnDelete = (Button) view.findViewById(R.id.monitor_edit_delete);
			btnDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CameraDao().delete(cameraInfo);//删除摄像机
					mContext.finish();
				}
			});
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

	/**
	 * 四路按钮单击触发
	 * 
	 * @author ylz
	 * 
	 */
	public class OnClickListenerImp4 implements OnClickListener {
		@Override
		public void onClick(View v) {
			camType = CameraInfo.CAMERA_TYPE_DVR_4;
			monitor4ImageView.setVisibility(View.VISIBLE);
			monitor8ImageView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 八路按钮单击触发
	 */
	public class OnClickListenerImp8 implements OnClickListener {
		@Override
		public void onClick(View v) {
			camType = CameraInfo.CAMERA_TYPE_DVR_8;
			monitor4ImageView.setVisibility(View.INVISIBLE);
			monitor8ImageView.setVisibility(View.VISIBLE);
		}
	}

	private void getValueFromView() {
		int mPortInteger ;
		String mCamName = monitorNameEditText.getText().toString().trim();
		String mUserName = monitorUserEditText.getText().toString().trim();
		String mHost = monitorHostEditText.getText().toString().trim();
		String mPassWord = monitorPwdEditText.getText().toString().trim();
		String mPort = monitorPortEditText.getText().toString().trim();
		if(!StringUtil.isNullOrEmpty(mPort))
		{
			mPortInteger = StringUtil.toInteger(monitorPortEditText.getText().toString().trim());
		}else{
			mPortInteger = 12201;
		}

		cameraInfo.setGwId(AccountManager.getAccountManger().getmCurrentInfo()
				.getGwID());
		cameraInfo.setCamName(mCamName);
		cameraInfo.setIconId(0);
		cameraInfo.setCamType(camType);
		cameraInfo.setUid("");
		cameraInfo.setHost(mHost);
		cameraInfo.setPort(mPortInteger);
		cameraInfo.setUsername(mUserName);
		cameraInfo.setPassword(mPassWord);
		cameraInfo.setBindDev("");
	}

	private boolean whetherAllEditTextFilled() {
		final TextView nameText = monitorNameEditText;
		final TextView userNameText = monitorUserEditText;
		// final TextView hostText = monitorHostEditText;
		final TextView passText = monitorPwdEditText;
		final TextView portText = monitorPortEditText;

		// before we judge all input values, reset view error hint
		nameText.setError(null);
		userNameText.setError(null);
		// hostText.setError(null);
		passText.setError(null);
		portText.setError(null);

		boolean allFilled = true;
		TextView errorView = null;
		if (StringUtil.isNullOrEmpty(cameraInfo.getCamName())) {
			allFilled = false;
			errorView = nameText;
		} else if (StringUtil.isNullOrEmpty(cameraInfo.getUsername())) {
			allFilled = false;
			errorView = userNameText;
		} else if (StringUtil.isNullOrEmpty(cameraInfo.getPassword())) {
			allFilled = false;
			errorView = passText;
		} else if (mPort == -1) {
			allFilled = false;
			errorView = portText;
		}

		if (errorView != null) {
			errorView.requestFocus();
			errorView.setError(mContext.getResources().getString(
					R.string.home_monitor_cloud_1_not_null));
		}
		return allFilled;
	}
}
