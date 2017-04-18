package cc.wulian.smarthomev5.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cc.wulian.smarthomev5.activity.Html5PlusWebViewV2Activity;
import cc.wulian.smarthomev5.activity.WebBrowserActivity;
import cc.wulian.smarthomev5.fragment.common.Html5PlusWebViewV2Fragment;
import cc.wulian.smarthomev5.fragment.common.WebBrowserFragment;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.item.AbstractViewItem;

public class IntentUtil {

	public static void startBrowser(Context context,String url){
		try{
			Intent intent = new Intent();
	        intent.setAction("android.intent.action.VIEW");
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        Uri content_url = Uri.parse(url);
	        intent.setData(content_url);
	        context.startActivity(intent);
		}catch(Exception e){
			
		}
	}
	public static void startInstallAPK(Context context,String path){
		try{
		    Intent intent = new Intent();
	        intent.setAction(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive"); 
	        context.startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());

			}catch(Exception e){
				
			}
	}
	public static void startCustomBrowser(Context context,String url,String title,String leftIconText){
		Intent intent = new Intent(context, WebBrowserActivity.class);
		intent.putExtra(WebBrowserFragment.TITLE,title );
		intent.putExtra(WebBrowserFragment.LEFT_ICON_TEXT, leftIconText);
		intent.putExtra(WebBrowserFragment.URL, url);
		//如果没有这一句，有时候会报如下错误：
		/*Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag.
		Is this really what you want?*/
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	/** webView在中间
	 * 开启H5 activity style 为没有标题栏
	 * @param context
	 * @param url
	 */
	public static void startHtml5PlusActivity(Context context,String url){
		Intent it = new Intent();
		it.setClass(context, Html5PlusWebViewActvity.class);
		it.putExtra(Html5PlusWebViewActvity.KEY_URL, url);
		context.startActivity(it);
	}
	/** webView在中间
	 * 开启H5 activity style 为有标题栏的
	 * @param context
	 * @param url
	 * @param title 标题
	 * @param leftIconText 左边的文字
	 */
	public static void startHtml5PlusActivity(Context context,String url,String title,String leftIconText){
		Intent it = new Intent();
		it.setClass(context, Html5PlusWebViewV2Activity.class);
		it.putExtra(Html5PlusWebViewV2Fragment.URL, url);
		it.putExtra(Html5PlusWebViewV2Fragment.TITLE,title);
		it.putExtra(Html5PlusWebViewV2Fragment.LEFT_ICON_TEXT,leftIconText);
		context.startActivity(it);
	}

	public static void sendMessage(Context context,String message){
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
		sendIntent.putExtra("sms_body", message);
	    context.startActivity(sendIntent);
	}

}
