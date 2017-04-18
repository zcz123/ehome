package com.wulian.iot.view.adapter;
import java.util.List;
import com.wulian.iot.Config;
import com.wulian.iot.bean.VideotapeInfo;
import com.wulian.icam.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class VideotapeHistoryAdapter extends SimpleAdapter<VideotapeInfo>{
	public VideotapeHistoryAdapter(List<VideotapeInfo> videos,Context context){
		super(context,videos);
	}
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		String fileName = null;
		ViewHoldler vh = null;
		VideotapeInfo bean = eList.get(position);
		if (convertView == null) {
			convertView = this.layoutInflater.inflate(R.layout.item_videotape_history, null);
			vh = new ViewHoldler();			
			vh.tv = (TextView) convertView.findViewById(R.id.tv_videotape_time);
			vh.iv = (ImageView) convertView.findViewById(R.id.iv_videotape_image);
			convertView.setTag(vh);
		} else {
			vh = (ViewHoldler)convertView.getTag();
		}
		fileName = bean.getFileName();//文件名称
		if(bean.getVideoType() == Config.LOCAL_VOIDE){
			//add by guofeng
			fileName = fileName.substring(10,19).replace(".",":");
		} else if(bean.getVideoType() == Config.SERVER_VOIDE){
			 StringBuilder sb=new StringBuilder(fileName.substring(9,15));
		     fileName = sb.insert(2,":").insert(5, ":").toString();
		}
		vh.tv.setText(fileName);
		vh.iv.setImageBitmap(bean.getBitmap());
		return convertView;
	}
	public class ViewHoldler {
		TextView tv;
		ImageView iv;
	}
}
