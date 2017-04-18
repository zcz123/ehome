package cc.wulian.smarthomev5.entity;

public class HouseKeeperEntity {

	public String time;
	public String weekDay;
	
	public HouseKeeperEntity()
	{
		time = "";
		weekDay = "";
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
	
	
}
