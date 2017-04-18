package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.util.Log;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * UEI设备--空调
 * @author yuxiaoxuan
 *
 */
public class UEIEntity_Air extends UEIEntity {

	public UEIEntity_Air(){
		super();
		this.airStates=new ArrayList<>();
	}
	private List<AirStateStandard> airStates;
	
	public List<AirStateStandard> getAirStates() {
		return airStates;
	}
	public void setAirStates(List<AirStateStandard> airStates) {
		this.airStates = airStates;
	}
	@Override
	protected void ExtractValueInfo() {
		if (!StringUtil.isNullOrEmpty(this.value)) {
			try {
				JSONObject jsonValue =JSONObject.parseObject(this.value);// new JSONObject(this.value);
				if(jsonValue.containsKey("nm")){
					this.brandCusName=jsonValue.getString("nm");
				}
				this.brandName = jsonValue.getString("b");
				this.brandType = jsonValue.getString("m");
				this.virKey = jsonValue.getString("kcs");
				if (!StringUtil.isNullOrEmpty(this.virKey)) {
					GetVirKeyList();
				}
			} catch (JSONException e) {
				Log.d("WL_23", "Value不符合json标准 " + this.value);
			}
		}
	}
	
	
	public JSONArray GetJsonArray_virKey(String virKeyData){
		JSONArray virKeyArray=new JSONArray();
		return virKeyArray;
	}
	@Override
	public List<UeiVirtualBtn> GetVirKeyList(){
		List<UeiVirtualBtn> standardStateList=new ArrayList<>();
		this.airStates.clear();
		if(!StringUtil.isNullOrEmpty(this.virKey)){
			JSONArray jsonarrayCurr =com.alibaba.fastjson.JSONArray.parseArray(this.virKey);	
			for(int i=0;i<jsonarrayCurr.size();i++){
				JSONObject jsonitemCurr=jsonarrayCurr.getJSONObject(i);
				AirStateStandard newAirState=new AirStateStandard(jsonitemCurr.getString("s"));
				newAirState.setIndex(jsonitemCurr.getString("ac"));
				newAirState.setCustomName(jsonitemCurr.getString("nm"));
				standardStateList.add(newAirState);
				this.airStates.add(newAirState);
			}
		}
		return standardStateList;
	}
	@Override
	protected int returnUeiType() {
		return 3;
	}
}
