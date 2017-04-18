package cc.wulian.smarthomev5.fragment.home.clickedfragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SocialInfoAdapter;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.event.SocialEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.view.DropDownListView;
import cc.wulian.smarthomev5.view.DropDownListView.OnRefreshListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLToast;

public class SocialFragment extends WulianFragment {
	
	private static final String SOCIAL_KEY = "social_key";
	private List<SocialEntity> entites = new ArrayList<SocialEntity>();
	private Preference mPreference = Preference.getPreferences();
	private SocialInfoAdapter mSocialInfoAdapter;
	private DropDownListView socialInfoList;
	private EditText socialContent;
	private TextView publish;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSocialInfoAdapter = new SocialInfoAdapter(getActivity(),null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_social, container, false);
		
		initBar();
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		socialInfoList = (DropDownListView) view.findViewById(R.id.social_infos);
		socialInfoList.setAdapter(mSocialInfoAdapter);
		socialInfoList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				loadHistoryMessages();
			}

		});
		socialContent = (EditText) view.findViewById(R.id.social_content);
		socialContent.requestFocus();
		publish = (TextView) view.findViewById(R.id.publish_info);
		publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (socialContent.getText() == null
						|| (socialContent.getText().toString().trim())
								.equals("")) {

				} else {
					String gwID = mAccountManger.getmCurrentInfo().getGwID();
					String userType = null;
					String userID = null;
					String appID = mAccountManger.getRegisterInfo().getAppID();
					String userName = "";
					System.out.println("------------"+mPreference.getString(gwID, Build.MODEL));
					System.out.println("------------"+userName);
					String time = DateUtil.convert2ServerTimeLong(new Date().getTime())+"";
					String data = socialContent.getText().toString();
					//增加对登录方式的判断 也对昵称是否为空进行判断
					String enterType=Preference.getPreferences().getUserEnterType();
					if(enterType.equals("account")){//账户登录
						userName = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.NICKNAME);
						if(userName.equals("")||userName==null){
							userName=mPreference.getString(gwID, Build.MODEL);
						}
					}else{//网关直接登录
						userName=mPreference.getString(gwID, Build.MODEL);
					}
					SendMessage.sendSocialMsg(gwID, userType, userID, appID,
							userName, time, data);
					socialContent.setText("");
				}
			}
		});

	}
	@Override
	public void onResume() {
		super.onResume();
		mDialogManager.showDialog(SOCIAL_KEY, mActivity, null, null);
		loadSocialMessage();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.home_return_message_titel));
		getSupportActionBar().setIconText(R.string.nav_home_title);
	}
	private List<SocialEntity> getSocialMessage(String startTime){
		List<SocialEntity>  entites = new ArrayList<SocialEntity>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("gwID", mAccountManger.getmCurrentInfo().getGwID());
			if(!StringUtil.isNullOrEmpty(startTime))
				jsonObject.put("time", startTime);
			String json = HttpUtil.postWulianCloud(WulianCloudURLManager.getSocialInfoURL(),jsonObject);
			if(json != null){
				Logger.debug("getSocialMessagejson" + json);
				JSONObject obj = JSON.parseObject(json);
				JSONArray array = obj.getJSONArray("retData");
				if(array != null){
					for(int i = 0; i < array.size(); i++){
						JSONObject socialObj = array.getJSONObject(i);
						SocialEntity entity = new SocialEntity();
							//{"data":"你好","cmd":"90","time":1427867611132,"gwID":"DC16EB8ECDC1","from":"HD865002025926973","alias":"MI 3W"}
						entity.setData(socialObj.getString("data"));
						entity.setmCmd(socialObj.getString("cmd"));
						entity.setTime(socialObj.getString("time"));
						entity.setGwID(socialObj.getString("gwID"));
						entity.setAppID(socialObj.getString("from"));
						entity.setUserName(socialObj.getString("alias"));
						entites.add(entity);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return entites;
	}
	
	private void loadSocialMessage(){
		TaskExecutor.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				entites = getSocialMessage(DateUtil.getDateBefore(0).getTime()+"");
				Collections.reverse(entites);
				mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mSocialInfoAdapter.swapData(entites);
						socialInfoList.setSelection(mSocialInfoAdapter.getCount());
						mDialogManager.dimissDialog(SOCIAL_KEY, 0);
					}
				});
			}
		});
	}

	
	private void loadHistoryMessages(){
		TaskExecutor.getInstance().executeDelay(new Runnable() {
			
			@Override
			public void run() {
				String startTime = "";
				if(entites.isEmpty()){
					WLToast.showToast(getActivity(), getResources().getString(R.string.home_social_no_history), WLToast.TOAST_SHORT);
					startTime = new Date().getTime()+"";
				}else{
					SocialEntity firstMessageEntity = entites.get(0); 
					startTime = firstMessageEntity.getTime();
				}
				final List<SocialEntity> list = getSocialMessage(startTime);
				Collections.reverse(list);
				mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mSocialInfoAdapter.addHistory(list);
					}
				});
			}
		},1000);
		
	}
	
	public void onEventMainThread(SocialEvent event) {
		if (event.entity != null) {
			mSocialInfoAdapter.addSocialEntity(event.entity);
			socialInfoList.setSelection(mSocialInfoAdapter.getCount());
		}
	}

}