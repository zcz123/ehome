package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.common.WebBrowserFragment;

/**
 * 注意：该类的WebView不支持js，若要使用支持js的Activity请使用Html5PlusWebViewActvity
 */
public class WebBrowserActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new WebBrowserFragment())
			.commit();
		}
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}

	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}

}
