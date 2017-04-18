package cc.wulian.smarthomev5.fragment.setting.timezone;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.AccountInformationSettingTimeItemClickActivity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class SettingTimeZoneFragment extends WulianFragment implements
		OnClickListener {

	private TextView currZoneTv;
	private LinearLayout goZoneListLayout;
	private static final String SHOW_DIALOG_KEY = "set_time_key";
	private static final int resuestCode = 10001;

	private TextView currentTimeTextView;
	private LinearLayout highGradeItemLinearlayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.account_manager_setting_timezone_set,
				container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
		getDatasFromServer();
	}

	private void initWidget(View view) {
		highGradeItemLinearlayout = (LinearLayout) view
				.findViewById(R.id.account_manager_high_grade_item_ll);
		currentTimeTextView = (TextView) view
				.findViewById(R.id.account_manager_current_time_tv);
		currZoneTv = (TextView) view
				.findViewById(R.id.account_manager_setting_curr_zone_tv);
		goZoneListLayout = (LinearLayout) view
				.findViewById(R.id.account_manager_go_zone_list);
		goZoneListLayout.setOnClickListener(this);
		highGradeItemLinearlayout.setOnClickListener(this);
	}

	// 获取本地时区编号
	private String getLocalTimeZoneIndex() {
		TimeZone zone = TimeZone.getDefault();
		long zoneOffset = zone.getOffset(System.currentTimeMillis());
		int zoneOffsetHours = (int) (zoneOffset / 60 / 60 / 1000);
		return zoneOffsetHours > 0 ? "+" + String.valueOf(zoneOffsetHours)
				: String.valueOf(zoneOffsetHours);
	}

	// 从服务端获取数据
	private void getDatasFromServer() {
		mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
		String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
		SendMessage.sendGetTimeZoneConfigMsg(gwID);
	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.gateway_timezone_setting));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
	}

	public void onEventMainThread(FlowerEvent event) {
		mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
		if (FlowerEvent.ACTION_FLOWER_TIMEZONE_GET.equals(event.getAction())
				|| FlowerEvent.ACTION_FLOWER_TIMEZONE_SET.equals(event
						.getAction())) {
			if (event.getData() != null) {
				try {
					String city = event.getData()
							.getString(ConstUtil.KEY_ZONE_NAME)
							.replace("/", "(")
							+ ")";
					currZoneTv.setText(city);
					String time = event.getData().getString(
							ConstUtil.KEY_ZONE_TIMEINZONE);
					char[] timeChar = time.toCharArray();
					List<String> timeString = new ArrayList<String>();
					for (int i = 0; i < timeChar.length; i++) {
						timeString.add(timeChar[i] + "");
					}
					if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){

						timeString.set(4, getString(R.string.device_adjust_year_common));
						timeString.set(7, getString(R.string.home_alarm_message_month));
						timeString.add(10, getString(R.string.scene_sun));
					}else {
						timeString.set(4, "-");
						timeString.set(7, "-");
					}
					String currentTime = "";
					for (int i = 0; i < timeString.size(); i++) {
						currentTime += timeString.get(i);
					}
					currentTimeTextView.setText(currentTime);
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getDatasFromServer();
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(mActivity,
				AccountInformationSettingTimeItemClickActivity.class);
		switch (arg0.getId()) {
		case R.id.account_manager_go_zone_list:
			intent.putExtra(
					AccountInformationSettingTimeItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					SelectTimeZomeFragment.class.getName());
			startActivityForResult(intent, resuestCode);
			break;
		case R.id.account_manager_high_grade_item_ll:
			intent.putExtra(
					AccountInformationSettingTimeItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
					highGradeSettingFragment.class.getName());
			startActivityForResult(intent, resuestCode);
			break;
		default:
			break;
		}
	}

}
