package cc.wulian.app.model.device.interfaces;

import java.util.Comparator;

import android.app.Dialog;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.entity.TaskEntity.SensorGroup;
import cc.wulian.smarthomev5.fragment.scene.SceneTaskManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

import com.yuantuo.customview.ui.WLDialog;

public abstract  class AbstractLinkTaskView {

	protected BaseActivity context;
	protected TaskInfo taskInfo;
	protected View rootView;
	protected DeviceCache deviceCache;
	protected WulianDevice sensorDevice;
	protected LayoutInflater inflater;
	protected Resources resources;
	protected TaskEntity taskEntity;
	protected SensorGroup linkGroup = null;
	protected WLDialog delayTimeDialog;
	protected ArrayAdapter<String> delayAdapter;
	
	protected static final String UNIT_MORE = "[";
	protected static final String UNIT_LESS = "]";
	protected static final String CONSTANT_COLOR_START = "<font color=#f31961>";
	protected static final String CONSTANT_COLOR_END = "</font>";
	
	protected Comparator<TaskInfo> compare = new Comparator<TaskInfo>() {

		@Override
		public int compare(TaskInfo lhs, TaskInfo rhs) {
			int result = lhs.getType().compareTo(rhs.getType());
			if (result == 0) {
				result = lhs.getEp().compareTo(rhs.getEp());
			}
			return result;
		}

	};
	public AbstractLinkTaskView(BaseActivity context,TaskInfo info){
		this.context = context;
		this.taskInfo = info;
		inflater = LayoutInflater.from(context);
		resources = context.getResources();
		deviceCache = DeviceCache.getInstance(context);
		sensorDevice = deviceCache.getDeviceByIDEp(context, info.getGwID(), info.getSensorID(),info.getSensorEp());
		WulianDevice currentDevice = deviceCache.getDeviceByID(context, info.getGwID(),info.getSensorID());
		taskInfo.setSensorName(DeviceTool.getDeviceShowName(currentDevice));
		taskInfo.setSensorType(sensorDevice.getDeviceInfo().getDevEPInfo().getEpType());
		taskEntity = SceneTaskManager.getInstance()
				.getTaskEntity(taskInfo.getGwID(), taskInfo.getSceneID());
		if (taskEntity != null)
			linkGroup = taskEntity.getLinkGroup();
		initDelayAdapter();
	}
	private void initDelayAdapter() {
		String[] items = new String[] { "0s", "3s", "10s", "30s", "1m", "5m",
				"10m" };
		delayAdapter = new ArrayAdapter<String>(context,
				R.layout.scene_link_task_delay_item, items);
	}

	public abstract View onCreateView();
	protected abstract void onViewCreated(View view);
	public abstract void initContentViews();
	
	public void save(){
		
	}
	public View getView(){
		return rootView;
	}
	/**
	 * 初始化内容Item
	 * 
	 * @param info
	 */
	protected LinearLayout initContentItem(final TaskInfo info,
			final LinearLayout parentView) {
		final LinearLayout lineLayout = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_item, null);
		LinearLayout taskInfoLayout = (LinearLayout) lineLayout
				.findViewById(R.id.scene_link_task_info_ll);
		ImageView iconImageView = (ImageView) lineLayout
				.findViewById(R.id.scene_link_task_device_icon_iv);
		TextView nameTextView = (TextView) lineLayout
				.findViewById(R.id.scene_link_task_device_name_tv);
		ToggleButton dataToggleButton = (ToggleButton) lineLayout
				.findViewById(R.id.scene_link_task_device_data_tb);
		ImageView dataImageView = (ImageView) lineLayout
				.findViewById(R.id.scene_link_task_device_data_iv);
		Button deleteButton = (Button) lineLayout
				.findViewById(R.id.scene_link_task_delete_bt);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentView.removeView(lineLayout);
				info.setAvailable(TaskEntity.VALUE_AVAILABL_NO);
			}
		});
		taskInfoLayout.setOnTouchListener(new SwipeTouchViewListener(
				taskInfoLayout, deleteButton));
		LinearLayout delayLineLayout = (LinearLayout) lineLayout
				.findViewById(R.id.scene_link_task_delay_ll);
		final TextView delayTimeTextView = (TextView) lineLayout
				.findViewById(R.id.scene_link_task_delay_tv);
		delayTimeTextView.setText(getTimeStr(info.getDelay()));
		delayLineLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectDelayTimeDialog(info, delayTimeTextView);
			}

		});
		final WulianDevice device = deviceCache.getDeviceByID(context,
				info.getGwID(), info.getDevID());
		iconImageView.setImageDrawable(device.getDefaultStateSmallIcon());
		
		StringBuilder sb = new StringBuilder();
		if (!device.isDeviceOnLine()) {
			sb.append(UNIT_MORE);
			// use spannable String to instead of this
			sb.append(CONSTANT_COLOR_START);
			sb.append(resources.getString(R.string.device_offline));
			sb.append(CONSTANT_COLOR_END);
			sb.append(UNIT_LESS);
		}
		sb.append(DeviceTool.getDeviceShowName(device));
		sb.append("-");
		sb.append(DeviceUtil.ep2IndexString(info.getEp()));
		nameTextView.setText(device.isDeviceOnLine() ? sb.toString() : Html
				.fromHtml(sb.toString()));
//		nameTextView.setText(DeviceTool.getDeviceShowName(device) + "-"
//				+ DeviceUtil.ep2IndexString(info.getEp()));
		Dialog contentDialog = device.onCreateChooseContolEpDataView(inflater,info.getEp(),info.getEpData());
		//布防和撤防
		if(device instanceof Defenseable){
			initDefenseableItem(info, contentDialog,dataToggleButton, dataImageView,device);
		}
		//控制类设备
		else if (device instanceof Controlable) {
			initControlableItem(info, contentDialog,dataToggleButton, dataImageView, device);
		} else {//自定义
			initCustomItem(info, contentDialog,dataToggleButton,dataImageView, device);
		}
		return lineLayout;
	}
	/**
	 * 控制类设备
	 * @param info
	 * @param dataToggleButton
	 * @param dataImageView
	 * @param device
	 */
	private void initControlableItem(final TaskInfo info,Dialog contentDialog,
			ToggleButton dataToggleButton, ImageView dataImageView,
			final WulianDevice device) {
		if(contentDialog == null){
			dataImageView.setVisibility(View.GONE);
			dataToggleButton.setVisibility(View.VISIBLE);
			final Controlable control = (Controlable) device;
			if (control.getOpenProtocol().equals(info.getEpData())) {
				dataToggleButton.setChecked(true);
			} else {
				dataToggleButton.setChecked(false);
			}
			dataToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								info.setEpData(control.getOpenProtocol());
							} else {
								info.setEpData(control.getCloseProtocol());
							}
						}
					});
		}else{
			initCustomItem(info,contentDialog,dataToggleButton,dataImageView,device);
		
		}
	}
	/**
	 * 自定义设备
	 * @param info
	 * @param dataImageView
	 * @param device
	 */
	private void initCustomItem(final TaskInfo info,final Dialog dialog, ToggleButton dataToggleButton,ImageView dataImageView,
			final WulianDevice device) {
		dataImageView.setVisibility(View.VISIBLE);
		dataToggleButton.setVisibility(View.GONE);
		dataImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(dialog == null)
					return ;
				Dialog epDialog = device.onCreateChooseContolEpDataView(inflater, info.getEp(), info.getEpData());
				device.setControlEPDataListener(new ControlEPDataListener() {

					@Override
					public void onControlEPData(String epData) {
						info.setEpData(epData);
					}
				});
				epDialog.show();
			}
		});
	}
	/**
	 * 布防和撤防设备
	 * @param info
	 * @param dataToggleButton
	 * @param device
	 */
	private void initDefenseableItem(final TaskInfo info,Dialog contentDialog,
			ToggleButton dataToggleButton,ImageView dataImageView, final WulianDevice device) {
		if(contentDialog == null){
			dataImageView.setVisibility(View.GONE);
			dataToggleButton.setVisibility(View.VISIBLE);
			final Defenseable defenseable = (Defenseable) device;
			if (defenseable.getDefenseSetupProtocol().equals(info.getEpData())) {
				dataToggleButton.setChecked(true);
			} else {
				dataToggleButton.setChecked(false);
			}
			dataToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					if (isChecked) {
						info.setEpData(defenseable.getDefenseSetupProtocol());
					} else {
						info.setEpData(defenseable.getDefenseUnSetupProtocol());
					}
				}
			});
		}else{
			initCustomItem(info,contentDialog,dataToggleButton,dataImageView,device);
		}
	}
	/**
	 * 选择延时对话框
	 * 
	 * @param info
	 * @param delayTimeTextView
	 */
	private void showSelectDelayTimeDialog(final TaskInfo info,
			final TextView delayTimeTextView) {
		LinearLayout lineLayout = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_delay_time, null);
		ListView listView = (ListView) lineLayout
				.findViewById(R.id.scene_link_task_delay_listview);
		listView.setAdapter(delayAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String delayStr = delayAdapter.getItem(position);
				info.setDelay(getSeconds(delayStr) + "");
				delayTimeTextView.setText(delayStr);
				if (delayTimeDialog != null && delayTimeDialog.isShowing()) {
					delayTimeDialog.dismiss();
				}
			}
		});
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setPositiveButton(null);
		builder.setNegativeButton(null);
		builder.setContentView(lineLayout);
		builder.setCancelOnTouchOutSide(true);
		delayTimeDialog = builder.create();
		delayTimeDialog.show();
	}

	/**
	 * 获取时间字符串
	 * 
	 * @param t
	 * @return
	 */
	private String getTimeStr(String t) {
		int time = StringUtil.toInteger(t);
		String result = "";
		if (time >= 60) {
			result = time / 60 + "m";
		} else {
			result = time + "s";
		}
		return result;
	}

	/**
	 * 根据字符串获取时间秒数
	 * 
	 * @param str
	 * @return
	 */
	private int getSeconds(String str) {
		int result = 0;
		if (!StringUtil.isNullOrEmpty(str)) {
			if (str.contains("s")) {
				result = StringUtil
						.toInteger(str.substring(0, str.length() - 1));
			} else if (str.contains("m")) {
				int minute = StringUtil.toInteger(str.substring(0,
						str.length() - 1));
				result = minute * 60;
			}
		}
		return result;
	}

}
