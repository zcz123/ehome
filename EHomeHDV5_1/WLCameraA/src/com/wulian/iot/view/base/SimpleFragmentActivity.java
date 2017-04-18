package com.wulian.iot.view.base;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.widght.DialogManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleFragmentActivity extends FragmentActivity implements I_DeskCamera{
	protected String TAG = getClass().getName().toString();
	protected ProgressDialog progressDialog,baseProgressDialog;// 单个请求时使用,一般由父类管理
	protected View baseContentView;
	protected Dialog mDiglog;
	protected  SharedPreferences sharedPreferences;//iot 通用sharedPreferences 请勿  在子类中随便添加
	protected Editor editor;
	protected static CameraHelper cameaHelper = null;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.cookie();
		this.root();
		this.initialize();
		Log.e(TAG,"------>onCreate");
	}

	public void cookie(){
		sharedPreferences = getSharedPreferences(Config.COMMON_SHARED, MODE_PRIVATE);
	    editor = sharedPreferences.edit();
	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG,"------>onStart");
	}
	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG,"------>onResume");
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG,"------>onPause");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e(TAG,"------>onRestart");
	}
	@Override
	protected void onStop() {
		super.onStop();
		this.dismissDialog();
		Log.e(TAG,"------>onStop");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.removeMessages();
		Log.e(TAG,"------>onDestroy");
	}
	@Override
	public void initData() {
	}
	@Override
	public void initialize() {
		this.initView();
		this.initData();
		this.initEvents();
	}
	@Override
	public void initView() {
	}
	@Override
	public void initEvents() {
	}
	@Override
	public void root() {
	}
	 protected void jumpAty(Context context,Class<?> aty){
		 Intent mIntent = new Intent(context,aty);
		 startActivity(mIntent);
	 }
	 protected void jumpAty(Context context,Class<?> aty,String tag,String data){
		 Intent mIntent = new Intent(context,aty);
		 mIntent.putExtra(tag, data);
		 startActivity(mIntent);
	 }
	 /**diglog view*/
	 @SuppressLint("InflateParams")
	 protected void showBaseDialog() {
			if (baseProgressDialog == null || baseContentView == null) {
				baseProgressDialog = new ProgressDialog(this, R.style.dialog);
				baseProgressDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						baseProgressDialogDissmissed();
					}
				});
				baseContentView = getLayoutInflater().inflate(
						R.layout.custom_progress_dialog,
						(ViewGroup) findViewById(R.id.custom_progressdialog));
				((TextView) baseContentView.findViewById(R.id.tv_desc))
						.setText(getResources().getText(R.string.common_in_processing));

			}
			if (!baseProgressDialog.isShowing()) {
				baseProgressDialog.show();
				baseProgressDialog.setContentView(baseContentView);
			}
		}
		protected void progressDialogDissmissed() {

		}
		protected void baseProgressDialogDissmissed() {

		}
		protected void dismissBaseDialog() {
			if (baseProgressDialog != null && baseProgressDialog.isShowing()) {
				baseProgressDialog.dismiss();
			}
		}
		/**等待 加载 diglog*/
		protected void initDiglog(){
			DialogManager manager = new DialogManager(this);
	        //自定义实现部分
			View view  = manager.getView(DialogManager.iot_camera);
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);// 加载布局
			ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
			TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
			spaceshipImage.setAnimation(manager.getAnimation(DialogManager.animation));
			tipTextView.setText("");
	        //自定义实现部分
			if(layout!=null){
				manager.setCancelable(true);
				mDiglog = manager.getDialog(DialogManager.iot_dialog_style, layout);
				showDialog();
			}
		}
		protected void showDialog(){
			if(mDiglog!=null){
				mDiglog.show( );
			}
		}
		protected void dismissDialog(){
			if(mDiglog!=null){
				mDiglog.dismiss();
				mDiglog = null;
			}
		}
		/**回收handler*/
		protected void removeMessages(){
		}
		/**动画的方式退出*/
		protected void animationExit(){
			    this.finish();
			    this.overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
		}
}
