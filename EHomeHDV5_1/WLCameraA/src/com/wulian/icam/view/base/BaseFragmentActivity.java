/**
 * Project Name:  FamilyRoute
 * File Name:     BaseActivity.java
 * Package Name:  com.wulian.familyroute.view.base
 * Date:          2014-9-5
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.UserInfo;
import com.wulian.icam.utils.ProgressDialogManager;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.routelibrary.common.ErrorCode;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;
import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 
 * @ClassName: BaseFragmentActivity
 * @Function: 带有基本功能的基类
 * @date: 2014-9-9
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class BaseFragmentActivity extends FragmentActivity implements
		TaskResultListener {
	ProgressDialog progressDialog;// 单个请求时使用,一般由父类管理
	ProgressDialog baseProgressDialog;// 多个请求连续调用时使用,一般由子类管理，第一个请求show，最后一个请求dismiss。
	protected EditText url_et;
	private Toast toast;
	private View contentView, baseContentView;
	private boolean sipRemoteAccessFlag = false;// 远程访问标识
	protected UserInfo userInfo;// 父类维护的一个用户信息引用，重新登录后，需要更新这个引用
	public ICamGlobal app;// 子类可覆盖，单例无所谓。
	private ImageView bt_title_back;// 返回
	private TextView tv_titlebar_title;// 标题
	private ImageView bt_right;// 右键
	protected ProgressDialogManager mDialogManager = ProgressDialogManager
			.getDialogManager();

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		if (ICamGlobal.APPFLAG <= 0) {
			Utils.sysoInfo("APPFLAG <= 0,非常规路线,内存被释放");
			// startActivity(new Intent(getApplicationContext(),
			// StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			// | Intent.FLAG_ACTIVITY_CLEAR_TASK));
			// this.finish();
			// return;
		}
		if (toast == null) {
			toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		}
		app = ICamGlobal.getInstance();
		userInfo = ICamGlobal.getInstance().getUserinfo();// 重新登陆后，要更新引用
		setViewContent();
		initBaseView();
		initBaseData();
		setBaseListener();
		registerBaseBroadCast();
	}

	protected void registerBaseBroadCast() {
		IntentFilter filterScreen = new IntentFilter();
		filterScreen.addAction(Intent.ACTION_SCREEN_OFF);
		filterScreen.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mScreenReceiver, filterScreen);

	}

	protected void unRegisterBaseBroadCast() {
		if (mScreenReceiver != null) {
			unregisterReceiver(mScreenReceiver);
		}
	}

	protected void setViewContent() {

	}

	private void initBaseView() {
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.titlebar_title);
		bt_right = (ImageView) findViewById(R.id.titlebar_right);
	}

	private void initBaseData() {
		if (tv_titlebar_title != null && getActivityTitle() != null) {
			tv_titlebar_title.setText(getActivityTitle());
		}
		if (bt_right != null) {
			if (getRightResource() != -1) {
				bt_right.setBackgroundResource(getRightResource());
			}
		}

	}

	private void setBaseListener() {
		if (bt_right != null) {
			if (getRightClick() != null) {
				bt_right.setOnClickListener(getRightClick());
			}
		}
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
	}

	protected void setActivityTitle(String value) {
		if (tv_titlebar_title != null && value != null) {
			tv_titlebar_title.setText(value);
		}
	}

	protected String getActivityTitle() {
		return null;
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				operatorSomeData();
				BaseFragmentActivity.this.finish();
			}
		};
	}

	protected OnClickListener getRightClick() {
		return null;
	}

	protected int getRightResource() {
		return -1;
	}

	protected void operatorSomeData() {
	}

	@Override
	public void onSaveInstanceState(Bundle outState,
			PersistableBundle outPersistentState) {
		// 解决Fragment不随activity的生命周期结束而结束
		// super.onSaveInstanceState(outState, outPersistentState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Utils.sysoInfo(this + " >>>base onresume app:" + app);
		if (app == null || userInfo == null) {// 每次都检查，即使kill也没事。
			app = ICamGlobal.getInstance();
			userInfo = app.getUserinfo();
			Utils.sysoInfo("app == null || userInfo == null base onresume app:"
					+ app);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// registerBaseBroadCast();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// unRegisterBaseBroadCast();//锁屏后，unRegister了，监测不到了。。。
		// 避免多个BaseFragmentActivity显示密码窗口=>singleTop解决
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
		dismissBaseDialog();// 处理java.lang.IllegalArgumentException: View not
							// attached to window manager异常，持续跟踪
		onStopSipRemoteAccess();
		unRegisterBaseBroadCast();
	}

	public void showMsg(String msg) {
		// toast.setText(msg);
		// toast.show();
		CustomToast.show(this, msg);// 说明：本App中，几乎所有地方都是用的该方法，为了确保统一。
	}

	public void showMsg(int res) {
		// toast.setText(res);
		// toast.show();
		CustomToast.show(this, res);
	}

	/**
	 * @Function 远程控制请求，必须调用该方法注册广播接收器
	 * @author Wangjj
	 * @date 2015年1月5日
	 */
	protected void onSendSipRemoteAccess() {
		if (sipRemoteAccessFlag == false) {
			registerReceiver(MessageCallStateReceiver, new IntentFilter(
					SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED()));//"com.wulian.siplibrary.icam.service.MESSAGE_RECEIVED"
			sipRemoteAccessFlag = true;
		}
	}

	protected void onStopSipRemoteAccess() {
		if (sipRemoteAccessFlag) {
			unregisterReceiver(MessageCallStateReceiver);
			sipRemoteAccessFlag = false;
		}
	}

	private BroadcastReceiver MessageCallStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("PML", "BroadcastReceiver callStateReceiver");
			if (action
					.equals(SipManager.GET_ACTION_SIP_MESSAGE_RECEIVED())) {//"com.wulian.siplibrary.icam.service.MESSAGE_RECEIVED"
				SipMessage sm = (SipMessage) intent
						.getParcelableExtra("SipMessage");
				if (sm != null) {
					SipMsgApiType apiType = SipHandler.parseXMLData(sm
							.getBody());
					if (sm.getType() == SipMessage.MESSAGE_TYPE_SENT) {
						if (!sm.getContact().equalsIgnoreCase("200")) {
							SipDataReturn(false, apiType, sm.getBody(),
									sm.getFrom(), sm.getTo());
						} else if (apiType == SipMsgApiType.NOTIFY_WEB_ACCOUNT_INFO) {
							SipDataReturn(false, apiType, sm.getBody(),
									sm.getFrom(), sm.getTo());
						}
					} else if (sm.getType() == SipMessage.MESSAGE_TYPE_INBOX) {
						SipDataReturn(true, apiType, sm.getBody(),
								sm.getFrom(), sm.getTo());
					}
				}
				// Log.d("MainActivity", sm.getBody());
			}
		}
	};

	protected void showBaseDialog() {
		if (baseProgressDialog == null || baseContentView == null) {
			baseProgressDialog = new ProgressDialog(this, R.style.dialog);
			baseProgressDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// Utils.sysoInfo("baseDialogDissmissed");
					baseProgressDialogDissmissed();
				}
			});
			baseContentView = getLayoutInflater().inflate(
					R.layout.custom_progress_dialog,
					(ViewGroup) findViewById(R.id.custom_progressdialog));
			((TextView) baseContentView.findViewById(R.id.tv_desc))
					.setText(getResources().getText(R.string.common_in_processing));

		}
		if (!baseProgressDialog.isShowing()) {
			baseProgressDialog.show();
			baseProgressDialog.setContentView(baseContentView);
		}
	}

	protected void progressDialogDissmissed() {

	}

	protected void baseProgressDialogDissmissed() {

	}

	protected void dismissBaseDialog() {
		if (baseProgressDialog != null && baseProgressDialog.isShowing()) {
			baseProgressDialog.dismiss();
		}
	}

	protected void showProgressDialog() {
		if (progressDialog == null || contentView == null) {
			progressDialog = new ProgressDialog(this, R.style.dialog);
			progressDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// Utils.sysoInfo("baseDialogDissmissed");
					progressDialogDissmissed();

				}
			});
			contentView = getLayoutInflater().inflate(
					R.layout.custom_progress_dialog,
					(ViewGroup) findViewById(R.id.custom_progressdialog));
			((TextView) contentView.findViewById(R.id.tv_desc))
					.setText(getResources().getText(R.string.common_in_processing));

		}
		if (!isFinishing() && !progressDialog.isShowing()) {
			progressDialog.show();
			progressDialog.setContentView(contentView);
		}
	}

	protected void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 
	 * @MethodName sendRequest
	 * @Function 发送网络请求
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 * @param type
	 *            类型
	 * @param params
	 *            请求参数
	 * @param showprogressDialog
	 *            是否显示对话框
	 */
	protected void sendRequest(RouteApiType type,
			HashMap<String, String> params, boolean showprogressDialog) {
		if (showprogressDialog) {
			showProgressDialog();
		}
	// 非Sips请求方式 都是HTTP请求方式
		// add　syf
			 RouteLibraryController.getInstance().doRequest(
					this, type, params, this);
	}

	/**
	 * 
	 * @MethodName sendRequest
	 * @Function 发送网络请求
	 * @author Puml
	 * @date: 2014-9-9
	 * @email puml@wuliangroup.cn
	 * @param type
	 *            类型
	 * @param params
	 *            请求参数
	 * @param showprogressDialog
	 *            是否显示对话框
	 */
	protected void sendRequest(RouteApiType type,
			HashMap<String, String> params, boolean showprogressDialog,
			boolean isShowReturn) {
		if (showprogressDialog) {
			showProgressDialog();
		}
		// add　syf
		 RouteLibraryController.getInstance().doRequest(
				this, type, params, isShowReturn ? this : null);
	}

	@Override
	public void OnSuccess(RouteApiType apiType, String json) {
		Utils.sysoInfo("base onsuccess " + apiType);

		if (url_et != null && json != null) {
			url_et.setText(json);
		}
		try {
			JSONObject dataJson = new JSONObject(json);

			if (!dataJson.isNull("URL") && url_et != null) {
				// url_et.setText(dataJson.getString("URL"));
			}
			int status = dataJson.optInt("status");
			if (status == 1) {
				DataReturn(true, apiType, json);// 第3个参数为需要的json数据
			} else {
				// ErrorCodeForUser errorCodeForUser = ErrorCodeForUser
				// .getErrorCodeForUser(apiType,
				// dataJson.optInt("error_code"));
				// if (dataJson.optInt("error_code") == 1126) {// 挤下线
				// SharedPreferences sp = this.getSharedPreferences(
				// APPConfig.SP_CONFIG, Context.MODE_PRIVATE);
				// Editor editor = sp.edit();
				// editor.putBoolean(APPConfig.IS_LOGIN_OUT, true);
				// editor.commit();
				// startActivity(new Intent(this, LoginActivity.class)
				// .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				// | Intent.FLAG_ACTIVITY_CLEAR_TASK));
				// CustomToast.show(this, R.string.account_offline);
				// return;
				// }

				// 404则检查强制升级
				if (dataJson.optInt("error_code") == 404) {
					Utils.sysoInfo("可能需要强制升级!");
					ICamGlobal.isForceUpdate = true;
				}
				try {
					DataReturn(false, apiType,
							handleErrorInfo(ErrorCode.getTypeByCode(dataJson
									.optInt("error_code"))));
				} catch (Exception e) {
					DataReturn(false, apiType,
							handleErrorInfo(ErrorCode.UNKNOWN_ERROR));
				}
			}
		} catch (JSONException e) {
			DataReturn(false, apiType, "");// 第3个参数为服务器返回的非json格式数据
		}
	}

	@Override
	public void OnFail(RouteApiType apiType, ErrorCode code) {
		Utils.sysoInfo("base onfail " + apiType);
		// 如果失败，直接将错误消息返回给子类，父类统一处理，不要交付给子类,这里的失败一般是网络没连接
		// ErrorCodeForUser errorCodeForUser = ErrorCodeForUser
		// .getErrorCodeForUser(apiType, code.getErrorCode());
		// if ("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
		// DataReturn(false, apiType, errorCodeForUser.getDescription());//
		// 第3个参数为用户友好错误提示，这里一般为无网络
		// } else {
		// DataReturn(false, apiType, errorCodeForUser.getDescription_en());
		// }
		DataReturn(false, apiType, handleErrorInfo(code));
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		switch (code) {
		case INVALID_PHONE:

			break;
		default:
			break;
		}
	}

	private String handleErrorInfo(ErrorCode code) {
		String result = "";
		Resources rs = getResources();
		switch (code) {
		case NO_INTERNET:
		case TIMEOUT_ERROR:
		case NETWORK_ERROR:
			result = rs.getString(R.string.error_no_network);
			break;
		case NO_WIFI:
			result = rs.getString(R.string.config_connect_wifi_first);
			break;
		case AIRPLANE_MODE:
			result = rs.getString(R.string.exception_flight_mode);
			break;
		case NOSDCARD:
			result = rs.getString(R.string.error_no_sdcard);
			break;
		case FILE_EXIST:
			result = rs.getString(R.string.exception_file_exist);
			break;
		case INVALID_REQUEST:
		case INVALID_IO:
			result = rs.getString(R.string.socket_invalid_io);
			break;
		case UNKNOWN_EXCEPTION:
		case UNKNOWN_ERROR:
			result = rs.getString(R.string.error_unknown);
			break;
		case TOKEN_EXPIRED:
			result = rs.getString(R.string.exception_1001);
			break;
		case INVALIDSTRLENGTH:
			result = rs.getString(R.string.exception_1002);
			break;
		case INVALID_DEVICE_BIND:
			result = rs.getString(R.string.exception_1006);
			break;
		case LIMIT_EXCEEDED:
			result = rs.getString(R.string.exception_1010);
			break;
		case PARAM_MISSING:
			result = rs.getString(R.string.exception_1100);
			break;
		case INVALID_MODEL:
			result = rs.getString(R.string.exception_1102);
			break;
		case INVALID_SOURCE:
			result = rs.getString(R.string.exception_1103);
			break;
		case INVALID_TYPE:
			result = rs.getString(R.string.exception_1104);
			break;
		case INVALID_EMAIL:
			result = rs.getString(R.string.exception_1105);
			break;
		case INVALID_DEVICE_ID:
			result = rs.getString(R.string.exception_1106);
			break;
		case INVALID_URL:
			result = rs.getString(R.string.exception_1108);
			break;
		case INVALID_BINDER_USERNAME:
			result = rs.getString(R.string.exception_1108);
			break;
		case INVALID_USER:
		case INVALID_APPSECRET:
		case INVALID_PHONE:
		case INVALID_CODE:
			result = rs.getString(R.string.exception_1111);
			break;
		case INVALID_LOGIN_AUTH:
			result = rs.getString(R.string.exception_1126);
			break;
		case NO_ROWS_AFFECTED:
			result = rs.getString(R.string.exception_2020);
			break;
		case UNAUTHORIZED_DEVICE:
			result = rs.getString(R.string.exception_2021);
			break;
		default:
			result = rs.getString(R.string.error_unknown);
			break;
		}
		return result;
	}

	/**
	 * 
	 * @Function 数据返回
	 * @author Wangjj
	 * @date 2014年11月8日
	 * @param success
	 * @param apiType
	 * @param json
	 *            success则为：成功的json数据 ;fail则为：服务器返回的非json格式数据 或 对用户友好错误提示
	 *            。由子类处理。
	 */
	protected void DataReturn(boolean success, RouteApiType apiType, String json) {
		// 如果是静默方式查询版本，则不处理对话框，避免影响其他请求的对话框显示
		// if ((apiType == RouteApiType.VERSION_DEV || apiType ==
		// RouteApiType.VERSION_STABLE)
		// && ICamApplication.isSilentUpdate) {
		// WulianLog.d("PML", "DataReturn is:" + apiType.name());
		// } else {
		// WulianLog.d("PML", "DataReturn dismiss");
		// dismissProgressDialog();
		// }
		Utils.sysoInfo("base DataReturn result=" + success);
		dismissProgressDialog();
		if (success) {
			switch (apiType) {
			case V3_LOGIN:// 重新登录成功，获取了新的授权码
				Utils.sysoInfo("重新登录成功，获取了新的授权码");
				try {// 加入本地超时计算起始时间
					JSONObject jsonObject = new JSONObject(json);
					jsonObject.put("localExpireStart",
							(int) (System.currentTimeMillis() / 1000));
					json = jsonObject.toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 重新保存全局用户信息,新的userinfo内存地址
				getSharedPreferences(APPConfig.SP_CONFIG, MODE_PRIVATE).edit()
						.putString(APPConfig.ACCOUNT_USERINFO, json).commit();// 缓存用户信息
				Utils.saveUserInfo(json);// 保存全局用户信息
				userInfo = app.getUserinfo();// 深bug: 基类同步更新，否则会 循环调用
				break;

			default:
				break;
			}

		} else {
			if (ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION) {
				CustomToast.show(this, this.getClass().getName() + ":"
						+ apiType.getmURL() + ":" + json);// 父类统一弹出错误提示，子类可以进一步处理
			} else {
				CustomToast.show(this, json);// 父类统一弹出错误提示，子类可以进一步处理
			}
		}
	}

	protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
			String xmlData, String from, String to) {
		// 如果是静默方式查询版本，则不处理对话框，避免影响其他请求的对话框显示
		// if (apiType == SipMsgApiType.QUERY_FIREWARE_VERSION
		// && ICamApplication.isSilentUpdate) {
		//
		// } else {
		// dismissProgressDialog();
		// }

		dismissProgressDialog();
		if (!isSuccess) {
			// CustomToast.show(this, "设备离线,sip消息发送失败");
		}
	}

	/**
	 * 
	 * @Function 重新登录提取到父类公用
	 * @author Wangjj
	 * @date 2014年11月24日
	 */
	public void reLogin() {
		SharedPreferences sp = getSharedPreferences(APPConfig.SP_CONFIG,
				MODE_PRIVATE);
		String phonenum = sp.getString(APPConfig.ACCOUNT_NAME, "");
		String password = sp.getString(APPConfig.PASSWORD, "");
		password = Utils.decrypt(password, APPConfig.ENCRYPT_KEY);
		sendRequest(RouteApiType.V3_LOGIN,
				RouteLibraryParams.V3Login(phonenum, password), false);
	}

	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			//DO nothing
		}
	};


}
