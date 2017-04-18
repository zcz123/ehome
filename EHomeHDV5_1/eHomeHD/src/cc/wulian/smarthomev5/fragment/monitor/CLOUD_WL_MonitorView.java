package cc.wulian.smarthomev5.fragment.monitor;
/**
 * 以UID文本编辑框作为用户的登录名
 * password文本编辑框作为登录密码；
 * 修改
 */

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.iotc.config.IOTCDevCKWifiActivity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;

import com.wulian.icam.view.device.config.DeviceIdQueryActivity;
import com.yuantuo.customview.ui.WLToast;

public class CLOUD_WL_MonitorView extends AbstractMonitorView {
	private TextView monitorUserTextView;//因需求改动  此tv现用于显示错误信息
	private EditText monitoruidEditText;
//	private ImageView cameraImageView;//此控件不用
	private Button  monitorButtonCertain;
	protected CameraInfo mCameraInfo;
    private boolean eagle = false;
	public CLOUD_WL_MonitorView(BaseActivity context, CameraInfo info) {
		super(context, info);
	}

	@Override
	public View onCreateView() {
		view = inflater.inflate(R.layout.monitor_wlclouda_setview, null);
		return view;
	}

	@Override
	public void onViewCreated() {
		monitorUserTextView = (TextView) view.findViewById(R.id.monitorUIDTextView);
		monitorUserTextView.setText(mContext.getResources().getString(R.string.home_monitor_uid_text));
		monitoruidEditText = (EditText) view.findViewById(R.id.monitorUIDEditText);
//		cameraImageView = (ImageView) view.findViewById(R.id.cameraSearchImageView);
		monitorButtonCertain = (Button) view.findViewById(R.id.monitor_edit_cloud1_button);
//		cameraImageView.setOnClickListener(new OnClickListenerCamSearch());
		monitorButtonCertain.setOnClickListener(new OnClickListenerAddWLCamera());
	}

/*	public class OnClickListenerCamSearch implements OnClickListener {
		@Override
		public void onClick(View v) {
			EventBus.getDefault().post(new ScanEvent(0));
		}
	}*/
/**
 * 
* @ClassName: OnClickListenerImp 
* @Description: TODO(向摄像机服务器提交申请) 
* @author ylz
* @date 2015-4-20 下午12:08:28 
 */
	public class OnClickListenerAddWLCamera implements OnClickListener {
		@Override
		public void onClick(View v) {
			String cameraUid = monitoruidEditText.getText().toString().trim();
			if(StringUtil.isNullOrEmpty(cameraUid)){
				WLToast.showToast(mContext, mContext.getResources().getString(R.string.home_monitor_result_unknow_id), WLToast.TOAST_SHORT);
			}
			else{
				setUID(cameraUid);
				((Activity) mContext).finish();
//				Intent it = new Intent(mContext, V2AddDeviceActivity.class);
//				it.putExtra("deviceId", cameraUid);
//				Logger.debug( "cmic=="+cameraUid);
				Intent mIntent = new Intent();
				mIntent = eagle?mIntent.setClass(mContext,IOTCDevCKWifiActivity.class):mIntent.setClass(mContext,DeviceIdQueryActivity.class);
				mIntent.putExtra("msgData", cameraUid);
				mIntent.putExtra("isAddDevice", true);
				mContext.startActivity(mIntent);
			}
		}
	}

	@Override
	public void setUID(String uid) {
		if(uid.length() == 16){
			monitoruidEditText.setText("cmic"+uid);
		}else if(uid.length() == 20){
			monitoruidEditText.setText(uid);
		} else if(uid.length()==26){
			// add syf 猫眼摄像机
			monitoruidEditText.setText(uid);
			eagle = true;//判断 是否是摄像机
		}else{
			WLToast.showToast(mContext, mContext.getResources().getString(R.string.home_monitor_result_unknow_id), WLToast.TOAST_SHORT);
		}
	}
}
