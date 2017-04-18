package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.util.Log;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * 根据模板学习的设备
 * @author yuxiaoxuan
 * @date 2016年8月5日09:27:01
 *
 */
public class UEIEntity_StudyByTemplate extends UEIEntity  {
	/*模板学习收到的设备信息如下：
	 {
	    "appID":"HDe5563a58449955c",
	    "cmd":"406",
	    "d":"DA1EB605004B1200",
	    "gwID":"50294D20037D",
	    "k":"2_T1471420454632",
	    "m":"3",
	    "t":"1471420455",
	    "v":"……"
	 }
	 其中v的格式是：
	{
	    "nm":"",
	    "kcs":[
	        {
	            "k":1,
	            "lc":0
	        },
	        {
	            "k":6,
	            "lc":3
	        },
	        {
	            "k":42,
	            "lc":5
	        },
	        {
	            "k":9,
	            "lc":15
	        },
	        {
	            "k":10,
	            "lc":16
	        }
	    ]
	}
	 * 
	 * */
	
	@Override
	protected void ExtractValueInfo() {
		if(!StringUtil.isNullOrEmpty(this.value)){
			try {
				JSONObject jsonValue = JSONObject.parseObject(this.value);
				if(jsonValue.containsKey("nm")){
					this.brandCusName=jsonValue.getString("nm");
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
					ueiVirbtn.setKeyid(jsonitemCurr.getString("k"));
					ueiVirbtn.setLc(jsonitemCurr.getString("lc"));
					ueiVirbtn.setNm("");//模板学习的按键没有名称
					virKeyList.add(ueiVirbtn);
				}				
			}
		}
		return virKeyList;
	}
	@Override
	protected int returnUeiType() {
		return 2;
	}
}
