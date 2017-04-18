package cc.wulian.smarthomev5.fragment.setting.minigateway;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.bean.EagleWifiListEntiy;
import com.yuantuo.customview.ui.WLToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.lan.LanSocketConnection;
import cc.wulian.lan.LanSocketConnectionHandler;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelayWifiConnectActivity;
import cc.wulian.smarthomev5.adapter.MiniWifiRelayAdapter;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
import cc.wulian.smarthomev5.adapter.SsidListViewPupopAdapter;
import cc.wulian.smarthomev5.callback.router.entity.GetWifi_ifaceEntity;
import cc.wulian.smarthomev5.event.GatewaInfoEvent;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.WifiUtil;

public class MiniGateWayRelaySettingFragment extends WulianFragment implements
		OnItemClickListener, LanSocketConnectionHandler,View.OnClickListener {
	private TextView relayHint;
	@ViewInject(R.id.mini_gateway_relay_wifi_info)
	private ListView wifiInfos;
	private TextView ssid;
	private EditText miniWifiName;
	private EditText wifiPassword;
	private RelativeLayout rlSsid;
	private RelativeLayout rlPassword;
	private LinearLayout miniSsid;
	private LinearLayout isDefult;
	private LinearLayout showPhoto;
	private RelativeLayout miniPassword;
	private EditText miniKey;
	private TextView wifiNote;
	private TextView chooseNote;
	private ImageView isShowkey;
	private ImageView isShowMinikey;
	private ImageView passwordClean;
	private ImageView isShowMiniSet;
	private ImageView moreWifi;
	private ImageView isLock;
	private Button next;
	private FrameLayout relayBody;
	private MiniGateWayRelayConnectWifiNor fragment_wifi_nor;
	private MiniGateWayRelayConnectWifiWep fragment_wifi_wep;
	private MiniWifiRelayAdapter miniWifiRelayAdapter;
	public WifiManager wifiManager; // 管理wifi
	public String GWIP;
	private AccountManager accountManager = AccountManager.getAccountManger();
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	private String wifiNameString;
	private ProgressDialogManager progressDialogManager = ProgressDialogManager
			.getDialogManager();
	List<WifiInfoEntity> entites = new ArrayList<WifiInfoEntity>();
	private ImageView sxgateway_iv;
	private GatewayInfo info = accountManager.getmCurrentInfo();
	private Boolean is_sxgateway = false;
	private int zegbeeChannel;

	LanSocketConnection connection = new LanSocketConnection(this);
	String Wifiname_key = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (info != null && info.getGwVer() != null) {
			String gwver = info.getGwVer();
			if (gwver.length() >= 3) {
				is_sxgateway = (gwver.substring(2,4)+"").equals("10");
			}
		}
		wifiManager = (WifiManager) mActivity
				.getSystemService(Context.WIFI_SERVICE); // 获得系统wifi服务
		miniWifiRelayAdapter = new MiniWifiRelayAdapter(mActivity, null);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.mini_gateway_relay_setting,
				container, false);
		ssid= (TextView) rootView.findViewById(R.id.tv_mini_wifi_name);
		miniWifiName= (EditText) rootView.findViewById(R.id.et_mini_wifi_name);
		rlSsid= (RelativeLayout) rootView.findViewById(R.id.rl_mini_wifiname);
 		isShowkey= (ImageView) rootView.findViewById(R.id.iv_mini_key_isvisity);
		wifiNote= (TextView) rootView.findViewById(R.id.mini_show_wifi_note);
		rlPassword= (RelativeLayout) rootView.findViewById(R.id.rl_mini_password);
		isShowMiniSet = (ImageView) rootView.findViewById(R.id.iv_mini_is_selectored);
		miniSsid= (LinearLayout) rootView.findViewById(R.id.ll_mini_wifi_set);
		miniPassword= (RelativeLayout) rootView.findViewById(R.id.rl_mini_wifi_password);
		wifiPassword= (EditText) rootView.findViewById(R.id.et_mini_wifi_password);
		passwordClean= (ImageView) rootView.findViewById(R.id.iv_mini_clean_password);
		isShowMinikey = (ImageView) rootView.findViewById(R.id.iv_mini_isshow);
		miniKey= (EditText) rootView.findViewById(R.id.et_mini_wifi_key);
		next = (Button) rootView.findViewById(R.id.btn_mini_wifi_set_next);
		isDefult= (LinearLayout) rootView.findViewById(R.id.ll_mini_wifi_is_defult);
		relayBody= (FrameLayout) rootView.findViewById(R.id.mini_gateway_relay_body);
		chooseNote= (TextView) rootView.findViewById(R.id.tv_mini_show_choose_wifi_note);
		showPhoto= (LinearLayout) rootView.findViewById(R.id.ll_mini_show_photo);
		moreWifi= (ImageView) rootView.findViewById(R.id.im_mini_wifi_more);
		isLock= (ImageView) rootView.findViewById(R.id.iv_mini_wifi_lock);
		next.setBackgroundResource(R.color.ab_message_normal);
		passwordClean.setVisibility(View.INVISIBLE);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		rlSsid.setOnClickListener(this);
		isShowMiniSet.setOnClickListener(this);
		passwordClean.setOnClickListener(this);
		isShowkey.setOnClickListener(this);
		isShowMinikey.setOnClickListener(this);
		next.setOnClickListener(this);

		wifiInfos.setOnItemClickListener(this);
		wifiInfos.setAdapter(miniWifiRelayAdapter);
		relayHint = (TextView) view
				.findViewById(R.id.mini_gateway_relay_textView);
		sxgateway_iv = (ImageView) view
				.findViewById(R.id.mini_gateway_relay_imageView);

		// 更换中继页面图标
		if (is_sxgateway) {
			sxgateway_iv.setImageResource(R.drawable.sx_gateway_rellay);
			relayBody.setVisibility(View.VISIBLE);
			relayHint.setVisibility(View.VISIBLE);
			setMiniViewGone();
		}else {
			setEditTextChange(); //不是竖型网关下调用
		}
		judgeScanAndLogon();

	}

	private void setMiniViewGone(){
		chooseNote.setVisibility(View.GONE);
		rlSsid.setVisibility(View.GONE);
		rlPassword.setVisibility(View.GONE);
		isDefult.setVisibility(View.GONE);
		next.setVisibility(View.GONE);

		LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) showPhoto.getLayoutParams();
		DisplayMetrics dm=getResources().getDisplayMetrics();
		layoutParams.height=((dm.heightPixels)/5)*2;
		showPhoto.setLayoutParams(layoutParams);
	}
	@Override
	public void onClick(View v) {
		if (v==rlSsid){
			ssidPupopWindows(rlSsid);
		}if (v==isShowMiniSet){//是否使用默认的修改miniwifi的方式
			isShowMiniWifiSet();
		}if (v==passwordClean){
			wifiPassword.setText("");
		}if (v==isShowkey){
			// 密码可见 与不可见
			setPasswordIsShow("0");
		}if (v==isShowMinikey){
			setPasswordIsShow("1");
		}if (v==next){
			sendWifiSet();
		}
	}
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.about_back));
		getSupportActionBar().setTitle(
				mApplication.getResources()
						.getString(R.string.miniGW_RelayWifi));
	}

	private void sendWifiSet(){
		if (mEntiy==null||mEntiy.getCapabilities().equals("WEP")){
			return;
		}
		String wifiName= mEntiy.getSsid();
		String wifiKey="";
		if (!mEntiy.getCapabilities().equals("none")){
			wifiKey=wifiPassword.getText().toString().trim();
		}else if (mEntiy.getCapabilities().equals("none")){

		}
		else if (StringUtil.isNullOrEmpty(wifiKey)) {
			WLToast.showToast(getContext(), "密码长度为8~63位", 0);
			return;
		}
		String miniName,miniPassword;
		if (isSet){
			 miniName=miniWifiName.getText().toString().trim();
			 miniPassword=miniKey.getText().toString().trim();
			if (StringUtil.isNullOrEmpty(miniName)){
				return;
			}else if (StringUtil.isNullOrEmpty(miniPassword)){
				WLToast.showToast(getContext(),getResources().getString(R.string.set_password_not_null_hint),0);
				return;
			}else if (miniPassword.length()<8){
				WLToast.showToast(getContext(), "密码长度为8~63位", 0);
				return;
			}
		}else {
			miniName=wifiName;
			miniPassword=wifiKey;
		}
		Intent it =new Intent(getActivity(), MiniGatewayRelayWifiConnectActivity.class);
		if (ss!=null){
			it.putExtra("FLAG_2","EXTRA_0");
		}
//		else {
//			it.putExtra("FLAG_2","");
//		}

		it.putExtra("ssid",wifiName);
		it.putExtra("pwd",wifiKey);
		it.putExtra("address",mEntiy.getBSSID());
		it.putExtra("channel",mEntiy.getChanel());
		it.putExtra("encryption",mEntiy.getCapabilities());

		it.putExtra("mininame",miniName);
		it.putExtra("minipassword",miniPassword);
		if (isSet){
			it.putExtra("customflag",0);
		}else {
			it.putExtra("customflag",1);
		}
		getActivity().startActivity(it);
		getActivity().finish();
	}
	private boolean isSet=false;
	// 是否 使用默认的mini wifi修改的方式
	private  void isShowMiniWifiSet(){
		if (isSet){
			miniSsid.setVisibility(View.INVISIBLE);
			miniPassword.setVisibility(View.INVISIBLE);
			isShowMiniSet.setBackgroundResource(R.drawable.mini_radio_selectored);
			isSet=false;
		}else {
			miniSsid.setVisibility(View.VISIBLE);
			miniPassword.setVisibility(View.VISIBLE);
			isShowMiniSet.setBackgroundResource(R.drawable.mini_radio_no_selectored);
			isSet=true;
		}
		miniWifiName.setText("Mini_" + gwID.substring(6));
		if (miniEntity!=null){
			miniWifiName.setText(miniEntity.getSsid());
		}
	}

	private void setEditTextChange(){
		wifiPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				passwordClean.setVisibility(View.INVISIBLE);
				next.setBackgroundResource(R.color.ab_message_normal);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count>0){
//					passwordClean.setVisibility(View.VISIBLE);  //
				}
				if (count>7){
					next.setBackgroundResource(R.color.about_us_term_of_service);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length()>7){
					next.setBackgroundResource(R.color.about_us_term_of_service);
				}
			}
		});
	}
	private boolean isWifiShow=false;
	private boolean isMiniShow=false;
	private void  setPasswordIsShow(String type){
 		if (type.equals("0")){
			if (isWifiShow) {
				wifiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				isShowkey.setBackgroundResource(R.drawable.mini_password_gone);
				isWifiShow=false;
			}else {
				wifiPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				isShowkey.setBackgroundResource(R.drawable.mini_password_show);
				isWifiShow=true;
			}
		}else {
			if (isMiniShow) {
				miniKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				isShowMinikey.setBackgroundResource(R.drawable.mini_password_gone);
				isMiniShow=false;
			}else {
				miniKey.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				isShowMinikey.setBackgroundResource(R.drawable.mini_password_show);
				isMiniShow=true;
			}
		}
	}
	private String ss="";
	private WifiListThread wifiListThread=null;

	private void judgeScanAndLogon() {
		Intent intent = getActivity().getIntent();
		ss = intent.getStringExtra("FLAG_0");
		if (wifiListThread==null){
			wifiListThread=new WifiListThread();
			wifiListThread.start();
		}
		progressDialogManager.showDialog("WIFIINFO", getActivity(), null, null);
	}


	private void sendGetWifiData(){
		NetSDK.sendSetRouterConfigMsg(gwID, "4", null);
	}

	private class WifiListThread extends Thread{

		private  volatile boolean isRunning=true;
		public void Stop(){
			isRunning=false;
		}
		@Override
		public void run() {
					if (ss == null) {
						connectMiniGateWayAndSend();  //wifi config
					}else {
						NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "2", "get", null);
						NetSDK.setGatewayInfo(gwID, "0", null,null,null, null, null, null,null,null);
						sendGetWifiData();
					}
				}
	}
	private void connectMiniGateWayAndSend() {
			try {
				// 建立连接
				if(connection.isConnected() == false) {
					connection.connectGateway(new WifiUtil().getGatewayIP(), 11328);
				}
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("cmd", "getWifiList");
				jsonObject.put("msgid", "2");
				// 发送数据
				connection.sendMessage(jsonObject.toString());
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		Intent intent = getActivity().getIntent();
		String ss = intent.getStringExtra("FLAG_0");
		Bundle bundle = new Bundle();
		bundle.putString("FLAG_1", ss);
		bundle.putString("ssid", entites.get(arg2).getSsid());
		bundle.putString("address", entites.get(arg2).getBSSID());
		bundle.putString("encryption", entites.get(arg2).getCapabilities());
		bundle.putString("channel", entites.get(arg2).getChanel());
		bundle.putInt("zegbeechannel", zegbeeChannel);
		
		if (entites.get(arg2).getCapabilities().equals("WEP")) {
			fragment_wifi_wep = new MiniGateWayRelayConnectWifiWep();
			// bundle传入
			fragment_wifi_wep.setArguments(bundle);
			transaction.add(R.id.mini_gateway_relay_body, fragment_wifi_wep);
		} else {
			fragment_wifi_nor = new MiniGateWayRelayConnectWifiNor();
			fragment_wifi_nor.setArguments(bundle);
			transaction.add(R.id.mini_gateway_relay_body, fragment_wifi_nor);
		}

		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	}

	public void onEventMainThread(GatewaInfoEvent gatewayinfoevent) {
		String zgbChannel = gatewayinfoevent.getGwChannel();
		zegbeeChannel = Integer.parseInt(zgbChannel, 16);
	}

	private GetWifi_ifaceEntity miniEntity;
	public void onEventMainThread(RouterWifiSettingEvent event){
		List<GetWifi_ifaceEntity>  list=event.getWifi_ifaceList();
		if (list.size()>0){
			 miniEntity= list.get(0);
		}
	}
	// 参数MiniGatewayEvent是一个定义的类，封装4个数据参数 接收并解析
	public void onEventMainThread(MiniGatewayEvent gatewayevent) {
		if (wifiListThread!=null){
			wifiListThread.Stop();
		}
		if (entites.size()>0){
			return;
		}
		if (!CmdUtil.MINIGATEWAY_GET_RELAY_SEARCH.equals(gatewayevent
				.getCmdindex())) {
			return;
		}
		if(is_sxgateway){
			wifiNameString = getActivity().getIntent().getStringExtra("sxWifiName");
		}else{
			wifiNameString = "Mini_" + gwID.substring(6);
		}
		relayHint.setText((getResources().getString(
				R.string.miniGW_GetConnected)
				+" "+ wifiNameString + "\n" + getResources().getString(
				R.string.miniGW_selectRelayWifi)));
		JSONObject jsonObject;
		try {
			JSONArray jsonArray = new JSONArray(gatewayevent.getData());
			jsonObject = jsonArray.getJSONObject(0);
			String wifiInfo = jsonObject.getString("cell");
			JSONArray wifiinfos = new JSONArray(wifiInfo);
			for (int i = 0; i < wifiinfos.length(); i++) {
				JSONObject object = wifiinfos.getJSONObject(i);
				WifiInfoEntity entity = new WifiInfoEntity();
				entity.setBSSID(object.getString("address"));
				entity.setCapabilities(object.getString("encryption"));
				entity.setChanel(object.getString("channel"));
				entity.setLevel(object.getString("signal"));
				entity.setSsid(object.getString("essid"));
				entites.add(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				miniWifiRelayAdapter.swapData(entites);
				if (entites.size()>0){
					ssid.setText(entites.get(0).getSsid());
					isShowWifiNote(entites.get(0));

					mEntiy=entites.get(0);
				}
			}
		});
		progressDialogManager.dimissDialog("WIFIINFO", 0);
	}

	@Override
	public void connectionBroken(int reason) {
		System.out.println("连接失败-------->");
	}

	@Override
	public void receviedMessage(String msg) {
		if (wifiListThread!=null){
			wifiListThread.Stop();
		}
		if (entites.size()>0){
			return;
		}
		Wifiname_key = getArguments().getString("Wifiname_key");
		try {
			JSONObject jsonObject = new JSONObject(msg);
			JSONObject bodyObject = jsonObject.getJSONObject("body");
			JSONArray cellArray = bodyObject.getJSONArray("cell");
			for (int i = 0; i < cellArray.length(); i++) {
				JSONObject object = cellArray.getJSONObject(i);
				WifiInfoEntity entity = new WifiInfoEntity();
				entity.setBSSID(object.getString("address"));
				entity.setCapabilities(object.getString("encryption"));
				entity.setChanel(object.getString("channel"));
				entity.setLevel(object.getString("signal"));
				entity.setSsid(object.getString("essid"));
				entites.add(entity);
			}

		} catch (JSONException e) {
			Log.e("cfgmini", "", e);
		}

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				miniWifiRelayAdapter.swapData(entites);
				relayHint.setText((getResources().getString(R.string.miniGW_GetConnected)+ Wifiname_key + "\n" + getResources().getString(R.string.miniGW_selectRelayWifi)));
				if (entites.size()>0){
					ssid.setText(entites.get(0).getSsid());
					isShowWifiNote(entites.get(0));

					mEntiy=entites.get(0);
				}
				progressDialogManager.dimissDialog("WIFIINFO", 0);
			}
		});
	}

	//根据wifi的类型 是否显示WEPwifi的提示
	private void isShowWifiNote(WifiInfoEntity entity){
		String wifitype= entity.getCapabilities();
		if (wifitype.equals("WEP")){
			wifiNote.setVisibility(View.VISIBLE);
			next.setBackgroundResource(R.color.ab_message_normal);
		}else if (wifitype.equals("none")){
			rlPassword.setVisibility(View.GONE);

			next.setBackgroundResource(R.color.about_us_term_of_service);

			isLock.setVisibility(View.INVISIBLE); //设置密码是否有
		}
		else {
			wifiNote.setVisibility(View.GONE);
			rlPassword.setVisibility(View.VISIBLE);

			isLock.setVisibility(View.VISIBLE);

			next.setBackgroundResource(R.color.ab_message_normal);
		}
	}

	private WifiInfoEntity mEntiy;
	private void ssidPupopWindows(View v){
		moreWifi.setImageResource(R.drawable.change_wifi_pressed);
		View mView=LayoutInflater.from(getActivity()).inflate(com.wulian.icam.R.layout.item_list_view_popupwindow, null);
		ListView mListView=(ListView) mView.findViewById(com.wulian.icam.R.id.lv_pupop_window);
		miniWifiRelayAdapter.swapData(entites);
		mListView.setAdapter(miniWifiRelayAdapter);
		final PopupWindow popupWindow = new PopupWindow(mView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mEntiy =(WifiInfoEntity)parent.getItemAtPosition(position);
				ssid.setText(mEntiy.getSsid());
				isShowWifiNote(mEntiy);
				//每一项的点击事件
				TextView mTextView=	(TextView) view.findViewById(com.wulian.icam.R.id.tv_list_pupop_show_ssid);
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				moreWifi.setImageResource(R.drawable.change_wifi_normal);
			}
		});
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		popupWindow.setBackgroundDrawable(getResources().getDrawable(com.wulian.icam.R.color.transparent));
		popupWindow.showAsDropDown(v);
	}
}
