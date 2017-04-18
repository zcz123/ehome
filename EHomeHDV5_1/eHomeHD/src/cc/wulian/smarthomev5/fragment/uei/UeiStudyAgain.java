package cc.wulian.smarthomev5.fragment.uei;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.databases.entitys.Task;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import cc.wulian.smarthomev5.utils.TargetConfigure;
import de.greenrobot.event.EventBus;

/**
 * UEI设备重新学习管理
 * @author yuxiaoxuan
 * @date 2016年9月13日11:06:26
 *
 */
public class UeiStudyAgain {
	private Context mcontext;
	private Activity mactivity;
	private String studyCode;//学习码
	private UeiCommonEpdata uce=null;
	private static WLDialog dialog_bingdingAgain;
	private static WLDialog dialog_bingdingSucess;
	private static WLDialog dialog_bingdingFailed;

	private static int timeoutsec=20;//超时时间
	private static int currsec=0;//当前时间
	private String TAG="UeiStudyAgain";

	public UeiStudyAgain(Context context,UeiUiArgs args){
		this.mcontext=context;
		uce=new UeiCommonEpdata(args.getGwID(), args.getDevID(), args.getEp());
		EventBus.getDefault().register(this);
	}
	public static void InitData(){
		if(dialog_bingdingAgain!=null&&dialog_bingdingAgain.isShowing()){
			dialog_bingdingAgain.dismiss();
			dialog_bingdingAgain=null;
		}
		if(dialog_bingdingSucess!=null&&dialog_bingdingSucess.isShowing()){
			dialog_bingdingSucess.dismiss();
			dialog_bingdingSucess=null;
		}
		if(dialog_bingdingFailed!=null&&dialog_bingdingFailed.isShowing()){
			dialog_bingdingFailed.dismiss();
			dialog_bingdingFailed=null;
		}
		dialog_bingdingAgain=null;
		dialog_bingdingSucess=null;
		dialog_bingdingFailed=null;
	}
	public void SetMyActivity(Activity activity){
		this.mactivity=activity;
	}
	public void setStudyCode(String studyCode){
		this.studyCode=studyCode;
	}
	public void BeginStudy(View view){
		iniPopupWidow(view);
	}
	private void iniPopupWidow(View v) {
		final DownUpMenuList downMenu = new DownUpMenuList(this.mcontext);
		DownUpMenuItem bindingItem = new DownUpMenuItem(this.mcontext) {

			@Override
			public void initSystemState() {
				String strText=mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_relearn);
				mTitleTextView.setText(strText);
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_topcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				bindingAgain();
				downMenu.dismiss();
			}
		};
		

		DownUpMenuItem cancelItem = new DownUpMenuItem(this.mcontext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(this.context.getResources().getString(
						R.string.cancel));
				linearLayout.setPadding(0, 30, 0, 0);
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_allcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				downMenu.dismiss();
			}
		};
		
		ArrayList<DownUpMenuItem> menuItems = new ArrayList<DownUpMenuList.DownUpMenuItem>();
		menuItems.add(bindingItem);
		menuItems.add(cancelItem);
		downMenu.setMenu(menuItems);
		downMenu.showBottom(v);
	}

	private void bindingAgain() {
		if(dialog_bingdingAgain==null){
			String strTitle=mcontext.getString(cc.wulian.app.model.device.R.string.uei_html_achieve_sign);
			String strPosBtnText=mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_matching);

			WLDialog.Builder builder = new Builder(this.mactivity);
			builder.setContentView(R.layout.uei_studing)
					.setTitle(strTitle)
					.setPositiveButton(strPosBtnText)
					.setDismissAfterDone(false).setListener(new MessageListener() {

				@Override
				public void onClickPositive(View contentViewLayout) {

				}
				public void onClickNegative(View contentViewLayout) {

				}

			});
			builder.setCancelOnTouchOutSide(false);
			dialog_bingdingAgain = builder.create();
			dialog_bingdingAgain.setCancelable(false);
		}
		if(!dialog_bingdingAgain.isShowing()){
			String epData=UeiStudyAgain.this.uce.getEpDataForUeiStudy(studyCode);
			if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
				Log.d(TAG, "studyCode="+studyCode+" epData="+epData);
			}

			UeiStudyAgain.this.uce.sendCommand12(this.mactivity,epData);
			initTimerTask();
//			startTimerTask();
			dialog_bingdingAgain.show();
		}
	}

	private void bindingSucess() {
		if (dialog_bingdingSucess == null) {
			createDialog_bingdingSucess();
			dialog_bingdingSucess.setCancelable(false);
		}
		if (!dialog_bingdingSucess.isShowing()) {
			if(this.mactivity!=null&&!this.mactivity.isFinishing()){
				dialog_bingdingSucess.show();
			}else {
				if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
					Log.d(TAG, "父窗体已被系统销毁！");
				}
				String strSuc=mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_succeed);
				Toast.makeText(this.mcontext,strSuc,Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void createDialog_bingdingSucess(){
		WLDialog.Builder builder = new Builder(this.mactivity);
		String strSuc=mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_succeed);
		builder.setContentView(R.layout.uei_study_success)
				.setTitle(strSuc)
				.setPositiveButton(this.mcontext.getString(R.string.common_ok))
				.setDismissAfterDone(false).setListener(new MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {

				dialog_bingdingSucess.dismiss();
			}

			public void onClickNegative(View contentViewLayout) {

				dialog_bingdingSucess.dismiss();
			}

		});
		builder.setCancelOnTouchOutSide(false);
		dialog_bingdingSucess = builder.create();
		dialog_bingdingSucess.setCancelable(false);
	}
	private void bindingFailed() {
		if(dialog_bingdingFailed==null){
			WLDialog.Builder builder = new Builder(this.mactivity);
			builder.setContentView(R.layout.uei_study_failed)
					.setTitle(mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_learn_fail))
					.setPositiveButton(mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_relearn))
					.setNegativeButton(this.mcontext.getString(R.string.common_cancel))
					.setDismissAfterDone(false).setListener(new MessageListener() {

				@Override
				public void onClickPositive(View contentViewLayout) {
					if(dialog_bingdingFailed!=null&&dialog_bingdingFailed.isShowing()){
						dialog_bingdingFailed.dismiss();
					}
					bindingAgain();
				}
				public void onClickNegative(View contentViewLayout) {
					dialog_bingdingFailed.dismiss();

				}

			});
			builder.setCancelOnTouchOutSide(false);
			dialog_bingdingFailed = builder.create();
			dialog_bingdingFailed.setCancelable(false);
		}
		if(!dialog_bingdingFailed.isShowing()){
			String strFailed=mcontext.getString(cc.wulian.app.model.device.R.string.uei_learn_learn_fail);
			try {
				if(this.mactivity!=null&&!this.mactivity.isFinishing()){
					dialog_bingdingFailed.show();
				}else {
					if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
						Log.d(TAG, "父窗体已被系统销毁！");
						Toast.makeText(this.mcontext,strFailed,Toast.LENGTH_SHORT).show();
					}
				}
			}catch (Exception ex){
				if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
					Log.d(TAG, ex.toString());
					Toast.makeText(this.mcontext,strFailed,Toast.LENGTH_SHORT).show();
				}

			}
		}

	}
	protected void finalize(){
		if(dialog_bingdingAgain!=null&&dialog_bingdingAgain.isShowing()){
			dialog_bingdingAgain.dismiss();
			dialog_bingdingAgain=null;
		}
		if(dialog_bingdingSucess!=null&&dialog_bingdingSucess.isShowing()){
			dialog_bingdingSucess.dismiss();
			dialog_bingdingSucess=null;
		}
		if(dialog_bingdingFailed!=null&&dialog_bingdingFailed.isShowing()){
			dialog_bingdingFailed.dismiss();
			dialog_bingdingFailed=null;
		}
		EventBus.getDefault().unregister(this);
	}
	public void onEventMainThread(DeviceEvent event) {
		String epdata = "";
		String eptype = "";
		if (event != null && event.deviceInfo != null && event.deviceInfo.getDevEPInfo() != null) {
			epdata = event.deviceInfo.getDevEPInfo().getEpData();
			eptype = event.deviceInfo.getDevEPInfo().getEpType();
		} else {
			//此处无代码
		}
		if (eptype.equals("23")) {
			if (currsec > 0 && currsec <= timeoutsec) {
				if (dialog_bingdingAgain != null && dialog_bingdingAgain.isShowing()) {
					dialog_bingdingAgain.dismiss();
				}
				Log.d(TAG, "epdata=" + epdata);
				stopTimerTask();
				String lastValue=epdata.substring(epdata.length()-1);
				if (lastValue.equals("0")) {
					this.mactivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (dialog_bingdingSucess != null && dialog_bingdingSucess.isShowing()) {
								return;
							}
							bindingSucess();
						}
					});
				} else {
					if (dialog_bingdingFailed != null && dialog_bingdingFailed.isShowing()) {
						//该处无代码，原因是为了避免多个失败弹窗的出现
					}else{
						bindingFailed();
					}
				}
			}
		}

	}
	private TimerTask task=null;//计时器任务
	private Timer timer=null;//计时
	private void initTimerTask(){
		task=new TimerTask() {
			@Override
			public void run() {
				if(currsec>=0){
					currsec++;
					if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
						Log.d("UeiStudyAgain", "currsec="+currsec);
					}
					if(currsec>timeoutsec){//因超时学习失败
						stopTimerTask();
						if(UeiStudyAgain.this.mactivity!=null){
							UeiStudyAgain.this.mactivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (dialog_bingdingAgain != null && dialog_bingdingAgain.isShowing()) {
										dialog_bingdingAgain.dismiss();
									}
									bindingFailed();
								}
							});
						}

					}
				}
			}
		};
		timer=new Timer(true);
		currsec=0;
		timer.schedule(task,1000L,1000L);
	}
	private void startTimerTask(){
		currsec=0;
		timer.schedule(task,1000,1000);
	}

	private void stopTimerTask(){
		if(timer!=null){
			timer.cancel();
			currsec=-1;
		}
	}
}
