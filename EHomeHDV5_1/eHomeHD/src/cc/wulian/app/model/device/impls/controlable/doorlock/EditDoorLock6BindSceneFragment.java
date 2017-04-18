package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import android.os.Bundle;
import android.view.View;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.DoorLockAlarmSettingAdapter;
import cc.wulian.smarthomev5.adapter.DoorLockBindSceneInfoAdapter;
import cc.wulian.smarthomev5.adapter.DoorLockSceneInfoAdapter;
import cc.wulian.smarthomev5.adapter.SceneInfoAdapter;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.fragment.scene.SceneFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class EditDoorLock6BindSceneFragment extends  SceneFragment{
	public static final String USERID = "userID";
	public static final String TOKEN = "token";
	public static final String PASSWORD = "password";
	public static final String PEROID = "peroid";
	public static final String CNAME = "cname";
	public static final String USERTYPE = "userType";
	public static final String SCENEID = "sceneid";
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String BINDED_SCENE_NAME = "binded_scene_name";
	
	public static String token;
	public static String userID;
	public static String peroid;
	public static String cname;
	public static String password;
	public static String userType;
	public static String sceneId;
	public static String gwID;
	public static String devID;
	public static String bindSceneName;
	
	private List<SceneInfo> infos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		token = getArguments().getString(TOKEN);
		userID= getArguments().getString(USERID);
		peroid = getArguments().getString(PEROID);
		cname = getArguments().getString(CNAME);
		password = getArguments().getString(PASSWORD);
		userType= getArguments().getString(USERTYPE);
		sceneId= getArguments().getString(SCENEID);
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		bindSceneName=getArguments().getString(BINDED_SCENE_NAME);
		super.onCreate(savedInstanceState);
		mSceneEditAdapter=new DoorLockBindSceneInfoAdapter(mActivity,getSupportActionBar());
	}
	
	@Override
	public void updateSceneEmptyText() {
		// TODO Auto-generated method stub
		super.updateSceneEmptyText();
		emptyTextView.setText(getString(R.string.smartLock_no_scene));
		mSceneGridView.setEmptyView(emptyTextView);
	}
	
	@Override
	public void initBar() {
		// TODO Auto-generated method stub
		super.initBar();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(getString(R.string.device_bind_scene));
		getSupportActionBar().setIconText(getString(R.string.about_back));
		
		SceneInfo info = new SceneInfo();
		info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		SceneDao sceneDao = SceneDao.getInstance();
		infos = sceneDao.findListAll(info);
		if (infos.size() == 0) {
			getSupportActionBar().setRightIconText("");
			getSupportActionBar().setRightMenuClickListener(null);
		} else if (bindSceneName != null && !bindSceneName.equals(getString(R.string.device_no_bind))) {
			getSupportActionBar().setRightIconText(getString(R.string.set_account_manager_permission_unbinding_status));
			getSupportActionBar().setRightMenuClickListener(
					new OnRightMenuClickListener() {

						@Override
						public void onClick(View v) {
							sendUnbindSceneCommand();
						}
					});
		} else {
			getSupportActionBar().setRightIconText("");
			getSupportActionBar().setRightMenuClickListener(null);
		}
	}
	
	private void sendUnbindSceneCommand(){
		JSONObject sendData = new JSONObject();
		sendData.put("token", token);
		sendData.put("userID", Integer.parseInt(userID, 16)+"");
		sendData.put("sceneId", "");
		sendData.put("peroid", peroid);
		String passwordStr="";
		for(int i=0;i<password.length();i++){
			if(i%2!=0){
				passwordStr+=password.charAt(i);
			}
		}
		sendData.put("password", passwordStr);
		sendData.put("cname", cname);
		sendData.put("userType", Integer.parseInt(userType)+"");
		NetSDK.sendSetDoorLockData(gwID, devID, "4", sendData);
		sceneId="";
		mSceneEditAdapter.notifyDataSetChanged();
	}
	
}
