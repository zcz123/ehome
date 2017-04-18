package cc.wulian.smarthomev5.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.more.shake.ShakeManager;
import cc.wulian.smarthomev5.fragment.more.shake.ShakeManager.ShakeListener;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yuantuo.customview.ui.ScreenSize;


public abstract class BaseActivity extends SherlockFragmentActivity implements
        H5PlusWebViewContainer {
    public static int MIN_WIDTH;
    public static int MIN_HEIGHT;
    protected MainApplication mApplication = MainApplication.getApplication();
    // have to set when stop activity
    private ActionBarCompat mActionBarCompat;
    private GestureDetector mGestureDetector;
    protected AccountManager mAccountManager = AccountManager
            .getAccountManger();
    private ShakeManager shakeManager = ShakeManager.getInstance();
    private ShakeListener shakeListener = new ShakeListener() {

        @Override
        public void onShake() {
            shakeManager.executeShake(BaseActivity.this);
        }
    };
    protected int isWebview = 0;
    protected Window window;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // String theme = app.mPreference.getThemeID();
        ScreenSize.getScreenSize(this);
        MIN_WIDTH = ScreenSize.screenWidth / 4;
        MIN_HEIGHT = ScreenSize.screenHeight / 6;
        mApplication.pushActivity(this);
        mApplication.mCurrentActivity = this;
        initGestureDetector();
        setImmerse();
    }

    public void setImmerse() {
        //		 沉浸暂不使用，华为手机不支持
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(this, this.getResources().getColor(R.color.action_bar_bg));
//            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView();
            decorView.addView(statusView);
//            window.setStatusBarColor( this.getResources().getColor(R.color.action_bar_bg));
        }
    }

    protected View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(this, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                float width = e2.getX() - e1.getX();
                float height = e2.getY() - e1.getY();
                Logger.debug("minWidth" + MIN_WIDTH + ";width" + width + ";"
                        + "minHeight" + MIN_HEIGHT + ";height"
                        + Math.abs(height));
                if (width > MIN_WIDTH && Math.abs(height) < MIN_HEIGHT) {
                    if (e1.getX() < ScreenSize.screenWidth / 20) {
                        return fingerRightFromLeft();
                    } else {
                        return fingerRightFromCenter();
                    }
                } else if (width < -MIN_WIDTH) {
                    return fingerLeft();
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
    }

    // @SuppressLint("NewApi") public static void setLopStatBar(Activity
    // activity, int color) {
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    // Window window = activity.getWindow();
    // window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    // | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    // window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    // | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    // window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    // window.setStatusBarColor(activity.getResources().getColor(color));
    // window.setNavigationBarColor(Color.TRANSPARENT);
    // }
    // }

    @Override
    protected void onResume() {
        super.onResume();
        if (finshSelf()) {
            this.finish();
            return;
        }
        Engine.onContainerResume(this);
        shakeManager.beginListenShake(shakeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeManager.cancelListenShake();
    }

    @Override
    protected void onDestroy() {
        mApplication.removeActivity(this);
        Engine.destroyPager(this);
        super.onDestroy();
    }

    public AccountManager getAccountManager() {
        return mAccountManager;
    }

    public void JumpTo(Class<? extends Activity> clazz) {
        JumpTo(clazz, null);
    }

    public void JumpTo(Class<? extends Activity> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    public void JumpTo(String action) {
        JumpTo(action, null);
    }

    public void JumpTo(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    public void JumpForResult(Class<? extends Activity> clazz, int requestCode,
                              Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    public final void JumpForFragmentResult(Fragment fragment,
                                            Class<? extends Activity> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityFromFragment(fragment, intent, requestCode);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    /*
     * public void onChangeTheme( int themeID ){ setTheme(themeID); // recreate
     * since api-version 11(3.0) // so we use it instead finish();
     * JumpTo(this.getClass(), getIntent().getExtras()); }
     */
    public ActionBarCompat getCompatActionBar() {
        if (mActionBarCompat == null)
            mActionBarCompat = new ActionBarCompat(this, getSupportActionBar());
        return mActionBarCompat;
    }

    public void resetActionMenu() {
        getCompatActionBar().setDisplayIconEnabled(true);
        getCompatActionBar().setDisplayIconTextEnabled(true);
        getCompatActionBar().setDisplayShowTitleEnabled(true);
        getCompatActionBar().setDisplayShowMenuEnabled(false);
        getCompatActionBar().setDisplayShowMenuTextEnabled(false);
        getCompatActionBar().setDisplayShowCustomMenuEnable(false);
        getCompatActionBar().setIconText("");
        getCompatActionBar().setIcon(R.drawable.action_bar_menu);
        getCompatActionBar().setTitle(R.string.nav_home_title);
        getCompatActionBar().setRightMenuClickListener(null);
        getCompatActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources()
                        .getColor(R.color.action_bar_bg)));
        getSupportActionBar().show();
    }

    protected boolean finshSelf() {
        boolean result = true;
        if (!mAccountManager.isConnectedGW()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean fingerRightFromLeft() {
        this.finish();
        return true;
    }

    public boolean fingerRightFromCenter() {
        return fingerRightFromLeft();
    }

    public boolean fingerLeft() {
        return true;
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
        return (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
    }

}