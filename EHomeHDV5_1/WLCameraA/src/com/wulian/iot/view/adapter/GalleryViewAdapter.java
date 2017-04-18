package com.wulian.iot.view.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.wulian.icam.R;
import com.wulian.iot.bean.GalleryInfo;
import com.wulian.iot.bean.GalleryItemInfo;
import com.wulian.iot.widght.NoScrollGridView;
public class GalleryViewAdapter extends SimpleAdapter<GalleryInfo> {
	private static final String TAG = "GalleryViewAdapter";
	public GalleryItemAdapter galleryItemAdapter = null;
	public GalleryViewAdapter(Context mContext, List<GalleryInfo> galleryInfos) {
		super(mContext, galleryInfos);
	}
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		GalleryInfo galleryInfo = eList.get(position);
		ViewHoldler viewHoldler = null;
		if (convertView == null) {
			viewHoldler = new ViewHoldler();
			convertView = this.layoutInflater.inflate(
					R.layout.list_gallery_item, null);
			viewHoldler.dateTxt = (TextView) convertView
					.findViewById(R.id.tv_gallery_time);
			viewHoldler.noScrollGridView = (NoScrollGridView) convertView
					.findViewById(R.id.gv_gallery);
			convertView.setTag(viewHoldler);
		} else {
			viewHoldler = (ViewHoldler) convertView.getTag();
		}
		viewHoldler.dateTxt.setText(galleryInfo.getFilename());
		galleryItemAdapter = new GalleryItemAdapter(context,galleryInfo.getGalleryItemInfos());
		viewHoldler.noScrollGridView.setAdapter(galleryItemAdapter);
		GalleryViewAdapter.this.Listener(viewHoldler.noScrollGridView,galleryItemAdapter);
		return convertView;
	}
	public void Listener( NoScrollGridView noScrollGridView,GalleryItemAdapter galleryItemAdapter ){
	}
	public class GalleryItemAdapter extends SimpleAdapter<GalleryItemInfo> {
		public GalleryItemAdapter(Context mContext,
				List<GalleryItemInfo> galleryItemInfos) {
			super(mContext, galleryItemInfos);
		}
		@Override
		public View view(int position, View convertView, ViewGroup parent) {
			GalleryItemInfo galleryItemInfo = eList.get(position);
			ViewHoldlerItem viewHoldlerItem = null;
			if (convertView == null) {
				viewHoldlerItem = new ViewHoldlerItem();
				convertView = this.layoutInflater.inflate(
						R.layout.grid_gallery_item, null);
				viewHoldlerItem.contextImg = (ImageView) convertView
						.findViewById(R.id.iv_gv_item_galleryshow);
				viewHoldlerItem.checkImg = (ImageView) convertView
						.findViewById(R.id.iv_gallery_checked);
				viewHoldlerItem.selectedImg = (ImageView) convertView
						.findViewById(R.id.iv_selected_bg);
				convertView.setTag(viewHoldlerItem);
			} else {
				viewHoldlerItem = (ViewHoldlerItem) convertView.getTag();
			}
			viewHoldlerItem.contextImg.setImageBitmap(galleryItemInfo
					.getBitmap());
			if (galleryItemInfo.isCheck()) {
				viewHoldlerItem.checkImg.setVisibility(View.VISIBLE);
			} else {
				viewHoldlerItem.checkImg.setVisibility(View.GONE);
			}
			if(galleryItemInfo.isSelectd()){
				viewHoldlerItem.selectedImg.setVisibility(View.VISIBLE);
			} else {
				viewHoldlerItem.selectedImg.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
	private class ViewHoldler {
		private TextView dateTxt;
		private NoScrollGridView noScrollGridView;
	}
	private class ViewHoldlerItem {
		private ImageView contextImg, checkImg, selectedImg;
	}
}
