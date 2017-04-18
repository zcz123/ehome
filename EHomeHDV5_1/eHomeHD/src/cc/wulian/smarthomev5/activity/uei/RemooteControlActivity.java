package cc.wulian.smarthomev5.activity.uei;

import android.os.Bundle;
import android.widget.Toast;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.event.DeviceIREvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

/*原先是每一种设备都创建一个Activity,再创建一个对应的Fragment
 * 其实Activity可以公用，而Fragment可以分开
 * 因为Activity接收的参数是共同的，所以可以写到一块
 * */
/**
 * UEI公用的Activity
 * @author yuxiaoxuan
 *
 */
public class RemooteControlActivity extends EventBusActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args=null;
		WulianFragment wulianFragment=null;
		if(getIntent()!=null){
			String model=getIntent().getStringExtra("deviceType");
			wulianFragment=WL_23_IR_Resource.getUeiFragment(model);
			args=getIntent().getBundleExtra("args");
		}
		if(wulianFragment!=null){
			wulianFragment.setArguments(args);
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, wulianFragment)
			.commit();
		}
		else{
			Toast.makeText(this, "尚未实现！", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
	}
	public void onEventMainThread(DeviceIREvent event){
		DeviceIREvent event1=event;
	}
	
	
}
