package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.st_SearchDeviceInfo;
import com.wulian.iot.Config;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.yuantuo.netsdk.TKCamHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;


import cc.wulian.smarthomev5.fragment.more.wifi.WifiDataManager;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 搜索猫眼 热点 界面
 * @author syf
 *
 */
public class HawkeyeFindHotActivity extends SimpleFragmentActivity implements Callback,OnClickListener{
    private AnimationDrawable mAnimation;
    private boolean isWifi = true;
    private TheadAsyncTask ta;
    private List<String> wifiList = null;
	private final Handler mHandler = new Handler(this);
	private String AP,devicesID,ssid= null;//msgData 为 设备  编号 devicesID
	private Map<String,Object> maps = null;
	private ImageView img,titleBack;
	private Context mContext = HawkeyeFindHotActivity.this;
	private TaskExecutor mTaskExecutor  = TaskExecutor.getInstance();
	private  String aimWIFI = null;
	private final static String wifiTag = "CamAp";
	private  String TAG = "IOTCamera";
	private int [] iamges={com.wulian.icam.R.drawable.cat_eye_camera_wifi1,
			com.wulian.icam.R.drawable.cat_eye_camera_wifi2,com.wulian.icam.R.drawable.cat_eye_camera_wifi3};
	
	private int getWifiNumber = 0;  //获取Wifi设备的次数。
	private String gwID;
	 private String deviceID;
	 
	private static class HandlerConstant{
		public static List<Integer> remove(){
			List<Integer> code = new ArrayList<>();
			code.add(STEP1);
			code.add(STEP2);
			code.add(STEP3);
			code.add(error);
			code.add(success);
			return code;
		}
		private static final int STEP1 = 1;
		private static final int STEP2 = 2;
		private static final int STEP3 = 3;
		private static final long START_DELAY = 1000;
		private static final int error = 1000;
		private static final int success= 1001;
		
	}
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case HandlerConstant.success:
			Log.e(TAG, "handleMessage");
			Intent mIntent = new Intent(this,HawkeyeSettingWifiActivity.class);
			mIntent.putExtra("devicesID", devicesID);		
			mIntent.putExtra("gwID", gwID);
			mIntent.putExtra("devID", deviceID);
			mIntent.putExtra("ssid", ssid);
            startActivity(mIntent);
            finish();
			break;
		case HandlerConstant.error:			
			//不能写成汉字 应该从资源文件中获取，这样写不利于国际化。
			Toast.makeText(HawkeyeFindHotActivity.this, "寻找、切换网络失败", 3000).show();	
			
			
			Intent it=new Intent(mContext, HawkeyeWifiConnFailActivity.class);
			it.putExtra("gwID", gwID);
			it.putExtra("devID", deviceID);
			startActivity(it);
			break;
		}
		return false;
	}
	@Override
	public void root() {
		this.setContentView(R.layout.activity_cateye_connect_cramera);
	}
   @Override
   public void initView() {
//	img=(ImageView) findViewById(R.id.iv_wifi_connect_number);
	titleBack = (ImageView)findViewById(R.id.iv_cateye_titlebar_back);
   }
   @Override
    public void initEvents() {
	   titleBack.setOnClickListener(this);
    } 
    @Override 
    public void initData() {
    		
    	ssid =WifiDataManager.getInstance().getSSID(mContext);
    	
    	gwID =getIntent().getStringExtra("gwID");
		deviceID =getIntent().getStringExtra("devID");
		
    }
    private final void getWifiList(){
    	// TODO 需要判断系统搜索wifi功能是否打开
    	wifiList = null;
    	wifiList = WifiDataManager.getInstance().getWifiScanResultList();
    }
    @Override
    protected void onResume() {
    	super.onResume();
        ta=new TheadAsyncTask(img, iamges);
      	ta.execute();
    	this.startPlaySurfaceView();
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	this.stopPlaySurfaceView();
    }
    @Override
    protected void removeMessages() {
    	super.removeMessages();
    	for(Integer obj:HandlerConstant.remove()){
    		mHandler.removeMessages(obj);
    	}
    }
    /**启动动画*/
    protected void startAnimation(final AnimationDrawable animation) {
		if (animation != null && !animation.isRunning()) {
			animation.run();
		}
	}
    /**关闭动画*/
    protected void stopAnimation(final AnimationDrawable animation) {
		if (animation != null && animation.isRunning())
			animation.stop();
	}
    private Runnable animRunnable = new Runnable() {
		@Override
		public void run() {
			
			startAnimation(mAnimation);
			
		}
	};
	public void startPlaySurfaceView() {
		mHandler.postDelayed(animRunnable, HandlerConstant.START_DELAY);
		findAimWifiAndLink();
	}
	private final void findAimWifiAndLink(){
	
		if(!checkSSID()){
		getWifiList();
		getWifiNumber++;         //获取wifi次数超过限制。不能再获取了。
		if ( 30 == getWifiNumber) {
			getWifiNumber = 0;
			Log.i("IOTCamera", "-------次"+getWifiNumber);
			mHandler.sendEmptyMessage(HandlerConstant.error);	
			return;
		}
		Log.i("IOTCamera", "-------getWifiList");
		if(wifiList!=null&&wifiList.size()>0){
			//查找目标wifi
			aimWIFI = aimWifi();
			if(aimWIFI == null){
				this.findAimWifiAndLink();
				Log.e(TAG, "再次刷新列表");
			} else {
				this.linkAimWifi();
				Log.e(TAG, "连接目标wifi");
			}
		}
	} 	
}
	private final void linkAimWifi(){
  	   Toast.makeText(getApplicationContext(), "Wifi切换中，请稍后。。。", Toast.LENGTH_LONG).show();
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				
		     	
				 if (WifiDataManager.getInstance().connectWifi(aimWIFI,mContext)) {
					 Log.i(TAG, "-------------正在转换wifi成功"+aimWIFI);
					 for(int i = 0; i < 10; i++){
				            try {
				                Thread.sleep(1000);//睡眠1秒。循环300次就是300秒也就是五分钟
				                Log.i(TAG, "-------------正在转换wifi成功"+i);
				            } catch (InterruptedException e) {
				                e.printStackTrace();
				            }
				        }
					 
					 mHandler.sendEmptyMessage(HandlerConstant.success);
				  }else{
					  Log.i(TAG, "-------------正在转换wifi失败"+aimWIFI);
					  mHandler.sendEmptyMessage(HandlerConstant.error);
					  
				  }
				 
			}
		});		
	}
	private final String aimWifi(){
		String aim = null;
		for(String obj:wifiList){
			if(obj.contains(wifiTag)){
				aim = obj;
				break;
			}
		}
		return aim;
	}
	public void stopPlaySurfaceView() {
		stopAnimation(mAnimation);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.iv_cateye_titlebar_back:
			Config.isEagleNetWork = true;
			finish();
			break;
		}
	}
	 /**
	  * 判断wifi 是否切换成功
	  * @return
	  */
	public boolean checkSSID(){
		String ssid = WifiDataManager.getInstance().getSSID(this);
		if(ssid!=null){			
			return ssid.contains(wifiTag);
		}
		return false;
	}
	/**
	 *无线图标切换动画
	 *生成该类的对象，并调用其execute方法之后
	 *首先执行的是onPreExecute方法
	 *其次是执行doInBackground方法
	 * @author Administrator
	 *
	 */
	class TheadAsyncTask extends AsyncTask<Void, Integer, Integer>{
		ImageView img;
		int [] imgs;
		TheadAsyncTask(ImageView img,int [] imgs){
			this.img=img;
			this.imgs=imgs;
		}
		
		int i=0;
		@Override
		protected Integer doInBackground(Void... params) {
			while(i!=-1){
			for (int j = 0; j < imgs.length-1; j++) {
				SystemClock.sleep(1000);
				i++;
				publishProgress(i);
				}
				if (i==2) {
					i=0;
				}
			}
			return i;
		}
		//该方法运行在Ui线程内，可以对UI线程内的控件设置和修改其属性
		@Override
		protected void onPreExecute() {
			img.setImageResource(com.wulian.icam.R.drawable.cat_eye_camera_wifi1);
		}
		
		//在doInBackground方法当中，每次调用publishProgrogress()方法之后，都会触发该方法
		@Override
		protected void onProgressUpdate(Integer... values) {
			img.setImageResource(imgs[i]);
		}
		//在doInBackground方法执行结束后再运行，并且运行在UI线程当中
	    //主要用于将异步操作任务执行的结果展示给用户
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
}
