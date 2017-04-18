package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
import cc.wulian.smarthomev5.tools.AccountManager;

/**
 * Created by WIN7 on 2014/7/11.
 */
public class RouteRemindWifiAdapter extends WLBaseAdapter<WifiInfoEntity> {
	
	private AccountManager accountManager = AccountManager.getAccountManger();
	public RouteRemindWifiAdapter(Context context, List<WifiInfoEntity> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		View newView = inflater.inflate(R.layout.route_remind_wifi_item, parent, false);
		return newView;
	}

	@Override
	protected void bindView(Context context, View view, int pos, WifiInfoEntity item) {
		
		RelativeLayout routeRemindLayout = (RelativeLayout) view.findViewById(R.id.route_remind_wifi_item_layout);
		TextView wifiSSID = (TextView) view.findViewById(R.id.more_wifi_ssid);
		TextView wifiLevel = (TextView) view.findViewById(R.id.more_wifi_level);
		TextView wifiChannel = (TextView) view.findViewById(R.id.more_wifi_chanl);
		TextView wifiCapabilities = (TextView) view.findViewById(R.id.more_wifi_capabilities);
		TextView wifiBssid = (TextView) view.findViewById(R.id.more_wifi_bssid);
		TextView wifiTip = (TextView) view.findViewById(R.id.more_wifi_tip_tv);
		ImageView wifiImgView = (ImageView) view.findViewById(R.id.more_wifi_imgview);
		wifiSSID.setText(item.getSsid());
		wifiLevel.setText(item.getLevel()+" dBm");
		wifiChannel.setText("chan:"+item.getChanel());
		wifiCapabilities.setText(item.getCapabilities());
		wifiBssid.setText(item.getBSSID());
		wifiImgView.setImageResource(R.drawable.account_information_router_wifi_icon);
		if(WifiInfoEntity.TYPE_CLASH.equals(item.getType())){
			wifiTip.setVisibility(View.VISIBLE);
//			wifiTip.setText("信道有冲突，建议更改路由的信道，保证信号互不干扰");
			wifiTip.setText(R.string.more_route_clash);
			wifiChannel.setTextColor(Color.parseColor("#FF0000"));
		}else if(WifiInfoEntity.TYPE_WARNING.equals(item.getType())){
			wifiTip.setVisibility(View.VISIBLE);
			wifiTip.setText(R.string.more_route_warning);
			wifiChannel.setTextColor(Color.parseColor("#E6E61A"));			
		}else{
			wifiTip.setVisibility(View.INVISIBLE);
			wifiChannel.setTextColor(Color.parseColor("#222222"));
		}
		
		
	}

	public static class WifiInfoEntity implements Comparable<WifiInfoEntity>{
		public static final String TYPE_CLASH = "1";
		public static final String TYPE_WARNING = "2";
		public static final String TYPE_NORMAL = "3";
		private String ssid;
		private String type;
		private String chanel;
		private String capabilities;
		private String BSSID;
		private String level;
		public String getCapabilities() {
			return capabilities;
		}
		public void setCapabilities(String capabilities) {
			this.capabilities = capabilities;
		}
		public String getBSSID() {
			return BSSID;
		}
		public void setBSSID(String bSSID) {
			BSSID = bSSID;
		}
		
		public String getSsid() {
			return ssid;
		}
		public void setSsid(String ssid) {
			this.ssid = ssid;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getChanel() {
			return chanel;
		}
		public void setChanel(String chanel) {
			this.chanel = chanel;
		}
		public String getLevel() {
			return level;
		}
		public void setLevel(String level) {
			this.level = level;
		}
		/**
		 * 根据频率获得信道
		 * 
		 * @param frequency
		 * @return
		 */
		public static int getChannelByFrequency(int frequency) {
			int channel = frequency;
			switch (frequency) {
			case 2412:
				channel = 1;
				break;
			case 2417:
				channel = 2;
				break;
			case 2422:
				channel = 3;
				break;
			case 2427:
				channel = 4;
				break;
			case 2432:
				channel = 5;
				break;
			case 2437:
				channel = 6;
				break;
			case 2442:
				channel = 7;
				break;
			case 2447:
				channel = 8;
				break;
			case 2452:
				channel = 9;
				break;
			case 2457:
				channel = 10;
				break;
			case 2462:
				channel = 11;
				break;
			case 2467:
				channel = 12;
				break;
			case 2472:
				channel = 13;
				break;
			case 2484:
				channel = 14;
				break;
			case 5180:
				channel = 36;
				break;
				
			case 5200:
				channel = 40;
				break;
			case 5220:
				channel = 44;
				break;
			case 5240:
				channel = 48;
				break;
			case 5260:
				channel = 52;
				break;
			case 5280:
				channel = 56;
				break;
			case 5300:
				channel = 60;
				break;
			case 5320:
				channel = 64;
				break;
			case 5500:
				channel = 100;
				break;
			case 5520:
				channel = 104;
				break;
			case 5540:
				channel = 108;
				break;
			case 5560:
				channel = 112;
				break;
			case 5580:
				channel = 116;
				break;
			case 5600:
				channel = 120;
				break;
			case 5620:
				channel = 124;
				break;
			case 5640:
				channel = 128;
				break;
			case 5660:
				channel = 132;
				break;
			case 5680:
				channel = 136;
				break;
			case 5700:
				channel = 140;
				break;
			case 5745:
				channel = 149;
				break;
			case 5765:
				channel = 153;
				break;
			case 5785:
				channel = 157;
				break;
			case 5805:
				channel = 161;
				break;
			case 5825:
				channel = 165;
				break;
			}
			return channel;
		}
		@Override
		public int compareTo(WifiInfoEntity another) {
//			if(WifiInfoEntity.TYPE_ZIBEE.equals(this.type))
//				return -1;
//			else 
			if(StringUtil.toInteger(this.chanel) == StringUtil.toInteger(AccountManager.getAccountManger().getmCurrentInfo().getGwChanel()))
				return -1;
			else if(StringUtil.toInteger(this.chanel) > StringUtil.toInteger(another.getChanel()))
				return 1;
			return -1;
		}
		
	}
}