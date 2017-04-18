package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;

@SuppressLint("ViewHolder")
public class MiniGatewayTimePeroidAdapter extends WLBaseAdapter<String> {

	public String adapterType = "second";

	public MiniGatewayTimePeroidAdapter(Context context) {
		super(context, new ArrayList<String>());
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.mini_gateway_time_peroid_item,
				parent, false);
		TextView textView = (TextView) view
				.findViewById(R.id.setting_time_peroid_tv);
		ImageView imageView = (ImageView) view
				.findViewById(R.id.setting_mini_time_peroid_iv);
		if (!getData().get(position).equals("1")) {
			imageView.setVisibility(View.INVISIBLE);
		}
		if (adapterType.equals("first")) {
			if (position > 9) {
				textView.setText(position + "" + ":00");
			} else {
				textView.setText("0" + position + "" + ":00");
			}
		} else {
			int pos = position + 12;
			textView.setText(pos + ":00");
		}
		return view;
	}

	public void addSettingItem(String item) {
		this.getData().add(item);
	}
}
