package cc.wulian.app.model.device.impls.configureable.ir.xml;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.R;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;

public class ACCommand
{
	private final int mIndex;
	private final String mCmd;
	private final String mCmdDescription;

	public ACCommand( int index, String cmd, String cmdDescription )
	{
		mIndex = index;
		mCmd = cmd;
		mCmdDescription = cmdDescription;
	}

	public String getCmd() {
		return mCmd;
	}

	public String getCmdDescription() {
		return mCmdDescription;
	}

	public int getIndex() {
		return mIndex;
	}

	private static boolean checkOverRange( List<?> data, int pos ) {
		if (data == null || data.isEmpty()) return true;
		return pos < 0 || pos >= data.size();
	}

	private static abstract class AbstractACCommand
	{
		public ACCommand getCommand00( int index ) {
			List<ACCommand> commands = getACCommands();
			if (checkOverRange(commands, index)) { return null; }
			return commands.get(index);
		}

		public ACCommand getCommand00( String cmdCode ) {
			List<ACCommand> commands = getACCommands();
			ACCommand find = null;
			for (ACCommand cmd : commands) {
				if (StringUtil.equals(cmdCode, cmd.mCmd)) {
					find = cmd;
					break;
				}
			}
			return find;
		}

		public abstract List<ACCommand> getACCommands();
	}

	public static class Power extends AbstractACCommand
	{
		public static final String PREFIX = "4";
		public static final String OFF = "01";
		public static final String ON = "02";

		static List<ACCommand> mCommands = new ArrayList<ACCommand>();
		static {
			ACCommand closeCmd = new ACCommand(0, PREFIX + OFF, MainApplication.getApplication().getString(R.string.device_state_close));
			ACCommand openCmd = new ACCommand(1, PREFIX + ON, MainApplication.getApplication().getString(R.string.device_state_open));
			mCommands.add(closeCmd);
			mCommands.add(openCmd);
		}
		static Power mPower = new Power();

		@Override
		public List<ACCommand> getACCommands() {
			return mCommands;
		}

		public static ACCommand getCommand( int index ) {
			return mPower.getCommand00(index);
		}

		public static ACCommand getCommand( String cmdCode ) {
			return mPower.getCommand00(cmdCode);
		}

		public static boolean checkOverRange( int index ) {
			return ACCommand.checkOverRange(mCommands, index);
		}
	}

	public static class Mode extends AbstractACCommand
	{
		public static final String PREFIX = "5";
		public static final String AUTO = "00";
		public static final String COOL = "01";
		public static final String DEF = "02";
		public static final String FAN = "03";
		public static final String HOT = "04";

		static List<ACCommand> mCommands = new ArrayList<ACCommand>();
		static {
			ACCommand auto = new ACCommand(0, PREFIX + AUTO, MainApplication.getApplication().getString(R.string.device_ac_cmd_fan_mode_auto));
			ACCommand cool = new ACCommand(1, PREFIX + COOL, MainApplication.getApplication().getString(R.string.device_ac_cmd_refrigeration));
			ACCommand de = new ACCommand(2, PREFIX + DEF, MainApplication.getApplication().getString(R.string.device_ac_cmd_dehumidification));
			ACCommand fan = new ACCommand(3, PREFIX + FAN, MainApplication.getApplication().getString(R.string.device_ac_cmd_air_supply));
			ACCommand hot = new ACCommand(4, PREFIX + HOT,MainApplication.getApplication().getString( R.string.device_ac_cmd_heating));

			mCommands.add(auto);
			mCommands.add(cool);
			mCommands.add(de);
			mCommands.add(fan);
			mCommands.add(hot);
		}
		static Mode mMode = new Mode();

		@Override
		public List<ACCommand> getACCommands() {
			return mCommands;
		}

		public static ACCommand getCommand( int index ) {
			return mMode.getCommand00(index);
		}

		public static ACCommand getCommand( String cmdCode ) {
			return mMode.getCommand00(cmdCode);
		}

		public static boolean checkOverRange( int index ) {
			return ACCommand.checkOverRange(mCommands, index);
		}
	}

	public static class Temp extends AbstractACCommand
	{
		public static final String UNIT_C = "\u00B0C";
		public static final String PREFIX = "6";

		static List<ACCommand> mCommands = new ArrayList<ACCommand>();
		static {
			ACCommand cmd16 = new ACCommand(0, PREFIX + 16, "16\u00B0C");
			ACCommand cmd17 = new ACCommand(1, PREFIX + 17, "17\u00B0C");
			ACCommand cmd18 = new ACCommand(2, PREFIX + 18, "18\u00B0C");
			ACCommand cmd19 = new ACCommand(3, PREFIX + 19, "19\u00B0C");
			ACCommand cmd20 = new ACCommand(4, PREFIX + 20, "20\u00B0C");
			ACCommand cmd21 = new ACCommand(5, PREFIX + 21, "21\u00B0C");
			ACCommand cmd22 = new ACCommand(6, PREFIX + 22, "22\u00B0C");
			ACCommand cmd23 = new ACCommand(7, PREFIX + 23, "23\u00B0C");
			ACCommand cmd24 = new ACCommand(8, PREFIX + 24, "24\u00B0C");
			ACCommand cmd25 = new ACCommand(9, PREFIX + 25, "25\u00B0C");
			ACCommand cmd26 = new ACCommand(10, PREFIX + 26,"26\u00B0C");
			ACCommand cmd27 = new ACCommand(11, PREFIX + 27,"27\u00B0C");
			ACCommand cmd28 = new ACCommand(12, PREFIX + 28,"28\u00B0C");
			ACCommand cmd29 = new ACCommand(13, PREFIX + 29,"29\u00B0C");
			ACCommand cmd30 = new ACCommand(14, PREFIX + 30,"30\u00B0C");
			ACCommand cmd31 = new ACCommand(15, PREFIX + 31,"31\u00B0C");

			mCommands.add(cmd16);
			mCommands.add(cmd17);
			mCommands.add(cmd18);
			mCommands.add(cmd19);
			mCommands.add(cmd20);
			mCommands.add(cmd21);
			mCommands.add(cmd22);
			mCommands.add(cmd23);
			mCommands.add(cmd24);
			mCommands.add(cmd25);
			mCommands.add(cmd26);
			mCommands.add(cmd27);
			mCommands.add(cmd28);
			mCommands.add(cmd29);
			mCommands.add(cmd30);
			mCommands.add(cmd31);
		}
		static Temp mTemp = new Temp();

		@Override
		public List<ACCommand> getACCommands() {
			return mCommands;
		}

		public static ACCommand getCommand( int index ) {
			return mTemp.getCommand00(index);
		}

		public static ACCommand getCommand( String cmdCode ) {
			return mTemp.getCommand00(cmdCode);
		}

		public static boolean checkOverRange( int index ) {
			return ACCommand.checkOverRange(mCommands, index);
		}
	}

	public static class FanSpeed extends AbstractACCommand
	{
		public static final String PREFIX = "7";
		public static final String AUTO = "00";
		public static final String SPEED_1 = "01";
		public static final String SPEED_2 = "02";
		public static final String SPEED_3 = "03";

		static List<ACCommand> mCommands = new ArrayList<ACCommand>();
		static {
			ACCommand auto = new ACCommand(0, PREFIX + AUTO, MainApplication.getApplication().getString(R.string.device_ac_cmd_fan_speed_auto));
			ACCommand s1 = new ACCommand(1, PREFIX + SPEED_1, MainApplication.getApplication().getString(R.string.device_ac_cmd_first_gear));
			ACCommand s2 = new ACCommand(2, PREFIX + SPEED_2,MainApplication.getApplication().getString( R.string.device_ac_cmd_second_gear));
			ACCommand s3 = new ACCommand(3, PREFIX + SPEED_3,MainApplication.getApplication().getString( R.string.device_ac_cmd_third_gear));

			mCommands.add(auto);
			mCommands.add(s1);
			mCommands.add(s2);
			mCommands.add(s3);
		}
		static FanSpeed mFanSpeed = new FanSpeed();

		@Override
		public List<ACCommand> getACCommands() {
			return mCommands;
		}

		public static ACCommand getCommand( int index ) {
			return mFanSpeed.getCommand00(index);
		}

		public static ACCommand getCommand( String cmdCode ) {
			return mFanSpeed.getCommand00(cmdCode);
		}

		public static boolean checkOverRange( int index ) {
			return ACCommand.checkOverRange(mCommands, index);
		}
	}

	public static class FanDirection extends AbstractACCommand
	{
		public static final String PREFIX = "8";
		public static final String AUTO = "00";
		public static final String MANUAL = "01";
		static List<ACCommand> mCommands = new ArrayList<ACCommand>();
		static {
			ACCommand auto = new ACCommand(0, PREFIX + AUTO, MainApplication.getApplication().getString(R.string.device_ac_cmd_auto));
			ACCommand manual = new ACCommand(1, PREFIX + MANUAL, MainApplication.getApplication().getString(R.string.device_ac_cmd_manual));

			mCommands.add(auto);
			mCommands.add(manual);
		}
		static FanDirection mFanDirection = new FanDirection();

		@Override
		public List<ACCommand> getACCommands() {
			return mCommands;
		}

		public static ACCommand getCommand( int index ) {
			return mFanDirection.getCommand00(index);
		}

		public static ACCommand getCommand( String cmdCode ) {
			return mFanDirection.getCommand00(cmdCode);
		}

		public static boolean checkOverRange( int index ) {
			return ACCommand.checkOverRange(mCommands, index);
		}
	}
}