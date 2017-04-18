package cc.wulian.smarthomev5.fragment.setting.flower.items;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class FlowerCloudItem extends AbstractSettingItem{

	private final String pluginName="Cloud.zip";
	
	private boolean isFling = false;

	private int downX,downY;

	private Button manuBtn;
	
	private FrameLayout layout;
	
	Handler handler=new Handler(Looper.getMainLooper());
	
	public FlowerCloudItem(Context context) {
		super(context, R.drawable.flower_cloud_disk_setting_icon, context.getResources().getString(R.string.flower_cloud_disk_setting_title));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setHardDiskInfo();
		layout = new FrameLayout(mContext);
		LayoutParams contentParam = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(contentParam);
		manuBtn = new Button(mContext);
		manuBtn.setText(mContext.getString(R.string.account_setting_format_title));
		manuBtn.setTextColor(mContext.getResources().getColor(R.color.black));
		manuBtn.setBackgroundColor(mContext.getResources().getColor(R.color.red_delete));
		manuBtn.setVisibility(View.INVISIBLE);
		LayoutParams manuParam = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
		manuBtn.setLayoutParams(manuParam);
		layout.addView(view);
		LayoutParams manuAddParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT,Gravity.RIGHT);
		layout.addView(manuBtn,manuAddParam);
		// 为格式化按钮添加点击事件
		setFormatEvent(manuBtn);
		// 添加左滑事件
		setSwipeEvent(layout);
	}

	@Override
	public View getShowView() {
		manuBtn.setVisibility(View.INVISIBLE);
		return layout;
	}
	
	@Override
	public View getView() {
		manuBtn.setVisibility(View.INVISIBLE);
		return layout;
	}
	
	public void setHardDiskInfo() {
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.voice_remind_right);
	}
	
	private void setSwipeEvent(final View view) {
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = (int) arg1.getX();
					if(!isFling)return false;
					break;
				case MotionEvent.ACTION_MOVE:
					int dis = (int) (downX-arg1.getX());
					if(dis>DisplayUtil.dip2Pix(mContext, 15))isFling=true;
					if(isFling)swipe(dis);
					break;
				case MotionEvent.ACTION_UP:
					if (!isFling)return false;
					if ((downX-arg1.getX()) > (manuBtn.getWidth() / 2)) {
						swipe(manuBtn.getWidth());
					} else {
						swipe(0);
					}
					arg1.setAction(MotionEvent.ACTION_CANCEL);
					view.onTouchEvent(arg1);
					break;
				}
			return true;
			}
		});
	}

	private void setFormatEvent(View view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WLDialog.Builder builder = new WLDialog.Builder(mContext);
				builder.setTitle(
						mContext.getString(R.string.account_setting_format_title))
						.setMessage(
								mContext.getString(R.string.account_setting_format_hint))
						.setPositiveButton(android.R.string.ok)
						.setNegativeButton(android.R.string.cancel)
						.setListener(new MessageListener() {
							@Override
							public void onClickPositive(View contentViewLayout) {
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										jumpToCloudDisk("format-hard-disk.html");
									}
								});
							}

							@Override
							public void onClickNegative(View contentViewLayout) {

							}
						});
				WLDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	private void swipe(int dis) {
		if (manuBtn.getVisibility() == View.INVISIBLE) {
			manuBtn.setVisibility(View.VISIBLE);
		}
		if(dis>manuBtn.getWidth())dis=manuBtn.getWidth();
		if(dis<=0){
			isFling=false;
			dis=0;
		}
		view.layout(-dis, view.getTop(), view.getWidth()-dis, view.getBottom());
		manuBtn.layout(view.getWidth() -dis, manuBtn.getTop(),view.getWidth() + manuBtn.getWidth()-dis,manuBtn.getBottom());
	}
	
	@Override
	public void doSomethingAboutSystem() {
		// add by yanzy:不允许被授权用户使用
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.GATEWAY_ROUTING)) {
			return;
		}

		handler.post(new Runnable() {
			
			@Override
			public void run() {
				jumpToCloudDisk(null);
			}
		});
	}

	private void jumpToCloudDisk(final String indexPager){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager.getInstance().getHtmlPlugin(mContext, pluginName,false, new PluginsManagerCallback() {
					
					@Override
					public void onGetPluginSuccess(PluginModel model) {
						String entryPager=indexPager;
						if(entryPager==null||entryPager.length()==0)entryPager=model.getEntry();
						File file=new File(model.getFolder(),entryPager);
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}	
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, AccountManager.getAccountManger().getmCurrentInfo().getGwID());
						IntentUtil.startHtml5PlusActivity(mContext, uri);
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if(hint!=null&&hint.length()>0){
							Handler handler=new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}
	
	public void setDiskUsedInfo(String info){
		infoTextView.setVisibility(View.VISIBLE);
		infoTextView.setText(info);
		setInfoTextViewColor(mContext.getResources().getColor(R.color.black));
	}
}
