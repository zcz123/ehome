package com.wulian.iot.view.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.wulian.icam.R;
import com.wulian.iot.bean.PresettingModel;
@SuppressLint("NewApi")
public abstract class PresetAdapter extends SimpleAdapter<PresettingModel> {
	public PresetAdapter(){
		
	}
    public PresetAdapter(Context context,List<PresettingModel> info){
      super(context,info);    	
    }
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		PresettingModel pModel = eList.get(position);
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.preset_item, null);
			viewHolder.perName = (TextView) convertView.findViewById(R.id.tv_testpos);
            viewHolder.perImage = (ImageView)convertView.findViewById(R.id.iv_preset_position);
            viewHolder.exitImage = (ImageView)convertView.findViewById(R.id.iv_del_pos);
            getView(viewHolder.exitImage,position);
		    convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		 viewHolder.perName.setText(pModel.getpName());
         viewHolder.perImage.setBackground(pModel.getpImg());
         if(pModel.isExit()){
        	 viewHolder.exitImage.setVisibility(View.VISIBLE);
         } else {
        	 viewHolder.exitImage.setVisibility(View.GONE);
         }
		return convertView;
	}
	private final class ViewHolder{
		 private TextView perName;
	     private ImageView perImage,exitImage;
	}
	public abstract void getView(View view,int position);
}
