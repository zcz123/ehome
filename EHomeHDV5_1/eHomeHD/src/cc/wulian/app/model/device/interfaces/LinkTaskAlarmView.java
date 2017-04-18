package cc.wulian.app.model.device.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.TaskInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.fragment.scene.AddDeviceToLinkTaskFragmentDialog;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LinkTaskAlarmView extends AbstractLinkTaskView{
	@ViewInject(R.id.scene_link_task_alarm_head)
	private LinearLayout alarmTaskHead;
	@ViewInject(R.id.scene_link_task_alarm_content)
	private LinearLayout alarmTaskContent;
	@ViewInject(R.id.scene_link_task_add_alarm_ll)
	private LinearLayout addAlarmTaskBtn;

	@ViewInject(R.id.scene_link_task_normal_head)
	private LinearLayout normalTaskHead;
	@ViewInject(R.id.scene_link_task_normal_content)
	private LinearLayout normalTaskContent;
	@ViewInject(R.id.scene_link_task_add_normal_ll)
	private LinearLayout addNormalTaskBtn;
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == addAlarmTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_OPEN);
				Alarmable alarm  = (Alarmable)sensorDevice;
				sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_EQUAL);
				sensorInfo.setSensorData(alarm.getAlarmProtocol());
				List<TaskInfo> selectTasks = getTasksAlarm();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
			if (v == addNormalTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_CLOSE);
				Alarmable alarm  = (Alarmable)sensorDevice;
				sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_EQUAL);
				sensorInfo.setSensorData(alarm.getNormalProtocol());
				List<TaskInfo> selectTasks = getTasksNormal();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
		}
	};
	public LinkTaskAlarmView(BaseActivity context, TaskInfo info) {
		super(context, info);
	}

	@Override
	public View onCreateView() {
		rootView = inflater.inflate(R.layout.scene_edit_link_task_alarm_content,null);
		ViewUtils.inject(this, rootView);
		onViewCreated(rootView);
		return rootView;
	}

	@Override
	protected void onViewCreated(View view) {
		initAlarmHeadContents();
		initContentViews();
		initListeners();
	}
	/**
	 * 初始化界面
	 */
	public void initContentViews() {
		initAlarmMoreContents();
		initArarmNormalContents();
	}

	@Override
	public void save() {
		List<TaskInfo> alarmTasks = linkGroup.getTaskSensorsList(taskInfo.getSensorID(), taskInfo.getSensorEp());
		if(sensorDevice instanceof Alarmable){
			for( int i=0;i<alarmTasks.size();i++){
				TaskInfo task = alarmTasks.get(i);
				task.setForward(taskInfo.getForward());
				JSONObject obj = new JSONObject();
				if(TaskEntity.VALUE_AVAILABL_YES.equals(task.getAvailable())){
					JsonTool.makeTaskJSONObject(obj,task,CmdUtil.MODE_UPD);
				}else{
					JsonTool.makeTaskJSONObject(obj,task,CmdUtil.MODE_DEL);
				}
				JSONArray array = new JSONArray();
				array.add(obj);
				NetSDK.sendSetTaskMsg(task.getGwID(), task.getSceneID(),
						task.getDevID(), task.getType(), task.getEp(),
						task.getEpType(), array);
			}
		}
	}
	/**
	 * 初始化警报头信息
	 */
	private void initAlarmHeadContents() {
		clearHead();
		LinearLayout alarmMoreHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_alarm_head, null);
		CheckBox alarmBox = (CheckBox) alarmMoreHeadContent
				.findViewById(R.id.scene_link_task_head_alarm_cb);
		if (TaskEntity.VALUE_FORWARD_ENABLE.equals(taskInfo.getForward())) {
			alarmBox.setChecked(true);
		} else {
			alarmBox.setChecked(false);
		}
		alarmBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					taskInfo.setForward(TaskEntity.VALUE_FORWARD_ENABLE);
				else {
					taskInfo.setForward(TaskEntity.VALUE_FORWARD_DISABLE);
				}
			}
		});
		alarmTaskHead.addView(alarmMoreHeadContent);
		LinearLayout alarmNormalHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_alarm_head, null);
		CheckBox normalBox = (CheckBox) alarmNormalHeadContent
				.findViewById(R.id.scene_link_task_head_alarm_cb);
		normalBox.setVisibility(View.INVISIBLE);
		TextView titleNormal = (TextView) alarmNormalHeadContent
				.findViewById(R.id.scene_link_task_head_title_tv);
		titleNormal.setText(resources.getString(R.string.scene_normal_hint));
		normalTaskHead.addView(alarmNormalHeadContent);
	}
	/**
	 * 清除头部信息
	 */
	private void clearHead() {
		alarmTaskHead.removeAllViews();
		normalTaskHead.removeAllViews();
	}
	/**
	 * 初始化警报任务列表
	 */
	private void initAlarmMoreContents() {
		alarmTaskContent.removeAllViews();
		for (TaskInfo info : getTasksAlarm()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout alarmLineLayout = initContentItem(info,
						alarmTaskContent);
				alarmTaskContent.addView(alarmLineLayout);
			}

		}
	}

	/**
	 * 初始化正常任务列表
	 */
	private void initArarmNormalContents() {
		normalTaskContent.removeAllViews();
		for (TaskInfo info : getTasksNormal()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout normalLineLayout = initContentItem(info,
						normalTaskContent);
				normalTaskContent.addView(normalLineLayout);
			}
		}
	}

	/**
	 * 初始化监听器
	 */
	private void initListeners() {
		addAlarmTaskBtn.setOnClickListener(listener);
		addNormalTaskBtn.setOnClickListener(listener);
	}
	/**
	 * 报警设备异常
	 * 
	 * @return
	 */
	private List<TaskInfo> getTasksAlarm() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for(TaskInfo info : linkGroup.getTaskSensorsList(taskInfo.getSensorID(),taskInfo.getSensorEp()) ){
			WulianDevice  device = deviceCache.getDeviceByIDEp(context,info.getGwID(), taskInfo.getSensorID(), taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if(device instanceof Alarmable){
				Alarmable alarm = (Alarmable)device;
				if(info.getSensorData().equals(alarm.getAlarmProtocol())){
					tasks.add(info);
				}
			} 
		}
		Collections.sort(tasks, compare);
		return tasks;
	}

	/**
	 * 报警设备正常
	 * 
	 * @return
	 */
	private List<TaskInfo> getTasksNormal() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for (TaskInfo info : linkGroup.getTaskSensorsList(
				taskInfo.getSensorID(), taskInfo.getSensorEp())) {
			WulianDevice device = deviceCache.getDeviceByIDEp(context,
					info.getGwID(), taskInfo.getSensorID(),
					taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if (device instanceof Alarmable) {
				Alarmable alarm = (Alarmable) device;
				if (info.getSensorData().equals(alarm.getNormalProtocol())) {
					tasks.add(info);
				}
			}
		}
		Collections.sort(tasks, compare);
		return tasks;
	}
}
