package cc.wulian.smarthomev5.fragment.about;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.AnnouncementActivity;
import cc.wulian.smarthomev5.activity.Feedback;
import cc.wulian.smarthomev5.activity.FuctionIntroduction;
import cc.wulian.smarthomev5.activity.TwoDimensionalCode;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

public class AboutMessageFragment extends WulianFragment {

	private LinearLayout contentLineLayout;
	private TextView mAboutUsWebSiteTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	private void initContentItems() {
		List<AbstractSettingItem> items = new ArrayList<AbstractSettingItem>();
		AbstractSettingItem functionItem = new AbstractSettingItem(mActivity,
				R.string.about_function_introduction) {

			@Override
			public void initSystemState() {
				super.initSystemState();
				infoImageView.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						DisplayUtil.dip2Pix(mActivity, 50));
				view.setLayoutParams(param);
				nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16 );
			}

			@Override
			public void doSomethingAboutSystem() {
				Intent intent = new Intent(mActivity, FuctionIntroduction.class);
				mActivity.startActivity(intent);
			}
		};
		functionItem.initSystemState();
		AbstractSettingItem twoDimensionalCodeItem = new AbstractSettingItem(
				mActivity, R.string.about_wechat_QR_code) {

			@Override
			public void doSomethingAboutSystem() {
				Intent intent = new Intent(mActivity, TwoDimensionalCode.class);
				mActivity.startActivity(intent);
			}

			@Override
			public void initSystemState() {
				super.initSystemState();
				infoImageView.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						DisplayUtil.dip2Pix(mActivity, 50));
				view.setLayoutParams(param);
				nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16 );
			}
		};
		twoDimensionalCodeItem.initSystemState();
		AbstractSettingItem feedBackItem = new AbstractSettingItem(mActivity,
				R.string.about_feedback) {

			@Override
			public void doSomethingAboutSystem() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.FEEDBACK)) {
					return;
				}
				
				Intent intent = new Intent(mActivity, Feedback.class);
				mActivity.startActivity(intent);
			}

			@Override
			public void initSystemState() {
				super.initSystemState();
				infoImageView.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						DisplayUtil.dip2Pix(mActivity, 50));
				view.setLayoutParams(param);
				nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16 );
			}
		};
		feedBackItem.initSystemState();
		AbstractSettingItem serviceTermsItem = new AbstractSettingItem(
				mActivity, R.string.about_service_terms) {

			@Override
			public void doSomethingAboutSystem() {
				String url = "file:///android_asset/disclaimer/disclaimer_zh_cn.html";
				if (!LanguageUtil.isChina())
					url = "file:///android_asset/disclaimer/disclaimer_en_us.html";
				String title = mApplication.getResources().getString(
						R.string.about_service_terms);
				String leftIconText = mApplication.getResources().getString(
						R.string.about_us);
				IntentUtil.startCustomBrowser(mActivity, url, title,
						leftIconText);
			}

			@Override
			public void initSystemState() {
				super.initSystemState();
				infoImageView.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						DisplayUtil.dip2Pix(mActivity, 50));
				view.setLayoutParams(param);
				nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16 );
			}
		};
		serviceTermsItem.initSystemState();
		AbstractSettingItem announcementItem = new AbstractSettingItem(
				mActivity, R.string.about_function_announcement) {

			@Override
			public void initSystemState() {
				super.initSystemState();
				infoImageView.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						DisplayUtil.dip2Pix(mActivity, 50));
				view.setLayoutParams(param);
				nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16 );
				boolean isAnnouncement = preference.getBoolean(
						IPreferenceKey.P_KEY_REDDOT_NAVIGATION_CONTACT_US,
						false);
				if (isAnnouncement) {
					iconImageView.setVisibility(View.VISIBLE);
					iconImageView.setImageResource(R.drawable.red_dot);
				} else {
					iconImageView.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void doSomethingAboutSystem() {
				Intent intent = new Intent(mActivity,
						AnnouncementActivity.class);
				mActivity.startActivity(intent);
				preference.putBoolean(
						IPreferenceKey.P_KEY_REDDOT_NAVIGATION_CONTACT_US,
						false);
				FragmentManager manager = mActivity.getSupportFragmentManager();
				Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
						.getName());
				if (fragment != null) {
					NavigationFragment navFragment = (NavigationFragment) fragment;
					navFragment.refreshLeftMenuRedDot();
				}
				iconImageView.setVisibility(View.INVISIBLE);
			}
		};
		announcementItem.initSystemState();
		items.add(functionItem);
		items.add(twoDimensionalCodeItem);
		items.add(feedBackItem);
		items.add(serviceTermsItem);
		items.add(announcementItem);
		contentLineLayout.removeAllViews();
		for (final AbstractSettingItem item : items) {
			View itemView = item.getView();
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					item.doSomethingAboutSystem();
				}
			});
			contentLineLayout.addView(itemView);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_feedback, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		contentLineLayout = (LinearLayout) view
				.findViewById(R.id.about_content_ll);
		mAboutUsWebSiteTextView = (TextView) view
				.findViewById(R.id.about_us_web_site_tv);
		mAboutUsWebSiteTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = mApplication.getResources().getString(
						R.string.about_more_understand);
				String leftIconText = mApplication.getResources().getString(
						R.string.about_us);
				IntentUtil.startCustomBrowser(mActivity, URLConstants.URL_WULIAN, title,
						leftIconText);
			}
		});
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setTitle(R.string.about_us);
	}

	@Override
	public void onResume() {
		super.onResume();
		initContentItems();
	}
	@Override
	public void onShow() {
		super.onShow();
		initBar();
		initContentItems();
	}
}
