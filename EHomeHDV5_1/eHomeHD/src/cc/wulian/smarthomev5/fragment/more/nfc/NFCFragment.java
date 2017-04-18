package cc.wulian.smarthomev5.fragment.more.nfc;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.NFCAddDeviceActivity;
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCManager.AbstractNFCListener;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCManager.NFCListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

public class NFCFragment extends WulianFragment {

	@ViewInject(R.id.nfc_select_scene_tv)
	private TextView mSceneTextView;
	@ViewInject(R.id.nfc_select_scene_Iv)
	private ImageView mSceneImageView;
	@ViewInject(R.id.nfc_select_scene_ll)
	private LinearLayout mSceneLinearLayout;
	@ViewInject(R.id.nfc_select_device_ll)
	private LinearLayout mDeviceLinearLayout;
	@ViewInject(R.id.nfc_write_save_linelayout)
	private LinearLayout mWriteNFCLinearLayout;
	@ViewInject(R.id.nfc_select_message_ll)
	private LinearLayout mMessageLinearLayout;
	private SceneList mSceneList;
	private WLDialog mWriteDialog,mMessageDialog;
	private NFCManager nfcManager = NFCManager.getInstance();
	private NFCListener nfcListener = new AbstractNFCListener() {
		
		@Override
		public void onRead(final Intent intent) {
			clearReadData();
			TaskExecutor.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					Logger.debug("excecute read");
					nfcManager.dispatchComingMessage(intent);
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							NFCEntity nfcEntity = nfcManager.getSceneInfo();
							setSceneInfo(mApplication.sceneInfoMap.get(nfcEntity.getGwID()+nfcEntity.getID()),true);
						}
					});
					
				}
			});
			
		}
		@Override
		public void onWrite(final Intent intent) {
			TaskExecutor.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					Logger.debug("excecute write");
					nfcManager.dispatchComingMessage(intent);
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							clearWriteData();
							WLToast.showToast(mActivity,mApplication.getResources().getString(R.string.more_nfc_function_write_information_success), WLToast.TOAST_SHORT);
						}
					});
				}
			});
		}
	};
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.nfc_select_scene_ll:
				mSceneList.show(v);
				break;
			case R.id.nfc_select_device_ll:
				mActivity.JumpTo(NFCAddDeviceActivity.class);
				break;
			case R.id.nfc_select_message_ll:
				if (mMessageDialog != null) {
					mMessageDialog.show();
				}
				break;
			case R.id.nfc_write_save_linelayout:
				if (mWriteDialog != null) {
					mWriteDialog.show();
				}
				break;
			default:
				break;
			}
		}
	};
	private final SceneList.OnSceneListItemClickListener mItemClickListener = new SceneList.OnSceneListItemClickListener() {

		@Override
		public void onSceneListItemClicked(SceneList list, int pos,
				SceneInfo info) {
			list.dismiss();
			setSceneInfo(info,false);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initOpertionNFCListeners();
		initWriteDialog();
		initMessageDialog();
		mSceneList = new SceneList(mActivity, true);
		mSceneList.setOnSceneListItemClickListener(mItemClickListener);
	}

	private void initOpertionNFCListeners() {
		nfcManager.setExecute(false);
		nfcManager.addNFCListener(nfcListener);
	}
	private void unitOpertionNFCListeners() {
		nfcManager.setExecute(true);
		nfcManager.removeNFCListener(nfcListener);
	}
	
	private void clearReadData() {
		mSceneTextView.setText(mApplication.getResources().getString(
				R.string.nav_scene_title));
		mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
		nfcManager.clear();
	}
	/**
	 * 写入成功后清除数据
	 */
	private void clearWriteData() {
		mSceneTextView.setText(mApplication.getResources().getString(
				R.string.nav_scene_title));
		mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
		if(mWriteDialog.isShowing()){
			mWriteDialog.dismiss();
		}
		nfcManager.clear();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initBar();
		View view = inflater.inflate(R.layout.more_nfc_content, container,
				false);
		ViewUtils.inject(this, view);
		return view;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_more));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.more_nfc_function));
		getSupportActionBar().setRightIconText(R.string.device_config_edit_dev_help);
		getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = "file:///android_asset/nfc/nfc_introduction_zh_cn.html";
				if (!LanguageUtil.isChina())
					url = "file:///android_asset/nfc/nfc_introduction_en_us.html";
				String title = mApplication.getResources().getString(
						R.string.device_config_edit_dev_help);
				String leftIconText = mApplication.getResources().getString(
						R.string.more_nfc_function);
				IntentUtil.startCustomBrowser(mActivity, url, title,
						leftIconText);
			}
		});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mWriteNFCLinearLayout.setOnClickListener(listener);
		mSceneLinearLayout.setOnClickListener(listener);
		mDeviceLinearLayout.setOnClickListener(listener);
		mMessageLinearLayout.setOnClickListener(listener);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!mAccountManger.isConnectedGW()) {
			mAccountManger.signinDefaultAccount();
		}
	}

	/**
	 * 选中场景时设置
	 * 
	 * @param info
	 */
	public void setSceneInfo(SceneInfo info,boolean isRead) {
		if (info == null || info.getSceneID() == null) {
			mSceneTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
			return ;
		}else if(CmdUtil.SCENE_UNKNOWN.equals(info.getSceneID())){
			mSceneTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
		}else{
			mSceneTextView.setText(info.getName());
			Drawable checkedIcon = SceneManager.getSceneIconDrawable_Light_Small(
						NFCFragment.this.getActivity(), info.getIcon());
			mSceneImageView.setImageDrawable(checkedIcon);
		}
		if(!isRead){
			NFCEntity entity =new NFCEntity();
			entity.setID(info.getSceneID());
			entity.setType(NFCEntity.TYPE_SCENE);
			nfcManager.setSceneInfo(entity);
		}
	}

	/**
	 * 初始化写的对话框
	 */
	private void initWriteDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this.getActivity());
		builder.setContentView(R.layout.more_write_nfc)
				.setPositiveButton(R.string.cancel)
				.setNegativeButton(null);
		;
		mWriteDialog = builder.create();
		mWriteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				nfcManager.setWriteMode(true);
			}

		});
		mWriteDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				nfcManager.setWriteMode(false);
			}
		});
	}
	
	/**
	 * 初始化消息对话框
	 */
	private void initMessageDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this.getActivity());
		LayoutInflater inflater=(LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view=inflater.inflate(R.layout.more_nfc_message, null);
		builder.setContentView(view)
				.setPositiveButton(R.string.cancel)
				.setNegativeButton(R.string.common_ok)
				.setListener(new WLDialog.MessageListener() {			
					@Override
					public void onClickPositive(View contentViewLayout) {
					}			
					@Override
					public void onClickNegative(View contentViewLayout) {
						EditText editText=(EditText) contentViewLayout.findViewById(R.id.more_nfc_message_edit);
						if(editText.getText()!=null){
							if(editText.getText().toString().trim().length()>0){
								nfcManager.setNfcMessage(editText.getText().toString());
							}else{
								nfcManager.setNfcMessage("");
							}
						}
						
					}
				});
		
		mMessageDialog = builder.create();
		mMessageDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				
				final EditText editText=(EditText) view.findViewById(R.id.more_nfc_message_edit);	
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						editText.setText(nfcManager.getNfcMessage());
					}
				});
				
				Timer timer=new Timer();
				TimerTask task=new TimerTask() {					
					@Override
					public void run() {											
						editText.requestFocus();						
						InputMethodManager inputManager =(InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.showSoftInput(editText, 0);												
					}
				};	
				timer.schedule(task, 500);
			}
		});
		mMessageDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unitOpertionNFCListeners();
	}
}
