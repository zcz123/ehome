package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

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
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.util.DesUtil;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.ZoneListAdapter;
import cc.wulian.smarthomev5.entity.ZoneListEntity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

public class LocationProvinceFragment extends WulianFragment {

	private String gwId;
	private String countryCode;
	private ListView locationListView;
	private LocationProvinceListAdapter mAdapter;
	private List<LocationEntity> mDatas = new ArrayList<LocationEntity>();
	private static final String SHOW_DIALOG_KEY = "select_TimeZome_key";

	
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
            }  
        }  
  
    };  

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		gwId = getArguments().getString("gwID");
		countryCode = getArguments().getString("countryCode");
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
		mAdapter = new LocationProvinceListAdapter(mActivity, mDatas);
		locationListView = (ListView) view.findViewById(R.id.location_setting__lv);
		locationListView.setAdapter(mAdapter);
		locationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mDatas != null && position < mDatas.size()) {
					jumpToLocationCityActivity(mDatas.get(position));
				}
			}
		});
	}	

	/**
	 * 跳转至LocationCityActivity
	 */
	private void jumpToLocationCityActivity(LocationEntity entity) {
		Intent intent = new Intent(mActivity,LocationCityActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("gwID", gwId);
		bundle.putSerializable("locationEntity", entity);
		intent.putExtra("provinceSetting", bundle);
		mActivity.startActivity(intent);
	}
	

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	private void initView() {
//		mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				
				try {
					String json = LocationHttpManager.postCloudLocationProvinvce(countryCode);
				
					if (!StringUtil.isNullOrEmpty(json)) {
						mDatas.clear();
						JSONObject obj = JSON.parseObject(json);
						JSONArray array = obj.getJSONArray("records");
						if (array != null) {
//							mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
							for (int i = 0; i < array.size(); i++) {
								LocationEntity entity = new LocationEntity();
								entity.setCountryCode(array.getJSONObject(i).getString("countryCode"));
								entity.seteProvince(array.getJSONObject(i).getString("eProvince"));
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
	 
	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setTitle("Province Setting");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setIconText("");
	}

	
}
