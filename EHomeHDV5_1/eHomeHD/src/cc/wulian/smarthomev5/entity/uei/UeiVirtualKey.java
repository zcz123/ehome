package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录UEI设备的遥控器键盘键值
 * 该类的资料来自：“Global Key Chart for***.pdf”
 * @author yuxiaoxuan
 * @date 2016年7月12日14:04:53
 */
public class UeiVirtualKey {
	public UeiVirtualKey(String virKey,String uiText){
		this.uiText=uiText;
		this.virKey=virKey;
	}
	public UeiVirtualKey(String ueiType,String virKey,String uiText){
		this.uiText=uiText;
		this.virKey=virKey;
		this.ueiType=ueiType;
	}
	private String uiText="";
	private String virKey="";
	/*若为空，则表示适用于所有的按键*/
	private String ueiType="";
	public String getUiText() {
		return uiText;
	}
	public String getVirKey() {
		return virKey;
	}
	public String getUeiType() {
		return ueiType;
	}
	/**
	 * 数字键 1~9
	 */
	private static List<UeiVirtualKey> digitKeyList=null;
	private static void loadDigitKeys(){
		if(digitKeyList==null||digitKeyList.size()==0){
			digitKeyList=new ArrayList<UeiVirtualKey>();
			digitKeyList.add(new UeiVirtualKey("09","1"));
			digitKeyList.add(new UeiVirtualKey("10","2"));
			digitKeyList.add(new UeiVirtualKey("11","3"));
			digitKeyList.add(new UeiVirtualKey("12","4"));
			digitKeyList.add(new UeiVirtualKey("13","5"));
			digitKeyList.add(new UeiVirtualKey("14","6"));
			digitKeyList.add(new UeiVirtualKey("15","7"));
			digitKeyList.add(new UeiVirtualKey("16","8"));
			digitKeyList.add(new UeiVirtualKey("17","9"));			
		}
	}
	public static List<UeiVirtualKey> getDigitKey(){
		loadDigitKeys();
		return digitKeyList;
	}
	
}
