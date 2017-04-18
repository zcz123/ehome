package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.CommentInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.AccountManager;

public class CommentsInfoAdapter extends WLBaseAdapter<CommentInfo>{

	public CommentsInfoAdapter(Context context, List<CommentInfo> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		 return  inflater.inflate(R.layout.aboutus_feedback_history_comment_item, null);
	}

	@Override
	protected void bindView(Context context, View view, final int pos,CommentInfo item) {
		TextView timeTextView = (TextView)view.findViewById(R.id.aboutus_feedback_history_comment_time_tv);
		TextView infoTextView = (TextView)view.findViewById(R.id.aboutus_feedback_history_comment_information_tv);
//		ImageView checkView = (ImageView)view.findViewById(R.id.user_switch_account_item_iv);
		timeTextView.setText(item.getTime());
		infoTextView.setText(item.getComment());
	}
}
