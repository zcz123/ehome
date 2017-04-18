/**
 * Project Name:  Z_BitmapfunTest
 * File Name:     ImageListViewAdapter.java
 * Package Name:  com.test.bitmap
 * @Date:         2015年3月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.album;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.AlbumEntity;
import com.wulian.icam.utils.ImageLoader;
import com.wulian.icam.view.widget.CustomToast;

/**
 * @Function: 相册适配器
 * @date: 2015年6月10日
 * @author Wangjj
 */

public class AlbumAdapter extends BaseAdapter implements OnScrollListener,
		OnItemClickListener {
	private Context mContext;
	private List<AlbumEntity> albumList;
	private boolean isFirstLoad = true;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	private ListView mListView;
	private ImageLoader loader;
	private LayoutInflater inflater;

	// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
	// Locale.ENGLISH);
	// SimpleDateFormat df1 = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
	// SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

	public AlbumAdapter(ListView mListView, Context mContext, ImageLoader loader) {
		this.mListView = mListView;
		this.mContext = mContext;
		this.loader = loader;
		inflater = LayoutInflater.from(mContext);
		// mListView.setOnScrollListener(this);//与pulllistview事件冲突
		mListView.setOnItemClickListener(this);
	}

	public void setSourceData(List<AlbumEntity> albumEntities) {
		this.albumList = albumEntities;
	}

	@Override
	public int getCount() {
		return albumList.size();
	}

	@Override
	public Object getItem(int position) {
		return albumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlbumEntity entity = albumList.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_album_new, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_date = (TextView) convertView
					.findViewById(R.id.tv_title_date);
			viewHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tv_count = (TextView) convertView
					.findViewById(R.id.tv_count);
			viewHolder.iv_preview = (ImageView) convertView
					.findViewById(R.id.iv_preview);
			viewHolder.pbloading = (ProgressBar) convertView
					.findViewById(R.id.pb_album);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.iv_preview.setTag(entity.getFirstImagePath());
		Bitmap bitmap = loader.showCacheBitmap(entity.getFirstImagePath(),
				new MOnImageLoader(viewHolder), 200, 200);
		if (bitmap != null) {// 缓存中已经存在
			viewHolder.pbloading.setVisibility(View.GONE);
			viewHolder.iv_preview.setImageBitmap(bitmap);
		} else {// 内部加载完成后通知
			viewHolder.pbloading.setVisibility(View.VISIBLE);
			// viewHolder.iv_preview继续保持上一次的样子
		}
		String name = entity.getDeviceName();
		if (!TextUtils.isEmpty(name)) {
			viewHolder.tv_title.setText(name);
		} else {
			viewHolder.tv_title.setText(entity.getFileName());
		}
		viewHolder.tv_date.setText(entity.getTime());
		viewHolder.tv_count.setText(entity.getCount() + "");
		return convertView;
	}

	private class MOnImageLoader implements ImageLoader.OnImageLoaderListener {
		private ViewHolder holder;

		public MOnImageLoader(ViewHolder holder) {
			this.holder = holder;
		}

		@Override
		public void onImageLoader(Bitmap bitmap, String url) {
			// wjj:
			// 图片较多时，回调发生时,可能是先前的holder请求的，照成"瞬变",所以要加个判断,确保可见视图的tag==回调的tag
			// 优化点:滚出屏幕的task可以取消，以减轻线程负担，加快显示速度！
			if (holder.iv_preview.getTag().equals(url)) {// 此holder是当前可见视图的那个holder
				holder.pbloading.setVisibility(View.GONE);
				holder.iv_preview.setImageBitmap(bitmap);
			}
		}
	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_date;
		TextView tv_count;
		ImageView iv_preview;
		ProgressBar pbloading;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// mContext.startActivity(new Intent(mContext, AlbumPicActivity.class)
		// .putExtra("AlbumEntity", albumList.get(position - 1)));
		mContext.startActivity(new Intent(mContext, AlbumGridActivity.class)
				.putExtra("AlbumEntity", albumList.get(position - 1)));
	}

	// 以下和滚动相关，没有启用
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			CustomToast.show(mContext, "idle showImage");
		} else {
			loader.cancelTask();
			CustomToast.show(mContext, "cancleTask");
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		if (isFirstLoad && mVisibleItemCount > 0) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstLoad = false;
		}
	}

	private void showImage(int mFirstItem, int mItemCount) {

		for (int i = mFirstItem; i < mFirstItem + mItemCount; i++) {
			AlbumEntity entity = albumList.get(i);
			final ImageView iv = (ImageView) mListView.findViewWithTag(entity
					.getFirstImagePath());
			loader.loadImage(entity.getFirstImagePath(),
					new ImageLoader.OnImageLoaderListener() {
						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (iv.getTag().equals(url)) {
								iv.setImageBitmap(bitmap);
							}
						}
					}, 200, 200);
		}
	}

	public void cancelTask() {
		if (loader != null) {
			loader.cancelTask();
		}
	}

}
