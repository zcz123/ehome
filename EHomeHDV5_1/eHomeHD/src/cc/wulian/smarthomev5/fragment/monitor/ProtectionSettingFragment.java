package cc.wulian.smarthomev5.fragment.monitor;

import com.wulian.icam.common.APPConfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class ProtectionSettingFragment extends WulianFragment{

	private SharedPreferences sp;
	private String spMoveArea;
	
	private LinearLayout linSetAera;
	private Button btStartProtect;
	private TextView isSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_protection_setting,container, false);
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);

		initView(paramView);
		
		initData();
		

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
		getSupportActionBar().setTitle(R.string.protection_setting);
	}

	private void initView(View paramView) {
		linSetAera = (LinearLayout) paramView.findViewById(R.id.lin_area_protect);
		btStartProtect = (Button) paramView.findViewById(R.id.bt_start_protect);
		isSet = (TextView) paramView.findViewById(R.id.tv_is_set);
		
		linSetAera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				/*mActivity.startActivityForResult((new Intent(this, DetectionAreaActivity.class))
						.putExtra("type", REQUESTCODE_MOVE_AREA).putExtra("area", spMoveArea), REQUESTCODE_MOVE_AREA);*/
			}
		});
		btStartProtect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
	}
	
	private void initData(){
		sp = mActivity.getSharedPreferences(APPConfig.SP_CONFIG, mActivity.MODE_PRIVATE);

		spMoveArea = sp.getString(APPConfig.MOVE_AREA, ";");
		
		int length = spMoveArea.split(";").length;
		
		if (length <= 0) {
			isSet.setText(getResources().getString(R.string.protect_not_set));
		} else {
			isSet.setText(length + getResources().getString(R.string.protect_areas));
		}
		
		if (sp == null) {
			Log.e("IOTCamera", "---------sp==null");
			mActivity.finish();
		}
	}
	
}
