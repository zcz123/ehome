package cc.wulian.smarthomev5.adapter.house;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.tools.SceneManager;

public class HousekeeperSceneTriggerAdapter extends WLBaseAdapter<SceneInfo>{

//	private int selectedPosition = 0;
	private String selectedSceneId;
	public HousekeeperSceneTriggerAdapter(Context context, List<SceneInfo> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.item_scene_home, parent, false);
	}

	@Override
	protected void bindView(Context context, View view, int pos, SceneInfo item) {
		super.bindView(context, view, pos, item);
		
		ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
		TextView nameText = (TextView) view.findViewById(R.id.name);
		
		Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(mContext, item.getIcon());
		Drawable selectIcon = SceneManager.getSceneIconDrawable_Bright(mContext, item.getIcon());
		
		iconImage.setImageDrawable(normalIcon);
		nameText.setText(item.getName());
		View state = view.findViewById(R.id.linearLayout_state);
//		state.setSelected(isUsing);
		if(selectedSceneId != null){
			if(StringUtil.equals(selectedSceneId, item.getSceneID())){
				iconImage.setImageDrawable(selectIcon);
				state.setSelected(true);
			}else{
				iconImage.setImageDrawable(normalIcon);
				state.setSelected(false);
			}
		}else{
			
		}
		
	}
	
//	public void setSelection(int position){
//		selectedPosition = position;
//	}
	
	public void setSelectSceneID(String sceneId){
		selectedSceneId = sceneId;
	}

}
