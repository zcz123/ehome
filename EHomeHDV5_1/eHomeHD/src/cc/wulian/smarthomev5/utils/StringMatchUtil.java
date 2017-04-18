package cc.wulian.smarthomev5.utils;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.DialogInterface;
import android.text.Html;
import android.widget.EditText;
import cc.wulian.ihome.wan.util.StringUtil;

public class StringMatchUtil
{
	public static boolean emailCheck( EditText info, DialogInterface dialog, String error ){
		String text = info.getText().toString().trim();
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		try{
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			if (!m.matches()){
				field.set(dialog, false);
				info.setError(Html.fromHtml("<font color=#f31961>" + error + "</font>"));
				return false;
			}
			else{
				field.set(dialog, true);
				return true;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkInputNameIsEmpty( DialogInterface dialog, EditText area_name,
			String errorInfo ){
		String name = area_name.getText().toString();
		try{
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			if (StringUtil.isNullOrEmpty(name)){
				field.set(dialog, false);
				area_name.setError(errorInfo);
				return false;
			}
			else{
				field.set(dialog, true);
				return true;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isValidIP( String ipAddress ){
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	public static final class Compare
	{
		private static int compare( String str, String target ){
			int d[][];
			int n = str.length();
			int m = target.length();
			int i; // str
			int j; // target
			char ch1; // str
			char ch2; // target
			int temp; // similar char (0 or 1)
			if (n == 0){ return m; }
			if (m == 0){ return n; }
			d = new int[n + 1][m + 1];
			for (i = 0; i <= n; i++){
				d[i][0] = i;
			}

			for (j = 0; j <= m; j++){
				d[0][j] = j;
			}

			for (i = 1; i <= n; i++){
				ch1 = str.charAt(i - 1);
				for (j = 1; j <= m; j++){
					ch2 = target.charAt(j - 1);
					if (ch1 == ch2){
						temp = 0;
					}
					else{
						temp = 1;
					}

					// left+1,top+1, left+temp get min
					d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
				}
			}
			return d[n][m];
		}

		private static int min( int one, int two, int three ){
			return (one = one < two ? one : two) < three ? one : three;
		}

		public static float getSimilarityRatio( String str, String target ){
			return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
		}
	}
}