package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.SceneManager;

public class SceneDefaultAdapter extends WLBaseAdapter<SceneInfo>{

	//用一个Map集合来存储CheckBox被选中的状态
	private Map<Integer,Boolean> checkedMap = new HashMap<Integer,Boolean>();
	private BitSet mBitSet;
	public BitSet getBitSet() {
		return mBitSet;
	}
	//
	public SceneDefaultAdapter(Context context, List<SceneInfo> data) {
		super(context, data);
		mBitSet = new BitSet();
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.item_scene_default_listview, null);
	}

	@Override
	protected void bindView(Context context, View view, int pos, SceneInfo item) {
		CheckBox sceneCheckBox = (CheckBox) view.findViewById(R.id.scene_default_grid_item);
		ImageView sceneImageView = (ImageView) view.findViewById(R.id.scene_default_select_imageview);
		TextView sceneName = (TextView) view.findViewById(R.id.scene_default_select_name);
		TextView sceneIntroduce = (TextView) view.findViewById(R.id.scene_default_select_introduce);
		
		//布局文件中为了避免CheckBox与GridView的焦点冲突
		//android:descendantFocusability="beforeDescendants" 会优先其子类控件而获取到焦点
		//android:descendantFocusability="afterDescendants" 只有当子类不需要获取焦点时才获取焦点
		//android:descendantFocusability="blocksDescendants" 会覆盖子类控件而直接获取较焦点  但其效果不明显，还需如下设置
		sceneCheckBox.setPressed(false);
		sceneCheckBox.setClickable(false);
		
		sceneName.setText(item.getName());
		sceneImageView.setImageDrawable(SceneManager.getSceneIconDrawable_Black(context,item.getIcon()));
		if("0".equals(item.getIcon())){
			sceneIntroduce.setText(mContext.getString(R.string.scene_default_back_home_introduce));
		}else if("1".endsWith(item.getIcon())){
			sceneIntroduce.setText(mContext.getString(R.string.scene_default_sleep_introduce));
		}else if("2".endsWith(item.getIcon())){
			sceneIntroduce.setText(mContext.getString(R.string.scene_default_sleep_introduce));
		}else if("4".endsWith(item.getIcon())){
			sceneIntroduce.setText(mContext.getString(R.string.scene_default_back_home_introduce));
		}else if("9".endsWith(item.getIcon())){
			sceneIntroduce.setText(mContext.getString(R.string.scene_default_all_open_introduce));
		}else if("10".endsWith(item.getIcon())){
			sceneIntroduce.setText(mContext.getString(R.string.scene_default_all_close_introduce));
		}
		//更新checkBox选中的状态
		if(isChecked(pos)){
			sceneCheckBox.setChecked(true);
		}else{
			sceneCheckBox.setChecked(false);
		}
		sceneCheckBox.setChecked(mBitSet.get(pos));
	}
	
	/**
	 * 给外部提供设置是否选中的方法，用于GridView点击相应item时走监听事件 
	 */
	public void setSelection(int position){
		Boolean isChecked = isChecked(position);
		//如果Gridview选中，则item还未标记选中-->false则改为true
		if(isChecked){
			checkedMap.put(position, false);
		}else{
			checkedMap.put(position, true);
		}
		mBitSet.set(position, !mBitSet.get(position));
		notifyDataSetChanged();
	}

	/**
	 * 查询Map集合中checkBox是否被选中，如果选中为true，为选中为false,如果（Map集合）里面没有包括此position，则为null-->false 
	 */
	private Boolean isChecked(int position) {
		Boolean isChecked = checkedMap.get(position);
		if(isChecked == null)
			isChecked = false;
		return isChecked;
	}
	
	/**
	 * 提供给外部调用，获取被选中场景信息的方法，通过循环遍历Map集合中被选中的key来添加相应的Info 
	 */
	public List<SceneInfo> getCheckedSceneInfos(){
		ArrayList<SceneInfo> infos = new ArrayList<SceneInfo>();
		for(Integer key : checkedMap.keySet()){
			if(isChecked(key)){
				infos.add(getItem(key));
			}
		}
		return infos;
	}
	
	
	public void clearcheckedMap(){
		checkedMap.clear();
	}
}
