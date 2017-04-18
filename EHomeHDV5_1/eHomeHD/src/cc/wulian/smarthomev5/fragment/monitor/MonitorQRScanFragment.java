package cc.wulian.smarthomev5.fragment.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.ScanEvent;
import cc.wulian.smarthomev5.fragment.singin.QRScanFragmentV5;
import com.actionbarsherlock.app.SherlockFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

public class MonitorQRScanFragment extends SherlockFragment{

	private EventBus mEventBus = EventBus.getDefault();
	@ViewInject(R.id.monitor_scan_back)
	private LinearLayout mScanLayout;
	@ViewInject(R.id.monitor_scan_get_data)
	private LinearLayout mMonitorScan;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_san_common, container,
				false);
		ViewUtils.inject(this, rootView);
		getFragmentManager().beginTransaction().replace(R.id.monitor_scan_get_data, new QRScanFragmentV5(),
				QRScanFragmentV5.class.getSimpleName()).commit();
		return rootView;
	}

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		mEventBus.register(this);
		EventBus.getDefault().post(
				new ScanEvent(ScanEvent.CODE_REQUEST_SCAN));
	}


	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub
		mEventBus.unregister(this);
	}
	private void initListener(){
		mScanLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(
						new ScanEvent(ScanEvent.CODE_RESULT_CANCLE));
			}
		});
	}
	
	
	public void onEventMainThread(ScanEvent event) {
		LogUtils.d(event.toString());
		switch (event.getCode()) {
		case ScanEvent.CODE_REQUEST_SCAN:
			QRScanFragmentV5 fragmentStart = (QRScanFragmentV5) getFragmentManager()
					.findFragmentByTag(QRScanFragmentV5.class.getSimpleName());
			fragmentStart.startScan();
			break;
		case ScanEvent.CODE_RESULT_OK:
		case ScanEvent.CODE_RESULT_CANCLE:
			QRScanFragmentV5 fragmentStop = (QRScanFragmentV5) getFragmentManager()
					.findFragmentByTag(QRScanFragmentV5.class.getSimpleName());
			fragmentStop.stopScan();
			
			if(!TextUtils.isEmpty(event.getResult())){
				String resultString = event.getResult();
				Intent resultIntent = new Intent();
				resultIntent.putExtra(EditMonitorInfoFragment.RESULT_UID, resultString);
				getActivity().setResult(EditMonitorInfoFragment.RESULT_OK, resultIntent);
			}
			this.getActivity().finish();
			break;
		default:
			break;
		}

	}

}
