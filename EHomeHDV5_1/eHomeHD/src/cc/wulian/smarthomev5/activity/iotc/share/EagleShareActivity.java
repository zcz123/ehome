package cc.wulian.smarthomev5.activity.iotc.share;
import java.util.ArrayList;
import java.util.List;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.widght.DialogRealize;
import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.sdk.user.entity.BindUser;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.camera.EagleCameraAdapter;
import cc.wulian.smarthomev5.adapter.camera.EagleShareUserAdapter;
import cc.wulian.smarthomev5.tools.DevicesUserManage;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class EagleShareActivity extends SimpleFragmentActivity implements OnClickListener{
	private EditText shareEtxt;
	private TextView confirmTxt,shareTxt,titleTxt;
	private ImageView exitImg;
	private EagleShareUserAdapter shareUserAdapter = null;
	private AMSDeviceInfo aDeviceInfo = null;
	private List<BindUser> bindUsers = null;
	private ListView bindUserListView = null;
	private  List<String> deviceIds = null; 
	private String aDeviceId = null;
	private List<BindUser> swapBindUser = new ArrayList<BindUser>();
	public  Callback  RunMainDataCallback= new Callback() {
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerConstant.SUCCESS:
				DialogRealize.unInit().dismissDialog();
				if (msg.obj != null) {
					if ((bindUsers = (List<BindUser>) msg.obj).size() > 0) {
						Log.i(TAG, "bindUsers(" + bindUsers.size() + ")");
						for (BindUser obj : bindUsers) {
							if (!obj.isAdmin()) {
								swapBindUser.add(obj);
							}
						}
						shareNumberOfPeople();
						shareUserAdapter.swapData(swapBindUser);
						swapBindUser.clear();
					}
				}
				return false;
			case HandlerConstant.ERROR:
				DialogRealize.unInit().dismissDialog();
				return false;
			case 0:
				fetchBindUser();
				Log.i(TAG, "bind success" +":"+ "unbind success");
				return false;
			case 2205:
				Log.i(TAG, "bind success 2205");
				return false;
			case 200:
				Log.i(TAG, "bind success 200");
				return false;
			}
			return false;
		}
	};
	private Handler runMainDataThread = new Handler(Looper.getMainLooper(),RunMainDataCallback);
	@Override
	public void root() {
		setContentView(R.layout.activity_share_eagle);
	}
	@Override
	public void initView() {
		shareEtxt = (EditText) findViewById(R.id.eagle_share_etxt);
		confirmTxt = (TextView) findViewById(R.id.eagle_share_confirm);
		shareTxt  = (TextView) findViewById(R.id.eagle_share_hint);
		bindUserListView = (ListView)findViewById(R.id.eagle_share_list_view);
		titleTxt = (TextView)findViewById(R.id.tv_cateye_titlebar_title);
		exitImg = (ImageView)findViewById(R.id.iv_cateye_titlebar_back);
		alterTitleBar();
		listViewBindAdapter();
	};
	@Override
	public void initData() {
		aDeviceId = (String) getIntent().getStringExtra(EagleCameraAdapter.SHARE_MODEL);
		if(aDeviceId!=null){
			addDeviceId();
			fetchBindUser();
		}
	}
	private void fetchBindUser(){
		DialogRealize.init(this).showDiglog();
		DevicesUserManage.queryUserByDevice(aDeviceId, runMainDataThread, HandlerConstant.SUCCESS);
	}
	@Override
	public void initEvents() {
		confirmTxt.setOnClickListener(this);
		exitImg.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if(v == confirmTxt){
			authUser();
		} else if(v == exitImg){
			finish();
		}
	}
	private void addDeviceId(){
		deviceIds  = new ArrayList<String>();
		deviceIds.add(aDeviceId);
	}
	private void authUser(){
		String  account = shareEtxt.getText().toString();
		if(account!=null&&!account.trim().equals("")){
			showDialogNote(account);
			shareEtxt.setText(null);
			return;
		}
		Toast.makeText(this,getResources().getString(R.string.home_monitor_username_cannotNull), Toast.LENGTH_SHORT).show();
	}
	private void listViewBindAdapter(){
		if(shareUserAdapter == null){
			shareUserAdapter = new EagleShareUserAdapter(this, null){
				@Override
				public void unBindUser(String user, int position) {
					authorization(false,user);
				}
			};
			bindUserListView.setAdapter(shareUserAdapter);
		}
	}
	private void  authorization (boolean auth,String user){
		DevicesUserManage.authUser(user,deviceIds , auth,runMainDataThread);
	}
	private void alterTitleBar(){
		titleTxt.setText(R.string.home_monitor_share);
	}
	private void shareNumberOfPeople(){
		shareTxt.setText(String.format(getResources().getString(R.string.cateye_share_prompt),swapBindUser.size()));
//		shareTxt.setText(getResources().getString(R.string.eagle_share_people_before)+ swapBindUser.size()+ getResources().getString(R.string.eagle_share_people_after));
	}
	private void showDialogNote(final String account){
		final WLDialog.Builder wb=new WLDialog.Builder(this);
		wb.setMessage(getResources().getString(R.string.cateye_share_hint))
				.setPositiveButton(getResources().getString(com.wulian.icam.R.string.common_sure))
				.setNegativeButton(getResources().getString(com.wulian.icam.R.string.common_cancel))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
//						authUser();
						authorization(true,account);
					}
					@Override
					public void onClickNegative(View contentViewLayout) {
						wb.create().dismiss();
					}
				});
		wb.create().show();
	}
}
