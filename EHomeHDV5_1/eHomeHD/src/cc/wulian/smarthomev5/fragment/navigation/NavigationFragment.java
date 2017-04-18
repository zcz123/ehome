package cc.wulian.smarthomev5.fragment.navigation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.sdk.user.entity.User;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.adapter.NavigationAdapter;
import cc.wulian.smarthomev5.fragment.about.AboutMessageFragment;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.FileDownload.FileDownloadCallBack;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.RedDotManager;
import cc.wulian.smarthomev5.tools.RedDotManager.RedDotListener;
import cc.wulian.smarthomev5.tools.configure.UserFileConfig;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLToast;

public class NavigationFragment extends WulianFragment {

	@ViewInject(R.id.nav_content)
	private GridView navContent;

	@ViewInject(R.id.gateway_name)
	private TextView gatewayNameView;
	@ViewInject(R.id.head_imageView)
	private ImageView headIcon;
	@ViewInject(R.id.nav_log_bar_ll)
	private LinearLayout logoLineLayout;
	@ViewInject(R.id.layout_user_bar)
	private LinearLayout userLineLayout;
	@ViewInject(R.id.user_name)
	private TextView userNameView;
	@ViewInject(R.id.nav_contact_us_tv)
	private TextView feedback;
	@ViewInject(R.id.nav_about_feedback_ll)
	private LinearLayout feedbackLineLayout;
	@ViewInject(R.id.nav_about_us_ll)
	private LinearLayout aboutLineLayout;
	@ViewInject(R.id.nav_contact_us_reddot_iv)
	private ImageView feedbackDotImageView;
	@ViewInject(R.id.nav_customer_service_tv)
	private TextView customerServiceTextView;
	private NavigationAdapter navAdatper;
	private SlidingMenu slidingMenu;
	private volatile boolean isShow = false;
	private List<WulianFragment> fragements = new ArrayList<WulianFragment>();
	private RedDotManager redDotManager = RedDotManager.getInstance();
	private Preference preference = Preference.getPreferences();
	private Handler handler = new Handler(Looper.getMainLooper());
	private String currentFragmentClassName;


	public NavigationAdapter getNavAdatper() {
		return navAdatper;
	}

	private RedDotListener menuLeftRedDotListener = new RedDotListener() {

		@Override
		public boolean getState() {

			boolean isContactUsRed = refreshContactUsRedDot();
			return isContactUsRed;
		}
	};
	private RedDotListener contactRedDotListener = new RedDotListener() {
		@Override
		public boolean getState() {
			return preference.getBoolean(IPreferenceKey.P_KEY_REDDOT_NAVIGATION_CONTACT_US, false);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navAdatper = new NavigationAdapter(mActivity);
		if (mActivity instanceof MainHomeActivity) {
			MainHomeActivity activity = (MainHomeActivity) mActivity;
			this.slidingMenu = activity.getmSlidingMenu();
			this.slidingMenu.setOnOpenedListener(new OnOpenedListener() {

				@Override
				public void onOpened() {
					setMenuShow(true);
					showHomeFragment();
				}
			});
			this.slidingMenu.setOnClosedListener(new OnClosedListener() {

				@Override
				public void onClosed() {
					setMenuShow(false);
				}
			});
			redDotManager.addMenuLeftDotListener(menuLeftRedDotListener);
			redDotManager.addContactUsListener(contactRedDotListener);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.layout_nav_content, container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mApplication.getResources().getBoolean(R.bool.use_about_us)
				&& mApplication.getResources().getBoolean(R.bool.use_about_us_detail)) {
			feedbackLineLayout.setVisibility(View.VISIBLE);
//			aboutLineLayout.setVisibility(View.GONE);
			feedback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goToModuleFragment(AboutMessageFragment.class.getName());
				}
			});
			customerServiceTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent= new Intent();
					intent.setClass(mActivity, Html5PlusWebViewActvity.class);
					intent.putExtra(Html5PlusWebViewActvity.KEY_URL, "file:///android_asset/customer/chatWebSocket/mainChat.html");
					startActivity(intent);
				}
			});
		} else if (mApplication.getResources().getBoolean(R.bool.use_about_us)
				&& !mApplication.getResources().getBoolean(R.bool.use_about_us_detail)) {
			feedbackLineLayout.setVisibility(View.VISIBLE);
			aboutLineLayout.setVisibility(View.VISIBLE);
			feedback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = "file:///android_asset/aboutus/about_us.html";
					IntentUtil.startCustomBrowser(mActivity, url, mApplication.getString(R.string.about_us),
							mApplication.getString(R.string.about_back));
				}
			});
		} else {
			feedbackLineLayout.setVisibility(View.INVISIBLE);
			aboutLineLayout.setVisibility(View.VISIBLE);
		}
		navContent.setAdapter(navAdatper);
		navContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				goToModuleFragment(navAdatper.getItem(position).getClassName());
			}
		});

		if (navAdatper.getCount() >= 4) {
			loadHomeFragment();
		}
		if (preference.isUseAccount()) {
			userLineLayout.setVisibility(View.VISIBLE);
			logoLineLayout.setVisibility(View.VISIBLE);
			userLineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String url = URLConstants.LOCAL_BASEURL + "personalcenter.html";
					IntentUtil.startHtml5PlusActivity(mActivity, url);
				}
			});
		} else {
			logoLineLayout.setVisibility(View.VISIBLE);
			userLineLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(preference.isUseAccount()) {
			setGatewayName();
			setUserAccount();
			downloadHeadPic();
			setHeadPic();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		RedDotManager.getInstance().removeContactUsListener(contactRedDotListener);
		RedDotManager.getInstance().removeMenuLeftDotListener(menuLeftRedDotListener);
	}

	private void setGatewayName() // 设置网关名
	{
		if (StringUtil.isNullOrEmpty(this.mAccountManger.getmCurrentInfo().getGwID())) {
			this.gatewayNameView.setText(mApplication.getResources().getString(R.string.login_not_login_gateway));
			return;
		}
		if(StringUtil.isNullOrEmpty(this.mAccountManger.getmCurrentInfo().getGwName())) {
			this.gatewayNameView.setText(mAccountManger.getmCurrentInfo().getGwID());
		} else {
			this.gatewayNameView.setText(this.mAccountManger.getmCurrentInfo().getGwName());
		}
	}

	private void setUserAccount() // 设置用户名
	{
		String username = null;
		User user = WLUserManager.getInstance().getStub().getUser();

//		if(user!= null && (StringUtil.isNullOrEmpty(user.getNick()) == false)) {
//		username = user.getNick();
		String nickName = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.NICKNAME);
		if(!StringUtil.isNullOrEmpty(nickName)) {
			username = nickName;
		} else {
			username = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.ACCOUNT, "");
		}
		if (!StringUtil.isNullOrEmpty(username)) {
			this.userNameView.setText(username);
		} else {
			this.userNameView.setText(mApplication.getResources().getString(R.string.login_no_user_name));
		}
	}

	private void setHeadPic() {
		Bitmap bm = BitmapFactory.decodeFile(UserFileConfig.getInstance().getUserFile(UserFileConfig.HEAD_ICON));
		if (bm == null) {
			headIcon.setImageResource(R.drawable.home_gateway_connected);
		}else{
			headIcon.setImageBitmap(bm);
		}
	}

	private Bitmap getHeadPicFromResource() {
		InputStream in = null;
		try {
			in = getAccountModuleResouce("img/icon120x120.png");
			return BitmapFactory.decodeStream(in);
		} catch (IOException e) {
			handleCloseResouceWhen(in, e);
		}
		return null;
	}

	public void handleCloseResouceWhen(InputStream in, IOException e) {
		Log.e("Account", "Can get head icon", e);
		if(in != null) {
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private InputStream getAccountModuleResouce (String relativeaPath) throws IOException {
		if(URLConstants.LOCAL_BASEURL.startsWith(URLConstants.ASSERTS_PREFIX)) {
			String assertFilePath = URLConstants.LOCAL_BASEURL.substring(URLConstants.ASSERTS_PREFIX.length()) + relativeaPath;
			return this.getActivity().getAssets().open(assertFilePath);
		} else {
			String absFilepath = URLConstants.LOCAL_BASEURL.substring(URLConstants.SDCARD_PREFIX.length()) + relativeaPath;
			return new FileInputStream(absFilepath);
		}
		
	}

	private FileDownloadCallBack headIconDownloadCallback = new FileDownloadCallBack() {

		@Override
		public void doWhatOnSuccess(String path) {
			try {
				final Bitmap bm = BitmapFactory.decodeFile(path);
				if (bm != null) {
					setHeadIcon(bm);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setHeadIcon(final Bitmap bm) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(bm==null){
						headIcon.setImageResource(R.drawable.home_gateway_connected);
					}else{
						headIcon.setImageBitmap(bm);
					}
				}
			});
		}

		@Override
		public void doWhatOnFailed(Exception e) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					headIcon.setImageResource(R.drawable.home_gateway_connected);
				}
			});
		}
	};
	
	// 设置用户头像
	private void downloadHeadPic() {
		TaskExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					UserFileConfig config = UserFileConfig.getInstance();
					config.downloadFileToFolder(UserFileConfig.HEAD_ICON, headIconDownloadCallback);
				}
			}
		});
	}

	public void refreshLeftMenuRedDot() {
		boolean isMenuLeftRed = redDotManager.fireMenuLeftRedDotChange();
		if (isMenuLeftRed) {
			getSupportActionBar().setIcon(R.drawable.action_bar_menu_red_dot);
		} else {
			getSupportActionBar().setIcon(R.drawable.action_bar_menu);
		}

	}

	public boolean refreshContactUsRedDot() {
		boolean isContactUsRed = redDotManager.fireContactUsRedDotChange();
		feedbackDotImageView.setSelected(isContactUsRed);
		return isContactUsRed;
	}

	public void showContent() {
		slidingMenu.showContent();
	}

	public void showMenu() {
		slidingMenu.showMenu();
	}

	public void setMenuShow(boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isShowMenu() {
		return this.isShow;
	}

	public boolean isCurrentHome() {
		return navAdatper.getItem(0).getClassName().equals(this.currentFragmentClassName);
	}

	public void showHomeFragment() {
		changeFragement(navAdatper.getItem(0).getClassName());
	}

	private void showFragment(WulianFragment selectedFragment) {
		FragmentManager manager = mActivity.getSupportFragmentManager();
		for (WulianFragment fragment : fragements) {
			if (fragment == selectedFragment) {
				manager.beginTransaction().show(fragment).commit();
				fragment.onShow();
			} else {
				manager.beginTransaction().hide(fragment).commit();
				fragment.onHide();
			}
		}
	}

	private void createFragment(FragmentManager manager, WulianFragment fragment) {
		for (WulianFragment f : fragements) {
			if (f == fragment) {
				manager.beginTransaction().show(f).commit();
			} else {
				manager.beginTransaction().hide(f).commit();
				f.onHide();
			}
		}
	}

	public void loadHomeFragment() {
		try {
			String className = navAdatper.getItem(0).getClassName();
			this.currentFragmentClassName = className;
			FragmentManager manager = mActivity.getSupportFragmentManager();
			WulianFragment fragment = (WulianFragment) Class.forName(className).newInstance();
			manager.beginTransaction().replace(R.id.modul_content_level, fragment, className).commit();
			fragements.add(fragment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 切换导航内容
	 * 
	 * @param className
	 */
	public void changeFragement(String className) {
		try {
			// add by yanzy:不允许被授权用户使用
			if (!UserRightUtil.getInstance().canOpenFragment(className)) {
				return;
			}

			this.currentFragmentClassName = className;
			FragmentManager manager = mActivity.getSupportFragmentManager();
			WulianFragment fragment = (WulianFragment) manager.findFragmentByTag(className);
			if (fragment == null) {
				fragment = (WulianFragment) Class.forName(className).newInstance();
				manager.beginTransaction().add(R.id.modul_content_level, fragment, className).commit();
				fragements.add(fragment);
				createFragment(manager, fragment);
			} else {
				showFragment(fragment);
			}
			mAccountManger.signinDefaultAccount();
		} catch (Exception e) {
			e.printStackTrace();
			WLToast.showToastWithAnimation(mActivity, mApplication.getResources()
					.getString(R.string.about_link_invalid), WLToast.TOAST_SHORT);
		}
	}

	public void goToModuleFragment(final String moduleFragmentClassName) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				changeFragement(moduleFragmentClassName);
			}
		});
		handler.postDelayed(new Runnable() {
			public void run() {
				showContent();
			}
		}, 50);
	}

	public boolean isShownFragment(String moduleFragmentClassName) {
		return StringUtil.equals(currentFragmentClassName, moduleFragmentClassName);
	}
}
