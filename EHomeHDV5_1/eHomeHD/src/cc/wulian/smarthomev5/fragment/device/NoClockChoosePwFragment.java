package cc.wulian.smarthomev5.fragment.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.NoClockCommonPwActivity;
import cc.wulian.smarthomev5.activity.NoClockOncePwActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class NoClockChoosePwFragment extends WulianFragment implements
		android.view.View.OnClickListener {

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	private String gwID;
	private String devID;

	@ViewInject(R.id.common_password)
	private LinearLayout commomPassword;
	@ViewInject(R.id.once_password)
	private LinearLayout oncePassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initEditDevice();
		initBar();
	}

	private void initBar() {
		// this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_uei_select_type));
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						getActivity().finish();
					}
				});
	}

	private void init() {

		commomPassword.setOnClickListener(this);
		oncePassword.setOnClickListener(this);
	}

	private void initEditDevice() {
		gwID = getActivity().getIntent().getStringExtra("gwid");
		devID = getActivity().getIntent().getStringExtra("deviceid");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.choose_password_type2,
				container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == commomPassword) {
			Intent it = new Intent();
			it.setClass(getActivity(), NoClockCommonPwActivity.class);
			it.putExtra(NoClockCommonPwFragment.GWID, gwID);
			it.putExtra(NoClockCommonPwFragment.DEVICEID, devID);
			startActivity(it);

		} else if (v == oncePassword) {
			Intent it2 = new Intent();
			it2.setClass(getActivity(), NoClockOncePwActivity.class);
			it2.putExtra(NoClockOncePwFragment.GWID, gwID);
			it2.putExtra(NoClockOncePwFragment.DEVICEID, devID);
			startActivity(it2);
		}
	}

}
