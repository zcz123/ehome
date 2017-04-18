package cc.wulian.smarthomev5.service.html5plus.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yuantuo.netsdk.TKCamHelper;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.IActivityCallerWithResult;
import cc.wulian.smarthomev5.utils.URLConstants;

public class Html5PlusWebViewActvity extends Activity implements IActivityCallerWithResult, H5PlusWebViewContainer {

	private IOnActivityResultCallback activityResultCallback;
	private H5PlusWebView webview;
	public static final String KEY_URL = "KEY_URL";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		String url = getIntent().getExtras().getString(KEY_URL);
		this.setContentView(R.layout.html5plus_single_view);
		webview = (H5PlusWebView)findViewById(R.id.html5plus_view);
		
		Engine.bindWebviewToContainer(this, webview);
		webview.loadUrl(url);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	@Override
	public void onResume() {
		Engine.onContainerResume(this);
        if ((getIntent().getExtras().getString(KEY_URL).startsWith(URLConstants.LOCAL_BASEURL +"login.html")) || (getIntent().getExtras().getString(KEY_URL).startsWith(URLConstants.LOCAL_BASEURL +"gatewayList.html"))){
            initIOTSDK();
        }
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		webview = null;
		Engine.destroyPager(this);
		if ((URLConstants.LOCAL_BASEURL +"login.html?autoLoginFlag=0").equals(getIntent().getExtras().getString(KEY_URL)) || (URLConstants.LOCAL_BASEURL +"gatewayList.html?action=controlCenter").equals(getIntent().getExtras().getString(KEY_URL))){
			uninitIOTSDK();
		}
//		if(webViewOnDestory!=null){
//			webViewOnDestory.OnDestory();
//		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (activityResultCallback != null) {
			activityResultCallback.doWhatOnActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void setOnActivityResultCallback(IOnActivityResultCallback callBack) {
		activityResultCallback = callBack;
	}

	@Override
	public Context getMyContext() {
		return this;
	}

	@Override
	public void myStartActivityForResult(Intent intent, int requestCode) {
		this.startActivityForResult(intent, requestCode);
	}

	@Override
	public void addH5PlusWebView(H5PlusWebView webview) {
		
	}


	@Override
	public void destroyContainer() {
		this.finish();
		
	}

	@Override
	public ViewGroup getContainerRootView() {
		// TODO Auto-generated method stub
		return  (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
	}

	private void initIOTSDK() {
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				TKCamHelper.init();
			}
		});
	}

	private void uninitIOTSDK() {
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				TKCamHelper.uninit();
			}
		});
	}

//	public interface WebView_OnDestory{
//		void OnDestory();
//	}
//	public static 	WebView_OnDestory webViewOnDestory;

}
