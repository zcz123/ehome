package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * 空调状态值</br> 参考资料：</br> ACEService.ACGetKeys接口返回的结果</br> 《UEI Android Services
 * SDK - AIRCON Support.pdf》中的3.2.4</br>
 * 因状态ID每个空调可能不一样，所以这里的ID只作为内部使用
 * 
 * @author yuxiaoxuan
 * @date 2016年7月22日08:55:28
 * 
 */
@SuppressLint("UseSparseArrays")
public class AirStateValues {
	
	/**
	 * 状态ID及名称，这里的名称是保存到网关中的名称
	 */
	private static Map<Integer,String> statekeynamemap=null;
	/**
	 * 状态值列表
	 */
	private static List<AirStateValues> airStateValuesList=null;
	/**
	 * 状态ID及参考名称对照。多个名称用逗号分隔
	 */
	private static Map<Integer,String> keynamerefmap=null;
	/**
	 * 电源是否开启
	 */
	public static final int power = 103;
	/**
	 * 模式
	 */
	public static final int mode = 102;
	/**
	 * 温度
	 */
	public static final int temp = 118;
	/**
	 * 温度单位</br> 该项暂时未知，是通过StateDataType来判断的
	 */
	public static final int tempUnit = -1;
	/**
	 * 风速
	 */
	public static final int speed = 104;
	/**
	 * 左右扫风
	 */
	public static final int swingH = 114;
	/**
	 * 上下扫风
	 */
	public static final int swingV = 113;
	
	public static final int tempUp=115;
	public static final int tempDown=116;
	/**
	 * 默认温度
	 */
	public static final int defaultTemp = 26;
	public AirStateValues(){}
	/**
	 * 初始化
	 * @param stateid 空调状态ID
	 * @param statevalue 空调状态值
	 * @param standardvalue 对应的标准值
	 */
	public AirStateValues(int stateid,String statevalue,int standardvalue){
		this.stateid=stateid;
		this.standardvalue=standardvalue;
		this.statevalue=statevalue;
	}
	private int stateid;
	private String statevalue;
	private int standardvalue;
	private String standardName;
	public int getStateid() {
		return stateid;
	}
	public void setStateid(int stateid) {
		this.stateid = stateid;
	}
	public String getStatevalue() {
		return statevalue;
	}
	public void setStatevalue(String statevalue) {
		this.statevalue = statevalue;
	}
	public int getStandardvalue() {
		return standardvalue;
	}
	public void setStandardvalue(int standardvalue) {
		this.standardvalue = standardvalue;
	}
	
	public String getStandardName() {
		return standardName;
	}
	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}
	/**
	 * 从状态对照列表中找到所属的标准等级
	 * @param stateid 状态ID
	 * @param stateValue 状态值
	 * @return 若返回-1表示没有找到
	 */
	@SuppressLint("DefaultLocale")
	public static int getStandardValue(int stateid,String stateValue){
		stateValue=stateValue.toLowerCase().replace(" ", "");
		int standardValue=-1;
		boolean flag=false;
		if(stateid>=0&&!StringUtil.isNullOrEmpty(stateValue)){
			for(AirStateValues item:GetAirStateValuesList()){
				//转换为小写并去掉所有空格,防止因为大小写及空调原因匹配不上
				flag=item.stateid==stateid
					&&item.statevalue.toLowerCase().replaceAll(" ", "").equals(stateValue);
				if(flag){
					standardValue=item.standardvalue;
					break;
				}
			}
		}
		return standardValue;
	}
	/**
	 * 从状态对照列表中找到所属的标准等级
	 * @param stateName 状态名称
	 * @param stateValue 状态值
	 * @return 若返回-1表示没有找到
	 */
	@SuppressLint("DefaultLocale")
	public static int getStandardValue(String stateName,String stateValue){
		int standardValue=-1;
		boolean flag=false;
		if(!StringUtil.isNullOrEmpty(stateValue)){
			List<AirStateValues> statevalues=GetAirStateValuesList();
			for(AirStateValues item:statevalues){
				//之所以转换为小写并去掉所有空格转换
				flag=item.standardName.equals(stateName)
					&&item.statevalue.toLowerCase().replaceAll(" ", "").equals(stateValue.toLowerCase().replaceAll(" ", ""));
				if(flag){
					standardValue=item.standardvalue;
					break;
				}
			}
		}
		return standardValue;
	}
	/**
	 * 从状态参考名称列表中找出相应的stateid
	 * @param stateName 名称
	 * @return
	 */

@SuppressLint("DefaultLocale")
	public static int getRefKeyByName(String stateName){
		int refkey=-1;
		String tempname="";
		stateName=stateName.toLowerCase().replace(" ", "");
		if(!StringUtil.isNullOrEmpty(stateName)){
			 Set<Entry<Integer, String>>  set=getStatekeynamemap_ref().entrySet();  
	          Iterator<Entry<Integer, String>>   iterator=set.iterator();  
			while(iterator.hasNext()){
				 Entry<Integer, String>  mapentry =iterator.next(); 
				 String [] namearr=mapentry.getValue().toString().split(",");
				 boolean isFind=false;
				 for(String name:namearr){
					 tempname=name.toLowerCase().replace(" ", "");
					 if(tempname.equals(stateName)){
						 refkey=mapentry.getKey();
						 isFind=true;
						 break;
					 }
				 }
				 if(isFind){
					 break;
				 }
			}
		}
		return refkey;
	}
	public static Map<Integer,String> GetStatekeynameMap(){
		initValues_statekeynamemap();
		return statekeynamemap;
	}
	public static List<AirStateValues> GetAirStateValuesList(){
		initValues_airStateValuesList();
		return airStateValuesList;
	}
	private static Map<Integer,String> getStatekeynamemap_ref()
	{
		initValues_keynamerefmap();
		return keynamerefmap;
	}
	private static void initValues_keynamerefmap(){
		if(keynamerefmap==null||keynamerefmap.size()>0){
			keynamerefmap=new HashMap<Integer, String>();
			keynamerefmap.put(AirStateValues.power, "power");
			keynamerefmap.put(AirStateValues.mode, "mode");
			keynamerefmap.put(AirStateValues.speed, "fanspeed,speed,Fan Speed");
			keynamerefmap.put(AirStateValues.swingV, "Swing,swing up-down,Swing Up/Down,Swing V");
			keynamerefmap.put(AirStateValues.swingH, "swing left-right,swing H,Swing Left/Right");
			keynamerefmap.put(AirStateValues.temp, "temprature,temp,Temperature DisPlay,Temp DisPlay,Temp Heat");
			keynamerefmap.put(AirStateValues.tempUnit, "temprature unit");
			keynamerefmap.put(AirStateValues.tempUp, "Temperature Up");
			keynamerefmap.put(AirStateValues.tempDown, "Temperature Down");
		}
	}
	private static void initValues_statekeynamemap(){
		if(statekeynamemap==null||statekeynamemap.size()==0){
			statekeynamemap=new HashMap<Integer, String>();
			statekeynamemap.put(AirStateValues.power, "power");
			statekeynamemap.put(AirStateValues.mode, "mode");
			statekeynamemap.put(AirStateValues.speed, "fanspeed");
			statekeynamemap.put(AirStateValues.swingV, "swing up-down");
			statekeynamemap.put(AirStateValues.swingH, "swing left-right");
			statekeynamemap.put(AirStateValues.temp, "temprature");
			statekeynamemap.put(AirStateValues.tempUnit, "temprature unit");
		}
	}
	private static void initValues_airStateValuesList(){
		
		if(airStateValuesList==null||airStateValuesList.size()==0){
			airStateValuesList=new ArrayList<>();
			//模式字段：有些空调是使用power表示的，有些是使用mode表示的；
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"off",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"on",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"auto",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"cool",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"heat",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"dry",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"fan",5));
			airStateValuesList.add(new AirStateValues(AirStateValues.power,"feel",6));

			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"off",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"on",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"auto",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"cool",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"heat",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"dry",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"fan",5));
			airStateValuesList.add(new AirStateValues(AirStateValues.mode,"feel",6));
			
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"auto",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"very low",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"low",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"medium",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"low high",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"high",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"very high",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"quiet",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"super quiet",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"breeze",5));//微风
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"long",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.speed,"static",4));
			
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"off",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"on",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"auto",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down 5",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down 4",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down 3",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down 2",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down 1",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down center 2",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"down center 1",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up center 1",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up center 2",3));
			
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up 1",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up 2",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up 3",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up 4",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"up 5",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"full up",5));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"full down",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingV,"full swing",3));
			
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"off",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"on",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"auto",0));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left 5",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left 4",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left 3",1));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left 2",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left 1",2));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left center 2",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"left center 1",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"center",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right center 1",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right center 2",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right 1",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right 2",4));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right 3",5));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right 4",5));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right 5",5));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right chaos",3));
			airStateValuesList.add(new AirStateValues(AirStateValues.swingH,"right full swing",3));
			
			if(statekeynamemap==null||statekeynamemap.size()==0){
				initValues_statekeynamemap();
			}
			for(AirStateValues stateValue:airStateValuesList){
				stateValue.setStandardName(statekeynamemap.get(stateValue.stateid));
			}
		}
	}
}
