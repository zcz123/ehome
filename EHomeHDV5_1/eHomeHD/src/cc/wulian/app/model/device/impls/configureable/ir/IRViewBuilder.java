package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.configureable.ir.IRStudyFragment.EditDeviceIRInfoEvent;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurModelListener;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurSwitchListener;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurTempListener;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.adapter.WLOperationAdapter;
import cc.wulian.smarthomev5.adapter.WLOperationAdapter.MenuItem;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.InputMethodUtils;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import de.greenrobot.event.EventBus;

public class IRViewBuilder {

	private int keyRes[] = new int[]{
			R.id.device_ir_key_on,R.id.device_ir_key_tv_av,R.id.device_ir_key_off,R.id.device_ir_key_1,R.id.device_ir_key_2,R.id.device_ir_key_3,
			R.id.device_ir_key_4,R.id.device_ir_key_5,R.id.device_ir_key_6,R.id.device_ir_key_7,R.id.device_ir_key_8,R.id.device_ir_key_9,
			R.id.device_ir_key_info,R.id.device_ir_key_0,R.id.device_ir_key_back,R.id.device_ir_key_up,R.id.device_ir_key_right,R.id.device_ir_key_down,
			R.id.device_ir_key_left,R.id.device_ir_key_ok,R.id.device_ir_key_menu,R.id.device_ir_key_voice
	};
	
	
	private Context context;
	private View rootView;
	private LinearLayout headLineLayout;
	private LinearLayout contentView;
	private LayoutInflater inflater;
	private IRGroupManager irGroupManager; 
	private String currentType = null;
	private String studyItemCode;
//	private StringBuffer epDataBuffer;
	private View childView;
//	private boolean isShowEditDialog;
	private boolean isCtrlModeHot = true;
	private boolean isOpen = true;
	private boolean isCtrlModeCool;
	private boolean isCtrlModeFan;
	private String newName;
	private String selectIrType;
	private boolean ishouse = false;
	private SelectIREpDataListener selectIREpDataListener;
	
	protected ProgressDialogManager mDialogManager = ProgressDialogManager.getDialogManager();
	private static final String SEND_STUDY_KEY = "send_study_key";
	public IRViewBuilder(Context context,IRGroupManager irGroupManager){
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.irGroupManager = irGroupManager;
		
	}
	public void initControlView(){
		rootView = inflater.inflate(R.layout.device_ir_contianer_content,null);
		headLineLayout = (LinearLayout)rootView.findViewById(R.id.device_ir_head_ll);
		contentView = (LinearLayout)rootView.findViewById(R.id.device_ir_content);
	}
	/**
	 * 创建控制视图，通过selectType选中类型，判断当前选中状态来移除视图，并改变成相应控制视图
	 * @param selectType
	 * @return
	 */
	
	public boolean isHasGrouptype(){
		if(irGroupManager.getSTBGroup().size() >0 || irGroupManager.getAriGroup().size() >0 ||
				irGroupManager.getGeneralGroupSize() >0)
			return true;
		return false;
	}
	public boolean isSelectSTBGrouptype(){
		if(irGroupManager.getSTBGroup().size() >0)
			return true;
		return false;
	}
	public boolean isSelectAriGrouptype(){
		if(irGroupManager.getAriGroup().size() >0)
			return true;
		return false;
	}
	public boolean isSelectGeneralGrouptype(){
		if(irGroupManager.getGeneralGroupSize() >0)
			return true;
		return false;
	}
	public View createControlView(String selectType){
		if(StringUtil.isNullOrEmpty(selectType)){
			if(StringUtil.isNullOrEmpty(currentType)){
				if(irGroupManager.getSTBGroup().size() >0)
					selectType = IRGroupManager.TYPE_STB;
				else if(irGroupManager.getAriGroup().size() >0)
					selectType = IRGroupManager.TYPE_AIR_CONDITION;
				else if(irGroupManager.getGeneralGroupSize() >0)
					selectType = IRGroupManager.TYPE_GENERAL;
			}
			else{
				selectType = currentType;
			}
		}else{
			currentType = selectType;
		}
		headLineLayout.removeAllViews();
		contentView.removeAllViews();
		createHeadView(selectType);
		if(IRGroupManager.TYPE_STB.equals(selectType)){
			createControlSTBContentView();
		}else if(IRGroupManager.TYPE_AIR_CONDITION.equals(selectType)){
			createControlAirContentView();
		}else if(IRGroupManager.TYPE_GENERAL.equals(selectType)){
			createControlGeneralContentView();
		}else{
			createEmptyView();
		}
		return rootView;
	}
	private void createHeadView(String selectType) {
		createControlSTBHeadView(selectType);
		createControlAirHeadView(selectType);
		createControlGenearlHeadView(selectType);
	}
	private void createLinkHeadView(String selectType,final StringBuffer codeBuffer,boolean ishouse) {
		createLinkSTBHeadView(selectType,codeBuffer,ishouse);
		createLinkAirHeadView(selectType,codeBuffer,ishouse);
		createLinkGenearlHeadView(selectType,codeBuffer,ishouse);
	}
	
	/**
	 * 创建联动顶盒控制头视图
	 * @param selectType
	 */
	public void createLinkSTBHeadView(String selectType,final StringBuffer codeBuffer,final boolean ishouse){
		final IRGroup group = irGroupManager.getSTBGroup();
		if(group != null && group.size() >0 ){
			Button headTextView = new Button(context);
			headTextView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2Pix(context, 80),DisplayUtil.dip2Pix(context, 40)));
			headTextView.setGravity(Gravity.CENTER);
			headTextView.setText(group.getGroupName());
			headTextView.setTextColor(R.drawable.device_ir_stb_head_font_color_selector);
			headTextView.setBackgroundResource(R.drawable.device_ir_head_bg);
			headLineLayout.addView(headTextView);
			if(IRGroupManager.TYPE_STB.equals(selectType)){
				headTextView.setSelected(true);
			}else{
				headTextView.setSelected(false);
			}
			headTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createLinkView(IRGroupManager.TYPE_STB,codeBuffer,ishouse);
				}
			});
		}
	}
	
	/**
	 * 创建机顶盒控制头视图
	 * @param selectType
	 */
	public void createControlSTBHeadView(String selectType){
		final IRGroup group = irGroupManager.getSTBGroup();
		if(group != null && group.size() >0 ){
			Button headTextView = new Button(context);
			headTextView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2Pix(context, 80),DisplayUtil.dip2Pix(context, 40)));
			headTextView.setGravity(Gravity.CENTER);
			headTextView.setText(group.getGroupName());
			headTextView.setTextColor(R.drawable.device_ir_stb_head_font_color_selector);
			headTextView.setBackgroundResource(R.drawable.device_ir_head_bg);
			headLineLayout.addView(headTextView);
			if(IRGroupManager.TYPE_STB.equals(selectType)){
				headTextView.setSelected(true);
			}else{
				headTextView.setSelected(false);
			}
			headTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createControlView(IRGroupManager.TYPE_STB);
					
				}
			});
		}
	}
	
	/**
	 * 创建机顶盒控制视图
	 */
	private void createControlSTBContentView() {
		LinearLayout stbView = (LinearLayout)inflater.inflate(R.layout.device_ir_stb_content, null);
		//通过将button的id放到相应数组里面，来通过遍历并初始化button--View
		for(int res : keyRes){
			final LinearLayout controSTBlayout = (LinearLayout) stbView.findViewById(res);
			childView = controSTBlayout.getChildAt(0);
			final String  code = childView.getTag().toString();
			final DeviceIRInfo info = irGroupManager.getDeviceIRInfo(IRGroupManager.TYPE_STB, code);
			//相应的button是否为不为空和学习状态，则为选中
			if(info != null && info.isStudy()){
				childView.setSelected(true);
			}
			controSTBlayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					buttonControSTBClick(code);
				}
			});
			childView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					buttonControSTBClick(code);
				}
			});
		}
		stbView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(stbView);
	}
	
	/**
	 * 创建联动空调的头视图
	 * @param selectType
	 */
	public void createLinkAirHeadView(String selectType,final StringBuffer codebuffer,final boolean ishouse){
		final IRGroup group = irGroupManager.getAriGroup();
		if(group != null && group.size() >0){
			Button headTextView = new Button(context);
			headTextView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2Pix(context, 80),DisplayUtil.dip2Pix(context, 40)));
			headTextView.setGravity(Gravity.CENTER);
			headTextView.setText(group.getGroupName());
			headLineLayout.addView(headTextView);
			headTextView.setBackgroundResource(R.drawable.device_ir_head_bg);
			headTextView.setTextColor(R.drawable.device_ir_stb_head_font_color_selector);
			if(IRGroupManager.TYPE_AIR_CONDITION.equals(selectType)){
				headTextView.setSelected(true);
			}else{
				headTextView.setSelected(false);
			}
			headTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createLinkView(IRGroupManager.TYPE_AIR_CONDITION,codebuffer,ishouse);
					
				}
			});
		}
	}
	private void buttonControSTBClick(String code){

		//控制码就是button的tag
		String keySet = code;
		//通过TYPE_STB和控制码得到机顶盒的相应控制码
		DeviceIRInfo info = irGroupManager.getDeviceIRInfo(IRGroupManager.TYPE_STB, keySet);
		if(info == null || !info.isStudy())
			WLToast.showToast(context,context.getString(R.string.device_ir_no_study), WLToast.TOAST_SHORT);
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(CmdUtil.IR_MODE_CTRL);
			sb.append(StringUtil.appendLeft(info.getCode(), 3, '0'));
			String sendData = sb.toString();
			//发送控制命令(2控制码000)补全0
			SendMessage.sendControlDevMsg(
					irGroupManager.getGwID(), 
					irGroupManager.getDevID(),
					WulianDevice.EP_14,
					ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL,
					sendData);	
		}
		
	
	}
	/**
	 * 创建空调的头视图
	 * @param selectType
	 */
	public void createControlAirHeadView(String selectType){
		final IRGroup group = irGroupManager.getAriGroup();
		if(group != null && group.size() >0){
			Button headTextView = new Button(context);
			headTextView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2Pix(context, 80),DisplayUtil.dip2Pix(context, 40)));
			headTextView.setGravity(Gravity.CENTER);
			headTextView.setText(group.getGroupName());
			headLineLayout.addView(headTextView);
			headTextView.setBackgroundResource(R.drawable.device_ir_head_bg);
			headTextView.setTextColor(R.drawable.device_ir_stb_head_font_color_selector);
			if(IRGroupManager.TYPE_AIR_CONDITION.equals(selectType)){
				headTextView.setSelected(true);
			}else{
				headTextView.setSelected(false);
			}
			headTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createControlView(IRGroupManager.TYPE_AIR_CONDITION);
					
				}
			});
		}
	}
	/**
	 * 创建空调的控制视图
	 */
	private void createControlAirContentView(){
		LinearLayout airView = (LinearLayout) inflater.inflate(R.layout.device_ir_air_content, null);
		GridView airGridview = (GridView) airView.findViewById(R.id.device_ir_air_gv);
		IRAirAdapter airAdapter = new IRAirAdapter(context, irGroupManager.getAriGroup().getAllKeys(), null);
		airGridview.setAdapter(airAdapter);
		airAdapter.setMode(IRAirAdapter.MODE_CONTROL);
		airView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(airView);
	}
	/**
	 * 创建通用的头视图
	 * @param selectType
	 */
	public void createControlGenearlHeadView(String selectType){
		final IRGroup group = irGroupManager.getGeneralGroup();
		if(irGroupManager.getGeneralGroupSize() >0){
			Button headTextView = new Button(context);
			headTextView.setText(group.getGroupName());
			headTextView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2Pix(context, 80),DisplayUtil.dip2Pix(context, 40)));
			headTextView.setGravity(Gravity.CENTER);
			headTextView.setBackgroundResource(R.drawable.device_ir_head_bg);
			headTextView.setTextColor(R.drawable.device_ir_stb_head_font_color_selector);
			headLineLayout.addView(headTextView);
			if(IRGroupManager.TYPE_GENERAL.equals(selectType)){
				headTextView.setSelected(true);
			}else{
				headTextView.setSelected(false);
			}
			headTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createControlView(IRGroupManager.TYPE_GENERAL);
					
				}
			});
		}
	}
	/**
	 * 创建通用的控制视图
	 * @param selectType
	 */
	private void createControlGeneralContentView() {
		LinearLayout generalView = (LinearLayout)inflater.inflate(R.layout.device_ir_genenral_content, null);
		GridView contentGridView = (GridView)generalView.findViewById(R.id.device_ir_general_gv);
		
		IRGeneralAdapter adapter = new IRGeneralAdapter(context, irGroupManager.getGeneralGroupDeviceIrInfos(),null);
		adapter.setMode(IRGeneralAdapter.MODE_CONTROL);
		contentGridView.setAdapter(adapter);
		generalView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(generalView);
	}
	
	public View createLinkView(String selectType,final StringBuffer codeBuffer, boolean ishouse){
		if(StringUtil.isNullOrEmpty(selectType)){
			if(StringUtil.isNullOrEmpty(currentType)){
				if(irGroupManager.getSTBGroup().size() >0)
					selectType = IRGroupManager.TYPE_STB;
				else if(irGroupManager.getAriGroup().size() >0)
					selectType = IRGroupManager.TYPE_AIR_CONDITION;
				else if(irGroupManager.getGeneralGroupSize() >0)
					selectType = IRGroupManager.TYPE_GENERAL;
			}
			else{
				selectType = currentType;
			}
		}
		headLineLayout.removeAllViews();
		contentView.removeAllViews();
		this.ishouse = ishouse;
		createLinkHeadView(selectType,codeBuffer,ishouse);
		if(IRGroupManager.TYPE_STB.equals(selectType)){
			createLinkSTBContentView(codeBuffer);
		}else if(IRGroupManager.TYPE_AIR_CONDITION.equals(selectType)){
			createLinkAirContentView(codeBuffer);
		}else if(IRGroupManager.TYPE_GENERAL.equals(selectType)){
			createLinkGeneralContentView(codeBuffer);
		}else if(IRGroupManager.NO_TYPE.equals(selectType)){
			headLineLayout.setVisibility(View.GONE);
			createEmptyLinkView();
		}
		return rootView;
	}
	
	/**
	 * 创建通用连联动的头视图
	 * @param selectType
	 */
	public void createLinkGenearlHeadView(String selectType,final StringBuffer codeBuffer,final boolean ishouse){
		final IRGroup group = irGroupManager.getGeneralGroup();
		if(irGroupManager.getGeneralGroupSize() >0){
			Button headTextView = new Button(context);
			headTextView.setText(group.getGroupName());
			headTextView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2Pix(context, 80),DisplayUtil.dip2Pix(context, 40)));
			headTextView.setGravity(Gravity.CENTER);
			headTextView.setBackgroundResource(R.drawable.device_ir_head_bg);
			headTextView.setTextColor(R.drawable.device_ir_stb_head_font_color_selector);
			headLineLayout.addView(headTextView);
			if(IRGroupManager.TYPE_GENERAL.equals(selectType)){
				headTextView.setSelected(true);
			}else{
				headTextView.setSelected(false);
			}
			headTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createLinkView(IRGroupManager.TYPE_GENERAL,codeBuffer,ishouse);
					
				}
			});
		}
	}
	/**
	 * 创建机顶盒联动
	 */
	private void createLinkSTBContentView(final StringBuffer codeBuffer) {
		LinearLayout stbView = (LinearLayout)inflater.inflate(R.layout.device_ir_scene_stb_content, null);
		//通过将button的id放到相应数组里面，来通过遍历并初始化button--View
		for(int res : keyRes){
			final LinearLayout controSTBlayout = (LinearLayout) stbView.findViewById(res);
			childView = controSTBlayout.getChildAt(0);
			final String  childCode = childView.getTag().toString();
			final DeviceIRInfo info = irGroupManager.getDeviceIRInfo(IRGroupManager.TYPE_STB, childCode);
			//相应的button是否为不为空和学习状态，则为选中
			if(info != null && info.isStudy()){
				if(codeBuffer != null && codeBuffer.length() > 1){
					if(info.getCode().equals(codeBuffer.substring(1))){
						childView.setPressed(true);
				}
			}
				childView.setSelected(true);
			}
			controSTBlayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					buttonLinkSTBClick(childCode,codeBuffer);
				}
			});
			childView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					buttonLinkSTBClick(childCode,codeBuffer);
				}
			});
		}
		stbView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(stbView);
	}
	
	private void buttonLinkSTBClick(String childCode,StringBuffer codeBuffer){

		//控制码就是button的tag
		String keySet = childCode;
		//通过TYPE_STB和控制码得到机顶盒的相应控制码
		DeviceIRInfo info = irGroupManager.getDeviceIRInfo(IRGroupManager.TYPE_STB, keySet);
		if(info == null || !info.isStudy())
			WLToast.showToast(context,context.getString(R.string.device_ir_no_study), WLToast.TOAST_SHORT);
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(CmdUtil.IR_MODE_CTRL);
			sb.append(StringUtil.appendLeft(info.getCode(), 3, '0'));
			String sendData = sb.toString();
			if(codeBuffer != null){
				codeBuffer.delete(0, codeBuffer.length());
				codeBuffer.append(sendData);
				contentView.removeAllViews();
				createLinkSTBContentView(codeBuffer);
				if(ishouse){
					fireSelectIREpDataListener(codeBuffer);
				}
			}
		}
	}
	private void createLinkAirContentView(StringBuffer codeBuffer){
		LinearLayout airView = (LinearLayout) inflater.inflate(R.layout.device_ir_air_content, null);
		GridView airGridview = (GridView) airView.findViewById(R.id.device_ir_air_gv);
		IRAirLinkAdapter airAdapter = new IRAirLinkAdapter(context, irGroupManager.getAriGroup().getAllKeys(), null,codeBuffer);
		airAdapter.setMode(IRAirLinkAdapter.MODE_CONTROL);
		airGridview.setAdapter(airAdapter);
//		airAdapter.setMode(IRAirAdapter.MODE_CONTROL);
		airView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(airView);
	}
	
	
	/**
	 * 创建通用的控制视图
	 * @param selectType
	 */
	private void createLinkGeneralContentView(StringBuffer codeBuffer) {
		LinearLayout generalView = (LinearLayout)inflater.inflate(R.layout.device_ir_genenral_content, null);
		GridView contentGridView = (GridView)generalView.findViewById(R.id.device_ir_general_gv);
		
		IRGeneralLinkAdapter adapter = new IRGeneralLinkAdapter(context, irGroupManager.getGeneralGroupDeviceIrInfos(),null,codeBuffer);
		adapter.setMode(IRGeneralLinkAdapter.MODE_CONTROL);
		contentGridView.setAdapter(adapter);
		generalView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(generalView);
	}
	
	/**
	 * 创建联动没有设备提示
	 */
	public void createEmptyLinkView(){
		//动态添加一个button按钮
		final TextView text = new TextView(context);
		text.setText(context.getString(R.string.device_ir_empty_link_text));
		text.setTextColor(context.getResources().getColor(R.color.black));
		text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		text.setPadding(0, 25, 0, 0);
		text.setGravity(Gravity.CENTER);
		contentView.addView(text);
	}
	/**
	 * 创建空白区域，添加相应设备
	 */
	public void createEmptyView(){
		//动态添加一个button按钮
		final Button button = new Button(context);
		final TextView textView = new TextView(context);
		LinearLayout layoutView = new LinearLayout(context);
		button.setBackgroundResource(R.drawable.device_ir_add_group);
		textView.setText(context.getString(R.string.device_ir_empty_view_remind));
		textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,4));
		layoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,0,1));
		button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		textView.setPadding(10, 0, 10, 0);
		textView.setGravity(Gravity.CENTER);
		button.setGravity(Gravity.BOTTOM);
		button.setPadding(0, 0, 0, 10);
		contentView.addView(textView);
		layoutView.addView(button);
		contentView.addView(layoutView);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAddIrGroupViewPopupWindow(button);
			}
		});
		
	}
	
	public void showAddIrGroupViewPopupWindow(View view){
		final DownUpMenuList menu = new DownUpMenuList(context);
		List<DownUpMenuItem> items = new ArrayList<DownUpMenuItem>();
		for(final IRGroup group : irGroupManager.getIRGroups()){
			int size = group.size();
			if(IRGroupManager.TYPE_GENERAL.equals(group.getGroupType())){
				size = irGroupManager.getGeneralGroupSize();
			}
			if(size ==0){
				items.add(new DownUpMenuItem(context) {
					
					@Override
					public void initSystemState() {
						mTitleTextView.setText(group.getGroupName());
					}
					
					@Override
					public void doSomething() {
						List<DeviceIRInfo> infos = irGroupManager.getDefaultDeviceIRInfo(group.getGroupType());
						JsonTool.saveIrInfo(context, irGroupManager.getGwID(), irGroupManager.getDevID(), WulianDevice.EP_14,infos, group.getGroupType());
						menu.dismiss();
					}
				});
			}
		}
		items.add(new DownUpMenuItem(context) {
			
			@Override
			public void initSystemState() {
				mTitleTextView.setText(R.string.device_cancel);
			}
			
			@Override
			public void doSomething() {
				menu.dismiss();
			}
		});
		menu.setMenu(items);
		menu.showBottom(view);
	}
	
	
	public View createStudySTBView(){
		LinearLayout stbView = (LinearLayout)inflater.inflate(R.layout.device_ir_stb_content, null);
		for(int res : keyRes){
			final LinearLayout studySTBlayout = (LinearLayout)stbView.findViewById(res);
			childView = studySTBlayout.getChildAt(0);
			final String code = childView.getTag().toString();
			final DeviceIRInfo info = irGroupManager.getDeviceIRInfo(IRGroupManager.TYPE_STB, code);
			if(info != null && info.isStudy()){
				childView.setSelected(true);
			}
			studySTBlayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					studySTBlayoutClick(code);
				}
			});
			childView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					studySTBlayoutClick(code);
				}
			});
		}
		return stbView;
	}
	
	private void studySTBlayoutClick(String code){
		DeviceIRInfo info = irGroupManager.getDeviceIRInfo(IRGroupManager.TYPE_STB, code);
		//info为空就是map里面没有存相应的控制码及没有学习
		if(info == null){
			info = new DeviceIRInfo();
			info.setDeviceID(irGroupManager.getDevID());
			info.setGwID(irGroupManager.getGwID());
			info.setIRType(IRGroupManager.TYPE_STB);
			info.setEp(WulianDevice.EP_14);
			info.setName(code);
			info.setCode(code);
			info.setKeyset(code);
			irGroupManager.addIrInfo(info);
		}
		mDialogManager.showDialog(IRStudyFragment.KEY_PROCESS_DIALOG_IR_STUDY, context, context.getString(R.string.device_ir_study_key_remind_information), null);
		StringBuilder sb = new StringBuilder();
		sb.append(CmdUtil.IR_MODE_STUDY);
		sb.append(StringUtil.appendLeft(info.getCode(), 3, '0'));
		String sendData = sb.toString();
		SendMessage.sendControlDevMsg(
				irGroupManager.getGwID(), 
				irGroupManager.getDevID(),
				WulianDevice.EP_14,
				ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL,
				sendData);
	}
	public View createStudyAirView(int mode){
		LinearLayout airView = (LinearLayout) inflater.inflate(R.layout.device_ir_air_content, null);
		final GridView airGridview = (GridView) airView.findViewById(R.id.device_ir_air_gv);
		LinearLayout editLayout= (LinearLayout) airView.findViewById(R.id.device_ir_edit_button);
		editLayout.setVisibility(View.VISIBLE);
		final ImageView editImageView = (ImageView) airView.findViewById(R.id.devcie_ir_edit_add_iv);
		final ImageView cancelEditImageView = (ImageView) airView.findViewById(R.id.devcie_ir_cancel_edit_add_iv);
		
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem() {
			
			@Override
			public View getView() {
				View view = inflater.inflate(R.layout.device_ir_air_item, null);
				ImageView iconView = (ImageView)view.findViewById(R.id.device_ir_air_iv);
				iconView.setVisibility(View.VISIBLE);
				FrameLayout addStudyLayout = (FrameLayout) view.findViewById(R.id.device_ir_air_add_study_layout);
				addStudyLayout.setVisibility(View.GONE);
				return view;
			}
			
			@Override
			public void doSomething() {
				newDeviceIRAir();
				EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRAirAdapter.MODE_STUDY));
			}
		});
		final IRAirAdapter adapter = new IRAirAdapter(context, irGroupManager.getAriGroup().getAllKeys(),items);
		airGridview.setAdapter(adapter);
		adapter.setMode(mode);
		if(mode == IRAirAdapter.MODE_EDIT){
			cancelEditImageView.setVisibility(View.VISIBLE);
			editImageView.setVisibility(View.GONE);
			cancelEditImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRAirAdapter.MODE_STUDY));
				}
			});
		}else{
			cancelEditImageView.setVisibility(View.GONE);
			editImageView.setVisibility(View.VISIBLE);
			editImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRAirAdapter.MODE_EDIT));
				}
			});
		}
		return airView;
	}
	public View createStudyGeneralView(int mode){
		LinearLayout generalView = (LinearLayout)inflater.inflate(R.layout.device_ir_genenral_content, null);
		final GridView contentGridView = (GridView)generalView.findViewById(R.id.device_ir_general_gv);
		
		LinearLayout editLayout= (LinearLayout) generalView.findViewById(R.id.device_ir_edit_button);
		editLayout.setVisibility(View.VISIBLE);
		final ImageView editImageView = (ImageView) generalView.findViewById(R.id.devcie_ir_edit_add_iv);
		final ImageView cancelEditImageView = (ImageView) generalView.findViewById(R.id.devcie_ir_cancel_edit_add_iv);
		
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem() {
			
			@Override
			public View getView() {
				View view = inflater.inflate(R.layout.device_ir_general_item, null);
				TextView iconView = (TextView)view.findViewById(R.id.device_ir_general_iv);
				iconView.setVisibility(View.VISIBLE);
				TextView keyButton = (TextView)view.findViewById(R.id.device_ir_general_btn);
				keyButton.setVisibility(View.GONE);
				return view;
			}
			
			@Override
			public void doSomething() {
				newGeneralDeviceIR();
				EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRGeneralAdapter.MODE_STUDY));
			}
		});
		final IRGeneralAdapter adapter = new IRGeneralAdapter(context, irGroupManager.getGeneralGroupDeviceIrInfos(),items);
		contentGridView.setAdapter(adapter);
		adapter.setMode(mode);
		if(mode == IRGeneralAdapter.MODE_EDIT){
			cancelEditImageView.setVisibility(View.VISIBLE);
			editImageView.setVisibility(View.GONE);
			cancelEditImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRGeneralAdapter.MODE_STUDY));
				}
			});
		}else{
			cancelEditImageView.setVisibility(View.GONE);
			editImageView.setVisibility(View.VISIBLE);
			editImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRGeneralAdapter.MODE_EDIT));
				}
			});
		}
		return generalView;
	}
	
	public void newDeviceIRAir() {
		DeviceIRInfo newInfo = null;
		int nextCode = -1;
		for (int code = 0; code < 255; code++) {
			 DeviceIRInfo info = irGroupManager.getAriGroup().getDeviceIrInfo(StringUtil.appendLeft(code + "", 3, '0'));
			 if(info == null){
				 nextCode = code;
				 break;
			 }
		}
		if (nextCode != -1) {
			newInfo = new DeviceIRInfo();
			newInfo.setCode(StringUtil.appendLeft(nextCode + "", 3, '0'));
			newInfo.setDeviceID(irGroupManager.getDevID());
			newInfo.setGwID(irGroupManager.getGwID());
			newInfo.setIRType(IRGroupManager.TYPE_AIR_CONDITION);
			newInfo.setEp(WulianDevice.EP_14);
			newInfo.setKeyset(newInfo.getCode());
			newInfo.setStatus(DeviceIRInfo.STATUS_NO_STUDY);
			newInfo.setName(newInfo.getCode() + context.getString(R.string.device_ir_air_add_new_key));
			irGroupManager.getAriGroup().addDeviceIrInfo(newInfo);
		}
	}
	public void newGeneralDeviceIR() {
		DeviceIRInfo newInfo = null;
		int nextCode = 0;
		for (int code = 511; code < 610; code++) {
			 DeviceIRInfo info = irGroupManager.getGeneralDeviceIrInfo(code+"");
			 if(info == null){
				 nextCode = code;
				 break;
			 }
		}
		if (nextCode != 0) {
			newInfo = new DeviceIRInfo();
			newInfo.setCode(nextCode + "");
			newInfo.setDeviceID(irGroupManager.getDevID());
			newInfo.setGwID(irGroupManager.getGwID());
			newInfo.setIRType(IRGroupManager.TYPE_GENERAL);
			newInfo.setEp(WulianDevice.EP_14);
			newInfo.setKeyset(newInfo.getCode());
			newInfo.setStatus(DeviceIRInfo.STATUS_NO_STUDY);
			newInfo.setName(newInfo.getCode()  + context.getString(R.string.device_ir_air_add_new_key));
			irGroupManager.addGeneralDeviceIrInfo(newInfo);
		}
	}
	
	public class IRGeneralAdapter extends IRAirAdapter{

		public IRGeneralAdapter(Context context, List<DeviceIRInfo> data, List<MenuItem> items) {
			super(context, data, items);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_general_item, null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				final DeviceIRInfo item) {
			ImageView deleteItem = (ImageView) view.findViewById(R.id.device_ir_general_item_delete);
			TextView keyButton = (TextView)view.findViewById(R.id.device_ir_general_btn);
			keyButton.setText(item.getName());
			if(item.isStudy()){
//				if(epDataBuffer != null){
//					if(item.getCode().equals(epDataBuffer.substring(1))){
//						keyButton.setPressed(true);
//					}
//				}
				keyButton.setSelected(true);
//				if(item.getCode().equals(studyItemCode) && isShowEditDialog){
////					mDialogManager.dimissDialog(SEND_STUDY_KEY, 0);
//					showEditDeviceIrInfoDialog(item);
//					isShowEditDialog = false;
//				}
			}else{
				keyButton.setSelected(false);
			}
			if(mode == MODE_EDIT){
				deleteItem.setVisibility(View.VISIBLE);
				deleteItem.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						irGroupManager.removeGeneralDeviceIrInfo(item);
						EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRGeneralAdapter.MODE_EDIT));
						
					}
				});
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showEditDeviceIrGeneralInfoDialog(item,IRGeneralAdapter.MODE_EDIT);
					}
				});
			}else{
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						viewClick(item);
					}
				});
			}
		}
	}
	public class IRAirAdapter extends WLOperationAdapter<DeviceIRInfo>{

		public static final int MODE_CONTROL = 0;
		public static final int MODE_STUDY = 1;
		public static final int MODE_EDIT = 2;
		protected int mode = 0;
		public IRAirAdapter(Context context,List<DeviceIRInfo> data,List<MenuItem> items) {
			super(context, data, items);
		}
		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_air_item, null);
		}
		
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
		@Override
		protected void bindView(final Context context, View view, int pos,
				final DeviceIRInfo item) {
			ImageView deleteItem = (ImageView) view.findViewById(R.id.device_ir_air_item_delete);
			TextView keyButton = (TextView)view.findViewById(R.id.device_ir_air_btn);
			keyButton.setText(item.getName());
			if(item.isStudy()){
				keyButton.setSelected(true);
//				if(epDataBuffer != null){
//					if(item.getCode().equals(epDataBuffer.substring(1))){
//						keyButton.setPressed(true);
//					}
//				}
				
//				if(item.getCode().equals(studyItemCode) && isShowEditDialog){
//					mDialogManager.dimissDialog(SEND_STUDY_KEY, 0);
//					showEditDeviceIrInfoDialog(item);
//					isShowEditDialog = false;
//				}
			}else{
				keyButton.setSelected(false);
			}
			
			if(mode == MODE_EDIT){
				deleteItem.setVisibility(View.VISIBLE);
				deleteItem.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						irGroupManager.getAriGroup().removeDeviceIrInfo(item);
						EventBus.getDefault().post(new EditDeviceIRInfoEvent(IRAirAdapter.MODE_EDIT));
					}
				});
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showEditDeviceIrAirInfoDialog(item,IRAirAdapter.MODE_EDIT);
					}
				});
			}else{
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						viewClick(item);
					}
				});
			}
		}

//		/**
//		 * 联动控制码
//		 * @param isControl
//		 * @param mContext
//		 * @param item
//		 */
//		protected void viewClick(DeviceIRInfo item){
//
//			if(mode == MODE_CONTROL){
//				if(!item.isStudy()){
//					WLToast.showToast(mContext, mContext.getString(R.string.device_ir_no_study), WLToast.TOAST_SHORT);
//					return ;
//				}
//				
//				
//				StringBuilder sb = new StringBuilder();
//				sb.append(CmdUtil.IR_MODE_CTRL);
//				sb.append(item.getCode());
//				String sendData = sb.toString();
//				if(epDataBuffer != null){
//					if(item.getIRType().equals(IRGroupManager.TYPE_AIR_CONDITION)){
//						epDataBuffer.delete(0, epDataBuffer.length());
//						epDataBuffer.append(sendData);
//						contentView.removeAllViews();
//						createControlAirContentView(epDataBuffer);
//					}else if(item.getIRType().equals(IRGroupManager.TYPE_GENERAL)){
//						epDataBuffer.delete(0, epDataBuffer.length());
//						epDataBuffer.append(sendData);
//						contentView.removeAllViews();
//						createControlGeneralContentView(epDataBuffer);
//					}
//				}
//				else{
//					NetSDKProxy.sendControlDevMsg(
//							item.getGwID(), 
//							item.getDeviceID(),
//							item.getEp(),
//							ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL,
//							sendData);
//				}
//			}else{
//				StringBuilder sb = new StringBuilder();
//				sb.append(CmdUtil.IR_MODE_STUDY);
//				studyItemCode = item.getCode(); 
//				sb.append(studyItemCode);
//				String sendData = sb.toString();
//				mDialogManager.showDialog(IRStudyFragment.KEY_PROCESS_DIALOG_IR_STUDY, context, context.getString(R.string.device_ir_study_key_remind_information), null);
//				NetSDKProxy.sendControlDevMsg(item.getGwID(), item.getDeviceID(),
//						item.getEp(), ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL, sendData);
//			}
//		
//		}
//		
//	}
		/**
		 * view的点击效果，发送控制码或学习码
		 * @param isControl
		 * @param mContext
		 * @param item
		 */
		protected void viewClick(DeviceIRInfo item){

			if(mode == MODE_CONTROL){
				if(!item.isStudy()){
					WLToast.showToast(mContext, mContext.getString(R.string.device_ir_no_study), WLToast.TOAST_SHORT);
					return ;
				}
				
				
				StringBuilder sb = new StringBuilder();
				sb.append(CmdUtil.IR_MODE_CTRL);
				sb.append(item.getCode());
				String sendData = sb.toString();
				SendMessage.sendControlDevMsg(
						item.getGwID(), 
						item.getDeviceID(),
						item.getEp(),
						ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL,
						sendData);
			}else{
				StringBuilder sb = new StringBuilder();
				sb.append(CmdUtil.IR_MODE_STUDY);
				studyItemCode = item.getCode(); 
				sb.append(studyItemCode);
				String sendData = sb.toString();
				mDialogManager.showDialog(IRStudyFragment.KEY_PROCESS_DIALOG_IR_STUDY, context, context.getString(R.string.device_ir_study_key_remind_information), null);
				SendMessage.sendControlDevMsg(item.getGwID(), item.getDeviceID(),
						item.getEp(), ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL, sendData);
			}
		
		}
		
	}
	
	public void showEditDeviceIrGeneralInfoDialog(final DeviceIRInfo item,final int mode){
		View editContentView = View.inflate(context, R.layout.device_ir_edit_button_name, null);
		final EditText contentEditText= (EditText) editContentView.findViewById(R.id.device_ir_edit_butto_name);
		contentEditText.setHint(item.getName());
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setTitle(context.getString(R.string.device_ir_air_new_button_title))
		.setContentView(editContentView)
		 .setPositiveButton(android.R.string.ok)
		 .setNegativeButton(android.R.string.cancel)
		 .setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				String newName = contentEditText.getText().toString();
				if(!StringUtil.isNullOrEmpty(newName)){
					item.setName(newName);
					EventBus.getDefault().post(new EditDeviceIRInfoEvent(mode));
				}
				if (InputMethodUtils.isShow(context)) {
					InputMethodUtils.hide(context);
				}
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
				
			}
		});
		WLDialog dialog = builder.create();
		dialog.show();
	}
	
	public void showEditDeviceIrAirInfoDialog(final DeviceIRInfo item,final int mode){
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setTitle(context.getString(R.string.device_ir_air_new_button_title))
		.setContentView(createEditAirView(item,mode))
		 .setPositiveButton(android.R.string.ok)
		 .setNegativeButton(android.R.string.cancel)
		 .setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
//				String newName = contentEditText.getText().toString();
				if(!StringUtil.isNullOrEmpty(newName)){
					item.setName(newName);
					EventBus.getDefault().post(new EditDeviceIRInfoEvent(mode));
				}
				if (InputMethodUtils.isShow(context)) {
					InputMethodUtils.hide(context);
				}
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
				
			}
		});
		WLDialog dialog = builder.create();
		dialog.show();
	}
	
	private View createEditAirView(final DeviceIRInfo item,final int mode) {
		
		final ThermostatViewBuilder builder = new ThermostatViewBuilder(inflater.getContext());
		View editContentView = builder.getContentView();
		builder.initIRAirEditNewNameView();
		builder.irAirEditNewNameNoShowSwitch();
		builder.setCurModel(ThermostatViewBuilder.CUR_MODEL_HOT_0);
		builder.initCurModel();
		builder.setSwitchOpen(isOpen);
		builder.initSwitchStatus();
		isCtrlModeHot = true;
		isCtrlModeCool = false;
		isCtrlModeFan = false;
		newName = context.getString(R.string.device_ir_air_edit_new_name_hot);
		builder.setCurModelListener(new CurModelListener() {

			@Override
			public void onModelChanged(int model) {
				if (ThermostatViewBuilder.CUR_MODEL_HOT_0 == model) {
					isCtrlModeHot = true;
					isCtrlModeCool = false;
					isCtrlModeFan = false;
					newName = context.getString(R.string.device_ir_air_edit_new_name_hot);
					builder.closeWindSpeedBackground();
				} else if (ThermostatViewBuilder.CUR_MODEL_COOL_1 == model) {
					isCtrlModeHot = false;
					isCtrlModeCool = true;
					isCtrlModeFan = false;
					newName = context.getString(R.string.device_ac_cmd_refrigeration);
					builder.closeWindSpeedBackground();
				} else if (ThermostatViewBuilder.CUR_MODEL_FAN_2 == model) {
					isCtrlModeHot = false;
					isCtrlModeCool = false;
					isCtrlModeFan = true;
					newName = context.getString(R.string.device_ac_cmd_air_supply);
				}
				builder.setCurModel(model);
				builder.initCurModel();
			}
		});
		builder.setCurTempListener(new CurTempListener() {

			@Override
			public void onTempChanged(int temp) {
				if (isCtrlModeHot) {
					newName = context.getString(R.string.device_ir_air_edit_new_name_hot) + temp + "°C";
				} else if (isCtrlModeCool) {
					newName = context.getString(R.string.device_ac_cmd_refrigeration) + temp + "°C";
				}else if(isCtrlModeFan){
					newName = context.getString(R.string.device_ac_cmd_air_supply) + temp + "°C";
				}
//				builder.setCurTemp(mCurrentTempValue);
//				builder.setmTempSign(mTempSign);
//				builder.initCurIrAirTemp();
			}
		});
//		builder.setCurWindSpeedListener(new CurWindSpeedListener() {
//			@Override
//			public void onWindSpeedChanged(int speed) {
//				if(isCtrlModeFan){
//					if (ThermostatViewBuilder.WIND_SPEED_0 == speed) {
//						newName = "停风";
//					} else if (ThermostatViewBuilder.WIND_SPEED_1 == speed) {
//						newName = "小风";
//					} else if (ThermostatViewBuilder.WIND_SPEED_2 == speed) {
//						newName = "中风";
//					} else if (ThermostatViewBuilder.WIND_SPEED_3 == speed) {
//						newName = "大风";
//					} else if (ThermostatViewBuilder.WIND_SPEED_4 == speed) {
//						newName = "Auto";
//					}
//					builder.setCurWindSpeed(speed);
//					builder.initAirSpeedShow();
//					builder.initWindSpeed();
//				}
//			}
//		});
		builder.setCurSwitchListener(new CurSwitchListener() {
			
			@Override
			public void oSwitchChanged(boolean open) {
				if (open) {
					newName = context.getString(R.string.device_state_open);
				} else {
					newName = context.getString(R.string.device_state_close);
				}
				builder.setSwitchOpen(open);
				builder.initSwitchStatus();
			}
		});
		
		return editContentView;
	}
	
	/**
	 * 联动Adapter
	 * @author Administrator
	 *
	 */
	public class IRAirLinkAdapter extends WLOperationAdapter<DeviceIRInfo>{

		public static final int MODE_CONTROL = 0;
		public static final int MODE_STUDY = 1;
		public static final int MODE_EDIT = 2;
		protected int mode = 0;
		protected StringBuffer codeBuffer;
		public IRAirLinkAdapter(Context context,List<DeviceIRInfo> data,List<MenuItem> items,StringBuffer codeBuffer) {
			super(context, data, items);
			this.codeBuffer = codeBuffer;
		}
		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_air_item, null);
		}
		
		public int getMode() {
			return mode;
		}
		public void setMode(int mode) {
			this.mode = mode;
		}
		@Override
		protected void bindView(final Context context, View view, int pos,
				final DeviceIRInfo item) {
			TextView keyButton = (TextView)view.findViewById(R.id.device_ir_air_btn);
			keyButton.setText(item.getName());
			if(item.isStudy()){
				keyButton.setSelected(true);
				if(codeBuffer != null && codeBuffer.length() > 1){
					if(item.getCode().equals(codeBuffer.substring(1))){
						keyButton.setPressed(true);
					}
				}
			}
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mode == MODE_CONTROL){
						if(!item.isStudy()){
							WLToast.showToast(mContext, mContext.getString(R.string.device_ir_no_study), WLToast.TOAST_SHORT);
							return ;
						}
						StringBuilder sb = new StringBuilder();
						sb.append(CmdUtil.IR_MODE_CTRL);
						sb.append(StringUtil.appendLeft(item.getCode(), 3, '0'));
						String sendData = sb.toString();
						if(codeBuffer != null){
							codeBuffer.delete(0, codeBuffer.length());
							codeBuffer.append(sendData);
//							notifyDataSetChanged();
							contentView.removeAllViews();
							createLinkAirContentView(codeBuffer);
							if(ishouse){
								fireSelectIREpDataListener(codeBuffer);
							}
						}
					}
				}
			});
		}
	}
	/**
	 * 联动通用控制am
	 * @author Administrator
	 *
	 */
	public class IRGeneralLinkAdapter extends IRAirLinkAdapter{

		
		public IRGeneralLinkAdapter(Context context, List<DeviceIRInfo> data, List<MenuItem> items,StringBuffer codeBuffer) {
			super(context, data, items, codeBuffer);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_general_item, null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				final DeviceIRInfo item) {
			TextView keyButton = (TextView)view.findViewById(R.id.device_ir_general_btn);
			keyButton.setText(item.getName());
			
			
			
			if(item.isStudy()){
				keyButton.setSelected(true);
				if(codeBuffer != null && codeBuffer.length() > 1){
					if(item.getCode().equals(codeBuffer.substring(1))){
						keyButton.setPressed(true);
					}
				}
			}
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mode == MODE_CONTROL){
						if(!item.isStudy()){
							WLToast.showToast(mContext, mContext.getString(R.string.device_ir_no_study), WLToast.TOAST_SHORT);
							return ;
						}
						StringBuilder sb = new StringBuilder();
						sb.append(CmdUtil.IR_MODE_CTRL);
						sb.append(StringUtil.appendLeft(item.getCode(), 3, '0'));
						String sendData = sb.toString();
						if(codeBuffer != null){
							codeBuffer.delete(0, codeBuffer.length());
							codeBuffer.append(sendData);
							contentView.removeAllViews();
							createLinkGeneralContentView(codeBuffer);
							if(ishouse){
								fireSelectIREpDataListener(codeBuffer);
							}
//							notifyDataSetChanged();
						}
					}
				}
			});
		}
	}
	private void fireSelectIREpDataListener(StringBuffer epData){
		if(selectIREpDataListener != null){
			selectIREpDataListener.onSelectIREpData(epData);
		}
	}
	
	public void setSelectEpDataListener(SelectIREpDataListener selectIREpDataListener) {
		this.selectIREpDataListener = selectIREpDataListener;
	}
	
	public interface SelectIREpDataListener{
		public void onSelectIREpData(StringBuffer epData);
	}
}
