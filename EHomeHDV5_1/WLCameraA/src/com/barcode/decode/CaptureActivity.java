package com.barcode.decode;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.barcode.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.android.camera.CameraManager;
import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.view.device.config.HandInputDeviceIdActivity;
import com.wulian.icam.view.device.config.DeviceIdQueryActivity;

/**
 * @Function: 扫描二维码
 * @date: 2014年10月24日
 * @author Wangjj
 */
public class CaptureActivity extends Activity implements
		SurfaceHolder.Callback, View.OnClickListener {

	private boolean hasSurface = false;
	private String characterSet = "UTF-8";
	private String msgData = null;
	private String originDeviceId = null;
	private ViewfinderView viewfinderView;
	private boolean isV2BarcodeScan = false;
	/**
	 * 活动监控器，用于省电，如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
	 * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;
	private CameraManager cameraManager;
	private Vector<BarcodeFormat> decodeFormats;// 编码格式
	private CaptureActivityHandler mHandler;// 解码线程

	private ImageView titlebar_back;// 返回按钮
	private Button titlebar_flash;// 闪光灯
	private ProgressDialog mProgress;// 进度
	private ImageView titlebar_edit;

	private AlertDialog mBarcodeTipsDialog;// 二维码提示对话框
	private View mBarcodeTipsView;// 二维码提示界面

	// 感兴趣的数据
	private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
			.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initSetting();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		initView();
		initData();
		initListeners();

	}

	private void initView() {
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		cameraManager = new CameraManager(getApplicationContext());//getApplication()
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		titlebar_flash = (Button) findViewById(R.id.titlebar_flash);
		titlebar_edit = (ImageView) findViewById(R.id.titlebar_edit);

	}

	private void initData() {
		isV2BarcodeScan = getIntent().getBooleanExtra("isV2BarcodeScan", false);
		if (isV2BarcodeScan) {
			SharedPreferences sp = getSharedPreferences(APPConfig.SP_CONFIG,
					Context.MODE_PRIVATE);
			String uuid = ICamGlobal.getInstance().getUserinfo().getUuid();
			if (sp.getBoolean(uuid + APPConfig.FIRST_SHOW_CAMERA_BARCODE, true)) {
				showBarcodeDialogTips();
				sp.edit()
						.putBoolean(uuid + APPConfig.FIRST_SHOW_CAMERA_BARCODE, false)
						.commit();
			}
		}
	}

	private void initListeners() {
		titlebar_back.setOnClickListener(this);
		titlebar_flash.setOnClickListener(this);
		titlebar_edit.setOnClickListener(this);
	}

	// 展示二维码提示对话框
	private void showBarcodeDialogTips() {
		DialogUtils.showBarcodeTipDialog(this);
	}

	/**
	 * @Function 初始化窗口设置
	 * @author Wangjj
	 * @date 2014年10月24日
	 */
	private void initSetting() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕高亮
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 主要对相机进行初始化工作
	 */
	@Override
	protected void onResume() {
		super.onResume();
		inactivityTimer.onActivity();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			// 如果SurfaceView已经渲染完毕，会回调surfaceCreated，其中已经调用了initCamera()
			surfaceHolder.addCallback(this);
			// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		// 恢复活动监控器
		inactivityTimer.onResume();
	}

	/**
	 * 暂停活动监控器,关闭摄像头
	 */
	@Override
	protected void onPause() {
		if (mHandler != null) {
			mHandler.quitSynchronously();
			mHandler = null;
		}
		// 暂停活动监控器
		inactivityTimer.onPause();
		// 关闭摄像头
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
		super.onPause();
	}

	/**
	 * 停止活动监控器,保存最后选中的扫描类型
	 */
	@Override
	protected void onDestroy() {
		// 停止活动监控器
		inactivityTimer.shutdown();
		if (mProgress != null) {
			mProgress.dismiss();
		}
		msgData = null;
		originDeviceId = null;
		super.onDestroy();
	}

	/**
	 * 获取扫描结果，由CaptureActivityHandler回调 。这里缺少方法申明的约束。
	 * 
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		mProgress = new ProgressDialog(CaptureActivity.this, R.style.dialog);
		mProgress.show();
		View mProgressView = getLayoutInflater().inflate(
				R.layout.custom_progress_dialog,
				(ViewGroup) findViewById(R.id.custom_progressdialog));
		((TextView) mProgressView.findViewById(R.id.tv_desc))
				.setText(R.string.config_scan_success_processing);
		mProgress.setContentView(mProgressView);

		// mProgress = ProgressDialog.show(CaptureActivity.this, null,
		// "已扫描，正在处理···", true, true);
		mProgress.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				restartPreviewAfterDelay(1l);
			}
		});

		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT);
		Map<ResultMetadataType, Object> metadata = rawResult
				.getResultMetadata();
		StringBuilder metadataText = new StringBuilder(20);
		if (metadata != null) {
			for (Map.Entry<ResultMetadataType, Object> entry : metadata
					.entrySet()) {
				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
			}
		}
		parseBarCode(rawResult.getText());
	}

	// 解析二维码
	private void parseBarCode(String msg) {
		// 手机震动
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);
		// if (!msg.startsWith("cmic")) {
		// msg = "cmic" + msg;
		// }

		if (isV2BarcodeScan && !TextUtils.isEmpty(msg)) {
			Intent it = new Intent(CaptureActivity.this,
					DeviceIdQueryActivity.class);
			it.putExtra("msgData", msg);
			it.putExtra("isAddDevice", true);
			startActivity(it);
			this.finish();
		} else {
			setResult(RESULT_OK, new Intent().putExtra("scan_result", msg));
			finish();
		}

	}

	/**
	 * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
	 * 
	 * @param delayMS
	 */
	public void restartPreviewAfterDelay(long delayMS) {
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	/**
	 * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the mHandler starts the preview, which can also throw a
			// RuntimeException.
			if (mHandler == null) {
				mHandler = new CaptureActivityHandler(this, decodeFormats,
						null, characterSet, cameraManager);

			}
		} catch (IOException ioe) {
		} catch (RuntimeException e) {
		}
	}

	/**
	 * 初始化照相机失败显示窗口
	 */

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_flash) {
			setFlash();
		} else if (id == R.id.titlebar_edit) {
			startActivityForResult(new Intent(this,
					HandInputDeviceIdActivity.class), 1);
		} else if (id == R.id.titlebar_back) {
			finish();
		} else {
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			String msg = data.getStringExtra("deviceid");
			if (isV2BarcodeScan && !TextUtils.isEmpty(msg)) {
				Intent it = new Intent(CaptureActivity.this,
						DeviceIdQueryActivity.class);
				it.putExtra("msgData", msg);
				it.putExtra("isAddDevice", true);
				startActivity(it);
				this.finish();
			} else {
				setResult(RESULT_OK, new Intent().putExtra("scan_result", msg));
				finish();
			}
		}
	}

	private void setFlash() {
		if (titlebar_flash.getTag() == null) {// 默认关闭状态
			cameraManager.setTorch(true);
			titlebar_flash.setTag("on");
			titlebar_flash.setBackgroundResource(R.drawable.flash_on);
		} else {// 开启状态
			cameraManager.setTorch(false);
			titlebar_flash.setTag(null);
			titlebar_flash.setBackgroundResource(R.drawable.flash_off);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {

		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	/**
	 * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
	 */
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}
}
