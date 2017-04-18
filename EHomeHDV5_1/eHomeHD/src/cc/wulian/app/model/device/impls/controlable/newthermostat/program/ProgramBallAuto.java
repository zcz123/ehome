package cc.wulian.app.model.device.impls.controlable.newthermostat.program;

public class ProgramBallAuto implements Cloneable {

	// 横坐标  纵坐标 宽度
	public float x;
	public float yHeat;
	public float yCool;
	
	public String tempHeat;
	public String tempCool;
	public String tempHeatF;
	public String tempCoolF;
	public String time;
	public String timeValue;
	public int width;
	
	public ProgramBallAuto() {
		super();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}



	public ProgramBallAuto(float x, float yHeat, float yCool, int width) {
		super();
		this.x = x;
		this.yHeat = yHeat;
		this.yCool = yCool;
		this.width = width;
	}

	public ProgramBallAuto(String tempCool, String tempHeat, String time, int width) {
		super();
		this.tempCool = tempCool;
		this.tempHeat = tempHeat;
		this.time = time;
		this.width = width;
	}

	@Override
	public String toString() {
		return "ProgramBallAuto [x=" + x + ", yHeat=" + yHeat + ", yCool=" + yCool + ", tempHeat=" + tempHeat
				+ ", tempCool=" + tempCool + ", time=" + time + ", timeValue=" + timeValue + ", width=" + width + "]";
	}

}
