package cc.wulian.smarthomev5.activity.monitor;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.CameraUtil;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public abstract class AbstractMonitorViewActivity extends EventBusActivity {
	private static final String TAG = AbstractMonitorViewActivity.class
			.getSimpleName();

	protected CameraInfo mCamInfo;
	protected int rotation = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mCamInfo = (CameraInfo) getIntent().getSerializableExtra(
				CameraInfo.EXTRA_CAMERA_INFO);

		if (StringUtil.isNullOrEmpty(mCamInfo.host)) {
			mCamInfo.host = AccountManager.getAccountManger().getmCurrentInfo()
					.getGwSerIP();
		}

		super.onCreate(savedInstanceState);
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.home_camera_monitor));
		getCompatActionBar().setTitle(mCamInfo.camName);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		// Mark: use view background instead of it, because of issue #167
		// getWindow().setBackgroundDrawableResource(android.R.color.black);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initSDK();
		initContentView();
		initUi();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startPlaySurfaceView();
		CameraUtil.isCameraRunning = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopPlaySurfaceView();
		CameraUtil.isCameraRunning = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uninitSDK();
	}

	protected Handler getMessageAction() {
		return mHandler;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CmdUtil.MESSAGE_PROCESS_DISMISS) {
				ProgressDialogManager.getDialogManager().dimissDialog(TAG, 0);
			} else if (msg.what == CmdUtil.MESSAGE_PROCESS_SHOW) {
				ProgressDialogManager
						.getDialogManager()
						.showDialog(TAG, AbstractMonitorViewActivity.this, "",
								null).setCancelable(false);
			}
		};
	};
	public  void initContentView(){
		
	}
	public  void initUi(){
		
	}
	protected abstract void initPortCtrlUI();

	protected abstract void initLandCtrlUI();

	protected abstract void initRotationUI(Configuration configuration);

	protected abstract void initSDK();

	protected abstract void uninitSDK();

	protected abstract void startPlaySurfaceView();

	protected abstract void stopPlaySurfaceView();

	protected abstract void listenin();

	protected abstract void speakout();

	protected abstract void cruise();

	protected abstract void horizontalRotation();

	protected abstract void verticalRotation();

	protected abstract void snapshot();

	protected abstract void showErrToast(int errCode);
}
