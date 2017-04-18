package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.DateUtil;

public class SocialInfoAdapter extends EditableBaseAdapter<SocialEntity>
{
	private AccountManager manager =AccountManager.getAccountManger();
	private Map<Integer, State> stateMap = new HashMap<Integer,State>();

	public SocialInfoAdapter( Context context, List<SocialEntity> data )
	{
		super(context, data);
	}

	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ) {
		boolean isFromMe = getItemViewType(pos) == 0;
		int resID = isFromMe ? R.layout.item_social_info_from_me : R.layout.item_social_info;
		return mInflater.inflate(resID, parent, false);
	}

	@Override
	public int getItemViewType( int position ) {
		String appID = manager.getRegisterInfo().getAppID();
		SocialEntity entity = getItem(position);
		boolean isFromMe = StringUtil.equals(appID, entity.appID);
		return isFromMe ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	protected void bindView( Context context, View view, int pos, SocialEntity item ) {
		TextView userName = (TextView) view.findViewById(R.id.user_name);
		final ImageView delateimage = (ImageView) view.findViewById(R.id.delate_image);
		userName.setText(item.userName);

//		ImageView userIcon = (ImageView) view.findViewById(R.id.user_icon);
//		userIcon.setImageResource(R.drawable.head_icon);

		TextView content = (TextView) view.findViewById(R.id.social_content);
		content.setText(item.data);

		TextView publishTime = (TextView) view.findViewById(R.id.publish_time);
		if (item.time != null) {
			publishTime.setText(DateUtil.getFormatMiddleTime(DateUtil.convert2LocalTimeLong(StringUtil.toLong(item.time))));
		}
		
		if(mIsEditingMode){
			delateimage.setVisibility(View.VISIBLE);
			State state = getState(pos);
			delateimage.setSelected(state.isDeleted());
		}else{
			delateimage.setVisibility(View.GONE);
		}
	}
	
	
	public State getState(int pos){
		State state = stateMap.get(pos);
		if(state == null){
			state = new State();
			stateMap.put(pos, state);
		}
		return state;
	}
	public void setAllSelect(boolean checked){
		for(int i = 0; i < getCount(); i++ ){
			State state = getState(i);
			state.setDeleted(checked);
			notifyDataSetChanged();
		}
	}
	
	
	public String getSelectedIds(){
		String ids = "";
		for(int key: stateMap.keySet()){
			if(stateMap.get(key).isDeleted()){
				ids+=getItem(key).getSocialID()+",";
			}
		}
		return ids;
	}
	public List<SocialEntity> getSelectedSocialEntites(){
		ArrayList<SocialEntity> entites = new ArrayList<SocialEntity>();
		for(int key: stateMap.keySet()){
			if(stateMap.get(key).isDeleted()){
				entites.add(getItem(key));
			}
		}
		return entites;
	}
	public void addSocialEntity(SocialEntity entity){
		getData().add(entity);
		notifyDataSetChanged();
	}
	public void addHistory(List<SocialEntity> entites){
		getData().addAll(0,entites);
		notifyDataSetChanged();
	}
	public void clearState(){
		stateMap.clear();
	}
	
	public class State{
		boolean isDeleted = false;

		public boolean isDeleted() {
			return isDeleted;
		}

		public void setDeleted(boolean isDeleted) {
			this.isDeleted = isDeleted;
		}
		
	}

	@Override
	public OnClickListener setEditableClickListener(int pos, SocialEntity item) {
		// TODO Auto-generated method stub
		return null;
	}

}
