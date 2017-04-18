package cc.wulian.smarthomev5.adapter.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.app.model.device.DesktopCameraDevice;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem.ShortCutSelectDataDeleteListener;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem.ShortCutSelectDataListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceResource;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionDelayActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionDelayFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTaskDelayTimeView;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SceneManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class AutoActionTaskAdapter extends WLBaseAdapter<AutoActionInfo> {


    private Map<WulianDevice, DeviceShortCutSelectDataItem> deviceMap = new HashMap<WulianDevice, DeviceShortCutSelectDataItem>();
    private DeviceCache deviceCache;
    private Resources resources;
    private AccountManager mAccountManager = AccountManager.getAccountManger();
    private MainApplication mMainApplication = MainApplication.getApplication();
    private BaseActivity context;
    private static final String SPLIT_SYMBOL = ">";
    private static final String UNIT_MORE = "[";
    private static final String UNIT_LESS = "]";
    private static final String CONSTANT_COLOR_START = "<font color=#f31961>";
    private static final String CONSTANT_COLOR_END = "</font>";
    private ArrayList<AutoActionInfo> mCopyList = new ArrayList<AutoActionInfo>();
    private ArrayAdapter<String> delayAdapter;
    protected LayoutInflater inflater;
    private WLDialog delayTimeDialog;
    private HouseKeeperTaskDelayTimeView saskDelayTimeView;

    private boolean isShowEdit;


    private ShortCutSelectDataListener shortCutSelectDataListener = new ShortCutSelectDataListener() {

        @Override
        public void onSelectData(AutoActionInfo autoActionInfo) {
            HouseKeeperActionTaskFragment.isSaveTask = true;
            notifyDataSetChanged();
        }
    };


    public AutoActionTaskAdapter(BaseActivity context, List<AutoActionInfo> data) {
        super(context, data);
        this.context = context;
        resources = context.getResources();
        inflater = LayoutInflater.from(context);
        deviceCache = DeviceCache.getInstance(context);
//		initDelayAdapter();
    }

    public void showDropItem(boolean showItem) {
        this.ShowItem = showItem;
    }

    public void setInvisiblePosition(int position) {
        invisilePosition = position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
//		convertView = newView(context, inflater, parent, position);
//		bindView(context, convertView, position, getItem(position));
//		return convertView;
        final AutoActionInfo actionInfo = getItem(position);
        if (StringUtil.equals("3", actionInfo.getType())) {
            View view = new View(context);
            view.setVisibility(View.GONE);
            return view;
        }
        if (StringUtil.equals("0", actionInfo.getType())) {
            View sceneView = (LinearLayout) inflater.inflate(R.layout.device_short_cut_select_data_item, null);
            LinearLayout sceneLayout = (LinearLayout) sceneView.findViewById(R.id.device_short_cut_device_background_layout);
            LinearLayout sceneIconLayout = (LinearLayout) sceneView.findViewById(R.id.device_short_cut_icon_layout);
            LinearLayout menuDeleteView = (LinearLayout) sceneView.findViewById(R.id.device_short_cut_menu_delete_ll);
            LinearLayout menuSortView = (LinearLayout) sceneView.findViewById(R.id.device_short_cut_menu_sort_ll);
            LinearLayout contentLayout = (LinearLayout) sceneView.findViewById(R.id.device_short_cut_content_layout);
            LinearLayout delayoutLayout = (LinearLayout) sceneView.findViewById(R.id.house_link_task_delay_ll);
            TextView deviceTextView = (TextView) sceneView.findViewById(R.id.house_link_delay_time_tv);
            LinearLayout deviceLayout = (LinearLayout) sceneView.findViewById(R.id.device_short_cut_device_detail_layout);
            ImageView sceneIcon = (ImageView) sceneView.findViewById(R.id.device_short_cut_icon_iv);
            TextView sceneName = (TextView) sceneView.findViewById(R.id.device_short_cut_name_tv);
            TextView areaTextView = (TextView) sceneView.findViewById(R.id.device_short_cut_areas_tv);
            TextView minute = (TextView) sceneView.findViewById(R.id.device_short_cut_delay_minute_text);
            TextView seconds = (TextView) sceneView.findViewById(R.id.device_short_cut_delay_sencond_text);
            TextView delayDescripe = (TextView) sceneView.findViewById(R.id.device_short_cut_delay_descripe_text);
            areaTextView.setVisibility(View.GONE);
            sceneIconLayout.setBackgroundResource(R.drawable.scene_state_using);

            if (mMainApplication.sceneInfoMap.containsKey(mAccountManager.getmCurrentInfo().getGwID() + actionInfo.getObject())) {
                sceneLayout.setBackgroundResource(R.drawable.account_manager_item_background);

                SceneInfo sceneInfo = mMainApplication.sceneInfoMap.get(mAccountManager.getmCurrentInfo().getGwID() + actionInfo.getObject());
                Drawable selectIcon = SceneManager.getSceneIconDrawable_Bright(mContext, sceneInfo.getIcon());
                sceneIcon.setImageDrawable(selectIcon);
                sceneName.setText(sceneInfo.getName());

                minute.setText(getTimeMinute(actionInfo.getDelay()));
                seconds.setText(getTimeSeconds(actionInfo.getDelay()));

                //管家延时取消的跳转
                delayDescripe.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mContext, HouseKeeperActionDelayActivity.class);
                        HouseKeeperActionDelayFragment.actionInfo = actionInfo;
                        mContext.startActivity(intent);
                    }
                });
                //设置延时的显示结果

                String delay = actionInfo.getDelay();
                String cancelDelay = actionInfo.getCancelDelay();
                if (!StringUtil.isNullOrEmpty(cancelDelay) && (cancelDelay.equals("1"))) {
                    delayDescripe.setText(R.string.housekeeper_cancel_delay_task);
                } else if (!(delay == null || delay.equals("0") || delay.equals(""))) {
                    int delayNumber = Integer.parseInt(delay);
                    int secondNumber = delayNumber % 60;
                    int minuteNumber = delayNumber / 60;
                    delayDescripe.setText(R.string.scene_delay);
                    delayDescripe.setText(delayDescripe.getText().toString() + minuteNumber + "m" + secondNumber + "s");
                } else {
                    delayDescripe.setText(R.string.housekeeper_no_delay_time);
                }
            } else {
                sceneLayout.setBackgroundResource(R.drawable.account_manager_item_red_background);
                delayoutLayout.setVisibility(View.GONE);
                deviceLayout.setVisibility(View.GONE);
                sceneIcon.setVisibility(View.INVISIBLE);
                deviceTextView.setText("场景已被删除");
            }

            if (isShowEdit) {
                menuDeleteView.setVisibility(View.VISIBLE);
                menuSortView.setVisibility(View.VISIBLE);
            } else {
                menuDeleteView.setVisibility(View.GONE);
                menuSortView.setVisibility(View.GONE);
            }

            menuDeleteView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getData().remove(position);
                    notifyDataSetChanged();
                }
            });

            if (isChanged) {
                if (position == invisilePosition) {
                    if (!ShowItem) {
                        menuDeleteView.setVisibility(View.INVISIBLE);
                        menuSortView.setVisibility(View.INVISIBLE);
                        contentLayout.setVisibility(View.INVISIBLE);
                    }
                }
                if (lastFlag != -1) {
                    if (lastFlag == 1) {
                        if (position > invisilePosition) {
                            Animation animation;
                            animation = getFromSelfAnimation(0, -height);
                            sceneView.startAnimation(animation);
                        }
                    } else if (lastFlag == 0) {
                        if (position < invisilePosition) {
                            Animation animation;
                            animation = getFromSelfAnimation(0, height);
                            sceneView.startAnimation(animation);
                        }
                    }
                }

            }
            return sceneView;
        }
        if (actionInfo.getObject() != null) {
            String[] type = actionInfo.getObject().split(SPLIT_SYMBOL);
            WulianDevice device = deviceCache.getDeviceByID(context,
                    mAccountManager.getmCurrentInfo().getGwID(), type[0]);
            //增加桌面摄像机的筛选
            if (actionInfo.getObject().equals("self")) {
                device = new DesktopCameraDevice(mContext, "camera");
            }
            if (device != null) {
                final DeviceShortCutSelectDataItem item = device.onCreateShortCutSelectDataView(deviceMap.get(actionInfo), LayoutInflater.from(mContext), actionInfo);
                deviceMap.put(device, item);
                item.setShortCutSelectDataListener(shortCutSelectDataListener);
                View view = item.getView();
                if (!StringUtil.isNullOrEmpty(actionInfo.getEpData()) || !StringUtil.isNullOrEmpty(actionInfo.getCancelDelay())) {
                    item.getContentBackgroundLayout().setBackgroundResource(R.drawable.account_manager_item_background);
                } else {
                    item.getContentBackgroundLayout().setBackgroundResource(R.drawable.account_manager_item_red_background);
                }
                if (isShowEdit) {
                    item.getMenuDeleteView().setVisibility(View.VISIBLE);
                    item.getMenuSortView().setVisibility(View.VISIBLE);
                } else {
                    item.getMenuDeleteView().setVisibility(View.GONE);
                    item.getMenuSortView().setVisibility(View.GONE);
                }

                item.setShortCutSelectDataDeleteListener(new ShortCutSelectDataDeleteListener() {

                    @Override
                    public void onDelete() {
                        if (getData().size() >= position + 1) {
                            getData().remove(position);
                        }
                        notifyDataSetChanged();
                    }
                });
                final TextView minute = item.getDelayMinuteText();
                final TextView seconds = item.getDelaySencondsText();
                minute.setText(getTimeMinute(actionInfo.getDelay()));
                seconds.setText(getTimeSeconds(actionInfo.getDelay()));
//				item.getDelayLineLayout().setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						showSelectDelayTimeDialog(actionInfo, minute,seconds);
//					}
//				});
                //管家延时取消的跳转
                item.getDelayDescripeText().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mContext, HouseKeeperActionDelayActivity.class);
                        HouseKeeperActionDelayFragment.actionInfo = actionInfo;
                        mContext.startActivity(intent);
                    }
                });
                //设置延时的显示结果

                String delay = actionInfo.getDelay();
                String cancelDelay = actionInfo.getCancelDelay();
                TextView delayDescripeText = item.getDelayDescripeText();
                if (!StringUtil.isNullOrEmpty(cancelDelay) && (cancelDelay.equals("1"))) {
                    delayDescripeText.setText(R.string.housekeeper_cancel_delay_task);
                } else if (!(delay == null || delay.equals("0") || delay.equals(""))) {
                    int delayNumber = Integer.parseInt(delay);
                    int secondNumber = delayNumber % 60;
                    int minuteNumber = delayNumber / 60;
                    delayDescripeText.setText(R.string.scene_delay);
                    delayDescripeText.setText(delayDescripeText.getText().toString() + minuteNumber + "m" + secondNumber + "s");
                } else {
                    delayDescripeText.setText(R.string.housekeeper_no_delay_time);
                }


                if (isChanged) {
                    if (position == invisilePosition) {
                        if (!ShowItem) {
                            item.getMenuDeleteView().setVisibility(View.INVISIBLE);
                            item.getMenuSortView().setVisibility(View.INVISIBLE);
                            item.getContentLayout().setVisibility(View.INVISIBLE);
                        }
                    }
                    if (lastFlag != -1) {
                        if (lastFlag == 1) {
                            if (position > invisilePosition) {
                                Animation animation;
                                animation = getFromSelfAnimation(0, -height);
                                view.startAnimation(animation);
                            }
                        } else if (lastFlag == 0) {
                            if (position < invisilePosition) {
                                Animation animation;
                                animation = getFromSelfAnimation(0, height);
                                view.startAnimation(animation);
                            }
                        }
                    }

                }
                return view;
            } else {
                String deviceType = type[1];
                String deviceName = mContext.getResources().getString(DeviceResource.getResourceInfo(deviceType).name);
                if (actionInfo.getObject().equals("self")) {
                    deviceType = "camera";
                    deviceName = mContext.getResources().getString(DeviceResource.getResourceInfo(deviceType).name);
                }
                return getNoDeviceView(position, deviceName);
            }
        } else {
            return getNoDeviceView(position, mContext.getResources().getString(R.string.nav_device_title));
        }

    }

    private View getNoDeviceView(final int position, String deviceName) {
        View defaultView = (LinearLayout) inflater.inflate(R.layout.device_short_cut_select_data_item, null);
        ImageView deviceImageView = (ImageView) defaultView.findViewById(R.id.device_short_cut_icon_iv);
        TextView deviceTextView = (TextView) defaultView.findViewById(R.id.house_link_delay_time_tv);
        LinearLayout deviceLayout = (LinearLayout) defaultView.findViewById(R.id.device_short_cut_device_detail_layout);
        LinearLayout menuDeleteView = (LinearLayout) defaultView.findViewById(R.id.device_short_cut_menu_delete_ll);
        LinearLayout menuSortView = (LinearLayout) defaultView.findViewById(R.id.device_short_cut_menu_sort_ll);
        LinearLayout delayoutLayout = (LinearLayout) defaultView.findViewById(R.id.house_link_task_delay_ll);
        delayoutLayout.setVisibility(View.GONE);
        deviceLayout.setVisibility(View.GONE);
        deviceImageView.setVisibility(View.INVISIBLE);
        if (isShowEdit) {
            menuDeleteView.setVisibility(View.VISIBLE);
            menuSortView.setVisibility(View.VISIBLE);
        } else {
            menuDeleteView.setVisibility(View.GONE);
            menuSortView.setVisibility(View.GONE);
        }
        //删除按钮点击事件
        menuDeleteView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getData().remove(position);
                notifyDataSetChanged();
            }
        });
        deviceTextView.setText(deviceName + mContext.getResources().getString(R.string.home_device_task_deleteed));
        return defaultView;
    }

    @Override
    protected void bindView(Context context, View view, final int pos,
                            final AutoActionInfo item) {
        super.bindView(context, view, pos, item);

//		LinearLayout deleteLayout = (LinearLayout) view.findViewById(R.id.house_link_task_delete_layout);
//		ImageView deleteImg = (ImageView) view.findViewById(R.id.house_link_task_delete_img);
//		LinearLayout moveLayout = (LinearLayout) view.findViewById(R.id.house_link_task_move_layout);
//		ImageView moveImag = (ImageView) view.findViewById(R.id.house_link_task_move_img);
//		
//		LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.house_link_task_device_copntent_iv);
//		
//		ImageView deviceIcon = (ImageView) view.findViewById(R.id.house_link_task_device_icon_iv);
//		TextView deviceName = (TextView) view.findViewById(R.id.house_link_task_device_name_tv);
//		TextView deviceArea = (TextView) view.findViewById(R.id.house_link_task_device_area_tv);
//		ToggleButton deviceButton = (ToggleButton) view.findViewById(R.id.house_link_task_device_data_tb);
//		ImageView deviceData = (ImageView) view.findViewById(R.id.house_link_task_device_data_iv);
//		
//		LinearLayout timeLayout = (LinearLayout) view.findViewById(R.id.house_link_task_delay_choose_time);
//		final TextView detailtext = (TextView) view.findViewById(R.id.house_link_task_delay_sencond_text);
//		
//		String[] type = item.getObject().split(SPLIT_SYMBOL);
//		final WulianDevice device = deviceCache.getDeviceByID(context,
//				mAccountManager.getmCurrentInfo().getGwID(), type[0]);
//		deviceIcon.setImageDrawable(device.getDefaultStateSmallIcon());
//		StringBuilder sb = new StringBuilder();
//		if (!device.isDeviceOnLine()) {
//			sb.append(UNIT_MORE);
//			// use spannable String to instead of this
//			sb.append(CONSTANT_COLOR_START);
//			sb.append(resources.getString(R.string.device_offline));
//			sb.append(CONSTANT_COLOR_END);
//			sb.append(UNIT_LESS);
//		}
//		sb.append(DeviceTool.getDeviceShowName(device));
//		sb.append("-");
//		sb.append(DeviceUtil.ep2IndexString(type[2]));
//		deviceName.setText(device.isDeviceOnLine() ? sb.toString() : Html
//				.fromHtml(sb.toString()));
//		detailtext.setText(getTimeStr(item.getDelay()));
//		timeLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				showSelectDelayTimeDialog(item, detailtext);
//			}
//		});
//		setAreaName(device,deviceArea);
//		
//		deleteImg.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				data.remove(pos);
//				notifyDataSetChanged();
//			}
//		});
//		
//		if(isShowEdit){
//			deleteLayout.setVisibility(View.VISIBLE);
//			moveLayout.setVisibility(View.VISIBLE);
//		}else{
//			deleteLayout.setVisibility(View.GONE);
//			moveLayout.setVisibility(View.GONE);
//		}

//		final DeviceShortCutSelectDataItem holder = device.onCreateShortCutSelectDataView(item, inflater, autoActionInfo)
//		if(holder.isShowDialog()){
//			deviceData.setVisibility(View.GONE);
//			deviceButton.setVisibility(View.VISIBLE);
//			final Defenseable defenseable = (Defenseable) device;
//			if (defenseable.getDefenseSetupProtocol().equals(item.getEpData())) {
//				deviceButton.setChecked(true);
//			} else {
//				deviceButton.setChecked(false);
//			}
//			deviceButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//					if (isChecked) {
//						item.setEpData(defenseable.getDefenseSetupProtocol());
//					} else {
//						item.setEpData(defenseable.getDefenseUnSetupProtocol());
//					}
//				}
//			});
//		}else{
//			deviceButton.setVisibility(View.GONE);
//			deviceData.setVisibility(View.VISIBLE);
//			deviceData.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
////					Bundle bundle = new Bundle();
////					bundle.putString(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, HouseKeeperLinkTaskSensorFragment.class.getName());
////					bundle.putString(HouseKeeperLinkTaskSensorFragment.DEV_GW_ID, device.getDeviceGwID());
////					bundle.putString(HouseKeeperLinkTaskSensorFragment.DEV_ID, device.getDeviceID());
////					holder.startActivity(context, bundle);
////					HouseKeeperLinkTaskSensorFragment.setSelectSensorAutoConditionListener(new SelectSensorAutoConditionListener() {
////						
////						@Override
////						public void onSelectSensorAutoConditionChanged(String value, String des) {
////							if(conditionDeviceListener != null){
////								String deviceData = device.getDeviceID() + ">" + device.getDeviceType() + 
////									">" + device.getDeviceInfo().getDevEPInfo().getEp() + ">" + device.getDeviceInfo().getDevEPInfo().getEpType();
////								conditionDeviceListener.onConditionDeviceListenerChanged(deviceData, value, des);
////							}
////							mActivity.finish();
////						}
////					});
//				}
//			});
//		}


    }

//	private void setAreaName(WulianDevice device,TextView deviceArea){
//		StringBuilder sb = new StringBuilder();
//		DeviceAreaEntity entity = AreaGroupManager.getInstance().getDeviceAreaEntity(device.getDeviceGwID(),device.getDeviceRoomID());
//		if (entity != null){
//			sb.append(UNIT_MORE);
//			sb.append(entity.getName());
//			sb.append(UNIT_LESS);
//			deviceArea.setText(sb.toString());
//		}
//		else {
//			sb.append(UNIT_MORE);
//			sb.append(mContext.getResources().getString(
//					R.string.device_config_edit_dev_area_type_other_default));
//			sb.append(UNIT_LESS);
//			deviceArea.setText(sb.toString());
//		}
//	}

    /**
     * 选择延时对话框
     */
    private void showSelectDelayTimeDialog(final AutoActionInfo info,
                                           final TextView minute, final TextView seconds) {
        saskDelayTimeView = new HouseKeeperTaskDelayTimeView(mContext, info.getDelay());
        WLDialog.Builder builder = new WLDialog.Builder(context);
        builder.setTitle(R.string.house_rule_add_new_task_delay_time);
        builder.setContentView(saskDelayTimeView);
        builder.setNegativeButton(context.getResources().getString(R.string.cancel));
        builder.setPositiveButton(context.getResources().getString(R.string.common_ok));
        builder.setListener(new MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {
                HouseKeeperActionTaskFragment.isSaveTask = true;
                int delayMinute = saskDelayTimeView.getSettingMinuesTime();
                int delaySendcos = saskDelayTimeView.getSettingSecondsTime();
                info.setDelay((delayMinute * 60 + delaySendcos) + "");
                minute.setText(delayMinute + "m");
                seconds.setText(delaySendcos + "s");
            }

            @Override
            public void onClickNegative(View contentViewLayout) {

            }
        });
        delayTimeDialog = builder.create();
        delayTimeDialog.show();
    }

    private String getTimeMinute(String t) {
        int time = 0;
        if (!StringUtil.isNullOrEmpty(t)) {
            time = StringUtil.toInteger(t);
        }
        String result = "";
        if (time >= 60) {
            result = time / 60 + "m";
        } else {
            result = 0 + "m";
        }
        return result;
    }

    private String getTimeSeconds(String t) {
        int time = 0;
        if (!StringUtil.isNullOrEmpty(t)) {
            time = StringUtil.toInteger(t);
        }
        String result = "";
        if (time >= 60) {
            result = time % 60 + "s";
        } else {
            result = time + "s";
        }
        return result;
    }

    /**
     * 根据字符串获取时间秒数
     *
     * @param str
     * @return
     */
    private int getSeconds(String str) {
        int result = 0;
        if (!StringUtil.isNullOrEmpty(str)) {
            if (str.contains("s")) {
                result = StringUtil
                        .toInteger(str.substring(0, str.length() - 1));
            } else if (str.contains("m")) {
                int minute = StringUtil.toInteger(str.substring(0,
                        str.length() - 1));
                result = minute * 60;
            }
        }
        return result;
    }

    /***
     * 动态修改ListVIiw的方位.
     *
     * @param start
     * 点击移动的position
     * @param down
     * 松开时候的position
     */
    private int invisilePosition = -1;
    private boolean isChanged = true;
    private boolean ShowItem = false;

    public void exchangeCopy(int startPosition, int endPosition) {
        AutoActionInfo startObject = getCopyItem(startPosition);
        if (startPosition < endPosition) {
            mCopyList.add(endPosition + 1, startObject);
            mCopyList.remove(startPosition);
        } else {
            mCopyList.add(endPosition, startObject);
            mCopyList.remove(startPosition + 1);
        }
        isChanged = true;
//		notifyDataSetChanged();
    }

    public AutoActionInfo getCopyItem(int position) {
        return mCopyList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public void copyList() {
        mCopyList.clear();
        for (AutoActionInfo info : getData()) {
            mCopyList.add(info);
        }
    }

    public void pastList() {
        getData().clear();
        for (AutoActionInfo info : mCopyList) {
            getData().add(info);
        }
    }

    private boolean isSameDragDirection = true;
    private int lastFlag = -1;
    private int height;
    private int dragPosition = -1;

    public void setIsSameDragDirection(boolean value) {
        isSameDragDirection = value;
    }

    public void setLastFlag(int flag) {
        lastFlag = flag;
    }

    public void setHeight(int value) {
        height = value;
    }

    public void setCurrentDragPosition(int position) {
        dragPosition = position;
    }

    public Animation getFromSelfAnimation(int x, int y) {
        TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
        go.setInterpolator(new AccelerateDecelerateInterpolator());
        go.setFillAfter(true);
        go.setDuration(100);
        go.setInterpolator(new AccelerateInterpolator());
        return go;
    }

    public Animation getToSelfAnimation(int x, int y) {
        TranslateAnimation go = new TranslateAnimation(
                Animation.ABSOLUTE, x, Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, y, Animation.RELATIVE_TO_SELF, 0);
        go.setInterpolator(new AccelerateDecelerateInterpolator());
        go.setFillAfter(true);
        go.setDuration(100);
        go.setInterpolator(new AccelerateInterpolator());
        return go;
    }

    public void setIsShowEdit(boolean isShow) {
        isShowEdit = isShow;
    }
}
