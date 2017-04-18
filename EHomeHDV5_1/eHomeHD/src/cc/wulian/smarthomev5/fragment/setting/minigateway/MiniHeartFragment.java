package cc.wulian.smarthomev5.fragment.setting.minigateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayTimeAlarmActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MiniHeartFragment extends WulianFragment implements
		OnClickListener {

	@ViewInject(R.id.setting_manager_now_time)
	private LinearLayout mSettingManagerItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// mini_gateway_heart_tool
		View rootView = inflater.inflate(R.layout.heart_tool, container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initBar();
		mSettingManagerItem.setOnClickListener(this);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.miniGW_Chime));
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent();
		switch (arg0.getId()) {
		case R.id.setting_manager_now_time:
			intent.setClass(mActivity, MiniGatewayTimeAlarmActivity.class);
			mActivity.startActivity(intent);
			break;
		default:
			break;
		}

	}
}
