package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.ZoneListAdapter;
import cc.wulian.smarthomev5.entity.ZoneListEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;

public class HousekeeperSelectTimeZomeFragment extends WulianFragment{

	private ListView zoneListView;
	private CheckedTextView synchonTextView;
	private ZoneListAdapter mAdapter;
	private List<ZoneListEntity> mDatas=new ArrayList<ZoneListEntity>();
	private static SelectZoneListener selectZoneListener;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_timezone_select, container,false);		
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}

	private void initWidget(View view){
		mAdapter=new ZoneListAdapter(mActivity, mDatas);
		zoneListView=(ListView) view.findViewById(R.id.setting_zone_lv);
		synchonTextView=(CheckedTextView) view.findViewById(R.id.zonelist_syncho_tv);
		
		synchonTextView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				synchonTextView.setChecked(!synchonTextView.isChecked());
				if(synchonTextView.isChecked()){
					String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
					String zoneID = getLocalTimeZoneIndex(System.currentTimeMillis());
					if(selectZoneListener != null){
						selectZoneListener.onSelectZoneListenerChanged(zoneID);
						mActivity.finish();
					}
				}
			}
		});
		zoneListView.setAdapter(mAdapter);		
		zoneListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(mDatas!=null&&arg2<mDatas.size()){
					ZoneListEntity enter=mDatas.get(arg2);
					if(selectZoneListener != null){
//						selectZoneListener.onSelectZoneListenerChanged(enter.getZoneIndex());
						mActivity.finish();
					}
				}
			}			
		});
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		setListDatas();
	}
	private void setListDatas(){
		mDatas.clear();
		Map<String,String> zoneMap=getZone();	
		//排序
		List<Map.Entry<String, String>> entryList=new ArrayList<Map.Entry<String, String>>(zoneMap.entrySet());
		Collections.sort(entryList,new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> arg0,Entry<String, String> arg1) {
				double value1=StringUtil.toDouble(arg0.getKey());
				double value2=StringUtil.toDouble(arg1.getKey());
				return (int) (value2-value1);
			}
		});
		//取值
		Iterator<Map.Entry<String, String>> iterator=entryList.iterator();
		while(iterator.hasNext()){
			Map.Entry<String, String> entry=iterator.next();
			ZoneListEntity entity=new ZoneListEntity();
//			entity.setZoneName(entry.getValue());
//			entity.setZoneIndex((String)entry.getKey());
			mDatas.add(entity);
		}
		mAdapter.swapData(mDatas);
	}
	//获取本地时区编号
		private String getLocalTimeZoneIndex(long localTime){
			TimeZone zone=TimeZone.getDefault();
			long zoneOffset=zone.getOffset(localTime);
			int zoneOffsetHours=(int) (zoneOffset/60/60/1000);	
			return zoneOffsetHours>0 ? "+"+String.valueOf(zoneOffsetHours) : String.valueOf(zoneOffsetHours);
		}
			
		private Map<String, String> getZone(){
	        Map<String,String> zoneMap=new TreeMap<String, String>();
	        zoneMap.put("-11","GMT-11:00");
	        zoneMap.put("-10","GMT-10:00");
	        zoneMap.put("-9","GMT-9:00");
	        zoneMap.put("-8","GMT-8:00");
	        zoneMap.put("-7","GMT-7:00");
	        zoneMap.put("-6","GMT-6:00");
	        zoneMap.put("-5","GMT-5:00");
	        zoneMap.put("-4","GMT-4:00");
	        zoneMap.put("-3","GMT-3:00");
	        zoneMap.put("-2","GMT-2:00");
	        zoneMap.put("-1","GMT-1:00");
	        zoneMap.put("0","GMT 0:00");
	        zoneMap.put("+1","GMT+1:00");
	        zoneMap.put("+2","GMT+2:00");
	        zoneMap.put("+3","GMT+3:00");
	        zoneMap.put("+4","GMT+4:00");
	        zoneMap.put("+5","GMT+5:00");
	        zoneMap.put("+6","GMT+6:00");
	        zoneMap.put("+7","GMT+7:00");
	        zoneMap.put("+8","GMT+8:00");
	        zoneMap.put("+9","GMT+9:00");
	        zoneMap.put("+10","GMT+10:00");
	        zoneMap.put("+11","GMT+11:00");
	        zoneMap.put("+12","GMT+12:00");
	        return zoneMap;
		}
	
	private void initBar()
    {
	    this.mActivity.resetActionMenu();
	    getSupportActionBar().setTitle(getResources().getString(R.string.gateway_timezone_setting_select_title));    
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayIconEnabled(true);
	    getSupportActionBar().setDisplayIconTextEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);
	    getSupportActionBar().setDisplayShowMenuEnabled(false);
	    getSupportActionBar().setIconText(getResources().getString(R.string.gateway_timezone_setting));
   }
	
	public static void setConditionListener(SelectZoneListener selectZoneListener) {
		HousekeeperSelectTimeZomeFragment.selectZoneListener = selectZoneListener;
	}


	public interface SelectZoneListener{
		public void onSelectZoneListenerChanged(String zoneID);
	}
}
