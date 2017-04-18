package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BackMusicActivationActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.adapter.CommonDeviceAdapter;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.JoinGatewayEvent;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceGuideJoinGWFailActivity;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceGuideJoinGWLowActivity;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceGuideJoinGWSuccessActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.Config;

/**
 * 
 * @创作日期 2014-7-26
 */
public class DeviceCommonFragment extends WulianFragment {
	public static final int DEVICE_INIT_SIZE = 10;

	@ViewInject(R.id.device_common_switch_iv)
	private View deviceCommonSwitchIv;
	@ViewInject(R.id.device_area_switch_iv)
	private View deviceAreaSwitchIv;
	@ViewInject(R.id.device_function_switch_iv)
	private View deviceFunctionSwitchImageView;
	@ViewInject(R.id.device_ground_name)
	private TextView groupName;
	@ViewInject(R.id.device_common_list)
	private SwipeMenuListView deviceCommonList;
	private CommonDeviceAdapter deviceAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceAdapter = new CommonDeviceAdapter(mActivity, null);
		deviceAdapter.setMenuCreator(creatLeftDeleteItem());
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.device_common_container, container,
				false);
		ViewUtils.inject(this, v);
		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		groupName.setText(mApplication.getResources().getString(R.string.device_config_edit_dev_favority));
		deviceCommonList.setAdapter(deviceAdapter);
		initDeviceListViewListeners();
		deviceCommonSwitchIv.setSelected(true);
		deviceFunctionSwitchImageView.setSelected(false);
		deviceAreaSwitchIv.setSelected(false);
		deviceFunctionSwitchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2Device();
			}
		});
		deviceAreaSwitchIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2Common();
			}
		});
		deviceAreaSwitchIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch2Area();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		loadCommonDevices();
	}

	@Override
	public void onShow() {
		super.onShow();
		loadCommonDevices();
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.nav_device_title));
		getSupportActionBar().setRightIcon(R.drawable.common_use_add);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						new DeviceActionBarManager(mActivity).show(v);

					}
				});

	}

	public void switch2Device() {
		FragmentManager manager = mActivity.getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
				.getName());
		if (fragment != null) {
			NavigationFragment navFragment = (NavigationFragment) fragment;
			navFragment.changeFragement(DeviceFunctionFragment.class.getName());
		}
	}

	public void switch2Common() {
		FragmentManager manager = mActivity.getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
				.getName());
		if (fragment != null) {
			NavigationFragment navFragment = (NavigationFragment) fragment;
			navFragment.changeFragement(DeviceAreaFragment.class.getName());
		}

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

	public void jumpToJoinGWSuccessActivity() {
		Intent intent = new Intent();
		intent.setClass(mActivity, DeviceGuideJoinGWSuccessActivity.class);
		startActivity(intent);

	}

	public void jumpToJoinGWFailActivity() {
		Intent intent = new Intent();
		intent.setClass(mActivity, DeviceGuideJoinGWFailActivity.class);
		startActivity(intent);

	}

	public void jumpToJoinGWLowActivity() {
		Intent intent = new Intent();
		intent.setClass(mActivity, DeviceGuideJoinGWLowActivity.class);
		startActivity(intent);
//		startActivityForResult(intent, requestCode)
	}

	/**
	 * 创建左划删除item样式
	 */
	private SwipeMenuCreator creatLeftDeleteItem() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu,int position) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(mActivity, 90));
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}

	private void initDeviceListViewListeners() {
		// 左划删除
		deviceAdapter.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				WulianDevice item = getCommonDevice(mActivity).get(position);
				switch (index) {
				case 0:
					deleteCommonDeviceItem(item);
					break;
				}
			}
		});
		// 解决左划删除与右划菜单栏冲突
		deviceCommonList.setOnOpenOrCloseListener(new OpenOrCloseListener() {

			@Override
			public void isOpen(boolean isOpen) {
			}
		});
		deviceCommonList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showCommonDeviceDetails((WulianDevice) arg0.getAdapter()
						.getItem(arg2));
			}
		});
	}

	/**
	 * 删除相应的设备
	 * 
	 * @param searchDevice
	 */
	private void deleteCommonDeviceItem(final WulianDevice searchDevice) {
		if (searchDevice == null)
			return;
		FavorityDao favorityDao = FavorityDao.getInstance();
		FavorityEntity entity = new FavorityEntity();
		entity.setGwID(searchDevice.getDeviceGwID());
		entity.setOperationID(searchDevice.getDeviceID());
		favorityDao.delete(entity);
		loadCommonDevices();

	}

	// 进入常用设备详情
	public void showCommonDeviceDetails(WulianDevice device) {
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

	// 通过FavorityEntity从数据库加载常用设备,然后从设备缓存中获取设备对象,并排序
	public List<WulianDevice> getCommonDevice(Context context) {
		DeviceCache deviceCache = DeviceCache.getInstance(context);
		List<WulianDevice> devices = new ArrayList<WulianDevice>();
		if (deviceCache.size() < DEVICE_INIT_SIZE) {
			devices.addAll(deviceCache.getAllDevice());
		} else {
			FavorityEntity mEntity = new FavorityEntity();
			String gwID = mAccountManger.getmCurrentInfo().getGwID();
			mEntity.setGwID(gwID);
			mEntity.setType(Favority.TYPE_DEVICE);
			FavorityDao favorityDao = FavorityDao.getInstance();
			List<FavorityEntity> favorityEntities = favorityDao
					.findListAll(mEntity);
			for (FavorityEntity entity : favorityEntities) {
				WulianDevice device = deviceCache.getDeviceByID(context, gwID,
						entity.getOperationID());
				if (device != null) {
					devices.add(device);
				}
			}
		}
		Collections.sort(devices, DEVICE_COMPARATOR);
		return devices;
	}

	private void loadCommonDevices() {
		TaskExecutor.getInstance().executeDelay(new Runnable() {

			@Override
			public void run() {
				final List<WulianDevice> list = getCommonDevice(mActivity);
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						deviceAdapter.swapData(list);
					}
				});
			}
		},500);
	}

	// 离线即按设备类型排序
	private static final Comparator<WulianDevice> DEVICE_COMPARATOR = new Comparator<WulianDevice>() {
		@Override
		public int compare(WulianDevice lhs, WulianDevice rhs) {
			if (lhs.isDeviceOnLine() && !rhs.isDeviceOnLine()) {
				return -1;
			} else if (!lhs.isDeviceOnLine() && rhs.isDeviceOnLine()) {
				return 1;
			} else if (!lhs.isDeviceOnLine() && !rhs.isDeviceOnLine()) {
				int lType = StringUtil.toInteger(lhs.getDeviceType(), 16);
				int rType = StringUtil.toInteger(rhs.getDeviceType(), 16);
				return lType - rType;
			} else {
				return 0;
			}
		}

	};

	public void onEventMainThread(DeviceEvent event) {
		if (DeviceEvent.REFRESH.equals(event.action)
				|| DeviceEvent.REMOVE.equals(event.action)) {
			loadCommonDevices();
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
