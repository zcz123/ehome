package com.wulian.iot.view.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wulian.icam.R;
import com.wulian.iot.bean.GalleryListItemInfo;
import com.wulian.iot.utils.IotUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/4/28 0028.
 */
public class GalleryGridviewAdapter extends BaseAdapter{

    private Context context;
    private List<GalleryListItemInfo> albums;

    public GalleryGridviewAdapter(List<GalleryListItemInfo> albums,Context context){
        this.context = context;
        this.albums = albums;
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyGridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new MyGridViewHolder();
            convertView =View.inflate(context, R.layout.grid_gallery_item,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_gv_item_galleryshow);
            viewHolder.ivShape = (ImageView) convertView.findViewById(R.id.iv_selected_bg);
            viewHolder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_gallery_checked);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyGridViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageBitmap(albums.get(position).getBitmap());
        switch (albums.get(position).getIsOver()){
            case 0:
                viewHolder.ivShape.setVisibility(View.GONE);
                break;
            case 1:
                viewHolder.ivShape.setVisibility(View.VISIBLE);
                break;
        }
        switch (albums.get(position).getIsCheck()){
        case 0:
            viewHolder.ivCheck.setVisibility(View.GONE);
            break;
        case 1:
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
            break;
    }
        
        return convertView;
    }

    private class MyGridViewHolder {
        ImageView imageView;
        ImageView ivShape;
        ImageView ivCheck;
    }
}
