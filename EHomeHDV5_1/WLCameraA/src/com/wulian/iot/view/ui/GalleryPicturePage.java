package com.wulian.iot.view.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.R.bool;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.wulian.icam.R;
import com.wulian.iot.bean.GalleryInfo;
import com.wulian.iot.bean.GalleryItemInfo;
import com.wulian.iot.bean.GalleryRoute;
import com.wulian.iot.server.controller.IEagleGallery;
import com.wulian.iot.server.controller.logic.EagleGalleryLogicImpl;
import com.wulian.iot.view.adapter.GalleryViewAdapter;
import com.wulian.iot.view.base.BasePage;
import com.wulian.iot.widght.DatePickerPopWindow;
import com.wulian.iot.widght.NoScrollGridView;
/**
 * @author syf
 */
public class GalleryPicturePage extends BasePage implements OnClickListener {
	private final static String TAG = "GalleryPicturePage";
	private LinearLayout linBom;
	private LinearLayout ckallLayout,delLayout,renewLayout;
	private ListView galleryListView;
	private ProgressBar pbLoad;
	private String folder = null;// 文件路径对应猫眼uid
	private View root = null;
	private IEagleGallery iEagleGalleryImpl = null;
	private DatePickerPopWindow datePickerPopWindow = null;
	private ImageView showPopuDateImg;
	private GalleryViewAdapter galleryViewAdapter = null;
	private List<GalleryInfo> galleryInfos = null;
	private List<GalleryRoute> delGallery = null;
	private GalleryRoute galleryRoute = null;
	private Handler runOnUiThread  = new Handler(Looper.getMainLooper());
	public GalleryPicturePage(Context mContext, String folder) {
		super(mContext);
		this.folder = folder;
		Log.e(TAG, "content");
	}
	@Override
	public View initView() {
		Log.e(TAG, "initView");
		root = View.inflate(context, R.layout.page_gallery_picture, null);
		galleryListView = (ListView) root.findViewById(R.id.lv_gallery);
		linBom = (LinearLayout) root.findViewById(R.id.lin_gallery_bottom);
		pbLoad = (ProgressBar) root.findViewById(R.id.gallery_progress);
		ckallLayout  = (LinearLayout)root.findViewById(R.id.ck_all_galleryitem_layout);
		delLayout  = (LinearLayout)root.findViewById(R.id.del_galleryitem_layout);
		renewLayout  = (LinearLayout)root.findViewById(R.id.rennew_galleryitem_layout);
		showPopuDateImg = (ImageView) root.findViewById(R.id.iv_alarm_date);
		return root;
	}
	@Override
	public void onClick(View v) {
		if(v == ckallLayout){
			GalleryPicturePage.this.check_all(true);
		} else if(v == delLayout){
			GalleryPicturePage.this.delete_all();
		} else if(v == renewLayout){
			GalleryPicturePage.this.select_all(false);
			GalleryPicturePage.this.check_all(false);
		} else if(v == showPopuDateImg){
			GalleryPicturePage.this.showDatePicker(v);
		}
	}
	private void settingVisibility(View view, boolean visibility) {
		if(visibility){
			view.setVisibility(View.VISIBLE);
		}else {
			view.setVisibility(View.GONE);
		}
	}
	@Override
	public void initEvents() {
		Log.e(TAG, "initEvents");
		ckallLayout.setOnClickListener(this);
		delLayout.setOnClickListener(this);
		renewLayout.setOnClickListener(this);
		showPopuDateImg.setOnClickListener(this);
	}
	@Override
	public void initData() {
		Log.e(TAG, "initData");
		showGallery("NULL");
	}
	@Override
	public void showGallery(String date) {
		Log.e(TAG, folder);
		this.bindGallery();
		new GalleryAsyncTask().execute(date);
	}
	private void delete_all(){
		runOnUiThread.post(delRunnable);
	}
	private void select_all(boolean selected){
		for(int var1 =0;var1<galleryViewAdapter.getCount();var1++){
			for(int var2 = 0;var2<galleryViewAdapter.getItem(var1).getGalleryItemInfos().size();var2++){
				galleryViewAdapter.getItem(var1).getGalleryItemInfos().get(var2).setSelectd(selected);
			}
		}
		settingVisibility(linBom,selected);
		galleryViewAdapter.notifyDataSetChanged();
	}
	private void check_all(boolean selected){
		for(int var1 =0;var1<galleryViewAdapter.getCount();var1++){
			for(int var2 = 0;var2<galleryViewAdapter.getItem(var1).getGalleryItemInfos().size();var2++){
				galleryViewAdapter.getItem(var1).getGalleryItemInfos().get(var2).setCheck(selected);
			}
		}
		galleryViewAdapter.notifyDataSetChanged();
	}
	private void bindGallery(){
		runOnUiThread.post(bindGalleryRunnable);
	}
	private Runnable bindGalleryRunnable = new Runnable() {
		@Override
		public void run() {
			if(galleryViewAdapter == null){
				galleryViewAdapter = new GalleryViewAdapter(context, null){
					@Override
					public void Listener(NoScrollGridView noScrollGridView,final GalleryItemAdapter galleryItemAdapter ) {
						noScrollGridView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
								GalleryItemInfo itemInfo  =   galleryItemAdapter.getItem(position);
								if(itemInfo.isSelectd()){
									if(itemInfo.isCheck()){
										galleryItemAdapter.getItem(position).setCheck(false);
									} else {
										galleryItemAdapter.getItem(position).setCheck(true);
									}
									galleryItemAdapter.notifyDataSetChanged();
								} else {
									showPictureDialog(itemInfo.getBitmap(),itemInfo.getItemName().substring(0, itemInfo.getItemName().lastIndexOf(".")));
								}
							}
						});
						noScrollGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
							@Override
							public boolean onItemLongClick(AdapterView<?> parent,View view, int position, long id) {
								GalleryPicturePage.this.select_all(true);
								return true;
							}
						});
					}
				};
				galleryListView.setAdapter(galleryViewAdapter);
			}
		}
	};
	private Runnable delRunnable = new Runnable() {
		@Override
		public void run() {
			delGallery = new ArrayList<GalleryRoute>();
			for(int var1 =0;var1<galleryViewAdapter.getCount();var1++){
				for(int var2 = 0;var2<galleryViewAdapter.getItem(var1).getGalleryItemInfos().size();var2++){
					GalleryItemInfo gItemInfo = galleryViewAdapter.getItem(var1).getGalleryItemInfos().get(var2);
					if(gItemInfo.isCheck()){
						galleryRoute = new GalleryRoute();
						galleryRoute.setFilePath(folder+"/"+galleryViewAdapter.getItem(var1).getFilename());
						galleryRoute.setFileName(gItemInfo.getItemName());
						delGallery.add(galleryRoute);
					}
				}
			}
			if(delGallery.size()>0){
				for(GalleryRoute obj:delGallery){
					iEagleGalleryImpl.delFileByWay(obj);
				}
				showGallery("NULL");
				settingVisibility(linBom,false);
				return;
			}
		}
	};
	private class GalleryAsyncTask extends
			AsyncTask<String, Void, List<GalleryInfo>> {
		@Override
		protected void onPreExecute() {
			settingVisibility(pbLoad, true);
		}
		@Override
		protected void onPostExecute(List<GalleryInfo> result) {
			galleryViewAdapter.swapData(result);
			settingVisibility(pbLoad, false);
		}
		@Override
		protected List<GalleryInfo> doInBackground(String... params) {
			String date = null;
			galleryInfos = new ArrayList<GalleryInfo>();
			if (iEagleGalleryImpl == null) {
				iEagleGalleryImpl = new EagleGalleryLogicImpl();
			}
			if ((date = params[0]).equals("NULL")) {
				date = null;
			}
			String[] files = iEagleGalleryImpl.findGalleryFilesByFolder(folder,date);
			if (files != null) {
				for (String obj : files) {
					galleryInfos.addAll(iEagleGalleryImpl
							.findGalleryItemByFileName(folder, obj));
				}
			}
			return galleryInfos;
		}
	}
	private void showDatePicker(View view) {
		if (datePickerPopWindow == null) {
			datePickerPopWindow = new DatePickerPopWindow(context) {
				@Override
				public void callBackData(String date) {
					Log.i(TAG,date);
					datePickerPopWindow.dismiss();
					GalleryPicturePage.this.showGallery(date);
				}
			};
		}
		datePickerPopWindow.show(view);
	}
	private void showPictureDialog(Bitmap mBitmap, String time) {
		final Dialog mDialog = new Dialog(context, R.style.Dialog_Fullscreen);
		View v = View.inflate(context,
				R.layout.activity_gallery_show_pictureinfo, null);
		mDialog.setContentView(v);
		ImageView mImageView = (ImageView) v
				.findViewById(R.id.iv_picture_show_info);
		TextView title = (TextView) v
				.findViewById(R.id.tv_cateye_titlebar_title);// 标题栏
		title.setText(time);
		ImageView mback = (ImageView) v
				.findViewById(R.id.iv_cateye_titlebar_back);// 返回键
		mback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mImageView.setImageBitmap(mBitmap);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
}
