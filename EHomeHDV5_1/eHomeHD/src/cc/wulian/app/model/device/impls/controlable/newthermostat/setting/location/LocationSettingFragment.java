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

public class LocationSettingFragment extends WulianFragment {

	private String gwId;
	private ListView locationListView;
	private LocationListAdapter mAdapter;
	private List<String> mDatas = new ArrayList<String>();
	private String[] listArray = LocationDatas.locationArray;
	private Map<String, String[]> locationCountryMap ;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		gwId = getArguments().getString("gwID");
		Log.i("ccccc", gwId);
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
		mAdapter = new LocationListAdapter(mActivity, mDatas);
		locationListView = (ListView) view.findViewById(R.id.location_setting__lv);
		locationListView.setAdapter(mAdapter);
		locationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				if (mDatas != null && position < mDatas.size()) {
					jumpToLocationCountryActivity(mDatas.get(position));
				}
			}
		});
	}	
	
	/**
	 * 跳转至LocationCountryActivity
	 */
	private void jumpToLocationCountryActivity(String key) {
		Intent intent = new Intent(mActivity,LocationCountryActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("gwID", gwId);
		bundle.putStringArray("countryList", locationCountryMap.get(key));
		intent.putExtra("countryData", bundle);
		mActivity.startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	private void initView() {
		if(mDatas==null||mDatas.size()==0){
			//完成主界面更新,拿到数据  
    		for (int i = 0; i < listArray.length; i++) {
    			mDatas.add(listArray[i]);
    		}
    		
    		LocationDatas.initLocationDatas();
    		locationCountryMap = LocationDatas.getLocationMap();
    	}
		mAdapter.swapData(mDatas);
	}
	
	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setTitle("Location Setting");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setIconText("");
	}

	
}
