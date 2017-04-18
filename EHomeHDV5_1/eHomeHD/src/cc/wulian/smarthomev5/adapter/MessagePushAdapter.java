package cc.wulian.smarthomev5.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.DateUtil;

public class MessagePushAdapter extends WLBaseAdapter<SocialEntity>
{
	private AccountManager manager = AccountManager.getAccountManger();
	public MessagePushAdapter( Context context, List<SocialEntity> data )
	{
		super(context, data);
	}

	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ) {
		boolean isFromMe = getItemViewType(pos) == 0;
		int resID = isFromMe ? R.layout.fragment_message_push_item_from_me : R.layout.fragment_message_push_item;
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
		userName.setText(item.userName);
		TextView content = (TextView) view.findViewById(R.id.social_content);
		content.setText(item.data);

		TextView publishTime = (TextView) view.findViewById(R.id.publish_time);
		if (item.time != null) {
			publishTime.setText(DateUtil.getFormatMiddleTime(Long.parseLong(item.time)));
		}
	}
}
