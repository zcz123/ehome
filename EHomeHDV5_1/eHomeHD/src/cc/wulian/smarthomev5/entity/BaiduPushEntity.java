package cc.wulian.smarthomev5.entity;

import java.io.Serializable;

public class BaiduPushEntity implements Serializable{
	public BaiduPushEntity(){

	}
	public BaiduPushEntity(String uid,String title,String eventType,String evetTime,String content,String pwd){
		setUid(uid);
		setTitle(title);
		setEventType(eventType);
		setEvetTime(evetTime);
		setContent(content);
		setPwd(pwd);
	}
	private String uid;
	private String pwd;
	private String title;
	private String eventType;
	private String evetTime;
	private String content;
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUid() {
		return uid;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEvetTime(String evetTime) {
		this.evetTime = evetTime;
	}
	public String getEvetTime() {
		return evetTime;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPwd() {
		return pwd;
	}
}
