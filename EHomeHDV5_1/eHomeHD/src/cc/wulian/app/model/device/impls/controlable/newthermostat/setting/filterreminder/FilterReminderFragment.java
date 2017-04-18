package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.filterreminder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class FilterReminderFragment extends WulianFragment{

	private final String TAG = getClass().getSimpleName();
	
	private ListView settingListView;
	
	private List<String> listViewItems;
	private MySettingAdapter settingAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		settingAdapter = new MySettingAdapter(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle("FilterReminder Setting");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_thermostat82_setting_reminder, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		settingListView = (ListView) view.findViewById(R.id.thermost_setting_reminder_lv);
		initSettingItems();
		settingAdapter.setListData(listViewItems);
		settingListView.setAdapter(settingAdapter);
		
		settingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				
				String result = listViewItems.get(position);
				Intent intent = new Intent();
				intent.putExtra("fliterResult", result);
				mActivity.setResult(mActivity.RESULT_OK, intent);
				mActivity.finish();
			}
			
		});
		
	}
	
	@Override
	public void onShow() {
		super.onShow();
		initBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		initBar();
	}

	
	private void initSettingItems(){
		
		listViewItems = new ArrayList<String>();
		listViewItems.add("Reminder after 10 whole days of running.\n(About 1 months)");
		listViewItems.add("Reminder after 10 whole days of running.\n(About 3 months)");
		listViewItems.add("Reminder after 10 whole days of running.\n(About 6 months)");
		listViewItems.add("Reminder after 10 whole days of running.\n(About 9 months)");
		listViewItems.add("Reminder after 10 whole days of running.\n(About 12 months)");

	}
	
	class MySettingAdapter extends BaseAdapter{

		private  List<String> listData;
		private Context context;
		
		class ViewHolder{
			TextView tvContent;
		}
		

		public MySettingAdapter(Context context) {
			super();
			this.context = context;
		}

		public void setListData(List<String> listData) {
			this.listData = listData;
		}

		@Override
		public int getCount() {
			return listData!= null ? listData.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(viewHolder == null){
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.device_thermostat82_setting_reminder_item, null);
				viewHolder.tvContent= (TextView) convertView.findViewById(R.id.thermost_setting_reminder_tv);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.tvContent.setText(listData.get(position));
			return convertView;
		}
		
	}
	
	
	
}
