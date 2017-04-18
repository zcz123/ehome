package com.wulian.icam.view.device.play;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.videoengine.FileUtils;
import org.webrtc.videoengine.ViERenderer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.VideoTimePeriod;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.utils.XMLHandler;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CameraHistorySeekBar;
import com.wulian.icam.view.widget.CameraHistorySeekBar.HistroySeekChangeListener;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.H264CustomeView;
import com.wulian.icam.view.widget.H264CustomeView.TakePictureCallBack;
import com.wulian.oss.Utils.OSSXMLHandler;
import com.wulian.oss.callback.ConnectDataCallBack;
import com.wulian.oss.model.FederationToken;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.service.WulianOssClient;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.utils.LibraryLoger;
import com.wulian.routelibrary.utils.LibraryPhoneStateUtil;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipProfile;

public class ReplayVideoActivity extends BaseFragmentActivity implements
		OnClickListener {

	private final static int PLAY_BACK_VIDEO_MSG = 1;// 所选时间段处理信息
	private final static int PLAY_BACK_VIDEO_ACTIVE_MSG = 2;// 第一次默s认视频处理信息

	private final static int SHOW_VIDEO_PROGRESS_MSG = 4;// 展示视频中间进度的消息
	private final static int SHOW_VIDEO_REPLAY_DATE_MSG = 5;// 展示视频当前文字信息
	private final static int CONTROL_SEEKBAR_PROGRESS_MSG = 6;// 控制Seekbar进度的信息
	private final static int REQUEST_NEXT_OBJECT_MSG = 7;// 40秒请求下次时间的信息
	private final static int PLAY_VIDEO_MSG = 9;// 直接播放的信息
	private final static int FILE_MOUNT_EXCEPTION = 10;
	private final static int TAKE_PICTURE_FAIL = 11;
	private final static int FILE_OK = 12;
	private final static int NO_SDCARD_MSG_CALLBACK = 20;
	private final static int NO_RECORD_VIDEO_MSG_CALLBACK = 21;
	private final static int NO_RIGHT_TO_SEE_MSG_CALLBACK = 22;
	private final static int REQUEST_TIMEOUT_MSG_CALLBACK = 23;
	private final static int REMOVE_PLAY_VIDEO_MSG = 100;
	private final static int NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG = 200;

	private final static int STREAM_HANDLE_MSG = 1000;// 视频流过来处理信息
	private Device mDevice;

	private GetObjectDataModel mNextObjectData;

	private SimpleDateFormat mDateAllFormat;
	private SimpleDateFormat mDateYMDFormat;
	private SimpleDateFormat mDateSimpleFormat;

	private RelativeLayout rl_replay_player;
	private LinearLayout ll_titlebar_back;// 返回
	private RelativeLayout rl_video_top_landscape;// 横屏时Top布局
	private RelativeLayout rl_control_landscape;// 横屏时布局
	private LinearLayout ll_control_portrait;// 竖屏时布局
	private RelativeLayout rl_seekbar_layout;// 进度布局
	private RelativeLayout rl_progress_replay_video;// 进度条提示布局

	private H264CustomeView view_h264video;// 播放View
	private CameraHistorySeekBar replay_historyseek;// 进度条

	private TextView tv_play_date;// 播放时间
	private TextView tv_seekbar_date;// 进度条日期
	private TextView tv_progress_video_tip;// 进度条视频提示

	private ImageView iv_progress_video;

	private Button btn_control_snapshot_landscape;// 横屏截图
	private Button btn_control_snapshot_portrait;// 竖屏时截图
	private Button btn_control_back_live_portrait;// 竖屏时返回直播
	private Button btn_control_quit_fullscreen_bar;// 结束全屏
	private LinearLayout ll_control_fullscreen_bar;// 全屏

	private CheckBox cb_control_record_landscape;// 横屏录制
	private CheckBox cb_control_record_portrait;// 竖屏时录制

	private GestureDetector mGestureDetector;
	private Animation mUpDownAnim;
	private MediaPlayer mMediaPlayer;

	private int mMaxWidth;
	private boolean mIsVideoInvert = false;
	private boolean mIsPortrait = true;
	private boolean mIsRecording = false;
	private boolean mIsFirstRequestRecord = true;
	private boolean mIsRequestVideo = true;
	private long mCurrentTimeStamp;// 当前Date实时显示时间戳
	private long mPlayProgressTimeStamp;// 当前选择播放进度显示时间戳
	private Date mDate;
	private List<Pair<Integer, Integer>> mRecordList;

	private int recordTime = 0;
	private SipProfile mAccount;
	private boolean mQueryHistory = true;
	private boolean mHasQueryData = false;
	private int mSeq = 1;
	private String mDeviceControlUrl = null;
	private String mSessionID = "";
	private WulianOssClient mClient;
	private WakeLock mVideoWakeLock;
	private PowerManager mPowerManager;
	private REPLAY_VIDEO_STATUS mReplayVideoStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
		setListeners();
	}

	protected void setViewContent() {
		setContentView(R.layout.activity_replay_video);
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoWakeLock != null) {
			mVideoWakeLock.acquire();
		}
	}

	protected void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}

		if (mUpDownAnim != null && mUpDownAnim.hasStarted()) {
			mUpDownAnim.cancel();
		}
		if (mVideoWakeLock != null) {
			mVideoWakeLock.release();
		}
		mPlayVideoHandler.removeCallbacksAndMessages(null);
		controlStopRecord();
		destroyOSS();
		super.onDestroy();
	}

	private enum REPLAY_VIDEO_STATUS {
		INIT, SEEKBAR_PROGRESS, GET_VIDEO, PLAY_VIDEO, DESTROY
	}

	private void initViews() {
		rl_progress_replay_video = (RelativeLayout) findViewById(R.id.rl_progress_replay_video);
		rl_video_top_landscape = (RelativeLayout) findViewById(R.id.rl_video_top_landscape);
		rl_replay_player = (RelativeLayout) findViewById(R.id.rl_replay_player);
		rl_control_landscape = (RelativeLayout) findViewById(R.id.rl_control_landscape);
		ll_control_portrait = (LinearLayout) findViewById(R.id.ll_control_portrait);
		rl_seekbar_layout = (RelativeLayout) findViewById(R.id.rl_seekbar_layout);
		view_h264video = (H264CustomeView) findViewById(R.id.view_h264video);
		replay_historyseek = (CameraHistorySeekBar) findViewById(R.id.replay_historyseek);
		ll_titlebar_back = (LinearLayout) findViewById(R.id.ll_titlebar_back);
		tv_play_date = (TextView) findViewById(R.id.tv_play_date);
		tv_seekbar_date = (TextView) findViewById(R.id.tv_seekbar_date);
		tv_progress_video_tip = (TextView) findViewById(R.id.tv_progress_video_tip);
		iv_progress_video = (ImageView) findViewById(R.id.iv_progress_video);
		cb_control_record_landscape = (CheckBox) findViewById(R.id.cb_control_record_landscape);
		btn_control_snapshot_landscape = (Button) findViewById(R.id.btn_control_snapshot_landscape);
		btn_control_snapshot_portrait = (Button) findViewById(R.id.btn_control_snapshot_portrait);
		btn_control_back_live_portrait = (Button) findViewById(R.id.btn_control_back_live_portrait);
		btn_control_quit_fullscreen_bar = (Button) findViewById(R.id.btn_control_quit_fullscreen_bar);
		ll_control_fullscreen_bar = (LinearLayout) findViewById(R.id.ll_control_fullscreen_bar);
		cb_control_record_portrait = (CheckBox) findViewById(R.id.cb_control_record_portrait);

	}

	private void initData() {
		mDevice = (Device) getIntent().getSerializableExtra("device");
		if (mDevice == null) {
			this.finish();
		}
		mIsVideoInvert = getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE)
				.getBoolean(mDevice.getDevice_id() + APPConfig.VIDEO_INVERT,
						false);
		mReplayVideoStatus = REPLAY_VIDEO_STATUS.INIT;
		mIsRequestVideo = true;
		int upDownDis = Utils.dip2px(getBaseContext(), 10);
		mUpDownAnim = new TranslateAnimation(0, 0, -upDownDis, upDownDis);
		mUpDownAnim.setRepeatCount(Animation.INFINITE);
		mUpDownAnim.setDuration(getResources().getInteger(
				android.R.integer.config_longAnimTime));
		mUpDownAnim.setRepeatMode(Animation.REVERSE);
		mDate = new Date();
		mDeviceControlUrl = mDevice.getDevice_id() + "@"
				+ mDevice.getSip_domain();
		mDateAllFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		mDateSimpleFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
		mDateYMDFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		long initTime = System.currentTimeMillis() / 1000 - 60 * 60;
		initTime = (long) (60 * Math.round(initTime / (float) 60));
		mPlayProgressTimeStamp = initTime;
		// Log.d("PML",
		// "init mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
		replay_historyseek.setMidTimeStamp(initTime);
		showDate(initTime);
		mRecordList = new ArrayList<Pair<Integer, Integer>>();

		mMediaPlayer = MediaPlayer.create(this, R.raw.snapshot);
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (mPowerManager != null) {
			mVideoWakeLock = mPowerManager.newWakeLock(
					PowerManager.FULL_WAKE_LOCK
							| PowerManager.ACQUIRE_CAUSES_WAKEUP,
					"com.wulian.Replay");
		}

		onSendSipRemoteAccess();
		getSipAccount();
		if (isPortrait()) {
			goPortrait();
		} else {
			goLandscape();
		}
		initOSS();
		showBaseDialog();
		sendRequest(RouteApiType.V3_TOKEN_DOWNLOAD_REPLAY,
				RouteLibraryParams.V3TokenDownloadReplay(userInfo.getAuth(),
						mDevice.getDevice_id(), mDevice.getSip_domain()), false);
	}

	private void setListeners() {
		ll_titlebar_back.setOnClickListener(this);
		btn_control_snapshot_landscape.setOnClickListener(this);
		btn_control_snapshot_portrait.setOnClickListener(this);
		btn_control_back_live_portrait.setOnClickListener(this);
		btn_control_quit_fullscreen_bar.setOnClickListener(this);
		ll_control_fullscreen_bar.setOnClickListener(this);

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// mp.release();
			}
		});

		mMediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.release();
				return false;
			}
		});
		replay_historyseek
				.setHistroySeekChangeListener(new HistroySeekChangeListener() {
					@Override
					public void onChangeSeekBarTempAction(long timeStamp) {
						mReplayVideoStatus = REPLAY_VIDEO_STATUS.SEEKBAR_PROGRESS;

						mPlayVideoHandler
								.removeMessages(REQUEST_NEXT_OBJECT_MSG);
						mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
						mPlayVideoHandler
								.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
						mPlayVideoHandler
								.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
						mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);

						showReplayProgress();
						showDate(timeStamp);
					}

					@Override
					public void onChangeSeekBarFinalAction(long timeStamp,
							boolean isRecord) {
						mPlayVideoHandler
								.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
						mPlayVideoHandler
								.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
						mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
						mPlayVideoHandler
								.removeMessages(REQUEST_NEXT_OBJECT_MSG);
						mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);

						LibraryLoger.d("PML", "onChangeSeekBarFinalAction is:"
								+ timeStamp + ";isRecord is:" + isRecord);
						// Log.d("PML", "onChangeSeekBarFinalAction is:"
						// + timeStamp + ";isRecord is:" + isRecord);
						Message msg = mPlayVideoHandler
								.obtainMessage(PLAY_BACK_VIDEO_MSG);
						msg.arg1 = isRecord ? (int) timeStamp : -1;
						mPlayVideoHandler.sendMessageDelayed(msg, 500);
					}

					@Override
					public void onActionDownMessage() {
						mPlayVideoHandler.removeMessages(PLAY_BACK_VIDEO_MSG);
					}
				});

		cb_control_record_portrait
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							cb_control_record_portrait
									.setText(R.string.common_stop);
						} else {
							cb_control_record_portrait
									.setText(R.string.common_record);
						}
					}
				});
		rl_replay_player.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				return true;
			}
		});
		mGestureDetector = new GestureDetector(ReplayVideoActivity.this,
				new OnGestureListener() {
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						return false;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						return false;
					}
				});
		mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (!mIsPortrait) {
					if (rl_video_top_landscape.getVisibility() == View.VISIBLE) {
						rl_video_top_landscape.setVisibility(View.GONE);
						rl_seekbar_layout.setVisibility(View.GONE);
						rl_control_landscape.setVisibility(View.GONE);
					} else {
						rl_video_top_landscape.setVisibility(View.VISIBLE);
						rl_seekbar_layout.setVisibility(View.VISIBLE);
						rl_control_landscape.setVisibility(View.VISIBLE);
					}
				}
				return false;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				if (mIsPortrait) {
					goLandscape();
				} else {
					goPortrait();
				}
				return false;
			}
		});
		iv_progress_video.startAnimation(mUpDownAnim);
	}

	private void goPortrait() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		rl_video_top_landscape.setVisibility(View.VISIBLE);
		rl_seekbar_layout.setVisibility(View.VISIBLE);

		ll_titlebar_back.setVisibility(View.VISIBLE);

		btn_control_quit_fullscreen_bar.setVisibility(View.GONE);
		rl_control_landscape.setVisibility(View.GONE);
		mIsPortrait = true;
		ll_control_fullscreen_bar.setVisibility(View.VISIBLE);
		tv_seekbar_date.setBackgroundColor(getResources().getColor(
				R.color.white));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				rl_seekbar_layout.getLayoutParams());
		lp.topMargin = 0;
		lp.height = Utils.dip2px(getBaseContext(), 160);
		rl_seekbar_layout.setLayoutParams(lp);
		ll_control_portrait.setVisibility(View.VISIBLE);

		LinearLayout.LayoutParams replay_lp = new LinearLayout.LayoutParams(
				rl_replay_player.getLayoutParams());
		WindowManager wm = this.getWindowManager();
		int height = (int) (wm.getDefaultDisplay().getWidth() * APPConfig.DEFAULT_WIDTH_HEIGHT_RATIO);
		replay_lp.height = height;
		rl_replay_player.setLayoutParams(replay_lp);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void goLandscape() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

		ll_titlebar_back.setVisibility(View.GONE);

		rl_video_top_landscape.setVisibility(View.GONE);
		rl_seekbar_layout.setVisibility(View.GONE);
		rl_control_landscape.setVisibility(View.GONE);

		mIsPortrait = false;
		btn_control_quit_fullscreen_bar.setVisibility(View.VISIBLE);
		tv_seekbar_date.setBackgroundColor(getResources().getColor(
				R.color.transparent));
		ll_control_fullscreen_bar.setVisibility(View.GONE);
		ll_control_portrait.setVisibility(View.GONE);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				rl_seekbar_layout.getLayoutParams());
		lp.height = Utils.dip2px(getBaseContext(), 120);
		lp.topMargin = -Utils.dip2px(getBaseContext(), 120);
		rl_seekbar_layout.setLayoutParams(lp);

		LinearLayout.LayoutParams replay_lp = new LinearLayout.LayoutParams(
				rl_replay_player.getLayoutParams());
		replay_lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
		rl_replay_player.setLayoutParams(replay_lp);
	}

	private void showReplayProgress() {
		switch (mReplayVideoStatus) {
		case INIT:
			if (mUpDownAnim.hasEnded()) {
				mUpDownAnim.startNow();
			}
			rl_progress_replay_video.setVisibility(View.VISIBLE);
			rl_replay_player.setBackgroundColor(getResources().getColor(
					R.color.transparent_deep));
			tv_progress_video_tip.setText(R.string.common_loading);
			break;
		case SEEKBAR_PROGRESS:
			if (mUpDownAnim.hasEnded()) {
				mUpDownAnim.startNow();
			}
			rl_progress_replay_video.setVisibility(View.VISIBLE);
			rl_replay_player.setBackgroundColor(getResources().getColor(
					R.color.transparent_deep));
			tv_progress_video_tip.setText(R.string.replay_moving_through_time);
			break;
		case GET_VIDEO:
			if (mUpDownAnim.hasEnded()) {
				mUpDownAnim.startNow();
			}
			rl_progress_replay_video.setVisibility(View.VISIBLE);
			rl_replay_player.setBackgroundColor(getResources().getColor(
					R.color.transparent_deep));
			tv_progress_video_tip.setText(R.string.replay_fetching_videos);
			break;
		case PLAY_VIDEO:
			if (mUpDownAnim.hasStarted()) {
				mUpDownAnim.cancel();
			}
			rl_progress_replay_video.setVisibility(View.GONE);
			rl_replay_player.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			break;
		default:
			break;
		}
	}

	private void showTopDate(long time) {
		tv_play_date.setText(mDateAllFormat.format(new Date(time * 1000)));
	}

	private void showDate(long time) {
		showTopDate(time);
		showSeekBarDate(time);
	}

	private void showSeekBarDate(long time) {
		Date date = new Date(time * 1000);
		if (isToday(date)) {
			tv_seekbar_date.setText(R.string.common_today);
		} else {
			tv_seekbar_date.setText(mDateSimpleFormat.format(date));
		}
	}

	private boolean isToday(Date date) {
		if (mDateYMDFormat.format(date).equalsIgnoreCase(
				mDateYMDFormat.format(new Date()))) {
			return true;
		}
		return false;
	}

	private void getSipAccount() {
		// 1、初始化sip
		app.initSip();
		if (!mDevice.getIs_lan() || !ICamGlobal.isPureLanModel) {// 非局域网设备||正常登陆
			// 2、用户注册账号
			mAccount = app.registerAccount();
			if (mAccount == null) {
				CustomToast.show(this,
						R.string.login_user_account_register_fail);
				ReplayVideoActivity.this.finish();
				return;
			}
		} else {// 局域网设备&&纯粹的局域网
			// account = new SipProfile();// 给个非空的实例，以兼容以前的判断逻辑
		}
	}

	private Handler mPlayVideoHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PLAY_BACK_VIDEO_MSG:
				long time = msg.arg1;
				if (time >= 0) {
					mCurrentTimeStamp = time;
					mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
					showReplayProgress();
					mIsRequestVideo = true;
					playHistoryVideo(time);
				} else {
					showMsg(R.string.replay_no_video_selected_period);
				}
				break;
			case PLAY_BACK_VIDEO_ACTIVE_MSG:
				if (replay_historyseek.getIsMidRecord()
						&& replay_historyseek.getTimeStamp() > 0) {
					mPlayProgressTimeStamp = replay_historyseek.getTimeStamp();
					LibraryLoger.d("PML",
							"mPlayProgressTimeStamp replay_historyseek is:"
									+ mPlayProgressTimeStamp);
					mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
					showSeekBarDate(mPlayProgressTimeStamp);
					showReplayProgress();
					playHistoryVideo(replay_historyseek.getTimeStamp());
				} else {
					// Log.d("PML",
					// "before getDefaultAvaiableProgressTimeStamp mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
					long tempTime = getDefaultAvaiableProgressTimeStamp(mPlayProgressTimeStamp);
					// Log.d("PML",
					// "after getDefaultAvaiableProgressTimeStamp mPlayProgressTimeStamp is:"+tempTime);
					if (tempTime > 0) {
						mPlayProgressTimeStamp = tempTime;
						LibraryLoger.d("PML", "mPlayProgressTimeStamp is:"
								+ mPlayProgressTimeStamp);
						// Log.d("PML",
						// "inner getDefaultAvaiableProgressTimeStamp mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
						replay_historyseek
								.setMidTimeStamp(mPlayProgressTimeStamp);
						mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
						showReplayProgress();
						showSeekBarDate(mPlayProgressTimeStamp);
						playHistoryVideo(mPlayProgressTimeStamp);
					}
				}
				break;
			case STREAM_HANDLE_MSG:
				Bundle bd = msg.getData();
				byte[] data = bd.getByteArray("data");
				int width = bd.getInt("width");
				int height = bd.getInt("height");
				if (view_h264video != null) {
					view_h264video.PlayVideo(data, width, height);
				}
				break;
			case SHOW_VIDEO_PROGRESS_MSG:
				showReplayProgress();
				break;
			case SHOW_VIDEO_REPLAY_DATE_MSG:
				mCurrentTimeStamp++;
				showTopDate(mCurrentTimeStamp);
				sendMessageDelayed(
						mPlayVideoHandler
								.obtainMessage(SHOW_VIDEO_REPLAY_DATE_MSG),
						1000);
				break;
			case CONTROL_SEEKBAR_PROGRESS_MSG:
				replay_historyseek.setMidTimeStamp(mCurrentTimeStamp);
				sendMessageDelayed(
						mPlayVideoHandler
								.obtainMessage(CONTROL_SEEKBAR_PROGRESS_MSG),
						5000);
				break;
			case REMOVE_PLAY_VIDEO_MSG:
				mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
				break;
			case REQUEST_NEXT_OBJECT_MSG:
				// Log.d("PML", "before getNextProgressTimeStamp is:"
				// + mPlayProgressTimeStamp);
				mPlayProgressTimeStamp = getNextProgressTimeStamp(mPlayProgressTimeStamp);
				// Log.d("PML",
				// "REQUEST_NEXT_OBJECT_MSG mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
				// Log.d("PML", "after getNextProgressTimeStamp is:"
				// + mPlayProgressTimeStamp);
				if (mPlayProgressTimeStamp > 0) {
					playHistoryVideo(mPlayProgressTimeStamp);
				} else {
					mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
					mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
					mPlayVideoHandler
							.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
					mPlayVideoHandler.sendMessageDelayed(mPlayVideoHandler
							.obtainMessage(REMOVE_PLAY_VIDEO_MSG), 20000);
				}
				break;
			case PLAY_VIDEO_MSG:
				// LibraryLoger.d("PML",
				// "PLAY_VIDEO_MSG is:" + mNextObjectData.getTimeStamp());
				if (mNextObjectData != null
						&& mNextObjectData.getTimeStamp() > 0) {
					mClient.playOSSObjectName(mNextObjectData,
							mIsFirstRequestRecord);
					if (mIsFirstRequestRecord) {
						mIsFirstRequestRecord = false;
					}
				}
				break;
			case FILE_MOUNT_EXCEPTION:
				showMsg(R.string.play_take_picture_mount_exception);
				break;
			case TAKE_PICTURE_FAIL:
				showMsg(R.string.play_take_picture_exception);
				break;
			case FILE_OK:
				showMsg(R.string.play_take_picture_ok);
				break;
			case NO_SDCARD_MSG_CALLBACK:
				InEnableReplay();
				showMsg(R.string.common_no_sdcard);
				break;
			case NO_RIGHT_TO_SEE_MSG_CALLBACK:
				InEnableReplay();
				showMsg(R.string.replay_one_people_to_see);
				break;
			case REQUEST_TIMEOUT_MSG_CALLBACK:
				InEnableReplay();
				showMsg(R.string.replay_request_timeout);
				break;
			case NO_RECORD_VIDEO_MSG_CALLBACK:
				InEnableReplay();
				showMsg(R.string.replay_no_video_in_sdcard);
				break;
			case NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG:
				mPlayVideoHandler
						.removeMessages(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG);
				notifyRecordHeartBeat();
				mPlayVideoHandler.sendMessageDelayed(mPlayVideoHandler
						.obtainMessage(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG),
						10000);
				break;
			default:
				break;
			}
		};
	};

	private void InEnableReplay() {
		dismissBaseDialog();
		if (mUpDownAnim != null && mUpDownAnim.hasStarted()) {
			mUpDownAnim.cancel();
		}
		replay_historyseek.setActionEnable(false);
		replay_historyseek.setRecordList(mRecordList);
		ll_control_fullscreen_bar.setEnabled(false);
		tv_seekbar_date.setText("");
		tv_play_date.setText("");
		btn_control_snapshot_portrait.setEnabled(false);
		cb_control_record_portrait.setEnabled(false);
	}

	private void EnableReplay() {
		replay_historyseek.setActionEnable(true);
		ll_control_fullscreen_bar.setEnabled(true);
		btn_control_snapshot_portrait.setEnabled(true);
		cb_control_record_portrait.setEnabled(true);
	}

	private long getDefaultAvaiableProgressTimeStamp(long time) {
		long result = -1;
		int size = mRecordList.size();
		// Log.d("PML",
		// "mRecordList size is:"+mRecordList.size()+";time is:"+time);
		Pair<Integer, Integer> pair = null;
		for (int i = size - 1; i >= 0; i--) {
			pair = mRecordList.get(i);
			// Log.d("PML",
			// "mRecordList i:"+i+"; pair is:left="+pair.first+";second="+pair.second);
			if (pair.second > time && pair.first >= time) {
				result = pair.first;
				// Log.d("PML", "AAAAAAAA:result is:"+result);
			}
			if (pair.second < time) {
				if (result == -1) {
					if (pair.second - pair.first >= 60 * 60) {
						// Log.d("PML", "GBBBBBBBBBBBBBB");
						return pair.second - 60 * 60;
					} else {
						// Log.d("PML", "CCCCCCCCCCCCCCCC");
						return pair.first;
					}
				} else {
					// Log.d("PML", "DDDDDDDDDDDDDDDDDDDDDD");
					return result;
				}
			}
		}
		return result;
	}

	private long getNextProgressTimeStamp(long initTime) {
		int size = mRecordList.size();
		for (int i = 0; i < size; i++) {
			Pair<Integer, Integer> pair = mRecordList.get(i);
			if (pair.first <= initTime && pair.second >= initTime) {
				if (initTime + 60 <= pair.second) {
					return initTime + 60;
				} else {
					if (i < size - 1) {
						return mRecordList.get(i + 1).first;
					} else {
						return -1;
					}
				}
			}
		}
		return -1;
	}

	private void playHistoryVideo(long time) {
		String sip_ok = "sip:" + mDeviceControlUrl;
		mPlayProgressTimeStamp = time;
		// Log.d("PML",
		// "playHistoryVideo mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
		// LibraryLoger.d("PML", "playHistoryVideo  mPlayProgressTimeStamp is:"
		// + mPlayProgressTimeStamp);
		// Log.d("PML", "playHistoryVideo  mPlayProgressTimeStamp is:"
		// + mPlayProgressTimeStamp);
		SipController.getInstance().sendMessage(
				mDeviceControlUrl,
				SipHandler.ControlHistoryRecordProgress(sip_ok, mSeq++,
						mSessionID, time), mAccount);
	}

	ConnectDataCallBack mListener = new ConnectDataCallBack() {
		@Override
		public void onH264StreamMessage(byte[] data, int width, int height) {
			if (mPlayVideoHandler != null) {
				Bundle bd = new Bundle();
				bd.putByteArray("data", data);
				bd.putInt("width", width);
				bd.putInt("height", height);
				Message msg = new Message();
				msg.what = STREAM_HANDLE_MSG;
				msg.setData(bd);
				mPlayVideoHandler.sendMessage(msg);
			}
		}

		public void onRequestObjectEndFlag() {

		}

		@Override
		public void onError(Exception error) {
		}

		@Override
		public void onDisconnect(int code, String reason) {
		}

		@Override
		public void onRequestGetObjectResultOK(long timestamp) {
			// LibraryLoger.d("PML", "onRequestGetObjectResultOK timestamp is:"
			// + timestamp + ";mPlayProgressTimeStamp is:"
			// + mPlayProgressTimeStamp);
			// Log.d("PML", "onRequestGetObjectResultOK timestamp is:" +
			// timestamp
			// + ";mPlayProgressTimeStamp is:" + mPlayProgressTimeStamp);
			if (timestamp == mPlayProgressTimeStamp
					&& mReplayVideoStatus == REPLAY_VIDEO_STATUS.GET_VIDEO) {
				mReplayVideoStatus = REPLAY_VIDEO_STATUS.PLAY_VIDEO;
				mCurrentTimeStamp = timestamp;
				mPlayVideoHandler.sendMessage(mPlayVideoHandler
						.obtainMessage(SHOW_VIDEO_PROGRESS_MSG));
			}

			mCurrentTimeStamp = timestamp;
			mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
			mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
			mPlayVideoHandler.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
			mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
			mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);

			mPlayVideoHandler
					.sendMessageDelayed(mPlayVideoHandler
							.obtainMessage(SHOW_VIDEO_REPLAY_DATE_MSG), 1000);
			mPlayVideoHandler.sendMessage(mPlayVideoHandler
					.obtainMessage(CONTROL_SEEKBAR_PROGRESS_MSG));
			mPlayVideoHandler.sendMessageDelayed(
					mPlayVideoHandler.obtainMessage(REQUEST_NEXT_OBJECT_MSG),
					40000);
			mPlayVideoHandler.sendMessageDelayed(
					mPlayVideoHandler.obtainMessage(PLAY_VIDEO_MSG), 60000);
		}
	};

	private void initOSS() {
		// 初始化OSS
		mClient = new WulianOssClient(mListener, getApplicationContext());
		mClient.initConfigData();
		// if (ICamGlobal.CURRENT_VERSION != ICamGlobal.STABLE_VERSION) {
		// mClient.enableLog();
		// } else {
		// mClient.disableLog();
		// }
		mClient.disableLog();
	}

	private void destroyOSS() {
		if (mClient != null) {
			mClient.disconnect();
		}
	}

	private void connectOSS() {
		// 连接
		mClient.connect();
		mClient.setIsReverse(mIsVideoInvert);
	}

	private void queryHistoryVideo() {
		mQueryHistory = true;
		mHasQueryData = false;
		String sip_ok = "sip:" + mDeviceControlUrl;
		SipController.getInstance().sendMessage(mDeviceControlUrl,
				SipHandler.QueryHistoryRecord(sip_ok, mSeq++), mAccount);
		mPlayVideoHandler.sendEmptyMessageDelayed(NO_RECORD_VIDEO_MSG_CALLBACK,
				10000);
	}

	private void controlStartRecord() {
		String sip_ok = "sip:" + mDeviceControlUrl;
		SipController.getInstance().sendMessage(
				mDeviceControlUrl,
				SipHandler.ControlStartRecord(sip_ok, mSeq++,
						LibraryPhoneStateUtil.getImsi(getBaseContext())),
				mAccount);
		// Log.d("PML", "controlStartRecord");
		mPlayVideoHandler
				.sendEmptyMessageDelayed(NO_SDCARD_MSG_CALLBACK, 10000);
	}

	private void controlStopRecord() {
		// Log.d("PML", "controlStopRecord");

		mPlayVideoHandler.removeMessages(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG);
		mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
		mPlayVideoHandler.removeMessages(NO_RIGHT_TO_SEE_MSG_CALLBACK);
		mPlayVideoHandler.removeMessages(NO_RECORD_VIDEO_MSG_CALLBACK);
		mPlayVideoHandler.removeMessages(NO_SDCARD_MSG_CALLBACK);
		mPlayVideoHandler.removeMessages(FILE_OK);
		mPlayVideoHandler.removeMessages(TAKE_PICTURE_FAIL);
		mPlayVideoHandler.removeMessages(FILE_MOUNT_EXCEPTION);
		mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
		mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
		mPlayVideoHandler.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
		mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
		mPlayVideoHandler.removeMessages(SHOW_VIDEO_PROGRESS_MSG);
		mPlayVideoHandler.removeMessages(PLAY_BACK_VIDEO_ACTIVE_MSG);
		mPlayVideoHandler.removeMessages(PLAY_BACK_VIDEO_MSG);
		mPlayVideoHandler.removeMessages(STREAM_HANDLE_MSG);
		if (!TextUtils.isEmpty(mSessionID)) {
			String sip_ok = "sip:" + mDeviceControlUrl;
			SipController.getInstance().sendMessage(mDeviceControlUrl,
					SipHandler.ControlStopRecord(sip_ok, mSeq++, mSessionID),
					mAccount);
		}
	}

	private void notifyRecordHeartBeat() {
		// Log.d("PML", "notifyRecordHeartBeat");
		if (!TextUtils.isEmpty(mSessionID)) {
			String sip_ok = "sip:" + mDeviceControlUrl;
			SipController.getInstance().sendMessage(
					mDeviceControlUrl,
					SipHandler.NotifyHistoryRecordHeartbeat(sip_ok, mSeq++,
							mSessionID), mAccount);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_titlebar_back) {
			finish();
		} else if (id == R.id.btn_control_snapshot_landscape
				|| id == R.id.btn_control_snapshot_portrait) {
			if (view_h264video.getBitmap() == null) {
				mPlayVideoHandler.sendEmptyMessage(TAKE_PICTURE_FAIL);
			} else {
				if (mMediaPlayer != null) {
					try {
						mMediaPlayer.stop();
						mMediaPlayer
								.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
						mMediaPlayer.prepare();
						mMediaPlayer.setVolume(0.1f, 0.1f);
						mMediaPlayer.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String storageState = Environment.getExternalStorageState();
				String savePath = "";
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + APPConfig.ALBUM_DIR;
					File dir = new File(savePath);
					if (!dir.exists()) {
						dir.mkdirs();
					}
				}
				// 没有挂载SD卡，无法下载文件
				if (TextUtils.isEmpty(savePath)) {
					mPlayVideoHandler.sendEmptyMessage(FILE_MOUNT_EXCEPTION);
					return;
				}
				String fileName = FileUtils.getfilename();
				String devicePath = savePath + mDevice.getDevice_id() + "/";
				{
					File dir = new File(devicePath);
					if (!dir.exists()) {
						dir.mkdirs();
					}
				}
				String snapSavePath = devicePath + fileName + ".jpg";
				LibraryLoger.d("PML", "snapSavePath is:" + snapSavePath);
				view_h264video.setTakePicture(snapSavePath,
						new TakePictureCallBack() {
							@Override
							public void TakePicture(boolean isFileOk, Bitmap bmp) {
								if (isFileOk) {
									Bundle bundle = new Bundle();
									bundle.putParcelable(
											ViERenderer.GET_PICTURE, bmp);
									Message msg = new Message();
									msg.what = FILE_OK;
									msg.setData(bundle);
									mPlayVideoHandler.sendMessage(msg);
								} else {
									mPlayVideoHandler
											.sendEmptyMessage(TAKE_PICTURE_FAIL);
								}
							}
						});
			}
		} else if (id == R.id.btn_control_back_live_portrait) {
//			Intent it = new Intent(ReplayVideoActivity.this,
//					PlayVideoActivity.class);
//			it.putExtra("device", mDevice);
//			startActivity(it);
			this.finish();
		} else if (id == R.id.btn_control_quit_fullscreen_bar
				|| id == R.id.ll_control_fullscreen_bar) {
			if (isPortrait()) {
				goLandscape();
			} else {
				goPortrait();
			}
		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isLandscape()) {
				goPortrait();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean isLandscape() {
		return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public boolean isPortrait() {
		return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (isLandscape()) {
			// land
			if (mIsPortrait) {
				goLandscape();
			}
		} else if (isPortrait()) {
			// port
			if (!mIsPortrait) {
				goPortrait();
			}
		}
	}

	@Override
	protected void DataReturn(boolean success, RouteApiType apiType, String json) {
		super.DataReturn(success, apiType, json);
		if (success) {
			switch (apiType) {
			case V3_TOKEN_DOWNLOAD_REPLAY:
				try {
					JSONObject jsonObject = new JSONObject(json);
					JSONObject jsonDataObject = jsonObject
							.getJSONObject("data");
					String Region = "";
					String Bucket = "";
					String RequestId = "";
					String AccessKeySecret = "";
					String AccessKeyId = "";
					String SecurityToken = "";
					if (!jsonDataObject.isNull("Region")) {
						Region = jsonDataObject.getString("Region");
						Region += APPConfig.DEFAULT_ALIYUN_STS_OSS_URL_SUFFIX;
					}
					if (!jsonDataObject.isNull("Bucket")) {
						Bucket = jsonDataObject.getString("Bucket");
					}
					if (!jsonDataObject.isNull("RequestId")) {
						RequestId = jsonDataObject.getString("RequestId");
					}
					if (!jsonDataObject.isNull("AccessKeySecret")) {
						AccessKeySecret = jsonDataObject
								.getString("AccessKeySecret");
					}
					if (!jsonDataObject.isNull("AccessKeyId")) {
						AccessKeyId = jsonDataObject.getString("AccessKeyId");
					}
					if (!jsonDataObject.isNull("SecurityToken")) {
						SecurityToken = jsonDataObject
								.getString("SecurityToken");
					}
					if (TextUtils.isEmpty(Region) || TextUtils.isEmpty(Bucket)
							|| TextUtils.isEmpty(AccessKeySecret)
							|| TextUtils.isEmpty(AccessKeyId)
							|| TextUtils.isEmpty(SecurityToken)) {
						dismissBaseDialog();
						Toast.makeText(getBaseContext(),
								R.string.common_none_account, Toast.LENGTH_LONG)
								.show();
					} else {
						FederationToken stsToken = new FederationToken();
						stsToken.setRequestId(RequestId);
						stsToken.setAccessKeySecret(AccessKeySecret);
						stsToken.setAccessKeyId(AccessKeyId);
						stsToken.setSecurityToken(SecurityToken);
						stsToken.setExpiration(60 * 60 * 2);
						mClient.setFederationToken(stsToken, Region, Bucket);
						controlStartRecord();
					}
				} catch (JSONException e) {

				}
				break;
			default:
				break;
			}
		} else {
			switch (apiType) {
			case V3_TOKEN_DOWNLOAD_REPLAY:
				InEnableReplay();
				showMsg(R.string.error_no_network);
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
			String xmlData, String from, String to) {
		super.SipDataReturn(isSuccess, apiType, xmlData, from, to);
		if (isSuccess) {
			switch (apiType) {
			case QUERY_HISTORY_RECORD:
				if (!mHasQueryData) {
					mPlayVideoHandler
							.removeMessages(NO_RECORD_VIDEO_MSG_CALLBACK);
					EnableReplay();
					if (mQueryHistory) {
						mRecordList.clear();
					}
					mQueryHistory = false;
					List<VideoTimePeriod> mTimeList = XMLHandler
							.getHistoryRecordList(xmlData);
					for (VideoTimePeriod videoTimePeriod : mTimeList) {
						Pair<Integer, Integer> pair = new Pair<Integer, Integer>(
								(int) videoTimePeriod.getTimeStamp(),
								(int) videoTimePeriod.getEndTimeStamp());
						mRecordList.add(pair);
					}
					if (XMLHandler.parseXMLDataJudgeEnd(xmlData)) {
						mHasQueryData = true;
						dismissBaseDialog();
						if (mRecordList.size() != 0) {
							if (mRecordList.size() == 1
									&& mRecordList.get(0).first == 0
									&& mRecordList.get(0).second == 0) {
								// Log.d("PML",
								// "QUERY_HISTORY_RECORD 0-------------0");
								mPlayVideoHandler
										.sendEmptyMessage(NO_RECORD_VIDEO_MSG_CALLBACK);
							} else {
								sortTime(mRecordList);
								replay_historyseek.setRecordList(mRecordList);
								// Log.d("PML",
								// "QUERY_HISTORY_RECORD sortTime(mRecordList)");
								mPlayVideoHandler
										.sendMessageDelayed(
												mPlayVideoHandler
														.obtainMessage(PLAY_BACK_VIDEO_ACTIVE_MSG),
												200);
							}
						} else {
							// Log.d("PML", "QUERY_HISTORY_RECORD else");
							mPlayVideoHandler
									.sendEmptyMessage(NO_RECORD_VIDEO_MSG_CALLBACK);
						}
					}
				}
				break;
			case CONTROL_START_RECORD:
				mPlayVideoHandler.removeMessages(NO_SDCARD_MSG_CALLBACK);
				EnableReplay();
				try {
					// Log.d("PML",
					// "CONTROL_START_RECORD OK  xmlData IS:"+xmlData);
					String status = XMLHandler.parseXMLDataGetStatus(xmlData);
					String session = XMLHandler
							.parseXMLDataGetSessionID(xmlData);
					if (session == null) {
						// Log.d("PML",
						// "CONTROL_START_RECORD OK  status == null || session == null");
						InEnableReplay();
						CustomToast.show(this, R.string.main_process_failed);
					} else {
						if(status == null || status.equals("") || status.equalsIgnoreCase("OK")) {
							// Log.d("PML",
							// "CONTROL_START_RECORD OK mSessionID is:"+session);
							mSessionID = session;
							connectOSS();
							queryHistoryVideo();
							mPlayVideoHandler
									.sendEmptyMessage(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG);
						} else if (status.equalsIgnoreCase("404")) {
							// Log.d("PML", "CONTROL_START_RECORD 404 500");
							mPlayVideoHandler
									.removeMessages(NO_SDCARD_MSG_CALLBACK);
							mPlayVideoHandler
									.sendEmptyMessage(NO_SDCARD_MSG_CALLBACK);
						} else if (status.equalsIgnoreCase("-1")
								|| status.equalsIgnoreCase("500")) {
							InEnableReplay();
							CustomToast
									.show(this, R.string.main_process_failed);
						} else if (status.equalsIgnoreCase("551")) {
							// Log.d("PML", "CONTROL_START_RECORD 551");
							mPlayVideoHandler
									.removeMessages(NO_RIGHT_TO_SEE_MSG_CALLBACK);
							mPlayVideoHandler
									.sendEmptyMessage(NO_RIGHT_TO_SEE_MSG_CALLBACK);
						} else {
							// Log.d("PML", "CONTROL_START_RECORD else");
							InEnableReplay();
							CustomToast
									.show(this, R.string.main_process_failed);
						}
					}
				} catch (Exception e) {
					// Log.d("PML", "Exception e");
					mPlayVideoHandler.sendEmptyMessage(NO_SDCARD_MSG_CALLBACK);
				}
				break;
			case CONTROL_STOP_RECORD:
				break;
			case CONTROL_HISTORY_RECORD_PROGRESS:
				// Log.d("PML", "CONTROL_HISTORY_RECORD_PROGRESS is:" +
				// xmlData);
				String fileName = XMLHandler.parseXMLDataGetFilename(xmlData);
				// Log.d("PML",
				// "CONTROL_HISTORY_RECORD_PROGRESS fileName is:"+fileName);
				if (!fileName.equalsIgnoreCase("OK")) {
					if (fileName.equalsIgnoreCase("403")) {
						InEnableReplay();
						CustomToast.show(this, R.string.exception_2021);
					} else if (fileName.equalsIgnoreCase("404")
							|| fileName.equalsIgnoreCase("403")
							|| fileName.equalsIgnoreCase("500")
							|| fileName.equalsIgnoreCase("1102")) {
						InEnableReplay();
						CustomToast.show(this, R.string.main_process_failed);
					} else if (fileName.equalsIgnoreCase("551")) {
						mPlayVideoHandler
								.removeMessages(NO_RIGHT_TO_SEE_MSG_CALLBACK);
						mPlayVideoHandler
								.sendEmptyMessage(NO_RIGHT_TO_SEE_MSG_CALLBACK);
					} else if (fileName.length() > 10) {
						GetObjectDataModel tempModel = OSSXMLHandler
								.getObjectData(xmlData, mDevice.getDevice_id());
						if (tempModel != null && tempModel.getFileSize() > 0) {
							mNextObjectData = tempModel;
							// LibraryLoger.d("PML",
							// "CONTROL_HISTORY_RECORD_PROGRESS is:"
							// + fileName);
							// Log.d("PML",
							// "CONTROL_HISTORY_RECORD_PROGRESS is:"
							// + fileName);

							if (mIsRequestVideo) {
								mPlayVideoHandler.sendMessage(mPlayVideoHandler
										.obtainMessage(PLAY_VIDEO_MSG));
								mIsRequestVideo = false;
							}
						} else {
							InEnableReplay();
							CustomToast
									.show(this, R.string.main_process_failed);
						}
					}

				}
				break;
			case NOTIFY_HISTORY_RECORD_HEARTBEAT:
				String status = XMLHandler.parseXMLDataGetStatus(xmlData);
				// Log.d("PML",
				// "parseXMLDataGetStatus NOTIFY_HISTORY_RECORD_HEARTBEAT status is:"+status);
				if (!TextUtils.isEmpty(status)) {
					if (status.equalsIgnoreCase("404")) {
						mPlayVideoHandler
								.removeMessages(NO_SDCARD_MSG_CALLBACK);
						mPlayVideoHandler
								.sendEmptyMessage(NO_SDCARD_MSG_CALLBACK);
					}
				}
				break;
			default:
				break;
			}
		} else {
			// Log.d("PML", "apiType wrong :apitype is:"+apiType.name());
			switch (apiType) {
			case QUERY_HISTORY_RECORD:
				if (!mHasQueryData) {
					mPlayVideoHandler
							.removeMessages(REQUEST_TIMEOUT_MSG_CALLBACK);
					mPlayVideoHandler
							.sendEmptyMessage(REQUEST_TIMEOUT_MSG_CALLBACK);
				}
				break;
			case CONTROL_START_RECORD:
				mPlayVideoHandler.removeMessages(REQUEST_TIMEOUT_MSG_CALLBACK);
				mPlayVideoHandler
						.sendEmptyMessage(REQUEST_TIMEOUT_MSG_CALLBACK);
				break;
			case CONTROL_STOP_RECORD:
				dismissBaseDialog();
				CustomToast.show(this, R.string.main_process_failed);
				break;
			case CONTROL_HISTORY_RECORD_PROGRESS:
				dismissBaseDialog();
				CustomToast.show(this, R.string.main_process_failed);
				break;
			case NOTIFY_HISTORY_RECORD_HEARTBEAT:
				break;
			default:
				break;
			}
		}
	}

	public void sortTime(List<Pair<Integer, Integer>> needSortList) {
		Comparator<Pair<Integer, Integer>> itemComparator = new Comparator<Pair<Integer, Integer>>() {
			public int compare(Pair<Integer, Integer> left,
					Pair<Integer, Integer> right) {
				return left.first > right.first ? 1 : -1;
			}
		};
		Collections.sort(needSortList, itemComparator);
	}

	static {
		try {
			System.loadLibrary("openh264");
			System.loadLibrary("WulianICamOpenH264");
		} catch (Exception e) {

		}
	}
}
