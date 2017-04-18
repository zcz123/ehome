package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SimpleDeviceInfoAdapter;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.action.AbstractPopWindow;
import com.yuantuo.customview.action.menu.ActionPopMenu;
import com.yuantuo.customview.ui.ScreenSize;

public class DeviceList {
	public static interface OnDeviceListItemClickListener {
		public void onDeviceListItemClicked(DeviceList list, int pos,
				DeviceInfo deviceInfo);
	}

	private final Context mContext;
	private final ActionPopMenu mPopMenu;
	private final SimpleDeviceInfoAdapter mAdapter;

	private List<DeviceInfo> mDeviceList;
	private OnDeviceListItemClickListener mItemClickListener;

	public DeviceList(Context context, List<DeviceInfo> deviceInfos) {
		this.mContext = context;
		if (deviceInfos == null) {
			this.mDeviceList = new ArrayList<DeviceInfo>();
		} else {
			this.mDeviceList = deviceInfos;
		}
		mAdapter = new SimpleDeviceInfoAdapter(context, mDeviceList);
		mPopMenu = new ActionPopMenu(context);
		mPopMenu.setTitle(R.string.device_select_device_hint);
		mPopMenu.setTitleBackground(R.color.holo_gray_light);
		// mPopMenu.setDivider(mContext.getResources().getDrawable(R.drawable.divider_vertical_holo_dark));
		createUnbindItem();
		mPopMenu.setAdapter(mAdapter);
		mPopMenu.setOnActionPopMenuItemSelectListener(mActionPopMenuItemSelectListener);
		mPopMenu.getmBottomView().setVisibility(View.VISIBLE);
		mPopMenu.setBottom(mContext.getResources().getString(
				R.string.cancel));
		mPopMenu.getmBottomView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopMenu.isShown()) {
					mPopMenu.dismiss();
				}
			}
		});
	}

	public boolean isShowing() {
		return mPopMenu.isShown();
	}

	public void dismiss() {
		mPopMenu.dismiss();
	}

	public void show(View view) {
		final Resources resources = mContext.getResources();
		mPopMenu.setBackgroundDrawable(new ColorDrawable(resources
				.getColor(R.color.holo_gray_light)));
		mPopMenu.setLayoutParams((int) (ScreenSize.screenWidth / 1.7),
				(int) (ScreenSize.screenHeight / 1.5));
		mPopMenu.showAtLocation(view, Gravity.CENTER, 0, 0);
	}

	public WLBaseAdapter<DeviceInfo> getAdapter() {
		return mAdapter;
	}

	public void setOnDeviceListItemClickListener(
			OnDeviceListItemClickListener listener) {
		mItemClickListener = listener;
	}

	public void createUnbindItem() {
		DeviceInfo info = new DeviceInfo();
		info.setName(mContext.getResources().getString(R.string.scene_unbind));
		mDeviceList.add(info);
	}
	
	private final AbstractPopWindow.OnActionPopMenuItemSelectListener mActionPopMenuItemSelectListener = new AbstractPopWindow.OnActionPopMenuItemSelectListener() {
		@Override
		public void onActionPopMenuItemSelect(int pos) {
			if (mItemClickListener != null) {
				mItemClickListener.onDeviceListItemClicked(DeviceList.this,
						pos, getAdapter().getItem(pos));
			}
		}
	};
}
