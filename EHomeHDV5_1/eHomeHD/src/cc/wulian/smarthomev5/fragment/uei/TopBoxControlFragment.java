package cc.wulian.smarthomev5.fragment.uei;


import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.adapter.uei.TopBoxTabAdapter;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.*;

import android.R.integer;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource.WL23_ResourceInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.uei.ExpandPopupWindow;
import cc.wulian.smarthomev5.activity.uei.KeyboardPopupWindow;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.HttpUtil;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.Preference;

public class TopBoxControlFragment extends WulianFragment implements OnClickListener{
	/*下面的任务尚未完成：
	 * 1.拓展按键面板（ExpandPopupWindow）功能未编写
	 * */
	private View parentView;
	UeiUiArgs args_value=null;
	UEIEntity uei=null;
	TextView tvtestResult=null;
	

	/*四个Fragment及页签，顺序是：遥控、节目、频道、收藏*/
	private List<WulianFragment> fragments=new ArrayList<>();
	private ViewPager viewPager;
	private int currPosition=-1;//当前选中时是索引,从0开始
	List<LinearLayout> tablayouts=null;//Tab外边的框
	List<TextView> tabTitles=null;//存放文字
	List<ImageView> tabImages=null;//图片
	List<Integer> normalImages=null;//普通图片
	List<Integer> preImages=null;//选中时的图片
//	private String url_Program="file:///android_asset/uei/contentList.html";//节目
//	private String url_Channel="file:///android_asset/uei/channelList.html";//频道
//	private String url_Collection="file:///android_asset/uei/ConnectionList.html";//收藏
	private String url_Program="";//节目
	private String url_Channel="";//频道
	private String url_Collection="";//收藏
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			args_value=bundle.getParcelable("args");
			uei=args_value.ConvertToEntity();
			
			YkanSDKManager.init(this.mActivity, args_value.getAppID(),args_value.getDevID());
			YkanSDKManager.getInstance().setLogger(true);

			if(!StringUtil.isNullOrEmpty(uei.getProCode())){//根据运营商编号获取频道列表
				new Thread(new Runnable(){
					@Override
					public void run() {
						YkanIRInterfaceImpl ykanImpl=new YkanIRInterfaceImpl();
						String ePGAllChannels=ykanImpl.getAllChannelsByPid(uei.getProCode(),1);
						SmarthomeFeatureImpl.setData("EPGAllChannels",ePGAllChannels);
					}
				}).start();
			}else {
				SmarthomeFeatureImpl.setData("EPGAllChannels","");
			}
		}

		initResources();
	}
	
	private void initResources(){
		normalImages=new ArrayList<>();
		normalImages.add(R.drawable.epg_bottom1_normal);
		normalImages.add(R.drawable.epg_bottom2_normal);
		normalImages.add(R.drawable.epg_bottom3_normal);
		normalImages.add(R.drawable.epg_bottom4_normal);
		preImages=new ArrayList<>();
		preImages.add(R.drawable.epg_bottom1_pre);
		preImages.add(R.drawable.epg_bottom2_pre);
		preImages.add(R.drawable.epg_bottom3_pre);
		preImages.add(R.drawable.epg_bottom4_pre);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_topbox_remotecontrol, container, false);
		return parentView;
	}
	@Override
	public void onViewCreated(View paramView, Bundle paramBundle){
	    super.onViewCreated(paramView, paramBundle); 
	    initView(paramView);
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
	    getSupportActionBar().setTitle(uei.getBrandName()+brandTypeName);
	    getSupportActionBar().setIconText("红外转发器");
	    if(args_value.getViewMode()==1){
//	    	getSupportActionBar().setIconText(getString(R.string.about_back));
//	    	getSupportActionBar().setDisplayShowMenuTextEnabled(true);
//	    	getSupportActionBar().setRightIconText(R.string.common_ok);
//	    	getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
//				@Override
//				public void onClick(View v) {
//					String epData=TopBoxControlFragment.this.virkeyBtn.getCurrEpData();
//					Intent intent=new Intent();
//					intent.putExtra("epdata", epData);
//					mActivity.setResult(1, intent);
//					mActivity.finish();
//				}
//			});

	    }else if(args_value.getViewMode()==0){
//		   getSupportActionBar().setIconText(getString(R.string.nav_device_title));
	    }
	 }
	 
	 private void initView(View paramView){
		
		 tvtestResult=(TextView) paramView.findViewById(R.id.tvtestResult);
		 paramView.findViewById(R.id.btntest).setOnClickListener(this);

		 viewPager=(ViewPager) paramView.findViewById(R.id.viewPager);
		 addAdatpterFragment();
		 addTabItemRes(paramView);
		 for(int i=0;i<4;i++){
			 tablayouts.get(i).setTag(i);
			 tabTitles.get(i).setTag(i);
			 tabImages.get(i).setTag(i);
			 tablayouts.get(i).setOnClickListener(tabOnClick);
			 tabTitles.get(i).setOnClickListener(tabOnClick);
			 tabImages.get(i).setOnClickListener(tabOnClick);
		 }
		 viewPager.setOnPageChangeListener(viewPageChanged);
		 changedTag(0);
		 initBar();
	}
	private void addAdatpterFragment(){
		if(fragments==null){
			fragments=new ArrayList<>();
		}
		fragments.clear();
		TVRemoteControlFragment tvRemoteFrg=new TVRemoteControlFragment();
		Bundle bundle=new Bundle();
		bundle.putBoolean("isShowBar",true);
		bundle.putParcelable("args",args_value);
		tvRemoteFrg.setArguments(bundle);
		fragments.add(tvRemoteFrg);
		if(WL_23_IR_Control.isUsePlugin){
			url_Program= Preference.getPreferences().getUeiTopBox_Program();
			url_Channel= Preference.getPreferences().getUeiTopBox_Channel();
			url_Collection= Preference.getPreferences().getUeiTopBox_Collection();
		}
		else {
			url_Program="file:///android_asset/uei/contentList.html";//节目
			url_Channel="file:///android_asset/uei/channelList.html";//频道
			url_Collection="file:///android_asset/uei/ConnectionList.html";//收藏
		}
		addTabWebFrg(url_Program);
		addTabWebFrg(url_Channel);
		addTabWebFrg(url_Collection);

		TopBoxTabAdapter topBoxTabAdapter=new TopBoxTabAdapter(this.mActivity.getSupportFragmentManager(),fragments);
		viewPager.setAdapter(topBoxTabAdapter);
	}

	private  void  addTabWebFrg(String url){
		TopBoxEpgFragment epgFragment=new TopBoxEpgFragment();
		Bundle bundle_Program=new Bundle();
		bundle_Program.putString("webUrl",url);
		epgFragment.setArguments(bundle_Program);
		fragments.add(epgFragment);
	}
	private  void  addTabItemRes(View paramView){
		if(tablayouts==null){
			tablayouts=new ArrayList<>();
		}
		tablayouts.clear();
		tablayouts.add((LinearLayout)paramView.findViewById(R.id.llRemote));
		tablayouts.add((LinearLayout)paramView.findViewById(R.id.llProgram));
		tablayouts.add((LinearLayout)paramView.findViewById(R.id.llChannel));
		tablayouts.add((LinearLayout)paramView.findViewById(R.id.llCollection));


		if(tabTitles==null){
			tabTitles=new ArrayList<>();
		}
		tabTitles.clear();
		tabTitles.add((TextView)paramView.findViewById(R.id.tvRemote));
		tabTitles.add((TextView)paramView.findViewById(R.id.tvProgram));
		tabTitles.add((TextView)paramView.findViewById(R.id.tvChannel));
		tabTitles.add((TextView)paramView.findViewById(R.id.tvCollection));


		if(tabImages==null){
			tabImages=new ArrayList<>();
		}
		tabImages.clear();
		tabImages.add((ImageView)paramView.findViewById(R.id.ivRemote));
		tabImages.add((ImageView)paramView.findViewById(R.id.ivProgram));
		tabImages.add((ImageView)paramView.findViewById(R.id.ivChannel));
		tabImages.add((ImageView)paramView.findViewById(R.id.ivCollection));
	}
    View.OnClickListener tabOnClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getTag()!=null){
				int position=Integer.parseInt(v.getTag().toString());
				changedTag(position);
			}
			
		}
	};
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btntest:
			{
				new Thread(new Runnable(){
				    @Override
				    public void run() {
				    	YkanIRInterfaceImpl ykanImpl=new YkanIRInterfaceImpl();
						final String json=ykanImpl.getAllChannelsByPid("155",1);
						TopBoxControlFragment.this.mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tvtestResult.setText(json);
							}
						});
						
				    }
				}).start();
			}
			break;
		default:
			break;
		}		
	}

	OnPageChangeListener viewPageChanged=new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			TopBoxControlFragment.this.changedTag(position);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	private void changedTag(int position){
		if(currPosition!=position){
			currPosition=position;
			setBarShowOrHide();
			setTabTitleAndImages();
			viewPager.setCurrentItem(currPosition);
		}
	}

	/**
	 * 设置Android原生标题栏显示与隐藏
	 */
	private  void setBarShowOrHide(){
		if(currPosition>=1){//除了第一个是原生的，其它webView中都有二级页面
			TopBoxEpgFragment epgFragment= (TopBoxEpgFragment) fragments.get(currPosition);
			String url=epgFragment.getWebUrl();
			if(!StringUtil.isNullOrEmpty(url)){
				//二级标题栏出现的时候，需要隐藏原生的bar
				if(url.contains("programDetail")||url.contains("EPGList")){
					getSupportActionBar().hide();
				}else {
					getSupportActionBar().show();
				}
			}
		}else {
			if(!getSupportActionBar().isShowing()){
				getSupportActionBar().show();
			}
		}
	}

	/**
	 * 设置TabBar标题及图标
	 */
	private void setTabTitleAndImages(){
		for(int i=0;i<4;i++){
			tabTitles.get(i).setTextColor(getResources().getColor(R.color.gray));
			tabImages.get(i).setBackgroundResource(normalImages.get(i));
			if(i==currPosition){
				tabTitles.get(i).setTextColor(getResources().getColor(R.color.epgbottom_textcolor));
				tabImages.get(i).setBackgroundResource(preImages.get(i));
			}
		}
	}
	
}
