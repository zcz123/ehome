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
import cc.wulian.smarthomev5.tools.StateDrawableFactory;
import cc.wulian.smarthomev5.tools.StateDrawableFactory.Builder;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class MonitorSceneQuickInfoAdapter extends WLBaseAdapter<SceneInfo>{

	public MonitorSceneQuickInfoAdapter(Context context, List<SceneInfo> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.item_scene_monitor, parent, false);
	}

	@Override
	protected void bindView(Context context, View view, int pos, SceneInfo item) {
		ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
		TextView nameText = (TextView) view.findViewById(R.id.name);

		boolean isUsing = SceneManager.isSceneInUse(item.getStatus());
		
		Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(context, item.getIcon());
		Drawable checkedIcon = null;
		if (isUsing){
			checkedIcon = SceneManager.getSceneIconDrawable_Bright(context, item.getIcon());
		}
		else{
			checkedIcon = SceneManager.getSceneIconDrawable_Black(context, item.getIcon());
		}

		Builder builder = StateDrawableFactory.makeSimpleStateDrawable(context, normalIcon, checkedIcon);

		iconImage.setImageDrawable(builder.create());
		nameText.setText(item.getName());
		
		View state = view.findViewById(R.id.linearLayout_state);
		state.setSelected(isUsing);
		state.setOnClickListener(new OnClick(item));
	}
	
	private final class OnClick implements View.OnClickListener{

		private final SceneInfo item;

		public OnClick( SceneInfo item )
		{
			this.item = item;
		}
		
		@Override
		public void onClick(View v) {
			SceneInfo newSceneInfo = item.clone();
			newSceneInfo.setStatus(CmdUtil.SCENE_USING);
			SceneManager.switchSceneInfo(mContext, newSceneInfo, true);
		}
		
	}

}
