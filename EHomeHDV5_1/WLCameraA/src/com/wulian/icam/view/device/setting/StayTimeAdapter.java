package com.wulian.icam.view.device.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.iot.view.adapter.SimpleAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */

public class StayTimeAdapter extends SimpleAdapter<String> {
    private String stayTime;
    private Context mcontext;

    public StayTimeAdapter(Context context, List<String> mData, String stayTime) {
        super(context, mData);
        this.mcontext = context;
        this.stayTime = stayTime;
        eList = mData;
    }

    @Override
    public View view(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder = null;
        if (convertView == null) {
            String type = eList.get(position);
            viewHolder = new viewHolder();
            convertView = this.layoutInflater.inflate(R.layout.item_eagle_stay_time, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_stay_time);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_stay);
            if ((stayTime + "s").equals(type)) {
                viewHolder.imageView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageView.setVisibility(View.INVISIBLE);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(eList.get(position));
        return convertView;
    }

    private final class viewHolder {
        private TextView textView;
        private ImageView imageView;
    }
}
