package cc.wulian.smarthomev5.entity.uei;

import java.text.MessageFormat;

import com.alibaba.fastjson.JSONObject;

import cc.wulian.ihome.wan.util.StringUtil;
import android.util.Log;

/**
 * App中使用到的空调标准状态</br>
 * 来源："开发协议-MK001-设备参数说明"
 * @author yuxiaoxuan
 * @date 2016年7月22日08:40:55
 */
public class AirStateStandard extends UeiVirtualBtn {
	public static final String tempUnitC="℃";
	public static final String tempUnitF="℉";
	/**
	 * 默认状态</br>
	 * 关闭,26摄氏度，风量自动，上下扫风自动、左右扫风都自动；
	 */
	public static final String defaultState="0-26-℃-0-0-0";
	/**
	 * 初始化标准状态
	 * @param status 空调状态格式</br>
		模式-温度-温度单位-风量-左右扫风-上下扫风</br>
		模式:0关-1自动-2制冷-3制热-4抽湿-5换气-6舒适；</br>
		温度:两位数字；</br>
		温度单位:C摄氏度，F华氏度；</br>
		风量:0-自动，1-低，2-中，3-高，4安静，5自然风；</br>
		左右扫风:0-自动，1-左2，2-左1，3-中，4-右1，5-右2；</br>
		上下扫风:0-自动，1-下2，2-下1，3-中，4-上1，5-上2;</br>
		<p>比如：“2-25-C-2-1-4”，就表示“制冷25摄氏度中风左2上1”</br>
	 * -----------------------------------------*/
	public AirStateStandard(String status){
		analyzeStatus(status);
	}
	//初始化为无效值
	public  void InitNoneValue(){
		this.mode=-1;
		this.temperature=-1;
		this.temperature_unit=tempUnitC;
		this.fanspeed=-1;
		this.swing_left_right=-1;
		this.swing_up_down=-1;
	}
	private void analyzeStatus(String status){
		this.status=status;
		if(!StringUtil.isNullOrEmpty(status)){
			String [] statusArr=status.split("-",-1);//第二个参数-1表示不忽略任何一个分隔符
			if(statusArr.length==6){
				if(!StringUtil.isNullOrEmpty(statusArr[0])){
					this.mode=Integer.parseInt(statusArr[0]);
				}
				if(!StringUtil.isNullOrEmpty(statusArr[1])){
					this.temperature=Integer.parseInt(statusArr[1]);
				}
				this.temperature_unit=statusArr[2];
				if(!StringUtil.isNullOrEmpty(statusArr[3]))
				{this.fanspeed=Integer.parseInt(statusArr[3]);}
				if(!StringUtil.isNullOrEmpty(statusArr[4])){
					this.swing_left_right=Integer.parseInt(statusArr[4]);
				}
				if(!StringUtil.isNullOrEmpty(statusArr[5])){
					this.swing_up_down=Integer.parseInt(statusArr[5]);
				}
			}else{
				Log.e("airState", "空调标准状不规范！status="+status);
			}
		}
		
	}
	private String index;
	private String status;
	private int mode=-1;
	private int temperature=-1;
	private String temperature_unit="";
	private int fanspeed=-1;
	private int swing_left_right=-1;
	private int swing_up_down=-1;
	private String sendData;
	private String customName="";
	public int getMode() {
		return mode;
	}
	public int getTemperature() {
		return temperature;
	}
	public String getTemperature_unit() {
		return temperature_unit;
	}
	public int getFanspeed() {
		return fanspeed;
	}
	public int getSwing_left_right() {
		return swing_left_right;
	}
	public int getSwing_up_down() {
		return swing_up_down;
	}
	public String getStatus(){
		status=MessageFormat.format("{0}-{1}-{2}-{3}-{4}-{5}",
				this.mode>=0?this.mode:"",
				this.temperature>=0?this.temperature:"",
				this.temperature_unit,
				this.fanspeed>=0?this.fanspeed:"",
				this.swing_left_right>=0?this.swing_left_right:"",
				this.swing_up_down>=0?this.swing_up_down:"");
		return status;
	}
	public JSONObject getJsonItem(){
		JSONObject jsonitem=new JSONObject();
		jsonitem.put("ac", this.getIndex());
		jsonitem.put("s", getStatus());
		jsonitem.put("nm", this.getCustomName());
		return jsonitem;
	}
	
	
	
	public void setStatus(String status) {
		if(!this.status.equals(status)){
			analyzeStatus(status);
		}		
		this.status = status;
	}
	public void setMode(int mode) {
		this.mode=mode>=0?mode:0;
	}
	public void setTemperature(int temperature) {
		if(temperature>0){
			this.temperature = temperature>=10?temperature:10;
		}else {
			this.temperature=-1;
		}

	}
	public void setTemperature_unit(String temperature_unit) {
		this.temperature_unit = temperature_unit;
	}
	public void setFanspeed(int fanspeed) {
		this.fanspeed = fanspeed>=0?fanspeed:0;
	}
	public void setSwing_left_right(int swing_left_right) {
		this.swing_left_right = swing_left_right>=0?swing_left_right:0;
	}
	public void setSwing_up_down(int swing_up_down) {
		this.swing_up_down = swing_up_down>=0?swing_up_down:0;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getSendData() {
		return sendData;
	}
	public void setSendData(String sendData) {
		this.sendData = sendData;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	
}
