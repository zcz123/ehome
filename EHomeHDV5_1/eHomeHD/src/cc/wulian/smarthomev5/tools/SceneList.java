package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.adapter.SimpleSceneInfoAdapter;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.action.AbstractPopWindow;
import com.yuantuo.customview.action.menu.ActionPopMenu;
import com.yuantuo.customview.ui.ScreenSize;

public class SceneList {
	private final Context mContext;
	private final boolean mNeedCancelItem;
	private final ActionPopMenu mPopMenu;
	private final SimpleSceneInfoAdapter mAdapter;
	private Resources resources;
	private final MainApplication mApplication;
	private final List<SceneInfo> mSceneListInfo = new ArrayList<SceneInfo>();
	private OnSceneListItemClickListener mItemClickListener;

	@SuppressLint("ResourceAsColor")
	public SceneList(Context context, boolean needCancelItem) {
		this.mContext = context;
		resources = this.mContext.getResources();
		mNeedCancelItem = needCancelItem;
		mApplication = MainApplication.getApplication();
		resetListInfo();
		mAdapter = new SimpleSceneInfoAdapter(context, mSceneListInfo);
		mPopMenu = new ActionPopMenu(context);
		mPopMenu.setTitle(R.string.scene_select_scene_hint);
		mPopMenu.setTitleTextColor(R.color.select_scene_title);
		mPopMenu.setTitleBackground(R.color.holo_gray_light);
		// mPopMenu.setDivider(mContext.getResources().getDrawable(
		// R.drawable.divider_vertical_holo_dark));
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

	private void resetListInfo() {
		mSceneListInfo.clear();
		mSceneListInfo.addAll(mApplication.sceneInfoMap.values());
		createDefaultScene();
	}
	private void createDefaultScene() {
		SceneInfo info = new SceneInfo();
		info.setSceneID(CmdUtil.SCENE_UNKNOWN);
		info.setName(resources.getString(R.string.scene_unbind));
		mSceneListInfo.add(info);
	}
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

	public WLBaseAdapter<SceneInfo> getAdapter() {
		return mAdapter;
	}

	public void setOnSceneListItemClickListener(
			OnSceneListItemClickListener listener) {
		mItemClickListener = listener;
	}

	private final AbstractPopWindow.OnActionPopMenuItemSelectListener mActionPopMenuItemSelectListener = new AbstractPopWindow.OnActionPopMenuItemSelectListener() {
		@Override
		public void onActionPopMenuItemSelect(int pos) {
			if (mItemClickListener != null) {
				mItemClickListener.onSceneListItemClicked(SceneList.this, pos,
						getAdapter().getItem(pos));
			}
		}
	};

	public static interface OnSceneListItemClickListener {
		public void onSceneListItemClicked(SceneList list, int pos,
				SceneInfo info);
	}

}
