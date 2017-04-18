package cc.wulian.smarthomev5.fragment.house;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cc.wulian.app.model.device.DesktopCameraDevice;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.house.AddActionTaskDeviceAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.view.WLEditText;
import cc.wulian.smarthomev5.view.WLEditText.WLInputTextWatcher;

public class HouseKeeperActionSelectDeviceFragment extends WulianFragment {

	public static final String ACTION_TASK_DEVICE_NUMBER = "ACTION_TASK_DEVICE_NUMBER";
	private ListView linkDeviceList;
	private WLEditText searchEditText;
	private ImageView functionSearchImageView;
	private ImageView areaSearchImageView;
	private DeviceCache mDeviceCache;
	private AddActionTaskDeviceAdapter deviceAdapter;
	private Category currentCategory;
	private DeviceAreaEntity currentDeviceAreaEntity;
	public static List<WulianDevice> preloadDeviceList;
	private List<WulianDevice> avabilieDeviceList;
	private List<WulianDevice> allDeviceList;
	private boolean isDesktopCamera = false;

	private static AddLinkDeviceListener addLinkDeviceListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getActivity().getIntent().getExtras();
		int deviceNumber = bundle.getInt(ACTION_TASK_DEVICE_NUMBER);
		mDeviceCache = DeviceCache.getInstance(mActivity);
		deviceAdapter = new AddActionTaskDeviceAdapter(mActivity, null,
				deviceNumber);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_link_choose_device_list,
				null);
	}

	@Override
	public void onViewCreated(final View view,
			@Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		linkDeviceList = (ListView) view
				.findViewById(R.id.house_keeper_task_link_choose_device);
		searchEditText = (WLEditText) view
				.findViewById(R.id.house_task_choose_device_search);
		functionSearchImageView = (ImageView) view
				.findViewById(R.id.house_task_choose_device_search_function_iv);
		areaSearchImageView = (ImageView) view
				.findViewById(R.id.house_task_choose_device_search_area);
		linkDeviceList.setAdapter(deviceAdapter);
		searchEditText.registWLIputTextWatcher(new EditTextWatcher());
		functionSearchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSearchCategoryPopupWindow(v);
			}
		});
		areaSearchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSearchAreaPopupWindow(v);
			}
		});
		// linkDeviceList.setOnItemClickListener(new
		// AdapterView.OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position,
		// long id) {
		//
		// }
		// });
		allDeviceList = preloadDeviceList;
		avabilieDeviceList = allDeviceList;
		preloadDeviceList = null;
		initViews(allDeviceList);
	}
	//按设备类别查找
	private void showSearchCategoryPopupWindow(View view) {
		final MoreMenuPopupWindow categoryGroupPopupWindow = new MoreMenuPopupWindow(
				mActivity);
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem(mActivity) {

			@Override
			public void initSystemState() {
				iconImageView.setVisibility(View.GONE);
				iconImageViewRight.setVisibility(View.VISIBLE);
				titleTextView.setText(mApplication.getResources().getString(
						R.string.device_all));
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
					if (currentCategory == c&&(!isDesktopCamera))
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
						isDesktopCamera=false;
					}
					loadData();
					categoryGroupPopupWindow.dismiss();
				}
			});
		}
		// 判断客户是否添加过桌面摄像机
		// 是则在菜单栏里显示
		if (isDesktopCameraExist()) {
			items.add(new MenuItem(mActivity) {
				@Override
				public void initSystemState() {
					iconImageView.setVisibility(View.GONE);
					iconImageViewRight.setVisibility(View.VISIBLE);
					titleTextView.setText(getString(R.string.WL_DESKTOP_CAMERA));
					iconImageViewRight
							.setImageResource(R.drawable.device_category_group__search_item_selector);
					if (isDesktopCamera)
						iconImageViewRight.setSelected(true);
					else
						iconImageViewRight.setSelected(false);
				}

				@Override
				public void doSomething() {
					if (isDesktopCamera) {
						iconImageViewRight.setSelected(false);
						isDesktopCamera = false;
						currentCategory=null;
					} else {
						iconImageViewRight.setSelected(true);
						isDesktopCamera = true;
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
	//按设备区域查找
	private void showSearchAreaPopupWindow(View view) {
		final MoreMenuPopupWindow areaGroupPopupWindow = new MoreMenuPopupWindow(
				mActivity);
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem(mActivity) {

			@Override
			public void initSystemState() {
				iconImageView.setVisibility(View.GONE);
				iconImageViewRight.setVisibility(View.VISIBLE);
				titleTextView.setText(mApplication.getResources().getString(
						R.string.device_all));
				iconImageViewRight
						.setImageResource(R.drawable.device_category_group__search_item_selector);
				if (currentDeviceAreaEntity == null)
					iconImageViewRight.setSelected(true);
				else
					iconImageViewRight.setSelected(false);
			}

			@Override
			public void doSomething() {
				if (currentDeviceAreaEntity != null)
					currentDeviceAreaEntity = null;
				loadData();
				areaGroupPopupWindow.dismiss();
			}
		});
		for (final DeviceAreaEntity deviceAreaEntity: AreaGroupManager.getInstance().getDeviceAreaEnties()) {
			items.add(new MenuItem(mActivity) {

				@Override
				public void initSystemState() {
					iconImageView.setVisibility(View.GONE);
					iconImageViewRight.setVisibility(View.VISIBLE);
					titleTextView.setText(deviceAreaEntity.getName());
					iconImageViewRight
							.setImageResource(R.drawable.device_category_group__search_item_selector);
					if (currentDeviceAreaEntity == deviceAreaEntity)
						iconImageViewRight.setSelected(true);
					else
						iconImageViewRight.setSelected(false);
				}

				@Override
				public void doSomething() {
					if (currentDeviceAreaEntity == deviceAreaEntity) {
						iconImageViewRight.setSelected(false);
						currentDeviceAreaEntity = null;
					} else {
						iconImageViewRight.setSelected(true);
						currentDeviceAreaEntity = deviceAreaEntity;
					}
					loadData();
					areaGroupPopupWindow.dismiss();
				}
			});
		}
		areaGroupPopupWindow.setMenuItems(items);
		areaGroupPopupWindow.show(view, -200, 2, 150);
		areaSearchImageView.setSelected(true);
		areaGroupPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				areaSearchImageView.setSelected(false);
			}
		});
	}
	// 判断是否是桌面摄像机
	private boolean isDesktopCameraExist() {
		AccountManager accountManager = AccountManager.getAccountManger();
		GatewayInfo info = accountManager.getmCurrentInfo();
		if (("" + info.getGwVer().charAt(2)).equals("9")) {
			return true;
		}
		return false;
	}

	private List<WulianDevice> getAddLinkDevices(Category category, DeviceAreaEntity deviceAreaEntity) {
		Collection<WulianDevice> result = allDeviceList;
		List<WulianDevice> devices = new ArrayList<WulianDevice>();
		for (WulianDevice device : result) {
			if (device.isAutoControl(true)) {
				if (category != null || deviceAreaEntity != null){
					if (category == null){
						if ((StringUtil.isNullOrEmpty(device.getDeviceInfo().getRoomID())
							&& deviceAreaEntity.getRoomID().equals("-1"))
							|| (!deviceAreaEntity.getRoomID().equals(device.getDeviceInfo().getRoomID()))) {
							continue;
						}
					}else if (deviceAreaEntity == null){
						if (!mDeviceCache.isCategory(device.getClass(), category)) {
							continue;
						}
					}else {
						if (!mDeviceCache.isCategory(device.getClass(), category) || !deviceAreaEntity.getRoomID().equals(device.getDeviceInfo().getRoomID())) {
							continue;
						}
					}
				}
				devices.add(device);
			}
		}
		// 首先判断是否是桌面摄像机
		//判断是是否选择的桌面摄像机选项或者全部设备从而决定是否添加桌面摄像机设备
		if(isDesktopCameraExist()){
			if (isDesktopCamera||currentCategory==null) {
				DesktopCameraDevice cameraDevice = new DesktopCameraDevice(
						mActivity,"camera");
				devices.add(cameraDevice);
			}
		}
		Collections.sort(devices, deviceComparator);
		return devices;
	}

	/**
	 * 加载设备数据
	 */
	private void loadData() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				avabilieDeviceList = getAddLinkDevices(currentCategory, currentDeviceAreaEntity);
				mActivity.runOnUiThread(new Runnable() {
					public void run() {
						initViews(avabilieDeviceList);
					}
				});
			}
		});
	}

	private void initViews(List<WulianDevice> controlDeviceList) {
		deviceAdapter.swapData(controlDeviceList);
		String hintStr = mApplication.getResources().getString(
				R.string.device_config_device_search_hint,
				deviceAdapter.getCount());
		searchEditText.setHint(hintStr);
	}

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

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.cancel);
		getSupportActionBar().setTitle(R.string.device_select_device_hint);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(R.string.common_ok);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						List<AutoActionInfo> tasks = createLinkTasks();
						if (addLinkDeviceListener != null) {
							addLinkDeviceListener
									.onAddLinkDeviceListenerChanged(tasks);
							addLinkDeviceListener = null;
						}
						mActivity.finish();
						// EventBus.getDefault().post(new TaskEvent(null,
						// CmdUtil.MODE_ADD, true, null,null, null));
					}
				});
	}

	private List<AutoActionInfo> createLinkTasks() {
		List<AutoActionInfo> entities = new ArrayList<AutoActionInfo>();
		List<WulianDevice> lists = deviceAdapter.getAllDevice();
		for (int i = 0; i < lists.size(); i++) {
			// 增加桌面摄像机的判断
			WulianDevice deviceInfo = lists.get(i);
			AutoActionInfo taskInfo = new AutoActionInfo();
			if (deviceInfo.getDefaultDeviceName().equals(getString(R.string.WL_DESKTOP_CAMERA))) {
				taskInfo.setSortNum(i + "");
				taskInfo.setType(1 + "");
				taskInfo.setObject("self");
				taskInfo.setEpData("");
				taskInfo.setDelay("0");
			} else {
				taskInfo.setSortNum(i + "");
				taskInfo.setType(2 + "");
				taskInfo.setObject(deviceInfo.getDeviceID() + ">"
						+ deviceInfo.getDeviceType() + ">" + WulianDevice.EP_0
						+ ">" + deviceInfo.getDeviceType());
				taskInfo.setEpData("");
				taskInfo.setDelay("0");
			}

			entities.add(taskInfo);
		}
		return entities;
	}

	public static void setAddLinkDeviceListener(
			AddLinkDeviceListener addLinkDeviceListener) {
		HouseKeeperActionSelectDeviceFragment.addLinkDeviceListener = addLinkDeviceListener;
	}

	public interface AddLinkDeviceListener {
		public void onAddLinkDeviceListenerChanged(List<AutoActionInfo> infos);
	}

	private class EditTextWatcher implements WLInputTextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			getSearchDevice(s.toString(), 10);
		}
	}

	private void getSearchDevice(final String searchKey, final int pageSize) {
		final List<WulianDevice> result = new ArrayList<WulianDevice>();
		if (StringUtil.isNullOrEmpty(searchKey) || avabilieDeviceList == null) {
			deviceAdapter.swapData(avabilieDeviceList);
		} else {
			TaskExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					Set<WulianDevice> allSet = new LinkedHashSet<WulianDevice>();
					String key = searchKey.toLowerCase().trim();
					boolean isOver = false;
					for (int i = 0; i < avabilieDeviceList.size(); i++) {
						WulianDevice device = avabilieDeviceList.get(i);
						String deviceName = DeviceTool
								.getDeviceShowName(device).toLowerCase().trim();
						if (StringUtil.isNullOrEmpty(deviceName))
							continue;
						if (Trans2PinYin.isFirstCharacter(key, deviceName)) {
							allSet.add(device);
						}
						if (allSet.size() >= pageSize) {
							isOver = true;
							break;
						}

					}
					if (!isOver) {
						for (int i = 0; i < avabilieDeviceList.size(); i++) {
							WulianDevice device = avabilieDeviceList.get(i);
							String deviceName = DeviceTool
									.getDeviceShowName(device).toLowerCase()
									.trim();
							if (StringUtil.isNullOrEmpty(deviceName))
								continue;
							if (Trans2PinYin.isStartPinYin(key, deviceName)) {
								allSet.add(device);
							}
							if (allSet.size() >= pageSize) {
								isOver = true;
								break;
							}
						}
					}
					if (!isOver) {
						for (int i = 0; i < avabilieDeviceList.size(); i++) {
							WulianDevice device = avabilieDeviceList.get(i);
							String deviceName = DeviceTool
									.getDeviceShowName(device).toLowerCase()
									.trim();
							if (StringUtil.isNullOrEmpty(deviceName))
								continue;
							if (Trans2PinYin.isContainsPinYin(key, deviceName)) {
								allSet.add(device);
							}
							if (allSet.size() >= pageSize) {
								isOver = true;
								break;
							}

						}
					}
					result.addAll(allSet);
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							deviceAdapter.swapData(result);
							// if(result != null && result.size() != 0){
							// searchListview.setVisibility(View.VISIBLE);
							// }
						}
					});
				}
			});

		}
	}
}
