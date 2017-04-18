package cc.wulian.smarthomev5.fragment.house;

import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.house.HousekeeperSceneTriggerAdapter;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.yuantuo.customview.ui.WLToast;
/**
 * 管家规则中的触发事件场景选择页
 * @author Administrator
 *
 */
public class HouseKeeperTriggerSceneFragment extends WulianFragment{

	public static final String CURRENT_SHOW_FRAGMENT_TIME = "3";
	public static final String TRIGGER_OR_CONDITION = "trigger_or_condition";
	public static final String TRIGGER_INFO_SCENE_SERIAL = "trigger_info_scene_serial";
//	public static final String TRIGGER_INFO_SCENE_POSITION = "trigger_info_scene_position";
	public static final String TRIGGER_SCENE_ON = "on";
	public static final String TRIGGER_SCENE_OFF = "off";
	
	private GridView mAbsGridView;
	private Button selectScene;
	private Button noSelectScene;
	private Button ensureButton;
	private TextView ensureRemind;
	private HousekeeperSceneTriggerAdapter mSceneChooseAdapter;
	private SceneDao sceneDao = SceneDao.getInstance();
	private String condition;
	private AutoConditionInfo conditionInfo;
	private AutoConditionInfo newconditionInfo;
	private int position;
	private String sceneId;
	
	private static SceneChooseListener sceneChooseListener;
//	private SceneTriggerListener sceneTriggerListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			condition = bundle.getString(TRIGGER_OR_CONDITION);
			if(bundle.containsKey(TRIGGER_INFO_SCENE_SERIAL)){
				conditionInfo = (AutoConditionInfo) bundle.getSerializable(TRIGGER_INFO_SCENE_SERIAL);
			}else{
				conditionInfo = new AutoConditionInfo();
			}
		}
		initBar();
	}


	public static void setSceneChooseListener(SceneChooseListener sceneChooseListener) {
		HouseKeeperTriggerSceneFragment.sceneChooseListener = sceneChooseListener;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_choose_scene, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSceneChooseAdapter = new HousekeeperSceneTriggerAdapter(mActivity, null);
		mAbsGridView = (GridView) view.findViewById(R.id.gridViewShowInfo);
		
		selectScene = (Button) view.findViewById(R.id.house_keeper_task_scene_trigger);
		noSelectScene = (Button) view.findViewById(R.id.house_keeper_task_scene_no_trigger);
		ensureButton = (Button) view.findViewById(R.id.house_keeper_task_scene_ensure);
		ensureRemind = (TextView) view.findViewById(R.id.house_keeper_task_scene_remind_text);
		selectScene.setOnClickListener(selectSceneListener);
		noSelectScene.setOnClickListener(selectSceneListener);
		ensureButton.setOnClickListener(selectSceneListener);
		mAbsGridView.setAdapter(mSceneChooseAdapter);
		mAbsGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				sceneId = mSceneChooseAdapter.getItem(position).getSceneID();
				mSceneChooseAdapter.setSelectSceneID(sceneId);
				mSceneChooseAdapter.notifyDataSetChanged();
			}
			
		});
		initChooseSceneView();
	}

	
	private void initChooseSceneView() {
		if(conditionInfo != null){
			sceneId = conditionInfo.getObject();
			mSceneChooseAdapter.setSelectSceneID(sceneId);
			if(StringUtil.equals(TRIGGER_SCENE_ON, conditionInfo.getExp())){
				selectScene.setSelected(true);
				noSelectScene.setSelected(false);
			}else if(StringUtil.equals(TRIGGER_SCENE_OFF, conditionInfo.getExp())){
				noSelectScene.setSelected(true);
				selectScene.setSelected(false);
			}
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		loadScenes();
	}

	private OnClickListener selectSceneListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == selectScene){
				selectScene.setSelected(true);
				noSelectScene.setSelected(false);
				ensureRemind.setText(mActivity.getResources().getString(R.string.house_rule_trigger_sence_select_hint1));
			}else if(v == noSelectScene){
				selectScene.setSelected(false);
				noSelectScene.setSelected(true);
				ensureRemind.setText(mActivity.getResources().getString(R.string.house_rule_trigger_sence_select_hint2));
			}else if(v == ensureButton){
				if((!selectScene.isSelected() && !noSelectScene.isSelected()) || StringUtil.isNullOrEmpty(sceneId)){
					WLToast.showToast(mActivity, getResources().getString(R.string.house_rule_add_new_condition_scene_no_select),WLToast.TOAST_SHORT);
				}else{
					if (sceneChooseListener != null) {
						if(selectScene.isSelected()){
							sceneChooseListener.onSceneChoseChanged(sceneId, TRIGGER_SCENE_ON,null);
						}else if(noSelectScene.isSelected()){
							sceneChooseListener.onSceneChoseChanged(sceneId, TRIGGER_SCENE_OFF,null);
						}
					}
					mActivity.finish();
				}
			}
		}
	};
	
	
//	private void updateCondition(){
//		if(!selectScene.isSelected() && !noSelectScene.isSelected()){
//			//没有选择激活或未激活
//		}else{
//			newconditionInfo = new AutoConditionInfo();
////			newconditionInfo.setType("0");
////			newconditionInfo.setObject(sceneId);
////			if(selectScene.isSelected()){
////				newconditionInfo.setExp(TRIGGER_SCENE_ON);
////			}else if(noSelectScene.isSelected()){
////				newconditionInfo.setExp(TRIGGER_SCENE_OFF);
////			}
//			
//			if (sceneIDListener != null) {
//				sceneIDListener.onSceneIDChanged(sceneId);
//				if(selectScene.isSelected()){
//					sceneTriggerListener.onSceneTriggerChanged(TRIGGER_SCENE_ON);
//				}else if(noSelectScene.isSelected()){
//					sceneTriggerListener.onSceneTriggerChanged(TRIGGER_SCENE_OFF);
//				}
//			}
//			
////			if(StringUtil.equals("trigger", condition)){
////				HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateTrigger(conditionInfo,newconditionInfo);
////			}else if(StringUtil.equals("condition", condition)){
////				HouseKeeperAddRulesFragment.autoProgramTaskInfo.updateCondition(conditionInfo,conditionInfo);
////			}
//		}
//	}
//	
//	private void addNewCondition(){
//		if(!selectScene.isSelected() && !noSelectScene.isSelected()){
//			//没有选择激活或未激活
//		}else{
////			conditionInfo.setType("0");
////			conditionInfo.setObject(sceneId);
////			if(selectScene.isSelected()){
////				conditionInfo.setExp(TRIGGER_SCENE_ON);
////			}else if(noSelectScene.isSelected()){
////				conditionInfo.setExp(TRIGGER_SCENE_OFF);
////			}
//			if (sceneIDListener != null) {
//				sceneIDListener.onSceneIDChanged(sceneId);
//				if(selectScene.isSelected()){
//					sceneTriggerListener.onSceneTriggerChanged(TRIGGER_SCENE_ON);
//				}else if(noSelectScene.isSelected()){
//					sceneTriggerListener.onSceneTriggerChanged(TRIGGER_SCENE_OFF);
//				}
//			}
////			if(StringUtil.equals("trigger", condition)){
////				HouseKeeperAddRulesFragment.autoProgramTaskInfo.addTrigger(conditionInfo);
////			}else if(StringUtil.equals("condition", condition)){
////				HouseKeeperAddRulesFragment.autoProgramTaskInfo.addCondition(conditionInfo);
////			}
//		}
//	}
	
	private void loadScenes() {
		TaskExecutor.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				SceneInfo sceneInfo = new SceneInfo();
				sceneInfo.setGwID(mAccountManger.getmCurrentInfo().getGwID());
				final List<SceneInfo> infos = sceneDao.findListAll(sceneInfo);
				mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mSceneChooseAdapter.swapData(infos);
					}
				});
			}
		});
	}


	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(StringUtil.equals("trigger", condition)){
			getSupportActionBar().setIconText(R.string.house_rule_add_new_trigger_condition);
		}else if(StringUtil.equals("condition", condition)){
			getSupportActionBar().setIconText(R.string.house_rule_add_new_limit_condition);
		}else{
			getSupportActionBar().setIconText(R.string.about_back);
		}
		getSupportActionBar().setTitle(R.string.scene_select_scene_hint);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.finish();
				if(sceneChooseListener != null){
					sceneChooseListener.onSceneChoseChanged(null, null,null);
				}
			}
		});
	}
	
	public void onEventMainThread(SceneEvent event ){
		loadScenes();
	}
	
	public interface SceneChooseListener {
		public void onSceneChoseChanged(String sceneID,String sceneTrigger,String des);
	}


}
