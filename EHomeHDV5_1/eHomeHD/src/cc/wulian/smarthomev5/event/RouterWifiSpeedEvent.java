package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.callback.router.entity.SpeedBandEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedListEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedStatusEntity;

public class RouterWifiSpeedEvent {
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	private String action;
	private SpeedStatusEntity statusEntity;
	private List<SpeedListEntity> entities;
	private SpeedBandEntity bandEntity;

	public RouterWifiSpeedEvent(String action, SpeedStatusEntity statusEntity,
			List<SpeedListEntity> entities, SpeedBandEntity bandEntity) {
		super();
		this.action = action;
		this.statusEntity = statusEntity;
		this.entities = entities;
		this.bandEntity = bandEntity;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public SpeedStatusEntity getStatusEntity() {
		return statusEntity;
	}

	public void setStatusEntity(SpeedStatusEntity statusEntity) {
		this.statusEntity = statusEntity;
	}

	public List<SpeedListEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<SpeedListEntity> entities) {
		this.entities = entities;
	}

	public SpeedBandEntity getBandEntity() {
		return bandEntity;
	}

	public void setBandEntity(SpeedBandEntity bandEntity) {
		this.bandEntity = bandEntity;
	}

}
