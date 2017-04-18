package cc.wulian.smarthomev5.fragment.uei;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource.WL23_ResourceInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.uei.ExpandPopupWindow;
import cc.wulian.smarthomev5.activity.uei.KeyboardPopupWindow;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class DVDRemoteControlFragment extends WulianFragment implements OnClickListener{

	private View parentView;
	private ImageView ivNumber;
	private ImageView ivExpand;
	private KeyboardPopupWindow keyboardPopupWindow;
	private ExpandPopupWindow expandPopupWindow;
	UeiUiArgs args_value=null;
	UEIEntity uei=null;
	private VirtualKeyButton virkeyBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			args_value=bundle.getParcelable("args");
			uei=args_value.ConvertToEntity();
		}
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_dvd_remootecontrol,
				container, false);
		return parentView;
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);
		initView(paramView);
	}

	private void initBar() {
		String brandTypeName="";
		WL23_ResourceInfo resourceInfo=WL_23_IR_Resource.getResourceInfo(uei.getDeviceType());
		if(resourceInfo.name>0){
			brandTypeName=getString(resourceInfo.name);
		}
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText(getString(R.string.nav_device_title));
		getSupportActionBar().setTitle(uei.getBrandName()+brandTypeName);
		
	}

	private void initView(View paramView) {
		virkeyBtn=new VirtualKeyButton(this.mActivity);
		virkeyBtn.setArgs(args_value);
		virkeyBtn.setKeyFlag("80");
		virkeyBtn.setTVLight((ImageView) paramView.findViewById(R.id.iv_TV_light));
//		virkeyBtn.RegiestVirtualKeyEvent(virtualKeyLayout);
		keyboardPopupWindow = new KeyboardPopupWindow(mActivity,virkeyBtn);
//		expandPopupWindow = new ExpandPopupWindow(mActivity, itemListener);
		ivNumber = (ImageView) paramView.findViewById(R.id.iv_DVDnumber);
		ivExpand = (ImageView) paramView.findViewById(R.id.iv_DVDexpand);
		
		
		ivNumber.setOnClickListener(this);
		ivExpand.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
		case R.id.iv_DVDnumber:
			UeiCommonEpdata ueiCommon=new UeiCommonEpdata(args_value.getGwID(), args_value.getDevID(), args_value.getEp());
			ueiCommon.sendCommand12(getContext(),"00010B");
			keyboardPopupWindow.showAtLocation(parentView.findViewById(R.id.lin_dvd), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.iv_DVDexpand:
			expandPopupWindow.showAtLocation(parentView.findViewById(R.id.lin_dvd), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		}
	}
	
	private OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			
		}
	};
}
