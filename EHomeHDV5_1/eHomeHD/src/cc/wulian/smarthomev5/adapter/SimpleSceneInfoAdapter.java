package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class SimpleSceneInfoAdapter extends WLBaseAdapter<SceneInfo>
{
	public SimpleSceneInfoAdapter( Context context, List<SceneInfo> data )
	{
		super(context, data);
	}

	@Override
	protected void bindView( Context context, View view, int pos, SceneInfo item ){
		ImageView iconImageView = (ImageView)view.findViewById(R.id.scene_icon_iv);
		if(!CmdUtil.SCENE_UNKNOWN.equals(item.getSceneID())){
			Drawable normalIcon = SceneManager.getSceneIconDrawable_Light_Small(context, item.getIcon());
			iconImageView.setImageDrawable(normalIcon);
		}else{
			iconImageView.setImageDrawable(null);
		}
		TextView sceneName = (TextView)view.findViewById(R.id.scene_name_tv);
		sceneName.setText(item.getName());
	}


	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.scene_popup_scene_item, null);
	}

}
