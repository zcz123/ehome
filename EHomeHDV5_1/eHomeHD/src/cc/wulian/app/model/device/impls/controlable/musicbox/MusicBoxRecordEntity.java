package cc.wulian.app.model.device.impls.controlable.musicbox;

public class MusicBoxRecordEntity {
	public String gwID;
	public String devID;
	public String ep;
	public String songID;
	public String songName;

	public MusicBoxRecordEntity() {

	}

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

	public String getEp() {
		return ep;
	}

	public void setEp(String ep) {
		this.ep = ep;
	}

	public String getSongID() {
		return songID;
	}

	public void setSongID(String songID) {
		this.songID = songID;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

}
