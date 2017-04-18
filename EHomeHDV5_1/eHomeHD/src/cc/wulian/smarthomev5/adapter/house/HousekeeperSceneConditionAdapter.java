package cc.wulian.smarthomev5.adapter.house;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class HousekeeperSceneConditionAdapter extends WLBaseAdapter<SceneInfo>{

//	private int selectedPosition = 0;
//	private List<String> selectedList = new ArrayList<>();
	private Map<String,Boolean> selectedMap = new HashMap<String,Boolean>();
	public HousekeeperSceneConditionAdapter(Context context, List<SceneInfo> data) {
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
		if (selectedMap.get(item.getSceneID()) != null && selectedMap.get(item.getSceneID())) {
			iconImage.setImageDrawable(selectIcon);
			state.setSelected(true);
		} else if(selectedMap.get(item.getSceneID()) != null && !selectedMap.get(item.getSceneID())){
			iconImage.setImageDrawable(normalIcon);
			state.setSelected(false);
		} else{
			iconImage.setImageDrawable(normalIcon);
			state.setSelected(false);
		}
		
	}
	
//	public void setSelection(int position){
//		selectedPosition = position;
//	}
	public void setSelectSceneID(String sceneId){
		if(selectedMap.get(sceneId) != null && selectedMap.get(sceneId)){
			selectedMap.put(sceneId, false);
		}else if(selectedMap.get(sceneId) != null && !selectedMap.get(sceneId)){
			selectedMap.put(sceneId, true);
		}else{
			selectedMap.put(sceneId, true);
		}
		notifyDataSetChanged();
	}
	
	public List<String> getSelectedList() {
		List<String> selectedList = new ArrayList<String>();
		for(String str : selectedMap.keySet()){
			if(selectedMap.get(str)){
				selectedList.add(str);
			}
		}
		return selectedList;
	}

	public void setSelectedList(List<String> selectedList) {
		for(int i = 0; i < selectedList.size(); i++){
			selectedMap.put(selectedList.get(i), true);
		}
	}
	
}
