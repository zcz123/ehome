package cc.wulian.smarthomev5.fragment.monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.monitor.DesktopCameraSetActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class SetResolvingPowerFragment extends WulianFragment implements
		OnClickListener {

	private LinearLayout lin1, lin2, lin3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_set_resolving_power,
				container, false);
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);

		initView(paramView);

	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(R.string.set_resolving_power);
	}

	private void initView(View paramView) {
		lin1 = (LinearLayout) paramView
				.findViewById(R.id.lin_resolving_power_1);
		lin2 = (LinearLayout) paramView
				.findViewById(R.id.lin_resolving_power_2);
		lin3 = (LinearLayout) paramView
				.findViewById(R.id.lin_resolving_power_3);
		lin1.setOnClickListener(this);
		lin2.setOnClickListener(this);
		lin3.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.lin_resolving_power_1:
			
			Bundle bundle1 = new Bundle();
			bundle1.putInt("resolvingpower", 1);
			mActivity.JumpTo(DesktopCameraSetActivity.class, bundle1);
			mActivity.finish();
			break;

		case R.id.lin_resolving_power_2:

			Bundle bundle2 = new Bundle();
			bundle2.putInt("resolvingpower", 2);
			mActivity.JumpTo(DesktopCameraSetActivity.class, bundle2);
			mActivity.finish();
			break;
			
		case R.id.lin_resolving_power_3:
			
			Bundle bundle3 = new Bundle();
			bundle3.putInt("resolvingpower", 3);
			mActivity.JumpTo(DesktopCameraSetActivity.class, bundle3);
			mActivity.finish();
			break;
		}

	}

}
