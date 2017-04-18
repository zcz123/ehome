package cc.wulian.smarthomev5.fragment.monitor;

import android.view.LayoutInflater;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.tools.UpdateCameraAPKManger;
import cc.wulian.smarthomev5.tools.UpdateCameraAPKManger.NewVersionDownloadListener;
import cc.wulian.smarthomev5.view.UpdateProcessDialog;

import com.yuantuo.customview.ui.WLToast;

public abstract class AbstractMonitorView 
{
	protected UpdateCameraAPKManger updateManager;
	protected UpdateProcessDialog progessDialog = null ;
	protected View view;
	protected BaseActivity mContext;
	protected LayoutInflater inflater;
	protected MainApplication mApp = MainApplication.getApplication();
	protected CameraInfo cameraInfo;
	public AbstractMonitorView( BaseActivity context ,CameraInfo info)
	{
		this.mContext = context;
		this.cameraInfo = info;
		inflater = LayoutInflater.from(this.mContext);
	}
	public abstract View onCreateView();
	public abstract void onViewCreated();
	public void setUID(String uid){
		
	}
	/**
	 * 
	* @Title: showChangeVersionUpdateDialog 
	* @Description: TODO(显示dialog，让用户选择是否下载最新的apk) 
	* @throws
	 */

	protected void showDownloadOrUpdateProgress() {
		updateManager.setNewVersionDownloadListener(new NewVersionDownloadListener() {
			
			@Override
			public void processing(int present) {
				if(progessDialog == null){
					progessDialog = new UpdateProcessDialog(mContext);
					progessDialog.show();
				}
				progessDialog.setProgess(present);
				if(present >= 100){
					progessDialog.dismiss();
					progessDialog = null;
					updateManager.startInstall();
				}
			}
			
			@Override
			public void processError(Exception e) {
				WLToast.showToast(mContext,mContext.getString(R.string.set_version_update_erro),WLToast.TOAST_SHORT);
				if(progessDialog != null){
					progessDialog.dismiss();
					progessDialog = null;
				}
			}
		});
	}
}
