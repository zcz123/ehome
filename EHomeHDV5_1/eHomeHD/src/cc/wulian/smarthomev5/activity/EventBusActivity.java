package cc.wulian.smarthomev5.activity;

import android.os.Bundle;

import android.view.ViewGroup;
import android.widget.Toast;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.GatewayEvent;

import com.yuantuo.customview.ui.WLToast;

import de.greenrobot.event.EventBus;

public class EventBusActivity extends BaseActivity {

	protected EventBus mEventBus = EventBus.getDefault();

	public EventBus getEventBus() {
		return mEventBus;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mEventBus.register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mEventBus.unregister(this);
	}

	public void onEventMainThread(GatewayEvent event) {
		if (GatewayEvent.ACTION_DISCONNECTED.equals(event)) {
			finshSelf();
		}
		if (GatewayEvent.ACTION_CHANGE_PWD.equals(event.action)) {
			WLToast.showToast(
					this,
					getResources()
							.getString(
									R.string.PWSETOK),
					Toast.LENGTH_SHORT);
		}
	}

	@Override
	public ViewGroup getContainerRootView() {
		// TODO Auto-generated method stub
		return  (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
	}
}
