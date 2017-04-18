package cc.wulian.smarthomev5.fragment.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.monitor.ProtectionSettingActivity;
import cc.wulian.smarthomev5.activity.monitor.SetResolvingPowerActivity;
import cc.wulian.smarthomev5.activity.uei.SetACRemooteControlActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class DesktopCameraSetFragment extends WulianFragment {

	private LinearLayout linSafe;
	private LinearLayout linResolvingPower;
	private TextView tvResolvingPower;
	private int resolvingPowerType = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			resolvingPowerType = bundle.getInt("resolvingpower");
		}
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_desktop_camera_set,container, false);
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
		getSupportActionBar().setTitle(R.string.home_monitor_setting_monitor);
	}

	private void initView(View paramView) {
		linSafe = (LinearLayout) paramView.findViewById(R.id.lin_safety_protection);
		linResolvingPower = (LinearLayout) paramView.findViewById(R.id.lin_resolving_power);
		tvResolvingPower = (TextView) paramView.findViewById(R.id.tv_resolvingower_type);
		if(resolvingPowerType==1){
			tvResolvingPower.setText(R.string.SD);
		}else if (resolvingPowerType==2) {
			tvResolvingPower.setText(R.string.HD);
		}else if(resolvingPowerType==3){
			tvResolvingPower.setText(R.string.Superclear);
		}
		
		linSafe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mActivity.JumpTo(ProtectionSettingActivity.class);
			}
		});
		linResolvingPower.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mActivity.JumpTo(SetResolvingPowerActivity.class);
				mActivity.finish();
			}
		});
	}
}
