package cc.wulian.smarthomev5.utils;


public class StringChange {
	//大写与小写之间的转换
	public static String lowerWithUpper(String src) {
		char[] array = src.toCharArray();
		int temp = 0;
		for (int i = 0; i < array.length; i++) {
			temp = (int) array[i];
			if (temp <= 90 && temp >= 65) { // array[i]为大写字母
				array[i] = (char) (temp + 32);
			} else if (temp <= 122 && temp >= 97) { // array[i]为小写字母
				array[i] = (char) (temp - 32);
			}
		}
		return String.valueOf(array);
	}
}
