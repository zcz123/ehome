package cc.wulian.smarthomev5.fragment.setting.flower;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.FlowerBroadcastVolumeItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.FlowerPositionSetItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.FlowerTimeShowItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.FlowerTimingBroadcastItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.FlowerVoiceControlItem;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SettingFlowerManagerFragment extends WulianFragment implements
		OnClickListener {

	private static final String SHOW_DIALOG_KEY = "flower_manager_dialog_key";
	@ViewInject(R.id.setting_manager_flower_dream)
	private ListView listView;

	private ImageView broadcastImg;
	private SettingManagerAdapter settingManagerAdapter;
	FlowerPositionSetItem flowerPositionSetItem;
	private List<AbstractSettingItem> items = new ArrayList<AbstractSettingItem>();
	private View listHead;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.settingManagerAdapter = new SettingManagerAdapter(this.mActivity);
		initBar();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
			Bundle bundle) {
		View localView = inflater.inflate(
				R.layout.setting_flower_dream_control, viewGroup, false);
		listHead = inflater.inflate(R.layout.flower_manager_list_head, null,
				false);
		ViewUtils.inject(this, localView);
		return localView;
	}

	public void onViewCreated(View paramView, @Nullable Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);
		listView.addHeaderView(listHead);
		this.listView.setAdapter(this.settingManagerAdapter);
		broadcastImg = (ImageView) listHead.findViewById(R.id.broadcast_img);
		broadcastImg.setOnClickListener(this);
		loadFlowerSetting();
		getLoaderData();
	}

	public void onResume() {
		super.onResume();
		String txt = SmarthomeFeatureImpl.getData(
				SmarthomeFeatureImpl.Constants.DISTRICT, "");
		if ((!"".equals(txt))
				&& (!txt.equals(flowerPositionSetItem.getInfoText()))) {
			saveDistrict(txt);
			setPosition(txt);
		}
	}

	private void setPosition(String positionTxt) {

		if (positionTxt != null)
			flowerPositionSetItem.setInfoText(positionTxt);
	}

	private void saveDistrict(final String district) {
		TaskExecutor.getInstance().execute(new Runnable() {
			public void run() {
				SendMessage.sendSetFlowerConfigMsg(AccountManager
						.getAccountManger().getmCurrentInfo().getGwID(),
						CmdUtil.FLOWER_POSITION_SET, district);
			}
		});
	}

	private void getLoaderData() {
		TaskExecutor.getInstance().execute(new Runnable() {
			public void run() {
				SendMessage.sendGetFlowerConfigMsg(AccountManager
						.getAccountManger().getmCurrentInfo().getGwID(),
						CmdUtil.FLOWER_POSITION_SET);
			}
		});
	}

	public void onShow() {
		super.onShow();
		loadFlowerSetting();
	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.gateway_dream_flower));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getSupportActionBar().setRightIcon(
				mApplication.getResources().getDrawable(R.drawable.home_quick_scene));

	}

	private void loadFlowerSetting() {
		this.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				initFlowerSettingItems();
			}
		});
	}

	protected void initFlowerSettingItems() {
		items.clear();

		EmptyItem emptyItem1 = new EmptyItem(this.getActivity());
		emptyItem1.initSystemState();
		FlowerTimingBroadcastItem flowerTimingBroadcastItem = new FlowerTimingBroadcastItem(
				this.getActivity(),
				R.string.gateway_dream_flower_timing_broadcast);
		flowerTimingBroadcastItem.initSystemState();
		FlowerTimeShowItem flowerTimeShowItem = new FlowerTimeShowItem(
				this.getActivity(), R.string.gateway_dream_flower_time_show);
		flowerTimeShowItem.initSystemState();
		EmptyItem emptyItem2 = new EmptyItem(this.getActivity());
		emptyItem2.initSystemState();
		FlowerBroadcastVolumeItem flowerBroadcastVoiceItem = new FlowerBroadcastVolumeItem(
				this.getActivity(), R.string.gateway_dream_flower_broadcast_set);
		flowerBroadcastVoiceItem.initSystemState();
		EmptyItem emptyItem3 = new EmptyItem(this.getActivity());
		emptyItem3.initSystemState();
		flowerPositionSetItem = new FlowerPositionSetItem(this.getActivity(),
				R.string.gateway_dream_flower_position_set);
		flowerPositionSetItem.initSystemState();

		FlowerVoiceControlItem voiceControlItem = new FlowerVoiceControlItem(
				mActivity);
		voiceControlItem.initSystemState();
		items.add(emptyItem1);
		items.add(flowerTimingBroadcastItem);
		items.add(flowerTimeShowItem);
		items.add(emptyItem2);
		items.add(flowerBroadcastVoiceItem);
//		items.add(voiceControlItem);
		items.add(emptyItem3);
		if(getResources().getConfiguration().locale.getCountry().equals("CN")||getResources().getConfiguration().locale.getCountry().equals("TW") ) {
			items.add(flowerPositionSetItem);
		}
		this.settingManagerAdapter.swapData(items);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.broadcast_img:
			SendMessage.sendSetFlowerConfigMsg(
					AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
					CmdUtil.FLOWER_IMMEDIATELY_BROADCAST, "");
			break;
		}
	}

	public void onEventMainThread(FlowerEvent event) {
		this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
		if ((event != null)&& (FlowerEvent.ACTION_FLOWER_POSITION_SET.equals(event.getAction()))) {
			setPosition(event.getEventStr());
		}
	}

}
