package com.wulian.icam.view.device.play;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.iot.view.adapter.SimpleAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/1/17.
 */

public class CameraKeyBoardAdapter extends SimpleAdapter<String> {
    private Context mcontext;
    private List<String> mData;

    public CameraKeyBoardAdapter(Context context, List<String> mData) {
        super(context, mData);
        this.mcontext = context;
        this.eList = mData;
    }

    @Override
    public View view(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new CameraKeyBoardAdapter.viewHolder();
            convertView = this.layoutInflater.inflate(R.layout.item_keyboard_layout, null);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_num);
            viewHolder.tv.setText(eList.get(position));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }
        return convertView;
    }


    class viewHolder {
        TextView tv;
    }

}
