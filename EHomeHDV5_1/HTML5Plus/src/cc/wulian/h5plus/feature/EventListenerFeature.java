package cc.wulian.h5plus.feature;

import android.webkit.JavascriptInterface;
import cc.wulian.h5plus.view.H5PlusWebView;

/**
 * Created by Administrator on 2016/2/23 0023.
 */
public class EventListenerFeature {


	@JavascriptInterface
	public void addEventListener(H5PlusWebView webView, String data) {
		webView.registerEvent(data);
	}

	public void removeEventListener(H5PlusWebView webView, String data) {
		webView.unregisterEvent(data);
	}
	
}
