package cc.wulian.smarthomev5.account;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.nineoldandroids.view.ViewPropertyAnimator;
import com.yuantuo.customview.ui.VerticalScrollView;
import com.yuantuo.customview.ui.WLProgressView;
import com.yuantuo.customview.ui.WLToast;

import java.util.List;
import java.util.concurrent.Future;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.core.http.Result;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.sdk.user.AMSConstants;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.QRScanActivity;
import cc.wulian.smarthomev5.adapter.SigninRecordsAdapterV5;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.fragment.singin.SigninFragmentV5;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.NetworkUtil;
import cc.wulian.smarthomev5.utils.SizeUtil;

public class BindGateWayActivity extends FragmentActivity {
	private InputMethodManager imm;
	private LinearLayout leftIconAndText;
	private VerticalScrollView scrollPage;
	private FrameLayout layoutLoadingView;
	private CompoundButton mScanBarcodeView;
	private EditText mGwIDEditText, mGwPwdEditText;
	private Button bindBtn;
	private View handleToGateway, handleToSignin, pageGateWay, pageBind;
	private ListView mSearchListView;
	private SigninRecordsAdapterV5 mSearchHistoryAdapter;
	private AccountManager mAccountManger;
	private TextView searchText, mAutoTextView;
	private View searchAgain;
	private WLProgressView mLoadingView;
    private SigninFragmentV5 msignFragmentV5;
	private Future<?> searchFuture;
	private Runnable searchRunnable = new Runnable() {

		@Override
		public void run() {
			mAccountManger.cacheAllGateWayInfo();
			final List<GatewayInfo> result = mAccountManger.searchGateway();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mSearchHistoryAdapter.swapData(mAccountManger.getHistoryGatewayInfos(), result);
					checkSearchResult();
				}
			});
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.bind_gateway_layout);
		initView();
		initListener();
	}

	private void initView() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		scrollPage = (VerticalScrollView) findViewById(R.id.scroll_page);
		pageGateWay = findViewById(R.id.page_gateway);
		pageBind = findViewById(R.id.page_bind);
		bindBtn = (Button) findViewById(R.id.btn_bind);

		mAccountManger = AccountManager.getAccountManger();
		TextView leftIconText = (TextView) findViewById(R.id.common_action_bar_left_icon_text);
		leftIconText.setText(R.string.html_gw_list_title);
		TextView centerTitle = (TextView) findViewById(R.id.common_action_bar_center_title);
		centerTitle.setText(R.string.account_system_binded_gateway);
		leftIconAndText = (LinearLayout) findViewById(R.id.common_action_bar_left_icon_and_text);

		layoutLoadingView = (FrameLayout) findViewById(R.id.loading_scan_layout);
		mScanBarcodeView = (CompoundButton) findViewById(R.id.scan_barcode);

		mGwIDEditText = (EditText) findViewById(R.id.et_gw_id);
		mGwIDEditText.setFilters(new InputFilter[] { new InputFilter.AllCaps(), new InputFilter.LengthFilter(21) });
		mGwPwdEditText = (EditText) findViewById(R.id.et_password);

		searchText = (TextView) findViewById(R.id.text_view_search);
		mAutoTextView = (TextView) findViewById(R.id.auto_password);

		searchAgain = findViewById(R.id.text_search_again);

		handleToGateway = findViewById(R.id.handle_to_gateway);

		handleToSignin = findViewById(R.id.handle_to_signin);

		mSearchListView = (ListView) findViewById(R.id.list_view_search);
		// 设置扫描二维码布局大小
		DisplayMetrics dm = SizeUtil.getScreenSize(getBaseContext());
		int size = Math.min(dm.heightPixels, dm.widthPixels);
		layoutLoadingView.setLayoutParams(new LinearLayout.LayoutParams(size / 2, size / 2));

		mSearchHistoryAdapter = new SigninRecordsAdapterV5(getBaseContext());
		mSearchListView.setAdapter(mSearchHistoryAdapter);
		mLoadingView = (WLProgressView) findViewById(R.id.view_loading);
	}

	private void initListener() {
		bindBtn.setOnClickListener(mClickListener);
		mAutoTextView.setOnClickListener(mClickListener);

		mGwIDEditText.setOnFocusChangeListener(mFocusChangeListener);
		mGwPwdEditText.setOnFocusChangeListener(mFocusChangeListener);

		mSearchListView.setOnItemClickListener(mItemClickListener);
		mSearchListView.setOnItemLongClickListener(mItemLongClickListener);
		SigninFragmentV5.GatewayIDWatcher mGatewayIDWatcher = new SigninFragmentV5().new GatewayIDWatcher(this, mGwIDEditText);
		mGwIDEditText.addTextChangedListener(mGatewayIDWatcher);
		leftIconAndText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 启动扫描界面
		mScanBarcodeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.setClass(getBaseContext(), QRScanActivity.class);
				it.putExtra("wulianScan", "nostart");// 用于区分是谁调用QRScanActivity
				startActivityForResult(it, 0);
			}
		});

		handleToGateway.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
				}
				mGwIDEditText.clearFocus();
				mGwPwdEditText.clearFocus();
				scrollPage.scrollSmoothTo(pageGateWay.getTop());
				searchGateWay();
			}
		});

		searchAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
				}
				searchGateWay();
			}
		});

		handleToSignin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imm.isActive()) {

					ViewPropertyAnimator.animate(pageBind).translationY(0)
							.setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000).start();
				}
				searchGateWayCancle();
				scrollPage.scrollSmoothTo(0);
			}
		});
	}

	/**
	 * 搜索网关信息
	 */
	private void searchGateWay() {

		if (!NetworkUtil.isNetworkAvailable(getBaseContext())) {
			WLToast.showToastWithAnimation(getBaseContext(), getResources().getString(R.string.login_no_network_hint),
					Toast.LENGTH_SHORT);
		}
		searchAgain.setVisibility(View.INVISIBLE);
		searchText.setVisibility(View.VISIBLE);
		searchText.setText(getResources().getString(R.string.login_gateway_searching_hint));
		searchFuture = TaskExecutor.getInstance().execute(searchRunnable);
	}

	/**
	 * 取消搜索
	 */
	private void searchGateWayCancle() {
		if (searchFuture != null && !searchFuture.isCancelled()) {
			searchFuture.cancel(true);
			searchFuture = null;
		}
		checkSearchResult();
	}

	private void checkSearchResult() {

		if (mSearchHistoryAdapter.getSearchCount() <= 0) {
			searchAgain.setVisibility(View.VISIBLE);
			searchText.setVisibility(View.INVISIBLE);
		} else {
			searchAgain.setVisibility(View.INVISIBLE);
			searchText.setVisibility(View.VISIBLE);
			searchText.setText(getResources().getString(R.string.login_gateway_search_list));
		}
	}

	private boolean isCompleteGwId(String str) {
		boolean isCampleteGW = false;
		if (str.length() == 12 || str.length() == 16 ||str.length() == 18 || str.length() == 20) {
			isCampleteGW = true;
		}
		return isCampleteGW;
	}

	private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

		/**
		 * 是否已经改变过了
		 */
		private boolean isChanged = false;

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!mGwIDEditText.isFocused() && !mGwPwdEditText.isFocused()) {
				isChanged = false;
			} else if (!isChanged) {
				isChanged = true;
			}

			if (v == mGwPwdEditText) {
				if (!hasFocus) {
					mAutoTextView.setVisibility(View.GONE);
					String password = mGwPwdEditText.getText().toString();
					if (!StringUtil.isNullOrEmpty(password)) {
						mGwPwdEditText.setText(MD5Util.encrypt(password));
						Logger.debug("password foucus:" + MD5Util.encrypt(password));
					}
				} else {
					mGwPwdEditText.setText("");
					if (isCompleteGwId(mGwIDEditText.getText().toString())) {
						mAutoTextView.setVisibility(View.VISIBLE);
					} else {
						mAutoTextView.setVisibility(View.GONE);
					}
				}
			}
		}
	};

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_bind:
				attempBind();
				break;
			case R.id.auto_password:
				mGwPwdEditText.requestFocus();
				String gwidString = mGwIDEditText.getText().toString();
				mGwPwdEditText.setText(gwidString.substring(gwidString.length() - 6));
				break;
			}
		}
	};

	// 检查输入
	private void attempBind() {

		mGwIDEditText.clearFocus();
		mGwPwdEditText.clearFocus();
		// 如果输入法显示,就隐藏
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		}

		final String gwID = mGwIDEditText.getText().toString().trim();
		final String gwPwd = mGwPwdEditText.getText().toString().trim();
		if (mAccountManger.isSigning(gwID))
			return;
		boolean cancel = false;

		String errorMsg = null;
		if (TextUtils.isEmpty(gwID)) {
			errorMsg = getString(R.string.login_gateway_name_not_null_hint, 12);
			cancel = true;
		} else if (!(gwID.length() == 12 || gwID.length() == 16 ||gwID.length() == 18 || gwID.length() == 20)) {
			errorMsg = getString(R.string.login_name_error);
			cancel = true;
		}

		if (!cancel) {
			if (TextUtils.isEmpty(gwPwd)) {
				errorMsg = getString(R.string.set_password_not_null_hint);
				cancel = true;
			} else if (gwPwd.length() < 6) {
				errorMsg = getString(R.string.login_gateway_name_length_hint, 6);
				cancel = true;
			}
		}

		if (cancel) {
			WLToast.showToastWithAnimation(getBaseContext(), errorMsg, Toast.LENGTH_SHORT);
		} else if (!NetworkUtil.isNetworkAvailable(getBaseContext())) {
			WLToast.showToastWithAnimation(getBaseContext(), getResources().getString(R.string.login_no_network_hint),
					Toast.LENGTH_SHORT);
		} else {
			String newgwID = gwID.substring(gwID.length() - 12, gwID.length());
			mGwIDEditText.setText(newgwID);
			beginBind(newgwID, gwPwd);
		}
	}

	public void startLoading() {

		mLoadingView.setVisibility(View.VISIBLE);
		mLoadingView.play();
	}

	public void stopLoading() {
		mLoadingView.setVisibility(View.INVISIBLE);
		mLoadingView.stop();
	}

	// 开始绑定网关
	private void beginBind(final String gwID, final String gwPwd) {
		startLoading();
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				final Result result = WLUserManager.getInstance().getStub().bindDevice(gwID, gwPwd,
						AMSConstants.DEVICE_TYPE_GATEWAY, null);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						if (AMSConstants.RESULT_AMS_COMMON_SUCCESSFUL == result.status) {
							JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, "BindGatewaySuccessBlock", "success", JsUtil.OK, true);
							SmarthomeFeatureImpl.setData("Account" + gwID , gwPwd);
							finish();
						} else {
							String defaultErrorTip = BindGateWayActivity.this.getString(R.string.html_user_gateway_bind_fail);
							String tip = StatusCodeHelper.getInstance().mapping(result.status, defaultErrorTip);
							WLToast.showToastWithAnimation(getBaseContext(), tip, Toast.LENGTH_SHORT);
						}
					}
				});

			}
		});

	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mSearchHistoryAdapter.showDeleteButton(false);
			GatewayInfo info = (GatewayInfo) mSearchHistoryAdapter.getItem(position);
			if (imm.isActive()) {
				ViewPropertyAnimator.animate(pageBind).translationY(0)
						.setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000).start();
			}
			scrollPage.scrollSmoothTo(0);
			String gwID = mGwIDEditText.getText().toString();
			if (!StringUtil.equals(gwID, info.getGwID())) {
				mGwIDEditText.setText(info.getGwID());
				mGwPwdEditText.setText(info.getGwPwd());
			}
		}
	};

	private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			SigninRecordsAdapterV5 adateper = (SigninRecordsAdapterV5) parent.getAdapter();
			adateper.showDeleteButton(true);
			adateper.notifyDataSetChanged();
			return true;
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String uid;
		try {
			uid = data.getStringExtra(EditMonitorInfoFragment.RESULT_UID);
		} catch (Exception e) {
			mScanBarcodeView.setChecked(false);
			return;
		}
		if (!StringUtil.isNullOrEmpty(uid)) {
			if ((uid.length() == 12 || uid.length() == 16 || uid.length() == 18 ||uid.length() == 20 || uid.startsWith("http://df"))) {
				mScanBarcodeView.setChecked(false);
				if (uid.startsWith("http://df")) {
					mGwIDEditText.setText(uid.substring(uid.length() - 12));
				} else {
					mGwIDEditText.setText(uid);
				}
				// if(uid.length()>12){
				// mGwIDEditText.setText(StringChange.lowerWithUpper(uid).substring(uid.length()-12));
				// }
				GatewayInfo gatewayInfo = mAccountManger.findExistGatewayInfo(uid.substring(uid.length() - 12));
				if (gatewayInfo != null) {
					String pwd = gatewayInfo.getGwPwd();
					mGwPwdEditText.setText(pwd);
				} else {
					mGwPwdEditText.setText("");
					mAutoTextView.setVisibility(View.VISIBLE);
				}
			} else {
				// 网关扫码错误
				mScanBarcodeView.setChecked(false);
				WLToast.showToast(getBaseContext(), getString(R.string.login_gateway_twodimensional_code_error_hint),
						WLToast.TOAST_SHORT);
			}
		}
	}

}
