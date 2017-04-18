package cc.wulian.smarthomev5.entity;

public class AdvertisementEntity {
	public static final String ADVERTISEMENT_TYPE_1 = "1";
	public static final String ADVERTISEMENT_TYPE_2 = "2";

	private int pictureIndex;
	private String pictureURL;
	private String pictureLinkURL;
	private String version;
	private String type;
	private String startDate;
	private String endDate;


	public String getPictureURL() {
		return pictureURL;
	}
	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}
	public String getPictureLinkURL() {
		return pictureLinkURL;
	}
	public void setPictureLinkURL(String pictureLinkURL) {
		this.pictureLinkURL = pictureLinkURL;
	}
	public int getPictureIndex() {
		return pictureIndex;
	}
	public void setPictureIndex(int pictureIndex) {
		this.pictureIndex = pictureIndex;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
