package cc.wulian.smarthomev5.eyecat;

import com.eques.icvss.api.ICVSSInstanceCreator;
import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.iface.ICVSSRoleType;

/**
 * 
 * ICVSS框架初始化类 
 *
 */
public class ICVSSUserModule {
	
	private static final String TAG = "UserICVSSModule";
	
	private Object obj = new Object();
    
	private static ICVSSUserModule mUserICVSSModule;
	private ICVSSUserInstance mIcvssInstance;
	
	public static ICVSSUserModule getInstance(ICVSSListener listener) {
		if (null == mUserICVSSModule) {
			synchronized (ICVSSUserModule.class) {
				if (null == mUserICVSSModule) {
					mUserICVSSModule = new ICVSSUserModule(listener);
				}
			}
		}
		return mUserICVSSModule;
	}
	
	private ICVSSUserModule(ICVSSListener listener) {
		synchronized (obj) {
			mIcvssInstance = ICVSSInstanceCreator.createUserInstance(ICVSSRoleType.CLIENT, listener);
		}
	}
	
	public ICVSSUserInstance getIcvss() {
		return mIcvssInstance;
	}
	
	public void closeIcvss(){
		synchronized (obj) {
			if (null != mUserICVSSModule) {
				mUserICVSSModule = null;
			}
			
			if (null != mIcvssInstance) {
				ICVSSInstanceCreator.close();
				mIcvssInstance = null;
				
			}
		}
	}
}
