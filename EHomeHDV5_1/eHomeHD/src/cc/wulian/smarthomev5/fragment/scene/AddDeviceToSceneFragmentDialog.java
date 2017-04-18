package cc.wulian.smarthomev5.fragment.scene;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.AddDeviceInfoAdapter;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.entity.TaskEntity.TaskGroup;
import cc.wulian.smarthomev5.event.TaskEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import de.greenrobot.event.EventBus;

public class AddDeviceToSceneFragmentDialog extends DialogFragment{

	private static final String TAG = AddDeviceToSceneFragmentDialog.class.getSimpleName();

	public static void showDeviceDialog( FragmentManager fm, FragmentTransaction ft,TaskGroup group) {
		DialogFragment df = (DialogFragment) fm.findFragmentByTag(TAG);
		if (df != null) {
			if (!df.getDialog().isShowing()) {
				ft.remove(df);
			}
			else {
				return;
			}
		}

		AddDeviceToSceneFragmentDialog fragment = new AddDeviceToSceneFragmentDialog();
		fragment.taskGroup = group;
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}

	private AddDeviceInfoAdapter mAddDeviceInfoAdapter;
	public TaskGroup taskGroup;
	private LinearLayout mEmptyLayout;
	private WLDialog dialog;
	private SelectDevicelistener selectDeviceListener;
	private DeviceCache deviceCache ;
	private DeviceDao deviceDao = DeviceDao.getInstance();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(this.getActivity());
		mAddDeviceInfoAdapter = new AddDeviceInfoAdapter(this.getActivity(), getRemainDevices());
	}
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		selectDeviceListener = new SelectDevicelistener();
		WLDialog.Builder builder = new Builder(this.getActivity());
		builder.setContentView(createView())
			   .setTitle(R.string.device_select_device_hint)
               .setPositiveButton(R.string.common_ok)
               .setNegativeButton(R.string.cancel)
               .setListener(selectDeviceListener);
		dialog = builder.create();	
		return dialog;
	}

	private class SelectDevicelistener implements MessageListener{

		@Override
		public void onClickPositive(View contentViewLayout) {
			if(taskGroup.getGroupID() == TaskEntity.NORMAL_TASK){
				//获取到选择后的 正常任务中设备的List集合
				List<TaskInfo> tasks = createNormalTasks();
				JSONArray array = new JSONArray();
				//循环遍历选择的设备，自动生成相应设备列表
				for(TaskInfo taskInfo : tasks){
					JSONObject obj = new JSONObject();
					JsonTool.makeTaskJSONObject(obj,taskInfo,CmdUtil.MODE_ADD);
					array.add(obj);
					SendMessage.sendSetTaskMsg(getActivity(), taskInfo.getGwID(), taskInfo.getSceneID(), taskInfo.getDevID(), taskInfo.getType(),taskInfo.getEp(), taskInfo.getEpType(),array);
				}
			}
			else{
				
				List<TaskInfo> tasks = createLinkTasks();
				for(TaskInfo task : tasks){
					taskGroup.addTask(task);
				}
				EventBus.getDefault().post(new TaskEvent(null, CmdUtil.MODE_ADD, true, null,null, null));
			}
			dialog.dismiss();
		}

		@Override
		public void onClickNegative(View contentViewLayout) {
			dialog.dismiss();
		}
		
	}
	/**
	 * 联动任务的结果
	 * @return
	 */
	private List<TaskInfo> createLinkTasks() {
		List<TaskInfo> entities = new ArrayList<TaskInfo>();
		List<DeviceInfo> lists = mAddDeviceInfoAdapter.getData();
		for (int i = 0; i < lists.size(); i++) {
			if (mAddDeviceInfoAdapter.getBitSet().get(i)) {
				DeviceInfo deviceInfo = lists.get(i);
				TaskInfo taskInfo = new TaskInfo();
				taskInfo.setTaskMode(TaskEntity.VALUE_TASK_MODE_REALTIME);
				taskInfo.setContentID(TaskEntity.VALUE_CONTENT_OPEN);
				taskInfo.setGwID(taskGroup.getGwID());
				taskInfo.setSceneID(taskGroup.getSceneID());
				taskInfo.setSensorID(deviceInfo.getDevID());
				taskInfo.setSensorEp(deviceInfo.getDevEPInfo().getEp());
				taskInfo.setSensorName(deviceInfo.getName());
				taskInfo.setSensorType(deviceInfo.getDevEPInfo().getEpType());
				taskInfo.setSensorData("0");
				taskInfo.setForward(TaskEntity.VALUE_FORWARD_ENABLE);
				taskInfo.setAvailable(TaskEntity.VALUE_AVAILABL_YES);
				taskInfo.setMutilLinkage(TaskEntity.VALUE_MULTI_LINK_YES);
				entities.add(taskInfo);
			}
		}
		return entities;
	}
	/**
	 * 普通任务的结果
	 * @return
	 */
	private List<TaskInfo> createNormalTasks() {
		List<TaskInfo> entities = new ArrayList<TaskInfo>();
		//通过adapter获取到选中的设备
		List<DeviceInfo> lists = mAddDeviceInfoAdapter.getData();
		for (int i = 0; i < lists.size(); i++) {
			//通过获取选中的pos，动态加载设备信息到List集合中
			if (mAddDeviceInfoAdapter.getBitSet().get(i)) {
				DeviceInfo deviceInfo = lists.get(i);
				TaskInfo taskInfo = new TaskInfo();
				taskInfo.setTaskMode(TaskEntity.VALUE_TASK_MODE_REALTIME);
				taskInfo.setContentID(TaskEntity.VALUE_CONTENT_OPEN);
				taskInfo.setGwID(taskGroup.getGwID());
				taskInfo.setSceneID(taskGroup.getSceneID());
				taskInfo.setDevID(deviceInfo.getDevID());
				taskInfo.setType(deviceInfo.getType());
				taskInfo.setEp(deviceInfo.getDevEPInfo().getEp());
				taskInfo.setEpType(deviceInfo.getDevEPInfo().getEpType());
				taskInfo.setEpData(deviceInfo.getDevEPInfo().getEpData());
				taskInfo.setSensorID(TaskEntity.VALUE_SENSOR_ID_NORMAL);
				taskInfo.setSensorEp(TaskEntity.VALUE_SENSOR_ID_NORMAL);
				taskInfo.setAvailable(TaskEntity.VALUE_AVAILABL_YES);
				taskInfo.setMutilLinkage(TaskEntity.VALUE_MULTI_LINK_YES);
				entities.add(taskInfo);
			}
		}
		return entities;
	}

	public View createView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_add_device_info, null);
		ListView listView = (ListView) view.findViewById(R.id.action_pop_menu_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(mAddDeviceInfoAdapter);
		listView.setEnabled(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				mAddDeviceInfoAdapter.onSelection(position);
			}
		});

		mEmptyLayout = (LinearLayout) view.findViewById(R.id.no_data);
		initEmptyView();
		return view;
	}

	//获取缓存中的去除不应该联动的所有设备
	private List<DeviceInfo> getRemainDevices(){
		List<TaskInfo> infos = taskGroup.getTaskList();
		if(taskGroup.getGroupID() == TaskEntity.LINK_TASK){
			ArrayList<TaskInfo> hasSelectedInfos = new ArrayList<TaskInfo>();
			for(TaskInfo info : infos){
				TaskInfo task = new TaskInfo();
				task.setGwID(info.getGwID());
				task.setSceneID(info.getSceneID());
				task.setDevID(info.getSensorID());
				task.setEp(info.getSensorEp());
				hasSelectedInfos.add(task);
			}
			infos = hasSelectedInfos;
		}
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setGwID(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		List<DeviceInfo> result = deviceDao.findListTaskRemain(deviceInfo,infos);
		for(int i=result.size()-1;i>=0;i--){
			DeviceInfo info = result.get(i);
			WulianDevice device = deviceCache.getDeviceByIDEp(getActivity(), info.getGwID(), info.getDevID(), info.getDevEPInfo().getEp());
			if(device == null  || !device.isDeviceUseable()){
				result.remove(info);
				continue;
			}
			if(taskGroup.getGroupID() == TaskEntity.LINK_TASK){
				if(!device.isLinkControl()){
					result.remove(info);
				}
			}else{
				if(!device.isAutoControl(true)){
					result.remove(info);
				}
			}
		}
		return result;
	}
	private void initEmptyView(){
		if(mAddDeviceInfoAdapter.getCount() == 0){
			mEmptyLayout.setVisibility(View.VISIBLE);
		}else{
			mEmptyLayout.setVisibility(View.GONE);
		}
	}
}
