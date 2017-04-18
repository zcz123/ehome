package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.CollectionsUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BackMusicActivationActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.adapter.DeviceListAdapter;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.JoinGatewayEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.view.DropDownListView;
import cc.wulian.smarthomev5.view.DropDownListView.OnRefreshListener;
import cc.wulian.smarthomev5.view.WLEditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.Config;
import com.yuantuo.customview.nineoldandroids.view.ViewPropertyAnimator;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.customview.ui.WLToast.OnToastInitListener;

public class DeviceFunctionFragment extends WulianFragment {
	@ViewInject(R.id.device_common_switch_iv)
	private View deviceCommonSwitchIv;
	@ViewInject(R.id.device_function_switch_iv)
	private View deviceFunctionSwitchIv;
	@ViewInject(R.id.device_area_switch_iv)
	private View deviceAreaSwitchIv;
	@ViewInject(R.id.device_ground_name)
	private TextView groupName;
	@ViewInject(R.id.device_function_search_ll)
	private LinearLayout checkFunctionSearchLinearLayout;
	@ViewInject(R.id.device_function_search_tv)
	private TextView deviceFunctionSearchTextView;
	@ViewInject(R.id.device_function_delete_iv)
	private ImageView deviceFunctionSearchDeleteImageView;
	@ViewInject(R.id.config_search_function_iv)
	private ImageView functionSearchImageView;

	@ViewInject(R.id.config_search_et)
	private WLEditText configSearchEditText;
	@ViewInject(R.id.config_device_lv)
	private DropDownListView dropDownDevicelistView;
	@ViewInject(R.id.config_wlsidebar)
	private LinearLayout sideBarLinearLayout;
	@ViewInject(R.id.device_list_global_layout)
	private LinearLayout globalLayout;
	@ViewInject(R.id.device_list_group_layout)
	private LinearLayout groupLayout;
	@ViewInject(R.id.device_list_search_box)
	private LinearLayout searchLayout;
	private DeviceListAdapter deviceAdapter;
	private Set<String> indexStringSet = new LinkedHashSet<String>();
	private Category currentCategory;

	private Comparator<WulianDevice> deviceComparator = new Comparator<WulianDevice>() {

		@Override
		public int compare(WulianDevice lhs, WulianDevice rhs) {
			String leftDeviceName = DeviceTool.getDeviceShowName(lhs);
			String leftDeviceRoomID = lhs.getDeviceRoomID();
			String rightDeviceName = DeviceTool.getDeviceShowName(rhs);
			String rightDeviceRoomID = rhs.getDeviceRoomID();
			int result = Trans2PinYin
					.trans2PinYin(leftDeviceName.trim())
					.toLowerCase()
					.compareTo(
							Trans2PinYin.trans2PinYin(rightDeviceName.trim())
									.toLowerCase());
			if (result != 0) {
				return result;
			} else {
				return leftDeviceRoomID.compareTo(rightDeviceRoomID);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceAdapter = new DeviceListAdapter(getActivity());
		initBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	@Override
	public void onShow() {
		super.onShow();
		initBar();
		loadData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list_expandable_container,
				container, false);
		ViewUtils.inject(this, v);
		return v;
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {

		groupName.setText(mApplication.getResources().getString(
				R.string.device_dev_group));
		deviceCommonSwitchIv.setSelected(false);
		deviceFunctionSwitchIv.setSelected(true);
		// alldevicelLayout.setVisibility(View.VISIBLE);
		deviceAreaSwitchIv.setSelected(false);

		dropDownDevicelistView.setAdapter(deviceAdapter);
		initDeviceLisrViewListeners();

		deviceAreaSwitchIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2Area();
			}
		});
		deviceCommonSwitchIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2Common();
			}
		});
		deviceFunctionSearchDeleteImageView
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						currentCategory = null;
						loadData();
					}
				});
		functionSearchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSearchCategoryPopupWindow(v);
			}
		});
		configSearchEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				animateToUp();
				SearchPopuWindow popupWindow = new SearchPopuWindow(mActivity);
				popupWindow.setDeviceListData(deviceAdapter.getData());
				popupWindow.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						initBar();
						animateToDowm();
					}
				});
				popupWindow.show(view);
				getSupportActionBar().hide();
			}
		});

	}

	private void showSearchCategoryPopupWindow(View view) {
		final MoreMenuPopupWindow categoryGroupPopupWindow = new MoreMenuPopupWindow(
				mActivity);
		List<MenuItem> items = new ArrayList<MoreMenuPopupWindow.MenuItem>();
		items.add(new MenuItem(mActivity) {

			@Override
			public void initSystemState() {
				iconImageView.setVisibility(View.GONE);
				iconImageViewRight.setVisibility(View.VISIBLE);
				titleTextView.setText(R.string.device_all);
				iconImageViewRight
						.setImageResource(R.drawable.device_category_group__search_item_selector);
				if (currentCategory == null)
					iconImageViewRight.setSelected(true);
				else
					iconImageViewRight.setSelected(false);
			}

			@Override
			public void doSomething() {
				if (currentCategory != null)
					currentCategory = null;
				loadData();
				categoryGroupPopupWindow.dismiss();
			}
		});
		for (final Category c : Category.values()) {
			items.add(new MenuItem(mActivity) {

				@Override
				public void initSystemState() {
					iconImageView.setVisibility(View.GONE);
					iconImageViewRight.setVisibility(View.VISIBLE);
					titleTextView.setText(DeviceTool.getCategoryName(
							mApplication, c));
					iconImageViewRight
							.setImageResource(R.drawable.device_category_group__search_item_selector);
					if (currentCategory == c)
						iconImageViewRight.setSelected(true);
					else
						iconImageViewRight.setSelected(false);
				}

				@Override
				public void doSomething() {
					if (currentCategory == c) {
						iconImageViewRight.setSelected(false);
						currentCategory = null;
					} else {
						iconImageViewRight.setSelected(true);
						currentCategory = c;
					}
					loadData();
					categoryGroupPopupWindow.dismiss();
				}
			});
		}

		categoryGroupPopupWindow.setMenuItems(items);
		categoryGroupPopupWindow.show(view);
		functionSearchImageView.setSelected(true);
		categoryGroupPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				functionSearchImageView.setSelected(false);
			}
		});

	}

	/**
	 * 设备ListView的相应事件处理
	 */
	private void initDeviceLisrViewListeners() {
		// 刷新功能
		dropDownDevicelistView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				requestDevicesSignal();
			}
		});
		// 点击进入设备大图
		dropDownDevicelistView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						showDetail((WulianDevice) parent.getAdapter().getItem(
								position));
					}

				});
	}

	/**
	 * 返回搜索后下返回初始状态动画
	 */
	private void animateToDowm() {
		ViewPropertyAnimator.animate(globalLayout).translationY(0)
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.setDuration(160).start();
	}

	/**
	 * 点击搜索后上滑动画
	 */
	private void animateToUp() {
		ViewPropertyAnimator.animate(globalLayout)
				.translationY(-groupLayout.getHeight())
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.setDuration(160).start();
	}

	public void switch2Area() {
		FragmentManager manager = mActivity.getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
				.getName());
		if (fragment != null) {
			NavigationFragment navFragment = (NavigationFragment) fragment;
			navFragment.changeFragement(DeviceAreaFragment.class.getName());
		}

	}

	public void switch2Common() {
		FragmentManager manager = mActivity.getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
				.getName());
		if (fragment != null) {
			NavigationFragment navFragment = (NavigationFragment) fragment;
			navFragment.changeFragement(DeviceCommonFragment.class.getName());
		}

	}

	/**
	 * 显示设备大图界面跳转，在Adapter中写会与左划冲突
	 * 
	 * @param device
	 */
	private void showDetail(WulianDevice device) {
		if (device == null) {
			return;
		}
		String isvalidate=device.getDeviceInfo().getIsvalidate();
		if(isvalidate!=null&&isvalidate.equals("2")){
			Intent it =new Intent(mActivity, BackMusicActivationActivity.class);
			it.putExtra(Config.DEVICE_ID,device.getDeviceID());
			it.putExtra(Config.DEVICE_TYPE,device.getDeviceType());
			it.putExtra(Config.GW_ID,device.getDeviceGwID());
			startActivity(it);
			return;
		}
		Bundle args = new Bundle();
		args.putString(DeviceDetailsFragment.EXTRA_DEV_GW_ID,
				device.getDeviceGwID());
		args.putString(DeviceDetailsFragment.EXTRA_DEV_ID, device.getDeviceID());
		Intent intent = new Intent();

		intent.setClass(mActivity, DeviceDetailsActivity.class);
		if (args != null)
			intent.putExtras(args);
		mActivity.startActivity(intent);

	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setTitle(
				mApplication.getResources()
						.getString(R.string.nav_device_title));
		getSupportActionBar().setRightIcon(R.drawable.common_use_add);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						new DeviceActionBarManager(mActivity).show(v);

					}
				});
	}

	/**
	 * 加载设备数据
	 */
	private void loadData() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				final List<WulianDevice> avabilieDeviceList = getAvailableDevice(mAccountManger.getmCurrentInfo()
						.getGwID(),currentCategory);

				final boolean isInitIndex = initIndexes(avabilieDeviceList);
				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						initViews(avabilieDeviceList, isInitIndex);
					}
				});
			}
		});

	}

	private boolean initIndexes(List<WulianDevice> avabilieDeviceList) {
		try {
			Set<String> indexSet = new LinkedHashSet<String>();
			for (WulianDevice device : avabilieDeviceList) {
				String deviceNamePinYin = Trans2PinYin.trans2PinYin(
						DeviceTool.getDeviceShowName(device)).toUpperCase();
				if (deviceNamePinYin != null && deviceNamePinYin.length() > 1)
					indexSet.add(deviceNamePinYin.trim().substring(0, 1));
			}
			if (!CollectionsUtil.isEquals(indexSet, indexStringSet)) {
				indexStringSet = indexSet;
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	private void initViews(List<WulianDevice> avabilieDeviceList,
			boolean isInitIndex) {
		if (isInitIndex) {
			sideBarLinearLayout.removeAllViews();
			for (String c : indexStringSet) {
				final TextView indexTextView = new TextView(mActivity);
				indexTextView.setText(c);
				indexTextView.setPadding(0, 8, 0, 8);
				indexTextView
						.setTextColor(getResources().getColor(R.color.desk_history_tvtext));
				indexTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						clickDeviceIndex(indexTextView.getText().toString());
					}
				});
				sideBarLinearLayout.addView(indexTextView);
			}
		}
		deviceAdapter.swapData(avabilieDeviceList);
		String hintStr = mApplication.getResources().getString(
				R.string.device_config_device_search_hint,
				deviceAdapter.getCount());
		configSearchEditText.setHint(hintStr);
		if (currentCategory != null) {
			checkFunctionSearchLinearLayout.setVisibility(View.VISIBLE);
			deviceFunctionSearchTextView.setText(DeviceTool.getCategoryName(
					mActivity, currentCategory));
		} else {
			 checkFunctionSearchLinearLayout.setVisibility(View.GONE);
//			checkFunctionSearchLinearLayout.setVisibility(View.VISIBLE);
//			deviceFunctionSearchDeleteImageView.setVisibility(View.GONE);
		}
	}

	public void clickDeviceIndex(String s) {
		if (s == null)
			return;
		final String showText = s;
		WLToast.showCustomToast(mActivity, R.layout.device_search_toast,
				new OnToastInitListener() {
					@Override
					public void init(View view) {
						TextView text = (TextView) view
								.findViewById(R.id.config_search_toast_textview);
						text.setText(showText);
					}
				}, WLToast.TOAST_SHORT, Gravity.CENTER);
		int pos = deviceAdapter.getPostionByLetter(s);
		dropDownDevicelistView.setSelectionFromTop(pos + 1, 0);
	}

	/**
	 * 获取所有可控设备
	 */
	public List<WulianDevice> getAvailableDevice(String gwID, Category category) {
		List<WulianDevice> avabilieDeviceList = new ArrayList<WulianDevice>();
		DeviceCache deviceCache = DeviceCache.getInstance(mActivity);
		for (WulianDevice device : deviceCache.getAllDevice()) {
			if (category != null)
				if (!deviceCache.isCategory(device.getClass(), category)) {
					continue;
				}
			avabilieDeviceList.add(device);
		}
		Collections.sort(avabilieDeviceList, deviceComparator);
		return avabilieDeviceList;
	}

	/**
	 * 刷新信号
	 * 
	 * @param
	 */
	public void requestDevicesSignal() {
		TaskExecutor.getInstance().execute(new Runnable() {
			final List<WulianDevice> devices = deviceAdapter.getData();

			@Override
			public void run() {
				if (devices == null)
					return;
				for (WulianDevice device : devices) {
					SendMessage.sendQueryDevRssiMsg(device.getDeviceGwID(),
							device.getDeviceID(), true);
				}
			}
		});
	}

	public void onEventMainThread(DeviceEvent event) {
		if (DeviceEvent.REFRESH.equals(event.action)
				|| DeviceEvent.REMOVE.equals(event.action)) {
			loadData();
		}
	}

	public void onEventMainThread(JoinGatewayEvent event) {
		if (event != null) {
			ProgressDialogManager dialogManager = ProgressDialogManager
					.getDialogManager();
			dialogManager.dimissDialog(
					DeviceActionBarManager.KEY_JOIN_GW_DIALOG, 0);
		}

	}
}