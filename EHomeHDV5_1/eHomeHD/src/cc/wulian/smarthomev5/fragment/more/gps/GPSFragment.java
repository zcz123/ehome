package cc.wulian.smarthomev5.fragment.more.gps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.UserLocation;
import cc.wulian.smarthomev5.event.GPSEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.location.LocationClient;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.Preference;

public class GPSFragment extends WulianFragment {

	private WebView mapWebView;
	private ImageView nearSceneImageView;
	private ImageView awaySceneImageView;
	private TextView nearSceneTextView;
	private TextView awaySceneTextView;

	private UserLocation mLocation;
	private LocationClient mLocationClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	/**
	 * 初始化ActionBar
	 */
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_more));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.more_gps_scene));
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(R.string.set_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.more_gps_content, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mapWebView = (WebView) view.findViewById(R.id.more_map_web_view);
		nearSceneImageView = (ImageView) view
				.findViewById(R.id.gps_near_scene_Iv);
		awaySceneImageView = (ImageView) view
				.findViewById(R.id.gps_away_scene_Iv);
		nearSceneTextView = (TextView) view
				.findViewById(R.id.gps_near_scene_tv);
		awaySceneTextView = (TextView) view
				.findViewById(R.id.gps_away_scene_tv);
		Button btn = (Button) view.findViewById(R.id.more_1);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mLocationClient = LocationClient.getInstance();
				mLocationClient.start();
			}
		});

		setWebView();
		init();
	}

	// 配置WebView
	private void setWebView() {
		// 设置支持JavaScript
		WebSettings webSettings = mapWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 设置放大缩小按钮
		webSettings.setBuiltInZoomControls(false);
		// 支持双击缩放
		webSettings.setUseWideViewPort(false);
		mapWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

	private void init() {

		Preference mPreference = Preference.getPreferences();
		mLocation = mPreference.getAdvertisementLocation();

		LatLng latLng = GPSUtils.transformFromWGSToGCJ(new LatLng(mLocation
				.getLongitude(), mLocation.getLatitude()));

		String url = Common.GAO_DE_MAP_URL + "/?dest=" + latLng.latitude + ","
				+ latLng.longitude + "&destName=1&hideRouteIcon=1&key="
				+ Common.GAO_DE_KEY;

		Log.d(getClass().getSimpleName(), url);
		mapWebView.loadUrl(url);

	}

	public void onEventMainThread(GPSEvent event) {
		Log.d(getClass().getSimpleName(), event.mLatitude + "     "
				+ event.mLongitude);
		mLocationClient.stop();

		LatLng latLng = GPSUtils.transformFromWGSToGCJ(new LatLng(
				event.mLongitude, event.mLatitude));

		// String url = Common.GAO_DE_MAP_URL + "/?dest=" + latLng.latitude +
		// ","
		// + latLng.longitude + "&destName=1&hideRouteIcon=1&key="
		// + +;

		String url = "http://m.amap.com/picker/?center=" + latLng.latitude
				+ "," + latLng.longitude + "&key=" + Common.GAO_DE_KEY;
		mapWebView.stopLoading();
		mapWebView.loadUrl(url);
	}

}
