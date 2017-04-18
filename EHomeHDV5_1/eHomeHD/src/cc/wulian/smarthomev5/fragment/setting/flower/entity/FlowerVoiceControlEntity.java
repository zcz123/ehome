package cc.wulian.smarthomev5.fragment.setting.flower.entity;

public class FlowerVoiceControlEntity {

	public static final String VALUE_STUDYED = "1";
	public static final String VALUE_UNSTUDYED = "0";
	public static final String VALUE_UNBINDSCENE = "-1";
	public static final String VALUE_BING_NO_SCENE = "-2";
	private String index;
	private String study;
	private String bindScene;
	private String gwID;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	
	public String getStudy() {
		return study;
	}
	public void setStudy(String study) {
		this.study = study;
	}
	public String getBindScene() {
		return bindScene;
	}
	public void setBindScene(String bindScene) {
		this.bindScene = bindScene;
	}
	public void clear(){
		this.study = VALUE_UNSTUDYED;
		bindScene = VALUE_UNBINDSCENE;
	}
	public String getGwID() {
		return gwID;
	}
	public void setGwID(String gwID) {
		this.gwID = gwID;
	}
	
}
