package cc.wulian.app.model.device.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.impls.sensorable.WL_a1_Curtain_Detector;
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

public class LinkTaskCurtainView extends AbstractLinkTaskView{
	@ViewInject(R.id.scene_link_task_left_in_head)
	private LinearLayout leftInTaskHead;
	@ViewInject(R.id.scene_link_task_left_in_content)
	private LinearLayout leftInTaskContent;
	@ViewInject(R.id.scene_link_task_add_left_in_ll)
	private LinearLayout addLeftInTaskBtn;

	@ViewInject(R.id.scene_link_task_both_in_head)
	private LinearLayout bothInTaskHead;
	@ViewInject(R.id.scene_link_task_both_in_content)
	private LinearLayout bothInTaskContent;
	@ViewInject(R.id.scene_link_task_add_both_in_ll)
	private LinearLayout addBothInTaskBtn;
	
	@ViewInject(R.id.scene_link_task_right_in_head)
	private LinearLayout rightInTaskHead;
	@ViewInject(R.id.scene_link_task_right_in_content)
	private LinearLayout rightInTaskContent;
	@ViewInject(R.id.scene_link_task_add_right_in_ll)
	private LinearLayout addRightInTaskBtn;
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == addLeftInTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_OPEN);
				sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_EQUAL);
				sensorInfo.setSensorData(WL_a1_Curtain_Detector.DATA_CTRL_STATE_BEFORE_ENTER);
				List<TaskInfo> selectTasks = getTasksLeftIn();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
			if (v == addBothInTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_STOP);
				sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_EQUAL);
				sensorInfo.setSensorData(WL_a1_Curtain_Detector.DATA_CTRL_STATE_NORMAL);
				List<TaskInfo> selectTasks = getTasksBothIn();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
			if (v == addRightInTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_CLOSE);
				sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_EQUAL);
				sensorInfo.setSensorData(WL_a1_Curtain_Detector.DATA_CTRL_STATE_BEHIND_ENTER);
				List<TaskInfo> selectTasks = getTasksRightIn();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
		}
	};
	public LinkTaskCurtainView(BaseActivity context, TaskInfo info) {
		super(context, info);
	}

	@Override
	public View onCreateView() {
		rootView = inflater.inflate(R.layout.scene_edit_link_task_curtain_content,null);
		ViewUtils.inject(this, rootView);
		onViewCreated(rootView);
		return rootView;
	}

	@Override
	protected void onViewCreated(View view) {
		initCurtainHeadContents();
		initContentViews();
		initListeners();
	}
	/**
	 * 初始化界面
	 */
	public void initContentViews() {
		initLeftInContents();
		initBothInContents();
		initRightInContents();
	}

	@Override
	public void save() {
		List<TaskInfo> curtainTasks = linkGroup.getTaskSensorsList(taskInfo.getSensorID(), taskInfo.getSensorEp());
		for( int i=0;i<curtainTasks.size();i++){
			TaskInfo task = curtainTasks.get(i);
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
	/**
	 * 初始化警报头信息
	 */
	private void initCurtainHeadContents() {
		clearHead();
		LinearLayout leftInHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_curtain_head, null);
		TextView titleLeftIn = (TextView) leftInHeadContent
				.findViewById(R.id.scene_link_task_head_title_tv);
		titleLeftIn.setText(resources.getString(R.string.device_before_state));
		leftInTaskHead.addView(leftInHeadContent);
		
		LinearLayout bothInHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_curtain_head, null);
		TextView titleBothIn = (TextView) bothInHeadContent
				.findViewById(R.id.scene_link_task_head_title_tv);
		titleBothIn.setText(resources.getString(R.string.scene_normal_hint));
		bothInTaskHead.addView(bothInHeadContent);
		
		LinearLayout rightInHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_curtain_head, null);
		TextView titleRightIn = (TextView) rightInHeadContent
				.findViewById(R.id.scene_link_task_head_title_tv);
		titleRightIn.setText(resources.getString(R.string.device_behind_satate));
		rightInTaskHead.addView(rightInHeadContent);
	}
	/**
	 * 清除头部信息
	 */
	private void clearHead() {
		leftInTaskHead.removeAllViews();
		bothInTaskHead.removeAllViews();
		rightInTaskHead.removeAllViews();
	}
	/**
	 * 初始化左边进入的任务列表
	 */
	private void initLeftInContents() {
		leftInTaskContent.removeAllViews();
		for (TaskInfo info : getTasksLeftIn()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout itemLineLayout = initContentItem(info,
						leftInTaskContent);
				leftInTaskContent.addView(itemLineLayout);
			}

		}
	}
	/**
	 * 初始化两边进入的任务列表
	 */
	private void initBothInContents() {
		bothInTaskContent.removeAllViews();
		for (TaskInfo info : getTasksBothIn()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout itemLineLayout = initContentItem(info,
						bothInTaskContent);
				bothInTaskContent.addView(itemLineLayout);
			}

		}
	}
	/**
	 * 初始化正常任务列表
	 */
	private void initRightInContents() {
		rightInTaskContent.removeAllViews();
		for (TaskInfo info : getTasksRightIn()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout itemLineLayout = initContentItem(info,
						rightInTaskContent);
				rightInTaskContent.addView(itemLineLayout);
			}
		}
	}

	/**
	 * 初始化监听器
	 */
	private void initListeners() {
		addLeftInTaskBtn.setOnClickListener(listener);
		addBothInTaskBtn.setOnClickListener(listener);
		addRightInTaskBtn.setOnClickListener(listener);
	}
	/**
	 * 左边有人
	 * 
	 * @return
	 */
	private List<TaskInfo> getTasksLeftIn() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for(TaskInfo info : linkGroup.getTaskSensorsList(taskInfo.getSensorID(),taskInfo.getSensorEp()) ){
			WulianDevice  device = deviceCache.getDeviceByIDEp(context,info.getGwID(), taskInfo.getSensorID(), taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if (device instanceof Sensorable) {
				if (TaskEntity.VALUE_CONDITION_EQUAL.equals(info.getSensorCond()) && WL_a1_Curtain_Detector.DATA_CTRL_STATE_BEFORE_ENTER.equals(info.getSensorData())) {
					tasks.add(info);
				}
			}
		}
		Collections.sort(tasks, compare);
		return tasks;
	}

	/**
	 * 两边都有人
	 * 
	 * @return
	 */
	private List<TaskInfo> getTasksBothIn() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for (TaskInfo info : linkGroup.getTaskSensorsList(
				taskInfo.getSensorID(), taskInfo.getSensorEp())) {
			WulianDevice device = deviceCache.getDeviceByIDEp(context,
					info.getGwID(), taskInfo.getSensorID(),
					taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if (device instanceof Sensorable) {
				if (TaskEntity.VALUE_CONDITION_EQUAL.equals(info.getSensorCond()) && WL_a1_Curtain_Detector.DATA_CTRL_STATE_NORMAL.equals(info.getSensorData())) {
					tasks.add(info);
				}
			}
		}
		Collections.sort(tasks, compare);
		return tasks;
	}
	/**
	 * 右边有人
	 * @return
	 */
	private List<TaskInfo> getTasksRightIn() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for (TaskInfo info : linkGroup.getTaskSensorsList(
				taskInfo.getSensorID(), taskInfo.getSensorEp())) {
			WulianDevice device = deviceCache.getDeviceByIDEp(context,
					info.getGwID(), taskInfo.getSensorID(),
					taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if (device instanceof Sensorable) {
				if (TaskEntity.VALUE_CONDITION_EQUAL.equals(info.getSensorCond()) && WL_a1_Curtain_Detector.DATA_CTRL_STATE_BEHIND_ENTER.equals(info.getSensorData())) {
					tasks.add(info);
				}
			}
		}
		Collections.sort(tasks, compare);
		return tasks;
	}
}
