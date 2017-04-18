package cc.wulian.smarthomev5.fragment.setting;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.tools.InstalServiceToolItem;
import cc.wulian.smarthomev5.fragment.setting.voice.AlarmItem;
import cc.wulian.smarthomev5.fragment.setting.voice.PushItem;
import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SettingManagerFragment extends WulianFragment {

	@ViewInject(R.id.setting_manager_lv)
	private ListView settingManagerListView;
	private SettingManagerAdapter settingManagerAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		settingManagerAdapter = new SettingManagerAdapter(mActivity);
	}
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.set_titel));
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.setting_content, container,
				false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		settingManagerListView.setAdapter(settingManagerAdapter);
	}
	@Override
	public void onShow() {
		super.onShow();
		initBar();
		initSettingItems();
	}
	@Override()
	public void onResume() {
		super.onResume();
		initSettingItems();
	}

	private void initSettingItems() {
		final List<AbstractSettingItem> items = new ArrayList<AbstractSettingItem>();
		EmptyItem emptyItem1 = new EmptyItem(mActivity);
		emptyItem1.initSystemState();
		EmptyItem emptyItem2 = new EmptyItem(mActivity);
		emptyItem2.initSystemState();
		EmptyItem emptyItem4 = new EmptyItem(mActivity);
		emptyItem4.initSystemState();

		AlarmItem alarmItem = new AlarmItem(mActivity);
		alarmItem.initSystemState();

		PushItem pushItem = new PushItem(mActivity);
		pushItem.initSystemState();
		
		/*GateWayUpdaeItem updateItem = new GateWayUpdaeItem(mActivity);
		updateItem.initSystemState();
		accountManagerAdapter.addSettingItem(updateItem);*/
		VersionItem versionItem = new VersionItem(mActivity);
		versionItem.initSystemState();
		items.add(alarmItem);
		//安装服务工具
		final InstalServiceToolItem instalServiceToolItem = new InstalServiceToolItem(mActivity);
		instalServiceToolItem.initSystemState();

		versionItem.setIconClickListener(new VersionItem.IconClickListener() {
			@Override
			public void onIconClick() {
				if(!items.contains(instalServiceToolItem)){
					items.add(instalServiceToolItem);
					settingManagerAdapter.swapData(items);
				}
			}
		});
		if(isPushItemShow()){
			items.add(pushItem);
		}
		items.add(emptyItem1);
		items.add(versionItem);
		items.add(emptyItem4);
		if(Preference.getPreferences().getInstalServiceToolActivity()){
			items.add(instalServiceToolItem);
		}
		settingManagerAdapter.swapData(items);
	}

	private boolean isPushItemShow(){
		String enterType = Preference.getPreferences().getUserEnterType();
		String pushType= SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_PUSH_TYPE);
		String appToken=SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_APP_TOKEN);
		boolean isFCMConnected=enterType.equals("account")&&(!StringUtil.isNullOrEmpty(appToken))&&pushType.equals(WelcomeActivityV5.ANDROID_LOGIN_PUSH_FCM);
		return (!isFCMConnected);
	}
}
