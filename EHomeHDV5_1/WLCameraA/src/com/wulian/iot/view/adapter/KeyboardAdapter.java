package com.wulian.iot.view.adapter;
import java.util.List;
import com.wulian.icam.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class KeyboardAdapter extends SimpleAdapter<String>{
     public KeyboardAdapter(Context context,List<String> eList){
        super(context,eList);    	 
     }
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		String obj = eList.get(position);
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.item_keyboard, null);
			viewHolder.keyTxt = (TextView)convertView.findViewById(R.id.keyTxt);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.keyTxt.setText(obj);
		return convertView;
	}
	private final class ViewHolder{
		private TextView keyTxt;
	}
}
