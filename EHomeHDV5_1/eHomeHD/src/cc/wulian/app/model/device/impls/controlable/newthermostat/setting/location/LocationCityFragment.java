package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.util.DesUtil;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.adapter.ZoneListAdapter;
import cc.wulian.smarthomev5.entity.ZoneListEntity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

public class LocationCityFragment extends WulianFragment {

	private String token;
	private String gwId;
	//设置城市返回状态
	private String status;
	private LocationEntity cityEntity;
	private ListView locationListView;
	private LocationCityListAdapter mAdapter;
	private List<LocationEntity> mDatas = new ArrayList<LocationEntity>();
	private static final String SHOW_DIALOG_KEY = "select_TimeZome_key";
	private WLDialog successDialog;

	private static final String STATUS_SUCCESS = "0";
	private static final String STATUS_FAILED = "2000";

	private static final String SUCCESS_OK = "OK";
	private static final String SETTING_SUCCESS = "Setting Successed";
	private static final String SETTING_FAILED = "Setting Failed";
	private static final String TOKEN_DISABLED = "Token Disabled";
	
	Handler mHandler = new Handler() {  
		  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case 0:  
            	if(mDatas==null||mDatas.size()==0){
            		locationListView.setVisibility(View.GONE);
            	}else{
            		//完成主界面更新,拿到数据  
            		locationListView.setVisibility(View.VISIBLE);
            		mAdapter.swapData(mDatas);
            	}
                break; 
            case 1:
            	if(StringUtil.equals(status, STATUS_SUCCESS)){
            		showSuccessDialog();
            	}
            	else if(StringUtil.equals(status, STATUS_FAILED)){
					WLToast.showToast(mActivity, TOKEN_DISABLED,WLToast.TOAST_SHORT);
            	}
            	break;
            }  
        }  
  
    };

	private void showSuccessDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);

		builder.setTitle(null)
				.setSubTitleText(null)
				.setMessage(SETTING_SUCCESS)
				.setPositiveButton(SUCCESS_OK)
				.setCancelOnTouchOutSide(false)
				.setDismissAfterDone(true).setListener(new WLDialog.MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				getActivity().finish();
				LocationProvinceActivity.instance.finish();
				LocationCountryActivity.instance.finish();
				LocationSettingActivity.instance.finish();
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		successDialog = builder.create();
		successDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("provinceSetting");
		gwId = bundle.getString("gwID");
		cityEntity = (LocationEntity) bundle.getSerializable("locationEntity");
		
		token = SmarthomeFeatureImpl.getData("token");
//		token = WLUserManager.getInstance().getStub().getUserToken();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.device_thermostat82_setting_location_select, container,
				false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}

	private void initWidget(View view) {
		mAdapter = new LocationCityListAdapter(mActivity, mDatas);
		locationListView = (ListView) view.findViewById(R.id.location_setting__lv);
		locationListView.setAdapter(mAdapter);
		locationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mDatas != null && position < mDatas.size()) {
					String wCityId = mDatas.get(position).getCityId();
					setLocation(wCityId);
				}
			}
		});
	}	


	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	//请求数据  初始化界面
	private void initView() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				
				try {
					String json = LocationHttpManager.postCloudLocationCitys(
							cityEntity.getCountryCode(),cityEntity.geteProvince());
					
					if (!StringUtil.isNullOrEmpty(json)) {
						mDatas.clear();
						JSONObject obj = JSON.parseObject(json);
						JSONArray array = obj.getJSONArray("records");
						if (array != null) {
							for (int i = 0; i < array.size(); i++) {
								LocationEntity entity = new LocationEntity();
								entity.setCountryCode(array.getJSONObject(i).getString("countryCode"));
								entity.seteProvince(array.getJSONObject(i).getString("eProvince"));
								entity.setCityId(array.getJSONObject(i).getString("cityId"));
								entity.seteCityName(array.getJSONObject(i).getString("eCityName"));
								mDatas.add(entity);
							}
							//耗时操作，完成之后发送消息给Handler，完成UI更新；  
			                mHandler.sendEmptyMessage(0);  
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	//设置位置
	private void setLocation (final String wCityId){
		mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				
				try {
					String json = LocationHttpManager.setCloudLocation(gwId ,wCityId ,token);
					if (!StringUtil.isNullOrEmpty(json)) {
						JSONObject obj = JSON.parseObject(json);
						status = obj.getString("status");
						mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
						
		                mHandler.sendEmptyMessage(1);  
						}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setTitle("City Setting");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setIconText("");
	}

	
}
