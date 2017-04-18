/**
 * Project Name:  FamilyRoute
 * File Name:     FamilyRouteApplication.java
 * Package Name:  com.wulian.familyroute
 * Date:          2014-9-5
 * Copyright (c)  2014, wulian All Rights Reserved.  Test By Yanmin
 */

package com.wulian.icam;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.BindingOauthAccountModel;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.UserInfo;
import com.wulian.icam.utils.Utils;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.manage.SipProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ICamGlobal 要点1、变量初始化，避免空指针异常 要点2、内存可能被释放，检查数据有效性 appflag
 * 要点3、登陆获取用户信息，MainActivity中重新初始化sip和account
 * 
 * @author Administrator email puml@wuliangroup.cn
 */
public class ICamGlobal {// extends Application
	private UserInfo userinfo = new UserInfo();// static 延期？初始化，避免空指针异常
	private ArrayList<Device> deviceList = new ArrayList<Device>();// 初始化，避免空指针异常
	private List<BindingOauthAccountModel> bindedAccount = new ArrayList<BindingOauthAccountModel>();// 已经绑定的授权用户列表引用

	public static boolean isPureLanModel = false;// 纯粹的局域网模式
	public static boolean isSipCreated = false;
	public static boolean isForceUpdate = false;
	public static boolean isAccountRegister = false;
	public static boolean isUnAccountRegister = false;
	public static boolean isRegisterAccountForce = false;
	public static boolean isRefreshAvatar = false;

	public static boolean isNeedRefreshDeviceList = false;// 是否需要重新刷新设备列表
	public static boolean isNeedRefreshDeviceListLocal = false;// 是否需要重新刷新设备列表,不发请求
	public static boolean isNeedRefreshSnap = false;// 是否需要重新刷新截图
	public static boolean isSilentUpdate = false;// 是否需静默更新,静默更新只在有更新时才会弹窗提醒，错误或最新均不提醒
	public static Handler killAppHandler;// 缓存 killApp的消息
	private String AppPath = "/iCam";// ICam导致我的iCam无法创建(2者互斥，先者存)
	// private String DOWNLOADPATH = "/download";
	public static boolean isItemClickProcessing = false;// 播放item被点击
	public static long time = System.currentTimeMillis();// app的销毁是彻底销毁

	private static String userSipAccount;// 用户sip账号
	private static String userSipPwd;// 用户sip密码,即登录密码。用户修改密码后，要重新注册服务器。
	private static SipProfile account;// 用户账号信息,app范围只有一个
	private static ICamGlobal mInstance;
	private static Handler pjSipThreadExecutor = null;
	private static Thread pjThread = new Thread(new Runnable() {

		@Override
		public void run() {
			Looper.prepare();
			pjSipThreadExecutor = new Handler();
			while (true) {
				try {
					Looper.loop();
				} catch (Exception e) {
					Log.e("SIP", "", e);
				}
			}
		}
	}, "pjWorkThread");

	public static final int DEVING_VERSION = 1;// 开发中版->内部测试版
	// public static int DEV_VERSION = 2;// 公开测试版
	public static int STABLE_VERSION = 3;// 公开稳定版
	public static int CURRENT_VERSION = DEVING_VERSION;// DEVING_VERSION;
	private int NatNum;

	public static int APPFLAG = 0;
	private String httpsPath;
	private Context mAppContext;
	private SharedPreferences sp;
	public static boolean forV5 = false;

	public static ICamGlobal getInstance() {
		if (mInstance == null) {
			ICamGlobal app = new ICamGlobal();
			mInstance = app;
		}
		return mInstance;
	}

	public void initForV5(Context context) {
		mAppContext = context;
		if (!pjThread.isAlive()) {
			pjThread.start();
		}
		while (pjSipThreadExecutor == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		forV5 = true;
		sp = mAppContext.getSharedPreferences(APPConfig.SP_CONFIG, Context.MODE_PRIVATE);
		APPConfig.Init(false);
		setServerPath(false);
	}

	/**
	 * 建立用户数据文件夹
	 */
	private void initPath() {
		String ROOT;
		// 判断SD卡是否插入
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			ROOT = "/data/data";
		}

		AppPath = ROOT + AppPath;
		// DOWNLOADPATH = AppPath + DOWNLOADPATH;

		File rootPath = new File(AppPath);
		if (!rootPath.exists()) {
			rootPath.mkdirs();
		}
		// File downloadPath = new File(DOWNLOADPATH);
		// if (!downloadPath.exists()) {
		// downloadPath.mkdirs();
		// }
	}

	public String getRootPath() {
		return AppPath;
	}

	// public static Context getAppContext() {
	// if (null == mInstance) {
	// throw new RuntimeException(
	// "Please AndroidManifest. XML configuration MyApplication");
	// }
	// return mInstance;
	// }

	public void setNatNum(int num) {
		this.NatNum = num;
	}

	public int getNatNum() {

		return this.NatNum;
	}

	/**
	 * 
	 * @MethodName setServerPath
	 * @Function 设置服务器地址
	 * @author Puml
	 * @date: 2014年10月8日
	 * @email puml@wuliangroup.cn
	 */
	public void setServerPath(boolean bTestServer) {
		try {
			ApplicationInfo appInfo = mAppContext.getPackageManager().getApplicationInfo(mAppContext.getPackageName(),
					PackageManager.GET_META_DATA);
			// String httpsPath;
			String appName = appInfo.metaData.getString("appName");
			if (bTestServer)
				httpsPath = appInfo.metaData.getString("httpsPathTest");
			else
				httpsPath = appInfo.metaData.getString("httpsPath");
			RouteLibraryController.setLibraryPath(httpsPath);
		} catch (NameNotFoundException e) {
			// do nothing
		}
	}

	public String getServerPath() {
		return httpsPath;
	}

	public UserInfo getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(UserInfo userinfo) {
		if (userinfo == null)// 初次安装,userinfo为null,空指针异常
			return;
		this.userinfo = userinfo;
		userSipAccount = userinfo.getSuid();// 可能为null
		Utils.sysoInfo("用户已经变为" + userSipAccount);
		SharedPreferences sp = mAppContext.getSharedPreferences(APPConfig.SP_CONFIG, Context.MODE_PRIVATE);// 确保该参数已经保存了
		String password = sp.getString(APPConfig.PASSWORD, "");
		userSipPwd = Utils.decrypt(password, APPConfig.ENCRYPT_KEY);

	}

	public ArrayList<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(ArrayList<Device> deviceList) {
		this.deviceList = deviceList;
	}

	public List<BindingOauthAccountModel> getBindedAccount() {
		return bindedAccount;
	}

	public void setBindedAccount(List<BindingOauthAccountModel> bindedAccount) {
		this.bindedAccount = bindedAccount;
	}

	/**
	 * @Function 建立sip通道，app范围内，只会初始化一次
	 * @author Wangjj
	 * @date 2014年12月5日
	 */

	public void initSip(final boolean isLan) {
		if (!isSipCreated) {
			pjSipThreadExecutor.post(new Runnable() {

				@Override
				public void run() {
					if (!isSipCreated) {
						long start = System.currentTimeMillis();
						Utils.sysoInfo("init initSip  runable start " + Thread.currentThread().getName() + ":"
								+ Thread.currentThread().getId());
						isSipCreated = SipController.getInstance().CreateSip(mAppContext, isLan);
						Utils.sysoInfo("init initSip end " + isSipCreated + " " + (System.currentTimeMillis() - start));
					} else {
						Utils.sysoInfo("sip already runable inited");
					}

				}
			});
		} else {
			Utils.sysoInfo("sip already inited");
		}

	}

	public void initSip() {
		if (!isSipCreated) {
			pjSipThreadExecutor.post(new Runnable() {

				@Override
				public void run() {
					if (!isSipCreated) {
						long start = System.currentTimeMillis();
						Utils.sysoInfo("init initSip  runable start " + Thread.currentThread().getName() + ":"
								+ Thread.currentThread().getId());
						isSipCreated = SipController.getInstance().CreateSip(mAppContext, false);
						Utils.sysoInfo("init initSip end " + isSipCreated + " " + (System.currentTimeMillis() - start));
					} else {
						Utils.sysoInfo("sip already runable inited");
					}

				}
			});
		} else {
			Utils.sysoInfo("sip already inited");
		}

	}

	public void destorySip() {
		pjSipThreadExecutor.post(new Runnable() {

			@Override
			public void run() {
				// if (isSipCreated) {
				long start = System.currentTimeMillis();
				Utils.sysoInfo("init destorySip start " + Thread.currentThread().getName() + ":"
						+ Thread.currentThread().getId());
				SipController.getInstance().DestroySip();// 必须同一线程?
				Utils.sysoInfo("init destorySip end " + (System.currentTimeMillis() - start));
				isSipCreated = false;
				// }

			}
		});
	}

	public void makeCall(final String remoteFrom, final SipProfile profile) {
		pjSipThreadExecutor.post(new Runnable() {
			@Override
			public void run() {
				if (isSipCreated) {
					SipController.getInstance().makeCall(remoteFrom, profile);
				}
			}
		});
	}

	public void makeLocalCall(final String remoteIP, final String password, final String sipAccountCallUrl) {
		pjSipThreadExecutor.post(new Runnable() {
			@Override
			public void run() {
				if (isSipCreated) {
					SipController.getInstance().makeLocalCall(remoteIP, password, sipAccountCallUrl);
				}
			}
		});
	}

	public void hangupAllCall() {
		pjSipThreadExecutor.post(new Runnable() {
			@Override
			public void run() {
				if (isSipCreated) {
					SipController.getInstance().hangupAllCall();
				}
			}
		});
	}

	public void registerLocalAccount() {
		if (account == null) {
			Utils.sysoInfo("account==null 开始注册账户");
			pjSipThreadExecutor.post(new Runnable() {
				@Override
				public void run() {
					if (!isSipCreated) {// 崩溃的时候满足
						mHandler.sendEmptyMessage(3);
						return;
					}
					SipController.getInstance().registerLocalAccount();
				}
			});
		}
	}

	/**
	 * @Function 用户账号信息，app范围内只有一个
	 * @author Wangjj
	 * @date 2014年12月5日
	 * @return
	 */
	public SipProfile registerAccount() {
		if (account == null) {
			Utils.sysoInfo("account==null 开始注册账户");
			pjSipThreadExecutor.post(new Runnable() {

				@Override
				public void run() {
					if (!isSipCreated) {// 崩溃的时候满足
						mHandler.sendEmptyMessage(2);
						return;
					}
					Utils.sysoInfo("account:" + account + ",userSipAccount:" + userSipAccount);
					if (account == null && userSipAccount != null && userSipAccount.length() > 0) {// 需要创建&&可以创建
						long start = System.currentTimeMillis();
						Utils.sysoInfo("init registerAccount start " + userSipAccount);

						account = SipController.getInstance().registerAccount(
								userSipAccount,
								TextUtils.isEmpty(userinfo.getPrefixdom()) ? userinfo.getSdomain() : (userinfo
										.getPrefixdom()),
								userSipPwd,
								TextUtils.isEmpty(userinfo.getPrefixdom()) ? userinfo.getSdomain() : (userinfo
										.getPrefixdom()));// 报错信息不一定就是病根，调试可以追溯根源
						isAccountRegister = true;
						Utils.sysoInfo("registerAccount");
						Utils.sysoInfo("registerAccount current app:" + ICamGlobal.this);
						Utils.sysoInfo("init registerAccount end " + (System.currentTimeMillis() - start));
					} else {
						Utils.sysoInfo("account has registered");
					}

				}
			});
		}
		return account;
	}

	public SipProfile registerAccountForce() {// 子线程执行，避免影响当前wifi配置的ui线程
		if (!isRegisterAccountForce) {
			pjSipThreadExecutor.post(new Runnable() {
				@Override
				public void run() {
					isRegisterAccountForce = true;
					try {
						registerAccountForce();
					} catch (Exception e) {
						Log.e("SIP", "", e);
					} finally {
						isRegisterAccountForce = false;
					}
				}

				private void registerAccountForce() {
					if (!isSipCreated) {// 崩溃的时候满足
						mHandler.sendEmptyMessage(2);
						isRegisterAccountForce = false;
						return;
					}
					if (userSipAccount == null || userSipAccount.length() == 0 || userSipPwd == null || userSipPwd.length() == 0) {
						Log.w("SIP", "Should login at first.");
						return;
					}
					account = SipController.getInstance().registerAccount(
							userSipAccount,
							TextUtils.isEmpty(userinfo.getPrefixdom()) ? userinfo.getSdomain() : (userinfo
									.getPrefixdom()),
							userSipPwd,
							TextUtils.isEmpty(userinfo.getPrefixdom()) ? userinfo.getSdomain() : (userinfo
									.getPrefixdom()));// 报错信息不一定就是病根，调试可以追溯根源
					isAccountRegister = true;
					Utils.sysoInfo("registerAccountForce");
					Utils.sysoInfo("registerAccountForce current app:" + ICamGlobal.this);
				}
			});
		}
		return account;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int statusCode = SipController.getInstance().getAccountInfo(account);
				mHandler.sendEmptyMessageDelayed(1, 5000);
				break;
			case 2:
				// CustomToast.show(getApplicationContext(),
				// R.string.sip_init_waiting);
				initSip();
				break;
			case 3: {
				boolean isLan = true;
				Bundle bundle = msg.getData();
				if (bundle != null) {
					if (bundle.containsKey("isLan")) {
						isLan = bundle.getBoolean("isLan");
					}
				}
				initSip(isLan);
				break;
			}
			default:
				break;
			}

		};
	};

	public void unRegisterAccount() {
		if (account != null && !isUnAccountRegister) {
			pjSipThreadExecutor.post(new Runnable() {
				@Override
				public void run() {
					if (account != null && !isUnAccountRegister) {
						Utils.sysoInfo("注销账户:" + account);
						isUnAccountRegister = true;
						boolean isUnRegister = SipController.getInstance().unregistenerAccount(account);
						account = null;
						Utils.sysoInfo("注销账户结果:" + isUnRegister);
						isUnAccountRegister = false;
						// SipController.getInstance().DestroySip();
						// isSipCreated = false;//注销账号也不用销毁sip通道
					} else {
						Utils.sysoInfo("无需注销,空账户:" + account);
					}
				}
			});
		} else {
			Utils.sysoInfo("无需注销,空账户:" + account);
		}
	}

	public void clearUserAndSipInfo() {
		hangupAllCall();
		unRegisterAccount();
		destorySip();
		userinfo = null;
		userSipAccount = null;
		userSipPwd = null;
	}
}
