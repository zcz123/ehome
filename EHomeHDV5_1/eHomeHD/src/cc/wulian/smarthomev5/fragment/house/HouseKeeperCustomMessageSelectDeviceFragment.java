package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.house.AddCustomMessageDeviceAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

/**
 * 自定义消息选择设备页面
 * 
 * @author Administrator
 * 
 */
public class HouseKeeperCustomMessageSelectDeviceFragment extends
		WulianFragment {

	
	private ListView messageDeviceList;

	public static AutoProgramTaskInfo autoProgramTaskInfo;

	private AddCustomMessageDeviceAdapter adapter;

	private List<WulianDevice> devices;

	private static final String SPLIT_SPACE = " ";

	private static OnSelectListener selectListener;

	private List<AutoConditionInfo> conditionList;

	private List<AutoActionInfo> actionList;

	private List<AutoConditionInfo> triggerList;

	private DeviceCache mDeviceCache;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(
				R.layout.task_manager_fragment_custom_message_select_device,
				null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		messageDeviceList = (ListView) view
				.findViewById(R.id.house_keeper_task_custom_message_select_device);

		adapter = new AddCustomMessageDeviceAdapter(mActivity, null);
		messageDeviceList.setAdapter(adapter);
		
		initDeviceListView();
	}
	

	private void initDeviceListView() {

		initData();
		List<WulianDevice> newDevices=new ArrayList<WulianDevice>();
		for(WulianDevice device : devices){
			if(device == null){
				//TODO
			}else{
				newDevices.add(device);
			}
		}
		devices=newDevices;
		adapter.swapData(devices);
		
		messageDeviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				WulianDevice device = adapter.getItem(arg2);
				if (selectListener != null) {

					selectListener.OnSelect(device);
					
					mActivity.finish();
				}
			}
		});

	}

	protected String checkRule() {
		// for (AutoConditionInfo triggerInfo : triggerList) {
		// if(triggerInfo.getObject())
		// }
		return null;
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 获取设备缓存类
		mDeviceCache = DeviceCache.getInstance(mActivity);

		// 存储设备ID的集合
		List<String> devIdList = new ArrayList<>();

		// 获取触发条件集合
		triggerList = autoProgramTaskInfo.getTriggerList();

		// 遍历触发条件,将不重复的且标识为设备的元素加入集合中
		for (AutoConditionInfo autoConditionInfo : triggerList) {

			if ("2".equals(autoConditionInfo.getType())) {
				String[] deviceData = autoConditionInfo.getObject().split(">");

				if (!devIdList.contains(deviceData[0])) {
					devIdList.add(deviceData[0]);
				}
			}
		}

		// 获取执行任务集合
		actionList = autoProgramTaskInfo.getActionList();
		// 遍历执行任务,将不重复的且标识为设备的加入集合中
		for (AutoActionInfo autoActionInfo : actionList) {
			if ("2".equals(autoActionInfo.getType())
					|| "3".equals(autoActionInfo.getType())) {

				String[] deviceData = autoActionInfo.getObject().split(">");

				System.out.println("执行任务devId:" + deviceData[0]);

				if (!devIdList.contains(deviceData[0])) {
					devIdList.add(deviceData[0]);
				}
			}
		}

		// 字符串形式存放的限制条件集合
		List<String> conditionStrList = new ArrayList<String>();

		if (autoProgramTaskInfo.getRoot() != null) {
			conditionStrList.addAll(autoProgramTaskInfo.getRoot()
					.toTreeStrings());
		}

		// 限制条件集合
		conditionList = new ArrayList<>();
		if (conditionStrList.size() >= 1) {
			for (String str : conditionStrList) {
				AutoConditionInfo conditionInfo = new AutoConditionInfo();
				String type = str.substring(0, 1);
				conditionInfo.setType(type);
				String[] splits = str.split(SPLIT_SPACE);
				String conditionObject = splits[0].substring(2);

				conditionInfo.setObject(conditionObject);
				if (StringUtil.equals(type, "0")
						|| StringUtil.equals(type, "1")) {
					if (StringUtil.equals(splits[1], "in")
							&& splits.length == 3) {
						conditionInfo.setExp(splits[1] + " " + splits[2]);
					} else if (StringUtil.equals(splits[1], "not")
							&& splits.length == 4) {
						conditionInfo.setExp(splits[1] + " " + splits[2] + " "
								+ splits[3]);
					}
				} else if (StringUtil.equals(type, "2") && splits.length == 3) {
					conditionInfo.setExp(splits[1] + splits[2]);
				}

				if ("2".equals(type)) {
					String[] deviceData = conditionObject.split(">");
					if (!devIdList.contains(deviceData[0])) {
						devIdList.add(deviceData[0]);
					}
				}
				conditionList.add(conditionInfo);

			}
		}

		// 关联设备的集合
		devices = new ArrayList<WulianDevice>();
		// 获取所有关联的设备
		for (int i = 0; i < devIdList.size(); i++) {

			WulianDevice device = mDeviceCache.getDeviceByID(mActivity,
					autoProgramTaskInfo.getGwID(), devIdList.get(i));
			devices.add(device);
		}

	}

	private void initBar() {
		mActivity.resetActionMenu();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(false);
		getSupportActionBar().setTitle(R.string.house_rule_select_device);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.finish();

					}
				});
	}

	public static void setOnSelectListener(OnSelectListener selectListener) {
		HouseKeeperCustomMessageSelectDeviceFragment.selectListener = selectListener;
	}

	public interface OnSelectListener {
		public void OnSelect(WulianDevice device);
	}
}
