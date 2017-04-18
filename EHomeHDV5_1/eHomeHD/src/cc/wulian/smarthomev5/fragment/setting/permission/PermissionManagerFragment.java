package cc.wulian.smarthomev5.fragment.setting.permission;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.PermissionManagerAdapter;
import cc.wulian.smarthomev5.entity.PermissionEntity;
import cc.wulian.smarthomev5.event.PermissionEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import de.greenrobot.event.EventBus;

public class PermissionManagerFragment extends WulianFragment{

	private static final String PERMISSION_KEY = "permission_key";
	private List<PermissionEntity> entites = new ArrayList<PermissionEntity>();
	private AccountManager mAccountManager = AccountManager.getAccountManger();
	private final Handler mHandler = new Handler(Looper.getMainLooper());
	private SwipeMenuListView permissionListView;
	private PermissionManagerAdapter permissionManagerAdapter;
	private LinearLayout remindLayout;
	private TextView remindText;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		permissionManagerAdapter = new PermissionManagerAdapter(mActivity, null);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_permission_manager_layout, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		permissionListView = (SwipeMenuListView) view
				.findViewById(R.id.setting_permission_manager_listview);
		permissionManagerAdapter.setMenuCreator(creatLeftDeleteItem());
		permissionListView.setAdapter(permissionManagerAdapter);
		remindLayout = (LinearLayout) view
				.findViewById(R.id.setting_permission_manager_layout);
		remindText = (TextView) view.findViewById(R.id.setting_permission_manager_layout_text);
		remindLayout.setVisibility(View.GONE);
		loadPermissionEntity();
		initPermissionList();
	}

	private void initPermissionList(){
		permissionManagerAdapter
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						PermissionEntity item = entites.get(
								position);
						switch (index) {
						case 0:
							deletePermission(position,item);
							break;
						}
					}

				});
		// 解决左划删除与右划菜单栏冲突
		permissionListView.setOnOpenOrCloseListener(new OpenOrCloseListener() {

			@Override
			public void isOpen(boolean isOpen) {
				
			}
		});
	}
	
	private void deletePermission(final int position,final PermissionEntity entity) {
		
		if (entity != null) {
			mDialogManager.showDialog(PERMISSION_KEY, mActivity, null, null);
			TaskExecutor.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					if(deletePermissionItem(entity)){
						entites.remove(position);
					}
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mDialogManager.dimissDialog(PERMISSION_KEY, 0);
							permissionManagerAdapter.swapData(entites);
						}
					});
				}
			});
		}
	}
		
	private boolean deletePermissionItem(PermissionEntity entity){
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("gwID", mAccountManager.getmCurrentInfo().getGwID());
//			jsonObject.put("gwID", "E04C0D8DC54B");
			jsonObject.put("userID", entity.getUserID());
			jsonObject.put("status", "1");

			JSONObject json = HttpUtil.postWulianCloudOrigin(
					WulianCloudURLManager.getResponsePermissionInfoURL(), jsonObject);

			if (json != null) {
				Logger.debug("json" + json);
				JSONObject obj = json.getJSONObject("header");
				String status = obj.getString("retCode");
				if (StringUtil.equals(status, "SUCCESS")) {
					EventBus.getDefault().post(new PermissionEvent(PermissionEvent.REJECT,entity));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 创建左划删除item样式
	 */
	private SwipeMenuCreator creatLeftDeleteItem() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu,int position) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(mActivity, 80));
//				deleteItem.setIcon(R.drawable.ic_delete);
				deleteItem.setTitle(R.string.set_account_manager_permission_unbinding_status);
				deleteItem.setTitleSize(DisplayUtil.dip2Sp(mActivity, 10));
				deleteItem.setTitleColor(mApplication.getResources().getColor(R.color.black));
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}
	
	
	private synchronized List<PermissionEntity> getPermissionMessages() {

		List<PermissionEntity> permissionEntites = new ArrayList<PermissionEntity>();
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("gwID", mAccountManager.getmCurrentInfo().getGwID());
//			jsonObject.put("gwID", "E04C0D8DC54B");

			JSONObject json = HttpUtil.postWulianCloudOrigin(
					WulianCloudURLManager.getPermissionInfoURL(), jsonObject);

			if (json != null) {
				Logger.debug("json" + json);
				JSONObject obj = json.getJSONObject("body");
				JSONArray array = obj.getJSONArray("retData");
				if (array != null) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject permissionObj = array.getJSONObject(i);
						PermissionEntity entity = new PermissionEntity();
//						entity.setGwID(permissionObj.getString("gwID"));
						entity.setUserID(permissionObj.getString("userId"));
						entity.setUserName(permissionObj.getString("name"));
						entity.setStatus(permissionObj.getString("status"));
						permissionEntites.add(entity);
						}
					}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return permissionEntites;
	}
	
	/**
	 * 加载授权控制信息
	 */
	private synchronized void loadPermissionEntity() {
		mDialogManager.showDialog(PERMISSION_KEY, mActivity, null, null);
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				entites.clear();
				entites = getPermissionMessages();
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mDialogManager.dimissDialog(PERMISSION_KEY, 0);
						permissionManagerAdapter.swapData(entites);
						initpermissionView();
					}
				});
			}
		});
	}
	
	private void initBar(){
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.about_back);
		getSupportActionBar().setTitle(R.string.set_account_manager_permission);
	}
	
	public void onEventMainThread(PermissionEvent event) {
		for(int i = 0; i < entites.size(); i++){
			if(StringUtil.equals(event.entity.getUserID(), entites.get(i).getUserID())){
				entites.remove(i);
			}
		}
		if(PermissionEvent.ACCEPT.equals(event.action)){
			entites.add(event.entity);
		}
		permissionManagerAdapter.swapData(entites);
		initpermissionView();
	}
	
	private void initpermissionView(){
		if(entites.size() == 0){
			remindLayout.setVisibility(View.VISIBLE);
			remindText.setVisibility(View.VISIBLE);
			remindText.setText(mApplication.getResources().getString(R.string.set_account_manager_permission_remind));
		}else{
			remindLayout.setVisibility(View.GONE);
		}
	}
}

