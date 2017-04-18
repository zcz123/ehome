package cc.wulian.smarthomev5.fragment.uei;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;

import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource.WL23_ResourceInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.entity.uei.UeiVirtualBtn;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import static cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control.pluginName;

public class CustomRemoteControlFragment extends WulianFragment{

	private View parentView;
	private GridView gvkeys;
	UeiUiArgs args_value=null;
	private Vibrator mVibrator01;  //声明一个振动器对象 
	UEIEntity uei=null;
	private CustomKeyAdapter customKeyAdapter;
	private UeiCommonEpdata commnEpdata;
	public ImageView TVLight=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mVibrator01 = ( Vibrator ) MainApplication.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		Bundle bundle = getArguments();
		if(bundle!=null){
			args_value=bundle.getParcelable("args");
			uei=args_value.ConvertToEntity();
			commnEpdata=new UeiCommonEpdata(args_value.getGwID(),args_value.getDevID(),args_value.getEp());
		}
		initBar();
//		initCustomKeyDialog();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_custom_remootecontrol,
				container, false);
		return parentView;
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);
		initView(paramView);
	}

	@Override
	public void onResume() {
		super.onResume();
		String uei_edit_save=SmarthomeFeatureImpl.getData("UEI_CUSSAVE_SUC","0");
		if(uei_edit_save.equals("1")){
			CustomRemoteControlFragment.this.getActivity().finish();
		}
	}

	private void initBar() {
		String brandTypeName="";
		WL23_ResourceInfo resourceInfo=WL_23_IR_Resource.getResourceInfo(uei.getDeviceType());
		if(resourceInfo.name>0){
			brandTypeName=getString(resourceInfo.name);
		}
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
//		getSupportActionBar().setIconText(getString(R.string.nav_device_title));
		getSupportActionBar().setTitle(uei.getBrandName()+brandTypeName);
		if(args_value.getViewMode()==1){
	    	getSupportActionBar().setIconText(getString(R.string.about_back));
	    	getSupportActionBar().setDisplayShowMenuTextEnabled(true);
	    	getSupportActionBar().setRightIconText(R.string.common_ok);
	    	getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent();
					intent.putExtra("epdata", customKeyAdapter.epdata);
					intent.putExtra("desc", customKeyAdapter.keydesc);
					mActivity.setResult(1, intent);
					mActivity.finish();
				}
			});
	    	
	    }else if(args_value.getViewMode()==0){
			getSupportActionBar().setDisplayShowMenuTextEnabled(true);
			getSupportActionBar().setIconText(getString(R.string.nav_device_title));
			getSupportActionBar().setRightIconText(R.string.device_edit);
			SmarthomeFeatureImpl.setData("UEI_CUSSAVE_SUC","0");
			getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
				@Override
				public void onClick(View v) {
					SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, args_value.getEp());
					SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPTYPE, args_value.getEpType());
					SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, args_value.getGwID());
					SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, args_value.getDevID());
//					Html5PlusWebViewActvity.webViewOnDestory=editWebViewOndestory;
					if(WL_23_IR_Control.isUsePlugin){
						getPlugin("newCustomkeys.html");
					}else {
						String strUri="file:///android_asset/uei/newCustomkeys.html";
						Intent intent = new Intent(DeviceDetailsActivity.instance, Html5PlusWebViewActvity.class);
						intent.putExtra(Html5PlusWebViewActvity.KEY_URL, strUri);
						JSONArray jsonArray = JSON.parseArray(uei.getVirkey());
						JSONObject relearnLearnDicJsonObject= new JSONObject();
						relearnLearnDicJsonObject.put("key", uei.getKey());
						relearnLearnDicJsonObject.put("nm", uei.getBrandCusName());
						relearnLearnDicJsonObject.put("kcs", jsonArray);
						SmarthomeFeatureImpl.setData("relearnLearnDic", relearnLearnDicJsonObject.toJSONString());
						DeviceDetailsActivity.instance.startActivity(intent);
					}
				}
			});
	    }
	}
//	Html5PlusWebViewActvity.WebView_OnDestory editWebViewOndestory=new Html5PlusWebViewActvity.WebView_OnDestory() {
//		@Override
//		public void OnDestory() {
//			String uei_edit_save=SmarthomeFeatureImpl.getData("UEI_CUSSAVE_SUC","0");
//			if(uei_edit_save.equals("1")){
//				CustomRemoteControlFragment.this.getActivity().finish();
//			}
//		}
//	};
	private void initView(View paramView) {
		gvkeys = (GridView) paramView.findViewById(R.id.gv_custom_key);
		TVLight=(ImageView) paramView.findViewById(R.id.iv_TV_light);
		if(this.args_value.getViewMode()==0){
			TVLight.setVisibility(View.VISIBLE);
		}else{
			TVLight.setVisibility(View.GONE);
		}
		if(uei!=null){
			List<UeiVirtualBtn> virbtnList= uei.GetVirKeyList();
			customKeyAdapter = new CustomKeyAdapter(this.mActivity.getBaseContext(),virbtnList);
			customKeyAdapter.setViewMode(args_value.getViewMode());
			gvkeys.setAdapter(customKeyAdapter);
		}
	}

//	private void initCustomKeyDialog() {
//		WLDialog.Builder builder = new WLDialog.Builder(this.getActivity());
//		builder.setContentView(R.layout.more_custom_key)
//				.setPositiveButton(R.string.cancel)
//				.setNegativeButton(R.string.common_ok)
//				.setTitle(R.string.add_custtom_key)
//				.setListener(new WLDialog.MessageListener() {			
//					@Override
//					public void onClickPositive(View contentViewLayout) {
//						
//					}			
//					@Override
//					public void onClickNegative(View contentViewLayout) {
//						
//					}
//		});
//	}
	
	
	private class CustomKeyAdapter extends BaseAdapter{
		private String epdata="";
		private String keydesc="";
		private int viewMode=0;
		private void setViewMode(int viewmode){this.viewMode=viewmode;}
		List<UeiVirtualBtn> virBtnList=null;
		Context mContext;
		public CustomKeyAdapter(Context context,List<UeiVirtualBtn> virBtnList){
			this.mContext=context;
			this.virBtnList=virBtnList;
		}
		
		@Override
		public int getCount() {
			return this.virBtnList.size();
		}

		@Override
		public Object getItem(int index) {
			return this.virBtnList.get(index);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewgroup) {
			HolderView holderview=null;
	    	if(view==null){
	    		holderview=new HolderView();
	    		view  = View.inflate(mActivity, R.layout.expand_grid_item, null);
	    		holderview.virBtn=(TextView) view.findViewById(R.id.tv_expand);
	    		if(this.viewMode==0){
		    		holderview.virBtn.setOnClickListener(virBtn_Onclick_fordevice);
		    		holderview.virBtn.setOnLongClickListener(virBtn_OnLongclick_fordevice);
	    		}else if(this.viewMode==1){
	    			holderview.virBtn.setOnClickListener(virBtn_Onclick_forhouse);
	    		}
	    		view.setTag(holderview);
	    	}else{
	    		holderview=(HolderView) view.getTag();
	    	}
	    	UeiVirtualBtn viewBtn=virBtnList.get(position);
	    	holderview.virBtn.setTag(viewBtn);
	    	holderview.virBtn.setText(viewBtn.getNm());
			return view;
		}
		
		private class HolderView{
			private TextView virBtn;
		}
		
		View.OnClickListener virBtn_Onclick_fordevice=new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UeiVirtualBtn virBtn=(UeiVirtualBtn) view.getTag();
				mVibrator01.vibrate(300);
				showLight();
				String epData=CustomRemoteControlFragment.this.commnEpdata.getEpDataForStudy(virBtn.getLc());
				CustomRemoteControlFragment.this.commnEpdata.sendCommand12(getContext(),epData);
			}
		};
		public View.OnLongClickListener virBtn_OnLongclick_fordevice=new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View virtualKey) {
				UeiStudyAgain.InitData();
				UeiVirtualBtn virBtn=(UeiVirtualBtn) virtualKey.getTag();
				UeiStudyAgain ueiStudyAgain=new UeiStudyAgain(CustomRemoteControlFragment.this.getApplication(),CustomRemoteControlFragment.this.args_value);
				ueiStudyAgain.SetMyActivity(CustomRemoteControlFragment.this.getActivity());
				String studyCode=virBtn.getLc();
				ueiStudyAgain.setStudyCode(studyCode);
				ueiStudyAgain.BeginStudy(virtualKey);
				return true;
			}
		};
		private View curSelectedView=null;
		View.OnClickListener virBtn_Onclick_forhouse=new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if(curSelectedView!=null){
					curSelectedView.setSelected(false);
				}
				view.setSelected(true);
				curSelectedView=view;
				UeiVirtualBtn virBtn=(UeiVirtualBtn) view.getTag();
				keydesc=virBtn.getNm();
				epdata=CustomRemoteControlFragment.this.commnEpdata.getEpDataForStudy(virBtn.getLc());
			}
		};
	}
	private boolean isStart = false;
	private int lightCount = 0;
	private void showLight(){
		if(TVLight==null){
			return;
		}
		isStart = true;
		new Thread() {
			@Override
			public void run() {

				super.run();
				while (isStart) {
					try {
						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				lightCount ++;
				if(lightCount>6){
					lightCount=0;
					isStart=false;
				}else{
					if(lightCount%2==1){
						TVLight.setImageResource(R.drawable.remote_control_light_2);
					}else{
						TVLight.setImageResource(R.drawable.remote_control_light_1);
					}
				}
			}
		}
	};

	// 获取插件
	private void getPlugin(final String entryPager) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mActivity, pluginName, new PluginsManager.PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file = new File(model.getFolder(), entryPager);
						String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
						if (file.exists()) {
							uri = "file:///" + file.getAbsolutePath();
						} else if (LanguageUtil.isChina()) {
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}

						JSONArray jsonArray = JSON.parseArray(uei.getVirkey());
						JSONObject relearnLearnDicJsonObject= new JSONObject();
						relearnLearnDicJsonObject.put("key", uei.getKey());
						relearnLearnDicJsonObject.put("nm", uei.getBrandCusName());
						relearnLearnDicJsonObject.put("kcs", jsonArray);
						SmarthomeFeatureImpl.setData("relearnLearnDic", relearnLearnDicJsonObject.toJSONString());
						IntentUtil.startHtml5PlusActivity(mActivity, uri);
						DeviceDetailsActivity.instance.finish();
						mActivity.finish();
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if (hint != null && hint.length() > 0) {
							Handler handler = new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mActivity, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}
}
