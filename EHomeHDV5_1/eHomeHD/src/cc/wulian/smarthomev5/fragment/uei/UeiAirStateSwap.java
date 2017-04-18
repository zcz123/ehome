package cc.wulian.smarthomev5.fragment.uei;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.entity.uei.AirStateValues;
import com.uei.control.AirConDefines;
import com.uei.control.AirConDevice;
import com.uei.control.AirConFunction;
import com.uei.control.AirConState;
import com.uei.control.AirConWidgetStatus;
import com.uei.control.acstates.StateDataTypes;
import com.uei.control.acstates.WidgetTypes;

/**
 * 为空调状态转换提供方法</br>
 * 把第三方给出的状态转换为当前的标准状态</br>
 * 第三方状态类：com.uei.control.AirConState</br>
 * 当前标准状态：UEIEntity_Air.AirStatus</br>
 * <p><p>该类的编写参考以下资料：</br>
 * 《UEI Android Services SDK - AIRCON Support.pdf》中的3.2.4</br>
 * ACEService.ACGetKeys接口返回的结果</br>
 * "开发协议-MK001-设备参数说明"</br>
 * @author yuxiaoxuan
 * @date 2016年7月21日16:39:14
 */
public class UeiAirStateSwap {
	private static String log_tag="airState";
	AirConState[] states;
	AirConDevice airConDevice;
	
	public UeiAirStateSwap(AirConDevice airConDevice,AirConState[] states){
		this.airConDevice=airConDevice;
		this.states=states;
	}
	
	public AirStateStandard ConvertToLocalState(){
		AirStateStandard localStatus=null;
		if(this.states!=null&&this.states.length>0&&this.airConDevice!=null){
			//默认是：关闭26摄氏度风量自动，上下扫风自动、左右扫风都自动；
			localStatus=new AirStateStandard("");
			localStatus.InitNoneValue();
//			boolean powerisOn=true;
			int powerValue=-1;
			for(AirConState state : states){				
				AirConFunction function = airConDevice.getFunctionById(state.Id);
				Log.d("airallstates", " ### State: "  + state.Id + " - " + state.Enabled + String.format("-> %s = %s", function.Name, AirConDefines.getStateDisplay(function.getFunctionType(), state)));

				if(function!=null){
					int stateid=state.Id;
//					boolean enabled=state.Enabled;
					String funName=function.Name;
					String value=AirConDefines.getStateDisplay(function.getFunctionType(), state);
//					Log.d(log_tag, " ### State: "  + state.Id + " - " + state.Enabled + String.format("-> %s = %s", function.Name, AirConDefines.getStateDisplay(function.getFunctionType(), state)));
						
					if(state.Enabled){
						/*注意这里：不能以state.Id找相应的值；因为每个空调是不一样的。
						 * 比如同样是power键，有些空调state.id是111，有些是103.
						 * 所以只能用名字进行匹配
						 * */
						int refstateid=AirStateValues.getRefKeyByName(funName);
						int standardValue=AirStateValues.getStandardValue(refstateid, value);
						if(refstateid==-1||standardValue==-1){
							Log.d(log_tag, "空调状态："+stateid+" "+funName+" value="+value+" 未找到对应的标准值！");
						}
						switch (refstateid) {
						case AirStateValues.power:
//							powerisOn=standardValue==0?false:true;
							powerValue=standardValue;
							break;
						case AirStateValues.mode:
							//之所以加上这个判断是因为有时候一个码库中会有两个mode，只取第一个
							if(localStatus.getMode()==-1){
								localStatus.setMode(standardValue);
							}
							break;
						case AirStateValues.temp:
							if(standardValue==-1&&!StringUtil.isNullOrEmpty(value)){
								int tempValue=-1;
								try {
									tempValue=Integer.parseInt(value);
								}catch(Exception ex){
									Log.d(log_tag, "空调状态："+stateid+" "+funName+" value="+value+" 温度不能转换为数字");
									tempValue=-1;
								}
								localStatus.setTemperature(tempValue);

							}
							break;
						case AirStateValues.speed:
							localStatus.setFanspeed(standardValue);
							break;
						case AirStateValues.swingH:
							localStatus.setSwing_left_right(standardValue);
							break;
						case AirStateValues.swingV:
							localStatus.setSwing_up_down(standardValue);
							break;
						default:
							break;
						}
						//判断温度单位，这个比较特殊,只有温度的数据类型是这样的
						if(state.StateDataType == StateDataTypes.TEMPERATURE_CELSIUS) {
							localStatus.setTemperature_unit(AirStateStandard.tempUnitC);
						} else if(state.StateDataType ==  StateDataTypes.TEMPERATURE_FAHRENHEIT) {
							localStatus.setTemperature_unit(AirStateStandard.tempUnitF);
						}
					}
				}
			}
			if(powerValue==0){
				localStatus.setMode(0);
			}else{
				//若没有Mode字段，则使用Power字段
				if(localStatus.getMode()==-1&&powerValue>1){
					localStatus.setMode(powerValue);
				}
			}
		}
		return localStatus;
	}
	public Map<String,Integer> GetBtnTagsValue(){
		Map<String,Integer> maptag=new HashMap<>();
		if(this.airConDevice!=null){
			for(Object funct :airConDevice.AirConFunctions){
				AirConFunction acFunct = (AirConFunction)funct;
				int funid=acFunct.Id;
				String funName=acFunct.Name;
				Log.d(log_tag, "funid="+funid+" funname="+funName+" funtype="+acFunct.getWidgetType());
				if(acFunct!=null&&acFunct.getWidgetType() == WidgetTypes.BUTTON){
					int refstateid=AirStateValues.getRefKeyByName(funName);
					switch (refstateid) {
					case AirStateValues.power:
						maptag.put("4", funid);
						break;
					case AirStateValues.mode:
						maptag.put("1", funid);
						break;
					case AirStateValues.speed:
						maptag.put("5", funid);
						break;
					case AirStateValues.swingH:
						maptag.put("7", funid);
						break;
					case AirStateValues.swingV:
						maptag.put("6", funid);
						break;
					case AirStateValues.tempUp:
						maptag.put("2", funid);
						break;
					case AirStateValues.tempDown:
						maptag.put("3", funid);
						break;
					default:
						break;
					}
				}
			}
		}
		return maptag;
	}
}
