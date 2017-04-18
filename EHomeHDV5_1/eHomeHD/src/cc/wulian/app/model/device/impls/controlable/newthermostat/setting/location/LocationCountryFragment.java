package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class LocationCountryFragment extends WulianFragment {

	private String gwId;
	private ListView locationListView;
	private LocationCountryListAdapter mAdapter;
	private List<String> mDatas = new ArrayList<String>();
	private String[] listCountryArray = null;
	private Map<String, String> countryCodeMap = LocationDatas.getCountryCodeMap();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("countryData");
		gwId = bundle.getString("gwID");
		listCountryArray = bundle.getStringArray("countryList");
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
	
		mAdapter = new LocationCountryListAdapter(mActivity, mDatas);
		locationListView = (ListView) view.findViewById(R.id.location_setting__lv);
		locationListView.setAdapter(mAdapter);
		locationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mDatas != null && position < mDatas.size()) {
					String countryCode = countryCodeMap.get(mDatas.get(position));
					jumpToLocationProvinceActivity(countryCode);
				}
			}
		});
	}	

	
	/**
	 * 跳转至LocationProvinceActivity
	 */
	private void jumpToLocationProvinceActivity(String countryCode) {
		Intent intent = new Intent(mActivity,LocationProvinceActivity.class);
		intent.putExtra("gwID", gwId);
		intent.putExtra("countryCode", countryCode);
		mActivity.startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	private void initView() {
		if(mDatas==null||mDatas.size()==0){
			for (int i = 0; i < listCountryArray.length; i++) {
				mDatas.add(listCountryArray[i]);
			}
    	}
		mAdapter.swapData(mDatas);
	}
	
	 
	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setTitle("Country Setting");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setIconText("");
		
	}

	
}
