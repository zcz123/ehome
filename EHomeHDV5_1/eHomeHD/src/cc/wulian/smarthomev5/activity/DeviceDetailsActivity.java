package cc.wulian.smarthomev5.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class DeviceDetailsActivity extends EventBusActivity
{

	public static Activity instance;
	DeviceDetailsFragment fragment=null;
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null){
			instance=this;
			fragment = new DeviceDetailsFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, fragment)
			.commit();
		}
	}
	public WulianFragment GetCurrentFragment(){
		return fragment;
	}
	//add_by_yanzy_at_2016-7-11:此处会影响所有的设备，暂时先注释掉，音乐盒使用用户比较少，先取消音乐盒的这个功能，后续有用户的确需要这个功能，再重新添加，但不能影响其它设备。
	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// 音量减小
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			Collection<WulianDevice> devicesMusicboxDownVolume = DeviceCache
					.getInstance(this)
					.getDeviceByType(
							AccountManager.getAccountManger().getmCurrentInfo()
									.getGwID(),
							ConstUtil.DEV_TYPE_FROM_GW_MUSIC_BOX);
			for (WulianDevice device : devicesMusicboxDownVolume) {
				((WL_E4_MusicBox) device).downVolume();

			}
			return true;
			// 音量增大
		case KeyEvent.KEYCODE_VOLUME_UP:
			Collection<WulianDevice> devicesMusicboxUpVolume = DeviceCache
					.getInstance(this)
					.getDeviceByType(
							AccountManager.getAccountManger().getmCurrentInfo()
									.getGwID(),
							ConstUtil.DEV_TYPE_FROM_GW_MUSIC_BOX);
			for (WulianDevice device : devicesMusicboxUpVolume) {
				((WL_E4_MusicBox) device).upVolume();

			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	*/
	
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(this.fragment!=null&&data!=null){
			if(this.fragment.getCurDevice()!=null){
				data.putExtra("requestCode", requestCode);
				data.putExtra("resultCode", resultCode);
				this.fragment.getCurDevice().OnRefreshResultData(data);
			}
		}
	}
	
}
