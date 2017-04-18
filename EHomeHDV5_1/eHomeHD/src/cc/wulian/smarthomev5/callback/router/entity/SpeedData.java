package cc.wulian.smarthomev5.callback.router.entity;

import java.util.List;

public class SpeedData {
	private int code;
	private SpeedBandEntity band;
	private List<SpeedListEntity> list;
	private SpeedStatusEntity status;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public SpeedBandEntity getBand() {
		return band;
	}

	public void setBand(SpeedBandEntity band) {
		this.band = band;
	}

	public List<SpeedListEntity> getList() {
		return list;
	}

	public void setList(List<SpeedListEntity> list) {
		this.list = list;
	}

	public SpeedStatusEntity getStatus() {
		return status;
	}

	public void setStatus(SpeedStatusEntity status) {
		this.status = status;
	}

}
