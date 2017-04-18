package cc.wulian.smarthomev5.event;
import cc.wulian.ihome.wan.entity.GatewayInfo;
public class DeskCameraEvent {
	private PresetEvent presetEvent = null;
	public DeskCameraEvent(GatewayInfo gatewayInfo){
		presetEvent = new PresetEvent(gatewayInfo,null);
	}
	public PresetEvent getPresetEvent() {
		return presetEvent;
	}
	public class PresetEvent extends Event{
		public PresetEvent(){
			
		}
		public PresetEvent(GatewayInfo gatewayInfo,String persetTitle){
			this(gatewayInfo.getTutkUID(), gatewayInfo.getTutkPASSWD(),gatewayInfo.getGwID());
			this.persetTitle = persetTitle;
		}
		public PresetEvent(String tutkUid,String tutkPwd,String gwId){
			super(tutkUid, tutkPwd,gwId);
		}
		private String persetTitle;
		public void setPersetTitle(String persetTitle) {
			this.persetTitle = persetTitle;
		}
		public String getPersetTitle() {
			return persetTitle;
		}
	}
	public class Event{
		public Event(){
			
		}
		public Event(String tutkUid,String tutkPwd,String gwId){
			this.tutkUid = tutkUid;
			this.tutkPwd = tutkPwd;
			this.gwId = gwId;
		}
		private String tutkUid;
		private String tutkPwd;
		private String gwId;
		public void setTutkPwd(String tutkPwd) {
			this.tutkPwd = tutkPwd;
		}
		public String getTutkPwd() {
			return tutkPwd;
		}
		public void setTutkUid(String tutkUid) {
			this.tutkUid = tutkUid;
		}
		public String getTutkUid() {
			return tutkUid;
		}
		public void setGwId(String gwId) {
			this.gwId = gwId;
		}
		public String getGwId() {
			return gwId;
		}
	}
}
