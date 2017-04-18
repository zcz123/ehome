package cc.wulian.smarthomev5.activity;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.tools.AccountManager;

public class ScanQRCodeActivity extends EventBusActivity {

	private String uid;
	private AccountManager mAccountManger = AccountManager.getAccountManger();
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	private Button loginButton;
	private TextView scanQrcodeTextView;
	private ImageView scanQrcodeImageView;
	private String typeValueString;
	private String urlString;
	private String typeKeyString;
	private final static String CODE_KEY="wlcodetype";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_qrcode_activity_layout);
		initBar();
		goToQRScanActivity();
		loginButton = (Button) findViewById(R.id.scan_qrcode_login_bt);
		scanQrcodeTextView= (TextView) findViewById(R.id.scan_qrcode_tv);
		scanQrcodeImageView= (ImageView) findViewById(R.id.scan_qrcode_iv);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//  https://testdemo.wulian.cc:6009/tv/writeIdInfo?&rid=73338qLI05008657017pF5z098da11H1&wlcodetype=1
				if (!StringUtil.isNullOrEmpty(uid)) {
					if (typeKeyString.equals((CODE_KEY))) {
						showScanQRCodeResult(urlString);
					} else {
						Toast.makeText(ScanQRCodeActivity.this, getString(R.string.gateway_explore_scanning_qr_code_error),
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_more));
		getCompatActionBar().setTitle(getString(R.string.gateway_explore_authorization_login));
	}

	private void goToQRScanActivity() {
		Intent it = new Intent();
		it.setClass(ScanQRCodeActivity.this, QRScanActivity.class);
		it.putExtra("wulianScan", "nostart");// 用于区分是谁调用QRScanActivity
		startActivityForResult(it, 0);
	}

	/**
	 * 防止左划删除的冲突
	 */
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			if (data == null) {
				finish();
			}
			uid = data.getStringExtra(EditMonitorInfoFragment.RESULT_UID);
			if (!StringUtil.isNullOrEmpty(uid)) {
				String []uidArray=uid.split("&");
				String typeParam=uidArray[uidArray.length-1];
				String []typeArray=typeParam.split("=");
				typeKeyString=typeArray[0];
				typeValueString=typeArray[typeArray.length-1];
				if (typeKeyString.equals((CODE_KEY))) {
					String gatewayAndPassword = mAccountManger.getmCurrentInfo()
							.getGwID()
							+ "-"
							+ mAccountManger.getmCurrentInfo().getGwPwd();
					urlString= uid.substring(0,uid.length()-typeParam.length()-1);
					urlString = urlString+ "&info=" + gatewayAndPassword;
					switch (typeValueString){
						case "1":
							scanQrcodeTextView.setText(R.string.gateway_explore_authorization_pad_has_logged);
							scanQrcodeImageView.setBackgroundResource(R.drawable.scan_qrcode_pad_imagview);
							break;
						case "2":
							scanQrcodeTextView.setText(R.string.gateway_explore_authorization_tv_has_logged);
							scanQrcodeImageView.setBackgroundResource(R.drawable.scan_qrcode_tv_imagview);
							break;
					}
				} else {
					Toast.makeText(ScanQRCodeActivity.this, getString(R.string.gateway_explore_scanning_qr_code_error),
							Toast.LENGTH_LONG).show();
					this.finish();
				}
			}
		} catch (Exception e) {
			finish();
			return;
		}
	}

	private void showScanQRCodeResult(final String urlString) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpCient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(urlString);
				HttpResponse httpResponse = null;
				try {
					// 设置连接超时时间
					// httpCient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					// 20000);
					// //设置获取超时时间
					// httpCient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					// 120000);
					httpResponse = httpCient.execute(httpGet);
					HttpEntity entity = httpResponse.getEntity();
					final HttpResponse finalHttpResponse = httpResponse;
					mainHandler.post(new Runnable() {

						@Override
						public void run() {
							if (finalHttpResponse.getStatusLine()
									.getStatusCode() == 200) {
								Toast.makeText(ScanQRCodeActivity.this,
										getSuccessResult(), Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										ScanQRCodeActivity.this,
										getFailResult(),
//												+ finalHttpResponse
//														.getStatusLine()
//														.getStatusCode(),
										Toast.LENGTH_LONG).show();
							}
						}
					});
				} catch (IOException e) {
					Toast.makeText(ScanQRCodeActivity.this, getFailResult(),
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();
	}

	private String getSuccessResult(){
		switch (typeValueString){
			case "1":
				return getString(R.string.gateway_explore_pad_has_logged);
			case "2":
				return getString(R.string.gateway_explore_tv_has_logged);
		}
		return "";
	}
	private String getFailResult(){
		switch (typeValueString){
			case "1":
				return getString(R.string.gateway_explore_pad_login_failure);
			case "2":
				return getString(R.string.gateway_explore_tv_login_failure);
		}
		return "";
	}


}
