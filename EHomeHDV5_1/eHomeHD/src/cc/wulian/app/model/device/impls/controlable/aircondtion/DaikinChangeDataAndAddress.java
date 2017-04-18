package cc.wulian.app.model.device.impls.controlable.aircondtion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.util.StringUtil;

public class DaikinChangeDataAndAddress {
	/**
	 * 从机地址WL_a0_Daikin_Air_Condition中CRC校验中sendData需要修改
	 */
	public static final String SLAVE_ADRESS = "01";
	public static final String CMD_DAIKIN_QUERY_04 = SLAVE_ADRESS + "04";
	public static final String CMD_DAIKIN_CONTROL_06 = SLAVE_ADRESS + "06";
	public static final String REGISTER_NUMBER_0003 = "0003";
	public static final String REGISTER_NUMBER_0006 = "0006";
	public static final String REGISTER_NUMBER_0009 = "0009";

	private static String linkGroup[] = { "30002", "30003", "30004", "30005",
			"30006", "30007", "30008", "30009" };
	private static Map<String, int[]> performanceMap = new LinkedHashMap<String, int[]>();
	private static Map<String, int[]> statusMap = new LinkedHashMap<String, int[]>();
	private static Map<String, int[]> controlMap = new LinkedHashMap<String, int[]>();
	static {
		int address = 31000;
		for (int i = 1; i <= 4; i++) {
			for (int j = 0; j < 16; j++) {
				int[] preAddressArray = new int[3];
				for (int k = 0; k < 3; k++) {
					address = address + 1;
					preAddressArray[k] = address;
				}
				performanceMap.put(
						i + "-" + StringUtil.appendLeft("" + j, 2, '0'),
						preAddressArray);
			}
		}
	}
	static {
		int address = 32000;
		for (int i = 1; i <= 4; i++) {
			for (int j = 0; j < 16; j++) {
				int[] preAddressArray = new int[6];
				for (int k = 0; k < 6; k++) {
					address = address + 1;
					preAddressArray[k] = address;
				}
				statusMap.put(i + "-" + StringUtil.appendLeft("" + j, 2, '0'),
						preAddressArray);
			}
		}
	}
	static {
		int address = 42000;
		for (int i = 1; i <= 4; i++) {
			for (int j = 0; j < 16; j++) {
				int[] preAddressArray = new int[3];
				for (int k = 0; k < 3; k++) {
					address = address + 1;
					preAddressArray[k] = address;
				}
				controlMap.put(i + "-" + StringUtil.appendLeft("" + j, 2, '0'),
						preAddressArray);
			}
		}
	}

	/**
	 * 十六进制转成二进制
	 * 
	 * @param hexString
	 * @return 2binaryString
	 */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
							hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

	/**
	 * 16进制的字符串表示转成字节数组
	 * 
	 * @param hexString
	 *            16进制格式的字符串
	 * @return 转换后的字节数组
	 **/
	public static byte[] hexStr2ByteArray(String hexString) {
		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	/**
	 * 16进制字符串转换成byte数组
	 * 
	 * @param 16进制字符串
	 * @return 转换后的byte数组
	 */
	public static byte[] hex2Byte(String hex) {
		String digital = "0123456789ABCDEF";
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			// 其实和上面的函数是一样的 multiple 16 就是右移4位 这样就成了高4位了
			// 然后和低四位相加， 相当于 位操作"|"
			// 相加后的数字 进行 位 "&" 操作 防止负数的自动扩展. {0xff byte最大表示数}
			temp = digital.indexOf(hex2char[2 * i]) * 16;
			temp += digital.indexOf(hex2char[2 * i + 1]);
			bytes[i] = (byte) (temp & 0xff);
		}
		return bytes;
	}

	// 获取16位二进制哪位有1
	public static List<Integer> getAddressIndex(String binnaryString) {
		ArrayList<Integer> addressList = new ArrayList<Integer>();
		char[] binnarys = binnaryString.toCharArray();
		for (int i = 0; i < binnarys.length; i++) {
			if (binnarys[i] == '1') {
				addressList.add(binnarys.length - 1 - i);
			}
		}
		return addressList;
	}

	public static String getGroupName(int index) {
		if (index < linkGroup.length) {
			return linkGroup[index];
		}
		return null;
	}

	// 获取组编号
	public static int getGroupIndex(String groupName) {
		for (int i = 0; i < linkGroup.length; i++) {
			if (linkGroup[i].equals(groupName)) {
				return i + 1;
			}
		}
		return -1;
	}

	public static String getGroupCRC() {
		return "01";
	}

	// 获取三种性能地址
	public static int[] getAirPerformanceAddress(String key) {
		return performanceMap.get(key);
	}

	// 获取六种状态地址
	public static int[] getAirStatusAddress(String key) {
		return statusMap.get(key);
	}

	// 获取三种控制地址
	public static int[] getAirControlAddress(String key) {
		return controlMap.get(key);
	}

}
