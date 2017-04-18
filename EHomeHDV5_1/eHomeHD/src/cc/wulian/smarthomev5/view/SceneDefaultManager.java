package cc.wulian.smarthomev5.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.adapter.SceneDefaultAdapter;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.fragment.scene.SceneRemindPopuwindow;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.CountDownTimer;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class SceneDefaultManager{

	public static final String SCENE_ICON_BACK_HOME = "0";
	public static final String SCENE_ICON_AWAY_HOME = "1";
	public static final String SCENE_ICON_SLEEP = "2";
	public static final String SCENE_ICON_GET_UP = "4";
	public static final String SCENE_ICON_ALL_OPEN = "9";
	public static final String SCENE_ICON_ALL_CLOSE = "10";
	private static Context mContext;
	private LayoutInflater inflater;
	private DeviceDao deviceDao;
	private SceneDefaultAdapter sceneDefaultAdapter;
	private DeviceCache mDeviceCache;
	private AccountManager accountManager = AccountManager.getAccountManger() ;
	private UpdateProcessDialog progressDialog;
	private int progress;
	private SceneRemindPopuwindow reminMenu;
	private final MainApplication mApplication;
	public Preference preference = Preference.getPreferences();
	public String isHandRemindKey;
	public int isFirst = 0;
	private WLDialog dialog;
	
	public SceneDefaultManager(Context context){
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mApplication = MainApplication.getApplication();
		mDeviceCache = DeviceCache.getInstance(mContext);
	}
	private View buildView(){
		LinearLayout mDefSceneLayout = (LinearLayout) inflater.inflate(R.layout.scene_default_edit_dialog, null);
		ListView mSceneList = (ListView) mDefSceneLayout.findViewById(R.id.scene_default_grid);
		sceneDefaultAdapter = new SceneDefaultAdapter(mContext, SceneManager.createDefaultScenes(mContext));
		mSceneList.setAdapter(sceneDefaultAdapter);
		mSceneList.clearChoices();
		mSceneList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//GridView选中的点击事件，改变子类ChexkBox的选中状态，item类通过Map集合来存储CheckBox选中状态
				sceneDefaultAdapter.setSelection(position);
			}
		});
		return mDefSceneLayout;
	}
	/**
	 * 提供从Adapter中获取-->被选中场景相应集合的方法
	 */
	public List<SceneInfo> getCheckedSceneInfos(){
		return sceneDefaultAdapter.getCheckedSceneInfos();
	}
	
	public void createDefaultScenesDialog() {
		progressDialog = new UpdateProcessDialog(mContext);
		class AddSceneListener implements MessageListener {

			@Override
			public void onClickPositive(View contentViewLayout) {
				//循环遍历 选中相应场景的信息-->List<SceneInfo>集合
				if(getCheckedSceneInfos().size() != 0){
					progressDialog.show();
					timer.start();
					saveDefaultScene();
					isFirst = 2;
				}else{
					isFirst = 1;
				}
			}

			@Override
			public void onClickNegative(View contentViewLayout) {
				isFirst = 1;
			}
		}
		AddSceneListener addSceneListener = new AddSceneListener();
		WLDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.scene_default_dialog_title)
				.setSubTitleText(R.string.scene_default_dialog_bottom_title)
				.setContentView(buildView())
				.setHeightPercent(0.8F)
				.setPositiveButton(
						mContext.getResources().getString(
								R.string.common_ok))
				.setNegativeButton(
						mContext.getResources().getString(
								R.string.guide_skip))
				.setListener(addSceneListener);
		if(dialog == null){
			dialog = builder.create();
			dialog.show();
		}
	}
	
	private void saveDefaultScene(){
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				for(SceneInfo info : getCheckedSceneInfos()){
					//发送选中的场景信息，生成相应的场景
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					SendMessage.sendSetSceneMsg(mContext,accountManager.getmCurrentInfo().getGwID(), CmdUtil.MODE_ADD,
							null, info.getName(), info.getIcon(),
							CmdUtil.SCENE_UNUSE, false);
				}
			}
			
		});
		
	}
	
	public void createDefaultSceneDevice(SceneInfo sceneInfo) {
		List<TaskInfo> entities = new ArrayList<TaskInfo>();
		if(SCENE_ICON_BACK_HOME.equals(sceneInfo.getIcon())){
			//回家：打开所有照明，打开窗帘，安防撤防
			entities.addAll(createTaskInfosLightOpen(sceneInfo));
			entities.addAll(createTaskInfosDefenseableClose(sceneInfo));
			entities.addAll(createTaskInfosCurtainOpen(sceneInfo));
		}else if(SCENE_ICON_AWAY_HOME.equals(sceneInfo.getIcon())){
			//离家：关闭所有照明，关闭窗帘，安防设防
			entities.addAll(createTaskInfosLightClose(sceneInfo));
			entities.addAll(createTaskInfosLongDefenseableOpen(sceneInfo));
			entities.addAll(createTaskInfosCurtainClose(sceneInfo));
		}else if(SCENE_ICON_SLEEP.equals(sceneInfo.getIcon())){
			//睡觉：关闭所有照明，关闭窗帘，客厅设防
			entities.addAll(createTaskInfosLightClose(sceneInfo));
			entities.addAll(createTaskInfosLongDefenseableOpen(sceneInfo));
			entities.addAll(createTaskInfosCurtainClose(sceneInfo));
		}else if(SCENE_ICON_GET_UP.equals(sceneInfo.getIcon())){
			//起床：打开主卫照明，打开窗帘，客厅撤防
			entities.addAll(createTaskInfosDefenseableClose(sceneInfo));
			entities.addAll(createTaskInfosCurtainOpen(sceneInfo));
		}else if(SCENE_ICON_ALL_OPEN.equals(sceneInfo.getIcon())){
			//全开：照明、插座、水阀、煤气阀、窗帘全打开，安防撤防
			entities.addAll(createTaskInfosLightOpen(sceneInfo));
			entities.addAll(createTaskInfosSocketOpen(sceneInfo));
			entities.addAll(createTaskInfosWaterValveOpen(sceneInfo));
			entities.addAll(createTaskInfosCurtainOpen(sceneInfo));
		}else if(SCENE_ICON_ALL_CLOSE.equals(sceneInfo.getIcon())){
			//全关：照明，插座、水阀、（煤气阀）、窗帘全关闭，安防设防
			entities.addAll(createTaskInfosLightClose(sceneInfo));
			entities.addAll(createTaskInfosSocketClose(sceneInfo));
			entities.addAll(createTaskInfosWaterValveClose(sceneInfo));
			entities.addAll(createTaskInfosCurtainClose(sceneInfo));
		}
		saveDefaultSceneTaskes(entities);
		
	}
	private List<TaskInfo> createTaskInfosLightOpen(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,true,SceneManager.allLightType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosLightClose(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,false,SceneManager.allLightType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosSocketOpen(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,true,SceneManager.allSocketType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosSocketClose(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,false,SceneManager.allSocketType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosLongDefenseableOpen(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,true,SceneManager.allLongDefenseSetupType(),Defenseable.class);
	}
	private List<TaskInfo> createTaskInfosDefenseableClose(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,false,SceneManager.allDefenseSetupType(),Defenseable.class);
	}
	private List<TaskInfo> createTaskInfosCurtainOpen(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,true,SceneManager.allCurtainType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosCurtainClose(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,false,SceneManager.allCurtainType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosWaterValveOpen(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,true,SceneManager.allWaterValveType(),Controlable.class);
	}
	private List<TaskInfo> createTaskInfosWaterValveClose(SceneInfo sceneInfo){
		return createTaskInfosControlable(sceneInfo,false,SceneManager.allWaterValveType(),Controlable.class);
	}
	
	
	private List<TaskInfo> createTaskInfosControlable(SceneInfo sceneInfo,boolean isOpen,List<String> types,Class clazz){
		List<TaskInfo> taskes = new ArrayList<TaskInfo>();
		for(String type :types){
			Collection<WulianDevice> devices = mDeviceCache.getDeviceByType(accountManager.getmCurrentInfo().getGwID(), type);
			for(WulianDevice device : devices){
				if(device instanceof Controlable){
					DeviceInfo deviceInfo = device.getDeviceInfo();
					String epData = "";
					if(clazz.getName().equals(Controlable.class.getName())){
						Controlable controlable = (Controlable)device;
						epData= controlable.getCloseProtocol();
						if(isOpen){
							epData = controlable.getOpenProtocol();
						}
					}
					getChildDevice(device,epData,deviceInfo,sceneInfo,taskes);
					
				}else if(device instanceof Defenseable){
					DeviceInfo deviceInfo = device.getDeviceInfo();
					String epData = "";
					if(clazz.getName().equals(Defenseable.class.getName())){
						Defenseable defenseable = (Defenseable)device;
						epData= defenseable.getDefenseUnSetupProtocol();
						if(isOpen){
							epData = defenseable.getDefenseSetupProtocol();
						}
					}
					getChildDevice(device,epData,deviceInfo,sceneInfo,taskes);
				}
			}
		}
		return taskes;
	}
	
	private void getChildDevice(WulianDevice device,String epData,DeviceInfo deviceInfo,SceneInfo sceneInfo,List<TaskInfo> taskes){
		if(device.getChildDevices() == null || device.getChildDevices().isEmpty()){
			TaskInfo info = createTaskInfo(sceneInfo,deviceInfo);
			info.setEpData(epData);
			info.setEpType(deviceInfo.getDevEPInfo().getEpType());
			info.setEp(deviceInfo.getDevEPInfo().getEp());
			taskes.add(info);
		}else{
			for(WulianDevice child : device.getChildDevices().values()){
				DeviceInfo childDeviceInfo = child.getDeviceInfo();
				TaskInfo info = createTaskInfo(sceneInfo,deviceInfo);
				info.setEpData(epData);
				info.setEpType(childDeviceInfo.getDevEPInfo().getEpType());
				info.setEp(childDeviceInfo.getDevEPInfo().getEp());
				taskes.add(info);
			}
		}
	}
	
	private TaskInfo createTaskInfo(SceneInfo sceneInfo,DeviceInfo deviceInfo) {
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskMode(TaskEntity.VALUE_TASK_MODE_REALTIME);
		taskInfo.setContentID(TaskEntity.VALUE_CONTENT_OPEN);
		taskInfo.setGwID(sceneInfo.getGwID());
		taskInfo.setSceneID(sceneInfo.getSceneID());
		taskInfo.setDevID(deviceInfo.getDevID());
		taskInfo.setType(deviceInfo.getType());
		taskInfo.setSensorID(TaskEntity.VALUE_SENSOR_ID_NORMAL);
		taskInfo.setSensorEp(TaskEntity.VALUE_SENSOR_ID_NORMAL);
		taskInfo.setAvailable(TaskEntity.VALUE_AVAILABL_YES);
		taskInfo.setMutilLinkage(TaskEntity.VALUE_MULTI_LINK_YES);
		return taskInfo;
	}
	private void saveDefaultSceneTaskes(List<TaskInfo> taskes){
		//循环遍历选择的设备，自动生成相应设备列表
		for(final TaskInfo taskInfo : taskes){
			JSONObject obj = new JSONObject();
			JsonTool.makeTaskJSONObject(obj,taskInfo,CmdUtil.MODE_ADD);
			JSONArray array = new JSONArray();
			array.add(obj);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			NetSDK.sendSetTaskMsg(taskInfo.getGwID(), taskInfo.getSceneID(), taskInfo.getDevID(), taskInfo.getType(),taskInfo.getEp(), taskInfo.getEpType(),array);
//			SendMessage.sendSetTaskMsg(mContext, taskInfo.getGwID(), taskInfo.getSceneID(), taskInfo.getDevID(), taskInfo.getType(),taskInfo.getEp(), taskInfo.getEpType(),array);
		}
	}
	
	public void upDefaultSceneProgressDialog(){
		int sendListIntem = getCheckedSceneInfos().size();
		int receiveItemSize = mApplication.sceneInfoMap.size();
		progress = (int) (receiveItemSize * 100.0 / sendListIntem);
		if (progress <= 100) {
			progressDialog.setProgess(progress);
		}
		if (progress >= 100) {
			progressDialog.dismiss();
			timer.cancel();
			isHandRemindKey = Preference.getPreferences().getDefaultSceneSetting();
			if(StringUtil.isNullOrEmpty(isHandRemindKey)){
				reminMenu = new SceneRemindPopuwindow(mContext);
				reminMenu.showBottom();
				preference.saveDefaultSceneSetting("-1");
			}
			isFirst = 1;
		}
	}
	
	CountDownTimer timer = new CountDownTimer(30000,1000) {  
        
	       @Override  
	       public void onTick(long millisUntilFinished) {  
	       }  
	        
	       @Override  
	       public void onFinish() {
	    	   progressDialog.dismiss();
	    	   reminMenu = new SceneRemindPopuwindow(mContext);
	    	   reminMenu.showBottom();
	       }  
	    }; 
}
