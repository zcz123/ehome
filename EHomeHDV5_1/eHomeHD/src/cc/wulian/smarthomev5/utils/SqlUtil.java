package cc.wulian.smarthomev5.utils;

public class SqlUtil
{
	public static String convertArr2SqlArr( String[] stringArray ){
		String replaceDevType = "";
		for (int i = 0; i < stringArray.length; i++){
			if (i == stringArray.length - 1){
				replaceDevType += "'" + stringArray[i] + "'";
			}
			else{
				replaceDevType += "'" + stringArray[i] + "',";
			}
		}
		return replaceDevType;
	}
}
