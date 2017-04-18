/**
 * xiaozhi 2014-7-27
 */
package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.AreaDevicesActivity;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.IconResourceEntity;
import cc.wulian.smarthomev5.fragment.device.AreaDevicesFragement;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.IconChooseView;
import cc.wulian.smarthomev5.view.IconChooseView.OnIconClickListener;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

/**
 * @author xiaozhi
 * @创作日期 2014-7-27
 */
public class AreaGroupAdapter extends WLOperationAdapter<DeviceAreaEntity> {
	private DeviceCache deviceCache;
	
	
	
	/**
	 * @param context
	 * @param data
	 */
	public AreaGroupAdapter(Context context, List<DeviceAreaEntity> data,List<MenuItem> items) {
		super(context, data,items);
		deviceCache = DeviceCache.getInstance(context);
	}

	/* 
     *
     */
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		View v = inflater.inflate(R.layout.area_group_item, parent, false);
		return v;
	}

	/* 
     *
     */
	@Override
	protected void bindView(Context context, View view, int pos,final DeviceAreaEntity item) {
		ImageView iv = (ImageView) view.findViewById(R.id.area_group_background_iv);
		int iconRes = DeviceTool.PressgetAreaIconResourceByIconIndex(item.getIcon());
		iv.setImageResource(iconRes);
		TextView deviceCountTextView = (TextView) view
				.findViewById(R.id.fragement_device_area_grid_item_device_count);
		int onLineCount = 0;
		List<DeviceInfo> deviceInfos = item.getDevices();
		for (DeviceInfo deviceInfo : deviceInfos) {
			WulianDevice d = deviceCache.getDeviceByID(context,
					deviceInfo.getGwID(), deviceInfo.getDevID());
			if (d != null && d.isDeviceOnLine()) {
				onLineCount++;
			}
		}
		deviceCountTextView.setText(onLineCount + "/"
				+ deviceInfos.size());
		TextView deviceNameTextView = (TextView) view
				.findViewById(R.id.fragement_device_area_grid_item_area_name);
		deviceNameTextView.setText(item.getName());
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				String roomID=item.getRoomID();
				intent.putExtra(AreaDevicesFragement.AREADEVICES_ROOMID,roomID);
				intent.setClass(mContext, AreaDevicesActivity.class);
				mContext.startActivity(intent);
			}
		});
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				iniPopupWidow(arg0, item);
				return false;
			}
		});
	}
	/**
	 * 常按弹出菜单
	 * 
	 * @param view
	 * @param item
	 */
	private void iniPopupWidow(View view, final DeviceAreaEntity item) {
		final DownUpMenuList downMenu = new DownUpMenuList(mContext);
		DownUpMenuItem renameItem = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.device_rename));
				downup_menu_view.setVisibility(View.GONE);
				mTitleTextView
						.setBackgroundResource(R.drawable.downup_menu_item_topcircle);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.ROOM_MODIFY)) {
					return;
				}

				if (item != null) {
					if (!item.isDelete()) {
						WLToast.showToast(
								mContext,
								mContext.getResources()
										.getString(
												R.string.device_config_edit_dev_edit_default_group_fail),
								0);
					} else {
						editAreaInfo(item);
					}
				}
				downMenu.dismiss();
			}

		};

		DownUpMenuItem deletItem = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				mTitleTextView.setTextColor(mContext.getResources().getColor(
						R.color.red_orange));
				mTitleTextView.setText(mContext.getResources().getString(
						R.string.device_config_edit_dev_area_create_item_delete));
				mTitleTextView
						.setBackgroundResource(R.drawable.downup_menu_item_bottomcircle);

			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.ROOM_DELETE)) {
					return;
				}

				if (item != null) {
					if (!item.isDelete()) {
						WLToast.showToast(
								mContext,
								mContext.getResources()
										.getString(
												R.string.device_config_edit_dev_delete_default_group_fail),
								0);
					} else {
						createDeleteAreaDialog(item);
					}
				}
				downMenu.dismiss();
			}
		};
		DownUpMenuItem cancelItem = new DownUpMenuItem(mContext) {

			@Override
			public void initSystemState() {
				linearLayout.setPadding(0, 30, 0, 0);
				mTitleTextView
						.setText(mContext
								.getResources()
								.getString(
										R.string.cancel));
				downup_menu_view.setVisibility(View.GONE);
				mTitleTextView
						.setBackgroundResource(R.drawable.downup_menu_item_allcircle);
			}

			@Override
			public void doSomething() {
				downMenu.dismiss();
			}
		};
		ArrayList<DownUpMenuItem> menuItems = new ArrayList<DownUpMenuList.DownUpMenuItem>();
		menuItems.add(renameItem);
		menuItems.add(deletItem);
		menuItems.add(cancelItem);
		downMenu.setMenu(menuItems);
		downMenu.showBottom(view);
	}
	public void editAreaInfo(final DeviceAreaEntity entity) {
		List<IconResourceEntity> areaIconBlackList= new ArrayList<IconResourceEntity>();
		for(int i=1;i<=18;i++){
			IconResourceEntity iconEntity=new IconResourceEntity();
			iconEntity.iconkey=i;
			iconEntity.iconRes = DeviceTool
					.DefaultgetAreaIconResourceByIconIndex(i + "");
			iconEntity.iconSelectedRes = DeviceTool
					.PressgetAreaIconResourceByIconIndex(i + "");
			areaIconBlackList.add(iconEntity);
		}
		final IconChooseView chooseView = new IconChooseView(mContext,areaIconBlackList);
		chooseView.setInputHintTextContent(mContext.getResources().getString(R.string.device_config_edit_dev_area));
		chooseView.setSelectedChangedBackgroundColor(false);
		chooseView.setSelectedChangedImageDrawable(true);
		chooseView.setOnItemClickListener(new OnIconClickListener() {
			
			@Override
			public void onIconClick(IconResourceEntity entity) {
				String mAreaName = chooseView.getInputTextContent();
				if(StringUtil.isNullOrEmpty(mAreaName)){
					String str = DeviceTool.getDefaultAreaTextByIconIndex(mContext,entity.iconkey);
					chooseView.setInputHintTextContent(str);
				}
				
			}
		});
		chooseView.swapData(areaIconBlackList);

		int index = StringUtil.toInteger(entity.getIcon());
		chooseView.setSelectIcon((index <1 || index > 18) ? 18 : index);
		chooseView.setInputTextContent(entity.getName());

		class AddSceneListener implements MessageListener {

			@Override
			public void onClickPositive(View contentViewLayout) {
				String areaName = chooseView.getInputTextContent().trim();
				if(StringUtil.isNullOrEmpty(areaName))
					areaName = chooseView.getInputHintTextContent();
				if (StringUtil.isNullOrEmpty(areaName)) {
					chooseView.requestFocus();
					chooseView.setError(mContext.getText(R.string.device_area_not_null_hint));
				}else if(StringUtil.isNullOrEmpty(chooseView.getCheckedItem()+"")){
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.area_icon_choose),  WLToast.TOAST_SHORT);
				} else {
					IconResourceEntity iconEntity =chooseView.getCheckedItem();
					SendMessage.sendSetRoomMsg(mContext,AccountManager.getAccountManger().getmCurrentInfo().getGwID(),CmdUtil.MODE_UPD, entity.getRoomID(),areaName, iconEntity.iconkey+"");
				}
			}

			@Override
			public void onClickNegative(View contentViewLayout) {
			}

		}
		AddSceneListener addSceneListener = new AddSceneListener();
		WLDialog.Builder builder = new Builder(mContext);
		builder.setTitle(
				R.string.device_config_edit_dev_area_create_item_rename_titel)
				.setContentView(chooseView)
				.setHeightPercent(0.6F)
				.setPositiveButton(
						mContext.getResources().getString(
								R.string.common_ok))
				.setNegativeButton(
						mContext.getResources().getString(
								R.string.cancel))
				.setListener(addSceneListener);
		builder.create().show();

	}
	/**
	 * 创建删除对话框
	 * 
	 * @param
	 */
	private void createDeleteAreaDialog(final DeviceAreaEntity item) {
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		final AccountManager mAccountManger = AccountManager.getAccountManger();
		/*
		 * TextView text = new TextView(mContext);
		 * text.setText("删除分组后，组内设备将移至“其他”组内");
		 */
		builder.setTitle(
				mContext.getResources().getString(
						R.string.device_config_edit_dev_area_create_item_delete))
				.setContentView(
						inflater.inflate(
								R.layout.device_area_add_dialog_delete_text_view,
								null));
		builder.setNegativeButton(
				mContext.getResources()
						.getString(
								R.string.cancel))
				.setPositiveButton(
						mContext.getResources()
								.getString(
										R.string.device_config_edit_dev_area_create_item_delete))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						if (item != null) {
							SendMessage.sendSetRoomMsg(mContext,
									mAccountManger.getmCurrentInfo().getGwID(),
									CmdUtil.MODE_DEL, item.getRoomID(),
									item.getName(), item.getIcon());
						}
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}
}
