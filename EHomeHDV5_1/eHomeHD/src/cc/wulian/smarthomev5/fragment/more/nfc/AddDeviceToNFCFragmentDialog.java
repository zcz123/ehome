package cc.wulian.smarthomev5.fragment.more.nfc;

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
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.event.NFCEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import de.greenrobot.event.EventBus;

public class AddDeviceToNFCFragmentDialog extends DialogFragment{

	private static final String TAG = AddDeviceToNFCFragmentDialog.class.getSimpleName();
	private DeviceDao deviceDao = DeviceDao.getInstance();
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

		AddDeviceToNFCFragmentDialog fragment = new AddDeviceToNFCFragmentDialog();
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}

	private AddDeviceInfoAdapter mAddDeviceInfoAdapter;
	private LinearLayout mEmptyLayout;
	private WLDialog dialog;
	private SelectDevicelistener selectDeviceListener;
	private DeviceCache deviceCache ;
	private NFCManager nfcManager = NFCManager.getInstance();

	@Override
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
			List<DeviceInfo> lists = mAddDeviceInfoAdapter.getData();
			for (int i = 0; i < lists.size(); i++) {

				if (mAddDeviceInfoAdapter.getBitSet().get(i)) {
					DeviceInfo deviceInfo = lists.get(i);
					NFCEntity mifareSectorInfo = new NFCEntity();
					mifareSectorInfo.setID(deviceInfo.getDevID());
					mifareSectorInfo.setType(NFCEntity.TYPE_DEVICE);
					mifareSectorInfo.setEp(deviceInfo.getDevEPInfo().getEp());
					mifareSectorInfo.setEpType(deviceInfo.getDevEPInfo().getEpType());
					mifareSectorInfo.setEpData(deviceInfo.getDevEPInfo().getEpData());
					nfcManager.addDeviceInfo(mifareSectorInfo);
				}
			}
			EventBus.getDefault().post(new NFCEvent(CmdUtil.MODE_ADD, false, null, null));
			dialog.dismiss();
		}

		@Override
		public void onClickNegative(View contentViewLayout) {
			dialog.dismiss();
		}
		
	}
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
	}
	public View createView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_add_device_info, null);
		ListView listView = (ListView) view.findViewById(R.id.action_pop_menu_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(mAddDeviceInfoAdapter);
		listView.setEnabled(true);
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
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setGwID(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		List<DeviceInfo> result = deviceDao.findListNFCRemain(deviceInfo,nfcManager.getDeviceNFCEntitys());
		for(int i=result.size()-1;i>=0;i--){
			DeviceInfo info = result.get(i);
			WulianDevice device = deviceCache.getDeviceByIDEp(getActivity(), info.getGwID(), info.getDevID(), info.getDevEPInfo().getEp());
			if(device == null  || !device.isDeviceUseable() || (!device.isAutoControl(false))){
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
