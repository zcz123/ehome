package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.List;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.HaiXin.JoinNetworkActivity;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceConfigJoinGWV2Activity;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceGuideJoinGWFailActivity;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceGuideJoinGWLowActivity;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceGuideJoinGWSuccessActivity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;

import com.yuantuo.customview.ui.CustomProgressDialog;
import com.yuantuo.customview.ui.CustomProgressDialog.OnDialogDismissListener;
import com.yuantuo.customview.ui.ScreenSize;

public class DeviceActionBarManager {
	private Context mContext;
	private static DeviceActionBarManager instance;
	private MoreMenuPopupWindow moreMenuManager;
	public static final String KEY_JOIN_GW_DIALOG = "KEY_JOIN_GW_DIALOG";

	public DeviceActionBarManager(Context context) {
		mContext = context;
		moreMenuManager = new MoreMenuPopupWindow(context);
	}

	public void setContext(Context context) {
		mContext = context;
	}

	public Context getContext() {
		return mContext;
	}

	public MoreMenuPopupWindow getMoreMenuPopupWindowManager() {
		return moreMenuManager;
	}

	public void show(View view) {
		// add by yanzy:不允许被授权用户使用添加新设备
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_ADD)) {
			return;
		}
		SmarthomeFeatureImpl.setData("BINDING_WIFI_SUC","0");
		Intent intent = new Intent();
		intent.setClass(mContext, DeviceConfigJoinGWV2Activity.class);
		mContext.startActivity(intent);
		moreMenuManager.dismiss();
	}

	public List<MenuItem> getCommonDeviceMenuItems() {
		// 添加新设备item
		List<MenuItem> mCommonDeviceMenuItems = new ArrayList<MenuItem>();
		MenuItem addNewDevice = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				iconImageView.setImageResource(R.drawable.device_add_new);
				titleTextView.setText(mContext
						.getString(R.string.device_common_new_hint));
				titleTextView.setGravity(Gravity.CENTER);

			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用添加新设备
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_ADD)) {
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mContext, DeviceConfigJoinGWV2Activity.class);
				mContext.startActivity(intent);
				moreMenuManager.dismiss();

			}

		};
		mCommonDeviceMenuItems.add(addNewDevice);
		return mCommonDeviceMenuItems;

	}

	
//	private void getRunningActivityName(){          
//        ActivityManager activityManager=(ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE); 
//        Logger.debug("activityManager" + activityManager.getRunningTasks(100).toString());
//        
//	}
	private OnDialogDismissListener dialogListener = new OnDialogDismissListener() {

		@Override
		public void onDismiss(CustomProgressDialog progressDialog, int result) {
			if (result == -1) {
				jumpToJoinGWFailActivity();
			} else {
				jumpToJoinGWSuccessActivity();
			}

		}
	};

	public void jumpToJoinGWSuccessActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, DeviceGuideJoinGWSuccessActivity.class);
		mContext.startActivity(intent);

	}

	public void jumpToJoinGWFailActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, DeviceGuideJoinGWFailActivity.class);
		mContext.startActivity(intent);

	}

	public void jumpToJoinGWLowActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, DeviceGuideJoinGWLowActivity.class);
		mContext.startActivity(intent);

	}
	//跳转至海信设备加网模式
	public void jumpToJoinAddHisenseDevice(){
		Intent intent=new Intent();
		intent.setClass(mContext,JoinNetworkActivity.class);
		mContext.startActivity(intent);
	}

}
