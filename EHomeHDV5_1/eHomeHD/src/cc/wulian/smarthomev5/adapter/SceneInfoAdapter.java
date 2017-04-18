package cc.wulian.smarthomev5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.SceneEditActivity;
import cc.wulian.smarthomev5.activity.TimingScenesActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionTaskActivity;
import cc.wulian.smarthomev5.activity.house.SceneTimingActionTaskActivity;
import cc.wulian.smarthomev5.adapter.SceneInfoAdapter.SceneEntity;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment.AddLinkTaskListener;
import cc.wulian.smarthomev5.fragment.scene.AddOrEditTimingSceneTimeFragment;
import cc.wulian.smarthomev5.fragment.scene.SceneEditFragment;
import cc.wulian.smarthomev5.fragment.scene.SceneTimingActionTaskFragment;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.MenuList;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.StateDrawableFactory;
import cc.wulian.smarthomev5.tools.StateDrawableFactory.Builder;
import cc.wulian.smarthomev5.utils.CmdUtil;
import de.greenrobot.event.EventBus;

public class SceneInfoAdapter extends WLBaseAdapter<SceneEntity> {
	public View state;
	public BaseActivity activity;
	public TimingSceneGroupEntity timingSceneGroup = TimingSceneManager
			.getInstance().getDefaultGroup();
	private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
			.getInstance();
	protected AccountManager mAccountManger = AccountManager.getAccountManger();
	private MenuList menuList;
	public Preference preference = Preference.getPreferences();
	private FavorityDao mFavorityDao = FavorityDao.getInstance();
	public SceneInfoAdapter(BaseActivity context) {
		super(context, new ArrayList<SceneEntity>());
		this.activity = context;
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.item_scene, parent, false);
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			SceneEntity item) {
		ImageButton mTimingSceneBt;
		// TextView mTimingSceneTv;
		// TextView mTimingSceneDe;

		ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
		TextView nameText = (TextView) view.findViewById(R.id.name);
		boolean isUsing = SceneManager.isSceneInUse(item.getStatus());
		Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(context,
				item.getIcon());
		Drawable checkedIcon = null;
		if (isUsing) {
			checkedIcon = SceneManager.getSceneIconDrawable_Bright(context,
					item.getIcon());
		} else {
			checkedIcon = SceneManager.getSceneIconDrawable_Black(context,
					item.getIcon());
		}

		Builder builder = StateDrawableFactory.makeSimpleStateDrawable(context,
				normalIcon, checkedIcon);

		iconImage.setImageDrawable(builder.create());
		nameText.setText(item.getName());

		state= view.findViewById(R.id.linearLayout_state);
		state.setSelected(isUsing);
		state.setOnClickListener(new OnClick(item));
		state.setOnLongClickListener(new SceneLongClick(pos));

		mTimingSceneBt = (ImageButton) view
				.findViewById(R.id.scene_timing_delbt);
		// mTimingSceneTv = (TextView) view.findViewById(R.id.scene_timing_tv);
		// mTimingSceneDe = (TextView)
		// view.findViewById(R.id.scene_timing_delete);

		String sceneID = item.getSceneID();

		
		boolean houseHasUpgrade = preference.getBoolean(
				IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
		if(!houseHasUpgrade){
			if (timingSceneGroup.contains(sceneID)) {
				item.setShowClock(true);
			} else {
				item.setShowClock(false);
			}
		}else{
			String programType = getSceneTimingTask(sceneID).getProgramType();
			if(!StringUtil.isNullOrEmpty(programType) && StringUtil.equals(programType, "1")){
				item.setShowClock(true);
			}else{
				item.setShowClock(false);
			}
		}
		
		if (item.isShowClock) {
			mTimingSceneBt.setVisibility(View.VISIBLE);
		} else {
			mTimingSceneBt.setVisibility(View.INVISIBLE);
		}

		// if (item.isShowClock() && item.isShowClickOne()) {
		// mTimingSceneTv.setVisibility(View.VISIBLE);
		// String mClockTime = timingSceneGroup.getTimingSceneEntity(item
		// .getSceneID()).time;
		// String mClockTimes = mClockTime.substring(0, 5);
		// mTimingSceneTv.setText(mClockTimes);
		// } else {
		// mTimingSceneTv.setVisibility(View.INVISIBLE);
		// }

		// if (item.isShowClock() && item.isShowClickLong()) {
		// mTimingSceneDe.setVisibility(View.VISIBLE);
		// } else {
		// mTimingSceneDe.setVisibility(View.INVISIBLE);
		// }
		// mTimingSceneBt.setOnClickListener(new OnTimingSceneClick(item));
		// mTimingSceneBt.setOnLongClickListener(new timingLongClick(item));
		// mTimingSceneDe.setOnClickListener(new OnTimingSceneDelClick(item));
	}

	public void addAllData(List<SceneInfo> newData) {
		getData().clear();
		if (newData == null) {
			notifyDataSetChanged();
			return;
		}
		for (SceneInfo info : newData) {
			SceneEntity entity = new SceneEntity();
			entity.setGwID(info.getGwID());
			entity.setGroupID(info.getGroupID());
			entity.setSceneID(info.getSceneID());
			entity.setName(info.getName());
			entity.setGroupName(info.getGroupName());
			entity.setStatus(info.getStatus());
			entity.setIcon(info.getIcon());
			entity.setShowClickOne(false);
			entity.setShowClickLong(false);
			entity.setShowClock(false);
			getData().add(entity);
		}
		notifyDataSetChanged();
	}

	// 点击闹铃图标监听事件
	// private final class OnTimingSceneClick implements View.OnClickListener {
	// private SceneEntity item;
	//
	// public OnTimingSceneClick(SceneEntity item) {
	// this.item = item;
	// }
	//
	// @Override
	// public void onClick(View v) {
	// this.item.setShowClickOne(!item.isShowClickOne());
	// this.item.setShowClickLong(false);
	// notifyDataSetChanged();
	// }
	//
	// }

	// 点击删除定时图标监听事件
	// private final class OnTimingSceneDelClick implements View.OnClickListener
	// {
	//
	// private SceneEntity item;
	//
	// public OnTimingSceneDelClick(SceneEntity item) {
	// this.item = item;
	// }
	//
	// @Override
	// public void onClick(View v) {
	// TimingSceneGroupEntity group = timingSceneGroup.clone();
	// group.removeTimingSceneEntity(item.getSceneID());
	// JsonTool.uploadTimingSceneList(CmdUtil.MODE_ADD,
	// group);
	// }
	//
	// }

	private final  class OnClick implements View.OnClickListener {
		private final SceneInfo item;

		public OnClick(SceneInfo item) {
			this.item = item;
		}

		@Override
		public void onClick(View v) {
			//对除了点击的场景之外的场景进行初始化状态
			initSceneStatus(item);
			SceneInfo newSceneInfo = item.clone();
			if (SceneManager.isSceneInUse(newSceneInfo.getStatus())) {
				newSceneInfo.setStatus(CmdUtil.SCENE_UNUSE);
			} else {
				newSceneInfo.setStatus(CmdUtil.SCENE_USING);
			}
			SceneManager.switchSceneInfo(mContext, newSceneInfo, true);
			notifyDataSetChanged();
		}
	}

	// 长按闹铃图标监听事件
	// private final class timingLongClick implements View.OnLongClickListener {
	// private SceneEntity item;
	//
	// public timingLongClick(SceneEntity item) {
	// this.item = item;
	// }
	//
	// @Override
	// public boolean onLongClick(View view) {
	// this.item.setShowClickLong(!this.item.isShowClickLong());
	// this.item.setShowClickOne(false);
	// notifyDataSetChanged();
	// return false;
	// }
	//
	// }

	private final class SceneLongClick implements View.OnLongClickListener {

		private int pos;

		public SceneLongClick(int pos) {
			this.pos = pos;
		}

		@Override
		public boolean onLongClick(View v) {
			iniPopupWidow(v, pos);
			return true;
		}

	}

	private AutoProgramTaskInfo getSceneOrdinaryTask(String sceneID) {
		AutoProgramTaskInfo info =  autoProgramTaskManager.getAutoProgramTypeScene(sceneID);
		if(info == null)
			info = new AutoProgramTaskInfo();
		return info;
			
	}
	
	public void initSceneStatus(SceneInfo item) {
		int dataSize=getData().size();
		for(int i=0;i<dataSize;i++){
			SceneInfo newSceneInfo=getItem(i).clone();
			if (SceneManager.isSceneInUse(newSceneInfo.getStatus())) {
				if(!newSceneInfo.equals(item)){
					newSceneInfo.setStatus(CmdUtil.SCENE_UNUSE);
//					SceneManager.switchSceneInfo(mContext, newSceneInfo, true);
				}
			}
		}
	}

	public AutoProgramTaskInfo getSceneTimingTask(String sceneID) {
		AutoProgramTaskInfo info = autoProgramTaskManager.getAutoProgramTypeTime(sceneID);
		if(info == null)
			info = new AutoProgramTaskInfo();
		return info;
	}
	
	
	private void iniPopupWidow(View v, final int pos) {

		final DownUpMenuList downMenu = new DownUpMenuList(mContext);
		DownUpMenuItem itemEditScene = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.scene_info_edit_scene));
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_topcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.SCENE_EDIT)) {
					return;
				}

				Bundle args = new Bundle();
				final SceneInfo info = getItem(pos);
				if(!preference.getBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false)){
					args.putSerializable(SceneEditFragment.EXTRA_SCENE_INFO, info);
					activity.JumpTo(SceneEditActivity.class, args);
				}else{
					HouseKeeperActionTaskFragment.setAddLinkDeviceListener(new AddLinkTaskListener() {
						
						@Override
						public void onAddLinkTaskListenerChanged(AutoProgramTaskInfo taskInfo) {
							if(taskInfo != null){
								
								if(taskInfo.getActionList().size() == 0 && !StringUtil.isNullOrEmpty( taskInfo.getProgramID())){
									JsonTool.deleteAndQueryAutoTaskList("D", taskInfo);
								}else if(taskInfo.getActionList().size() != 0){
									String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
									String programID = taskInfo.getProgramID();
									String operType = "";
									if(!StringUtil.isNullOrEmpty(programID)){
										operType = "U";
									}else{
										operType = "C";
									}
									String programName = info.getSceneID();
									String programDesc = ""; 
									String programType = "0"; 
									String status = "2"; 
									JSONObject jsonObj = new JSONObject();
									jsonObj.put("type", "0");
									jsonObj.put("object", info.getSceneID());
									jsonObj.put("exp", "on");
									JSONArray triggerArray = new JSONArray();
									triggerArray.add(jsonObj);
									
//									JSONArray conditionArray = new JSONArray();
									JSONArray actionArray = new JSONArray();
									for(int i=0; i < taskInfo.getActionList().size(); i++){
										AutoActionInfo info = taskInfo.getActionList().get(i);
										JSONObject obj = new JSONObject();
										JsonTool.makeTaskActionJSONObject(obj,info);
										actionArray.add(obj);
									}
									NetSDK.sendSetProgramTask(gwID, operType, programID, programName, programDesc,programType, status, triggerArray, null, actionArray);
								
								}
							}
							
						}
					});
					AutoProgramTaskInfo taskInfo = getSceneOrdinaryTask(info.getSceneID());
					args.putSerializable("AutoProgramTaskInfo", taskInfo);
					args.putString(HouseKeeperActionTaskFragment.LINK_LIST_PROGRAMTYPE_KEY, "0");
					args.putString(HouseKeeperActionTaskFragment.LINK_LIST_SCENCE_NAME,info.getName());
					activity.JumpTo(HouseKeeperActionTaskActivity.class, args);
				}
				
				downMenu.dismiss();
			}
		};

		DownUpMenuItem itemTiming = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.scene_info_timing_scene));
				mTitleTextView.setBackgroundResource(R.drawable.announcement_item_seletor);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.SCENE_TIMING)) {
					return;
				}

				Bundle sceneargs = new Bundle();
				SceneInfo sceneinfo = getItem(pos);
				
				if(!preference.getBoolean(
						IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false)){
					sceneargs.putSerializable(
							AddOrEditTimingSceneTimeFragment.SCENE_INFO_SERIAL,
							sceneinfo);
					activity.JumpTo(TimingScenesActivity.class, sceneargs);
				}else{
					sceneargs.putSerializable(SceneTimingActionTaskFragment.SCENE_INFO_TIMING,sceneinfo.getSceneID());
					SceneTimingActionTaskFragment.autoProgramTaskInfo = getSceneTimingTask(sceneinfo.getSceneID());
					activity.JumpTo(SceneTimingActionTaskActivity.class, sceneargs);
				}

				downMenu.dismiss();
			}
		};
		DownUpMenuItem itemRename = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.scene_info_rename_scene));
				mTitleTextView.setBackgroundResource(R.drawable.announcement_item_seletor);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.SCENE_RENAME)) {
					return;
				}

				SceneManager.editSceneInfo(mContext, getItem(pos));
				downMenu.dismiss();
			}
		};

		DownUpMenuItem itemDelete = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.scene_info_delete_scene));
				mTitleTextView.setBackgroundResource(R.drawable.announcement_item_seletor);
			}

			@Override
			public void doSomething() {
				
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.SCENE_RENAME)) {
					return;
				}
				
				SceneInfo sceneinfo = getItem(pos);
				AutoProgramTaskInfo timingInfo = getSceneTimingTask(sceneinfo.getSceneID());
				AutoProgramTaskInfo sceneTaskInfo = getSceneOrdinaryTask(sceneinfo.getSceneID());
				SceneManager.deleteSceneInfo(mContext, sceneinfo,timingInfo,sceneTaskInfo);
				downMenu.dismiss();
			}
		};
		
		DownUpMenuItem favorityItem = new DownUpMenuItem(mContext) {
			@Override
			public void initSystemState() {
				if(isFavority(pos)){
					mTitleTextView.setText(mContext.getResources().getString(
							R.string.scene_info_cancel_favority_scene));
				}else{
					mTitleTextView.setText(mContext.getResources().getString(
							R.string.scene_info_favority_scene));
				}
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_bottomcircle);
			}

			@Override
			public void doSomething() {
				if(isFavority(pos)){
					mFavorityDao.delete(getFavorityEntity(pos));
				}else{
					SceneInfo sceneinfo = getItem(pos);
					FavorityEntity favorityEntity = new FavorityEntity();
					favorityEntity.setGwID(sceneinfo.getGwID());
					favorityEntity.setType(Favority.TYPE_SCENE);
					favorityEntity.setOperationID(sceneinfo.getSceneID());
					favorityEntity.setOrder(Favority.OPERATION_AUTO);
					mFavorityDao.operateFavorityDao(favorityEntity);
				}
				EventBus.getDefault().post(new SceneEvent(null, false, null));
				downMenu.dismiss();
			}
			
			private Boolean isFavority(final int pos) {
				boolean isFavority = false;
				FavorityEntity entity = getFavorityEntity(pos);
				if(!StringUtil.isNullOrEmpty(entity.getOperationID())){
					isFavority = true;
				}
				return isFavority;
			}

			private FavorityEntity getFavorityEntity(final int pos) {
				SceneInfo sceneinfo = getItem(pos);
				FavorityEntity favorityEntity = new FavorityEntity();
				favorityEntity.setGwID(sceneinfo.getGwID());
				favorityEntity.setOperationID(sceneinfo.getSceneID());
				FavorityEntity entity = mFavorityDao.getById(favorityEntity);
				return entity;
			}
		};

		DownUpMenuItem cancelItem = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.cancel));
				linearLayout.setPadding(0, 30, 0, 0);
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_allcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				downMenu.dismiss();
			}
		};
		
		ArrayList<DownUpMenuItem> menuItems = new ArrayList<DownUpMenuItem>();
		menuItems.add(itemEditScene);
		menuItems.add(itemTiming);
		menuItems.add(itemRename);
		menuItems.add(itemDelete);
		menuItems.add(favorityItem);
		menuItems.add(cancelItem);
		downMenu.setMenu(menuItems);
		downMenu.showBottom(v);
		// menuList = new MenuList(mContext, R.string.hint_select_scene);
		// MenuItem itemEdit = new MenuItem(mContext) {
		//
		// @Override
		// public void initSystemState() {
		// titleTextView.setText(mContext.getResources().getString(
		// R.string.scene_info_edit_scene));
		// }
		//
		// @Override
		// public void doSomething() {
		// Bundle args = new Bundle();
		// SceneInfo info = getItem(pos);
		// args.putSerializable(SceneEditFragment.EXTRA_SCENE_INFO,
		// info);
		// activity.JumpTo(SceneEditActivity.class, args);
		// menuList.dismiss();
		// }
		// };
		// MenuItem itemTiming = new MenuItem(mContext) {
		//
		// @Override
		// public void initSystemState() {
		// titleTextView.setText(mContext.getResources().getString(
		// R.string.scene_info_timing_scene));
		// }
		//
		// @Override
		// public void doSomething() {
		// Bundle sceneargs = new Bundle();
		// SceneInfo sceneinfo = getItem(pos);
		// sceneargs.putSerializable(
		// AddOrEditTimingSceneTimeFragment.SCENE_INFO_SERIAL, sceneinfo);
		// activity.JumpTo(TimingScenesActivity.class, sceneargs);
		// menuList.dismiss();
		// }
		// };
		// MenuItem itemRename = new MenuItem(mContext) {
		//
		// @Override
		// public void initSystemState() {
		// titleTextView.setText(mContext.getResources().getString(
		// R.string.scene_info_rename_scene));
		// }
		//
		// @Override
		// public void doSomething() {
		// SceneManager.editSceneInfo(mContext, false, getItem(pos));
		// menuList.dismiss();
		// }
		// };
		//
		// MenuItem itemDelete = new MenuItem(mContext) {
		//
		// @Override
		// public void initSystemState() {
		// titleTextView.setText(mContext.getResources().getString(
		// R.string.scene_info_delete_scene));
		// }
		//
		// @Override
		// public void doSomething() {
		// SceneManager.deleteSceneInfo(mContext, getItem(pos));
		// menuList.dismiss();
		// }
		// };
		// ArrayList<MenuItem> menus = new ArrayList<MenuList.MenuItem>();
		// menus.add(itemEdit);
		// menus.add(itemTiming);
		// menus.add(itemRename);
		// menus.add(itemDelete);
		// menuList.addMenu(menus);
		// menuList.show(v);
	}

	public static class SceneEntity extends SceneInfo {
		private static final long serialVersionUID = 1L;
		public boolean isShowClock;
		private boolean isShowClickOne;
		private boolean isShowClickLong;

		public SceneEntity() {
		}

		public boolean isShowClock() {
			return isShowClock;
		}

		public void setShowClock(boolean isShowClock) {
			this.isShowClock = isShowClock;
		}

		public boolean isShowClickOne() {
			return isShowClickOne;
		}

		public void setShowClickOne(boolean isShowClickOne) {
			this.isShowClickOne = isShowClickOne;
		}

		public boolean isShowClickLong() {
			return isShowClickLong;
		}

		public void setShowClickLong(boolean isShowClickLong) {
			this.isShowClickLong = isShowClickLong;
		}

	}
}
