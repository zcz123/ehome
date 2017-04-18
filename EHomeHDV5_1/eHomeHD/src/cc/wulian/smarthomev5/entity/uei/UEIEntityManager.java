package cc.wulian.smarthomev5.entity.uei;

import android.annotation.SuppressLint;
import android.content.Context;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource.WL23_ResourceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.entity.Command406Result;

public class UEIEntityManager {
	
	/**创建UEI对象*/
	@SuppressLint("DefaultLocale")
	public static UEIEntity CreateUEIEnitity(String key){
		UEIEntity ueiEntity=null;	
		if(!StringUtil.isNullOrEmpty(key)){
			String [] arrKey=key.split("_");
			if(arrKey.length==2&&!StringUtil.isNullOrEmpty(arrKey[0])){
				switch (arrKey[0]) {
				case "1":// 普通设备
					ueiEntity = new UEIEntity_Common();
					break;
				case "2":// 根据模板学习
					ueiEntity = new UEIEntity_StudyByTemplate();
					break;
				case "3":// 空调
					ueiEntity = new UEIEntity_Air();
					break;
				case "@"://完全自定义学习
					ueiEntity = new UEIEntity_StudyByCustomer();
					break;
				default:
					break;
				}
			}
		}
		return ueiEntity;
	}
	/**
	 * 把406返回的数据转换为UEI对象
	 * @param context
	 * @param appID
	 * @param result
	 * @return
	 */
	public static UEIEntity ConvertToUEIEntity(Context context, String appID,
			Command406Result result) {
		UEIEntity entity = CreateUEIEnitity(result.getKey());
		if (entity != null) {
			if (StringUtil.isNullOrEmpty(result.getAppID())) {
				entity.setAppID(appID);
			} else {
				entity.setAppID(result.getAppID());
			}
			entity.setDevID(result.getDevID());
			entity.setGwID(result.getGwID());
			entity.setKey(result.getKey());
			entity.setTime(result.getTime());
			entity.setValue(result.getData());
			WL23_ResourceInfo resourceInfo = WL_23_IR_Resource
					.getResourceInfo(entity.getDeviceType());
			if (resourceInfo.name > 0) {
				entity.setBrandTypeName(context.getString(resourceInfo.name));
			} else {
				entity.setBrandTypeName(resourceInfo.strName);
			}
			entity.setSmallIcon(resourceInfo.smallIcon);
		}
		return entity;
	}
}
