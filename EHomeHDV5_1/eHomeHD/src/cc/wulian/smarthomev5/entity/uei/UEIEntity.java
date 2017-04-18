package cc.wulian.smarthomev5.entity.uei;

import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;

public abstract class UEIEntity{
	
	public static final String TYPE_SCENE = "0";
	public static final String TYPE_DEVICE = "1";
	/*-------------------------原生数据部分------------------------*/
	private String gwID;
	private String devID;
	private String appID;
	private String key;//格式是 mode_deviceCode
	private String time;
	protected String value;
	
	
	
	
	public String getGwID() {
		return gwID;
	}
	public void setGwID(String gwID) {
		this.gwID = gwID;
	}
	public String getDevID() {
		return devID;
	}
	public void setDevID(String devID) {
		this.devID = devID;
	}
	public String getAppID() {
		return appID;
	}
	public void setAppID(String appID) {
		this.appID = appID;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
		this.mode="";
		this.deviceCode="";
		this.deviceType="";
		if(!StringUtil.isNullOrEmpty(this.key)){
			String [] arrKey=this.key.split("_");
			if(arrKey.length==2){
				this.mode=arrKey[0];
				this.deviceCode=arrKey[1];
				//@表示完全自定义学习，解析方式和其它的不一样
				if(!this.mode.equals("@")){
					if(!StringUtil.isNullOrEmpty(this.deviceCode)){
						this.deviceType=this.deviceCode.substring(0, 1);
					}
				}else{
					this.deviceType="@";
				}
				
			}
		}
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
		ExtractValueInfo();
	}
	/*------------------------解析部分------------------------*/
	private String mode="";
	private String deviceCode="";
	private String deviceType="";
	protected String brandName="";
	protected String brandCusName="";//对应NM
	protected String brandType="";
	protected String proName="";//运营商名称
	protected String proCode="";//运营商编号


	//对普通设备来说是虚拟键盘所对应的键；对空调来说是快捷键状态
	protected String virKey="";
	
	public String getVirkey(){
		return virKey;
	}
	/**
	 * 匹配模式,通过key的第1个字节解析出</br>
	 * 1是匹配成功，2是学习的其它设备，3代表空调，4代表学习的空调
	 * @return 
	 */
	public String getMode(){
		return mode;
	}
	/**
	 * 设备编码，通过key的第2个字节向后解析出
	 * @return
	 */
	public String getDeviceCode(){
		return deviceCode;
	}
	/**
	 * 设备类型，通过deviceCode的第1个字节解析出
	 * @return
	 */
	public String getDeviceType(){
		return this.deviceType;
	}
	
	/**
	 * 品牌类型，从value中的m解析出
	 * @return
	 */
	public String getBrandType(){
		return this.brandType;
	}
	
	/**
	 * 品牌名，从value中的b解析出
	 * @return
	 */
	public String getBrandName(){
		return this.brandName;
	}

	/**
	 * 自定义名称，从value中的nm解析出
	 * @return
     */
	public String getBrandCusName(){
		return this.brandCusName;
	}

	public String getProName(){
		return this.proName;
	}
	public String getProCode(){
		return this.proCode;
	}

	/**
	 * 获取显示名称
	 * @return BrandCusName有优先显示，若无则显示BrandType
     */
	public String getDisplayName(){
		String displayName="";
		if(!StringUtil.isNullOrEmpty(getBrandCusName())){
			displayName=getBrandCusName();
		}else if(!StringUtil.isNullOrEmpty(getBrandType())){
			displayName=getBrandType();
		}
		return displayName;
	}
	
	/*---------------------抽象方法-----------------*/
	/**
	 * 提取Value中的信息
	 */
	protected abstract void ExtractValueInfo();
	/**
	 * 获取该遥控中的按键信息</br>
	 * 该信息一般是从406接口中“v——>kcs”中获取的
	 * @return
	 */
	public abstract List<UeiVirtualBtn> GetVirKeyList();
	
	protected abstract int returnUeiType();
	
	/*---------------------界面显示-----------------*/
	private int smallIcon=0;
	public int getSmallIcon() {
		return smallIcon;
	}
	public void setSmallIcon(int smallIcon) {
		this.smallIcon = smallIcon;
	}
	private String brandTypeName;


	public String getBrandTypeName() {
		return brandTypeName;
	}
	public void setBrandTypeName(String brandTypeName) {
		this.brandTypeName = brandTypeName;
	}
	
	private int ueiType=0;
	/**
	 * 获取UEI类型</br>
	 * 1:普通设备；
	 * 2:根据模板学习的设备；
	 * 3:空调；
	 * 4:完全自定义学习；
	 * @return
	 */
	public int getUeiType(){
		ueiType=returnUeiType();
		return ueiType;
	}
}
