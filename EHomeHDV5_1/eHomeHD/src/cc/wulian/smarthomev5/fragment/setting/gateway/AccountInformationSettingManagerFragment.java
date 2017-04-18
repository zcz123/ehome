package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.core.http.HttpProvider;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.IActivityCallerWithResult;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.event.GatewaInfoEvent;
import cc.wulian.smarthomev5.event.GatewayCityEvent;
import cc.wulian.smarthomev5.event.GatewayEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.AccountPermissionItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.fragment.setting.IntroductionItem;
import cc.wulian.smarthomev5.fragment.setting.MiniGatewayHeartSettingItem;
import cc.wulian.smarthomev5.fragment.setting.SXGatewayHeartSettingItem;
import cc.wulian.smarthomev5.fragment.setting.TimingSceneItem;
import cc.wulian.smarthomev5.fragment.setting.flower.DreamFlowerSettingItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.FlowerCloudItem;
import cc.wulian.smarthomev5.fragment.setting.permission.PermissionManagerItem;
import cc.wulian.smarthomev5.fragment.setting.router.MiniRouterSettingItem;
import cc.wulian.smarthomev5.fragment.setting.router.RouterSettingItem;
import cc.wulian.smarthomev5.fragment.setting.router.SXRouterSettingItem;
import cc.wulian.smarthomev5.fragment.setting.timezone.TimezoneSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.IOnActivityResultCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.PhotoSelector;
import cc.wulian.smarthomev5.service.html5plus.plugins.PhotoSelector.PhotoSelectCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.AreaList;
import cc.wulian.smarthomev5.tools.AreaList.OnAreaListItemClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.view.WLEditText;

public class AccountInformationSettingManagerFragment extends WulianFragment
		implements IActivityCallerWithResult {

	public static String PICTURE_GATEWAY_HEAD = "image_gateway_head.png";
	private IOnActivityResultCallback activityResultCallback;

	@ViewInject(R.id.setting_manager_lv)
	private ListView settingManagerListView;// 设置列表
	@ViewInject(R.id.head_portrait_circle_imageView)
	private ImageView HeadPortraitImageView;// 头像按钮
	@ViewInject(R.id.gateway_name_textview)
	private TextView gatewayName;// 网关名称
	@ViewInject(R.id.gateway_device_size_textview)
	private TextView gatewayDeviceSize;// 网关下设备数量
	@ViewInject(R.id.gateway_area_textview)
	private TextView gatewayArea;// 网关下设备数量

	private GateWayPasswordItem passwordItem;

	private WLEditText editText;
	private WLDialog dialog;

	
	private Preference preference = Preference.getPreferences();
	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();

	private DeviceCache deviceCache;
	private SettingManagerAdapter settingManagerAdapter;

	private FlowerCloudItem cloudItem;
	public static CharSequence GatewayName;

	private ViewGroup container = null;
	public boolean isMiniGateway = false;// is minigateway
	public boolean isSX_Gateway = false; // is sxgateway

	private LocationSettingItem locationSettingItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		settingManagerAdapter = new SettingManagerAdapter(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						// 如果是从html登陆的 返回的时候要返回到首页不能返回到html
						if ("true".equals(SmarthomeFeatureImpl.getData(
								SmarthomeFeatureImpl.Constants.IS_LOGIN, ""))) {
							Intent intent = new Intent(mActivity,
									MainHomeActivity.class);
							startActivity(intent);
						}
						mActivity.finish();
					}
				});

		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.gateway_control_center));
		getSupportActionBar().setIconText(R.string.nav_home_title);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.account_information_setting_content, container, false);
		View headVeiw = inflater.inflate(
				R.layout.gateway_information_header_ll, null);
		this.container = container;
		ViewUtils.inject(this, rootView);
		settingManagerListView.addHeaderView(headVeiw);
		ViewUtils.inject(this, headVeiw);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		settingManagerListView.setAdapter(settingManagerAdapter);
		/**
		 * 更换头像
		 */
		showHeadPortrait();

		HeadPortraitImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AccountInformationSettingManagerFragment me = AccountInformationSettingManagerFragment.this;
				PhotoSelector selector = new PhotoSelector(me, me
						.getHeadFullpathname());
				selector.setPhotoSelectCallback(new PhotoSelectCallback() {
					@Override
					public void doWhatOnSuccess(String path) {
						AccountInformationSettingManagerFragment.this
								.showHeadPortrait();
					}

					@Override
					public void doWhatOnFailed(Exception e) {
						AccountInformationSettingManagerFragment.this
								.showHeadPortrait();
					}
				});
				selector.iniPopupWidow(v);
			}
		});

		gatewayName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// add by yanzy:不允许被授权用户使用
				if (!UserRightUtil.getInstance().canDo(
						UserRightUtil.EntryPoint.GATEWAY_RENAME)) {
					return;
				}

				showChangeGatewayNameDialog();
			}
		});
		gatewayArea.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// add by yanzy:不允许被授权用户使用
				if (!UserRightUtil.getInstance().canDo(
						UserRightUtil.EntryPoint.GATEWAY_SET_ROOM)) {
					return;
				}

				final AreaList areaList = new AreaList(mActivity, true);
				areaList.setOnAreaListItemClickListener(new OnAreaListItemClickListener() {
					@Override
					public void onAreaListItemClicked(AreaList list, int pos,
							RoomInfo info) {
						NetSDK.setGatewayInfo(
								mAccountManger.getmCurrentInfo().getGwID(), "2",
								null, mAccountManger.getmCurrentInfo().getGwName(),
								info.getRoomID(), null, null, null,null,null);
						Logger.debug("info.getRoomID()  ==" + info.getRoomID());
						areaList.dismiss();
					}
				});
				areaList.show(getSupportActionBar().getCustomView());
			}
		});

	}

	private void showHeadPortrait() {
		try {
			Bitmap bm = BitmapFactory.decodeFile(getHeadFullpathname());
			if (bm != null) {
				HeadPortraitImageView.setImageBitmap(bm);
			} else {
				if (mAccountManger.getmCurrentInfo().isFlowerGatewayNoDisk()
						|| mAccountManger.getmCurrentInfo()
								.isFlowerGatewayWithDisk()) {
					HeadPortraitImageView
							.setImageResource(R.drawable.gateway_head_dream_flower);
				} else {
					HeadPortraitImageView
							.setImageResource(R.drawable.gateway_header_default);
				}
			}
		} catch (Exception e) {
			HeadPortraitImageView
					.setImageResource(R.drawable.gateway_header_default);
		}
	}

	private String getHeadFullpathname() {
		return FileUtil.getGatewayDirectoryPath(mAccountManger.getmCurrentInfo()
				.getGwID()) + "/" + PICTURE_GATEWAY_HEAD;
	}

	public void setGateWayName() {
		String showText = info.getGwID();
		if (!StringUtil
				.isNullOrEmpty(preference.getGateWayName(info.getGwID()))) {
			showText = preference.getGateWayName(info.getGwID());
			System.out.println("------>showText" + showText);
		}
		gatewayName.setText(showText);
	}

	@Override
	public void onShow() {
		super.onShow();
		initBar();
		loadData();
	}

	@Override()
	public void onResume() {
		super.onResume();
		gateWayInfoGwVerGet();
		loadData();
		// mini网关获取当前网关中继是否已配置
		if (isMiniGateway) {// mini网关
			NetSDK.sendMiniGatewayWifiSettingMsg(
					mAccountManger.getmCurrentInfo().getGwID(), "1", "get", null);
		}
	}

	private void loadData() {
		mActivity.runOnUiThread(new Runnable() {

			public void run() {
				initSettingItems();
				changeDeviceSize();
				showHeadPortrait();
				loadGatewayName(accountManager.getmCurrentInfo().getGwID());
				laodGatewayArea();
			}

		});
	}

	/**
	 * 设置列表添加
	 */
	private void initSettingItems() {
		final List<AbstractSettingItem> items = new ArrayList<AbstractSettingItem>();
		EmptyItem emptyItem0 = new EmptyItem(mActivity);
		emptyItem0.initSystemState();
		EmptyItem emptyItem1 = new EmptyItem(mActivity);
		emptyItem1.initSystemState();
		EmptyItem emptyItem2 = new EmptyItem(mActivity);
		emptyItem2.initSystemState();
		EmptyItem emptyItem3 = new EmptyItem(mActivity);
		emptyItem3.initSystemState();
		EmptyItem emptyItem4 = new EmptyItem(mActivity);
		emptyItem4.initSystemState();

		// 网关ID
		final GateWayIDItem idItem = new GateWayIDItem(mActivity);
		idItem.initSystemState();
		idItem.setLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				CloneGatewayItem cloneGatewayItem=new CloneGatewayItem(mActivity);
				cloneGatewayItem.initSystemState();
				items.add(2,cloneGatewayItem);
				settingManagerAdapter.swapData(items);
				idItem.setLongClickListener(null);
				return true;
			}
		});
		GateWayUpdaeItem versionItem = new GateWayUpdaeItem(mActivity);
		versionItem.initSystemState();
		GateWayIPItem ipItem = new GateWayIPItem(mActivity);
		ipItem.initSystemState();
		// 修改密码
		passwordItem = new GateWayPasswordItem(mActivity);
		passwordItem.initSystemState();
		// 切换网关
		SwitchGateWayItem mSwitchGateWayItem = new SwitchGateWayItem(mActivity);
		mSwitchGateWayItem.initSystemState();
		String gwType=info.getGwType();
		PermissionManagerItem permissionManagerItem = new PermissionManagerItem(
				mActivity);
		permissionManagerItem.initSystemState();
		AccountPermissionItem accountPermissionItem = new AccountPermissionItem(
				mActivity);
		accountPermissionItem.initSystemState();
		// 退出网关设置
		ExitGatewaySettingItem exitItem = new ExitGatewaySettingItem(mActivity);
		exitItem.initSystemState();
		items.add(emptyItem0);
		items.add(idItem);
		items.add(versionItem);
		if (StringUtil.isInnerIP(mAccountManger.getmCurrentInfo().getGwSerIP())) {
			items.add(ipItem);
		}
		// mini网关
		if (isMiniGateway) {
			// mini网关中继设置
			MiniGatewayHeartSettingItem miniGatewayHeartSettingItem = new MiniGatewayHeartSettingItem(
					mActivity);
			miniGatewayHeartSettingItem.initSystemState();
		
			MiniRouterSettingItem minirouterSettingItem = new MiniRouterSettingItem(
					mActivity);
			minirouterSettingItem.initSystemState();

			items.add(miniGatewayHeartSettingItem);
			items.add(emptyItem4);
			items.add(minirouterSettingItem);
		}

		if (isSX_Gateway) {
			// SX网关中继设置
			SXGatewayHeartSettingItem sxGatewayHeartSettingItem = new SXGatewayHeartSettingItem(
					mActivity);
			sxGatewayHeartSettingItem.initSystemState();
			SXRouterSettingItem sxrouterSettingItem = new SXRouterSettingItem(
					mActivity);
			sxrouterSettingItem.initSystemState();
			
			items.add(sxGatewayHeartSettingItem);
			items.add(emptyItem4);
			items.add(sxrouterSettingItem);
		}
		items.add(passwordItem);
		items.add(mSwitchGateWayItem);
		if(!StringUtil.isNullOrEmpty(gwType)){
			if(gwType.equals("1")){
				// 管理网关
				ManagerGateWayItem managerGateWayItem = new ManagerGateWayItem(mActivity);
				managerGateWayItem.initSystemState();
				items.add(managerGateWayItem);
			}
		}
		// 参考物联ACS接口服务协议文档
		// items.add(permissionManagerItem);
		if ("true".equals(SmarthomeFeatureImpl.getData(
				SmarthomeFeatureImpl.Constants.IS_LOGIN, ""))) {
			items.add(accountPermissionItem);
		}
		items.add(emptyItem1);

		boolean isUpgrade = Preference.getPreferences().getBoolean(
				IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
		TimingSceneItem timingSceneItem = new TimingSceneItem(mActivity);
		timingSceneItem.initSystemState();
		IntroductionItem timingSceneIntroductionIteem = new IntroductionItem(
				mActivity);
		timingSceneIntroductionIteem.setIntroductionStr(mApplication
				.getResources()
				.getString(R.string.set_timing_secne_description));
		timingSceneIntroductionIteem.initSystemState();

		items.add(timingSceneItem);
		items.add(timingSceneIntroductionIteem);
		// 时区设置
		if (isUpgrade) {
			TimezoneSettingItem timeZoneSettingItem = new TimezoneSettingItem(
					mActivity);
			timeZoneSettingItem.initSystemState();
			items.add(timeZoneSettingItem);
		}

		if (mAccountManger.getmCurrentInfo().isFlowerGatewayNoDisk()
				|| mAccountManger.getmCurrentInfo().isFlowerGatewayWithDisk()) {
			// 梦想之花设置
			DreamFlowerSettingItem dreamFlowerSettingItem = new DreamFlowerSettingItem(
					mActivity);
			dreamFlowerSettingItem.initSystemState();
			// 路由器设置
			RouterSettingItem routerSettingItem = new RouterSettingItem(
					mActivity);
			routerSettingItem.initSystemState();
			items.add(dreamFlowerSettingItem);
			items.add(emptyItem2);

			items.add(routerSettingItem);
			items.add(emptyItem3);
			// 云网盘
			cloudItem = new FlowerCloudItem(mActivity);
			cloudItem.initSystemState();
			if (mAccountManger.getmCurrentInfo().isFlowerGatewayWithDisk()) {
				items.add(cloudItem);
				items.add(emptyItem4);
				getDiskUsedInfo();
			}
			GateWaySpecificationLinkItem gatewaySpecificationItem = new GateWaySpecificationLinkItem(
					mActivity);
			gatewaySpecificationItem.initSystemState();
			items.add(gatewaySpecificationItem);
		}
		if(LanguageUtil.isChina()){
			locationSettingItem = new LocationSettingItem(mActivity);
			locationSettingItem.initSystemState();
			items.add(locationSettingItem);
		}
		items.add(exitItem);
		settingManagerAdapter.swapData(items);
	}

	// 获取网盘使用情况
	private void getDiskUsedInfo() {
		String gwId = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
		JSONObject msgBody = new JSONObject();
		msgBody.put("cmd", ConstUtil.CMD_PRIVATE_CLOUD);
		msgBody.put(ConstUtil.KEY_CMD_INDEX, "9");
		msgBody.put(ConstUtil.KEY_GW_ID, gwId);
		msgBody.put("data", "");
		NetSDK.sendDevMsg(gwId, msgBody);
	}

	private void gateWayInfoGwVerGet() {
		if(info!=null&&info.getGwVer()!=null&&!StringUtil.isNullOrEmpty(info.getGwVer())){
			String gwVer = info.getGwVer();
			String verType = gwVer.charAt(2) + "";
			String sx_verType = gwVer.substring(2,4)+"";
			if (verType.equals("8")) {
				isMiniGateway = true;
			}else if(sx_verType.equals("10")){
				isSX_Gateway = true;
			}
		}else{
			
		}
		
	}
	

	/**
	 * <h1>显示修改网关名的对话框</h1>
	 */
	private void showChangeGatewayNameDialog() {
		final WLDialog.Builder builder = new Builder(mActivity);
		View rootView = inflater.inflate(R.layout.gateway_name_change_dialog,
				this.container, false);
		final TextView lengthPromptText = (TextView) rootView
				.findViewById(R.id.gateway_name_length_prompt_text);
		editText = (WLEditText) rootView
				.findViewById(R.id.gateway_name_change_edit_text);
		editText.setText(gatewayName.getText());

		builder.setContentView(rootView)
				.setTitle(R.string.set_account_manager_gw_name_modify_titel)
				.setPositiveButton(R.string.common_ok)
				.setNegativeButton(R.string.cancel)
				.setWidthPercent((float) (3.0 / 4))
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						String etGatewayName = editText.getText().toString();
						if (!StringUtil.isNullOrEmpty(etGatewayName)
								&& etGatewayName.length() > 20) {
							lengthPromptText.setVisibility(View.VISIBLE);
							builder.setDismissAfterDone(false);
						} else {
							lengthPromptText.setVisibility(View.GONE);
							modifyGateWayUserId(etGatewayName);
							builder.setDismissAfterDone(true);
						}

					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}

				});
		dialog = builder.create();
		dialog.show();
	}

	private void loadGatewayName(String gwID) {
		gatewayName.setText(mAccountManger.getGatewayName(gwID));
	}

	private void laodGatewayArea() {
		DeviceAreaEntity areaEntity = AreaGroupManager.getInstance()
				.getDeviceAreaEntity(accountManager.getmCurrentInfo().getGwID(),
						accountManager.getmCurrentInfo().getGwRoomID());
		if (areaEntity != null) {
			gatewayArea.setText(areaEntity.getName());
		} else {
			gatewayArea
					.setText(mActivity
							.getString(R.string.device_config_edit_dev_area_type_other_default));
		}
	}

	/**
	 * 修改网关名称
	 * 
	 * @param gateWayName
	 */
	private void modifyGateWayUserId(String gateWayName) {
		if (StringUtil.isNullOrEmpty(gateWayName)) {
			WLToast.showToast(
					mActivity,
					mApplication
							.getResources()
							.getString(
									R.string.set_account_manager_gw_name_modify_not_empty),
					WLToast.TOAST_SHORT);
			return;
		}
		preference.saveGateWayName(info.getGwID(), gateWayName);
		NetSDK.setGatewayInfo(info.getGwID(), "2", null, gateWayName,
				info.getGwRoomID(), null, null, null,null,null);
	}

	/**
	 * 显示设备数量
	 */
	private void changeDeviceSize() {
		String hintStr = mApplication.getResources().getString(
				R.string.device_config_device_search_hint);
		hintStr = String.format(hintStr, deviceCache.size());
		gatewayDeviceSize.setText(hintStr);
	}

	/**
	 * 设备刷新，修改设备数量
	 * 
	 * @param event
	 */
	public void onEventMainThread(DeviceEvent event) {
		changeDeviceSize();
	}

	/**
	 * 网关时数据刷新，修改网关名称
	 * 
	 * @param event
	 */
	public void onEventMainThread(GatewaInfoEvent event) {
		if (CmdUtil.MODE_UPD.equals(event.getMode())) {
			loadGatewayName(event.getGwID());
			laodGatewayArea();
			setNet(event.getGwID());
		}
	}

	/**
	 *实时刷新 网关 City
	 * @param event
     */
	public void onEventMainThread(GatewayCityEvent event){
		String mode = event.getMode();
		String gwID = event.getGwID();
		if(StringUtil.equals(mode , "2") && StringUtil.equals(gwID ,mAccountManger.getmCurrentInfo().getGwID())){
			locationSettingItem.setCity();
		}
	}

	private void setNet(final String gwID) {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				JSONObject params = new JSONObject();
				params.put("deviceId", gwID);
				params.put("deviceAlias", mAccountManger.getGatewayName(gwID));
				System.out.println("---------------------->"
						+ params.toString());
				String url = "https://v2.wuliancloud.com:52182/AMS/user/device";
				Map<String, String> headerMap = new HashMap<String, String>();
				headerMap.put("token", SmarthomeFeatureImpl.getData("token"));
				headerMap.put("cmd", "deviceUpdate");
				byte[] body = params.toString().getBytes();
				HttpProvider httpProvider = HttpManager.getDefaultProvider();
				JSONObject json = httpProvider.post(url, headerMap, body);
				System.out.println("-------------" + "json" + json.toString());
			}
		});

	}

	/**
	 * 连接网关
	 */
	public void onEventMainThread(GatewayEvent event) {
		if (GatewayEvent.ACTION_CHANGE_PWD.equals(event.action)) {
			if (event.result != 0) {
				WLToast.showToast(
						mActivity,
						resources
								.getString(R.string.set_account_manager_modify_gw_password_fail),
						WLToast.TOAST_SHORT);
			}
		}
	}

	// 云网盘实用信息
	public void onEventMainThread(FlowerEvent event) {
		if (FlowerEvent.ACTION_FLOWER_HARD_DISK_INFO.equals(event.getAction())) {
			cloudItem.setDiskUsedInfo(event.getEventStr());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (activityResultCallback != null) {
			activityResultCallback.doWhatOnActivityResult(requestCode,
					resultCode, data);
		}
	}

	@Override
	public void setOnActivityResultCallback(IOnActivityResultCallback callBack) {
		activityResultCallback = callBack;
	}

	@Override
	public Context getMyContext() {
		return this.getActivity();
	}

	@Override
	public void myStartActivityForResult(Intent intent, int requestCode) {
		this.startActivityForResult(intent, requestCode);
	}

}
