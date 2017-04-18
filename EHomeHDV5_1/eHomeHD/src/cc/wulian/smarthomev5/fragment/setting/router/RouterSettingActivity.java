package cc.wulian.smarthomev5.fragment.setting.router;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.callback.router.KeyTools;
import cc.wulian.smarthomev5.callback.router.RouterDataCacheManager;
import cc.wulian.smarthomev5.callback.router.entity.BlackAndWhiteEntity;
import cc.wulian.smarthomev5.callback.router.entity.DeviceInfo;
import cc.wulian.smarthomev5.event.RouterBWModeEvent;
import cc.wulian.smarthomev5.event.RouterBlackListEvent;
import cc.wulian.smarthomev5.event.RouterDevcieInfoEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class RouterSettingActivity extends EventBusActivity {
	private static final String BLACK_LIST_TYPE_0 = "0";
	private static final String KEY_PROGRESS_DIALOG_DEVICE = "KEY_PROGRESS_DIALOG_DEVICE";
	private static final String KEY_PROGRESS_DIALOG_STATUS = "KEY_PROGRESS_DIALOG_STATUS";
	private static final String KEY_PROGRESS_DIALOG_GET_BLACK = "KEY_PROGRESS_DIALOG_GET_BLACK";
	private static final String KEY_PROGRESS_DIALOG_DELETE_BLACK = "KEY_PROGRESS_DIALOG_DELETE_BLACK";
	private View contentView;
	private TextView totalSpeed;
	private TextView downloadSpeed;
	private TextView uploadSpeed;
	private TextView tvCurMode;
	private TextView tvConnectedDev;
	private ListView connectedDevicelv;
	private RouterConnectedDeviceAdapter mConnectedDeviceAdapter;
	private SwipeMenuListView blackListlv;
	private RouterBlackListAdapter mBlackListAdapter;
	private List<BlackAndWhiteEntity> blackLists = new ArrayList<BlackAndWhiteEntity>();
	private List<DeviceInfo> deviceLists = new ArrayList<DeviceInfo>();
	private RouterDataCacheManager routerDataCache = RouterDataCacheManager
			.getInstance();
	private ProgressDialogManager progressDialog = ProgressDialogManager.getDialogManager();
	private Runnable refreshRunnable=new Runnable() {
		//刷新设备列表
		@Override
		public void run() {
			NetSDK.sendGetRouterConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),CmdindexTools.CMDINDEX_1);
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		contentView = inflater.inflate(
				R.layout.device_df_router_setting_fragment, null);
		this.setContentView(contentView);
		initBar();
		initCacheData();
		contentViewCreated();
	}

	// 初始化数据缓存,从routerDataCache中获取,数据只有一条,放在list中通过get(0)获取
	private void initCacheData() {
		if (routerDataCache.getBalckLists().size() == 1) {
			blackLists = routerDataCache.getBalckLists().get(0).getList();
		}
		if (routerDataCache.getDeviceInfos().size() == 1) {
			deviceLists = routerDataCache.getDeviceInfos().get(0).getInfo();
		}
		
		NetSDK.sendGetRouterConfigMsg(
				AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
				CmdindexTools.CMDINDEX_3);
		progressDialog.showDialog(KEY_PROGRESS_DIALOG_DEVICE, this, null, null);
		if (StringUtil.isNullOrEmpty(routerDataCache.getCurMode())) {
			NetSDK.sendGetRouterConfigMsg(
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdindexTools.CMDINDEX_11);
			progressDialog.showDialog(KEY_PROGRESS_DIALOG_STATUS, this, null,
					null);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//定时刷新设备列表
		TaskExecutor.getInstance().addScheduled(refreshRunnable, 1000, 10000, TimeUnit.MILLISECONDS);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		TaskExecutor.getInstance().removeScheduled(refreshRunnable);
	}
	
	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.about_back));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.gateway_router_setting));
		getCompatActionBar().setDisplayShowMenuTextEnabled(true);
		getCompatActionBar().setRightIconText(
				getResources().getString(R.string.set_titel));
		getCompatActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(RouterSettingActivity.this, RouterWifiSettingActivity.class);
						startActivity(intent);

					}
				});
	}

	public void contentViewCreated() {
		totalSpeed = (TextView) contentView
				.findViewById(R.id.router_setting_speed_total);
		downloadSpeed = (TextView) contentView
				.findViewById(R.id.router_setting_speed_download);
		uploadSpeed = (TextView) contentView
				.findViewById(R.id.router_setting_speed_upload);
		tvCurMode = (TextView) contentView
				.findViewById(R.id.router_setting_black_tv);
		tvConnectedDev = (TextView) contentView
				.findViewById(R.id.router_setting_connected_device_tv);
		connectedDevicelv = (ListView) contentView
				.findViewById(R.id.router_setting_connected_device_list);
		blackListlv = (SwipeMenuListView) contentView
				.findViewById(R.id.router_setting_black_list);

		mConnectedDeviceAdapter = new RouterConnectedDeviceAdapter(this,
				deviceLists);
		connectedDevicelv.setAdapter(mConnectedDeviceAdapter);
		setListViewHeightBasedOnChildren(connectedDevicelv);
		totalsSpeed(deviceLists);

		if (deviceLists.size() != 0) {
			tvConnectedDev.setText(getResources().getString(
					R.string.gateway_router_setting_connected_device));
		} else {
			tvConnectedDev.setText(getResources().getString(
					R.string.gateway_router_setting_connected_device_toast));
		}
		if (CmdindexTools.SET_DATA_0.equals(routerDataCache.getCurMode())
				|| CmdindexTools.SET_DATA_1
						.equals(routerDataCache.getCurMode())) {
			tvCurMode.setText(getResources().getString(
					R.string.gateway_router_setting_white_list));
			blackListlv.setVisibility(View.GONE);
		} else if (CmdindexTools.SET_DATA_2
				.equals(routerDataCache.getCurMode())) {
			tvCurMode.setText(getResources().getString(
					R.string.gateway_router_setting_black_list));
			blackListlv.setVisibility(View.VISIBLE);
			// 如果是黑名单模式,则请求黑名单
			NetSDK.sendGetRouterConfigMsg(
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdindexTools.CMDINDEX_14);
			progressDialog.showDialog(KEY_PROGRESS_DIALOG_GET_BLACK, this,
					null, null);

		}
		mBlackListAdapter = new RouterBlackListAdapter(this, blackLists);
		blackListlv.setAdapter(mBlackListAdapter);
		setListViewHeightBasedOnChildren(blackListlv);
		mBlackListAdapter.setMenuCreator(creatLeftDeleteItem());
		initBlackListViewListeners();

	}

	private void initBlackListViewListeners() {
		// 左划删除
		mBlackListAdapter
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						BlackAndWhiteEntity item = blackLists.get(position);
						switch (index) {
						case 0:
							blackLists.remove(position);
							mBlackListAdapter.swapData(blackLists);
							deleteBlackListItem(item);
							break;
						}
					}

				});
		// 解决左划删除与右划菜单栏冲突
		blackListlv.setOnOpenOrCloseListener(new OpenOrCloseListener() {

			@Override
			public void isOpen(boolean isOpen) {
			}
		});
	}

	// 删除黑名单
	private void deleteBlackListItem(BlackAndWhiteEntity item) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KeyTools.name, item.getName());
			jsonObject.put(KeyTools.mac, item.getMac());
			jsonObject.put(KeyTools.type, BLACK_LIST_TYPE_0);
			jsonArray.add(0, jsonObject);
			NetSDK.sendSetRouterConfigMsg(
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdindexTools.CMDINDEX_14, jsonArray);
			progressDialog.showDialog(KEY_PROGRESS_DIALOG_DELETE_BLACK, this,
					null, null);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// 侧滑删除
	private SwipeMenuCreator creatLeftDeleteItem() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu,int position) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						RouterSettingActivity.this);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(
						RouterSettingActivity.this, 90));
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}

	// 统计上传速度、下载速度以及总速度速度数据
	public void totalsSpeed(List<DeviceInfo> list) {
		int upTotals = 0;
		int downTotals = 0;
		for (DeviceInfo entity : list) {

			upTotals += entity.getUp();
			downTotals += entity.getDown();
		}
		StringBuffer sb1 = new StringBuffer();
		sb1.append(upTotals);
		sb1.append("kb/s");
		uploadSpeed.setText(sb1);
		StringBuffer sb2 = new StringBuffer();
		sb2.append(downTotals);
		sb2.append("kb/s");
		downloadSpeed.setText(sb2);
		int totals = upTotals + downTotals;
		StringBuffer sb3 = new StringBuffer();
		sb3.append(totals);
		sb3.append("kb/s");
		totalSpeed.setText(sb3);
	}

	/**
	 * 解决ScollView中嵌套ListView显示不全问题
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	public void onEventMainThread(RouterDevcieInfoEvent event) {
		if (RouterDevcieInfoEvent.ACTION_REFRESH.equals(event.getAction())) {
			mConnectedDeviceAdapter.swapData(event.getList());
			setListViewHeightBasedOnChildren(connectedDevicelv);
			totalsSpeed(event.getList());
			if (event.getList().size() != 0) {
				tvConnectedDev.setText(getResources().getString(
						R.string.gateway_router_setting_connected_device));
			}
			progressDialog.dimissDialog(KEY_PROGRESS_DIALOG_DEVICE, 0);
		}
	}

	public void onEventMainThread(RouterBWModeEvent event) {
		if (RouterBWModeEvent.ACTION_REFRESH.equals(event.getAction())) {
			// 黑白名单无效或白名单模式
			if (RouterBWModeEvent.CUR_MODEL_0.equals(event.getMode())
					|| RouterBWModeEvent.CUR_MODEL_1.equals(event.getMode())) {
				tvCurMode.setText(getResources().getString(
						R.string.gateway_router_setting_white_list));
				blackListlv.setVisibility(View.GONE);
				// 黑名单模式
			} else if (RouterBWModeEvent.CUR_MODEL_2.equals(event.getMode())) {
				tvCurMode.setText(getResources().getString(
						R.string.gateway_router_setting_black_list));
				blackListlv.setVisibility(View.VISIBLE);

			}
			progressDialog.dimissDialog(KEY_PROGRESS_DIALOG_STATUS, 0);
		}
	}

	public void onEventMainThread(RouterBlackListEvent event) {
		if (RouterBlackListEvent.ACTION_REFRESH.equals(event.getAction())) {
			blackLists = event.getList();
			mBlackListAdapter.swapData(blackLists);
			progressDialog.dimissDialog(KEY_PROGRESS_DIALOG_GET_BLACK, 0);
			progressDialog.dimissDialog(KEY_PROGRESS_DIALOG_DELETE_BLACK, 0);
		}

	}

}
