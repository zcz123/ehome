package cc.wulian.smarthomev5.fragment.device.joingw;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.event.JoinDeviceEvent;

import com.viewpagerindicator.CirclePageIndicator;

public class DeviceGuideJoinGWLowActivity extends EventBusActivity implements OnClickListener {
	private List<View> guideItemView = new ArrayList<View>();
	private DeviceGuidePagerAdapter pagerAdapter;
	private View contentView;
	private LayoutInflater inflater;
	private Button mNosetDeviceBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		contentView = inflater.inflate(R.layout.fragment_guide_device_join_gw,
				null);
		setContentView(contentView);
		initBar();
		initViewPager();
		
		mNosetDeviceBtn = (Button) contentView.findViewById(R.id.device_join_gw_noset_btn);
		mNosetDeviceBtn.setOnClickListener(this);
	}

	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.nav_device_title));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.device_common_new_hint));
	}

	public void initViewPager() {
		ViewPager viewPager = (ViewPager) contentView
				.findViewById(R.id.device_join_gw_viewPager);

		View lowView = inflater.inflate(R.layout.fragment_guide_join_gw_low,
				null);
		View setView = inflater.inflate(R.layout.fragment_guide_join_gw_set,
				null);
		View successView = inflater.inflate(
				R.layout.fragment_guide_join_gw_success, null);
		// 含有颜色标记的字体
		TextView lowTextView = (TextView) lowView
				.findViewById(R.id.fragment_guide_join_gw_low_text);
		TextView setTextView = (TextView) setView
				.findViewById(R.id.fragment_guide_join_gw_set_text);
		lowTextView.setText(Html.fromHtml(getResources().getString(
				R.string.device_guide_join_gw_low_hint)));
		setTextView.setText(Html.fromHtml(getResources().getString(
				R.string.device_guide_join_gw_set_hint)));

		guideItemView.add(lowView);
		guideItemView.add(setView);
		guideItemView.add(successView);

		pagerAdapter = new DeviceGuidePagerAdapter(guideItemView);
		viewPager.setAdapter(pagerAdapter);

		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) contentView
				.findViewById(R.id.device_join_gw_dot);
		circlePageIndicator.setViewPager(viewPager);
		
	}

	public void jumpToConfigDeviceActivity(String gwID, String devID) {
		Intent intent = new Intent();
		intent.setClass(DeviceGuideJoinGWLowActivity.this,
				DeviceConfigJoinGWActivity.class);
		startActivity(intent);
		finish();
	}

	public boolean fingerRightFromCenter() {
		return false;
	}

	public boolean fingerLeft() {
		return false;
	}

	public void onEventMainThread(JoinDeviceEvent event) {
		jumpToConfigDeviceActivity(event.mGwID, event.mDevID);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this,  DeviceGuideAddNoSetDeviceListActivity.class);
		startActivity(intent);
	}
}
