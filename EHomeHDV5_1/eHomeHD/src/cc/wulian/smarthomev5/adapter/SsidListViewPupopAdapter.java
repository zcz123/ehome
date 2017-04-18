package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.iot.bean.EagleWifiListEntiy;
import com.wulian.iot.view.adapter.SimpleAdapter;

public class SsidListViewPupopAdapter extends  SimpleAdapter<EagleWifiListEntiy> {

	public SsidListViewPupopAdapter(Context context,List<EagleWifiListEntiy> list){
		super(context, list);
	}
	
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		EagleWifiListEntiy obj=(EagleWifiListEntiy)eList.get(position);
		ViewHolder mViewHolder=null;
		if (convertView==null) {
			mViewHolder=new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.list_item_textview_pupopwindow, null);
			mViewHolder.mSsidShow=(TextView) convertView.findViewById(R.id.tv_list_pupop_show_ssid);
			convertView.setTag(mViewHolder);
		}else {
			mViewHolder = (ViewHolder)convertView.getTag();
		}
		mViewHolder.mSsidShow.setText(obj.getWifiname());
		return convertView;
	}

	private final class ViewHolder{
		 private TextView mSsidShow;
	}
}
