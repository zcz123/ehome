package cc.wulian.smarthomev5.tools;

import java.util.List;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.receiver.OperBaiduReceiver;
import cc.wulian.smarthomev5.receiver.baidu.BaiduPushMessageReceiver;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;

import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class WLEagleOperationManager {
	private final static String TAG = "WLEagleOperationManager";
	private Context mContext = null;
	public static WLEagleOperationManager instance = null;
	private Handler mainThreadHandler = null;
	private OperBaiduReceiver operBaiduReceiver = null;
	private String mappingToken = null;
	private List<AMSDeviceInfo> amsDeviceInfos = null;
	private List<GatewayInfo> gatewayInfos = null;
	private TaskExecutor mTaskExecutor = null;
	private String deviceId = null;
	private Callback mCallback = new Callback() {
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.GATEWAYINFOUSER:
				Log.i(TAG, "===GATEWAYINFOUSER===");
				gatewayInfos = (List<GatewayInfo>) msg.obj;
				getDeviceInfoBywld(gatewayInfos);
				break;
			case HandlerConstant.GATEDEVICEINFOBYGWID:
				if (dataBackListener != null) {
					if ((amsDeviceInfos = (List<AMSDeviceInfo>) msg.obj).size() > 0) {
						Log.i(TAG, "===GATEDEVICEINFOBYGWID===");
						dataBackListener.onDeviceListBack(amsDeviceInfos);
					}else {
						dataBackListener.noDevicelist();
					}
					final List<String> list=CameraUtil.isUnBind(amsDeviceInfos);
					if (list.size()>0){
						new Thread(new Runnable() {
							@Override
							public void run() {
								for (String uid:list){
									BaiduPushManager.getInstance().registerTutkServer(null, uid, BaiduPushManager.unreg_mapping, mainThreadHandler);
									BaiduPushManager.getInstance().stopWork(mContext);
								}
							}
						}).start();
					}
					return false;
				}
				break;
			case HandlerConstant.BAIDU_REGISTER_SERVER_BY_GET:
				if(msg.obj != null){
					deviceId = (String) msg.obj;
					registerTutkServer(deviceId);
					Log.i(TAG, "===注册服务===");
				}
				break;
			case HandlerConstant.BAIDU_REGISTER_MAPPING_BY_GET:
				if(deviceId != null){
					registerTutkMapping(deviceId);
					Log.i(TAG, "===注册映射===");
				}
				break;
			case HandlerConstant.SUCCESS:
				Log.i(TAG, "===注册成功===");
				Config.isResBaiduPush = false;
				break;
			case HandlerConstant.ERROR:
				Log.e(TAG, "===("+msg.obj.toString()+")===");
				break;
			case 200:
				Log.e(TAG, "===(曾未解绑的 解绑成功！！)===");
				break;

			}
			return false;
		}
	};
	public EagleDataBackListener dataBackListener = null;

	public interface EagleDataBackListener {
		public void onDeviceListBack(List<AMSDeviceInfo> amsDeviceInfos);
		public void noDevicelist(); // add  mabo 在没有获取到数据时，adapter也要更新
	}

	public static WLEagleOperationManager getInstance(Context mContext) {
		if (instance == null) {
			synchronized (WLEagleOperationManager.class) {
				if (instance == null) {
					instance = new WLEagleOperationManager(mContext);
				}
			}
		}
		return instance;
	}

	private WLEagleOperationManager(Context mContext) {
		mainThreadHandler = new Handler(Looper.getMainLooper(), mCallback);
		this.mContext = mContext;
		this.mTaskExecutor = TaskExecutor.getInstance();
	}

	public void getDeviceOfUser() {
		DevicesUserManage.gatewayInfoOfUser(mainThreadHandler,
				HandlerConstant.GATEWAYINFOUSER, Config.CAMERA);
	}

	private void getDeviceInfoBywld(List<GatewayInfo> gatewayInfos) {
		if(gatewayInfos.size()>0){
			DevicesUserManage.getDeviceInfoBywld(gatewayInfos, mainThreadHandler,
				HandlerConstant.GATEDEVICEINFOBYGWID);
		}else {
			dataBackListener.noDevicelist();
			final List <String> Unbind= CameraUtil.getEagleUidList();//如果获取的监控列表为空，在本地还能拿到，就直接注销tutk
			if (Unbind.size()>0){
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (String uid:Unbind){
							BaiduPushManager.getInstance().registerTutkServer(null, uid, BaiduPushManager.unreg_mapping, mainThreadHandler);
						}
					}
				}).start();
			}
		}
	}
	public void setDataBackListener(EagleDataBackListener dataBackListener) {
		this.dataBackListener = dataBackListener;
	}
	private void registerBaiduPush(final String deviceId) {
		this.mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
					BaiduPushManager.getInstance().startWork(mContext);
					registerMsgReceiver(deviceId);
			}
		});
	}
	private void registerMsgReceiver(final String deviceId) {
		if (operBaiduReceiver == null) {
			operBaiduReceiver = new OperBaiduReceiver() {
				@Override
				public void doReg(Intent mIntent) {
					int code = mIntent.getIntExtra("code", -1);
					if (code == 0) {
						mappingToken = (String) mIntent
								.getSerializableExtra("userId")
								+ "@"
								+ (String) mIntent
										.getSerializableExtra("channelId");
						Message msg = new Message();
						msg.obj = deviceId;
						msg.what =HandlerConstant.BAIDU_REGISTER_SERVER_BY_GET;
						mainThreadHandler.sendMessage(msg);
					}
				}
			};
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(OperBaiduReceiver.ACTION);
			this.mContext.registerReceiver(operBaiduReceiver, intentFilter);
		}
	}

	private void registerTutkServer(final String eagleId) {
		this.mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (eagleId != null) {
					BaiduPushManager.getInstance().registerTutkServer(
							mappingToken, eagleId, BaiduPushManager.reg_client,
							mainThreadHandler);
				}
			}
		});
	}
	private void registerTutkMapping(final String eagleId) {
		this.mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (eagleId != null) {
					BaiduPushManager.getInstance().registerTutkServer(
							mappingToken, eagleId,
							BaiduPushManager.reg_mapping, mainThreadHandler);
				}
			}
		});
	}
	public  void findMainUserByAMS(List<AMSDeviceInfo> amsDeviceInfos) {// TODO 逻辑判断
		if(amsDeviceInfos.size()>0){
			for(AMSDeviceInfo obj:amsDeviceInfos){
				if(obj.getIsAdmin()){
					Log.i(TAG, obj.getDeviceId()+"    "+obj.getIsAdmin());
					if(Config.isResBaiduPush){
						registerBaiduPush(obj.getDeviceId());//注册服务
					}
//					break;  //此处break 岂不是只会注册一个
				}
			}
		}
	}
	public void destoryInstance() {
		if (instance != null) {
			if (operBaiduReceiver != null) {
				instance.mContext.unregisterReceiver(operBaiduReceiver);
			}
			instance = null;
		}
	}
}
