package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.house.HousekeeperSceneConditionAdapter;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.yuantuo.customview.ui.WLToast;

/**
 * 管家规则中的限制条件场景选择页。
 * @author Administrator
 *
 */
public class HouseKeeperConditionSceneFragment extends WulianFragment{

	public static final String CURRENT_SHOW_FRAGMENT_TIME = "3";
	public static final String TRIGGER_OR_CONDITION = "trigger_or_condition";
	public static final String TRIGGER_INFO_SCENE_SERIAL = "trigger_info_scene_serial";
//	public static final String TRIGGER_INFO_SCENE_POSITION = "trigger_info_scene_position";
	public static final String TRIGGER_SCENE_IN = "in";
	public static final String TRIGGER_SCENE_NOT_IN = "not in";
	private static final String SPLIT_SPACE = " ";
	private static final String SPLIT_COMMA = ",";
	
	private GridView mAbsGridView;
	private LinearLayout triggerLayout;
	private TextView mTextView;
	private Button ensureButton;
	private HousekeeperSceneConditionAdapter mSceneChooseAdapter;
	private SceneDao sceneDao = SceneDao.getInstance();
	private String condition;
	private AutoConditionInfo conditionInfo;
	private AutoConditionInfo newconditionInfo;
	private int position;
	private List<String> sceneId  = new ArrayList<String>();
	private String sceneIdItem;
	
	private static SceneConditionListener sceneConditionListener;
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


	public static void setSceneChooseListener(SceneConditionListener sceneConditionListener) {
		HouseKeeperConditionSceneFragment.sceneConditionListener = sceneConditionListener;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_choose_scene, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSceneChooseAdapter = new HousekeeperSceneConditionAdapter(mActivity, null);
		mAbsGridView = (GridView) view.findViewById(R.id.gridViewShowInfo);
		triggerLayout = (LinearLayout) view.findViewById(R.id.house_keeper_task_scene_trigger_layout);
		mTextView = (TextView) view.findViewById(R.id.house_keeper_task_scene_trigger_text);
		mTextView.setText(mActivity.getResources().getString(R.string.house_rule_add_new_condition_scene_remind));
		triggerLayout.setVisibility(View.GONE);
		ensureButton = (Button) view.findViewById(R.id.house_keeper_task_scene_ensure);
		ensureButton.setOnClickListener(selectSceneListener);
		mAbsGridView.setAdapter(mSceneChooseAdapter);
		mAbsGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				sceneIdItem = mSceneChooseAdapter.getItem(position).getSceneID();
				mSceneChooseAdapter.setSelectSceneID(sceneIdItem);
//				mSceneChooseAdapter.notifyDataSetChanged();
			}
			
		});
		initChooseSceneView();
	}

	
	private void initChooseSceneView() {
		if(!StringUtil.isNullOrEmpty(conditionInfo.getExp())){
			String[] splits = conditionInfo.getExp().split(SPLIT_SPACE);
			String sceneIdSplit;
			if(StringUtil.equals(splits[0], "in")){
				sceneIdSplit = splits[1].substring(1,splits[1].length() - 1);
			}else{
				sceneIdSplit = splits[2].substring(1,splits[2].length() - 1);
			}
			if(sceneIdSplit != null){
				String[] sceneIdStr = sceneIdSplit.split(SPLIT_COMMA);
				for(int i =0; i < sceneIdStr.length; i++){
					sceneId.add(sceneIdStr[i]);
				}
				mSceneChooseAdapter.setSelectedList(sceneId);
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
			sceneId = mSceneChooseAdapter.getSelectedList();
			String sceneIdStr = "";
			if(sceneId != null){
				for(int i = 0; i < sceneId.size(); i++){
					
					if((i + 1) == sceneId.size()){
						sceneIdStr += sceneId.get(i);
						break;
					}else{
						sceneIdStr += sceneId.get(i) + ",";
					}
				}
			}
			if (StringUtil.isNullOrEmpty(sceneIdStr)) {
				WLToast.showToast(
						mActivity,
						getResources()
								.getString(
										R.string.house_rule_add_new_condition_scene_no_select),
						WLToast.TOAST_SHORT);
			} else {
				if (sceneConditionListener != null) {
						sceneConditionListener.onSceneConditionChanged(sceneIdStr,
								TRIGGER_SCENE_IN, null);
				}
				mActivity.finish();
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
		if(StringUtil.equals("condition", condition)){
			getSupportActionBar().setIconText(R.string.house_rule_add_new_limit_condition);
		}else{
			getSupportActionBar().setIconText(R.string.about_back);
		}
		getSupportActionBar().setTitle(R.string.scene_select_scene_hint);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.finish();
				if(sceneConditionListener != null){
					sceneConditionListener.onSceneConditionChanged(null, null,null);
				}
			}
		});
	}
	
	public void onEventMainThread(SceneEvent event ){
		loadScenes();
	}
	
	public interface SceneConditionListener {
		public void onSceneConditionChanged(String sceneID,String sceneCondition,String des);
	}


}
