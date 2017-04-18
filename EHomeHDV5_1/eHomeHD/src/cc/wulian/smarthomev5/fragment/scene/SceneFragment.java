package cc.wulian.smarthomev5.fragment.scene;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionTaskActivity;
import cc.wulian.smarthomev5.adapter.SceneInfoAdapter;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.fragment.house.AutoTaskEvent;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.view.SceneDefaultManager;
import cc.wulian.smarthomev5.view.WLEditText;

public class SceneFragment extends WulianFragment {
    private WLEditText searchEditText;
    private ImageView sortSearchImageView;
    private TextView classifySearchTextView;
    public GridView mSceneGridView;
    public SceneInfoAdapter mSceneEditAdapter;
    private SceneDao sceneDao = SceneDao.getInstance();
    private SceneDefaultManager manager;
    private static SceneRemindPopuwindow reminMenu;
    private String isSelectKey;
    public TextView emptyTextView;
    private boolean iscontainsDevice;
    private String currentClassify;
    private String currentSort;
    List<SceneInfo> infos;
    private Preference preference = Preference.getPreferences();


    private Comparator<SceneInfo> sceneSortComparator = new Comparator<SceneInfo>() {

        @Override
        public int compare(SceneInfo lhs, SceneInfo rhs) {
            String leftSceneName = lhs.getName();
            String leftSceneID = lhs.getSceneID();
            String rightSceneName = rhs.getName();
            String rightSceneID = rhs.getSceneID();
            int result = Trans2PinYin
                    .trans2PinYin(leftSceneName.trim())
                    .toLowerCase()
                    .compareTo(
                            Trans2PinYin.trans2PinYin(rightSceneName.trim())
                                    .toLowerCase());
            if (result != 0) {
                return result;
            } else {
                return leftSceneID.compareTo(rightSceneID);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBar();
        manager = new SceneDefaultManager(mActivity);
        mSceneEditAdapter = new SceneInfoAdapter(mActivity);
        createSceneDefaultDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scene, container, false);
    }

    public void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayShowMenuEnabled(true);
        getSupportActionBar().setTitle(
                mApplication.getResources().getString(R.string.nav_scene_title));
        getSupportActionBar().setRightIcon(R.drawable.common_use_add);
        getSupportActionBar().setRightMenuClickListener(
                new OnRightMenuClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add by yanzy:不允许被授权用户使用
                        if (!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.SCENE_ADD)) {
                            return;
                        }

                        SceneManager.editSceneInfo(getActivity(), null);
                    }
                });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchEditText = (WLEditText) view
                .findViewById(R.id.house_task_choose_device_search);
        sortSearchImageView = (ImageView) view
                .findViewById(R.id.scene_search_sort);
        classifySearchTextView = (TextView) view
                .findViewById(R.id.scene_search_classify);
        mSceneGridView = (GridView) view.findViewById(R.id.gridViewShowInfo);
        emptyTextView = (TextView) view.findViewById(android.R.id.empty);
        //mSceneEditAdapter是一个对象，不能用来判断是否为空,即使判断是否为空，也是判断.getCount是否为零----这句为错误代码
        //应该在OnResum()加载Adapter中通过访问缓存中是否为空来判断
//		if(mSceneEditAdapter.getCount() == 0){
//			showDialog();
//		}
        mSceneGridView.setAdapter(mSceneEditAdapter);
        searchEditText.registWLIputTextWatcher(new EditTextWatcher());
        sortSearchImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSearchSortPopupWindow(v);
            }
        });
        classifySearchTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSearchClassifyPopupWindow(v);
            }
        });
    }


    public void updateSceneEmptyText() {
        Collection<WulianDevice> deviceCollection = DeviceCache.getInstance(mActivity).getAllDevice();
        if (deviceCollection.isEmpty()) {
            emptyTextView.setText(mActivity.getString(R.string.scene_no_device_no_add_scene_hint));
        } else {
            for (WulianDevice device : deviceCollection) {
                if (mAccountManger.getmCurrentInfo().getGwID().equals(device.getDeviceGwID())) {
                    emptyTextView.setText(mActivity.getString(R.string.scene_no_scene_add_hint));
                    break;
                } else {
                    emptyTextView.setText(mActivity.getString(R.string.scene_no_device_no_add_scene_hint));
                }
            }
        }
        mSceneGridView.setEmptyView(emptyTextView);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchEditText.setText("");
        //在执行onViewCreated之后，是从缓存里面查有场景，然后通过加载到adapter里面，然后刷新显示
        TaskExecutor.getInstance().executeDelay(new Runnable() {
            @Override
            public void run() {
                loadScenes();
            }
        }, 500);
    }

    public void loadScenes() {
        infos = new ArrayList<SceneInfo>();

        if (currentClassify == null) {
            SceneInfo info = new SceneInfo();
            info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
            infos = sceneDao.findListAll(info);
        } else {
            FavorityEntity mEntity = new FavorityEntity();
            mEntity.setGwID(mAccountManger.getmCurrentInfo().getGwID());
            mEntity.setType(Favority.TYPE_SCENE);
            FavorityDao favorityDao = FavorityDao.getInstance();
            List<FavorityEntity> favorityEntities = favorityDao
                    .findListAll(mEntity);
            for (FavorityEntity entity : favorityEntities) {
                SceneInfo sceneInfo = MainApplication.getApplication().sceneInfoMap
                        .get(entity.getGwID() + entity.getOperationID());
                if (sceneInfo != null) {
                    infos.add(sceneInfo);
                } else {
                    favorityDao.delete(entity);
                }
            }
        }

        if (mApplication.getResources().getString(
                R.string.scene_list_name_sort).equals(currentSort)) {
            Collections.sort(infos, sceneSortComparator);
        }
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mSceneEditAdapter.addAllData(infos);
                updateSceneEmptyText();
            }
        });
    }

    public void createSceneDefaultDialog() {
        if (!mAccountManger.isConnectedGW())
            return;
        Collection<WulianDevice> deviceCollection = DeviceCache.getInstance(mActivity).getAllDevice();
        if (!deviceCollection.isEmpty()) {
            for (WulianDevice device : deviceCollection) {
                if (mAccountManger.getmCurrentInfo().getGwID().equals(device.getDeviceGwID())) {
                    iscontainsDevice = true;
                    break;
                }
            }
        }
        isSelectKey = Preference.getPreferences().getDefaultSceneSelect();
        if (mApplication.sceneInfoMap.size() == 0 && StringUtil.isNullOrEmpty(isSelectKey) && mAccountManger.isConnectedGW() && iscontainsDevice) {
            //new一个默认场景管理类对象，来调用里面提供的创建Dialog的功能--面向对象的思想
            manager.createDefaultScenesDialog();
            preference.saveDefaultSceneSelect("-1");
        } else {
            manager.preference.saveDefaultSceneSetting("-1");
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        initBar();
        loadScenes();
    }

    public void onEventMainThread(SceneEvent event) {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                loadScenes();
            }
        });
        final SceneInfo scene = event.sceneInfo;
        if (manager.isFirst == 2) {
            TaskExecutor.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    manager.createDefaultSceneDevice(scene);
                }
            });
            //更新UI的内容需放在主线程操作
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    manager.upDefaultSceneProgressDialog();
                }
            });
        }
        showHand();
       //若为新建场景，则直接跳转到场景编辑界面
        if(StringUtil.equals(event.action , CmdUtil.MODE_ADD)){
            if(scene != null){
                jumpToNewSenceEditFragment(scene);
            }
        }
    }

    private void jumpToNewSenceEditFragment(final SceneInfo newScene){
        Bundle args = new Bundle();
        HouseKeeperActionTaskFragment.setAddLinkDeviceListener(new HouseKeeperActionTaskFragment.AddLinkTaskListener() {

            @Override
            public void onAddLinkTaskListenerChanged(AutoProgramTaskInfo taskInfo) {
                if(taskInfo != null){

                    if(taskInfo.getActionList().size() == 0 && !StringUtil.isNullOrEmpty( taskInfo.getProgramID())){
                        JsonTool.deleteAndQueryAutoTaskList("D", taskInfo);
                    }
                    else if(taskInfo.getActionList().size() != 0){
                        String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
                        String programID = taskInfo.getProgramID();
                        String operType = "";
                        if(!StringUtil.isNullOrEmpty(programID)){
                            operType = "U";
                        }else{
                            operType = "C";
                        }
                        String programName = newScene.getSceneID();
                        String programDesc = "";
                        String programType = "0";
                        String status = "2";
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("type", "0");
                        jsonObj.put("object", newScene.getSceneID());
                        jsonObj.put("exp", "on");
                        JSONArray triggerArray = new JSONArray();
                        triggerArray.add(jsonObj);
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

        AutoProgramTaskInfo taskInfo = getSceneOrdinaryTask(newScene.getSceneID());
        args.putSerializable("AutoProgramTaskInfo", taskInfo);
        args.putString(HouseKeeperActionTaskFragment.LINK_LIST_PROGRAMTYPE_KEY, "0");
        args.putString(HouseKeeperActionTaskFragment.LINK_LIST_SCENCE_NAME,newScene.getName());
        Intent intent = new Intent(mActivity,HouseKeeperActionTaskActivity.class);
        if (args != null) {
            intent.putExtras(args);
        }
        mActivity.startActivity(intent);
    }

    private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
            .getInstance();
    private AutoProgramTaskInfo getSceneOrdinaryTask(String sceneID) {
        AutoProgramTaskInfo info =  autoProgramTaskManager.getAutoProgramTypeScene(sceneID);
        if(info == null)
            info = new AutoProgramTaskInfo();
        return info;

    }

    /**
     * 判断是否第一次显示手势
     */
    private void showHand() {
        manager.isHandRemindKey = Preference.getPreferences().getDefaultSceneSetting();
        if (StringUtil.isNullOrEmpty(manager.isHandRemindKey) && manager.isFirst == 1) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    reminMenu = new SceneRemindPopuwindow(mActivity);
                    reminMenu.showBottom();
                }

            });
            manager.isFirst = 0;
            manager.preference.saveDefaultSceneSetting("-1");
        }
    }

    public void onEventMainThread(TimingSceneEvent event) {
        mSceneEditAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(AutoTaskEvent event) {
        if (StringUtil.equals(event.action, AutoTaskEvent.ADDRULE) || StringUtil.equals(event.action, AutoTaskEvent.REMOVE)) {
            mSceneEditAdapter.notifyDataSetChanged();
        }
    }

    private void showSearchClassifyPopupWindow(View view) {
        final MoreMenuPopupWindow classifyGroupPopupWindow = new MoreMenuPopupWindow(
                mActivity);
        List<MoreMenuPopupWindow.MenuItem> items = new ArrayList<MoreMenuPopupWindow.MenuItem>();
        items.add(new MoreMenuPopupWindow.MenuItem(mActivity) {

            @Override
            public void initSystemState() {
                iconImageView.setVisibility(View.GONE);
                iconImageViewRight.setVisibility(View.VISIBLE);
                titleTextView.setText(mApplication.getResources().getString(
                        R.string.scene_list_all_scene));
                iconImageViewRight
                        .setImageResource(R.drawable.device_category_group__search_item_selector);
                if (currentClassify == null)
                    iconImageViewRight.setSelected(true);
                else
                    iconImageViewRight.setSelected(false);
            }

            @Override
            public void doSomething() {
                if (currentClassify != null)
                    currentClassify = null;
                loadScenes();
                classifyGroupPopupWindow.dismiss();
            }
        });
        items.add(new MoreMenuPopupWindow.MenuItem(mActivity) {

            @Override
            public void initSystemState() {
                iconImageView.setVisibility(View.GONE);
                iconImageViewRight.setVisibility(View.VISIBLE);
                titleTextView.setText(mApplication.getResources().getString(
                        R.string.scene_list_commonly_used_scene));
                iconImageViewRight
                        .setImageResource(R.drawable.device_category_group__search_item_selector);
                if (mApplication.getResources().getString(
                        R.string.scene_list_commonly_used_scene).equals(currentClassify))
                    iconImageViewRight.setSelected(true);
                else
                    iconImageViewRight.setSelected(false);
            }

            @Override
            public void doSomething() {
                if (mApplication.getResources().getString(
                        R.string.scene_list_commonly_used_scene).equals(currentClassify)) {
                    currentClassify = null;
                } else {
                    currentClassify = mApplication.getResources().getString(
                            R.string.scene_list_commonly_used_scene);
                }
                loadScenes();
                classifyGroupPopupWindow.dismiss();
            }
        });
        classifyGroupPopupWindow.setMenuItems(items);
        classifyGroupPopupWindow.show(view, -10, 2, 200);
        classifyGroupPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if (StringUtil.isNullOrEmpty(currentClassify)) {
                    classifySearchTextView.setText(mApplication.getResources().getString(
                            R.string.scene_list_all));
                } else {
                    classifySearchTextView.setText(mApplication.getResources().getString(
                            R.string.scene_list_commonly_used));
                }
            }
        });
    }

    private void showSearchSortPopupWindow(View view) {
        final MoreMenuPopupWindow sortGroupPopupWindow = new MoreMenuPopupWindow(
                mActivity);
        List<MoreMenuPopupWindow.MenuItem> items = new ArrayList<MoreMenuPopupWindow.MenuItem>();
        items.add(new MoreMenuPopupWindow.MenuItem(mActivity) {

            @Override
            public void initSystemState() {
                iconImageView.setVisibility(View.GONE);
                iconImageViewRight.setVisibility(View.VISIBLE);
                titleTextView.setText(mApplication.getResources().getString(
                        R.string.scene_list_time_sort));
                iconImageViewRight
                        .setImageResource(R.drawable.device_category_group__search_item_selector);
                if (currentSort == null)
                    iconImageViewRight.setSelected(true);
                else
                    iconImageViewRight.setSelected(false);
            }

            @Override
            public void doSomething() {
                if (currentSort != null)
                    currentSort = null;
                loadScenes();
                sortGroupPopupWindow.dismiss();
            }
        });
        items.add(new MoreMenuPopupWindow.MenuItem(mActivity) {

            @Override
            public void initSystemState() {
                iconImageView.setVisibility(View.GONE);
                iconImageViewRight.setVisibility(View.VISIBLE);
                titleTextView.setText(mApplication.getResources().getString(
                        R.string.scene_list_name_sort));
                iconImageViewRight
                        .setImageResource(R.drawable.device_category_group__search_item_selector);
                if (mApplication.getResources().getString(
                        R.string.scene_list_name_sort).equals(currentSort))
                    iconImageViewRight.setSelected(true);
                else
                    iconImageViewRight.setSelected(false);
            }

            @Override
            public void doSomething() {
                if (mApplication.getResources().getString(
                        R.string.scene_list_name_sort).equals(currentSort)) {
                    currentSort = null;
                } else {
                    currentSort = mApplication.getResources().getString(
                            R.string.scene_list_name_sort);
                }
                loadScenes();
                sortGroupPopupWindow.dismiss();
            }
        });

        sortGroupPopupWindow.setMenuItems(items);
        sortGroupPopupWindow.show(view, -300, 2, 200);
        sortGroupPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
            if (StringUtil.isNullOrEmpty(currentSort)) {
                sortSearchImageView.setImageResource(R.drawable.scene_search_sort_time);
            } else {
                sortSearchImageView.setImageResource(R.drawable.scene_search_sort_name);
            }
            }
        });
    }

    private class EditTextWatcher implements WLEditText.WLInputTextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            getSearchScene(s.toString(), 10);
        }
    }

    private void getSearchScene(final String searchKey, final int pageSize) {
        final List<SceneInfo> result = new ArrayList<SceneInfo>();
        if (StringUtil.isNullOrEmpty(searchKey) || infos == null) {
            mSceneEditAdapter.addAllData(infos);
            updateSceneEmptyText();
        } else {
            TaskExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Set<SceneInfo> allSet = new LinkedHashSet<SceneInfo>();
                    String key = searchKey.toLowerCase().trim();
                    boolean isOver = false;
                    for (int i = 0; i < infos.size(); i++) {
                        SceneInfo sceneInfo = infos.get(i);
                        String sceneName = sceneInfo.getName().toLowerCase().trim();
                        if (StringUtil.isNullOrEmpty(sceneName))
                            continue;
                        if (Trans2PinYin.isFirstCharacter(key, sceneName)) {
                            allSet.add(sceneInfo);
                        }
                        if (allSet.size() >= pageSize) {
                            isOver = true;
                            break;
                        }

                    }
                    if (!isOver) {
                        for (int i = 0; i < infos.size(); i++) {
                            SceneInfo sceneInfo = infos.get(i);
                            String sceneName = sceneInfo.getName().toLowerCase()
                                    .trim();
                            if (StringUtil.isNullOrEmpty(sceneName))
                                continue;
                            if (Trans2PinYin.isStartPinYin(key, sceneName)) {
                                allSet.add(sceneInfo);
                            }
                            if (allSet.size() >= pageSize) {
                                isOver = true;
                                break;
                            }
                        }
                    }
                    if (!isOver) {
                        for (int i = 0; i < infos.size(); i++) {
                            SceneInfo sceneInfo = infos.get(i);
                            String sceneName = sceneInfo.getName().toLowerCase()
                                    .trim();
                            if (StringUtil.isNullOrEmpty(sceneName))
                                continue;
                            if (Trans2PinYin.isContainsPinYin(key, sceneName)) {
                                allSet.add(sceneInfo);
                            }
                            if (allSet.size() >= pageSize) {
                                isOver = true;
                                break;
                            }

                        }
                    }
                    result.addAll(allSet);
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mSceneEditAdapter.addAllData(result);
                            updateSceneEmptyText();
                        }
                    });
                }
            });

        }
    }

}
