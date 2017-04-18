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
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.AddDeviceInfoAdapter;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.event.TaskEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import de.greenrobot.event.EventBus;

public class AddDeviceToLinkTaskFragmentDialog extends DialogFragment{

	private static final String TAG = AddDeviceToLinkTaskFragmentDialog.class.getSimpleName();
	
	public static void showDeviceDialog( FragmentManager fm, FragmentTransaction ft,List<TaskInfo> selectedInfos,TaskInfo sensorInfo) {
		DialogFragment df = (DialogFragment) fm.findFragmentByTag(TAG);
		if (df != null) {
			if (!df.getDialog().isShowing()) {
				ft.remove(df);
			}
			else {
				return;
			}
		}
		
		AddDeviceToLinkTaskFragmentDialog fragment = new AddDeviceToLinkTaskFragmentDialog();
		fragment.selectedInfos = selectedInfos;
		fragment.sensorInfo = sensorInfo;
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}
	private AddDeviceInfoAdapter mAddDeviceInfoAdapter;
	private TaskInfo sensorInfo;
	private LinearLayout mEmptyLayout;
	private WLDialog dialog;
	private SelectDevicelistener selectDeviceListener;
	private DeviceCache deviceCache ;
	private  List<TaskInfo> selectedInfos;
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
				
			List<TaskInfo> tasks = createLinkTasks();
			for(TaskInfo task : tasks){
				SceneTaskManager.getInstance().getTaskEntity(sensorInfo.getGwID(),sensorInfo.getSceneID()).getLinkGroup().addSensorTask(task);
			}
			EventBus.getDefault().post(new TaskEvent(null, CmdUtil.MODE_ADD, true, null,null, null));
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
				taskInfo.setDevID(deviceInfo.getDevID());
				taskInfo.setType(deviceInfo.getType());
				taskInfo.setEp(deviceInfo.getDevEPInfo().getEp());
				taskInfo.setEpType(deviceInfo.getDevEPInfo().getEpType());
				taskInfo.setEpData(deviceInfo.getDevEPInfo().getEpData());
				taskInfo.setTaskMode(TaskEntity.VALUE_TASK_MODE_REALTIME);
				taskInfo.setContentID(sensorInfo.getContentID());
				taskInfo.setAvailable(TaskEntity.VALUE_AVAILABL_YES);
				taskInfo.setGwID(sensorInfo.getGwID());
				taskInfo.setSceneID(sensorInfo.getSceneID());
				taskInfo.setSensorID(sensorInfo.getSensorID());
				taskInfo.setSensorEp(sensorInfo.getSensorEp());
				taskInfo.setSensorName(sensorInfo.getSensorName());
				taskInfo.setSensorType(sensorInfo.getSensorType());
				taskInfo.setForward(sensorInfo.getForward());
				taskInfo.setMutilLinkage(TaskEntity.VALUE_MULTI_LINK_YES);
				taskInfo.setVersion(sensorInfo.getVersion());
				taskInfo.setSensorData(sensorInfo.getSensorData());
				taskInfo.setSensorCond(sensorInfo.getSensorCond());
				taskInfo.setDelay("0");
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

	private List<DeviceInfo> getRemainDevices(){
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setGwID(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		List<DeviceInfo> result = deviceDao.findListTaskRemain(deviceInfo,selectedInfos);
		for(int i=result.size()-1;i>=0;i--){
			DeviceInfo info = result.get(i);
			WulianDevice device = deviceCache.getDeviceByIDEp(getActivity(), info.getGwID(), info.getDevID(), info.getDevEPInfo().getEp());
			if(device==null || !device.isDeviceUseable()|| DeviceUtil.isDeviceSensorable(device) || DeviceUtil.isDeviceAlarmable(device) || !device.isAutoControl(true)){
				result.remove(info);
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
