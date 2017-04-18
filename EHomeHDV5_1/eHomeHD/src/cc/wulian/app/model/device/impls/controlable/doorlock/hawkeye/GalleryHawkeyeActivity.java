package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;

import java.util.ArrayList;
import java.util.List;

import com.wulian.iot.view.base.BasePage;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.ui.GalleryPicturePage;
import com.wulian.iot.view.ui.GalleryVideotapePage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.wulian.icam.R;

public class GalleryHawkeyeActivity extends SimpleFragmentActivity implements Callback {

	private ViewPager vpGallery;
	private RadioGroup rgGallery;
	private RadioButton rbPicture, rbVideotape, rbInfrared;
	private List<BasePage> galleryPagers;
	private ImageView ivBack;
	private Context mContext = GalleryHawkeyeActivity.this;
	private Handler mHandler = new Handler(this);

	private String tutkUid = "XGW17V7DGVW2FAV4111A";

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	public void root() {
		setContentView(R.layout.activity_hawkeye_gallery);
	}

	@Override
	public void initView() {

		vpGallery = (ViewPager) findViewById(R.id.vp_gallery_hawk);
		rgGallery = (RadioGroup) findViewById(R.id.rg_gallery_hawk);
		rbPicture = (RadioButton) findViewById(R.id.rb_picture_hawk);
		rbVideotape = (RadioButton) findViewById(R.id.rb_videotape_hawk);
		rbInfrared = (RadioButton) findViewById(R.id.rb_infrared_alarm_hawk);
		ivBack = (ImageView) findViewById(R.id.back_from_gallery_hawk);

	}

	/** 初始化page */
	private Runnable initPage = new Runnable() {
		@Override
		public void run() {
			vpGallery.setOffscreenPageLimit(3);
			galleryPagers = new ArrayList<BasePage>();
			galleryPagers.add(new GalleryPicturePage(GalleryHawkeyeActivity.this, tutkUid));
			galleryPagers.add(new GalleryVideotapePage(mContext, tutkUid)); // 这两个都可以单独使用、一个是图库
																			// 一个是视频库
																			// 本地视频库
			galleryPagers.add(new GalleryHawkEyeAlarmPage(mContext));
			vpGallery.setAdapter(new MyPageAdapter());
			setTableRadioGroup(0);
		}
	};

	private class MyPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return galleryPagers.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(galleryPagers.get(position).getView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(galleryPagers.get(position).getView());
			galleryPagers.get(position).initData();
			return galleryPagers.get(position).getView();
		}
	}

	public void initData() {

		mHandler.post(initPage);

	}

	public void initEvents() {
		rgGallery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkId) {
				int item = -1;
				if (checkId == R.id.rb_picture_hawk) {
					item = 0;
					setTableRadioGroup(0);
				} else if (checkId == R.id.rb_videotape_hawk) {
					item = 1;
					setTableRadioGroup(1);
				} else if (checkId == R.id.rb_infrared_alarm_hawk) {
					item = 2;
					setTableRadioGroup(2);
				}
				vpGallery.setCurrentItem(item, false);
			}
		});

		vpGallery.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				((RadioButton) rgGallery.getChildAt(position)).setChecked(true);
				setTableRadioGroup(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 注意这句话 可能执行错误。注意何时。
				finish();
			}
		});
	}

	public void setTableRadioGroup(int i) {
		Drawable drawable = getResources().getDrawable(R.drawable.eagle_gallery_line);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		switch (i) {
		case 0:
			rbPicture.setCompoundDrawables(null, null, null, drawable);
			rbVideotape.setCompoundDrawables(null, null, null, null);
			rbInfrared.setCompoundDrawables(null, null, null, null);

			rbPicture.setTextColor(getResources().getColor(R.color.eagle_green));
			rbVideotape.setTextColor(getResources().getColor(R.color.white));
			rbInfrared.setTextColor(getResources().getColor(R.color.white));
			break;
		case 1:
			rbPicture.setCompoundDrawables(null, null, null, null);
			rbVideotape.setCompoundDrawables(null, null, null, drawable);
			rbInfrared.setCompoundDrawables(null, null, null, null);

			rbPicture.setTextColor(getResources().getColor(R.color.white));
			rbVideotape.setTextColor(getResources().getColor(R.color.eagle_green));
			rbInfrared.setTextColor(getResources().getColor(R.color.white));
			break;
		case 2:
			rbPicture.setCompoundDrawables(null, null, null, null);
			rbVideotape.setCompoundDrawables(null, null, null, null);
			rbInfrared.setCompoundDrawables(null, null, null, drawable);

			rbPicture.setTextColor(getResources().getColor(R.color.white));
			rbVideotape.setTextColor(getResources().getColor(R.color.white));
			rbInfrared.setTextColor(getResources().getColor(R.color.eagle_green));
			break;
		}
	}

}
