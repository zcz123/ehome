package cc.wulian.h5plus.common.client;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cc.wulian.h5plus.common.JsUtil;

/**
 * Created by Administrator on 2015/11/20.
 */
public class IWebViewClient extends WebViewClient{

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	Log.d("H5+ engine", "Loading url:" + url);
        view.loadUrl(url);
        return true;
    }

 	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		String url = "file:///android_asset/disclaimer/error_page_404_en.html";
//		if (LanguageUtil.isChina()){
//			url = "file:///android_asset/disclaimer/error_page_404_zh.html";
//		}
		view.loadUrl(url);
	}

    @Override
    public void onPageFinished(WebView view, String url) {
        if(view.getProgress()==100){
            JsUtil.getInstance().execJSFunction(view, "javascript:plus.onReady()");
        }
    }
}
