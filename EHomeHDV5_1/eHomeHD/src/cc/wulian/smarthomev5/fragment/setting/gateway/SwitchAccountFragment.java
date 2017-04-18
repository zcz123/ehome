package cc.wulian.smarthomev5.fragment.setting.gateway;

import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SwitchAccountAdapter;
import cc.wulian.smarthomev5.dao.SigninDao;
import cc.wulian.smarthomev5.event.SigninEvent;
import cc.wulian.smarthomev5.fragment.home.HomeManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.CustomProgressDialog;
import com.yuantuo.customview.ui.WLToast;

public class SwitchAccountFragment extends WulianFragment {

	public static String PROCESS_DIALOG_SWITHC_GATEWAY = "PROCESS_DIALOG_SWITHC_GATEWAY";
	@ViewInject(R.id.user_account_switch_lv)
	private ListView switchAccountListView;
	private SwitchAccountAdapter switchAccountAdapter;
	private Preference preference = Preference.getPreferences();
	private SigninDao signDao = SigninDao.getInstance();
	private HomeManager homeManager = HomeManager.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		switchAccountAdapter = new SwitchAccountAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initBar();
		View rootView = inflater.inflate(R.layout.user_switch_account,
				container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(
						R.string.about_back));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.set_account_manager_change_gw));
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		switchAccountListView.setAdapter(switchAccountAdapter);
		switchAccountListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switchAccount(switchAccountAdapter.getItem(position));
			}
		});
	}

	/**
	 * 切换当前账号
	 * 
	 * @param item
	 */
	private void switchAccount(GatewayInfo item) {
		if(mAccountManger.isConnectedGW() ){
			if(item.getGwID().equals(mAccountManger.getmCurrentInfo().getGwID())){
				return ;
			}
		}
		if(!preference.isRememberChecked(item.getGwID())){
			mAccountManger.switchAccount(item);
			mAccountManger.exitCurrentGateway(mActivity);		
			return ;
		}
		mDialogManager.showDialog(PROCESS_DIALOG_SWITHC_GATEWAY,mActivity,null,null,CustomProgressDialog.DELAYMILLIS_40);
		mAccountManger.switchAccount(item);
		mAccountManger.updateAutoLogin(item.getGwID());
		homeManager.setHomeRefresh(true);
		mAccountManger.signinDefaultAccount();
		
	}
	@Override
	public void onResume() {
		super.onResume();
		loadSigninHistory();
	}

	/**
	 * 查找所有登陆过的网关
	 */
	private void loadSigninHistory() {
		List<GatewayInfo> allHistoryGatewayInfos = signDao.findListAll(new GatewayInfo());
		switchAccountAdapter.swapData(allHistoryGatewayInfos);
	}

	/**
	 * 切换网关提醒
	 * 
	 * @param event
	 */
	public void onEventMainThread(SigninEvent event) {
		mDialogManager.dimissDialog(PROCESS_DIALOG_SWITHC_GATEWAY,CustomProgressDialog.resultOk);
		loadSigninHistory();
		if(event.result == ResultUtil.EXC_GW_PASSWORD_WRONG){
			mAccountManger.exitCurrentGateway(mActivity);		
		}
	}
}
