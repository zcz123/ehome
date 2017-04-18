package com.wulian.iot.bean;
/**
 * 监控报警实体
 * @author syf
 */
public class GalleryAlarmInfo extends BaseCameraInfo{
	private String title;//标题
	private short year;
	private byte[] timeAck;
	private byte[] timeReceive;
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public short getYear() {
		return year;
	}
	public void setTimeAck(byte[] timeAck) {
		this.timeAck = timeAck;
	}
	public byte[] getTimeAck() {
		return timeAck;
	}

	public byte[] getTimeReceive() {
		return timeReceive;
	}

	public void setTimeReceive(byte[] timeReceive) {
		this.timeReceive = timeReceive;
	}
}
