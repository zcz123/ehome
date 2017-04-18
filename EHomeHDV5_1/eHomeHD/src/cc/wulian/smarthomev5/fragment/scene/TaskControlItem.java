package cc.wulian.smarthomev5.fragment.scene;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;

import java.util.List;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.SceneEditLinkTaskActivity;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.event.TaskEvent;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.AbstractDeviceControlItem;
import de.greenrobot.event.EventBus;

public class TaskControlItem extends AbstractDeviceControlItem{
	private String mDevID;
	private String mEp;
	private String mDevEpData;
	private TaskInfo taskInfo;
	private boolean isLinkTask =  false;
	public TaskControlItem(Context context,TaskInfo taskInfo) {
		super(context);
		this.taskInfo = taskInfo;
		if(StringUtil.isNullOrEmpty(this.taskInfo.getSensorID()) || this.taskInfo.getSensorID().equals(TaskEntity.VALUE_SENSOR_ID_NORMAL)){
			isLinkTask = false;
			mDevID = this.taskInfo.getDevID();
			mDevEpData = this.taskInfo.getEpData();
			mEp = this.taskInfo.getEp();
			mDeleteView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteNormalTaskInfo();
				}
			});
		}else{
			isLinkTask =true;
			mDevID = this.taskInfo.getSensorID();
			mDevEpData = this.taskInfo.getSensorData();
			mEp = this.taskInfo.getSensorEp();
			mControlButton.setVisibility(View.VISIBLE);
			mControlButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startActivity();
				}
			});
		}
		mDevice = mDeviceCache.getDeviceByID(mContext, taskInfo.getGwID(), mDevID);
		setWulianDevice(mDevice);
		if(isLinkTask){
			mControlSwitch.setVisibility(View.GONE);
			mControlButton.setVisibility(View.VISIBLE);
//			mControlButton.setOnClickListener(null);
			mDeleteView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteLinkTaskInfo();
				}
			});
		}
	}
	private void deleteNormalTaskInfo(){
		taskInfo.setAvailable(TaskEntity.VALUE_AVAILABL_NO);
		JSONObject obj = new JSONObject();
		JsonTool.makeTaskJSONObject(obj,taskInfo,CmdUtil.MODE_DEL);
		JSONArray array = new JSONArray();
		array.add(obj);
		NetSDK.sendSetTaskMsg(taskInfo.getGwID(),taskInfo.getSceneID(), taskInfo.getDevID(), taskInfo.getType(),taskInfo.getEp(), taskInfo.getEpType(),array);
	}
	private void deleteLinkTaskInfo() {
		TaskEntity taskEntity = SceneTaskManager.getInstance().getTaskEntity(taskInfo.getGwID(), taskInfo.getSceneID());
		if(taskEntity == null){
			return;
		}
		List<TaskInfo> taskInfos = taskEntity.getLinkGroup().getTaskSensorsList(taskInfo.getSensorID(), taskInfo.getSensorEp());
		if(taskInfos == null || taskInfos.isEmpty()){
			taskEntity.getLinkGroup().removeTask(taskInfo.getSensorID(), taskInfo.getSensorEp());
			EventBus.getDefault().post(new TaskEvent(taskInfo.getGwID(), CmdUtil.MODE_DEL, true, taskInfo.getSceneID(), null, null));
			return ;
		}
		else{
			WLDialog.Builder builder = new WLDialog.Builder(mContext);
			builder.setMessage(R.string.scene_delete_link_task_hint);
			builder.setPositiveButton(R.string.common_ok);
			builder.setTitle(R.string.device_config_edit_dev_area_create_item_delete);
			builder.create().show();
		}
	}
	private void startActivity(){
		Intent intent = new Intent();
		intent.putExtra(SceneEditLinkTaskFragment.SCENE_ID, taskInfo.getSceneID());
		intent.putExtra(SceneEditLinkTaskFragment.GW_ID, taskInfo.getGwID());
		intent.putExtra(SceneEditLinkTaskFragment.SENSOR_ID, taskInfo.getSensorID());
		intent.putExtra(SceneEditLinkTaskFragment.SENSOR_EP, taskInfo.getSensorEp());
		intent.setClassName(mContext, SceneEditLinkTaskActivity.class.getName());
		mContext.startActivity(intent);
	}
	@Override
	public void setEpData(String epData) {
		mDevEpData = epData;
		taskInfo.setEpData(epData);
		JSONObject obj = new JSONObject();
		JsonTool.makeTaskJSONObject(obj,taskInfo,CmdUtil.MODE_UPD);
		JSONArray array = new JSONArray();
		array.add(obj);
		NetSDK.sendSetTaskMsg(taskInfo.getGwID(),taskInfo.getSceneID(), taskInfo.getDevID(), taskInfo.getType(),taskInfo.getEp(), taskInfo.getEpType(),array);
	}

	@Override
	public String getEPData() {
		return mDevEpData;
	}
	public TaskInfo getTaskInfo() {
		return taskInfo;
	}
	public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}
	@Override
	public String getEP() {
		return mEp;
	}


}
