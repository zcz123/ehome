package cc.wulian.smarthomev5.tools;
import java.util.ArrayList;
import java.util.List;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.server.receiver.Smit406_Receiver;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.camera.DeskTopCameraEntity;
import cc.wulian.smarthomev5.event.DeviceUeiItemEvent;
import cc.wulian.smarthomev5.utils.NetworkUtil;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
public class WLDeskCameraOperationManager {
	private final static String TAG = "WLDeskCameraOperationManager";
	public static WLDeskCameraOperationManager instance =null;
	private Context mContext = null;
	private  TaskExecutor mTaskExecutor  = null;
	private List<DeskTopCameraEntity> deskTopCameraList = null;
	public DeskCameraDataBackListener deskCameraDataBackListener = null;
	private WLDialog dialog = null;
	private WLPresettingDataManager wlPresettingDataManager = null;
	public interface DeskCameraDataBackListener{
		public void onDeviceListBack(List<DeskTopCameraEntity> deskTopCameraEntities);
		public void onDeviceBack(IOTCameraBean iotCameraBean);
	}
	public void setDeskCameraDataBackListener(
			DeskCameraDataBackListener deskCameraDataBackListener) {
		this.deskCameraDataBackListener = deskCameraDataBackListener;
	}
	public static WLDeskCameraOperationManager getInstance(Context mContext){
		if(instance == null){
			synchronized(WLDeskCameraOperationManager.class){
				if(instance == null){
					instance = new WLDeskCameraOperationManager(mContext);
				}
			}
		}
		return instance;
	}
	private WLDeskCameraOperationManager(Context context){
		this.mContext = context;
		this.mTaskExecutor = TaskExecutor.getInstance();
		wlPresettingDataManager = new WLPresettingDataManager(this.mContext);
	}
	public void getDeviceInfoByGw(final GatewayInfo gwInfo){
		mTaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if(gwInfo!=null){
					if(gwInfo.getGwVer().charAt(2) == '9'){
						if(deskCameraDataBackListener!=null){
							deskTopCameraList = new ArrayList<DeskTopCameraEntity>();
							deskTopCameraList.add(new DeskTopCameraEntity(gwInfo));
							WLDeskCameraOperationManager.this.deskCameraDataBackListener.onDeviceListBack(deskTopCameraList);
						}
					}
				}
			}
		});
	} 
	public void checkNetwork(DeskTopCameraEntity obj){
		if (NetworkUtil.isWIFI(mContext)) {
			if(deskCameraDataBackListener!=null){
				deskCameraDataBackListener.onDeviceBack(clone(obj));
				return;
			}
		} else if (NetworkUtil.isMobileConnected(mContext)) {
			this.isMobileDataRemind(obj);
		} else {
		}
	}
	private  void isMobileDataRemind(final DeskTopCameraEntity obj) {
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(null);
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setMessage(mContext.getResources().getString(R.string.cateye_wifiMode_hit));
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				if(deskCameraDataBackListener!=null){
					deskCameraDataBackListener.onDeviceBack(WLDeskCameraOperationManager.this.clone(obj));
					return;
				}
			}
			@Override
			public void onClickNegative(View contentViewLayout) {
			}
		});
		dialog = builder.create();
		dialog.show();
	}
	private IOTCameraBean clone(DeskTopCameraEntity obj) {
		IOTCameraBean info = new IOTCameraBean();
		info.setUid(obj.getTutkUID());
		info.setPassword(obj.getTutkPASSWD());
		info.setGwId(obj.getGwID());
		info.setCamName(obj.getGwName());
		return info;
	}
	public void destoryInstance(){
		if(instance!=null){
			instance.clear();
		}
	}
	private void clear(){
		wlPresettingDataManager.destroy();
		instance = null;
		deskTopCameraList = null;
		deskCameraDataBackListener = null;
		mTaskExecutor = null;
		mContext = null;
		dialog = null;
	}
}
