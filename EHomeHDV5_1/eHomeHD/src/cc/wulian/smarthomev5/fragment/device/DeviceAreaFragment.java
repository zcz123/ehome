/**
 * xiaozhi 2014-7-26
 */
package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.AreaGroupAdapter;
import cc.wulian.smarthomev5.adapter.IconChooseAdapter;
import cc.wulian.smarthomev5.adapter.WLOperationAdapter.MenuItem;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.IconResourceEntity;
import cc.wulian.smarthomev5.event.JoinGatewayEvent;
import cc.wulian.smarthomev5.event.RoomEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.CountDownTimer;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.IconChooseView;
import cc.wulian.smarthomev5.view.UpdateProcessDialog;
import cc.wulian.smarthomev5.view.IconChooseView.OnIconClickListener;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

/**
 * 区域分组fragment
 * 
 * @创作日期 2014-7-26
 */
public class DeviceAreaFragment extends WulianFragment {

	@ViewInject(R.id.device_common_switch_iv)
	private View deviceCommonSwitchIv;
	@ViewInject(R.id.device_area_switch_iv)
	private View deviceAreaSwitchIv;
	@ViewInject(R.id.device_function_switch_iv)
	private View deviceFunctionSwitchImageView;
	@ViewInject(R.id.device_ground_name)
	private TextView groupName;
	@ViewInject(R.id.area_group_container_gv)
	private GridView gridView;
	private AreaGroupAdapter areaGroupAdapter;
	// private Preference preference = Preference.getPreferences();
	private UpdateProcessDialog defaultAreasCreatingProgressDialog;
	private Map<String, Integer> AreaHomeSettingPositionMap = new HashMap<String, Integer>();
	private List<String> AreaHomeSettingList = new ArrayList<String>();
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();

	// private static String clickOk = "0";
	// private static String clickCancel = "-1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		areaGroupAdapter = new AreaGroupAdapter(mActivity,
				new ArrayList<DeviceAreaEntity>(), null);
		initBar();
	}

	// 创建gridview默认的添加Item
	public MenuItem createDefaultAddItemView() {
		MenuItem item1 = new MenuItem() {

			@Override
			public View getView() {
				LinearLayout itemView = (LinearLayout) inflater.inflate(
						R.layout.area_group_item, null);
				TextView countTextView = (TextView) itemView
						.findViewById(R.id.fragement_device_area_grid_item_device_count);
				countTextView.setVisibility(View.INVISIBLE);
				TextView nameTextView = (TextView) itemView
						.findViewById(R.id.fragement_device_area_grid_item_area_name);
				nameTextView.setText(mApplication.getResources().getString(
						R.string.add));
				ImageView itemImageView = (ImageView) itemView
						.findViewById(R.id.area_group_background_iv);
				itemImageView.setImageResource(R.drawable.area_add_big);
				return itemView;
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if (!UserRightUtil.getInstance().canDo(
						UserRightUtil.EntryPoint.ROOM_ADD)) {
					return;
				}

				addAreaEntity();
			}
		};
		return item1;
	}

	// 定制item
	public MenuItem createDefaultCustomizedItemView() {
		MenuItem item2 = new MenuItem() {

			@Override
			public View getView() {
				LinearLayout itemView = (LinearLayout) inflater.inflate(
						R.layout.area_group_item, null);
				TextView countTextView = (TextView) itemView
						.findViewById(R.id.fragement_device_area_grid_item_device_count);
				countTextView.setVisibility(View.INVISIBLE);
				TextView nameTextView = (TextView) itemView
						.findViewById(R.id.fragement_device_area_grid_item_area_name);
				nameTextView.setText(mApplication.getResources().getString(
						R.string.device_area_order));
				ImageView itemImageView = (ImageView) itemView
						.findViewById(R.id.area_group_background_iv);
				itemImageView.setImageResource(R.drawable.area_common_big);
				return itemView;
			}

			@Override
			public void doSomething() {
				showCreateDefaultAreasDialog();
			}
		};
		return item2;
	}

	// 进入时初始化添加和定制的item
	public void initMenuItems() {
		menuItems.clear();
		// 根据区域大小显示menuItems
		if (AreaGroupManager.getInstance().getDeviceAreaEnties().size() == 1) {
			menuItems.add(createDefaultAddItemView());
			menuItems.add(createDefaultCustomizedItemView());
		} else {
			menuItems.add(createDefaultAddItemView());
		}
		// 根据btn选择显示menuItems
		// if (StringUtil.isNullOrEmpty(preference.getAreaGroupHomeSetting())) {
		// return;
		// }
		// if (clickOk.equals(preference.getAreaGroupHomeSetting())) {
		// menuItems.add(createDefaultAddItemView());
		// } else if (clickCancel.equals(preference.getAreaGroupHomeSetting()))
		// {
		// menuItems.add(createDefaultAddItemView());
		// menuItems.add(createDefaultCustomizedItemView());
		// }
		areaGroupAdapter.swapMenuItem(menuItems);

	}

	public void addAreaEntity() {
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
		final IconChooseView chooseView = new IconChooseView(mActivity,
				areaIconBlackList);
		chooseView.setInputHintTextContent(mActivity.getResources().getString(
				R.string.device_config_edit_dev_area));
		chooseView.setSelectedChangedBackgroundColor(false);
		chooseView.setSelectedChangedImageDrawable(true);
		chooseView.setOnItemClickListener(new OnIconClickListener() {

			@Override
			public void onIconClick(IconResourceEntity entity) {
				String mAreaName = chooseView.getInputTextContent();
				if (StringUtil.isNullOrEmpty(mAreaName)) {
					String str = DeviceTool.getDefaultAreaTextByIconIndex(
							mActivity, entity.iconkey);
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
					chooseView.setError(mActivity
							.getText(R.string.device_area_not_null_hint));
				} else if (StringUtil.isNullOrEmpty(chooseView.getCheckedItem()
						+ "")) {
					WLToast.showToast(mActivity, mActivity.getResources()
							.getString(R.string.area_icon_choose),
							WLToast.TOAST_SHORT);
				} else {
					IconResourceEntity iconEntity =chooseView.getCheckedItem();
					SendMessage.sendSetRoomMsg(mActivity,AccountManager.getAccountManger().getmCurrentInfo().getGwID(),CmdUtil.MODE_ADD, null, areaName, iconEntity.iconkey+"");
				}
			}

			@Override
			public void onClickNegative(View contentViewLayout) {
			}

		}
		AddSceneListener addSceneListener = new AddSceneListener();
		WLDialog.Builder builder = new Builder(mActivity);
		builder.setTitle(R.string.device_edit_area_add)
				.setContentView(chooseView)
				.setHeightPercent(0.6F)
				.setPositiveButton(
						mActivity.getResources().getString(R.string.common_ok))
				.setNegativeButton(
						mActivity.getResources().getString(R.string.cancel))
				.setListener(addSceneListener);
		builder.create().show();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.device_area_group_container,
				container, false);
		ViewUtils.inject(this, v);
		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		groupName.setText(mApplication.getResources().getString(
				R.string.device_area_group));
		deviceAreaSwitchIv.setSelected(true);
		deviceFunctionSwitchImageView.setSelected(false);
		deviceCommonSwitchIv.setSelected(false);
		gridView.setAdapter(areaGroupAdapter);
		initMenuItems();
		deviceFunctionSwitchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2Device();
			}
		});
		deviceCommonSwitchIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2Common();
			}
		});
		checkCreateDefaultAreaes();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadAllAreas();
	}

	@Override
	public void onShow() {
		super.onShow();
		initBar();
		loadAllAreas();
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
			navFragment.changeFragement(DeviceCommonFragment.class.getName());
		}
	}

	/**
	 * 加载所有区域
	 */
	public void loadAllAreas() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				final List<DeviceAreaEntity> areas = AreaGroupManager
						.getInstance().getDeviceAreaEnties();
				for (DeviceAreaEntity entity : areas) {
					entity.clearDevices();
				}
				for (WulianDevice device : DeviceCache.getInstance(mActivity)
						.getAllDevice()) {
					int count = 0;
					for (int i = 0; i < areas.size(); i++) {
						DeviceAreaEntity entity = areas.get(i);
						if (entity.getRoomID().equals(device.getDeviceRoomID())) {
							entity.addDevice(device.getDeviceInfo());
							break;
						}
						count++;
					}
					if (count > 0 && count == areas.size()) {
						DeviceAreaEntity detaultEntity = AreaGroupManager
								.getInstance().getDefaultAreaEntity();
						detaultEntity.addDevice(device.getDeviceInfo());
					}
				}
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						areaGroupAdapter.swapData(areas);
						initMenuItems();
					}
				});
			}
		});
	}

	private void checkCreateDefaultAreaes() {
		// 第一次进入 key为空,区域模块只有一个默认区域
		List<DeviceAreaEntity> list = AreaGroupManager.getInstance()
				.getDeviceAreaEnties();
		boolean isConnected = AccountManager.getAccountManger().isConnectedGW();
		if (list.size() == 1 && isConnected) {
			showCreateDefaultAreasDialog();
		}
	}

	private void showCreateDefaultAreasDialog() {
		// 进入过该界面后将-1存入sharepreference,默认用户已经进入过该界面
		final WLDialog.Builder builder = new WLDialog.Builder(getActivity());
		builder.setTitle(mApplication.getResources().getString(
				R.string.device_config_edit_dev_area_type_customization));
		// 布局为两行三列
		builder.setContentView(createDefaultAreasDialogContentView(2, 3));
		builder.setPositiveButton(android.R.string.ok);
		builder.setDismissAfterDone(false);
		builder.setNegativeButton(mApplication.getResources().getString(
				R.string.guide_skip));
		builder.setListener(new MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				// preference.saveAreaGroupHomeSetting(clickOk);
				menuItems.clear();
				// 有户型定制Item消失
				menuItems.add(createDefaultAddItemView());
				areaGroupAdapter.swapMenuItem(menuItems);
				// 带上当前dialog对象,在确定后需检查是否有户型,在用户选择过户型后该dialog才消失,否则不消失
				sendAllData(builder);
				AreaHomeSettingPositionMap.clear();
				AreaHomeSettingList.clear();

			}

			@Override
			public void onClickNegative(View contentViewLayout) {
				AreaHomeSettingPositionMap.clear();
				AreaHomeSettingList.clear();
				// preference.saveAreaGroupHomeSetting(clickCancel);
				menuItems.clear();
				menuItems.add(createDefaultAddItemView());
				menuItems.add(createDefaultCustomizedItemView());
				areaGroupAdapter.swapMenuItem(menuItems);
			}

		});
		WLDialog defaultAreasSettingDialog = builder.create();
		defaultAreasSettingDialog.show();
	}

	/**
	 * 向Dialog添加布局 内容
	 */
	public View createDefaultAreasDialogContentView(int row, int column) {
		View linearView = View.inflate(mActivity,
				R.layout.device_area_home_setting_parent, null);
		LinearLayout parentView = (LinearLayout) linearView
				.findViewById(R.id.area_home_setting_content);
		parentView.removeAllViews();
		for (int i = 0; i < row; i++) {
			LinearLayout rowLinearLayout = new LinearLayout(mActivity);
			LinearLayout.LayoutParams rowparams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLinearLayout.setGravity(Gravity.CENTER);
			rowLinearLayout.setPadding(5, 5, 5, 5);
			rowLinearLayout.setLayoutParams(rowparams);

			for (int j = 0; j < column; j++) {
				int rowIndex = i * 3 + (j + 1);
				LinearLayout columnLinearLayout = new LinearLayout(mActivity);
				LinearLayout.LayoutParams columnParams = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				columnLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				columnLinearLayout.setGravity(Gravity.CENTER);
				columnParams.weight = 1;
				columnLinearLayout.setLayoutParams(columnParams);

				// 从list获取ItemView,注意此处数目为6;
				if ((rowIndex - 1) <= getAllItemView().size()) {
					LinearLayout itemView = getAllItemView().get(rowIndex - 1)
							.getView();
					if (itemView != null) {
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT);
						lp.weight = 1;
						itemView.setGravity(Gravity.CENTER_HORIZONTAL);
						itemView.setLayoutParams(lp);
						columnLinearLayout.addView(itemView);
					}
				}
				rowLinearLayout.addView(columnLinearLayout);
			}
			parentView.addView(rowLinearLayout);
		}
		return linearView;
	}

	/**
	 * 发送所有数据
	 */
	public void sendAllData(WLDialog.Builder builder) {
		for (AreaHomeSettingItemView itemView : getAllItemView()) {
			createDefaultAreaNameByItemView(itemView);
		}
		if (AreaHomeSettingList.isEmpty()) {
			WLToast.showToast(
					mActivity,
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_type_customization_hint),
					WLToast.TOAST_SHORT);
			return;
		}
		builder.setDismissAfterDone(true);
		Collections.reverse(AreaHomeSettingList);
		defaultAreasCreatingProgressDialog = new UpdateProcessDialog(
				getActivity());
		defaultAreasCreatingProgressDialog.show();
		defaultAreasSettingDialogTimer.start();

		for (String str : AreaHomeSettingList) {
			String areaIconID = DeviceTool.getDefaultRoomIconID(mActivity, str);
			try {
				// 添加等待防止一次发送数据过多导致传输过程部分数据丢失
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			SendMessage.sendSetRoomMsg(getActivity(),
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdUtil.MODE_ADD, null, str, areaIconID);
		}
	}

	/**
	 * 初始化Item的相关数据,存入缓存
	 * 
	 * 根据卧室数以及客厅数,生成相应的区域名
	 * 
	 * @param itemView
	 */
	public void createDefaultAreaNameByItemView(AreaHomeSettingItemView itemView) {
		String homeItemName = itemView.getmTextView();
		String benroom = mApplication.getResources().getString(
				R.string.device_config_edit_dev_area_benroom);
		String livingRoom = mApplication.getResources().getString(
				R.string.device_config_edit_dev_area_type_living_room);
		int homeItemNumber = 0;
		// 卧室名初始化
		if (homeItemName.equals(benroom)) {
			String[] benroomArrays = {
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_master_benroom),
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_second_benroom),
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_childen_benroom),
					mApplication.getResources().getString(
							R.string.device_config_edit_dev_area_study_benroom) };
			if (AreaHomeSettingPositionMap.get(homeItemName) != null) {
				homeItemNumber = AreaHomeSettingPositionMap.get(benroom);
				if (homeItemNumber == 1) {
					AreaHomeSettingList.add(benroom);
				} else if (homeItemNumber > 1
						&& homeItemNumber <= benroomArrays.length) {
					for (int i = 0; i < homeItemNumber; i++) {
						AreaHomeSettingList.add(benroomArrays[i]);
					}
				} else if (homeItemNumber > benroomArrays.length) {
					int moreBenroom = homeItemNumber - benroomArrays.length;
					for (String str : benroomArrays) {
						AreaHomeSettingList.add(str);
					}
					for (int i = 0; i < moreBenroom; i++) {
						AreaHomeSettingList.add(benroom + (i + 1));
					}

				}

			}

			// 客厅名初始化
		} else if (homeItemName.equals(livingRoom)) {
			String[] livingRoomArrays = {
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_type_living_room),
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_type_restaurant),
					mApplication
							.getResources()
							.getString(
									R.string.device_config_edit_dev_area_type_multifunction) };
			if (AreaHomeSettingPositionMap.get(homeItemName) != null) {
				homeItemNumber = AreaHomeSettingPositionMap.get(livingRoom);
				if (homeItemNumber <= livingRoomArrays.length) {
					for (int i = 0; i < homeItemNumber; i++) {
						AreaHomeSettingList.add(livingRoomArrays[i]);
					}
				} else if (homeItemNumber > livingRoomArrays.length) {
					int moreLivingRoom = homeItemNumber
							- livingRoomArrays.length;
					for (String str : livingRoomArrays) {
						AreaHomeSettingList.add(str);
					}
					for (int i = 0; i < moreLivingRoom; i++) {
						AreaHomeSettingList.add(livingRoom + (i + 1));
					}

				}

			}
			// 其他区域名初始化,如阳台1.2.3等
		} else {
			if (AreaHomeSettingPositionMap.get(homeItemName) != null) {
				homeItemNumber = AreaHomeSettingPositionMap.get(itemView
						.getmTextView());
			}
			if (homeItemNumber > 0 && homeItemNumber < 7) {
				if (homeItemNumber == 1) {
					AreaHomeSettingList.add(homeItemName);
				} else {
					for (int i = 0; i < homeItemNumber; i++) {
						AreaHomeSettingList.add(homeItemName + (i + 1));
					}
				}
			}
		}

	}

	/**
	 * 刷新Dialog
	 */
	public void refreshProgressDialog() {
		if (defaultAreasCreatingProgressDialog == null)
			return;
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				int backItemSize = AreaGroupManager.getInstance()
						.getDeviceAreaEnties().size();
				int settingItemSize = AreaHomeSettingList.size();
				int progress = (int) (backItemSize * 100.0 / settingItemSize);
				if (progress <= 100) {
					defaultAreasCreatingProgressDialog.setProgess(progress);
				}
				if (progress >= 100) {
					defaultAreasCreatingProgressDialog.dismiss();
					defaultAreasSettingDialogTimer.cancel();
				}
			}
		});

	}

	/**
	 * 计时器,在限定时间内让defaultAreasCreatingProgressDialog消失,目前设置为超时20s
	 */
	CountDownTimer defaultAreasSettingDialogTimer = new CountDownTimer(20000,
			1000) {

		@Override
		public void onTick(long millisUntilFinished) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFinish() {
			defaultAreasCreatingProgressDialog.setProgess(100);
			defaultAreasCreatingProgressDialog.dismiss();
		}
	};

	/**
	 * 创建Item对象实例 ,存入list
	 * 
	 * @return
	 */
	public List<AreaHomeSettingItemView> getAllItemView() {
		List<AreaHomeSettingItemView> viewList = new ArrayList<AreaHomeSettingItemView>();
		for (final RoomInfo info : DeviceTool.getDefaultRoomInfos(mActivity)) {
			AreaHomeSettingItemView wView = new AreaHomeSettingItemView(
					mActivity) {

				@Override
				public void setText() {
					mTextView.setText(info.getName());
				}
			};
			viewList.add(wView);
		}
		return viewList;
	}

	public void onEventMainThread(RoomEvent event) {
		loadAllAreas();
		refreshProgressDialog();
	}

	public void onEventMainThread(JoinGatewayEvent event) {
		if (event != null) {
			ProgressDialogManager dialogManager = ProgressDialogManager
					.getDialogManager();
			dialogManager.dimissDialog(
					DeviceActionBarManager.KEY_JOIN_GW_DIALOG, 0);
		}

	}

	private class NumeberAdapter extends BaseAdapter {
		int mHeight = 50;
		String[] mData = null;

		public NumeberAdapter(String[] data) {
			mHeight = (int) DisplayUtil.dip2Pix(getActivity(), mHeight);
			this.mData = data;
		}

		@Override
		public int getCount() {
			return (null != mData) ? mData.length : 0;
		}

		@Override
		public String getItem(int arg0) {
			return mData[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WheelTextView textView = null;

			if (null == convertView) {
				textView = new WheelTextView(getActivity());
				textView.setLayoutParams(new TosGallery.LayoutParams(-1,
						mHeight));
				textView.setTextSize(20);
				textView.setGravity(Gravity.CENTER);
				textView.setTextColor(mApplication.getResources().getColor(
						R.color.black));
			} else {
				textView = (WheelTextView) convertView;
			}

			String text = getItem(position);
			textView.setTextSize(20);
			textView.setText(text);
			return textView;
		}
	}

	public abstract class AreaHomeSettingItemView {
		public WheelView mWheelView;
		public TextView mTextView;
		public LinearLayout linearLayout;
		private NumeberAdapter mAdapter;
		public LayoutInflater inflater;
		private String[] number = { "0", "1", "2", "3", "4", "5", "6" };
		private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(TosAdapterView<?> parent, View view,
					int position, long id) {
				((WheelTextView) view).setTextSize(30);
				int index = StringUtil.toInteger(view.getTag().toString());
				int count = parent.getChildCount();
				if (index < count - 1) {
					((WheelTextView) parent.getChildAt(index + 1))
							.setTextSize(20);
				}
				if (index > 0) {
					((WheelTextView) parent.getChildAt(index - 1))
							.setTextSize(20);
				}
				// 此处将Item的区域名称作为Key,解决无法在多个wheelview中无法获取当前对象的text问题,通过在回调中获取可以解决这一问题
				AreaHomeSettingPositionMap.put(getmTextView(), position);
			}

			@Override
			public void onNothingSelected(TosAdapterView<?> parent) {

			}

		};

		public String getmTextView() {
			return (String) mTextView.getText();
		}

		public AreaHomeSettingItemView(Context context) {
			inflater = LayoutInflater.from(context);
			linearLayout = (LinearLayout) inflater.inflate(
					R.layout.device_area_home_setting_item, null);
			mWheelView = (WheelView) linearLayout
					.findViewById(R.id.area_wheel_view_item);
			mWheelView.setScrollCycle(true);
			mAdapter = new NumeberAdapter(number);
			mWheelView.setAdapter(mAdapter);
			mWheelView.setSelection(0, true);
			((WheelTextView) mWheelView.getSelectedView()).setTextSize(30);
			mWheelView.setOnItemSelectedListener(mListener);
			mWheelView.setUnselectedAlpha(0.5f);

			mTextView = (TextView) linearLayout
					.findViewById(R.id.area_item_text_view);
			setText();
		}

		public abstract void setText();

		public LinearLayout getView() {
			return linearLayout;
		}

	}

}
