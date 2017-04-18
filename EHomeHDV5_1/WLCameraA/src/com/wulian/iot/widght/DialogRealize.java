package com.wulian.iot.widght;
import com.wulian.icam.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class DialogRealize {
	private static DialogRealize instance = null; 
	private  static Dialog mDiglog  = null;
	public DialogRealize(){
	}
	public  static DialogRealize init(Context mContext){
			instance = new DialogRealize();
			instance.initView(mContext);
    	   return instance;
	}
	private void initView(Context mContext){
		DialogManager manager = new DialogManager(mContext);
		View view  = manager.getView(DialogManager.iot_camera);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_view);// 加载布局
		ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
		spaceshipImage.setAnimation(manager.getAnimation(DialogManager.animation));
		tipTextView.setText("");
		if(layout!=null){
			manager.setCancelable(true);
			mDiglog = manager.getDialog(DialogManager.iot_dialog_style, layout);
		}
	}
	public void showDiglog(){
		if(mDiglog!=null){
			mDiglog.show();
		}
	}
	public static DialogRealize unInit(){
		if(instance == null){
			instance = new DialogRealize();
		}
		return instance;
	}
	public  void dismissDialog(){
		if(mDiglog!=null){
			mDiglog.dismiss();
		}
		mDiglog = null;
		instance = null;
	}
}
