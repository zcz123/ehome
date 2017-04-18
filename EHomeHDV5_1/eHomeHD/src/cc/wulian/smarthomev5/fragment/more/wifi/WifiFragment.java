package cc.wulian.smarthomev5.fragment.more.wifi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.WifiAddDeviceActivity;
import cc.wulian.smarthomev5.activity.WifiSelectActivity;
import cc.wulian.smarthomev5.dao.WifiDao;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class WifiFragment extends WulianFragment {
	private TextView mSelectTextView;
	private LinearLayout mSceneLayout;
	private LinearLayout mDeviceLayout;
	private ImageView mSceneImageView;
	private TextView mSceneTextView;
	private SceneList mSceneList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WifiDataManager.getInstance();
		initBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		WifiEntity mSceneEntity = WifiDataManager.wifiEntities.get(0);
		if (mSceneEntity.getSSID() != null) {
			mSelectTextView.setText(mSceneEntity.getSSID());
		}

	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_more));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.more_wifi_scene));
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(R.string.set_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						saveWifiSetting();

					}

				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.more_wifi_content, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSelectTextView = (TextView) view.findViewById(R.id.wifi_scene_select);
		mSceneLayout = (LinearLayout) view.findViewById(R.id.wifi_scene_ll);
		mDeviceLayout = (LinearLayout) view.findViewById(R.id.wifi_device_ll);
		mSceneImageView = (ImageView) view.findViewById(R.id.wifi_scene_iv);
		mSceneTextView = (TextView) view.findViewById(R.id.wifi_scene_tv);
		mSceneList = new SceneList(mActivity, true);
		mSceneList.setOnSceneListItemClickListener(mItemClickListener);
		mSelectTextView.setOnClickListener(listener);
		mSceneLayout.setOnClickListener(listener);
		mDeviceLayout.setOnClickListener(listener);
		if (WifiDataManager.wifiEntities.size() > 0) {
			WifiEntity entity = WifiDataManager.wifiEntities.get(0);
			if (entity.SSID != null) {
				mSelectTextView.setText(entity.SSID);
			}
			setSceneInfo(mApplication.sceneInfoMap.get(entity.getGwID()
					+ entity.getOperateID()));
		}
	}

	private final SceneList.OnSceneListItemClickListener mItemClickListener = new SceneList.OnSceneListItemClickListener() {
		@Override
		public void onSceneListItemClicked(SceneList list, int pos,
				SceneInfo info) {
			list.dismiss();
			setSceneInfo(info);
		}
	};

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.wifi_scene_ll:
				mSceneList.show(v);
				break;
			case R.id.wifi_device_ll:
				mActivity.JumpTo(WifiAddDeviceActivity.class);
				break;
			case R.id.wifi_scene_select:
				mActivity.JumpTo(WifiSelectActivity.class);
			}

		}
	};

	/**
	 * 保存设置scene信息,此处为场景触发条件可以为空
	 * 
	 */
	public void setSceneInfo(SceneInfo info) {
		if (info == null || info.getSceneID() == null) {
			mSceneTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
		} else if (CmdUtil.SCENE_UNKNOWN.equals(info.getSceneID())) {
			mSceneTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
			if (WifiDataManager.wifiEntities.size() > 0) {
				WifiEntity mSceneWifiEntity = WifiDataManager.wifiEntities
						.get(0);
				mSceneWifiEntity.operateID = info.getSceneID();
				mSceneWifiEntity.time = String.valueOf(System
						.currentTimeMillis());
			}
		} else {
			mSceneTextView.setText(info.getName());
			Drawable checkedIcon = SceneManager
					.getSceneIconDrawable_Light_Small(
							WifiFragment.this.getActivity(), info.getIcon());
			mSceneImageView.setImageDrawable(checkedIcon);
			if (WifiDataManager.wifiEntities.size() > 0) {
				WifiEntity mSceneWifiEntity = WifiDataManager.wifiEntities
						.get(0);
				mSceneWifiEntity.operateID = info.getSceneID();
				mSceneWifiEntity.time = String.valueOf(System
						.currentTimeMillis());
			}
		}
	}

	/**
	 * 保存wifi设置,以及将当前选定wifi的SSID保存数据库供下次进入时获取并显示在首位
	 */
	private void saveWifiSetting() {
		WifiEntity sceneEntity = WifiDataManager.wifiEntities.get(0);
		if (sceneEntity.SSID == null) {
			Toast.makeText(
					mActivity,
					mApplication.getResources().getString(
							R.string.more_wifi_choose_wifi_toast),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if ((sceneEntity.getOperateID() == null || CmdUtil.SCENE_UNKNOWN
				.equals(sceneEntity.getOperateID())
				&& WifiDataManager.wifiEntities.size() < 2)) {
			Toast.makeText(
					mActivity,
					mApplication.getResources().getString(
							R.string.more_wifi_choose_scene_toast),
					Toast.LENGTH_SHORT).show();
			return;
		}
		WifiDao wifiDao = WifiDao.getInstance();
		WifiEntity deleteEntity = new WifiEntity();
		deleteEntity.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		wifiDao.delete(deleteEntity);
		for (WifiEntity entity : WifiDataManager.wifiEntities) {
			wifiDao.insert(entity);
		}
		Toast.makeText(
				mActivity,
				mApplication.getResources().getString(
						R.string.device_set_success_hint),
				Toast.LENGTH_SHORT).show();
		mActivity.finish();

	}

}
