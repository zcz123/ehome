package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.view.WLEditText;

public class GateWayPasswordItem extends AbstractSettingItem {

	private Resources resources;
	private WLDialog dialog;
	private String newPasswordForWlCamera;

	public GateWayPasswordItem(Context context) {
		super(context, R.drawable.icon_change_password, context.getResources()
				.getString(R.string.set_account_manager_modify_gw_password));
		resources = context.getResources();
	}

	@Override
	public void doSomethingAboutSystem() {
		// add by yanzy:不允许被授权用户使用
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_MOD_PASSWORD)) {
			return;
		}

		showChangeGatewayPasswordDialog();
	}

	/**
	 * <h1>显示修改网关密码的对话框</h1>
	 */
	private void showChangeGatewayPasswordDialog() {
		WLDialog.Builder builder = new Builder(mContext);

		builder.setContentView(R.layout.gateway_password_change_dialog)
				.setTitle(R.string.account_manager_modify_gateway_password_title)
				.setPositiveButton(
						R.string.common_ok)
				.setNegativeButton(
						R.string.cancel)
				.setDismissAfterDone(false).setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						WLEditText oldPasswordEditTextView = (WLEditText) contentViewLayout
								.findViewById(R.id.original_password_et);
						WLEditText newPasswordEditTextView = (WLEditText) contentViewLayout
								.findViewById(R.id.new_password_et);
						WLEditText confirmNewPasswordEditTextView = (WLEditText) contentViewLayout
								.findViewById(R.id.confirm_password_et);
						modifyGateWayPassword(oldPasswordEditTextView.getText()
								.toString().trim(), newPasswordEditTextView
								.getText().toString().trim(),
								confirmNewPasswordEditTextView.getText()
										.toString().trim());
					}

					public void onClickNegative(View contentViewLayout) {

					}

				});
		dialog = builder.create();
		dialog.show();
	}

	private void modifyGateWayPassword(String oldPassword, String newPassword,
			String confirmPassword) {

		AccountManager am = AccountManager.getAccountManger();
		if (StringUtil.isNullOrEmpty(oldPassword)) {
			WLToast.showToast(
					mContext,
					resources
							.getString(R.string.set_password_not_null_hint),
					WLToast.TOAST_SHORT);
			return;
		} else{
			String encrptOldPassword = MD5Util.encrypt(oldPassword);
			if(!StringUtil.equals(am.getmCurrentInfo().getGwPwd(), encrptOldPassword)){
				WLToast.showToast(
						mContext,
						resources
								.getString(R.string.set_account_manager_modify_gw_password_previous_wrong),
						WLToast.TOAST_SHORT);
				return;
			}
		}
		if (StringUtil.isNullOrEmpty(newPassword)) {
			WLToast.showToast(
					mContext,
					resources
							.getString(R.string.set_password_not_null_hint),
					WLToast.TOAST_SHORT);
			return;
		}
		if(StringUtil.isNullOrEmpty(confirmPassword)) {
			WLToast.showToast(
					mContext,
					resources
							.getString(R.string.set_password_not_null_hint),
					WLToast.TOAST_SHORT);
			return;
		}
		else if (!newPassword.equals(confirmPassword)) {
			WLToast.showToast(
					mContext,
					resources
							.getString(R.string.set_account_manager_modify_gw_password_new_compare_sure_unequal),
					WLToast.TOAST_SHORT);
			return;
		} else if (newPassword.length() < 6 || newPassword.length() > 16) {
			WLToast.showToast(
					mContext,
					resources
							.getString(R.string.set_account_manager_modify_gw_password_length_not_enough),
					WLToast.TOAST_SHORT);
			return;
		}
		
		if(oldPassword.equals(newPassword)){
			WLToast.showToast(
					mContext,
					resources
							.getString(R.string.set_account_manager_modify_gw_old_new_not_same),
					WLToast.TOAST_SHORT);
			return;
		}
		if (dialog != null)
			dialog.dismiss();
		oldPassword = MD5Util.encrypt(oldPassword);
		newPassword = MD5Util.encrypt(newPassword);
		am.setNewPassword(newPassword);
		SendMessage.sendChangeGwPwdMsg(mContext, am.getmCurrentInfo().getGwID(), oldPassword,
				newPassword);
	}

}
