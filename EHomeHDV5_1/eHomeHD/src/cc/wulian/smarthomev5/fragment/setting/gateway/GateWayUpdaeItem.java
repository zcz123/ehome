package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class GateWayUpdaeItem extends AbstractSettingItem {

	public GateWayUpdaeItem(Context context) {
		super(context, R.drawable.icon_gateway_update, context.getResources()
				.getString(R.string.set_account_manager_gw_version));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoTextView.setVisibility(View.VISIBLE);
		String gwVersion = preference.getGateWayVersion(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		infoTextView.setText(gwVersion);
	}

	@Override
	public void doSomethingAboutSystem() {
		//showChangeGatewayUpdateDialog();
	}

	/**
	 * <h1>提示更新对话框</h1>
	 */
	private void showChangeGatewayUpdateDialog() {
		final WLDialog dialog;
		WLDialog.Builder builder = new Builder(mContext);
		builder.setContentView(R.layout.software_new_dialog)
				.setPositiveButton(
						R.string.set_account_manager_gw_version_have_new_btn_now_update)
				.setNegativeButton(
						R.string.set_account_manager_gw_version_have_new_btn_next)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}

				});
		dialog = builder.create();
		dialog.show();
	}

}
