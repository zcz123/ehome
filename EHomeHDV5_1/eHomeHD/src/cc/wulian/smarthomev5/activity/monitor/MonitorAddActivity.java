package cc.wulian.smarthomev5.activity.monitor;

import com.wulian.icam.view.device.config.DeviceIdQueryActivity;
import com.yuantuo.customview.ui.WLToast;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EditMonitorInfoActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;

/**
 * 手动输出uid界面
 * */
public class MonitorAddActivity extends EventBusActivity {


	private ImageView ivback,ivinput;
	private EditText idinput;
 	private Button btnok;
	private TextView tvshow;
	
	private String devicesID="";
	 private boolean eagle = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_input);
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
	   ivback=(ImageView) findViewById(R.id.iv_scan_titlebar_back);
	   ivinput=(ImageView) findViewById(R.id.titlebar_devices_manual_input);
	   ivinput.setVisibility(View.GONE);
	   idinput=(EditText) findViewById(R.id.et_monitor_input_uid);
	   tvshow=(TextView) findViewById(R.id.tv_monitor_show_result);
	   btnok=(Button) findViewById(R.id.btn_monitor_certain);
	   onClick();
	}
	
	protected void onClick() {
		tvshow.setText("");
		ivback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				devicesID=idinput.getText().toString().trim();
//				setUID(devicesID);
				if(StringUtil.isNullOrEmpty(devicesID)){
					WLToast.showToast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.home_monitor_result_unknow_id), WLToast.TOAST_SHORT);
				}
				else{
					if (setUID(devicesID)) {
							MonitorAddActivity.this.finish();
							Intent mIntent = new Intent();
							mIntent = eagle?mIntent.setClass(MonitorAddActivity.this,EditMonitorInfoActivity.class):mIntent.setClass(MonitorAddActivity.this,DeviceIdQueryActivity.class);
							if (eagle){
								Resources resources = MonitorAddActivity.this.getResources();
								CameraInfo info = new CameraInfo();
								info.setCamId(-1);
								info.setCamType(CameraInfo.CAMERA_TYPE_CLOUD_2);
								info.setUid(devicesID);
								info.setCamName( resources.getString(R.string.monitor_cloud_two_video_camera));
								Bundle bundle = new Bundle();
								bundle.putSerializable(EditMonitorInfoFragment.CAMERA_INFO, info);
								mIntent.putExtras(bundle);
							}else {
								mIntent.putExtra("isAddDevice", true);
								mIntent.putExtra("msgData", devicesID);
							}
							MonitorAddActivity.this.startActivity(mIntent);
					}else {
						//把光标移动到后面
					}
				}
			}
		});
		//监听下文本框  改变tvshow的文字
		idinput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (count==0) {
					tvshow.setText("");
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	public boolean setUID(String uid) {
		boolean flag=false;
		if (uid.length() == 16&&uid.startsWith("ZHJ")){
			eagle = true;//猫眼已经不走这走了 借用一下
			return true;
		}
		else if(uid.length() == 16){
			devicesID="cmic"+uid;
			flag=true;
		}else if(uid.length() == 20){
			devicesID=uid;
			flag=true;
		} else {
			tvshow.setText(R.string.device_id_error);
		}
		return flag;
	}
}
