package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.AccountManager;

public class SigninRecordsAdapterV5 extends WLBaseAdapter<GatewayInfo>{

	private final int mWanColor;
	private final int mLanColor;
	private boolean mHasDeleButton;
	private int greenSize = 0;
	private final AccountManager mAccountManager;
	public SigninRecordsAdapterV5(Context context) {
		super(context,new ArrayList<GatewayInfo>());
		mAccountManager = AccountManager.getAccountManger();
		Resources r = mContext.getResources();
		mWanColor = r.getColor(R.color.v5_gray_mid);
		mLanColor = r.getColor(R.color.v5_green_light);
	}

	public void showDeleteButton(boolean isDel) {
		mHasDeleButton = isDel;
	}

	public boolean getIsDeleteButtonShown() {
		return mHasDeleButton;
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.layout_item_signin_records, parent,
				false);
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			GatewayInfo info) {

		TextView name = (TextView) view.findViewById(R.id.textView_name);
		View del = view.findViewById(R.id.view_del);
		View pwd = view.findViewById(R.id.view_pwd);

		if (pos < greenSize) {
			name.setTextColor(mLanColor);
		}else{
			name.setTextColor(mWanColor);
		}
		name.setText(mAccountManager.getGatewayName(info.getGwID()));
		/**
		 * 如果是搜索到网关，那么就显示删除按钮
		 */
		if (mHasDeleButton) {
			del.setBackgroundResource(R.drawable.icon_delete_gateway);
			del.setOnClickListener(new DeleteClickListener(pos, info));
			del.setVisibility(View.VISIBLE);
		} else {
			del.setVisibility(View.GONE);
		}

		/**
		 * 如果未保存密码，那么在在前面显示密码图片
		 */
		if (StringUtil.isNullOrEmpty(info.getGwPwd())) {
			pwd.setVisibility(View.VISIBLE);
		} else {
			pwd.setVisibility(View.INVISIBLE);
		}
	}

	private class DeleteClickListener implements View.OnClickListener {
		final int pos;
		final GatewayInfo info;

		public DeleteClickListener(int pos, GatewayInfo info) {
			this.pos = pos;
			this.info = info;
		}

		@Override
		public void onClick(View v) {
			mAccountManager.removeAccount(info.getGwID());
			getData().remove(pos);
			notifyDataSetChanged();
		}
	}
	/**
	 * 根据获取到的网关的类型来给适配器添加数据
	 * 
	 * @param newData
	 *            获取到的网关集合
	 * @param flag
	 *            网关的类型
	 */
	public void swapData(List<GatewayInfo> history,List<GatewayInfo> search) {
		ArrayList<GatewayInfo> result = new ArrayList<GatewayInfo>();
		if(search == null){
			search = new ArrayList<GatewayInfo>();
		}
		if(history == null){
			history = new ArrayList<GatewayInfo>();
		}
		for (int i = 0; i < search.size() - 1; i++) {
			for (int j = search.size() - 1; j > i; j--) {
				if (search.get(j).getGwID().equals(search.get(i).getGwID())) {
					search.remove(j);
				}
			}
		}
		result.addAll(search);
		greenSize = result.size();
		for(GatewayInfo h : history){
			int j = 0;
			for(j=0 ;j < search.size();j++){
				GatewayInfo info = search.get(j);
				if(info.getGwID().equals(h.getGwID())){
					result.get(j).setGwPwd(h.getGwPwd());
					break;
				}
			}
			if(j == search.size()){
				result.add(h);
			}
		}
		super.swapData(result);
	}


	/**
	 * 用来返回搜索到网关的数量
	 * 
	 * @return 搜索到网关的数量
	 */
	public int getSearchCount() {
		return getData().size();
	}
}
