package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.RoomEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class AreaEditFragment extends WulianFragment {
	private AreaAdapter areaAdapter;
	private ListView areaListView;
	private RelativeLayout addButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		areaAdapter = new AreaAdapter(mActivity);
		areaAdapter.swapData(AreaGroupManager.getInstance()
				.getDeviceAreaEnties());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View contentView = inflater.inflate(
				R.layout.device_area__dialog_content, null);
		initBar();
		return contentView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_device_title));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.device_edit_area_edit));
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		addButton = (RelativeLayout) view
				.findViewById(R.id.fragemnt_device_btn_add);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createAddAreaDialog();
			}
		});
		areaListView = (ListView) view
				.findViewById(R.id.fragement_device_area_name_listview);
		areaListView.setAdapter(this.areaAdapter);
	}

	/**
	 * 创建添加对话框
	 */
	private void createAddAreaDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mApplication.getResources()
				.getString(R.string.device_edit_area_add));
		View dlgView = inflater.inflate(
				R.layout.device_area_add_dialog_edit_text, null);
		builder.setContentView(dlgView)
				.setNegativeButton(
						mApplication.getResources().getString(R.string.cancel))
				.setPositiveButton(
						mApplication.getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						EditText areaNameEditText = (EditText) contentViewLayout
								.findViewById(R.id.fragement_device_area_add_dialog_edit_text);
						String areaName = areaNameEditText.getText().toString();
						if (StringUtil.isNullOrEmpty(areaName)) {
							WLToast.showToast(
									mActivity,
									mApplication.getResources().getString(
											R.string.device_area_not_null_hint),
									0);
							return;
						}
						SendMessage.sendSetRoomMsg(mActivity,
								mAccountManger.getmCurrentInfo().getGwID(),
								CmdUtil.MODE_ADD, null, areaName, "");
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	/**
	 * 创建编辑对话框
	 * 
	 * @param position
	 */
	private void createEditAreaDialog(final int position) {
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mApplication.getResources().getString(
				R.string.device_config_edit_dev_area_create_item_rename_titel));
		View dlgView = inflater.inflate(
				R.layout.device_area_add_dialog_edit_text, null);
		final EditText areaNameEditText = (EditText) dlgView
				.findViewById(R.id.fragement_device_area_add_dialog_edit_text);
		areaNameEditText.setText(areaAdapter.getItem(position).getName());
		builder.setContentView(dlgView)
				.setNegativeButton(
						mApplication.getResources().getString(R.string.cancel))
				.setPositiveButton(
						mApplication.getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						String areaName = areaNameEditText.getText().toString();
						if (StringUtil.isNullOrEmpty(areaName)) {
							WLToast.showToast(
									mActivity,
									mApplication.getResources().getString(
											R.string.device_area_not_null_hint),
									0);
							return;
						}
						DeviceAreaEntity entity = areaAdapter.getItem(position);
						entity.setName(areaName);
						SendMessage.sendSetRoomMsg(mActivity,
								mAccountManger.getmCurrentInfo().getGwID(),
								CmdUtil.MODE_UPD, entity.getRoomID(), areaName,
								"");
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	/**
	 * 创建删除对话框
	 * 
	 * @param position
	 */
	private void createDeleteAreaDialog(final int position) {
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		/*
		 * TextView text = new TextView(mContext);
		 * text.setText("删除分组后，组内设备将移至“其他”组内");
		 */
		builder.setTitle(
				mApplication.getResources().getString(
						R.string.device_config_edit_dev_area_create_item_delete))
				.setContentView(
						inflater.inflate(
								R.layout.device_area_add_dialog_delete_text_view,
								null));
		builder.setNegativeButton(
				mApplication.getResources()
						.getString(
								R.string.cancel))
				.setPositiveButton(
						mApplication.getResources()
								.getString(
										R.string.device_config_edit_dev_area_create_item_delete))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						DeviceAreaEntity entity = areaAdapter.getItem(position);
						SendMessage.sendSetRoomMsg(mActivity,
								mAccountManger.getmCurrentInfo().getGwID(),
								CmdUtil.MODE_DEL, entity.getRoomID(),
								entity.getName(), entity.getIcon());
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	class AreaAdapter extends WLBaseAdapter<DeviceAreaEntity> {

		public AreaAdapter(Context context) {
			super(context, new ArrayList<DeviceAreaEntity>());
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			View view = this.mInflater.inflate(
					R.layout.device_area_add_dialog__item, null);
			return view;
		}

		@Override
		protected void bindView(Context context, View view, final int pos,
				final DeviceAreaEntity item) {
			final Button delete = (Button) view
					.findViewById(R.id.fragemnt_device_area_delete);
			delete.setVisibility(View.INVISIBLE);
			delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					createDeleteAreaDialog(pos);
				}
			});
			ImageButton showDelete = (ImageButton) view
					.findViewById(R.id.fragemnt_device_area_item_show_delete);
			showDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!item.isDelete()) {
						WLToast.showToast(
								mContext,
								mApplication.getResources()
										.getString(
												R.string.device_config_edit_dev_delete_default_group_fail),
								0);
						return;
					}
					if (delete.getVisibility() == View.VISIBLE) {
						delete.setVisibility(View.INVISIBLE);
					} else {
						delete.setVisibility(View.VISIBLE);
					}
				}
			});
			TextView areaNameTextView = (TextView) view
					.findViewById(R.id.fragemnt_device_area_name_textview);
			areaNameTextView.setText(item.getName());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!item.isDelete()) {
						WLToast.showToast(
								mContext,
								mApplication.getResources()
										.getString(
												R.string.device_config_edit_dev_edit_default_group_fail),
								0);
						return;
					}
					createEditAreaDialog(pos);
				}
			});
		}
	}

	
	public void onEventMainThread(RoomEvent event) {
		areaAdapter.swapData(AreaGroupManager.getInstance()
				.getDeviceAreaEnties());
	}

}
