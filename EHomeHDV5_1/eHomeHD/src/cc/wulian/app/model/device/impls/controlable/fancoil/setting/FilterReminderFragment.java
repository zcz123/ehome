package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.Preference;

public class FilterReminderFragment extends WulianFragment{

	private final String TAG = getClass().getSimpleName();
	private LinearLayout layoutClose;
	private ImageView imageViewClose;
	private LinearLayout layoutThree;
	private ImageView imageViewThree;
	private LinearLayout layoutSix;
	private ImageView imageViewSix;
	private LinearLayout layoutNine;
	private ImageView imageViewNine;
	private LinearLayout layoutTwelve;
	private ImageView imageViewTwelve;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mActivity.getResources().getString(R.string.thermostat_fans_filter_hint));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_fancoil_setting_reminder, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		layoutClose = (LinearLayout) view.findViewById(R.id.thermostat_setting_reminder_layout_1);
		layoutThree = (LinearLayout) view.findViewById(R.id.thermostat_setting_reminder_layout_2);
		layoutSix = (LinearLayout) view.findViewById(R.id.thermostat_setting_reminder_layout_3);
		layoutNine = (LinearLayout) view.findViewById(R.id.thermostat_setting_reminder_layout_4);
		layoutTwelve = (LinearLayout) view.findViewById(R.id.thermostat_setting_reminder_layout_5);
		imageViewClose = (ImageView) view.findViewById(R.id.thermostat_setting_reminder_1);
		imageViewThree = (ImageView) view.findViewById(R.id.thermostat_setting_reminder_2);
		imageViewSix = (ImageView) view.findViewById(R.id.thermostat_setting_reminder_3);
		imageViewNine = (ImageView) view.findViewById(R.id.thermostat_setting_reminder_4);
		imageViewTwelve = (ImageView) view.findViewById(R.id.thermostat_setting_reminder_5);
		String selectText = Preference.getPreferences().getString(FanCoilUtil.P_KEY_FANCOIL_FILTER_REMINDER,null);
		setReminderSelected(selectText);
		ReminderClickListener clickListener = new ReminderClickListener();
		layoutClose.setOnClickListener(clickListener);
		layoutThree.setOnClickListener(clickListener);
		layoutSix.setOnClickListener(clickListener);
		layoutNine.setOnClickListener(clickListener);
		layoutTwelve.setOnClickListener(clickListener);
	}

	private void setReminderSelected(String selectText){
		if(!StringUtil.isNullOrEmpty(selectText)){
			if(StringUtil.equals(selectText ,FanCoilUtil.FILTER_REMINDER_CLOSE)){
				setLayoutClose();
			}else if(StringUtil.equals(selectText ,FanCoilUtil.FILTER_REMINDER_THREE)){
				setLayoutThree();
			}else if(StringUtil.equals(selectText ,FanCoilUtil.FILTER_REMINDER_SIX)){
				setLayoutSix();
			}else if(StringUtil.equals(selectText ,FanCoilUtil.FILTER_REMINDER_NINE)){
				setLayoutNine();
			}else if(StringUtil.equals(selectText ,FanCoilUtil.FILTER_REMINDER_TWELVE)){
				setLayoutTwelve();
			}else{
				setLayoutClose();
			}
		}
	}
	
	@Override
	public void onShow() {
		super.onShow();
		initBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		initBar();
	}

	class ReminderClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			String selectText = "";
			switch (v.getId()){
				case R.id.thermostat_setting_reminder_layout_1:
					setLayoutClose();
					selectText = FanCoilUtil.FILTER_REMINDER_CLOSE;
					break;
				case R.id.thermostat_setting_reminder_layout_2:
					setLayoutThree();
					selectText = FanCoilUtil.FILTER_REMINDER_THREE;
					break;
				case R.id.thermostat_setting_reminder_layout_3:
					setLayoutSix();
					selectText = FanCoilUtil.FILTER_REMINDER_SIX;
					break;
				case R.id.thermostat_setting_reminder_layout_4:
					setLayoutNine();
					selectText = FanCoilUtil.FILTER_REMINDER_NINE;
					break;
				case R.id.thermostat_setting_reminder_layout_5:
					setLayoutTwelve();
					selectText = FanCoilUtil.FILTER_REMINDER_TWELVE;
					break;
				default:
					break;
			}
			Preference.getPreferences().putString(FanCoilUtil.P_KEY_FANCOIL_FILTER_REMINDER,selectText);

		}
	}

	private void setLayoutClose(){
		imageViewClose.setVisibility(View.VISIBLE);
		imageViewThree.setVisibility(View.INVISIBLE);
		imageViewSix.setVisibility(View.INVISIBLE);
		imageViewNine.setVisibility(View.INVISIBLE);
		imageViewTwelve.setVisibility(View.INVISIBLE);
	}

	private void setLayoutThree(){
		imageViewClose.setVisibility(View.INVISIBLE);
		imageViewThree.setVisibility(View.VISIBLE);
		imageViewSix.setVisibility(View.INVISIBLE);
		imageViewNine.setVisibility(View.INVISIBLE);
		imageViewTwelve.setVisibility(View.INVISIBLE);
	}

	private void setLayoutSix(){
		imageViewClose.setVisibility(View.INVISIBLE);
		imageViewThree.setVisibility(View.INVISIBLE);
		imageViewSix.setVisibility(View.VISIBLE);
		imageViewNine.setVisibility(View.INVISIBLE);
		imageViewTwelve.setVisibility(View.INVISIBLE);
	}

	private void setLayoutNine(){
		imageViewClose.setVisibility(View.INVISIBLE);
		imageViewThree.setVisibility(View.INVISIBLE);
		imageViewSix.setVisibility(View.INVISIBLE);
		imageViewNine.setVisibility(View.VISIBLE);
		imageViewTwelve.setVisibility(View.INVISIBLE);
	}

	private void setLayoutTwelve(){
		imageViewClose.setVisibility(View.INVISIBLE);
		imageViewThree.setVisibility(View.INVISIBLE);
		imageViewSix.setVisibility(View.INVISIBLE);
		imageViewNine.setVisibility(View.INVISIBLE);
		imageViewTwelve.setVisibility(View.VISIBLE);
	}


}
