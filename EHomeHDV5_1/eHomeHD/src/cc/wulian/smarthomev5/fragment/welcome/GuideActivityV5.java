package cc.wulian.smarthomev5.fragment.welcome;

import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.activity.SigninActivityV5;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

public class GuideActivityV5 extends FragmentActivity {
	private final List<View> guideViews = Lists.newArrayList();
	private Preference preference = Preference.getPreferences();
	private boolean isRemberPassword= false;
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivity();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.guide_content);
		initGuideViews();
		ViewPager guideViewPager = (ViewPager) this
				.findViewById(R.id.home_viewPager);
		guideViewPager.setAdapter(new GuidePagerAdapter(guideViews));
		String defaultGwID = preference.getLastSigninID();
		isRemberPassword = preference.isAutoLoginChecked(defaultGwID)
				&& preference.isRememberChecked(defaultGwID);
	}
	private void startActivity() {
		preference.readWelcomeGuide();
		if (getApplication().getResources().getBoolean( R.bool.use_account)) {
			Intent intent = new Intent(GuideActivityV5.this, Html5PlusWebViewActvity.class);
			String uri = URLConstants.LOCAL_BASEURL + "login.html";
			intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
			startActivity(intent);
		} else if (isRemberPassword) {
			startActivity(new Intent(GuideActivityV5.this, MainHomeActivity.class));
		} else {
			startActivity(new Intent(GuideActivityV5.this, SigninActivityV5.class));
		}
		this.finish();
	}
	private void initGuideViews() {
		LayoutInflater inflater = getLayoutInflater();
		FrameLayout guide1 = (FrameLayout)inflater.inflate(R.layout.guide_ui_1, null);
		FrameLayout guide2 = (FrameLayout)inflater.inflate(R.layout.guide_ui_2, null);
		FrameLayout guide3 = (FrameLayout)inflater.inflate(R.layout.guide_ui_3, null);
		TextView guide1TextView = (TextView)guide1.findViewById(R.id.guide_1_tv);
		guide1TextView.setOnClickListener(listener);
		TextView guide2TextView = (TextView)guide2.findViewById(R.id.guide_2_tv);
		guide2TextView.setOnClickListener(listener);
		Button guide3Btn = (Button)guide3.findViewById(R.id.guide_3_btn);
		guide3Btn.setOnClickListener(listener);
		if (!LanguageUtil.isChina()) {
			guide1.setBackgroundResource(R.drawable.guide_1_english);
			guide2.setBackgroundResource(R.drawable.guide_2_english);
			guide3.setBackgroundResource(R.drawable.guide_3_english);

		}
		guideViews.add(guide1);
		guideViews.add(guide2);
		guideViews.add(guide3);
	}
	private class GuidePagerAdapter extends PagerAdapter {

		private final List<View> viewList;

		public GuidePagerAdapter(List<View> list) {
			this.viewList = list;
		}

		@Override
		public int getCount() {

			return viewList != null ? viewList.size() : 0;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position), 0);
			return viewList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewList.get(position));
		}

	}
}
