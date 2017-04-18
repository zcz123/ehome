package cc.wulian.smarthomev5.fragment.device.joingw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.JoinDeviceEvent;
import cc.wulian.smarthomev5.tools.AreaList;
import cc.wulian.smarthomev5.tools.AreaList.OnAreaListItemClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class DeviceConfigJoinGWActivity extends EventBusActivity {
	private BaseActivity mActivity;
	private LayoutInflater inflater;
	private View contentView;
	private TextView devName;
	private TextView areaGroup;
	private EditText rename;
	private TextView commit;
	private LinearLayout areaLayout;
	private DeviceJoinGWManager joinGWManager = DeviceJoinGWManager
			.getInstance();
	private WulianDevice curDevice;
	protected ProgressDialogManager mDialogManager = ProgressDialogManager
			.getDialogManager();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		inflater = LayoutInflater.from(this);
		contentView = inflater.inflate(R.layout.fragment_config_join_gw, null);
		setContentView(contentView);
		curDevice = joinGWManager.getAllDevice().poll();
		initContentView();
	}

	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));

	}

	private void initContentView() {
		devName = (TextView) contentView
				.findViewById(R.id.config_join_gw_dev_name);
		areaLayout = (LinearLayout) contentView
				.findViewById(R.id.config_join_gw_area_linear);
		areaGroup = (TextView) contentView
				.findViewById(R.id.config_join_gw_area);
		rename = (EditText) contentView.findViewById(R.id.config_join_gw_edit);
		commit = (TextView) contentView
				.findViewById(R.id.config_join_gw_commit);
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 单击提交时只有一个设备时
				if (joinGWManager.getAllDevice().size() == 0) {
					String settingName = rename.getText().toString();
					if (settingName != null) {
						SendMessage.sendSetDevMsg(
								DeviceConfigJoinGWActivity.this,
								curDevice.getDeviceGwID(), CmdUtil.MODE_UPD,
								curDevice.getDeviceID(), "",
								curDevice.getDeviceType(), settingName,
								curDevice.getDeviceCategory(), tempRoomID, "",
								"", null, true, false);
					}
					DeviceConfigJoinGWActivity.this.finish();
					// 单击提交时有多个设备时
				} else {
					String settingName = rename.getText().toString();
					curDevice = joinGWManager.getAllDevice().poll();
					// 手动点击刷新设备内容
					initCurrentDeviceView(curDevice);
					// 手动点击刷新commit按钮
					changeCommitBtnByQueueItem();
					if (settingName != null) {
						SendMessage.sendSetDevMsg(
								DeviceConfigJoinGWActivity.this,
								curDevice.getDeviceGwID(), CmdUtil.MODE_UPD,
								curDevice.getDeviceID(), "",
								curDevice.getDeviceType(), settingName,
								curDevice.getDeviceCategory(), tempRoomID, "",
								"", "", true, false);
					}
					areaGroup
							.setText(getResources()
									.getString(
											R.string.device_config_edit_dev_area_type_other_default));
					rename.setText("");
				}
			}
		});
		// 初次进入初始化commit按钮
		changeCommitBtnByQueueItem();
		// 初次进去初始化设备相关内容
		initCurrentDeviceView(curDevice);
	}

	public void changeCommitBtnByQueueItem() {
		if (joinGWManager.getAllDevice().size() == 0) {
			commit.setText(getResources()
					.getString(
							R.string.set_sound_notification_bell_prompt_choose_complete));
		} else {
			commit.setText(getResources().getString(
					R.string.device_ir_title_next));
		}

	}

	private String tempRoomID;

	public void initCurrentDeviceView(final WulianDevice device) {
		devName.setText(getResources().getString(
				R.string.device_guide_join_gw_congratulations_hint)
				+ DeviceTool.getDeviceShowName(device)
				+ getResources().getString(
						R.string.device_guide_join_gw_add_success_hint));
		areaLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final AreaList areaList = new AreaList(
						DeviceConfigJoinGWActivity.this, true);
				areaList.setOnAreaListItemClickListener(new OnAreaListItemClickListener() {

					@Override
					public void onAreaListItemClicked(AreaList list, int pos,
							RoomInfo info) {
						tempRoomID = list.getAdapter().getItem(pos).getRoomID();
						areaGroup.setText(list.getAdapter().getItem(pos)
								.getName());
						System.out.println("------>+temproomid" + tempRoomID);
						SendMessage.sendSetDevMsg(
								DeviceConfigJoinGWActivity.this,
								device.getDeviceGwID(), CmdUtil.MODE_UPD,
								device.getDeviceID(), "",
								device.getDeviceType(), device.getDeviceName(),
								device.getDeviceCategory(), tempRoomID, "", "",
								null, true, false);
						areaList.dismiss();
					}
				});
				areaList.show(arg0);

			}
		});

	}

	public void onEventMainThread(JoinDeviceEvent event) {
		changeCommitBtnByQueueItem();
	}

	public ProgressDialogManager getDialogManager() {
		return mDialogManager;
	}

	public void onEventMainThread(DialogEvent event) {
		// 自动刷新commit按钮
		ProgressDialogManager dialogManager = getDialogManager();
		if (dialogManager.containsDialog(SendMessage.ACTION_SET_DEVICE
				+ event.actionKey)) {
			dialogManager.dimissDialog(SendMessage.ACTION_SET_DEVICE
							+ event.actionKey, event.resultCode);
		} else if (dialogManager.containsDialog(event.actionKey)) {
			dialogManager.dimissDialog(event.actionKey, event.resultCode);
		}

	}
}
