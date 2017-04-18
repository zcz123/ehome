package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;

public class MiniGatewayVoiceChooseAdapter extends WLBaseAdapter<String> {

	public MiniGatewayVoiceChooseAdapter(Context context) {
		super(context, new ArrayList<String>());
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=LayoutInflater.from(mContext);
		View view=inflater.inflate(R.layout.device_mini_voice_choose_item,null);
		TextView textView=(TextView) view.findViewById(R.id.voice_choose_tv);
		ImageView imageView=(ImageView) view.findViewById(R.id.voice_choose_imageview);
		switch (position) {
		case 0:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_Dingdong));
			break;
		case 1:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_jingle));
			break;
		case 2:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_crisp));
			break;
		case 3:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_Long_tone));
			break;
		case 4:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_fluctuation));
			break;
		case 5:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_Cuckoo));
			break;
		case 6:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_Didi));
			break;
		case 7:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_fierce));
			break;
		case 8:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_rapid));
			break;
		case 9:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_sharp));
			break;
		case 10:
			textView.setText(mContext.getResources().getString(R.string.miniGW_DeviceVoice_police));
			break;
		}
		if(getData().get(position).equals("1")){
			imageView.setVisibility(View.VISIBLE);
		}
		return view;
	}
	
}
