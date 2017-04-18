package cc.wulian.smarthomev5.fragment.setting.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.PermissionEntity;
import cc.wulian.smarthomev5.event.PermissionEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import de.greenrobot.event.EventBus;

public class PermissionItem {

	private Context mContext;
	protected LayoutInflater inflater;
	protected Resources mResources;
	protected LinearLayout lineLayout;
	private AccountManager mAccountManager = AccountManager.getAccountManger();
	protected ProgressDialogManager mDialogManager = ProgressDialogManager.getDialogManager();
	private static final String PERMISSION_KEY = "permission_key";
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	private TextView permissionName;
	private TextView permissionStatus;
	private LinearLayout permissionLayout;
	private TextView permissionReject;
	private TextView permissionAccept;
	private EditText editAdress;
	private EditText editPhone;
	private TextView mErrorView;
	
	public PermissionItem(final Context context,final PermissionEntity entity){
		mContext = context;
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		
		lineLayout = (LinearLayout)inflater.inflate(R.layout.setting_control_permission_item, null);
		permissionName = (TextView) lineLayout.findViewById(R.id.setting_contro_permission_usename);
		permissionStatus = (TextView) lineLayout.findViewById(R.id.setting_contro_permission_status);
		permissionLayout = (LinearLayout) lineLayout.findViewById(R.id.setting_control_permission_layout);
		permissionReject = (TextView) lineLayout.findViewById(R.id.setting_contro_permission_reject);
		permissionAccept = (TextView) lineLayout.findViewById(R.id.setting_contro_permission_accept);
		
		permissionName.setText(entity.getUserName());
		if(entity != null){
			if(StringUtil.equals(entity.getStatus(), "0")){
				permissionLayout.setVisibility(View.GONE);
				permissionStatus.setText(mContext.getResources().getString(R.string.set_account_manager_permission_binding_status));
				
			}else{
				permissionLayout.setVisibility(View.VISIBLE);
				permissionStatus.setText(mContext.getResources().getString(R.string.set_account_manager_permission_please_status));
				permissionAccept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
//						sendChangePermissionRequest(entity, "0");
						createAdressPhoneDialog(entity);
					}
				});
				permissionReject.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						sendChangePermissionRequest(entity, "1");
					}
				});
			}
		}
	}
	
	public View getView(PermissionEntity entity) {
		return lineLayout;
	}
	
	
	private void sendChangePermissionRequest(final PermissionEntity entity,final String status){
		mDialogManager.showDialog(PERMISSION_KEY, mContext, null, null);
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject jsonObject = new JSONObject();

					jsonObject.put("gwID", mAccountManager.getmCurrentInfo().getGwID());
//					jsonObject.put("gwID", "E04C0D8DC54B");
					jsonObject.put("userID", entity.getUserID());
					jsonObject.put("status", status);
					if(!StringUtil.isNullOrEmpty(entity.getAddress())){
						jsonObject.put("address", entity.getAddress());
					}
					if(!StringUtil.isNullOrEmpty(entity.getPhone())){
						jsonObject.put("phone", entity.getPhone());
					}

					JSONObject json = HttpUtil.postWulianCloudOrigin(
							WulianCloudURLManager.getResponsePermissionInfoURL(), jsonObject);

					if (json != null) {
						Logger.debug("json" + json);
						JSONObject obj = json.getJSONObject("header");
						String backResult = obj.getString("retCode");
						if (StringUtil.equals(backResult, "SUCCESS")) {
							if(StringUtil.equals(status, "0")){
								entity.setStatus("0");
								EventBus.getDefault().post(new PermissionEvent(PermissionEvent.ACCEPT,entity));
							}else if(StringUtil.equals(status, "1")){
								entity.setStatus("1");
								EventBus.getDefault().post(new PermissionEvent(PermissionEvent.REJECT,entity));
							}
							dissDialog();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void dissDialog(){
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mDialogManager.dimissDialog(PERMISSION_KEY, 0);
			}
		});
	}
	
	// 接受授权弹出完善地址电话的popuwindows
	private void createAdressPhoneDialog(final PermissionEntity entity) {
		// popupwindow背景
		View popupView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		final PopupWindow popupWindowBg = createPopuWindowBackground(popupView);
		// popupwindow上的dialog
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.gateway_timeZone_setting_permission_manager_data));
		View createPermissionView = inflater.inflate(
				R.layout.setting_permission_manager_phone_adress, null);
		editAdress = (EditText) createPermissionView.findViewById(R.id.setting_permission_manager_edit_adress);
		editPhone = (EditText) createPermissionView.findViewById(R.id.setting_permission_manager_edit_phoneNumber);
		builder.setContentView(createPermissionView)
				.setNegativeButton(mContext.getResources().getString(R.string.cancel))
				.setPositiveButton(mContext.getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(false)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						if(confirmPwd()){
							entity.setAddress(editAdress.getText().toString());
							entity.setPhone(editPhone.getText().toString());
							sendChangePermissionRequest(entity, "0");
						}
					}
					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		WLDialog dialog = builder.create();
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				popupWindowBg.dismiss();
			}
		});
		dialog.show();
	}
	
	//密码是否为空
	private boolean confirmPwd() {
		if (editAdress == null
				|| StringUtil.isNullOrEmpty(editAdress.getText().toString())) {
			mErrorView = editAdress;
			mErrorView.requestFocus();
			mErrorView.setError(mContext.getResources().getString(
					R.string.hint_not_null_edittext));
			return false;
		} else if (editPhone == null
				|| StringUtil.isNullOrEmpty(editPhone.getText().toString())) {
			mErrorView = editPhone;
			mErrorView.requestFocus();
			mErrorView.setError(mContext.getResources().getString(
					R.string.hint_not_null_edittext));
			return false;
		} else {
			return true;
		}
	}

	private PopupWindow createPopuWindowBackground(View contentView) {
		PopupWindow popupWindow = new PopupWindow(contentView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.showAtLocation(lineLayout, Gravity.CENTER, 0, 0);
		return popupWindow;
	}
}
