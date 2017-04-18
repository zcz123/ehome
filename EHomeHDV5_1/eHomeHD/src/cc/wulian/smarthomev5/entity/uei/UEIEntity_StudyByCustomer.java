package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import cc.wulian.ihome.wan.util.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * UEI设备完全自定义学习
 * @author yuxiaoxuan
 * @date 2016年8月5日09:30:41
 */
public class UEIEntity_StudyByCustomer  extends UEIEntity {
	/*完全自定义学习返回的数据
	 * {
		    "cmd":"406",
		    "d":"DA1EB605004B1200",
		    "gwID":"50294D20037D",
		    "k":"@_1471421500084",
		    "m":"1",
		    "t":"1471421501",
		    "v":"……"
	   }
	 * 其中v里面的数据格式是：
	 * {
	    "nm":"完全自定义学习1",
	    "kcs":[
	        {
	            "nm":"音量+",
	            "lc":1
	        },
	        {
	            "nm":"音量-",
	            "lc":2
	        },
	        {
	            "nm":"确定",
	            "lc":3
	        },
	        {
	            "nm":"频道+",
	            "lc":4
	        },
	        {
	            "nm":"频道-",
	            "lc":5
	        }
	    ]
	}
	 * */
	@Override
	protected void ExtractValueInfo() {
		if(!StringUtil.isNullOrEmpty(this.value)&&this.value.contains(":")){
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
//				this.brandName = jsonValue.containsKey("nm")?jsonValue.getString("nm"):"";
				this.brandType=jsonValue.containsKey("b")?jsonValue.getString("b"):"";
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
			JSONArray jsonarrayCurr =JSONArray.parseArray(this.virKey);	
			for(int i=0;i<jsonarrayCurr.size();i++){
				JSONObject jsonitemCurr=jsonarrayCurr.getJSONObject(i);
				if(jsonitemCurr!=null){
					UeiVirtualBtn ueiVirbtn=new UeiVirtualBtn();
					ueiVirbtn.setKeyid("");//学习的按键没有k
					if(jsonitemCurr.containsKey("lc")){
						ueiVirbtn.setLc(jsonitemCurr.getString("lc"));
					}
					if(jsonitemCurr.containsKey("nm")){
						ueiVirbtn.setNm(jsonitemCurr.getString("nm"));
					}
					virKeyList.add(ueiVirbtn);
				}				
			}
		}
		return virKeyList;
	}
	@Override
	protected int returnUeiType() {
		return 4;
	}
}
