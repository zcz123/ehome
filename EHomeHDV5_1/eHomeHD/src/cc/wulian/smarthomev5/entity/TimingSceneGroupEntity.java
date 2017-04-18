package cc.wulian.smarthomev5.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev5.utils.CmdUtil;

public class TimingSceneGroupEntity
{
	public String groupID;
	public String groupName;
	public String groupStatus;
	private Map<String, List<TimingSceneEntity>> timingSceneMap = new HashMap<String, List<TimingSceneEntity>>();
	public TimingSceneGroupEntity()
	{
		groupID = "1";
		groupName = "1";
		groupStatus = CmdUtil.SCENE_UNUSE;
	}
	public TimingSceneGroupEntity(String groupID,String groupName,String groupStatus){
		this.groupID = groupID;
		this.groupName = groupName;
		this.groupStatus = groupStatus;
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
	public boolean isUseable(){
		return CmdUtil.SCENE_USING.equals(groupStatus);
	}
	public void addTimingSceneEntity(TimingSceneEntity entity){
		List<TimingSceneEntity> entites = timingSceneMap.get(entity.getSceneID());
		if(entites ==null){
			entites = new ArrayList<TimingSceneEntity>();
			this.timingSceneMap.put(entity.getSceneID(), entites);
		}
		entites.add(entity);
	}
	public List<TimingSceneEntity> modifyTimingSceneEntityNewList(TimingSceneEntity oldTimingSceneEntity,TimingSceneEntity entity){
		List<TimingSceneEntity> entites = getAllTimeSceneEntities();
		TimingSceneEntity selectEntity = null ;
		for(TimingSceneEntity e : entites){
			if(e.equals(oldTimingSceneEntity)){
				selectEntity = e;
				break;
			}
		}
		entites.remove(selectEntity);
		entites.add(entity);
		return entites;
	}
	public  List<TimingSceneEntity> removeTimingSceneEntitiesNewList(TimingSceneEntity mTimingSceneEntity){
		List<TimingSceneEntity> entites = getAllTimeSceneEntities();
		TimingSceneEntity selectEntity = null ;
		for(TimingSceneEntity e : entites){
			if(e.equals(mTimingSceneEntity)){
				selectEntity = e;
				break;
			}
		}
		entites.remove(selectEntity);
		return entites;
	}
	
	public  List<TimingSceneEntity> removeTimingSceneListNewList(List<TimingSceneEntity> mTimingSceneEntities){
		List<TimingSceneEntity> entites = getAllTimeSceneEntities();
		for(int i= entites.size() -1;i >= 0 ;i--){
			TimingSceneEntity e = entites.get(i);
			for(TimingSceneEntity d : mTimingSceneEntities){
				if(e.equals(d)){
					entites.remove(i);
				}
			}
		}
		return entites;
	}
	
	public List<TimingSceneEntity> addTimingSceneEntityNewList(TimingSceneEntity entity){
		List<TimingSceneEntity> entites = getAllTimeSceneEntities();
		entites.add(entity);
		return entites;
	}
	public  List<TimingSceneEntity> getTimingSceneEntities(String sceneID){
		return this.timingSceneMap.get(sceneID);
	}
	public boolean contains(String sceneID){
		return timingSceneMap.containsKey(sceneID);
	}
	public void clear(){
		timingSceneMap.clear();
	}
	public Map<String, List<TimingSceneEntity>> getTimingSceneMap() {
		return timingSceneMap;
	}
	public void setTimingSceneMap(Map<String, List<TimingSceneEntity>> timingSceneMap) {
		this.timingSceneMap = timingSceneMap;
	}
	public void removeTimingSceneEntity(String sceneID){
		this.timingSceneMap.remove(sceneID);
	}
	public List<TimingSceneEntity> getAllTimeSceneEntities(){
		List<TimingSceneEntity> entites = new ArrayList<TimingSceneEntity>();
		for(List<TimingSceneEntity> mTimingSceneEntities :timingSceneMap.values()){
			entites.addAll(mTimingSceneEntities);
		}
		return entites;
	}
}