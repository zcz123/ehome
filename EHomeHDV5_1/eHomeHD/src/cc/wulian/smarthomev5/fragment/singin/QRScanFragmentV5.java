package cc.wulian.smarthomev5.fragment.singin;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MonitoringConnectionActivity;
import cc.wulian.smarthomev5.activity.monitor.MonitorAddActivity;
import cc.wulian.smarthomev5.event.ScanEvent;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.fragment.singin.handler.QRScanHandler;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.view.ViewfinderView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.android.InactivityTimer;
import com.google.zxing.client.android.IntentSource;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.Intents;
import com.lidroid.xutils.util.LogUtils;
import de.greenrobot.event.EventBus;

/**
 * 扫码，返回UID
 * */
public class QRScanFragmentV5 extends SherlockFragment implements OnClickListener,IQRScanHandlerResult {

	protected static final String TAG = QRScanFragmentV5.class.getSimpleName();
	private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;

	private QRScanHandler handler;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private IntentSource source;
	private boolean hasSurface;

	private ViewfinderView viewfinderView;
	private SurfaceView surfaceView;

	private TextView scancode;

	private boolean isScan = false;
	private boolean isFirst = true;
	private EventBus mEventBus = EventBus.getDefault();

	// 接受传递过来的值 做个识别
	private String deviceflag = "";
	private boolean hitvFlag = false;
	// 几个控件加载进来
	private LinearLayout ll_scan_device_titlelbar, ll_scan_device_link;
	private Button mlinkentry;
	// titlebar 里面的控件
	private ImageView titlebarback, titlebarlinput;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Intent it = activity.getIntent();
		deviceflag = it.getStringExtra("wulianScan");
		hitvFlag = it.getBooleanExtra("HitvScanJudge",false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView;
		if (hitvFlag){
			rootView = inflater.inflate(R.layout.fragment_hitvscan, null);
		}else{
			rootView = inflater.inflate(R.layout.fragment_qrscan, null);
		}
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		viewfinderView = (ViewfinderView) view.findViewById(R.id.view_viewfinder);
		surfaceView = (SurfaceView) view.findViewById(R.id.view_preview);

		surfaceView.getHolder().addCallback(callback);
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		scancode = (TextView) view.findViewById(R.id.tv_scan_or_devices_code);
		if(hitvFlag){
			scancode.setOnClickListener(this);
		}else{
			if (deviceflag.equals("wulianScan")){
				// 本为隐藏控件，需要条件判断显示
				ll_scan_device_titlelbar = (LinearLayout) view.findViewById(R.id.ll_scan_device_titlelbar);
				ll_scan_device_link = (LinearLayout) view.findViewById(R.id.ll_scan_device_link);
				mlinkentry = (Button) view.findViewById(R.id.btn_scan_link_entry);
				// 这是在include里面的控件
				titlebarback = (ImageView) view.findViewById(com.wulian.icam.R.id.iv_scan_titlebar_back);
				titlebarlinput = (ImageView) view.findViewById(R.id.titlebar_devices_manual_input);// 手动输出控件
				scancode.setText(R.string.scan_qr_code_add_devices);
				setControlDisplayDisappears();
			}
		}

	}

	// 判断是谁在调用，是否显示控件
	protected void setControlDisplayDisappears() {
		if (deviceflag.equals("wulianScan")) {
			ll_scan_device_link.setVisibility(View.VISIBLE);
			ll_scan_device_titlelbar.setVisibility(View.VISIBLE);
		}
		mlinkentry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(getActivity(), MonitoringConnectionActivity.class);
				startActivity(it);
				QRScanFragmentV5.this.getActivity().finish();
			}
		});
		titlebarback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击退出
				QRScanFragmentV5.this.getActivity().finish();
			}
		});

		titlebarlinput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// CLOUD_WL_MonitorView 要跳转的页面 暂时完成
				Intent it = new Intent(getActivity(), MonitorAddActivity.class);
				startActivity(it);
				getActivity().finish();
			}
		});
	}

	public void stopScan() {

		if (!isScan) {
			return;
		}

		isScan = false;
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		if (CameraManager.get() != null) {
			CameraManager.get().closeDriver();
		}
	}

	public void startScan() {

		// if (isFirst) {
		// isFirst = false;
		// return;
		// }

		if (isScan) {
			return;
		}

		CameraManager.init(getActivity().getApplication());
		boolean isLandscape=false;
		Intent intent=getActivity().getIntent();
		if(hitvFlag){
			isLandscape=true;
		}else{
			isLandscape=false;
		}
		CameraManager.get().setIsLandscape(isLandscape);
		getActivity().setRequestedOrientation(
				CameraManager.get().isLandscape() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
						: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		inactivityTimer = new InactivityTimer(getActivity());

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(callback);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		source = IntentSource.NATIVE_APP_INTENT;
		isScan = true;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mEventBus.unregister(this);
		stopScan();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mEventBus.register(this);
		startScan();
	}

	private Callback callback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			hasSurface = false;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (!hasSurface) {
				hasSurface = true;
				initCamera(holder);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// TODO Auto-generated method stub

		}
	};

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}
	@Override
	public Handler getHandler() {
		return handler;
	}
	@Override
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {

		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new QRScanHandler(this, decodeFormats, "utf-8");
		}
	}

	/**
	 * 处理二维码结果
	 * 
	 * @param rawResult
	 * @param barcode
	 */
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		Log.d(TAG,rawResult.getBarcodeFormat().toString()+"   数据："+rawResult.getText());
		switch (source) {
		// we do not have any other type
		case NATIVE_APP_INTENT:
		case NONE:
		case PRODUCT_SEARCH_LINK:
		case ZXING_LINK:
			handleDecodeExternally(rawResult, barcode);
			break;
		default:
			break;
		}

	}

	private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
		if(hitvFlag){
			JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,rawResult.toString(),JsUtil.OK, false);
		}else{}
		viewfinderView.drawResultBitmap(barcode);
		long resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			playBeepSoundAndVibrate();
		}
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
		intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());

		byte[] rawBytes = rawResult.getRawBytes();
		if (rawBytes != null && rawBytes.length > 0) {
			intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
		}
		Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
		if (metadata != null) {
			Integer orientation = (Integer) metadata.get(ResultMetadataType.ORIENTATION);
			if (orientation != null) {
				intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
			}
			String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
			if (ecLevel != null) {
				intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
			}
		}
		sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);
	}

	private void sendReplyMessage(int id, Object arg, long delayMS) {
		Message message = Message.obtain(handler, id, arg);
		if (delayMS > 0L) {
			handler.sendMessageDelayed(message, delayMS);
		} else {
			handler.sendMessage(message);
		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
		}
		if (inactivityTimer != null) {
			inactivityTimer.shutdown();
		}
		super.onDestroy();
	}
	@Override
	public void setResult(int result, Intent data) {

		if (result == Activity.RESULT_OK) {
			EventBus.getDefault().post(
					new ScanEvent(ScanEvent.CODE_RESULT_OK, data.getStringExtra(Intents.Scan.RESULT)));
		}
	}

	public void onEventMainThread(ScanEvent event) {
		LogUtils.d(event.toString());
		switch (event.getCode()) {
		case ScanEvent.CODE_REQUEST_SCAN:
			QRScanFragmentV5 fragmentStart = (QRScanFragmentV5) getFragmentManager().findFragmentByTag(
					QRScanFragmentV5.class.getSimpleName());
			fragmentStart.startScan();
			break;
		case ScanEvent.CODE_RESULT_OK:
		case ScanEvent.CODE_RESULT_CANCLE:
			// QRScanFragmentV5 fragmentStop = (QRScanFragmentV5)
			// getFragmentManager()
			// .findFragmentByTag(QRScanFragmentV5.class.getSimpleName());
			// fragmentStop.stopScan();
			if (!TextUtils.isEmpty(event.getResult())) {
				String resultString = event.getResult();//真正的数据
				Intent resultIntent = new Intent();
				resultIntent.putExtra(EditMonitorInfoFragment.RESULT_UID, resultString);
				// 此判断没有暂不需要 看以后是否需要 再做处理
				// if
				// (deviceflag.equals("nostart")||deviceflag.equals("wulianScan"))
				// {
				getActivity().setResult(EditMonitorInfoFragment.RESULT_OK, resultIntent);
				// }
			}
			this.getActivity().finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.tv_scan_or_devices_code:{
//				String resultString="inputBySelf";
//				String resultString="1KK0500HDN0000H58PF0300";
				String resultString="";
				JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,resultString,JsUtil.OK, false);
				this.getActivity().finish();
			};
		}
	}
}
