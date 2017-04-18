package com.wulian.iot.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.utils.StringUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */

public class DeskSensitivityAdapter extends SimpleAdapter<String> {
    private List<String> mdata;
    SharedPreferences sp;
    private String selectedType;//选择的灵敏度

    public DeskSensitivityAdapter(Context context, List<String> data,String type) {
        super(context, data);
        this.mdata = data;
        this.selectedType = type;
    }

    @Override
    public View view(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder = null;
        if (convertView == null) {
            String type = mdata.get(position);
            viewHolder = new DeskSensitivityAdapter.viewHolder();
            convertView = this.layoutInflater.inflate(R.layout.item_desk_sensitivity, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.desk_sensitivity_type);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.desk_sensitivity_pic);
            viewHolder.textView.setText(type);
            if (selectedType.equals(type)) {
                viewHolder.imageView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageView.setVisibility(View.INVISIBLE);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) convertView.getTag();
        }
        return convertView;
    }

    private final class viewHolder {
        private TextView textView;
        private ImageView imageView;
    }
}
