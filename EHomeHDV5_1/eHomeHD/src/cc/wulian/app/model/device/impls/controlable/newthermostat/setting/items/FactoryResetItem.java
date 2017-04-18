package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FactoryResetItem extends AbstractSettingItem{
	
	private boolean isResetOpen = false;
	private ShowFactoryResetDownViewListener factoryResetDownViewListener;
	
	private static final int DRAWABLE_RESET_UP = R.drawable.thermost_setting_arrow_up;
	private static final int DRAWABLE_RESET_DOWN = R.drawable.thermost_setting_arrow_down;

	public FactoryResetItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "Factory Reset");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setFactoryReset();
	}

	public void setFactoryResetDownViewListener(ShowFactoryResetDownViewListener factoryResetDownViewListener) {
		this.factoryResetDownViewListener = factoryResetDownViewListener;
	}

	public void setFactoryReset() {
		iconImageView.setVisibility(View.GONE);
		LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameParams.setMargins(0, 0,0, 0);
		nameTextView.setLayoutParams(nameParams);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 3, 0);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setLayoutParams(params);
		infoImageView.setImageDrawable(null);
		infoImageView.setBackgroundResource(DRAWABLE_RESET_DOWN);
		
	}

	@Override
	public void doSomethingAboutSystem() {
		if(isResetOpen){
			infoImageView.setBackgroundResource(DRAWABLE_RESET_DOWN);
			isResetOpen = false;
		}else{
			infoImageView.setBackgroundResource(DRAWABLE_RESET_UP);
			isResetOpen = true;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {

				Handler handler=new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						factoryResetDownViewListener.onViewOpenChangeed(isResetOpen);
					}
				});
			};
		}).start();
		
	}
	
	public interface ShowFactoryResetDownViewListener{
		public void onViewOpenChangeed(boolean isOpened);
	}
	
	
}
