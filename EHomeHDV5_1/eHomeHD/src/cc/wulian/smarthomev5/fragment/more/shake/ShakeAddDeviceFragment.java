package cc.wulian.smarthomev5.fragment.more.shake;

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
import cc.wulian.smarthomev5.adapter.ShakeAdapter;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.event.ShakeEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.yuantuo.customview.ui.WLListViewBuilder;

public class ShakeAddDeviceFragment extends WulianFragment {

	private ShakeAdapter shakeAdapter;
	private LinearLayout shakeDevicesContentLineLayout;
	private WLListViewBuilder listViewBuilder;
	private RelativeLayout mDeviceLayout;
	public OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.more_add_device_ll:
				if (shakeAdapter.getEditMode()) {
					shakeAdapter.toggleEditMode();
					shakeAdapter.notifyDataSetChanged();
				} else {
					FragmentManager fm = ShakeAddDeviceFragment.this
							.getActivity().getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					AddDeviceToShakeFragmentDialog.showDeviceDialog(fm, ft);
				}
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shakeAdapter = new ShakeAdapter(mActivity,
				getShakeItems(ShakeManager.shakeEntity));
		listViewBuilder = new WLListViewBuilder(mActivity);
		listViewBuilder.setAdapter(shakeAdapter);
	}

	/**
	 * 根据Shake数据生成界面
	 * 
	 * @param entites
	 * @return
	 */
	private List<ShakeControlItem> getShakeItems(List<ShakeEntity> entites) {
		List<ShakeControlItem> items = new ArrayList<ShakeControlItem>();
		for (int i = 0; i < entites.size(); i++) {
			if (ShakeEntity.TYPE_DEVICE.equals(entites.get(i).getOperateType()))
				items.add(new ShakeControlItem(mActivity, entites.get(i)));
		}
		return items;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.more_add_device, container,
				false);
		ViewUtils.inject(this, rootView);
		initBar();
		return rootView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.more_shake_off_function));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.nav_device_title));
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mDeviceLayout = (RelativeLayout) view
				.findViewById(R.id.more_add_device_ll);
		shakeDevicesContentLineLayout = (LinearLayout) view
				.findViewById(R.id.device_content);
		mDeviceLayout.setOnClickListener(listener);
		shakeDevicesContentLineLayout.addView(listViewBuilder.create());
	}

	
	public void onEventMainThread(ShakeEvent event) {

		if ((event.action + "").equals(CmdUtil.MODE_ADD)) {
			if (event.entities != null) {
				ShakeManager.shakeEntity.addAll(event.entities);
				shakeAdapter.swapData(getShakeItems(ShakeManager.shakeEntity));
			}
		} else if (CmdUtil.MODE_DEL.equals(event.action)) {
			ShakeEntity entity = event.shakeEntity;
			if (entity != null) {
				ShakeManager.shakeEntity.remove(entity);
				shakeAdapter.swapData(getShakeItems(ShakeManager.shakeEntity));
			}
		} else {
			shakeAdapter.notifyDataSetChanged();
		}
	}

}
