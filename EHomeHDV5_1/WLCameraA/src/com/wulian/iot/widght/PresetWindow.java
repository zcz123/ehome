package com.wulian.iot.widght;

import java.util.List;
import java.util.Map;

import com.wulian.iot.view.device.play.PlayDesktopActivity;
import com.wulian.iot.widght.PresetWindow.DialogPojo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
/**
 * popWindow
 * @author syf
 *
 */
@SuppressLint("InlinedApi")
public  abstract class PresetWindow extends PopupWindow{
    @SuppressWarnings("unused")
	private Context context = null;
	private Handler runOnUiThread  = new Handler(Looper.getMainLooper());
    private View view = null;
    private int width = LayoutParams.MATCH_PARENT;
	private int height = LayoutParams.WRAP_CONTENT;
    public PresetWindow(){

    }
    public PresetWindow(Context context,int layout){
        super(context);
        this.context = context;
        initView(context,layout);
    }
    public PresetWindow(Context context,int layout,int widht,int height){
        super(context);
        this.context = context;
        this.width = widht;
        this.height = height;
        initView(context,layout);
    }
    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }
    private void initView(Context context,int layout){
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(layout, null);
        if(view!=null){
            this.setContentView(view);
            setHeight(height);
            setWidth(width);
            this.setFocusable(true);
            this.setBackgroundDrawable(new ColorDrawable(0000000000));
            this.setOutsideTouchable(true);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            getView(view);
        }
    }
    public  abstract void getView(View view);

    public void runOnUiThread(Runnable runnable){
    	if(runOnUiThread != null){
    		runOnUiThread.post(runnable);
    	}
    }
    public void alertDialog(final DialogPojo dialogPojo){
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder  builder = new AlertDialog.Builder(dialogPojo.getmContext(), AlertDialog.THEME_HOLO_LIGHT);
	    		 builder.setView(dialogPojo.getView());
	    		 builder.setTitle(dialogPojo.getTitle());
	    		 builder.setNegativeButton(dialogPojo.getCloseTitle(), dialogPojo.getOnClickListenerMap().get(DialogPojo.CLOSK));
	    		 builder.setPositiveButton(dialogPojo.getConfirmTitle(), dialogPojo.getOnClickListenerMap().get(DialogPojo.CONFIRM));
	    		 builder.show();
			}
		});
    }
    public static class DialogPojo{
    	public static String CLOSK = "CLOSK";
        public static String CONFIRM = "CONFIRM";
    	public DialogPojo(){
    	}
    	private View view;
    	private String title;
    	private String closeTitle;
    	public  String confirmTitle;
    	private Context mContext;
    	public Map<String,OnClickListener> onClickListenerMap = null;
    	public void setmContext(Context mContext) {
			this.mContext = mContext;
		}
    	public Context getmContext() {
			return mContext;
		}
    	public void setCloseTitle(String closeTitle) {
			this.closeTitle = closeTitle;
		}
    	public String getCloseTitle() {
			return closeTitle;
		}
    	public void setConfirmTitle(String confirmTitle) {
			this.confirmTitle = confirmTitle;
		}
    	public String getConfirmTitle() {
			return confirmTitle;
		}
    	public void setView(View view) {
			this.view = view;
		}
    	public View getView() {
			return view;
		}
    	public void setTitle(String title) {
			this.title = title;
		}
    	public String getTitle() {
			return title;
		}
    	public void setOnClickListenerMap(
				Map<String, OnClickListener> onClickListenerMap) {
			this.onClickListenerMap = onClickListenerMap;
		}
    	public Map<String, OnClickListener> getOnClickListenerMap() {
			return onClickListenerMap;
		}
    }
}
