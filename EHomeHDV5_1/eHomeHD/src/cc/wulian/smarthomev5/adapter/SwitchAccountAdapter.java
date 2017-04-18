package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.AccountManager;

public class SwitchAccountAdapter extends WLBaseAdapter<GatewayInfo>{

	private AccountManager accountManager = AccountManager.getAccountManger();
	public SwitchAccountAdapter(Context context) {
		super(context, new ArrayList<GatewayInfo>());
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		 return  inflater.inflate(R.layout.user_switch_account_item, null);
	}

	@Override
	protected void bindView(Context context, View view, final int pos,GatewayInfo item) {
		TextView gwIDTextView = (TextView)view.findViewById(R.id.user_switch_account_item_tv);
		ImageView checkView = (ImageView)view.findViewById(R.id.user_switch_account_item_iv);
		gwIDTextView.setText(accountManager.getGatewayName(item.getGwID()));
		if(accountManager.getmCurrentInfo() != null && accountManager.isConnectedGW() && StringUtil.equals(accountManager.getmCurrentInfo().getGwID(),getItem(pos).getGwID())){
        	checkView.setVisibility(View.VISIBLE);
        }else{
        	checkView.setVisibility(View.INVISIBLE);
        }
	}
}
