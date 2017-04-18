package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SimpleAreaAdapter;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.action.AbstractPopWindow;
import com.yuantuo.customview.action.menu.ActionPopMenu;
import com.yuantuo.customview.ui.ScreenSize;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class MonitorDialogList {
	private final Context mContext;
	private final boolean mNeedCancelItem;
	private final ActionPopMenu mPopMenu;
	private final SimpleAreaAdapter mAdapter;
	private Resources resources;
	private AreaGroupManager mAreaGroupManager = AreaGroupManager.getInstance();
	private final List<RoomInfo> mAreaList = new ArrayList<RoomInfo>();
	private OnAreaListItemClickListener mItemClickListener;

	@SuppressLint("ResourceAsColor")
	public MonitorDialogList(Context context, boolean needCancelItem) {
		this.mContext = context;
		resources = this.mContext.getResources();
		mNeedCancelItem = needCancelItem;
		resetListInfo();
		mAdapter = new SimpleAreaAdapter(context, mAreaList);
		mPopMenu = new ActionPopMenu(context);
		mPopMenu.setTitleTextColor(R.color.select_scene_title);
		mPopMenu.setTitleBackground(R.color.holo_gray_light);
		// mPopMenu.setDivider(mContext.getResources().getDrawable(
		// R.drawable.divider_vertical_holo_dark));
		mPopMenu.setAdapter(mAdapter);
		mPopMenu.setTitle(mContext.getResources().getString(
				R.string.device_config_choose_area));
		mPopMenu.setOnActionPopMenuItemSelectListener(mActionPopMenuItemSelectListener);
		mPopMenu.getmBottomView().setVisibility(View.VISIBLE);
		mPopMenu.setBottom(mContext.getResources().getString(
				R.string.device_config_edit_dev_area_create));
		mPopMenu.getmBottomView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopMenu.isShown()) {
					mPopMenu.dismiss();
					// Intent it = new Intent();
					// it.setClass(mContext, AreaGroupEditActivity.class);
					// mContext.startActivity(it);
					createAddAreaDialog();
				}
			}
		});
	}

	/**
	 * 创建添加对话框
	 */
	private void createAddAreaDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(
				R.string.device_edit_area_add));
		View dlgView = inflater.inflate(
				R.layout.device_area_add_dialog_edit_text, null);
		builder.setContentView(dlgView)
				.setNegativeButton(
						mContext.getResources().getString(
								R.string.cancel))
				.setPositiveButton(
						mContext.getResources().getString(
								R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						EditText areaNameEditText = (EditText) contentViewLayout
								.findViewById(R.id.fragement_device_area_add_dialog_edit_text);
						String areaName = areaNameEditText.getText().toString();
						if (StringUtil.isNullOrEmpty(areaName)) {
							WLToast.showToast(
									mContext,
									mContext.getResources().getString(
											R.string.device_area_not_null_hint),
									0);
							return;
						}
						SendMessage.sendSetRoomMsg(mContext, AccountManager
								.getAccountManger().getmCurrentInfo().getGwID(),
								CmdUtil.MODE_ADD, null, areaName, "");
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	private void resetListInfo() {
		mAreaList.clear();
		mAreaList.addAll(mAreaGroupManager.getDeviceAreaEnties());
		// createDefaultScene();
	}

	// private void createDefaultScene() {
	// RoomInfo info = new RoomInfo();
	// info.setSceneID(CmdUtil.SCENE_UNKNOWN);
	// info.setName(resources.getString(R.string.scene_unbind));
	// mSceneListInfo.add(info);
	// }
	public boolean isShowing() {
		return mPopMenu.isShown();
	}

	public void dismiss() {
		mPopMenu.dismiss();
	}

	public void show(View view) {
		mPopMenu.setBackgroundDrawable(new ColorDrawable(resources
				.getColor(R.color.holo_gray_light)));
		mPopMenu.setLayoutParams((int) (ScreenSize.screenWidth / 1.7),
				(int) (ScreenSize.screenHeight / 1.5));
		mPopMenu.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	public void refreshListData() {
		if (isShowing()) {
			resetListInfo();
			mAdapter.notifyDataSetChanged();
		}
	}

	public WLBaseAdapter<RoomInfo> getAdapter() {
		return mAdapter;
	}

	public void setOnAreaListItemClickListener(
			OnAreaListItemClickListener listener) {
		mItemClickListener = listener;
	}

	private final AbstractPopWindow.OnActionPopMenuItemSelectListener mActionPopMenuItemSelectListener = new AbstractPopWindow.OnActionPopMenuItemSelectListener() {
		@Override
		public void onActionPopMenuItemSelect(int pos) {
			if (mItemClickListener != null) {
				mItemClickListener.onAreaListItemClicked(MonitorDialogList.this, pos,
						getAdapter().getItem(pos));
			}
		}
	};

	public static interface OnAreaListItemClickListener {
		public void onAreaListItemClicked(MonitorDialogList list, int pos, RoomInfo info);
	}

}
