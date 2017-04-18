package cc.wulian.app.model.device.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectSensorDeviceDataFragment;
import cc.wulian.smarthomev5.tools.SingleChooseManager;

public class CurtainDetectorChooseView {

	private Context mContext;
	private LayoutInflater inflater;
	private LinearLayout rootView;

	private ImageView leftImageView;
	private ImageView rightImageView;
	private ImageView normalImageView;

	private String values;
	private String describe;
	private String ep;
	private String epType;

	private SingleChooseManager manager;
	private Resources mResources;

	public CurtainDetectorChooseView(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		rootView = (LinearLayout) inflater.inflate(
				R.layout.task_manager_curtain_detector, null);

		leftImageView = (ImageView) rootView
				.findViewById(R.id.task_manager_select_left);
		rightImageView = (ImageView) rootView
				.findViewById(R.id.task_manager_select_right);
		normalImageView = (ImageView) rootView
				.findViewById(R.id.task_manager_select_normal);
		manager = new SingleChooseManager(R.drawable.task_manager_select,R.drawable.task_manager_no_select);
		manager.addImageView(leftImageView);
		manager.addImageView(rightImageView);
		manager.addImageView(normalImageView);
		OnClickListener checkClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				manager.setChecked(v.getId());
			}
		};
		leftImageView.setOnClickListener(checkClickListener);
		rightImageView.setOnClickListener(checkClickListener);
		normalImageView.setOnClickListener(checkClickListener);
		
		Button ensureButton = (Button) rootView
				.findViewById(R.id.house_keeper_task_sensor_ensure);
		ensureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Activity activity = (Activity) mContext;
				activity.finish();
				if(manager.getCheckID() == leftImageView.getId()){
					values = "=" + "02";
					
				}else if(manager.getCheckID() == rightImageView.getId()){
					values = "=" + "03";
				}else{
					values = "=" + "00";
				}
				ep = WulianDevice.EP_14;
				epType = ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_DETECTOR;
				HouseKeeperSelectSensorDeviceDataFragment
						.fireSelectDeviceDataListener(ep, epType,values, describe);
			}
		});
	}


	public void setmSensorDeviceValues(String value, String describe) {
		this.values = value;
		this.describe = describe;
		initCurtainSensorView(value, describe, manager);
	}

	private void initCurtainSensorView(String value, String describe,
			SingleChooseManager manager) {
		if (!StringUtil.isNullOrEmpty(value)) {
			String symbol = value.substring(0, 1);
			String data = value.substring(1);
			if (StringUtil.equals(symbol, "=")) {
				if(StringUtil.equals(data, "02")){
					manager.setChecked(leftImageView.getId());
				}else if(StringUtil.equals(data, "03")){
					manager.setChecked(rightImageView.getId());
				}else{
					manager.setChecked(normalImageView.getId());
				}
			}
		} else {
			manager.setChecked(leftImageView.getId());
			values = "=" + "02";
		}
	}

	public View getView() {
		return rootView;
	}

}
