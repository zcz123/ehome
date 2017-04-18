package cc.wulian.smarthomev5.fragment.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.more.gps.GPSFragment;
import cc.wulian.smarthomev5.fragment.more.gps.GPSItem;
import cc.wulian.smarthomev5.fragment.more.littlewhite.LittleWhiteItem;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCItem;
import cc.wulian.smarthomev5.fragment.more.route.RouteRemindItem;
import cc.wulian.smarthomev5.fragment.more.shake.ShakeItem;
import cc.wulian.smarthomev5.fragment.more.wifi.WifiItem;
import cc.wulian.smarthomev5.fragment.setting.IntroductionItem;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MoreManagerFragment extends WulianFragment {

	@ViewInject(R.id.more_items_lv)
	private ListView moreManagerListView;
	private SettingManagerAdapter moreManagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		moreManagerAdapter = new SettingManagerAdapter(mActivity);
		initBar();
	}

	private void initItemsView() {
		/*
		 * GPSItem gpsItem = new GPSItem(mActivity); gpsItem.initSystemState();
		 * moreManagerAdapter.addSettingItem(gpsItem);
		 */
		/**
		 * NFC设置
		 */
		NFCItem nfcItem = new NFCItem(mActivity);
		nfcItem.initSystemState();
		moreManagerAdapter.addSettingItem(nfcItem);
		//下方提示信息
		IntroductionItem nfcIntroductionIteem = new IntroductionItem(mActivity);
		nfcIntroductionIteem.setIntroductionStr(mApplication.getResources().getString(
				R.string.more_nfc_introduction));
		nfcIntroductionIteem.initSystemState();
		moreManagerAdapter.addSettingItem(nfcIntroductionIteem);
		
		/**
		 * 摇一摇设置
		 */
		ShakeItem shakeItem = new ShakeItem(mActivity);
		shakeItem.initSystemState();
		moreManagerAdapter.addSettingItem(shakeItem);
		IntroductionItem shakeIntroductionIteem = new IntroductionItem(
				mActivity);
		shakeIntroductionIteem.setIntroductionStr(mApplication.getResources().getString(
				R.string.more_shake_introduction));
		shakeIntroductionIteem.initSystemState();
		moreManagerAdapter.addSettingItem(shakeIntroductionIteem);
		
		/**
		 * WIFI设置
		 */
		WifiItem wifiItem = new WifiItem(mActivity);
		wifiItem.initSystemState();
		moreManagerAdapter.addSettingItem(wifiItem);
		IntroductionItem wifiIntroductionIteem = new IntroductionItem(mActivity);
		wifiIntroductionIteem.setIntroductionStr(mApplication.getResources().getString(
				R.string.more_wifi_introduction));
		wifiIntroductionIteem.initSystemState();
		moreManagerAdapter.addSettingItem(wifiIntroductionIteem);
		//信道检测。。。。此版本暂且不发   V5.2.8
//		RouteRemindItem routeChanelCheckingItem = new RouteRemindItem(mActivity);
//		routeChanelCheckingItem.initSystemState();
//		moreManagerAdapter.addSettingItem(routeChanelCheckingItem);
		
		/**
		 * GPS场景功能
		 */
		GPSItem gpsItem = new GPSItem(mActivity);
		gpsItem.initSystemState();
//		moreManagerAdapter.addSettingItem(gpsItem);
		IntroductionItem gpsIntroductionIteem = new IntroductionItem(mActivity);
		gpsIntroductionIteem.setIntroductionStr(mApplication.getResources().getString(
				R.string.more_gps_introduction));
		gpsIntroductionIteem.initSystemState();
//		moreManagerAdapter.addSettingItem(gpsIntroductionIteem);
		//信道检测。。。。
//		RouteRemindItem routeChanelCheckingItem = new RouteRemindItem(mActivity);
//		routeChanelCheckingItem.initSystemState();
//		moreManagerAdapter.addSettingItem(routeChanelCheckingItem);
//		IntroductionItem RouteRemindIntroductionIteem = new IntroductionItem(mActivity);
//		RouteRemindIntroductionIteem.setIntroductionStr(mApplication.getResources().getString(R.string.more_route_tips));
//		RouteRemindIntroductionIteem.initSystemState();
//		moreManagerAdapter.addSettingItem(RouteRemindIntroductionIteem);
		//扫一扫 跟随pad版一起 先关闭
		ScanQRCodeItem scanQRCodeItem=new ScanQRCodeItem(mActivity);
		scanQRCodeItem.initSystemState();
		moreManagerAdapter.addSettingItem(scanQRCodeItem);
		IntroductionItem scanQRCodeIntroductionIteem = new IntroductionItem(mActivity);
		scanQRCodeIntroductionIteem.setIntroductionStr(mApplication.getResources().getString(R.string.gateway_explore_scanning_hint));
		scanQRCodeIntroductionIteem.initSystemState();
		moreManagerAdapter.addSettingItem(scanQRCodeIntroductionIteem);
		//第三方登录
		LittleWhiteItem littleWhiteItem = new LittleWhiteItem(mActivity);
		littleWhiteItem.initSystemState();
		moreManagerAdapter.addSettingItem(littleWhiteItem);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.more_manager_content,
				container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.more_titel_Laboratory));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		moreManagerListView.setAdapter(moreManagerAdapter);
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				initItemsView();
			}
		});
	}

	@Override
	public void onShow() {
		super.onShow();
		initBar();
	}

}
