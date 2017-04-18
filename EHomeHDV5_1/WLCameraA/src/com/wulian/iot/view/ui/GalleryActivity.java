package com.wulian.iot.view.ui;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.view.base.BasePage;
import com.wulian.iot.view.base.SimpleFragmentActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
public class GalleryActivity extends SimpleFragmentActivity implements OnCheckedChangeListener,OnPageChangeListener{
	private ViewPager vpGallery;
	private RadioGroup rgGallery;
	private RadioButton rbPicture,rbVideotape,rbInfrared;
	private List<BasePage> galleryPagers;
	private Context mContext = null;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private final int pictureIndex = 0;
	private final int  localVideoIndex = 1;
	private final int alarmIndex = 2;
	private GalleryPageAdapter galleryPageAdapter = null;
	public String tutkUID;
	/**初始化page*/
	private Runnable initPage = new Runnable() {
		@Override
		public void run() {
			vpGallery.setOffscreenPageLimit(3);
			galleryPagers = new ArrayList<BasePage>();
			galleryPagers.add(new GalleryPicturePage(mContext,Config.eaglePhoto+tutkUID));
//	    	galleryPagers.add(new GalleryVideotapePage(mContext,Config.eagleVideo+tutkUid));//本地视频
			galleryPagers.add(new GalleryInfraredAlarmPage(mContext,cameaHelper));//设备视频
			if(galleryPageAdapter == null){
				galleryPageAdapter = new GalleryPageAdapter(galleryPagers);
			}
			vpGallery.setAdapter(galleryPageAdapter);
			setTableRadioGroup(pictureIndex);
		}
	};
	@Override
	public void root() {
		setContentView(R.layout.activity_gallery);
		mContext = this;
	}
	@Override
	public void initView() {
		vpGallery = (ViewPager) findViewById(R.id.vp_gallery);
		rgGallery = (RadioGroup) findViewById(R.id.rg_gallery);//控件按钮载体
		rbPicture = (RadioButton) findViewById(R.id.rb_picture);
//		rbVideotape = (RadioButton) findViewById(R.id.rb_videotape);
		rbInfrared = (RadioButton) findViewById(R.id.rb_infrared_alarm);
	}
	@Override
	public void initData() {
		tutkUID=getIntent().getStringExtra(Config.tutkUid);
		mHandler.post(initPage);
//		mIntent.putExtra(, tutkUid);
	}
	@Override
	public void initEvents() {
		rgGallery.setOnCheckedChangeListener(this);//点击切换
		vpGallery.setOnPageChangeListener(this);
	}
	public void exit(View view){
		this.finish();
	}
	private class GalleryPageAdapter extends PagerAdapter {
		private List<BasePage> eList = null;
		public GalleryPageAdapter(List<BasePage> galleryPagers ){
			this.eList =galleryPagers== null?new ArrayList<BasePage>():galleryPagers;
		}
		@Override
		public int getCount() {
			return this.eList.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(this.eList.get(position).getView());
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(this.eList.get(position).getView());
			this.eList.get(position).initData();
			return this.eList.get(position).getView();
		}
	}
	public void setTableRadioGroup(int index){
		Drawable drawable = getResources().getDrawable(R.drawable.eagle_gallery_line);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		switch(index){
			case pictureIndex:
				this.pictureStyle(drawable);
				break;
			case localVideoIndex:
//			this.localVideoStyle(drawable);
				break;
			case alarmIndex:
				this.alarmVideoStyle(drawable);
				break;
		}
	}
	private void pictureStyle(Drawable drawable){
		rbPicture.setCompoundDrawables(null, null, null, drawable);
//		rbVideotape.setCompoundDrawables(null, null, null, null);
		rbInfrared.setCompoundDrawables(null, null, null, null);
		rbPicture.setTextColor(getResources().getColor(R.color.eagle_green));
//		rbVideotape.setTextColor(getResources().getColor(R.color.white));
		rbInfrared.setTextColor(getResources().getColor(R.color.white));
	}
	//	private void localVideoStyle(Drawable drawable){
//		rbPicture.setCompoundDrawables(null, null, null, null);
//		rbVideotape.setCompoundDrawables(null, null, null, drawable);
//		rbInfrared.setCompoundDrawables(null, null, null, null);
//		rbPicture.setTextColor(getResources().getColor(R.color.white));
//		rbVideotape.setTextColor(getResources().getColor(R.color.eagle_green));
//		rbInfrared.setTextColor(getResources().getColor(R.color.white));
//	}
	public void alarmVideoStyle(Drawable drawable){
		rbPicture.setCompoundDrawables(null, null, null, null);
//		rbVideotape.setCompoundDrawables(null, null, null, null);
		rbInfrared.setCompoundDrawables(null, null, null, drawable);
		rbPicture.setTextColor(getResources().getColor(R.color.white));
//		rbVideotape.setTextColor(getResources().getColor(R.color.white));
		rbInfrared.setTextColor(getResources().getColor(R.color.eagle_green));
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.i(TAG, "===onCheckedChanged===");
		int item = -1;
		if (checkedId == R.id.rb_picture) {
			item = pictureIndex;
			setTableRadioGroup(pictureIndex);
//		} else if (checkedId == R.id.rb_videotape) {
//			item = localVideoIndex;
//			setTableRadioGroup(localVideoIndex);
		} else if (checkedId == R.id.rb_infrared_alarm) {
			item = alarmIndex;
			setTableRadioGroup(alarmIndex);
		}
		vpGallery.setCurrentItem(item, false);
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		Log.e(TAG, "===onPageSelected===");
		((RadioButton) rgGallery.getChildAt(arg0)).setChecked(true);
		setTableRadioGroup(arg0);
	}
}
