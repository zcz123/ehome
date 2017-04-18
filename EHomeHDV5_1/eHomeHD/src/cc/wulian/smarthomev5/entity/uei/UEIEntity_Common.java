package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cc.wulian.ihome.wan.util.StringUtil;

import android.os.Parcel;
import android.util.Log;

/**
 * 普通的UEI设备
 * 
 * @author yuxiaoxuan
 * @date 2016年7月14日15:38:43
 */
public class UEIEntity_Common extends UEIEntity {
	/*普通设备收到的设备信息如下：
	{"v":
		{
			"b":"Hitachi",
			"m":"40HE1321S1",
			"kcs":["01","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F","10","11","12","15","16","18","19","1A","1B","1C","1D","21","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F","30","44","48","4D","4E","4F","50","56","5A","5E","62","67","69","6A","6C","6F","71","79","84","95","B9","BA","BB","BC","BD","BE","BF","C0","C1","C2","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DD","DE","DF","E0","E1","E2","E3","E4","E5","E6","E9"]
		},
			"d":"041DB605004B1200",
			"gwID":"50294D20429B",
			"m":"1",
			"k":"1_T3981"
			”nm":""
	}
	b:品牌名称
	m:品牌编码
	k:设备支持的按键值集合
	 * 
	 * */

	@Override
	protected void ExtractValueInfo() {
		if (!StringUtil.isNullOrEmpty(this.value)) {
			try {
				JSONObject jsonValue = JSONObject.parseObject(this.value);
				if(jsonValue.containsKey("nm")){
					this.brandCusName=jsonValue.getString("nm");
				}
				if(jsonValue.containsKey("pro")){
					this.proCode=jsonValue.getString("pro");
				}
				if(jsonValue.containsKey("proName")){
					this.proName=jsonValue.getString("proName");
				}
				this.brandName = jsonValue.getString("b");
				this.brandType = jsonValue.getString("m");
				this.virKey = jsonValue.getString("kcs");
			} catch (JSONException e) {
				Log.d("WL_23", "Value不符合json标准 " + this.value);
			}
		}
	}
	@Override
	public List<UeiVirtualBtn> GetVirKeyList(){
		 List<UeiVirtualBtn> virKeyList=new ArrayList<>();
		if(!StringUtil.isNullOrEmpty(this.virKey)){
			String tempvirKey=this.virKey.replace("[","").replace("]", "").replace("\"", "");
			String [] arrVirKey=tempvirKey.split(",");
			for(String vk:arrVirKey){
				if(!StringUtil.isNullOrEmpty(vk)){
					String strint10 = "";
					int int10=Integer.parseInt(vk, 16);
					if (int10 < 10) {
						strint10 = "0" + int10;
					}else{
						strint10=int10+"";
					}
					virKeyList.add(new UeiVirtualBtn(strint10));
				}
			}
		}
		return virKeyList;
	}
	@Override
	protected int returnUeiType() {
		return 1;
	}
}
