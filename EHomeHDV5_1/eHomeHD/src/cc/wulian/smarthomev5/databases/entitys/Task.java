package cc.wulian.smarthomev5.databases.entitys;

import android.support.v4.database.DatabaseUtilsCompat;

public class Task
{
	public final static String GW_ID = "T_GW_ID";
	public final static String SCENE_ID = "T_SCENE_ID";
	public final static String DEV_ID = "T_DEV_ID";
	public final static String DEV_TYPE = "T_DEV_TYPE";
	public final static String EP = "T_DEV_EP";
	public final static String EP_TYPE = "T_DEV_EP_TYPE";
	public final static String EP_DATA = "T_EP_DATA";
	public final static String CONTENT_ID = "T_CONTENT_ID";
	public final static String STATUS = "T_STATUS";
	public final static String AVAILABLE = "T_AVAILABLE";
	public final static String ADD_TIME = "T_ADD_TIME";
	public final static String UP_TIME = "T_UP_TIME";
	public final static String DEL_TIME = "T_DEL_TIME";
	
	
	public static final int POS_DEV_ID = 0;
	public static final int POS_GW_ID = 1;
	public static final int POS_SCENE_ID = 2;
	public static final int POS_DEV_TYPE = 3;
	public static final int POS_EP = 4;
	public static final int POS_EP_TYPE_APPEND = 5;     //later added so append "_APPEND"

	/**
	 * every type task has columns
	 */
	private static final String[] PROJECTION_TASK_BASE = {
		EP_TYPE,
		EP_DATA,
		CONTENT_ID,
		STATUS,
		AVAILABLE
	};
	
	public static final int POS_EP_TYPE = 0;
	public static final int POS_EP_DATA = 1;
	public static final int POS_CONTENT_ID = 2;
	public static final int POS_STATUS = 3;
	public static final int POS_AVAILABLE = 4;

	/**
	 * Task Auto
	 */
	public static class Auto extends Task
	{
		public static final String TABLE_AUTO = "T_AUTO";

		public final static String SENSOR_ID = "T_SENSOR_ID";
		public final static String SENSOR_EP = "T_SENSOR_EP";
		public final static String SENSOR_TYPE = "T_SENSOR_TYPE";
		public final static String SENSOR_NAME = "T_SENSOR_NAME";
		public final static String SENSOR_COND = "T_SENSOR_COND";
		public final static String SENSOR_DATA = "T_SENSOR_DATA";
		public final static String DELAY = "T_DELAY";
		public final static String FORWARD = "T_FORWARD";

		private static final String[] PROJECTION = {
			SENSOR_ID,
			SENSOR_EP,
			SENSOR_TYPE,
			SENSOR_NAME,
			SENSOR_COND,
			SENSOR_DATA,
			DELAY,
			FORWARD
		};

		public static final String[] PROJECTION_AUTO = DatabaseUtilsCompat.appendSelectionArgs(
				PROJECTION_TASK_BASE, PROJECTION);

		public static final int POS_SENSOR_ID = 5;
		public static final int POS_SENSOR_EP = 6;
		public static final int POS_SENSOR_TYPE = 7;
		public static final int POS_SENSOR_NAME = 8;
		public static final int POS_SENSOR_COND = 9;
		public static final int POS_SENSOR_DATA = 10;
		public static final int POS_DELAY = 11;
		public static final int POS_FORWARD = 12;
	}

	/**
	 * Task Timer
	 */
	public static class Timer extends Task
	{
		public static final String TABLE_TIMER = "T_TIMER";

		public final static String TIME = "T_TIME";
		public final static String WEEKDAY = "T_WEEKDAY";

		private static final String[] PROJECTION = {
			TIME,
			WEEKDAY
		};

		public static final String[] PROJECTION_TIMER = DatabaseUtilsCompat.appendSelectionArgs(
				PROJECTION_TASK_BASE, PROJECTION);

		public static final int POS_TIME = 5;
		public static final int POS_WEEKDAY = 6;
	}
}