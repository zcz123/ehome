package cc.wulian.smarthomev5.utils;
import java.util.List;

import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.smarthomev5.account.WLUserManager;
public class OsClient {
	public final static String DEVICE_INFO_BY_GW = "GW";
	public final static String DEVICE_INFO_BY_CAMERA ="CAMERA";
	
	private OsClient() {
	}
    private volatile static OsClient _instance;
    private static boolean isIniting = false;
    private static Object singletonLock = new Object();
    public static OsClient getInstanceDC() {
        if (_instance == null) {
            synchronized (singletonLock) {
                if (_instance == null) {
                	if(isIniting) {
    					throw new RuntimeException("Should not getAccountManger when initing AccountManger.");
    				}
    				isIniting = true;
                    _instance = new OsClient();
    				isIniting = false;
                }
            }
        }
        return _instance;
    }


	 public int sMtrlBindDevice(String deviceId,String devicePasswd,String deviceType,String deviceModel){
		 return WLUserManager.getInstance().getStub().bindDevice(deviceId, devicePasswd, deviceType, deviceModel).status;
	 }
	/**分享*/
	 public String sMtrlShare(){
		 return null;
	 }
	 /**获取用户设备信息*/
	 public List<GatewayInfo> getSimpleDeviceByUser(){
		 return WLUserManager.getInstance().getStub().getSimpleDeviceByUser();
	 }
	 /**获取设备详细信息*/ // TODO 将设备类型全部转换为DatewayInfo
	 public AMSDeviceInfo getDeviceInfo(String gwId){
		 return WLUserManager.getInstance().getStub().getDeviceInfo(gwId);
	 }
//	 /**根据类型获取设备信息*/
//	 public List<AMSDeviceInfo> getDeviceInfoByMode(String model){
//		 List<GatewayInfo> gInfos = this.getSimpleDeviceByUser();
//		 List<AMSDeviceInfo>aInfos = null;
//		 if(gInfos.size()>0){
//			 for(GatewayInfo obj:gInfos){
//				 if(!obj.getDeviceType().equals(model)){
//					 gInfos.remove(obj);
//				 }
//			 }
//			 if(gInfos.size()>0){
//				 aInfos = new ArrayList<>();
//				 for(GatewayInfo obj:gInfos){
//					 aInfos.add(this.getDeviceInfo(obj.getGwID()));
//				 }
//			 }
//			 return null;
//		 }
//	 }

}
