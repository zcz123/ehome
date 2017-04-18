package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DateUtil;

/**
 * Created by WIN7 on 2014/7/11.
 */
public class AlarmMessageAdapter extends WLBaseAdapter<MessageEventEntity> {

    private final DeviceCache mCache;

    public AlarmMessageAdapter(Context context, List<MessageEventEntity> data) {
        super(context, data);
        mCache = DeviceCache.getInstance(context);
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater, ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.home_alarm_message_item, parent, false);
    }

    @Override
    protected void bindView(Context context, View view, int pos, MessageEventEntity item) {
        TextView mTextView = (TextView) view.findViewById(R.id.alarm_detail_message);
        TextView mTimeView = (TextView) view.findViewById(R.id.alarm_message_time);
        TextView mStatusTextView = (TextView) view.findViewById(R.id.alarm_status);
        String mtime = DateUtil.getHourAndMinu(mContext, StringUtil.toLong(item.getTime()));
        mTimeView.setText(mtime);
        WulianDevice device = mCache.getDeviceByID(mContext, item.gwID, item.devID);
        String formatString = "";
        if (device != null) {
            WulianDevice childDevcie = mCache.getDeviceByIDEp(mContext, item.gwID, item.devID, item.ep);
            if (childDevcie != null) {
                String epName = device.getDeviceName();
                if (!StringUtil.isNullOrEmpty(epName)) {
                    formatString = epName;
                    if (StringUtil.equals(epName, "-1")) {
                        formatString = device.getDefaultDeviceName();
                    }
                } else {
                    formatString = DeviceTool.getDeviceShowName(device);
                }
            } else {
                formatString = DeviceTool.getDeviceShowName(device);
            }
        } else {
            if (item.getEpType().equals("DC")) {//新增DC设备的首页报警
                formatString = item.getEpData();
            } else {
                formatString = DeviceTool.getDeviceNameByIdAndType(mContext, item.devID, item.epType);
            }
        }

        mTextView.setText(formatString);
        String status = "";
        if (item.isMessageAlarm()) {
            if (item.epData != null && item.epData.length() > 2) {
                status = item.epData.substring(item.epData.length() - 2, item.epData.length());
            } else {
                status = mResources.getString(R.string.scene_alarm);
            }
        } else if (item.isMessageOffline()) {
            status = mResources.getString(R.string.device_offline);
        } else if (item.isMessageLowPower()) {
            status = mResources.getString(R.string.home_message_low_power_warn);
        } else if (item.isMessageDestory()) {
            status = mResources.getString(R.string.set_sound_notification_bell_prompt_break);
        } else {
            status = mResources.getString(R.string.scene_alarm);
        }
        mStatusTextView.setText(status);

    }
}
