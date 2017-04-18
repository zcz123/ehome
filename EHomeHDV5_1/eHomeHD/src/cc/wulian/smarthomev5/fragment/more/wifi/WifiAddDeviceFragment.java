package cc.wulian.smarthomev5.fragment.more.wifi;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WifiAddDeviceAdapter;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.event.WifiEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.yuantuo.customview.ui.WLListViewBuilder;

public class WifiAddDeviceFragment extends WulianFragment {
	private WifiAddDeviceAdapter wifiDeviceAdapter;
	private LinearLayout wifiDevicesContentLineLayout;
	private WLListViewBuilder listViewBuilder;
	private RelativeLayout mDeviceLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wifiDeviceAdapter = new WifiAddDeviceAdapter(mActivity,
				getWifiItems(WifiDataManager.wifiEntities));
		listViewBuilder = new WLListViewBuilder(mActivity);
		listViewBuilder.setAdapter(wifiDeviceAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.more_add_device, container,
				false);
		ViewUtils.inject(this, rootView);
		initBar();
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mDeviceLayout = (RelativeLayout) view
				.findViewById(R.id.more_add_device_ll);
		wifiDevicesContentLineLayout = (LinearLayout) view
				.findViewById(R.id.device_content);
		mDeviceLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (wifiDeviceAdapter.getEditMode()) {
					wifiDeviceAdapter.toggleEditMode();
					wifiDeviceAdapter.notifyDataSetChanged();
				} else {
					FragmentManager fm = WifiAddDeviceFragment.this
							.getActivity().getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					AddDeviceToWifiFragmentDialog.showDeviceDialog(fm, ft);
				}

			}
		});
		wifiDevicesContentLineLayout.addView(listViewBuilder.create());
	}

	private List<WifiAddDeviceControlItem> getWifiItems(List<WifiEntity> entites) {
		List<WifiAddDeviceControlItem> items = new ArrayList<WifiAddDeviceControlItem>();
		for (int i = 0; i < entites.size(); i++) {
			if (WifiEntity.TYPE_DEVICE.equals(entites.get(i).getOperateType()))
				items.add(new WifiAddDeviceControlItem(mActivity, entites
						.get(i)));
		}
		return items;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.more_wifi_scene));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.nav_device_title));
	}

	public void onEventMainThread(WifiEvent event) {

		if ((event.action + "").equals(CmdUtil.MODE_ADD)) {
			if (event.entities != null) {
				WifiDataManager.wifiEntities.addAll(event.entities);
				wifiDeviceAdapter
						.swapData(getWifiItems(WifiDataManager.wifiEntities));
			}
		} else if (CmdUtil.MODE_DEL.equals(event.action)) {
			WifiEntity entity = event.wifiEntity;
			if (entity != null) {
				WifiDataManager.wifiEntities.remove(entity);
				wifiDeviceAdapter
						.swapData(getWifiItems(WifiDataManager.wifiEntities));
			}
		} else {
			wifiDeviceAdapter.notifyDataSetChanged();
		}
	}
}
