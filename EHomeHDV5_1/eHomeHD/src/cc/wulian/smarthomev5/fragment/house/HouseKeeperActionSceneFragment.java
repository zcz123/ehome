package cc.wulian.smarthomev5.fragment.house;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.adapter.HouseKeeperActionSceneAdapter;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.view.SceneDefaultManager;
import cc.wulian.smarthomev5.view.WLEditText;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class HouseKeeperActionSceneFragment extends WulianFragment {
    private WLEditText searchEditText;
    private ImageView sortSearchImageView;
    private TextView classifySearchTextView;
    public GridView mSceneGridView;
    public HouseKeeperActionSceneAdapter mSceneEditAdapter;
    private SceneDao sceneDao = SceneDao.getInstance();
    private SceneDefaultManager manager;
    private String isSelectKey;
    public TextView emptyTextView;
    private boolean iscontainsDevice;
    private String currentClassify;
    private String currentSort;
    private SceneInfo currentSelectScene;
    List<SceneInfo> infos;
    private Preference preference = Preference.getPreferences();

    private static HouseKeeperActionSceneFragment.AddLinkSceneListener addLinkSceneListener;

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
        initBarChange();
        manager = new SceneDefaultManager(mActivity);
        mSceneEditAdapter = new HouseKeeperActionSceneAdapter(mActivity);
        createSceneDefaultDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scene, container, false);
    }

    public void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIconText(R.string.cancel);
        getSupportActionBar().setTitle(R.string.scene_select_scene_hint);
        getSupportActionBar().setDisplayShowMenuTextEnabled(true);
        getSupportActionBar().setRightIconText(R.string.common_ok);
        getSupportActionBar()
                .setRightGrayIconText(
                        getResources()
                                .getString(
                                        R.string.common_ok),
                        getResources().getColor(R.color.white));
        getSupportActionBar().setRightMenuClickListener(
                new ActionBarCompat.OnRightMenuClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<AutoActionInfo> sceneTasks = new ArrayList<AutoActionInfo>();
                        AutoActionInfo actionInfo = new AutoActionInfo();
                        actionInfo.setType("0");
                        actionInfo.setObject(currentSelectScene.getSceneID());
                        actionInfo.setEpData("2");
                        sceneTasks.add(actionInfo);
                        if (addLinkSceneListener != null) {
                            addLinkSceneListener
                                    .onAddLinkSceneListenerChanged(sceneTasks);
                            addLinkSceneListener = null;
                        }
                        mActivity.finish();
                    }
                });
    }

    private void initBarChange() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIconText(R.string.cancel);
        getSupportActionBar().setTitle(R.string.scene_select_scene_hint);
        getSupportActionBar().setDisplayShowMenuTextEnabled(true);
        getSupportActionBar().setRightIconText(R.string.common_ok);
        getSupportActionBar()
                .setRightGrayIconText(
                        getResources()
                                .getString(
                                        R.string.common_ok),
                        getResources().getColor(R.color.v5_gray_mid));
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
        searchEditText.registWLIputTextWatcher(new HouseKeeperActionSceneFragment.EditTextWatcher());
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
        mSceneGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                initBar();

                currentSelectScene = mSceneEditAdapter.getItem(position);

                mSceneEditAdapter.setSeclection(position);
                mSceneEditAdapter.notifyDataSetChanged();
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
    }


    private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
            .getInstance();
    private AutoProgramTaskInfo getSceneOrdinaryTask(String sceneID) {
        AutoProgramTaskInfo info =  autoProgramTaskManager.getAutoProgramTypeScene(sceneID);
        if(info == null)
            info = new AutoProgramTaskInfo();
        return info;

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
        classifyGroupPopupWindow.show(view);
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
        sortGroupPopupWindow.show(view, -300, 2, 180);
        sortSearchImageView.setSelected(true);
        sortGroupPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                sortSearchImageView.setSelected(false);
            }
        });
    }

    public static void setAddLinkSceneListener(
            HouseKeeperActionSceneFragment.AddLinkSceneListener addLinkDeviceListener) {
        HouseKeeperActionSceneFragment.addLinkSceneListener = addLinkDeviceListener;
    }

    public interface AddLinkSceneListener {
        public void onAddLinkSceneListenerChanged(List<AutoActionInfo> infos);
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
