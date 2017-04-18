package cc.wulian.app.model.device.impls.controlable.light;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.app.model.device.view.ColorPickerView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;

/**
 * 返回数据(十六进制)
 * 	1.AA55081AXXRRGGBB，用户敲击设备时上报，其中XX表示模式，取值为00-06，当XX为01时，RRGGBB取值才有效
 *	2.AA55088501RRGGBB，当用户在App上设置单色模式时上报
 *	3.AA550686XX00，当用户设置非单色模式时上报
 *  发送数据(十六进制)
 *  绿色心情：AA55050605
 *  蓝色小夜曲：AA55050604
 *  月色迷人：AA55050603
 *  烛光晚餐：AA55050602
 *  关闭：AA55050600
 *  单色模式：AA55090510RRGGBB00，RR、GG、BB分别表示红绿蓝取值，范围00-FF
 *  调节亮度: AA550621XX00 
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_LIGHT }, category = Category.C_LIGHT)
public class WL_D7_Light extends ControlableDeviceImpl implements OnClickListener{
	
	private String DATA_CTRL_STATE_CLOSE_0 = "AA55050600";	
	private String TIP_LIGHT_SINGLE="AA55090510%s00"; //单色模式        需要格式化
	private String TIP_LIGHT_DINNER = "AA55050602"; 	//烛光晚餐
	private String TIP_LIGHT_MOONLIGHT = "AA55050603";	//月光迷人
	private String TIP_LIGHT_SERENADE = "AA55050604";	//蓝调小夜曲
	private String TIP_LIGHT_MOOD = "AA55050605";		//绿色心情
	private String TIP_LIGHT_LIGHT="AA550621%s00";     //亮度调节
	
	private String cmd="00",closeCmd="00",singleColorCmd="01";
	private String lightNumStr="32"; //亮度值
	
	private int SMALL_OPEN_D = R.drawable.device_light_led_auto;
	private int SMALL_CLOSE_D = R.drawable.device_light_led_comman;
	
	protected LinearLayout mLightLayout;
	
    private ColorPickerView colorPickerView;
    private RelativeLayout coloritem;
	private CheckedTextView tipLightDinner,tipLightMoonLight,tipLightSerenade,tipLightMood;
	private ImageButton onBtn,offBtn,minLightBtn,maxLightBtn;
    private SeekBar lightSeekBar;
	
    private String colorTxt="ff0025";
    
    private boolean isOpen=false;

	public WL_D7_Light(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenProtocol() {
		return 	String.format(TIP_LIGHT_SINGLE, colorTxt);
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_STATE_CLOSE_0;
	}

	@Override
	public String getOpenSendCmd() {
		return getOpenProtocol();
	}

	@Override
	public String getCloseSendCmd() {
		return getCloseProtocol();
	}

	public int getOpenSmallIcon() {
		return SMALL_OPEN_D;
	}

	public int getCloseSmallIcon() {
		return SMALL_CLOSE_D;
	}
	
	@Override
	public boolean isOpened() {
		return isOpen;
	}

	@Override
	public boolean isClosed() {
		return !isOpened();
	}

	@Override
	public Drawable getStateSmallIcon() {
		Drawable icon = null;
		if (isOpened() || isStoped()) {
			icon = getDrawable(SMALL_OPEN_D);
		}
		else if (isClosed()) {
			icon = getDrawable(SMALL_CLOSE_D);
		}
		else {
			icon =getDefaultStateSmallIcon();
		}
		return icon;	
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = "";
		int color = COLOR_NORMAL_ORANGE;

		if (isOpened()) {
			state = getString(R.string.device_state_open);
			color = COLOR_CONTROL_GREEN;
		} else if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
				getColor(color)));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle saveState) {
		mLightLayout = (LinearLayout) inflater.inflate(R.layout.device_light_d7, container, false);
		return mLightLayout;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		initWidget(view);
	}

	private void addColorView(){
		WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int width = (int)(metrics.widthPixels/2);
		int height = (int)(metrics.heightPixels/2);
		if(width > height)width = height;
		colorPickerView = new ColorPickerView(mContext,width, width, Color.parseColor("#"+colorTxt), new ColorPickerView.OnColorChangedListener()
		{
			@Override
			public void colorChanged( String color ) {	
				if (color == null || color.length() < 8) return;
				colorTxt = (color).substring(2,color.length());	
				sendControlDeviceMsg(String.format(TIP_LIGHT_SINGLE, colorTxt));
			}
		});
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		colorPickerView.setLayoutParams(params);
		coloritem.addView(colorPickerView);
	}
	
	private void initWidget(View view){
		coloritem = (RelativeLayout) view.findViewById(R.id.flower_light_color);
		addColorView();
		tipLightDinner=(CheckedTextView) view.findViewById(R.id.tip_light_dinner);
		tipLightMoonLight=(CheckedTextView) view.findViewById(R.id.tip_light_moonlight);
		tipLightMood=(CheckedTextView) view.findViewById(R.id.tip_light_mood);
		tipLightSerenade=(CheckedTextView) view.findViewById(R.id.tip_light_serenade);
		lightSeekBar=(SeekBar) view.findViewById(R.id.device_light_d7_seekBar);
		
		onBtn=(ImageButton) view.findViewById(R.id.tip_light_open_btn);
		offBtn=(ImageButton) view.findViewById(R.id.tip_light_close_btn);
		minLightBtn=(ImageButton) view.findViewById(R.id.device_light_d7_min_light);
		maxLightBtn=(ImageButton) view.findViewById(R.id.device_light_d7_max_light);
		
		tipLightDinner.setOnClickListener(this);
		tipLightMoonLight.setOnClickListener(this);
		tipLightSerenade.setOnClickListener(this);
		tipLightMood.setOnClickListener(this);
		onBtn.setOnClickListener(this);
		offBtn.setOnClickListener(this);
		minLightBtn.setOnClickListener(this);
		maxLightBtn.setOnClickListener(this);
		lightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				sendControlDeviceMsg(String.format(TIP_LIGHT_LIGHT, StringUtil.appendLeft(arg0.getProgress()+"", 2, '0')));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			}
		});
	}
	
	@Override
	public void onClick(View view) {
		colorPickerView.setColor(Color.parseColor("#000000"));
		switch(view.getId()){
		case R.id.tip_light_dinner:  //灯效   烛光晚餐
			sendControlDeviceMsg(TIP_LIGHT_DINNER);
			break;
		case R.id.tip_light_moonlight:	//灯效  月光迷人
			sendControlDeviceMsg(TIP_LIGHT_MOONLIGHT);		
			break;
		case R.id.tip_light_mood:	//灯效  绿色心情 
			sendControlDeviceMsg(TIP_LIGHT_MOOD);
			break;
		case R.id.tip_light_serenade:	//灯效 蓝调小夜曲
			sendControlDeviceMsg(TIP_LIGHT_SERENADE);
			break;
		case R.id.tip_light_open_btn:	//on
			sendControlDeviceMsg(String.format(TIP_LIGHT_SINGLE, colorTxt));
			break;
		case R.id.tip_light_close_btn:	//off
			sendControlDeviceMsg(DATA_CTRL_STATE_CLOSE_0);
			break;
		case R.id.device_light_d7_min_light:	//min
			lightSeekBar.setProgress(0);
			sendControlDeviceMsg(String.format(TIP_LIGHT_LIGHT, "00"));
			break;
		case R.id.device_light_d7_max_light:	//max
			lightSeekBar.setProgress(64);
			sendControlDeviceMsg(String.format(TIP_LIGHT_LIGHT, "64"));
			break;
		}		
	}

	private void sendControlDeviceMsg(String msg){
		fireWulianDeviceRequestControlSelf();
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),msg);
	}
	
	private void setViewEnable(View view,boolean isEnable){
		CheckedTextView tipLightDinner=(CheckedTextView) view.findViewById(R.id.tip_light_dinner);
		CheckedTextView tipLightMoonLight=(CheckedTextView) view.findViewById(R.id.tip_light_moonlight);
		CheckedTextView tipLightMood=(CheckedTextView) view.findViewById(R.id.tip_light_mood);
		CheckedTextView tipLightSerenade=(CheckedTextView) view.findViewById(R.id.tip_light_serenade);
		tipLightDinner.setSelected(isEnable);
		tipLightMoonLight.setSelected(isEnable);
		tipLightSerenade.setSelected(isEnable);
		tipLightMood.setSelected(isEnable);
	}
	
	private void changeViewCheck(View rootView,boolean isChecked,CheckedTextView view){
		changeViewsCheck(rootView,false);
		if(view!=null)view.setChecked(isChecked);
	}
	
	private void changeViewsCheck(View view,boolean isChecked){
		CheckedTextView tipLightDinner=(CheckedTextView) view.findViewById(R.id.tip_light_dinner);
		CheckedTextView tipLightMoonLight=(CheckedTextView) view.findViewById(R.id.tip_light_moonlight);
		CheckedTextView tipLightMood=(CheckedTextView) view.findViewById(R.id.tip_light_mood);
		CheckedTextView tipLightSerenade=(CheckedTextView) view.findViewById(R.id.tip_light_serenade);
		tipLightDinner.setChecked(isChecked);
		tipLightMoonLight.setChecked(isChecked);
		tipLightSerenade.setChecked(isChecked);
		tipLightMood.setChecked(isChecked);		
	}
	
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		setListClickData();
	}
	
	public void setListClickData(){		
		if(isNull(epData)||epData.length()<11)return;
		cmd=epData.substring(8,10);
		lightNumStr=epData.substring(epData.length()-2, epData.length());
		System.out.println(epData+"  cmd "+cmd);
		isOpen=true;
		if(closeCmd.equals(cmd)){
			isOpen=false;
		}
		if(singleColorCmd.equals(cmd)){
			colorTxt=epData.substring(10,16);
			System.out.println(colorTxt);
		}		
	}
	
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		showView();		
	}

	/**
	 * 添加界面处理
	 */
	public void showView() {
		setViewEnable(mLightLayout,true);
		changeViewCheck(mLightLayout,false,null);		
		switch(StringUtil.toInteger(cmd)){
		case 0:			
			setViewEnable(mLightLayout,false);
			break;
		case 1:
			colorPickerView.setColor(Color.parseColor("#"+colorTxt));
			break;
		case 2:			    //灯效   烛光晚餐
			changeViewCheck(mLightLayout,true,tipLightDinner); 
			break;
		case 3:				//灯效  月光迷人
			changeViewCheck(mLightLayout,true,tipLightMoonLight);
			break;
		case 4:				//灯效 蓝调小夜曲
			changeViewCheck(mLightLayout,true,tipLightSerenade);
			break;
		case 5:				//灯效  绿色心情 
			changeViewCheck(mLightLayout,true,tipLightMood);
			break;
		}
		offBtn.setVisibility((isOpen ? View.VISIBLE:View.GONE ));
		onBtn.setVisibility((isOpen ? View.GONE:View.VISIBLE ));
		try{
			lightSeekBar.setProgress(Integer.parseInt(lightNumStr));
		}catch(Exception e){
			e.printStackTrace();
			lightNumStr="32";
			lightSeekBar.setProgress(32);
		}
	}
	
   /**
    * 设置dialog
    */
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,   final AutoActionInfo autoActionInfo) {
		
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		String data=autoActionInfo.getEpData();			
		final View view=(LinearLayout) inflater.inflate(R.layout.device_light_d7, null);
	    RelativeLayout coloritem = (RelativeLayout) view.findViewById(R.id.flower_light_color);	    			
		final CheckedTextView tipLightDinner=(CheckedTextView) view.findViewById(R.id.tip_light_dinner);
		final CheckedTextView tipLightMoonLight=(CheckedTextView) view.findViewById(R.id.tip_light_moonlight);
		final CheckedTextView tipLightMood=(CheckedTextView) view.findViewById(R.id.tip_light_mood);
		final CheckedTextView tipLightSerenade=(CheckedTextView) view.findViewById(R.id.tip_light_serenade);
	//	ImageButton onBtn=(ImageButton) view.findViewById(R.id.tip_light_open_btn);
		ImageButton offBtn=(ImageButton) view.findViewById(R.id.tip_light_close_btn);
		LinearLayout  lightSelectLayout=(LinearLayout) view.findViewById(R.id.light_select_ll);
		lightSelectLayout.setVisibility(View.GONE);
	    WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int width = (int)(metrics.widthPixels/2);
		int height = (int)(metrics.heightPixels/2);
		if(width > height)width = height;
		final ColorPickerView colorPickerView = new ColorPickerView(mContext,width, width, Color.parseColor("#000000"), new ColorPickerView.OnColorChangedListener()
		{
			@Override
			public void colorChanged( String color ) {	
				if (color == null || color.length() < 8) return;
				changeViewCheck(view,false,null);
				autoActionInfo.setEpData(String.format(TIP_LIGHT_SINGLE, color.substring(2,color.length())));
			}
		});
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		colorPickerView.setLayoutParams(params);
		coloritem.addView(colorPickerView);
		
		tipLightDinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setViewEnable(view,true);
				changeViewCheck(view,true,tipLightDinner);
				colorPickerView.setColor(Color.parseColor("#000000"));
				autoActionInfo.setEpData(TIP_LIGHT_DINNER);
			}
		});
		tipLightMoonLight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setViewEnable(view,true);
				changeViewCheck(view,true,tipLightMoonLight);
				colorPickerView.setColor(Color.parseColor("#000000"));
				autoActionInfo.setEpData(TIP_LIGHT_MOONLIGHT);
			}
		});
		tipLightMood.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setViewEnable(view,true);
				changeViewCheck(view,true,tipLightMood);
				colorPickerView.setColor(Color.parseColor("#000000"));
				autoActionInfo.setEpData(TIP_LIGHT_MOOD);
			}
		});
		tipLightSerenade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setViewEnable(view,true);
				changeViewCheck(view,true,tipLightSerenade);
				colorPickerView.setColor(Color.parseColor("#000000"));
				autoActionInfo.setEpData(TIP_LIGHT_SERENADE);
			}
		});
//		onBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				setViewEnable(view,true);
//				changeViewCheck(view,false,null);
//				autoActionInfo.setEpData(String.format(TIP_LIGHT_SINGLE, colorTxt));
//			}
//		});
		offBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setViewEnable(view,false);
				changeViewCheck(view,false,null);
				colorPickerView.setColor(Color.parseColor("#000000"));
				autoActionInfo.setEpData(DATA_CTRL_STATE_CLOSE_0);
			}
		});
				
		if(!isNull(data)){
			setViewEnable(view,true);
			changeViewCheck(view,false,null);
			colorPickerView.setColor(Color.parseColor("#000000"));
			switch(StringUtil.toInteger(data.substring(8,10))){
			case 0:		
				setViewEnable(view,false);
				break;
			case 10:
			case 1:
				colorPickerView.setColor(Color.parseColor("#"+data.substring(10,16)));	
				break;
			case 2:
				changeViewCheck(view,true,tipLightDinner);
				break;
			case 3:
				changeViewCheck(view,true,tipLightMoonLight);
				break;
			case 4:
				changeViewCheck(view,true,tipLightSerenade);
				break;
			case 5:
				changeViewCheck(view,true,tipLightMood);
				break;
			}		
		}
		offBtn.setVisibility(View.VISIBLE);
	//	onBtn.setVisibility(View.VISIBLE );
		
		holder.setShowDialog(false);
		holder.setContentView(view);
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

}