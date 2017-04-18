package cc.wulian.smarthomev5.fragment.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SceneEditTaskAdapter;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.entity.TaskEntity.SensorGroup;
import cc.wulian.smarthomev5.entity.TaskEntity.TaskGroup;
import cc.wulian.smarthomev5.event.TaskEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class SceneEditFragment extends WulianFragment {
	public static final String EXTRA_SCENE_INFO = "extra_scene_info";

	private SceneInfo mCurrentSceneInfo;
	private SceneTaskManager taskManager = SceneTaskManager.getInstance();
	private ExpandableListView taskExpandListView;
	private SceneEditTaskAdapter taskAdapter;
	private TaskEntity entity;
	private boolean isRequestTasks = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle arg = getArguments();
		if (arg != null) {
			mCurrentSceneInfo = (SceneInfo) arg
					.getSerializable(EXTRA_SCENE_INFO);
		} else {
			mCurrentSceneInfo = new SceneInfo();
			mCurrentSceneInfo
					.setGwID(getAccountManger().getmCurrentInfo().getGwID());
			mCurrentSceneInfo.setSceneID(CmdUtil.SENSOR_DEFAULT);
			return;
		}

		String gwID = mCurrentSceneInfo.getGwID();
		String sceneID = mCurrentSceneInfo.getSceneID();
		entity = taskManager.getTaskEntity(gwID, sceneID);
		if (entity == null) {
			entity = new TaskEntity();
			entity.setGwID(gwID);
			entity.setSceneID(sceneID);
			TaskGroup groupNormal = new TaskGroup();
			groupNormal.setName(mApplication.getResources().getString(
					R.string.scene_common_task));
			groupNormal.setGwID(gwID);
			groupNormal.setSceneID(sceneID);
			groupNormal.setGroupID(TaskEntity.NORMAL_TASK);
			entity.addNormalGroup(groupNormal);
			SensorGroup groupLink = new SensorGroup();
			groupLink.setName(mApplication.getResources()
					.getString(R.string.scene_task_auto));
			groupLink.setGroupID(TaskEntity.LINK_TASK);
			groupLink.setGwID(gwID);
			groupLink.setSceneID(sceneID);
			entity.addLinkGroup(groupLink);
			taskManager.addTaskEntity(entity);
		}
		initBar();
		taskAdapter = new SceneEditTaskAdapter(mActivity);
		taskAdapter.swapGroupData(entity.getGrouplList());
		
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.nav_scene_title);
		getSupportActionBar().setTitle(mCurrentSceneInfo.getName());
	}

	private void expandListView() {
		List<TaskGroup> groups = taskAdapter.getGroupData();
		for (int i = 0; i < groups.size(); i++) {
			if (!taskExpandListView.isGroupExpanded(i))
				taskExpandListView.expandGroup(i);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.scene_edit_content, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		taskExpandListView = (ExpandableListView) view
				.findViewById(R.id.scene_edit_task_expand_list);
		taskExpandListView.setAdapter((BaseExpandableListAdapter) taskAdapter);
		taskExpandListView.setGroupIndicator(null);
		initListeners();
		expandListView();
	}
	@Override
	public void onResume() {
		super.onResume();
		if(!isRequestTasks){
			SendMessage.sendGetTaskMsg(mActivity,mCurrentSceneInfo.getGwID(),"-1", mCurrentSceneInfo.getSceneID());
			isRequestTasks = true;
		}
	}
	private void initListeners() {
		taskExpandListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		entity.getLinkGroup().clear();
		entity.getNormalGroup().clear();
	}

	
	public void onEventMainThread(TaskEvent event) {
		TaskEntity entity = taskManager.getTaskEntity(
				mCurrentSceneInfo.getGwID(), mCurrentSceneInfo.getSceneID());
		if (entity != null) {
			List<List<TaskControlItem>> groups = new ArrayList<List<TaskControlItem>>();
			for (TaskGroup group : entity.getGrouplList()) {
				ArrayList<TaskControlItem> childrens = new ArrayList<TaskControlItem>();
				for (TaskInfo info : group.getTaskList()) {
					try {
						childrens.add(new TaskControlItem(mActivity, info));
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				Collections.sort(childrens);
				groups.add(childrens);
			}
			taskAdapter.swapChildData(groups);
		}
	}

}