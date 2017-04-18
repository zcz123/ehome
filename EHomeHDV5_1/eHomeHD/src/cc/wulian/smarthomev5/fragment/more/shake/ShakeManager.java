package cc.wulian.smarthomev5.fragment.more.shake;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.ShakeDao;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;

public final class ShakeManager implements SensorEventListener
{
	private static final int SPEED_THRESHOLD = 200;
	private static final long SHAKE_TIME_INTERVAL = 500;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private long mLastShakeTime;
	private float mLastX, mLastY, mLastZ;
	private int mRepetCount;
	private static ShakeManager shakeManager ;
	private MainApplication application ;
	private AccountManager accountManager = AccountManager.getAccountManger();
	private ShakeDao shakeDao  = ShakeDao.getInstance();
	public static List<ShakeEntity> shakeEntity = new ArrayList<ShakeEntity>();
	private ShakeListener shakeListener;
	private ShakeManager(){
		application = MainApplication.getApplication();
		mSensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null) {
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		cacheShakeData();
	}
	private void createShakeSuccessDialog(Context context){
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setPositiveButton(R.string.common_ok);
		builder.setMessage(R.string.more_shake_execute_success);
		builder.setNegativeButton(null);
		WLDialog sucessDialog = builder.create();
		sucessDialog.show();
	}
	public static ShakeManager getInstance(){
		if(shakeManager == null){
			shakeManager = new ShakeManager();
		}
		return shakeManager;
	}
	public void beginListenShake(ShakeListener listener) {
		if (ensureSensor()) {
			this.shakeListener = listener;
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	public void cancelListenShake() {
		if (ensureSensor()) {
			this.shakeListener = null;
			mSensorManager.unregisterListener(this, mSensor);
		}
	}
	private boolean ensureSensor() {
		return mSensorManager != null && mSensor != null;
	}
	/**
	 * 初始化缓存摇一摇数据
	 */
	private void cacheShakeData() {
		ShakeManager.shakeEntity.clear();
		ShakeEntity senceEntity = createDefaultShakeEntity();
		ShakeManager.shakeEntity.add(0, senceEntity);
		ShakeEntity shakeEntity = new ShakeEntity();
		shakeEntity.setGwID(accountManager.getmCurrentInfo().getGwID());
		List<ShakeEntity> entites = shakeDao.findListAll(shakeEntity);
		for (ShakeEntity entity : entites) {
			if (ShakeEntity.TYPE_SCENE.equals(entity.getOperateType())) {
				ShakeManager.shakeEntity.remove(0);
				ShakeManager.shakeEntity.add(0, entity);
			} else {
				ShakeManager.shakeEntity.add(entity);
			}
		}
	}
	/**
	 * 执行摇一摇
	 */
	public void executeShake(Context context) {
		try{
			if(AccountManager.getAccountManger().isConnectedGW() == false) {
				return;
			}
			List<ShakeEntity> entities = ShakeManager.shakeEntity;
			for (ShakeEntity entity : entities) {
				 //执行场景
				if (ShakeEntity.TYPE_SCENE.equals(entity.getOperateType())) {
					SceneInfo sceneInfo = application.sceneInfoMap
							.get(entity.gwID + entity.operateID);
					if (sceneInfo != null) {
						SceneInfo newInfo = sceneInfo.clone();
						newInfo.setStatus(CmdUtil.SCENE_USING);
						SceneManager.switchSceneInfo(context, newInfo, true);
					}
				} else {//执行设备
					WulianDevice device = DeviceCache.getInstance(context)
							.getDeviceByID(context, entity.gwID,
									entity.operateID);
					if (device != null && device.isDeviceOnLine()) {
						if(StringUtil.isNullOrEmpty(entity.ep)){
							entity.ep = WulianDevice.EP_0;
						}
						device.controlDevice(entity.ep, entity.epType,entity.epData);
					}
				}
			}
			createShakeSuccessDialog(context);
		}catch(Exception e){
			
		}
	}

	/**
	 * 创建默认的摇一摇数据
	 * 
	 * @return
	 */
	private ShakeEntity createDefaultShakeEntity() {
		ShakeEntity senceEntity = new ShakeEntity();
		senceEntity.setGwID(accountManager.getmCurrentInfo().getGwID());
		senceEntity.setOperateID(CmdUtil.ID_UNKNOW);
		senceEntity.setOperateType(ShakeEntity.TYPE_SCENE);
		return senceEntity;
	}
	@Override
	public void onSensorChanged( SensorEvent event ) {
		long nowTime = System.currentTimeMillis();
		long interval = nowTime - mLastShakeTime;

		if (interval < SHAKE_TIME_INTERVAL) return;

		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		float xGap = x - mLastX;
		float yGap = y - mLastY;
		float zGap = z - mLastZ;

		double speed = Math.sqrt(xGap * xGap + yGap * yGap + zGap * zGap) / interval * 10000;
		if (speed >= SPEED_THRESHOLD) {
			mRepetCount++;
			if ( mRepetCount >= 3) {
				boolean opShake = Preference.getPreferences().getBoolean(IPreferenceKey.P_KEY_OPEN_SHAKE, false);
				PowerManager pm = (PowerManager)application.getSystemService(Context.POWER_SERVICE);
				if (opShake&&pm.isScreenOn()) {
					if(shakeListener != null){
						shakeListener.onShake();
					}
				}
				mRepetCount = 0;
			}
		}

		mLastX = x;
		mLastY = y;
		mLastZ = z;
		mLastShakeTime = nowTime;
	}

	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy ) {
	}
	
	public interface ShakeListener{
		public void onShake();
	}
}