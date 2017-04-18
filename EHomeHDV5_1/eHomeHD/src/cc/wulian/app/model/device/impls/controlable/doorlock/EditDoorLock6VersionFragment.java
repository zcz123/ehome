package cc.wulian.app.model.device.impls.controlable.doorlock;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.wulian.iot.Config;
import com.wulian.iot.bean.CameraEagleUpdateInfo;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.connect.Connect;
import com.wulian.iot.connect.ConnectEagleManage;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.EagleUtil;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.device.play.PlayEagleActivity;
import com.wulian.iot.widght.DialogManager;
import com.yuantuo.netsdk.TKCamHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import de.greenrobot.event.EventBus;

public class EditDoorLock6VersionFragment extends WulianFragment implements
		OnClickListener {
	private static final String TAG = "EditDoorLock6VersionFragment";
	private static final int VERSION_SUCCESS = 0;
	private Context mContext;

	private Dialog mDiglog;

	private SharedPreferences sharedPreferences;// iot 通用sharedPreferences
												// 请勿在子类中随便添加
	private Editor editor;
	private String CameraUid = null;;
	private String CameraPassword = "admin";

	private int version = -1;
	private final String url = "http://otacdn.wulian.cc/yingyan_zh.xml";

	private int mVersionCode;// 保存服务端获取固件版本号
	private String mLocaVersionName = null; // 保存本地获取的name
	private String mVersionName = null;// 保存服务器获取的name

	private CameraHelper cHelper = null;
	private TKCamHelper mCamera = null;

	@ViewInject(R.id.device_new_door_lock_setting_alarm_defense_btn)
	private RelativeLayout mVersioRelativeLayout;

	@ViewInject(R.id.version_string_string)
	private TextView mVersioText;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VERSION_SUCCESS:
				Log.i(TAG,"网络获取 解析成功；VERSION_SUCCESS");
				handler.postDelayed(startAhannel, 5000);
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// initEditDevice();
		mContext = (Context) mActivity;
		getVersionCode();
		initiBar();
		initData();
		initDiglog();

	}

	private void startPlaySurfaceView() {

		if (cHelper == null) {
			cHelper = CameraHelper.getInstance(new IOTCDevChPojo(
					CameraUid, CameraPassword, Config.EagleConnMode,
					Config.Hawkeye));
			cHelper.attach(cameraCallback);
			cHelper.registerstIOTCLiener();
			cHelper.attach(observer);
			cHelper.register();
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View contentView = inflater
				.inflate(R.layout.device_new_door_lock_setting_version,
						container, false);
		ViewUtils.inject(this, contentView);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	private void initiBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.set_titel);
		getSupportActionBar().setTitle(R.string.camera_firmware_version);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
	}

	private void initData() {
		// TODO Auto-generated method stub

		sharedPreferences = mContext.getSharedPreferences(
				Config.COMMON_SHARED_HAWKEYE, mContext.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Intent it = getActivity().getIntent();
		CameraUid = it.getStringExtra(Config.tutkUid);
		String gwID = it.getStringExtra(EditDoorLock6Fragment.GWID);
		String devID = it.getStringExtra(EditDoorLock6Fragment.DEVICEID);
		NetSDK.sendControlDevMsg(gwID, devID, "14", "89", "25");// 发送上电命令
		Log.i(TAG, CameraUid + "   length:" + CameraUid.length());
	}

	@Override
	public void onResume() {
		super.onResume();
		int version = sharedPreferences.getInt(Config.VERSION_EAGLE, -1);
		if (version != -1) {
			mLocaVersionName = EagleUtil.interceptionString(version).trim();
			mVersioText.setText(mLocaVersionName);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyWailThread();
		new Thread(disPlayRunnable).start();

	}

	private Runnable startAhannel = new Runnable() {
		@Override
		public void run() {
			startPlaySurfaceView();
		}
	};
	private Runnable disPlayRunnable = new Runnable() {
		@Override
		public void run() {
			if (cHelper != null) {
				cHelper.detach(cameraCallback);
				cHelper.detach(observer);
				cHelper.destroyCameraHelper();
			}
			cHelper = null;
		}
	};
	private void destroyWailThread() {
		if (createSessionWaitThread != null) {
			createSessionWaitThread.stopThread();
			createSessionWaitThread = null;
		}
		if (createAvChannelWaitThread != null) {
			createAvChannelWaitThread.stopThread();
			createAvChannelWaitThread = null;
		}
	}

	@Override
	public void onClick(View arg0) {

	}
	private CameraHelper.Observer observer=new CameraHelper.Observer() {
		@Override
		public void avIOCtrlOnLine() {

		}

		@Override
		public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						switch (avIOCtrlMsgType){
							case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:
								version=IotUtil.parseEagleInfo(data).getVersion();// 解析固件版本数据
//								getSpEagleInfo();
								if (version != -1) {// 1 00 00 00
									mLocaVersionName = EagleUtil.interceptionString(version);
									mVersioText.setText(mLocaVersionName);
								}
								startUpdate();
								Log.i("IOTCamera", "data.lenth:" + data.length);
								dismissDialog();
								break;
						}
					}
				});
		}

		@Override
		public void avIOCtrlMsg(int resCode, String method) {

		}
	};
	/** 等待 加载 diglog */
	protected void initDiglog() {
		DialogManager manager = new DialogManager(mContext);
		// 自定义实现部分
		View view = manager.getView(DialogManager.iot_camera);
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.dialog_view);// 加载布局
		ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
		spaceshipImage.setAnimation(manager
				.getAnimation(DialogManager.animation));
		tipTextView.setText("");
		// 自定义实现部分
		if (layout != null) {
			manager.setCancelable(true);
			mDiglog = manager.getDialog(DialogManager.iot_dialog_style, layout);
			showDialog();
		}
	}

	protected void showDialog() {
		if (mDiglog != null) {
			mDiglog.show();
		}
	}

	protected void dismissDialog() {
		if (mDiglog != null) {
			mDiglog.dismiss();
			mDiglog = null;
		}
	}

	/**
	 * 得到设备服务端固件信息
	 * 
	 * @return 固件版本号
	 */
	private void getVersionCode() {
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				Connect mConnect = new Connect();
				mConnect.parseXMLInfo(url, new ConnectEagleManage() {
					@Override
					public void success(CameraEagleUpdateInfo mCameraEagleUpdateInfo) {
						super.success(mCameraEagleUpdateInfo);
						if (mCameraEagleUpdateInfo != null) {
							mVersionName = mCameraEagleUpdateInfo.getVersionName();
							handler.sendEmptyMessage(VERSION_SUCCESS);
							Log.i(TAG,"解析已发哦！");
						}
					}

					@Override
					public String error(String msg) {
						return error("错误");
					}
				});
			}
		});

	}

	private void startUpdate() {
		if (mVersionName != null && mLocaVersionName != null) {
			if (mLocaVersionName.compareTo(mVersionName) < 0) {
				// 提示升级
				showUpdataDialog();
			} else {
				Toast.makeText(mContext, R.string.desktop_setting_has_latest,
						Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(mContext, R.string.desktop_setting_device_connected_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void showUpdataDialog() {
		AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);
		mAlertDialog.setTitle(R.string.set_new_version_update_hint);
		mAlertDialog.setMessage(R.string.smartLock_update_prompt);
		mAlertDialog.setPositiveButton(R.string.common_ok,
				new DialogInterface.OnClickListener() { // 设置确定按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (cHelper == null) {
							handler.post(startAhannel);
							return;
						}
						// 执行升级
						IotSendOrder.sendEagleUpdata(mCamera);
						dialog.dismiss(); // 关闭dialog
					}
				});
		mAlertDialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() { // 设置取消按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		// 参数都设置完成了，创建并显示出来
		mAlertDialog.create().show();
	}

	private CameraHelper.IOTCDevConnCallback cameraCallback = new CameraHelper.IOTCDevConnCallback() {

		@Override
		public void success() {
			Log.i("IOTCamera", "===Successfully===");
			mCamera = cHelper.getmCamera();
			// 发送获取版本号的命令
			IotSendOrder.findEagleVerByIoc(mCamera);
		}

		@Override
		public void session() {
			Log.i(TAG, "===session===");
			createSessionWaitThread = new EditDoorLock6VersionFragment.CreateSessionWaitThread();
			createSessionWaitThread.start();
		}

		@Override
		public void avChannel() {
			createAvChannelWaitThread =new CreateAvChannelWaitThread();
			createAvChannelWaitThread.start();
		}
	};

	private EditDoorLock6VersionFragment.CreateSessionWaitThread createSessionWaitThread = null;

	private class CreateSessionWaitThread extends Thread {
		private boolean mIsRunning = true;

		public void stopThread() {
			mIsRunning = false;
		}

		@Override
		public void run() {
			mIsRunning = true;
			while (mIsRunning) {
				if (cHelper.checkSession()) {
					cHelper.register();
					mIsRunning = false;
				}
			}
		}
	}

	private EditDoorLock6VersionFragment.CreateAvChannelWaitThread createAvChannelWaitThread = null;

	private class CreateAvChannelWaitThread extends Thread {
		private boolean mIsRunning = true;

		public void stopThread() {
			mIsRunning = false;
		}

		@Override
		public void run() {
			mIsRunning = true;
			while (mIsRunning) {
				if (cHelper.checkAvChannel()) {
					cHelper.register();
					mIsRunning = false;
				}
			}
		}
	}
}
