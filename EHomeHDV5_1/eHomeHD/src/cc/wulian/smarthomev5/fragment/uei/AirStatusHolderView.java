package cc.wulian.smarthomev5.fragment.uei;

import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.entity.uei.UEIEntity_Air;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 空调各个模式通用的控制逻辑
 * 
 * @author yuxiaoxuan
 *
 */
public class AirStatusHolderView {
	private Context context;
	public AirStatusHolderView(Context context){
		this.context=context;
	}
	LinearLayout modecommlayout;
	ImageButton modecommimage;
	TextView modecommName1;
	TextView modecommName2;
	
	LinearLayout modecomplexlayout;
	ImageButton windspeedImage;
	ImageButton airmodeimage;
	TextView temperaturetv;
	TextView temperatureunittv;
	ImageButton wind_left_rightimage;
	ImageButton wind_up_downimage;
	TextView customernametv;
	ImageButton airspeedflagimage;
	View line_top;
	View line_buttom;
	View line_left;
	View line_right;
	/**
	 * 自定义名称是否覆盖其它视图
	 */
	private boolean isShowAll=false;
	
	public boolean getIsShowAll() {
		return isShowAll;
	}

	public void setIsShowAll(boolean isShowAll) {
		this.isShowAll = isShowAll;
	}
	Object tag;
	
	public void FillData(AirStateStandard airStatus){
		boolean isCommMode=airStatus.getMode()==0
				||airStatus.getMode()==4
				||airStatus.getMode()==5
				||airStatus.getMode()==6;
		int imageflag=WL_23_IR_Resource.getAirModeImage("mode", airStatus.getMode());
		if(isCommMode){
			this.modecommlayout.setVisibility(View.VISIBLE);
			this.modecomplexlayout.setVisibility(View.GONE);
			String modecommName1="";
			String modecommName2="";
			switch (airStatus.getMode()) {
			case 0:
				modecommName1="电源";
				modecommName2="power";
				break;
			case 4:
				modecommName1="除湿";
				modecommName2="dehumidify";
				break;
			case 5:
				modecommName1="换气";
				modecommName2="recycle";
				break;
			case 6:
				modecommName1="舒适";
				modecommName2="comfort";
				break;
			default:
				imageflag=-1;
				break;
			}	
			if(imageflag>0){
				this.modecommimage.setImageDrawable(this.context.getResources().getDrawable(imageflag));//模式
			}
			this.modecommName1.setText(modecommName1);
			this.modecommName2.setText(modecommName2);
			
		}else{
			this.modecommlayout.setVisibility(View.GONE);
			this.modecomplexlayout.setVisibility(View.VISIBLE);
			if(this.customernametv!=null){
				this.customernametv.setText(airStatus.getCustomName());
			}
			if(imageflag>0){
				this.airmodeimage.setImageDrawable(this.context.getResources().getDrawable(imageflag));//模式
			}
			if(airStatus.getTemperature()>=0){
				this.temperaturetv.setText(airStatus.getTemperature()+"");
				this.temperatureunittv.setText(airStatus.getTemperature_unit());
			}else{
				this.temperaturetv.setText("——");
				this.temperatureunittv.setText("——");				
			}
//			if(airStatus.getTemperature_unit().equals("C")){
//				this.temperatureunittv.setText("℃");
//			}else if(airStatus.getTemperature_unit().equals("F")){
//				this.temperatureunittv.setText("℉");
//			}
			//风量
			imageflag=WL_23_IR_Resource.getAirModeImage("wind", airStatus.getFanspeed());
			if(imageflag>0){
				this.windspeedImage.setImageDrawable(this.context.getResources().getDrawable(imageflag));
			}
			//左右扫风
			imageflag=WL_23_IR_Resource.getAirModeImage("windLR", airStatus.getSwing_left_right());
			if(imageflag>0){
				this.wind_left_rightimage.setImageDrawable(this.context.getResources().getDrawable(imageflag));
			}
			//上下扫风
			imageflag=WL_23_IR_Resource.getAirModeImage("windUP", airStatus.getSwing_up_down());
			if(imageflag>0){
			this.wind_up_downimage.setImageDrawable(this.context.getResources().getDrawable(imageflag));}

			if(this.isShowAll){
				if(this.customernametv!=null){
					this.customernametv.setVisibility(View.VISIBLE);
				}
				this.temperaturetv.setVisibility(View.VISIBLE);
				this.temperatureunittv.setVisibility(View.VISIBLE);
				this.windspeedImage.setVisibility(View.VISIBLE);
				this.wind_left_rightimage.setVisibility(View.VISIBLE);
				this.wind_up_downimage.setVisibility(View.VISIBLE);
			}else{
				if(!StringUtil.isNullOrEmpty(airStatus.getCustomName())){
					if(this.customernametv!=null){
						this.customernametv.setVisibility(View.VISIBLE);
					}
					this.temperaturetv.setVisibility(View.GONE);
					this.temperatureunittv.setVisibility(View.GONE);
					this.windspeedImage.setVisibility(View.GONE);
					this.wind_left_rightimage.setVisibility(View.GONE);
					this.wind_up_downimage.setVisibility(View.GONE);
					this.airspeedflagimage.setVisibility(View.GONE);
				}else{				
					//温度及温度单位
					if(this.customernametv!=null){
						this.customernametv.setVisibility(View.GONE);
					}
					this.temperaturetv.setVisibility(View.VISIBLE);
					this.temperatureunittv.setVisibility(View.VISIBLE);
					this.windspeedImage.setVisibility(View.VISIBLE);
					this.wind_left_rightimage.setVisibility(View.VISIBLE);
					this.wind_up_downimage.setVisibility(View.VISIBLE);
					this.airspeedflagimage.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	public void FillData(String status){
		AirStateStandard airStatus=new AirStateStandard(status);
		if(airStatus!=null){
			FillData(status);
		}
	}
}
