package cc.wulian.smarthomev5.fragment.setting.router;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.minigateway.RemindUserGatewayDisconnectDialog;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.callback.router.KeyTools;
import cc.wulian.smarthomev5.callback.router.RouterDataCacheManager;
import cc.wulian.smarthomev5.callback.router.entity.GetRadioEntity;
import cc.wulian.smarthomev5.callback.router.entity.GetWifi_ifaceEntity;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.event.RouterZigbeeChannelEvent;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniRouterSettingActivity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class RouterWifiSetting2_4GChannelActivity extends EventBusActivity {
	private RouterDataCacheManager cacheManager = RouterDataCacheManager
			.getInstance();
	private GetWifi_ifaceEntity cur2_4gifaceEntity;
	private GetRadioEntity cur2_4gradio0;
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();
	private TextView gateway_remind_word_TV;
	private Boolean is_sxgateway = false;

	private String curChannel;
	private int curZigbeeChannel;
	private int hasChecked = 0;
	private View contentView;
	private ListView lvChannel;
	private RouterWifiSettingChannelAdapter mChannelAdapter;
	private List<String> lists2_4G = new ArrayList<String>();
	// 从网络寻找用于获取数字字符串的正则表达式
	private String regEx = "[^0-9]";
	private Pattern pattern = Pattern.compile(regEx);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		if (info != null && info.getGwVer() != null) {
			String gwver = info.getGwVer();
			if (gwver.length() >= 3) {
				is_sxgateway = (gwver.substring(2, 4) + "").equals("10");
			}
		}
		contentView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_channel, null);
		setContentView(contentView);
		initBar();
		init2_4GCacheData();
		contentView2_4GCreated();

	}

	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.about_back));
		getCompatActionBar().setTitle(
				getResources().getString(
						R.string.gateway_router_setting_wifi_channel));
	}

	// 初始化缓存数据,当前wifi数据对象和信道值以及信道1~13的初始化
	private void init2_4GCacheData() {
		if (cacheManager.getGet2_4GLists().get(0).getWifi_iface().size() == 1) {
			cur2_4gifaceEntity = cacheManager.getGet2_4GLists().get(0)
					.getWifi_iface().get(0);
		}
		if (cacheManager.getGet2_4GLists().get(0).getRadio0().size() == 1) {
			cur2_4gradio0 = cacheManager.getGet2_4GLists().get(0).getRadio0()
					.get(0);
			curChannel = cur2_4gradio0.getChannel();
		}
		curZigbeeChannel = cacheManager.getZigbeeChannel();
		for (int i = 1; i < 14; i++) {
			StringBuffer sb = new StringBuffer();
			sb.append(getResources().getString(
					R.string.gateway_router_setting_wifi_channel));
			sb.append(i);
			lists2_4G.add(sb.toString());

		}
		lists2_4G.add(getResources().getString(
				R.string.gateway_router_setting_wifi_channel_auto));

	}

	private void contentView2_4GCreated() {
		lvChannel = (ListView) contentView
				.findViewById(R.id.router_setting_wifi_channel_lv);
		mChannelAdapter = new RouterWifiSettingChannelAdapter(this, lists2_4G);
		lvChannel.setAdapter(mChannelAdapter);
		// 校验数据 部分数据出现"()" 等非法字符串,此处理方式并不成熟
		String str = pattern.matcher(curChannel).replaceAll("").trim();
		if (StringUtil.isNullOrEmpty(str) || "(".equals(curChannel)) {
			return;
		}
		refreshCheckedByChannel();

		// 判断竖型网关,channel

		if (is_sxgateway) {
			NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "1", "get", null);
			int Channel_int = Integer.parseInt(curChannel);
			gateway_remind_word_TV = (TextView) findViewById(R.id.device_router_channel_remind_word);
			if (Channel_int > 5) {
				gateway_remind_word_TV
						.setText(getResources()
								.getString(
										R.string.gateway_router_setting_channel_interference_router_hint));
				gateway_remind_word_TV.setVisibility(View.VISIBLE);
			} else {
				gateway_remind_word_TV.setVisibility(View.GONE);

			}
		}
	}

	// 刷新信道值
	private void refreshCheckedByChannel() {
		// 如果channel为0,表示为自动
		if (curChannel.equals("0")) {
			hasChecked = 13;
			lvChannel.setSelection(13);
		} else {
			hasChecked = StringUtil.toInteger(curChannel) - 1;
			lvChannel.setSelection(StringUtil.toInteger(curChannel) - 1);
		}

	}

	public class RouterWifiSettingChannelAdapter extends WLBaseAdapter<String> {

		public RouterWifiSettingChannelAdapter(Context context,
				List<String> data) {
			super(context, data);
		}

		private boolean isAllowClick = true;

		public void setIsAllowClick(boolean allowclick) {
			this.isAllowClick = allowclick;
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(
					R.layout.device_df_router_setting_wifi_channel_item, null);
		}

		@Override
		protected void bindView(Context context, final View view,
				final int pos, String item) {
			super.bindView(context, view, pos, item);
			TextView tvChannel = (TextView) view
					.findViewById(R.id.router_setting_wifi_channel_item_tv);
			ImageView ivChecked = (ImageView) view
					.findViewById(R.id.router_setting_wifi_channel_item_iv);
			tvChannel.setText(item);

			if (is_sxgateway) {
				if (pos < 5) {
					view.setBackgroundColor(getResources().getColor(
							R.color.v5_green_dark));
				} else {
					view.setBackgroundColor(getResources().getColor(
							R.color.v5_gray_light));
				}
			} else if (curZigbeeChannel != 0) {
				int zigbeeChannelPos = curZigbeeChannel - 1;
				if (zigbeeChannelPos == pos) {
					// zigbee信道标记为红色
					view.setBackgroundColor(Color.rgb(223, 59, 71));
					// zigbee信道前四个以及后四个标记为橙色
				} else if ((pos > zigbeeChannelPos && pos <= zigbeeChannelPos + 4)
						|| (pos < zigbeeChannelPos && pos >= zigbeeChannelPos - 4)) {
					view.setBackgroundColor(Color.rgb(244, 140, 34));

				} else {
					// 其他标记为绿色
					view.setBackgroundColor(getResources().getColor(
							R.color.v5_green_dark));
				}
			} else {
				view.setBackgroundColor(getResources().getColor(
						R.color.v5_green_dark));
			}

			if (this.isAllowClick) {
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String checkedChannel = null;
						hasChecked = pos;
						notifyDataSetChanged();
						Matcher matcher = pattern.matcher(lists2_4G.get(pos));
						checkedChannel = matcher.replaceAll("").trim();
						if (StringUtil.isNullOrEmpty(checkedChannel)) {
							checkedChannel = "0";
						}
						sendChange2_4GWifiChannelData(checkedChannel);
						RemindUserGatewayDisconnectDialog mdialog = new
								RemindUserGatewayDisconnectDialog(RouterWifiSetting2_4GChannelActivity.this);
						mdialog.remindUserGatewayRestart();
					}
				});
				// 为了校验当前被选择的item,类似于checkbox
				if (hasChecked == pos) {
					ivChecked.setVisibility(View.VISIBLE);
					if (is_sxgateway) {
						if (pos >= 5) {
							view.setBackgroundColor(getResources().getColor(
									R.color.red));
							gateway_remind_word_TV
							.setText(getResources()
									.getString(
											R.string.gateway_router_setting_channel_interference_router_hint));
							gateway_remind_word_TV.setVisibility(View.VISIBLE);
						}else{
							gateway_remind_word_TV.setVisibility(View.GONE);
						}
					}

					view.setSelected(true);
				} else {
					ivChecked.setVisibility(View.GONE);
					view.setSelected(false);
				}
			} else {
				view.setOnClickListener(null);
			}
		}
	}

	// 发送选择信道命令,未添加等待的dialog
	private void sendChange2_4GWifiChannelData(String checkedChannel) {
		if (cur2_4gifaceEntity == null || cur2_4gradio0 == null) {
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(KeyTools.key, cur2_4gifaceEntity.getKey());
			jsonObject.put(KeyTools.mode, cur2_4gifaceEntity.getMode());
			jsonObject.put(KeyTools.ssid, cur2_4gifaceEntity.getSsid());
			jsonObject.put(KeyTools.encryption,
					cur2_4gifaceEntity.getEncryption());
			jsonObject.put(KeyTools.disabled, cur2_4gradio0.getDisabled());
			jsonObject.put(KeyTools.channel, checkedChannel);
			jsonArray.add(0, jsonObject);
			NetSDK.sendSetRouterConfigMsg(
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdindexTools.CMDINDEX_4, jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void onEventMainThread(RouterWifiSettingEvent event) {

		if (RouterWifiSettingEvent.ACTION_REFRESH.equals(event.getAction())) {
			if (RouterWifiSettingEvent.TYPE_2_4G_WIFI.equals(event.getType())) {
				// 刷新当前数据对象,目的是在发送数据命令时,该数据对象能及时刷新
				cur2_4gifaceEntity = event.getWifi_ifaceList().get(0);
				cur2_4gradio0 = event.getRadioList().get(0);
				curChannel = cur2_4gradio0.getChannel();
				refreshCheckedByChannel();
				mChannelAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onEventMainThread(RouterZigbeeChannelEvent event) {
		if (RouterZigbeeChannelEvent.ACTION_REFRESH.equals(event.getAction())) {
			// 刷新zigbee信道值
			curZigbeeChannel = cacheManager.zigbeeToWifiChannel(event
					.getZigbeeChannel());
			mChannelAdapter.notifyDataSetInvalidated();
		}
	}

	public void onEventMainThread(MiniGatewayEvent gatewayevent)
			throws org.json.JSONException {
		if (!CmdUtil.MINIGATEWAY_GET_JUDGE_RELAY_SIGN.equals(gatewayevent
				.getCmdindex())) {
			return;
		}
		org.json.JSONObject jsonObject;
		org.json.JSONArray jsonArray;
		try {
			jsonArray = new org.json.JSONArray(gatewayevent.getData());
			jsonObject = jsonArray.getJSONObject(0);
			String wifiFlag = jsonObject.getString("setRepeaterFlag");
			if (wifiFlag.equals("0")) {
				mChannelAdapter.setIsAllowClick(true);
			} else if (wifiFlag.equals("1")) {
				mChannelAdapter.setIsAllowClick(false);
				mChannelAdapter.notifyDataSetChanged();

				int Channel_int = Integer.parseInt(curChannel);
				gateway_remind_word_TV = (TextView) findViewById(R.id.device_router_channel_remind_word);
				if (Channel_int > 5) {
					gateway_remind_word_TV
							.setText(getResources()
									.getString(
											R.string.gateway_router_setting_channel_interference_relay_hint));
					gateway_remind_word_TV.setVisibility(View.VISIBLE);
				} else {
					gateway_remind_word_TV.setVisibility(View.GONE);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
