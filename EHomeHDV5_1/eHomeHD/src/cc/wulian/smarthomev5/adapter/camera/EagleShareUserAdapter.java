package cc.wulian.smarthomev5.adapter.camera;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cc.wulian.ihome.wan.sdk.user.entity.BindUser;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DevicesUserManage;
import com.wulian.iot.view.adapter.SimpleAdapter;
import com.yuantuo.customview.ui.WLDialog;

public  class EagleShareUserAdapter extends  SimpleAdapter<BindUser> {
	private static String TAG ="EagleShareUserAdapter";
	public EagleShareUserAdapter(Context mContext,List<BindUser> bindUsers){
		super(mContext,bindUsers);
	}
	@Override
	public View view(final int position, View convertView, ViewGroup parent) {
		final BindUser bindUser = eList.get(position);
		ViewHoldler viewHoldler = null;
		if(convertView == null){
			viewHoldler = new ViewHoldler();
			convertView = this.layoutInflater.inflate(R.layout.item_eagle_share_listview, null);
			viewHoldler.shareAccountTxt = (TextView)convertView.findViewById(R.id.device_share_account);
			viewHoldler.unshareTxt = (TextView)convertView.findViewById(R.id.device_unshare);
			convertView.setTag(viewHoldler);
		} else {
			viewHoldler = (ViewHoldler) convertView.getTag();
		}
		if(!bindUser.isAdmin()){
			viewHoldler.unshareTxt.setVisibility(View.VISIBLE);
			viewHoldler.unshareTxt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					unBindUser(getAuthUser(bindUser),position);
					showDialogNote(bindUser,position);
				}
			});
		} 
		viewHoldler.shareAccountTxt.setText(getAuthUser(bindUser));
		return convertView;
	}
	private class ViewHoldler{
		private TextView shareAccountTxt,unshareTxt;
	}
	public String  getAuthUser(BindUser bindUser){
		String user = null;
		if(bindUser.getUserName()!=null){
			user = bindUser.getUserName();
		} else if(bindUser.getUserId() != -1){
			user = String.valueOf(bindUser.getUserId());
		}
		return user;
	}
	public  void unBindUser(String  user,int position){
	}
	private void showDialogNote(final BindUser bindUser , final int position){
		final WLDialog.Builder wb=new WLDialog.Builder(context);
		wb.setMessage(context.getResources().getString(R.string.cateye_unshare_hint))
				.setPositiveButton(context.getResources().getString(com.wulian.icam.R.string.common_sure))
				.setNegativeButton(context.getResources().getString(com.wulian.icam.R.string.common_cancel))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						unBindUser(getAuthUser(bindUser),position);
					}
					@Override
					public void onClickNegative(View contentViewLayout) {
						wb.create().dismiss();
					}
				});
		wb.create().show();
	}

}
