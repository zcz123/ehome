package cc.wulian.smarthomev5.adapter.flower;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.FlowerVoiceControlEntity;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;

public class FlowerVoiceControlAdapter extends SwipeMenuAdapter<FlowerVoiceControlEntity> {

	private MainApplication application = MainApplication.getApplication();
	public FlowerVoiceControlAdapter(Context context,
			List<FlowerVoiceControlEntity> data) {
		super(context, data);
	}
	protected void bindView(Context mContext2, View convertView, int position,
			FlowerVoiceControlEntity item) {
		TextView commondTextView = (TextView)convertView.findViewById(R.id.dreamflower_item_name);
		commondTextView.setText("指令"+item.getIndex());
		TextView sceneTextView = (TextView)convertView.findViewById(R.id.dreamflower_item_scene_name);
		SceneInfo info = application.sceneInfoMap.get(item.getGwID()+item.getBindScene());
		if (info == null || info.getSceneID() == null) {
			sceneTextView.setText(mContext.getResources().getString(
					R.string.nav_scene_title));
		}else if(CmdUtil.SCENE_UNKNOWN.equals(info.getSceneID())){
			sceneTextView.setText(mContext.getResources().getString(
					R.string.nav_scene_title));
		}else{
			sceneTextView.setText(info.getName());
		}
	}
	@Override
	protected View newView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		return inflater.inflate(R.layout.gateway_dreamflower_voice_control_item_ll, null);
	}
}
