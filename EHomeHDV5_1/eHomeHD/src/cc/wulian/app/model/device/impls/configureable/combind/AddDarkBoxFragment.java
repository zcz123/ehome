package cc.wulian.app.model.device.impls.configureable.combind;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.smarthomev5.entity.CombindDeviceEntity;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.MenuList;
import cc.wulian.smarthomev5.tools.MenuList.MenuItem;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AddDarkBoxFragment extends WulianFragment{

	@ViewInject(R.id.device_combind_left_tv)
	private TextView leftDeviceTextView;
	@ViewInject(R.id.device_combind_left_iv)
	private ImageView leftDeviceImageView;
	@ViewInject(R.id.device_combind_right_tv)
	private TextView rightDeviceTextView;
	@ViewInject(R.id.device_combind_right_iv)
	private ImageView rightDeviceImageView;
	@ViewInject(R.id.device_combind_name_et)
	private EditText customDeviceNameEditText;
	@ViewInject(R.id.device_combind_area_tv)
	private TextView areaTextView;
	@ViewInject(R.id.device_combind_area_iv)
	private ImageView areaImageView;
	private CombindDeviceEntity combindDeviceEntity = new CombindDeviceEntity();
	private MenuList menuList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		menuList = new MenuList(mActivity);
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(R.string.device_ir_save);
		getSupportActionBar().setIconText(R.string.device_ir_back);
		getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
			
			@Override
			public void onClick(View v) {
				showMenu();
			}
		});
		
	}
	private void showMenu() {
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem(mActivity) {
			
			@Override
			public void initSystemState() {
				titleTextView.setText("hello");
			}
			
			@Override
			public void doSomething() {
			}
		});
		menuList.addMenu(items);
		menuList.show(getSupportActionBar().getCustomView());
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_combind_add, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		areaImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAreaPopupWindow();
			}
		});
	}

	protected void showAreaPopupWindow() {
		List<DeviceAreaEntity> entites = AreaGroupManager.getInstance().getDeviceAreaEnties();
		List<MenuItem> items = new ArrayList<MenuList.MenuItem>();
		for(final DeviceAreaEntity e :entites){
			MenuItem item = new MenuItem(mActivity){

				@Override
				public void initSystemState() {
					titleTextView.setText(e.getName());
				}

				@Override
				public void doSomething() {
					combindDeviceEntity.setRoomID(e.getRoomID());
					areaTextView.setText(e.getName());
					menuList.dismiss();
				}
				
			};
			items.add(item);
		}
		menuList.addMenu(items);
		menuList.show(areaImageView);
	}
	

}
