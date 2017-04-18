package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.AudioEntity;
import cc.wulian.smarthomev5.tools.MediaPlayerTool;

public class RingtonSetAdapter extends WLBaseAdapter<AudioEntity>
{
	private String existUri;

	public RingtonSetAdapter( Context context, List<AudioEntity> data, String existUri )
	{
		super(context, data);
		this.existUri = existUri;
	}
	public void setSelectedPath( String selectedPath ){
		this.existUri = selectedPath;
		notifyDataSetChanged();
	}

	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ){
		View view = inflater.inflate(R.layout.setting_select_bell_reminder, null);
		return view;
	}

	@Override
	protected void bindView( Context context, View view, final int pos, AudioEntity item ){
		TextView ringNameTextView = (TextView)view.findViewById(R.id.setting_select_image_tv);
		ringNameTextView.setText(item.getmAudioName());
		ImageView ringSelectedImageView = (ImageView)view.findViewById(R.id.setting_select_image_btn);
		if(this.existUri != null && this.existUri.equals( item.getmAudioPath())){
			ringSelectedImageView.setVisibility(View.VISIBLE);
		}else{
			ringSelectedImageView.setVisibility(View.INVISIBLE);
		}
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path= getItem(pos).getmAudioPath();
				setSelectedPath(path);
				if(!StringUtil.isNullOrEmpty(path)){
					MediaPlayerTool.play(mContext, Uri.parse(path));
				}
					
				
			}
		});
	}
	public AudioEntity getSelectedAudioEntity(){
		for(AudioEntity a : getData()){
			if(a.getmAudioPath().equals(existUri)){
				return a;
			}
		}
		return null;
	}
}