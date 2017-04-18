package cc.wulian.smarthomev5.fragment.setting.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
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
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class RouterWifiSetting5GChannelActivity extends EventBusActivity {
	private RouterDataCacheManager cacheManager = RouterDataCacheManager
			.getInstance();
	private GetWifi_ifaceEntity cur5gifaceEntity;
	private GetRadioEntity cur5gradio1;

	private String curChannel;
	private int hasChecked = 0;
	private View contentView;
	private ListView lvChannel;
	private RouterWifiSettingChannelAdapter mChannelAdapter;
	private List<String> lists5G = new ArrayList<String>();
	private Map<String, Integer> channelKeys = new HashMap<String, Integer>();

	// 用于获取数字字符串的正则表达式
	private String regEx = "[^0-9]";
	private Pattern pattern = Pattern.compile(regEx);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		contentView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_channel, null);
		setContentView(contentView);
		initBar();

		init5GCacheData();
		contentView5GCreated();

	}

	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.set_titel));
		getCompatActionBar().setTitle(
				getResources().getString(
						R.string.gateway_router_setting_wifi_channel));
	}

	private void init5GCacheData() {

		if (cacheManager.getGet5GLists().get(0).getWifi_iface().size() == 1) {
			cur5gifaceEntity = cacheManager.getGet5GLists().get(0)
					.getWifi_iface().get(0);
		}
		if (cacheManager.getGet5GLists().get(0).getRadio1().size() == 1) {
			cur5gradio1 = cacheManager.getGet5GLists().get(0).getRadio1()
					.get(0);
			curChannel = cur5gradio1.getChannel();
		}

		final int a = 36;
		final int b = 149;
		for (int i = 0; i < 8; i++) {
			StringBuffer sb = new StringBuffer();
			sb.append(getResources().getString(
					R.string.gateway_router_setting_wifi_channel));
			sb.append(a + i * 4);
			lists5G.add(sb.toString());
		}
		for (int i = 0; i < 5; i++) {
			StringBuffer sb = new StringBuffer();
			sb.append(getResources().getString(
					R.string.gateway_router_setting_wifi_channel));
			sb.append(b + i * 4);
			lists5G.add(sb.toString());
		}
		lists5G.add(getResources().getString(
				R.string.gateway_router_setting_wifi_channel_auto));

		channelKeys.put("36", 0);
		channelKeys.put("40", 1);
		channelKeys.put("44", 2);
		channelKeys.put("48", 3);
		channelKeys.put("52", 4);
		channelKeys.put("56", 5);
		channelKeys.put("60", 6);
		channelKeys.put("64", 7);
		channelKeys.put("149", 8);
		channelKeys.put("153", 9);
		channelKeys.put("157", 10);
		channelKeys.put("161", 11);
		channelKeys.put("165", 12);
		channelKeys.put("0", 13);

	}

	private void contentView5GCreated() {
		lvChannel = (ListView) contentView
				.findViewById(R.id.router_setting_wifi_channel_lv);
		mChannelAdapter = new RouterWifiSettingChannelAdapter(this, lists5G);
		lvChannel.setAdapter(mChannelAdapter);
		if (curChannel != null) {
			// 校验数据 部分数据出现"("情况
			String str = pattern.matcher(curChannel).replaceAll("").trim();
			if (StringUtil.isNullOrEmpty(str) || "(".equals(curChannel)) {
				return;
			}
			refreshCheckedByChannel();
		}
	}

	// 刷新checked信道值
	private void refreshCheckedByChannel() {
		lvChannel.setSelection(channelKeys.get(curChannel));
		hasChecked = channelKeys.get(curChannel);
	}

	public class RouterWifiSettingChannelAdapter extends WLBaseAdapter<String> {

		public RouterWifiSettingChannelAdapter(Context context,
				List<String> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(
					R.layout.device_df_router_setting_wifi_channel_item, null);
		}

		@Override
		protected void bindView(Context context, View view, final int pos,
				String item) {
			super.bindView(context, view, pos, item);
			TextView tvChannel = (TextView) view
					.findViewById(R.id.router_setting_wifi_channel_item_tv);
			ImageView ivChecked = (ImageView) view
					.findViewById(R.id.router_setting_wifi_channel_item_iv);
			tvChannel.setText(item);
			view.setBackgroundColor(getResources().getColor(
					R.color.v5_green_dark));
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String checkedChannel = null;
					hasChecked = pos;
					notifyDataSetChanged();
					Matcher matcher = pattern.matcher(lists5G.get(pos));
					checkedChannel = matcher.replaceAll("").trim();
					if (StringUtil.isNullOrEmpty(checkedChannel)) {
						checkedChannel = "0";
					}
					sendChange5GWifiChannelData(checkedChannel);
					RemindUserGatewayDisconnectDialog mdialog = new
							RemindUserGatewayDisconnectDialog(RouterWifiSetting5GChannelActivity.this);
					mdialog.remindUserGatewayRestart();
				}
			});
			if (hasChecked == pos) {
				ivChecked.setVisibility(View.VISIBLE);
				view.setSelected(true);

			} else {
				ivChecked.setVisibility(View.GONE);
				view.setSelected(false);

			}

		}
	}

	private void sendChange5GWifiChannelData(String checkedChannel) {
		if (cur5gifaceEntity == null || cur5gradio1 == null) {
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(KeyTools.key, cur5gifaceEntity.getKey());
			jsonObject.put(KeyTools.mode, cur5gifaceEntity.getMode());
			jsonObject.put(KeyTools.ssid, cur5gifaceEntity.getSsid());
			jsonObject.put(KeyTools.encryption,
					cur5gifaceEntity.getEncryption());
			jsonObject.put(KeyTools.disabled, cur5gradio1.getDisabled());
			jsonObject.put(KeyTools.channel, checkedChannel);
			jsonArray.add(0, jsonObject);
			NetSDK.sendSetRouterConfigMsg(
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdindexTools.CMDINDEX_5, jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void onEventMainThread(RouterWifiSettingEvent event) {
		if (RouterWifiSettingEvent.ACTION_REFRESH.equals(event.getAction())) {
			if (RouterWifiSettingEvent.TYPE_5G_WIFI.equals(event.getType())) {
				cur5gifaceEntity = event.getWifi_ifaceList().get(0);
				cur5gradio1 = event.getRadioList().get(0);
				curChannel = cur5gradio1.getChannel();
				refreshCheckedByChannel();
				mChannelAdapter.notifyDataSetChanged();
			}
		}
	}
}
