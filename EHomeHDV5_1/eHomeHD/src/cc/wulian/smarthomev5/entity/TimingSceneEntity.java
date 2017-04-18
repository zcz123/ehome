package cc.wulian.smarthomev5.entity;

import android.text.TextUtils;

public class TimingSceneEntity implements Cloneable
{
	public String sceneID;
	public String sceneIcon;
	public String sceneName;
	public String time;
	public String weekDay;

	public String groupID;
	public String groupName;
	public String groupStatus;

	public TimingSceneEntity()
	{
		sceneID = "";
		sceneIcon = "";
		sceneName = "";
		time = "";
		weekDay = "";
	}
	@Override
	public boolean equals( Object o ){
		if(o instanceof TimingSceneEntity) {
			TimingSceneEntity ts = (TimingSceneEntity) o;
			return TextUtils.equals(sceneID, ts.sceneID) 
					&& TextUtils.equals(sceneIcon, ts.sceneIcon)
					&& TextUtils.equals(sceneName, ts.sceneName) 
					&& TextUtils.equals(time, ts.time)
					&& TextUtils.equals(weekDay, ts.weekDay);
		}
		else {
			return super.equals(o);
		}
	}

	@Override
	public TimingSceneEntity clone(){
		TimingSceneEntity entity = new TimingSceneEntity();
		entity.setGroupID(this.getGroupID());
		entity.setGroupName(this.getGroupName());
		entity.setGroupStatus(this.getGroupStatus());
		entity.setSceneID(this.getSceneID());
		entity.setSceneName(this.getSceneName());
		entity.setSceneIcon(this.getSceneIcon());
		entity.setTime(this.getTime());
		entity.setWeekDay(this.getWeekDay());
		return entity;
	}

	public String getSceneID() {
		return sceneID;
	}

	public void setSceneID(String sceneID) {
		this.sceneID = sceneID;
	}

	public String getSceneIcon() {
		return sceneIcon;
	}

	public void setSceneIcon(String sceneIcon) {
		this.sceneIcon = sceneIcon;
	}

	public String getSceneName() {
		return sceneName;
	}

	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	
}