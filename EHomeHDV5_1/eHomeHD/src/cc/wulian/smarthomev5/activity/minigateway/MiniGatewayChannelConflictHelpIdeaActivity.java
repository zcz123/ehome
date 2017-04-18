package cc.wulian.smarthomev5.activity.minigateway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniRouterSettingActivity;

public class MiniGatewayChannelConflictHelpIdeaActivity extends Activity
		implements OnClickListener {

	private RelativeLayout close_route_btn;
	protected BaseActivity mActivity;
	private ImageView bt_title_back;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.device_mini_gateway_channel_conflict_help);
		initView();
		setListener();
		initData();
	}

	private void initView() {
		close_route_btn = (RelativeLayout) findViewById(R.id.close_mini_gateway_route);
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);

	}

	private void initData() {
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
	}

	private void setListener() {
		close_route_btn.setOnClickListener(this);
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				MiniGatewayChannelConflictHelpIdeaActivity.this.finish();
			}
		};
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		 if (id == R.id.close_mini_gateway_route) {
		 Intent intent = new Intent(
		 MiniGatewayChannelConflictHelpIdeaActivity.this,
		 MiniRouterSettingActivity.class);
		 startActivity(intent);
		 finish();
		 }else if (id == R.id.titlebar_back) {
			finish();
		}
	}

}
