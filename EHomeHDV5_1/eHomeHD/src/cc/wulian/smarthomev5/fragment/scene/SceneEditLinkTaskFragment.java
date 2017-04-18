package cc.wulian.smarthomev5.fragment.scene;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.entity.TaskEntity.SensorGroup;
import cc.wulian.smarthomev5.event.TaskEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

import com.yuantuo.customview.ui.WLDialog;

public class SceneEditLinkTaskFragment extends WulianFragment {

	public static final String SCENE_ID = "scene_id";
	public static final String GW_ID = "gwID";
	public static final String SENSOR_ID = "sensor_id";
	public static final String SENSOR_EP = "sensor_ep";
	private TaskInfo taskInfo;
	private SensorGroup linkGroup = null;
	private DeviceCache deviceCache;
	private WulianDevice sensorDevice;
	private TaskEntity taskEntity;
	private AbstractLinkTaskView taskView;
	private WLDialog saveTaskSuccessDialog;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		String gwID = getArguments().getString(GW_ID);
		String sceneID = getArguments().getString(SCENE_ID);
		String sensorID = getArguments().getString(SENSOR_ID);
		String sensorEp = getArguments().getString(SENSOR_EP);
		taskEntity = SceneTaskManager.getInstance()
				.getTaskEntity(gwID, sceneID);
		if (taskEntity != null)
			linkGroup = taskEntity.getLinkGroup();
		if (linkGroup != null)
			taskInfo = linkGroup.getTask(sensorID, sensorEp);
		if (taskInfo == null) {
			mActivity.finish();
			return;
		}
		sensorDevice = deviceCache.getDeviceByIDEp(mActivity, gwID, sensorID,sensorEp);
		initBar();
	}

	
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(
						R.string.set_save));
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.scene_task_list_hint));
		getSupportActionBar().setTitle(taskInfo.getSensorName());
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						taskView.save();
					}
				});

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		taskView = sensorDevice.onCreateLinkTaskView(mActivity, taskInfo);
		return taskView.getView();
	}
		
	
	
	public void onEventMainThread(TaskEvent event) {
		taskView.initContentViews();
		if(saveTaskSuccessDialog == null){
			WLDialog.Builder builder = new WLDialog.Builder(mActivity);
			builder.setPositiveButton(R.string.common_ok);
			builder.setNegativeButton(null);
			builder.setMessage(R.string.scene_save_task_success);
			builder.setTitle(R.string.set_save);
			builder.setCancelOnTouchOutSide(true);
			saveTaskSuccessDialog = builder.create();
		}
		if(!saveTaskSuccessDialog.isShowing() && !event.isFromMe && event.sceneID!= null && event.sceneID.equals(taskInfo.getSceneID()) ){
			saveTaskSuccessDialog.show();
		}
	}
}
