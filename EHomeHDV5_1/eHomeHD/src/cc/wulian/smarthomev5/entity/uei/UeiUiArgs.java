package cc.wulian.smarthomev5.entity.uei;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用于UEI Activity之间、Fragment之间及接口之间相互传值
 * 因为字段比较多，所以封装了一个类。否则容易混乱。
 * @author yuxiaoxuan
 * @date 2016年7月11日20:10:50
 */
public class UeiUiArgs implements Parcelable {
	private String gwID;
	private String devID;
	private String appID;
	private String key;
	private String time;
	private String value;
	private String ep;
	private String epType;
	private int viewMode;
	
	public UeiUiArgs(){
		gwID="";
		devID="";
		appID="";
		key="";
		time="";
		value="";
		ep="";
		epType="";
		viewMode=-1;
	}
	public UeiUiArgs(Parcel source){
		gwID=source.readString();
		devID=source.readString();
		appID=source.readString();
		key=source.readString();
		time=source.readString();
		value=source.readString();
		ep=source.readString();
		epType=source.readString();
		viewMode=source.readInt();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(gwID);
		dest.writeString(devID);
		dest.writeString(appID);
		dest.writeString(key);
		dest.writeString(time);
		dest.writeString(value);
		dest.writeString(ep);
		dest.writeString(epType);
		dest.writeInt(viewMode);
	}
	//实例化静态内部对象CREATOR实现接口Parcelable.Creator  
    public static final Parcelable.Creator<UeiUiArgs> CREATOR = new Creator<UeiUiArgs>() {  
          
        @Override  
        public UeiUiArgs[] newArray(int size) {  
            return new UeiUiArgs[size];  
        }  
          
        //将Parcel对象反序列化为ParcelableDate  
        @Override  
        public UeiUiArgs createFromParcel(Parcel source) {  
            return new UeiUiArgs(source);  
        }  
    };

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
	}
	public String getEp() {
		return ep;
	}
	public void setEp(String ep) {
		this.ep = ep;
	}
	public String getEpType() {
		return epType;
	}
	public void setEpType(String epType) {
		this.epType = epType;
	}
	
	public int getViewMode() {
		return viewMode;
	}
	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}
	public UEIEntity ConvertToEntity(){
		UEIEntity uei=UEIEntityManager.CreateUEIEnitity(this.key);
		uei.setAppID(this.appID);
		uei.setDevID(this.devID);
		uei.setGwID(this.gwID);
		uei.setKey(this.key);
		uei.setValue(this.value);
		return uei;
	}
}
