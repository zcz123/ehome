package cc.wulian.app.model.device.impls.controlable.floorwarm.program;

public class ProgramBall  implements Cloneable  {

	// 横坐标  纵坐标 宽度
		public float x;
		public float y;
		
		public String temp;
		public String tempF;
		public String time;
		public String timeValue;
		public int width;
		
		public ProgramBall() {
			super();
		}

		public ProgramBall(float x, float y, int width) {
			super();
			this.x = x;
			this.y = y;
			this.width = width;
		}

		public ProgramBall(String temp, String time, int width) {
			super();
			this.temp = temp;
			this.time = time;
			this.width = width;
		}

		@Override
		public String toString() {
			return "ProgramBall [x=" + x + ", y=" + y + ", temp=" + temp + ", time=" + time + ", timeValue=" + timeValue
					+ ", width=" + width + "]";
		}
		 @Override  
		    public Object clone() throws CloneNotSupportedException  
		    {  
		        return super.clone();  
		    }  
	
}
