package cc.wulian.smarthomev5.fragment.singin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.nineoldandroids.animation.ValueAnimator;
import com.yuantuo.customview.nineoldandroids.view.ViewPropertyAnimator;
import com.yuantuo.customview.ui.VerticalScrollView;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLProgressView;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.activity.QRScanActivity;
import cc.wulian.smarthomev5.activity.minigateway.ConfigDeviceMiniGateWayKnowWifiRelayActivity;
import cc.wulian.smarthomev5.activity.minigateway.ConfigDeviceMiniGatewayPageActivity;
import cc.wulian.smarthomev5.adapter.SigninRecordsAdapterV5;
import cc.wulian.smarthomev5.dao.SigninDao;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.WLCameraOperationManager;
import cc.wulian.smarthomev5.utils.ConstantsUtil;
import cc.wulian.smarthomev5.utils.InputMethodUtils;
import cc.wulian.smarthomev5.utils.NetworkUtil;
import cc.wulian.smarthomev5.utils.SizeUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_READABLE;

public class SigninFragmentV5 extends Fragment {

    @ViewInject(R.id.server_ip_fl)
    FrameLayout ipframeLayout;
    @ViewInject(R.id.et_gw_server_ip)
    private EditText mGwServerIpEditText;
    @ViewInject(R.id.et_gw_id)
    private EditText mGwIDEditText;

    @ViewInject(R.id.et_password)
    private EditText mGwPwdEditText;

    @ViewInject(R.id.remember_password)
    private CompoundButton mGWPwdRemember;

    @ViewInject(R.id.btn_signin)
    private Button mSigninButton;

    @ViewInject(R.id.auto_password)
    private TextView mAutoTextView;

    @ViewInject(R.id.scan_barcode)
    private CompoundButton mScanBarcodeView;

    @ViewInject(R.id.view_loading)
    private WLProgressView mLoadingView;

    @ViewInject(R.id.layout_password)
    private View mLayoutPassword;
    @ViewInject(R.id.list_view_search)
    private ListView mSearchListView;

    @ViewInject(R.id.scroll_page)
    private VerticalScrollView scrollPage;

    @ViewInject(R.id.text_view_search)
    private TextView searchText;

    @ViewInject(R.id.text_search_again)
    private View searchAgain;

    @ViewInject(R.id.handle_to_gateway)
    private View handleToGateway;

    @ViewInject(R.id.handle_to_signin)
    private View handleToSignin;

    @ViewInject(R.id.page_gateway)
    private View pageGateWay;

    @ViewInject(R.id.page_signin)
    private View pageSignin;

    @ViewInject(R.id.layout_loading_scan)
    private FrameLayout layoutLoadingView;

    @ViewInject(R.id.layout_fragment_scan)
    private FrameLayout layoutScanView;

    @ViewInject(R.id.goto_account_login_page_tv)
    private TextView gotoAccountLoginPageTv;

    private boolean isFromHtml = false; // 是否是从html跳转进来的

    private SigninRecordsAdapterV5 mSearchHistoryAdapter;
    private SwipeTouchViewListener mSwipeTouchViewListener;
    private Preference mPreference = Preference.getPreferences();
    private MainApplication mApplication;
    private AccountManager mAccountManger;
    private Future<?> searchFuture;
    private String add_device_id;
    private String varId = null;
    private SharedPreferences sp;
    private String varPwd = null;
    private Runnable searchRunnable = new Runnable() {

        @Override
        public void run() {
            mAccountManger.cacheAllGateWayInfo();
            final List<GatewayInfo> result = mAccountManger.searchGateway();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSearchHistoryAdapter.swapData(
                            mAccountManger.getHistoryGatewayInfos(), result);
                    checkSearchResult();
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MainApplication.getApplication();
        mAccountManger = AccountManager.getAccountManger();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin_v5,
                container, false);
        ViewUtils.inject(this, rootView);
        DisplayMetrics dm = SizeUtil.getScreenSize(getActivity());
        int size = Math.min(dm.heightPixels, dm.widthPixels);
        layoutLoadingView.setLayoutParams(new LinearLayout.LayoutParams(
                size / 2, size / 2));
//        ShockPasswordMainPage();
        // getFragmentManager()
        // .beginTransaction()
        // .replace(R.id.layout_fragment_scan, new QRScanFragmentV5(),
        // QRScanFragmentV5.class.getSimpleName()).commit();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        varId = getActivity().getIntent().getStringExtra(
                WelcomeActivityV5.gateWayIdTag);
        varPwd = getActivity().getIntent().getStringExtra(
                WelcomeActivityV5.gatePwdTag);
        if (!StringUtil.isNullOrEmpty(varId)
                && !StringUtil.isNullOrEmpty(varPwd)) {
            loginFromWithout();
        } else {
            initGWInfo();
        }
    }

    private void judgeDeviceMiniWiFiConfigTyle(String add_device_id, Context context) {
            Intent it = new Intent(context,
                    ConfigDeviceMiniGatewayPageActivity.class);
            it.putExtra("deviceId", add_device_id);
            startActivity(it);
    }

    private void initView() {
        mSearchHistoryAdapter = new SigninRecordsAdapterV5(getActivity());
        mSearchListView.setAdapter(mSearchHistoryAdapter);
        mGWPwdRemember.setChecked(true);
        mGWPwdRemember
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        mPreference.saveRememberChecked(isChecked,
                                mGwIDEditText.getText().toString());
                        if (!isChecked
                                && !StringUtil.isNullOrEmpty(mGwIDEditText
                                .getText().toString())) {
                            mApplication.mDataBaseHelper
                                    .deleteFromGwHistory(mGwIDEditText
                                            .getText().toString());
                            mGwPwdEditText.setText("");
                        }
                    }
                });
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        if (mApplication.getResources().getBoolean(R.bool.use_account)) {
            gotoAccountLoginPageTv.setVisibility(View.VISIBLE);
        } else {
            gotoAccountLoginPageTv.setVisibility(View.GONE);
        }
        gotoAccountLoginPageTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(SigninFragmentV5.this.getActivity(),
                        Html5PlusWebViewActvity.class);
                String uri = URLConstants.LOCAL_BASEURL
                        + "login.html?autoLoginFlag=0";
                intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
                startActivity(intent);
                getActivity().finish();
            }
        });
        mGwIDEditText.setOnFocusChangeListener(mFocusChangeListener);
        mGwPwdEditText.setOnFocusChangeListener(mFocusChangeListener);
        mGwServerIpEditText.setOnFocusChangeListener(mFocusChangeListener);
        mGwIDEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(21)});
        mGwIDEditText.addTextChangedListener(new GatewayIDWatcher(this
                .getActivity(), mGwIDEditText));

        mGwPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                mAutoTextView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isCompleteGwId(mGwIDEditText.getText().toString())) {

                    if (mGwPwdEditText.getText().toString().equals("")) {
                        mAutoTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mAutoTextView.setVisibility(View.GONE);
                }
            }
        });

        mSwipeTouchViewListener = new SwipeTouchViewListener(mLayoutPassword,
                mGWPwdRemember);
        mGwPwdEditText.setOnTouchListener(mSwipeTouchViewListener);
        mGwPwdEditText.setLongClickable(false);
        mSigninButton.setOnClickListener(mClickListener);
        mAutoTextView.setOnClickListener(mClickListener);
        mScanBarcodeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getActivity(), QRScanActivity.class);
                it.putExtra("wulianScan", "nostart");// 用于区分是谁调用QRScanActivity
                getActivity().startActivityForResult(it, 0);
            }
        });

        mSigninButton.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ipframeLayout.setVisibility(View.VISIBLE);
                return true;
            }

        });
        mSearchListView.setOnItemClickListener(mItemClickListener);
        mSearchListView.setOnItemLongClickListener(mItemLongClickListener);
        searchAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InputMethodUtils.isShow(getActivity())) {
                    InputMethodUtils.hide(getActivity());
                }
                searchGateWay();
            }
        });

        handleToGateway.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (InputMethodUtils.isShow(getActivity())) {
                    InputMethodUtils.hide(getActivity());
                }
                mGwIDEditText.clearFocus();
                mGwServerIpEditText.clearFocus();
                mGwPwdEditText.clearFocus();
                scrollPage.scrollSmoothTo(pageGateWay.getTop());
                searchGateWay();
            }
        });

        handleToSignin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InputMethodUtils.isShow(getActivity())) {

                    ViewPropertyAnimator
                            .animate(pageSignin)
                            .translationY(0)
                            .setInterpolator(
                                    new AccelerateDecelerateInterpolator())
                            .setDuration(1000).start();
                }
                searchGateWayCancle();
                scrollPage.scrollSmoothTo(0);
            }
        });

        scrollPage.setFocusable(true);
        scrollPage.setFocusableInTouchMode(true);
        scrollPage.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGwIDEditText.clearFocus();
                mGwPwdEditText.clearFocus();
                // 如果输入法显示,就隐藏
                if (InputMethodUtils.isShow(getActivity())) {
                    InputMethodUtils.hide(getActivity());
                }
                return true;
            }
        });
    }

    public void LogonEditTextAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 10);
        animator.setDuration(100);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(5);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                mGwPwdEditText.layout(curValue, curValue, curValue
                                + mGwPwdEditText.getWidth(),
                        curValue + mGwPwdEditText.getHeight());
            }
        });
        animator.start();
    }

//    private void ShockPasswordMainPage() {
//        Intent intent = getActivity().getIntent();
//        String num = intent.getStringExtra("shock");
//        if ("shock_value".equals(num)) {
//            LogonEditTextAnimator();
//        }
//    }

    // 初始化网关信息
    private void initGWInfo() {
        GatewayInfo info = initBoundGwInfo();
        if (info == null) {
            info = initLastGwInfo();
        }
        if (info == null)
            return;
        mGwIDEditText.setText(info.getGwID());
        mGwPwdEditText.setText(info.getGwPwd());

        boolean keepPass = mPreference.isRememberChecked(info.getGwID());
        boolean isAutoLogin = mPreference.isAutoLoginChecked(info.getGwID());
        mGWPwdRemember.setChecked(keepPass);
        if (isFromHtml && info.getGwPwd() != null
                && info.getGwPwd().length() > 0 || keepPass && isAutoLogin) {
            attempSignin();
        }
    }

    // 初始化用户绑定的网关信息
    private GatewayInfo initBoundGwInfo() {
        String gwId = getActivity().getIntent().getStringExtra("gwID");
        GatewayInfo boundGateWayInfo = null;
        if (gwId != null) {
            boundGateWayInfo = new GatewayInfo();
            boundGateWayInfo.setGwID(gwId);
            GatewayInfo gateWayInfo = SigninDao.getInstance().getById(
                    boundGateWayInfo);
            if (gateWayInfo != null) {
                boundGateWayInfo.setGwPwd(gateWayInfo.getGwPwd());
                isFromHtml = true;
            }
        }
        return boundGateWayInfo;
    }

    // add sunyf 外部登陆
    private void loginFromWithout() {
        if (mGwIDEditText.getText() != null && mGwPwdEditText.getText() != null) {
            mGwIDEditText.setText("");
            mGwPwdEditText.setText("");
        }
        mGwIDEditText.setText(varId);
        mGwPwdEditText.setText(MD5Util.encrypt(varPwd));
        attempSignin();
    }

    // 获取最后一次登录的网关信息
    private GatewayInfo initLastGwInfo() {
        GatewayInfo lastGateWayInfo = mAccountManger.getLastGateayInfo();
        return lastGateWayInfo;
    }

    private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

        /**
         * 是否已经改变过了
         */
        private boolean isChanged = false;

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!mGwIDEditText.isFocused() && !mGwPwdEditText.isFocused()
                    && !mGwServerIpEditText.isFocused()) {
                isChanged = false;
            } else if (!isChanged) {
                isChanged = true;
            }

            if (v == mGwPwdEditText) {
                if (!hasFocus) {
                    mAutoTextView.setVisibility(View.GONE);
                    String password = mGwPwdEditText.getText().toString();
                    if (!StringUtil.isNullOrEmpty(password)) {
                        Preference.getPreferences().saveLittlewhiteGwpwd(password);
                        mGwPwdEditText.setText(MD5Util.encrypt(password));
                        Logger.debug("password foucus:"
                                + MD5Util.encrypt(password));
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
                case R.id.btn_signin:
                    attempSignin();
                    break;
                case R.id.auto_password:
                    mGwPwdEditText.requestFocus();
                    String gwidString = mGwIDEditText.getText().toString();
                    mGwPwdEditText
                            .setText(gwidString.substring(gwidString.length() - 6));
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void attempSignin() {
        mGwIDEditText.clearFocus();
        mGwPwdEditText.clearFocus();
        // 如果输入法显示,就隐藏
        if (InputMethodUtils.isShow(getActivity())) {
            InputMethodUtils.hide(getActivity());
        }

        String rawgwID = mGwIDEditText.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(rawgwID)) {
            WLToast.showToast(getActivity(),
                    getString(R.string.login_gateway_name_not_null_hint),
                    Toast.LENGTH_SHORT);
            return;
        }
        if (rawgwID.length() < ConstantsUtil.GATEWAY_ID_LENGTH) {
            WLToast.showToast(getActivity(),
                    getString(R.string.login_name_error), Toast.LENGTH_SHORT);
            return;
        }
        final String gwPwd = mGwPwdEditText.getText().toString().trim();
        if (StringUtil.isNullOrEmpty(gwPwd)) {
            if (gwPwd.equalsIgnoreCase("null")) {
                WLToast.showToast(getActivity(),
                        getString(R.string.login_password_error),
                        Toast.LENGTH_SHORT);
                return;
            } else {
                WLToast.showToast(getActivity(),
                        getString(R.string.set_password_not_null_hint),
                        Toast.LENGTH_SHORT);
                return;
            }
        }
        final String gwID = rawgwID.substring(rawgwID.length() - 12);

        startLoading();
        mGwIDEditText.setText(gwID);
        String customIp = mGwServerIpEditText.getText().toString().trim();
        customIp = StringUtil.getIpFromString(customIp);
        ArrayList<String> ips = null;
        if (!StringUtil.isNullOrEmpty(customIp)) {
            ips = new ArrayList<String>();
            ips.add(customIp);
        }

        mAccountManger.setConnectGatewayCallbackAndActivity(
                connectGatewayCallback, this.getActivity());
        mAccountManger.connectToGateway(gwID, gwPwd, true,
                ips, Preference.ENTER_TYPE_GW);
    }

    private AccountManager.ConnectGatewayCallback connectGatewayCallback = new AccountManager.ConnectGatewayCallback() {

        @Override
        public void connectSucceed() {
            jumpToMainActivity();
            AccountManager.getAccountManger().clearConnectGatewayCallbackAndActivity(this);
            stopLoading();
            SigninFragmentV5.this.getActivity().finish();
        }

        @Override
        public void connectFailed(int reason) {
            if (reason == ResultUtil.EXC_GW_PASSWORD_WRONG ) {
                AccountManager.showResetGwPasswordDialog(SigninFragmentV5.this.getActivity());
            }
            stopLoading();
        }
    };

    /**
     * 搜索网关信息
     */
    private void searchGateWay() {

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            WLToast.showToastWithAnimation(getActivity(), getResources()
                            .getString(R.string.login_no_network_hint),
                    Toast.LENGTH_SHORT);
        }
        searchAgain.setVisibility(View.INVISIBLE);
        searchText.setVisibility(View.VISIBLE);
        searchText.setText(getResources().getString(
                R.string.login_gateway_searching_hint));
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

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            mSearchHistoryAdapter.showDeleteButton(false);
            GatewayInfo info = (GatewayInfo) mSearchHistoryAdapter
                    .getItem(position);
            if (InputMethodUtils.isShow(getActivity())) {
                ViewPropertyAnimator
                        .animate(pageSignin)
                        .translationY(0)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(1000).start();
            }
            scrollPage.scrollSmoothTo(0);
            String gwID = mGwIDEditText.getText().toString();
            if (!StringUtil.equals(gwID, info.getGwID())) {
                mGwIDEditText.setText(info.getGwID());
                mGWPwdRemember.setChecked(mPreference.isRememberChecked(info
                        .getGwID()));
                mGwPwdEditText.setText(info.getGwPwd());
            }
        }
    };

    private OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            SigninRecordsAdapterV5 adateper = (SigninRecordsAdapterV5) parent
                    .getAdapter();
            adateper.showDeleteButton(true);
            adateper.notifyDataSetChanged();
            return true;
        }
    };

    private void checkSearchResult() {

        if (mSearchHistoryAdapter.getSearchCount() <= 0) {
            searchAgain.setVisibility(View.VISIBLE);
            searchText.setVisibility(View.INVISIBLE);
        } else {
            searchAgain.setVisibility(View.INVISIBLE);
            searchText.setVisibility(View.VISIBLE);
            searchText.setText(getResources().getString(
                    R.string.login_gateway_search_list));
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

    private void jumpToMainActivity() {
        Intent intent = new Intent(this.getActivity(), MainHomeActivity.class);
        this.startActivity(intent);

    }

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
            if ((uid.length() == 12 || uid.length() == 16 || uid.length() == 18
                    || uid.length() == 20 || uid.startsWith("http://df"))) {
                mScanBarcodeView.setChecked(false);
                if (uid.startsWith("http://df")) {
                    mGwIDEditText.setText(uid.substring(uid.length() - 12));
                } else {
                    mGwIDEditText.setText(uid);
                }
                GatewayInfo gatewayInfo = mAccountManger
                        .findExistGatewayInfo(uid.substring(uid.length() - 12));
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
                WLToast.showToast(
                        getActivity(),
                        getString(R.string.login_gateway_twodimensional_code_error_hint),
                        WLToast.TOAST_SHORT);
            }
        }
    }

    private static boolean isCompleteGwId(String str) {
        boolean isCampleteGW = false;
        if (str.length() == 12 || str.length() == 16 || str.length() == 18
                || str.length() == 20) {
            isCampleteGW = true;
        }
        return isCampleteGW;
    }

    public class GatewayIDWatcher implements TextWatcher {
        private EditText edit;
        private Activity activity;
        private AccountManager mAccountManger = null;

        public GatewayIDWatcher(Activity a, EditText edit) {
            this.edit = edit;
            this.activity = a;
            mAccountManger = AccountManager.getAccountManger();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String gwString = edit.getText().toString();
            if (gwString.length() >= 21) {
                String errorMsg = null;
                errorMsg = MainApplication.getApplication().getString(
                        R.string.set_account_manager_gw_name_length_text);
                WLToast.showToastWithAnimation(activity, errorMsg,
                        Toast.LENGTH_SHORT);

                gwString = gwString.substring(0, 20);
                edit.setText(gwString);
                edit.setSelection(edit.getText().length());

            }
            if (isCompleteGwId(edit.getText().toString())) {
                gwString = edit.getText().toString();
                // 16位或20位是摄像头网关
                if ((gwString.length() == 16 || gwString.length() == 20)) {
                    GatewayInfo info = new GatewayInfo();
                    info.setGwID(gwString.substring(gwString.length() - 12));
                    mAccountManger.updateCurrentAccount(info);
                    WLCameraOperationManager.judgeAndOperateWLCameraGwId(
                            gwString, activity);
                } else if (gwString.length() == 18) {
                    if (gwString.startsWith("GWMN02")) {
                        GatewayInfo info = new GatewayInfo();
                        info.setGwID(gwString.substring(gwString.length() - 12));
                        add_device_id = gwString.toLowerCase();
                        mAccountManger.updateCurrentAccount(info);
                        mAccountManger.showRemindallocationrelayDialog(true, true,
                                add_device_id, activity);
                    }
                }

            }

        }
    }
}
