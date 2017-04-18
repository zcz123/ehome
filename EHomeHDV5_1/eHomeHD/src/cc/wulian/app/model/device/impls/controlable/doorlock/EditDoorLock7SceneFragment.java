package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.DoorLock7SceneInfoAdapter;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.fragment.scene.SceneFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class EditDoorLock7SceneFragment extends SceneFragment {
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String BINDED_SCENE_NAME = "binded_scene_name";
	public static final String SCENEID = "sceneID";
	public static String gwID;
	public static String devID;
	public static String bindSceneName;
	public static String sceneId;
	private List<SceneInfo> infos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		bindSceneName = getArguments().getString(BINDED_SCENE_NAME);
		sceneId = getArguments().getString(SCENEID);
		super.onCreate(savedInstanceState);
		mSceneEditAdapter = new DoorLock7SceneInfoAdapter(mActivity,getSupportActionBar());
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
		getSupportActionBar().setIconText(getString(R.string.set_titel));
		
		SceneInfo info = new SceneInfo();
		info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		SceneDao sceneDao = SceneDao.getInstance();
		infos = sceneDao.findListAll(info);
		if (infos.size() == 0) {
			getSupportActionBar().setRightIconText("");
			getSupportActionBar().setRightMenuClickListener(null);
		} else if (bindSceneName != null && !bindSceneName.equals("")) {
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

	private void sendUnbindSceneCommand() {
		JSONArray sendData = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ep", "14");
		jsonObject.put("sceneID", "");
		jsonObject.put("bindDevID", "");
		jsonObject.put("bindEp", "");
		jsonObject.put("bindData", "");
		sendData.add(jsonObject);
		NetSDK.sendSetBindSceneMsg(gwID, "1", devID, "Bd", sendData);
		sceneId="";
		mSceneEditAdapter.notifyDataSetChanged();
	}
}
