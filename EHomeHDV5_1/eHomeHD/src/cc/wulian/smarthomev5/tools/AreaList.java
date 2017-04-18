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
import cc.wulian.smarthomev5.entity.IconResourceEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.IconChooseView;

import com.yuantuo.customview.action.AbstractPopWindow;
import com.yuantuo.customview.action.menu.ActionPopMenu;
import com.yuantuo.customview.ui.ScreenSize;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class AreaList {
	private final Context mContext;
	private final boolean mNeedCancelItem;
	private final ActionPopMenu mPopMenu;
	private final SimpleAreaAdapter mAdapter;
	private Resources resources;
	private AreaGroupManager mAreaGroupManager = AreaGroupManager.getInstance();
	private final List<RoomInfo> mAreaList = new ArrayList<RoomInfo>();
	private OnAreaListItemClickListener mItemClickListener;

	@SuppressLint("ResourceAsColor")
	public AreaList(Context context, boolean needCancelItem) {
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

		List<IconResourceEntity> areaIconBlackList = new ArrayList<IconResourceEntity>();
		for (int i = 1; i <= 18; i++) {
			IconResourceEntity iconEntity = new IconResourceEntity();
			iconEntity.iconkey = i;
			iconEntity.iconRes = DeviceTool
					.DefaultgetAreaIconResourceByIconIndex(i + "");
			iconEntity.iconSelectedRes = DeviceTool
					.PressgetAreaIconResourceByIconIndex(i + "");
			areaIconBlackList.add(iconEntity);
		}
		final IconChooseView chooseView = new IconChooseView(mContext,
				areaIconBlackList);
		chooseView.setInputHintTextContent(mContext.getResources().getString(
				R.string.device_config_edit_dev_area));
		chooseView.setSelectedChangedBackgroundColor(false);
		chooseView.setSelectedChangedImageDrawable(true);

		chooseView.setOnItemClickListener(new IconChooseView.OnIconClickListener() {

			@Override
			public void onIconClick(IconResourceEntity entity) {
				String mAreaName = chooseView.getInputTextContent();
				if (StringUtil.isNullOrEmpty(mAreaName)) {
					String str = DeviceTool.getDefaultAreaTextByIconIndex(
							mContext, entity.iconkey);
					chooseView.setInputHintTextContent(str);
				}
			}
		});
		chooseView.swapData(areaIconBlackList);
		class AddSceneListener implements MessageListener {

			@Override
			public void onClickPositive(View contentViewLayout) {
				String areaName = chooseView.getInputTextContent().trim();
				if (StringUtil.isNullOrEmpty(areaName))
					areaName = chooseView.getInputHintTextContent();
				if (StringUtil.isNullOrEmpty(areaName)) {
					chooseView.requestFocus();
					chooseView.setError(mContext
							.getText(R.string.device_area_not_null_hint));
				} else if (StringUtil.isNullOrEmpty(chooseView.getCheckedItem()
						+ "")) {
					WLToast.showToast(mContext, mContext.getResources()
									.getString(R.string.area_icon_choose),
							WLToast.TOAST_SHORT);
				} else {
					IconResourceEntity iconEntity =chooseView.getCheckedItem();
					SendMessage.sendSetRoomMsg(mContext,AccountManager.getAccountManger().getmCurrentInfo().getGwID(),CmdUtil.MODE_ADD, null, areaName, iconEntity.iconkey+"");
				}
			}

			@Override
			public void onClickNegative(View contentViewLayout) {
			}

		}
		AddSceneListener addSceneListener = new AddSceneListener();
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(R.string.device_edit_area_add)
				.setContentView(chooseView)
				.setHeightPercent(0.6F)
				.setPositiveButton(
						mContext.getResources().getString(R.string.common_ok))
				.setNegativeButton(
						mContext.getResources().getString(R.string.cancel))
				.setListener(addSceneListener);
		builder.create().show();
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
				mItemClickListener.onAreaListItemClicked(AreaList.this, pos,
						getAdapter().getItem(pos));
			}
		}
	};

	public static interface OnAreaListItemClickListener {
		public void onAreaListItemClicked(AreaList list, int pos, RoomInfo info);
	}

}
