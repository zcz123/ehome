package cc.wulian.smarthomev5.fragment.setting.timezone;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class highGradeSettingFragment extends WulianFragment {

	private Button toButton;
	private static final String HIGH_GRADE_SHOW_DIALOG_KEY="high_grade_show_dialog_key";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.high_grade_setting, container,false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view){
		toButton=(Button) view.findViewById(R.id.setting_autoset_switch);
		toButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mDialogManager.showDialog(HIGH_GRADE_SHOW_DIALOG_KEY, mActivity, null, null);
				String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
				long currentTime=new Date().getTime();
				String time=String.valueOf(currentTime/1000);
				SendMessage.sendSetTimeZoneConfigMsg(gwID, null,null,null,time);
			}
		});
	}

	private void initBar()
    {
	    this.mActivity.resetActionMenu();
	    getSupportActionBar().setTitle(getResources().getString(R.string.device_module_advanced));
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayIconEnabled(true);
	    getSupportActionBar().setDisplayIconTextEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);
	    getSupportActionBar().setDisplayShowMenuEnabled(false);
	    getSupportActionBar().setIconText(mApplication.getResources().getString(R.string.gateway_timezone_setting));
   }

	public void onEventMainThread(FlowerEvent event) {
		if(FlowerEvent.ACTION_FLOWER_TIMEZONE_SET.equals(event.getAction())||
				FlowerEvent.ACTION_FLOWER_TIMEZONE_GET.equals(event.getAction())){			
		 mDialogManager.dimissDialog(HIGH_GRADE_SHOW_DIALOG_KEY, 0); 
		 Intent it=new Intent();
		 mActivity.setResult(Activity.RESULT_OK,it);
		 mActivity.finish();	
		}
	}
}
