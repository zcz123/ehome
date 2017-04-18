package cc.wulian.smarthomev5.fragment.more.wifi;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class WifiSelectFragment extends WulianFragment {
	private ListView mSelectListView;
	private LinearLayout mLinearLayout;
	private ImageView mSelectImageView;
	private TextView mSelectTextView;
	private SceneList mSceneList;
	private WifiSSIDAdapter adapter;
	// 作为判断当前listview的Item是否被选中的标记
	private static Boolean hasChecked = false;
	private int checkedPos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.more_wifi_scene));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.more_wifi_choose));
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(R.string.common_ok));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						if (hasChecked) {
							String result = adapter.getItem(checkedPos);
							WifiEntity mSceneWifiEntity = WifiDataManager.wifiEntities
									.get(0);
							mSceneWifiEntity.setSSID(result);
							WifiDataManager.wifiList.add(0, result);
							mActivity.finish();
						} else {
							Toast.makeText(
									mActivity,
									mApplication.getResources()
											.getString(
													R.string.more_wifi_choose_wifi_toast),
									Toast.LENGTH_SHORT).show();
						}

					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.more_wifi_condition_select, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSelectListView = (ListView) view.findViewById(R.id.wifi_select_list);
		mLinearLayout = (LinearLayout) view.findViewById(R.id.wifi_select_ll);
		mSelectImageView = (ImageView) view.findViewById(R.id.wifi_select_iv);
		mSelectTextView = (TextView) view.findViewById(R.id.wifi_select_tv);
		// 初始化listview中的Item,其中需要包括上一次选定wifi的数据
		adapter = new WifiSSIDAdapter(mActivity, WifiDataManager.getInstance().getWifiScanResultList());
		mSelectListView.setAdapter(adapter);
		if (WifiDataManager.getInstance().hasWifiSSID()
				|| hasChecked) {
			checkedPos = 0;
			hasChecked = true;
		}
		mSceneList = new SceneList(mActivity, true);
		mSceneList.setOnSceneListItemClickListener(mItemClickListener);
		mLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 场景信息popupwindow显示
				mSceneList.show(arg0);
			}
		});
		initConditionContent();
	}

	private final SceneList.OnSceneListItemClickListener mItemClickListener = new SceneList.OnSceneListItemClickListener() {
		@Override
		public void onSceneListItemClicked(SceneList list, int pos,
				SceneInfo info) {
			list.dismiss();
			setSceneInfo(info);
		}
	};

	public void initConditionContent() {
		WifiEntity entity = WifiDataManager.wifiEntities.get(0);
		if (entity.getConditionContent() == null) {
			return;
		}
		setSceneInfo(mApplication.sceneInfoMap.get(entity.getGwID()
				+ entity.getConditionContent()));
	}

	// 设置wifi设置中选定场景条件信息
	public void setSceneInfo(SceneInfo info) {
		if (info == null || info.getSceneID() == null) {
			mSelectTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSelectImageView.setImageResource(R.drawable.nav_scene_normal);
		} else if (CmdUtil.SCENE_UNKNOWN.equals(info.getSceneID())) {
			mSelectTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSelectImageView.setImageResource(R.drawable.nav_scene_normal);
			if (WifiDataManager.wifiEntities.size() > 0) {
				WifiEntity mSceneWifiEntity = WifiDataManager.wifiEntities
						.get(0);
				mSceneWifiEntity.setConditionContent(info.getSceneID());
				mSceneWifiEntity.time = String.valueOf(System
						.currentTimeMillis());
			}
		} else {
			mSelectTextView.setText(info.getName());
			Drawable checkedIcon = SceneManager
					.getSceneIconDrawable_Light_Small(mActivity, info.getIcon());
			mSelectImageView.setImageDrawable(checkedIcon);
			if (WifiDataManager.wifiEntities.size() > 0) {
				WifiEntity mSceneWifiEntity = WifiDataManager.wifiEntities
						.get(0);
				mSceneWifiEntity.conditionContent = info.getSceneID();
				mSceneWifiEntity.time = String.valueOf(System
						.currentTimeMillis());
			}
		}
	}

	public class WifiSSIDAdapter extends WLBaseAdapter<String> {
		public WifiSSIDAdapter(Context context, List<String> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(
					R.layout.more_wifi_condition_select_list_item, null);
		}

		@Override
		protected void bindView(Context context, final View view,
				final int pos, final String item) {
			super.bindView(context, view, pos, item);
			TextView mTextView = (TextView) view
					.findViewById(R.id.more_wifi_item_SSID);
			mTextView.setText(item);
			ImageView mCheckedDoit = (ImageView) view
					.findViewById(R.id.more_wifi_scene_doit);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					hasChecked = true;
					checkedPos = pos;
					notifyDataSetChanged();
				}
			});
			if (hasChecked && (checkedPos == pos)) {
				mCheckedDoit.setVisibility(View.VISIBLE);
				view.setSelected(true);
			} else {
				mCheckedDoit.setVisibility(View.INVISIBLE);
				view.setSelected(false);
			}
		}

	}

}
