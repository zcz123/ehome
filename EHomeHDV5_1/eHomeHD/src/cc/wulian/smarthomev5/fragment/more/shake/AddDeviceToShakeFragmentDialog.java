package cc.wulian.smarthomev5.fragment.more.shake;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.AddDeviceInfoAdapter;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.event.ShakeEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import de.greenrobot.event.EventBus;

public class AddDeviceToShakeFragmentDialog extends DialogFragment{
	private static final String TAG = AddDeviceToShakeFragmentDialog.class.getSimpleName();
	public static void showDeviceDialog( FragmentManager fm, FragmentTransaction ft) {
		DialogFragment df = (DialogFragment) fm.findFragmentByTag(TAG);
		if (df != null) {
			if (!df.getDialog().isShowing()) {
				ft.remove(df);
			}
			else {
				return;
			}
		}

		AddDeviceToShakeFragmentDialog fragment = new AddDeviceToShakeFragmentDialog();
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}
	private static final int LOADER_DEVICES = 1;
	private AddDeviceInfoAdapter mAddDeviceInfoAdapter;
	private LinearLayout mEmptyLayout;
	private WLDialog dialog;
	private SelectDevicelistener selectDeviceListener;
	private DeviceCache deviceCache ;
	private DeviceDao deviceDao = DeviceDao.getInstance();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(this.getActivity());
		mAddDeviceInfoAdapter = new AddDeviceInfoAdapter(this.getActivity(), getRemainDevices());
	}
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		selectDeviceListener = new SelectDevicelistener();
		WLDialog.Builder builder = new Builder(this.getActivity());
		builder.setContentView(createView())
			   .setTitle(R.string.device_select_device_hint)
               .setPositiveButton(R.string.common_ok)
               .setNegativeButton(R.string.cancel)
               .setListener(selectDeviceListener);
		dialog = builder.create();	
		return dialog;
	}
	private class SelectDevicelistener implements MessageListener{

		@Override
		public void onClickPositive(View contentViewLayout) {
			
			List<ShakeEntity> selectedShakeEntities = new ArrayList<ShakeEntity>();
			List<DeviceInfo> lists = mAddDeviceInfoAdapter.getData();
			for (int i = 0; i < lists.size(); i++) {
				if (mAddDeviceInfoAdapter.getBitSet().get(i)) {
					DeviceInfo deviceInfo = lists.get(i);
					ShakeEntity entity = new ShakeEntity();
					entity.setGwID(deviceInfo.getGwID());
					entity.setOperateID(deviceInfo.getDevID());
					entity.setOperateType(ShakeEntity.TYPE_DEVICE);
					entity.setEp(deviceInfo.getDevEPInfo().getEp());
					entity.setEpType(deviceInfo.getDevEPInfo().getEpType());
					entity.setEpData(deviceInfo.getDevEPInfo().getEpData());
					entity.setTime(System.currentTimeMillis()+"");
					selectedShakeEntities.add(entity);
				}
			}
			EventBus.getDefault().post(new ShakeEvent(CmdUtil.MODE_ADD,selectedShakeEntities,null, false));
		}

		@Override
		public void onClickNegative(View contentViewLayout) {
		}
		
	}
	public View createView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_add_device_info, null);
		ListView listView = (ListView) view.findViewById(R.id.action_pop_menu_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(mAddDeviceInfoAdapter);
		listView.setEnabled(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				mAddDeviceInfoAdapter.onSelection(position);
			}
		});

		mEmptyLayout = (LinearLayout) view.findViewById(R.id.no_data);
		initEmptyView();
		return view;
	}

	private List<DeviceInfo> getRemainDevices(){
		if(ShakeManager.shakeEntity == null){
			return new ArrayList<DeviceInfo>();
		}
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setGwID(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		List<DeviceInfo> result = deviceDao.findListShakeRemain(deviceInfo,ShakeManager.shakeEntity);
		for(int i=result.size()-1;i>=0;i--){
			DeviceInfo info = result.get(i);
			WulianDevice device = deviceCache.getDeviceByIDEp(getActivity(), info.getGwID(), info.getDevID(), info.getDevEPInfo().getEp());
			if(device == null  || !device.isDeviceUseable() || !device.isAutoControl(true)){
				result.remove(info);
			}
		}
		return result;
	}
	private void initEmptyView(){
		if(mAddDeviceInfoAdapter.getCount() == 0){
			mEmptyLayout.setVisibility(View.VISIBLE);
		}else{
			mEmptyLayout.setVisibility(View.GONE);
		}
	}
}
