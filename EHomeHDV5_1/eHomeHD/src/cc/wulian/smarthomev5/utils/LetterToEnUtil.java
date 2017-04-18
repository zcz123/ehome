package cc.wulian.smarthomev5.utils;
/*
 * 将数字转化成英文，用于英文语言下门锁编号的播报
 * add by hxc 2016/8/30
 */
public class LetterToEnUtil {
	private static final String[] tensNames = { "", " ten", " twenty",
			" thirty", " forty", " fifty", " sixty", " seventy", " eighty",
			" ninety" };

	private static final String[] numNames = { "", " one", " two", " three",
			" four", " five", " six", " seven", " eight", " nine", " ten",
			" eleven", " twelve", " thirteen", " fourteen", " fifteen",
			" sixteen", " seventeen", " eighteen", " nineteen" };

	private LetterToEnUtil() {
	}

	public static String convertLessThanOneThousand(int number) {
		String soFar;

		if (number % 100 < 20) {
			soFar = numNames[number % 100];
			number /= 100;
		} else {
			soFar = numNames[number % 10];
			number /= 10;

			soFar = tensNames[number % 10] + soFar;
			number /= 10;
		}
		if (number == 0)
			return soFar;
		return numNames[number]  + soFar;
	}
}
