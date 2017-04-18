package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.DoorLock4HistoryAdapter;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.utils.HttpUtil;

/**
 * Created by syf on 2017/1/13.
 */
public class EditDoorLock4HistoryFragment extends EditDoorLock6HistoryFragment {

    private String gwID;
    private String devID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gwID = getArguments().getString(EditDoorLock4Fragment.GWID);
        devID = getArguments().getString(EditDoorLock4Fragment.DEVICEID);
        adapter = new DoorLock4HistoryAdapter(mActivity, null,gwID,devID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void initBar() {
        // TODO Auto-generated method stub
        super.initBar();
        getSupportActionBar().setTitle(getString(R.string.smartLock_unlock_history));
        getSupportActionBar().setIconText(getString(R.string.set_titel));
    }

    @Override
    public synchronized List<MessageEventEntity> getMessages(long starttime,
                                                             long comparetime) {
        List<MessageEventEntity> entites = new ArrayList<MessageEventEntity>();
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("gwID", gwID);
            jsonObject.put("devID", devID);
            jsonObject.put("time", String.valueOf(starttime));// 1426867199999
//            String json = HttpUtil.postWulianCloud(
//                    WulianCloudURLManager.getDeviceInfoURL(), jsonObject);
            String json = HttpUtil
                    .postWulianCloud(
                            "https://acs.wuliancloud.com:33443/acs/gateway/queryDeviceData",
                            jsonObject);
            if (json != null) {
                Logger.debug("json" + json);
                JSONObject obj = JSON.parseObject(json);
                JSONArray array = obj.getJSONArray("retData");
                // 选择的日期的时间戳+一天的时间戳
                if (array != null) {// 1426831354388(3/20 14:2:34)
                    // 1426830397348(3/20 13:46:37)
                    // 1426757988159 > 1426734845456(3/19
                    // 11:14:5)
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject alarmObj = array.getJSONObject(i);
                        MessageEventEntity entity = new MessageEventEntity();
                        // long outl = l;
                        if (Long.parseLong(alarmObj.getString("time")) < comparetime) {
                            System.out.println("------>getMessages");
                            break;
                        } else {
                            entity.setTime(alarmObj.getString("time"));
                            // entity.setSmile(alarmObj.getString("epStatus"));
                            entity.setGwID(alarmObj.getString("gwID"));
                            entity.setEpData(alarmObj.getString("epData"));
                            entity.setEpType(alarmObj.getString("epType"));
                            entity.setEp(alarmObj.getString("ep"));
                            entity.setType(alarmObj.getString("type"));
                            entity.setDevID(alarmObj.getString("devID"));
                            entity.setExtData(alarmObj.getString("extData"));
                            entites.add(entity);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if(entites.size()<10){
        // loadTenMessages(selectedDatime);
        // }else{
        // currentNumberr=0;
        // }
        return entites;

    }

    /**
     * 加载设备报警信息
     */
    @Override
    public synchronized void loadDeviceAlarmMessage(final Date date) {
        mDialogManager.showDialog(ALARM_KEY, mActivity, null, null);
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                entites.clear();
                currentCompareTime = date.getTime();
                List<MessageEventEntity> list = getMessages(
                        (date.getTime() + DayTime13), date.getTime());
                if (!list.isEmpty()) {
                    MessageEventEntity message = list.get(list.size() - 1);
                    selectedDatime = new Date(StringUtil.toLong(message
                            .getTime()));
                }
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("----------------epdata"
                            + list.get(i).getEpData());
                    entites.add(list.get(i));
                }
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mDialogManager.dimissDialog(ALARM_KEY, 0);
                        adapter.swapData(entites);
                        mHomeAlarmList.setSelection(adapter.getCount());
                        mHomeAlarmList.setResultSize(entites.size());
                    }
                });
//				if (currentNumberr < 10) {
//					loadTenMessages(selectedDatime);
//				}
            }
        });
    }

    // 上拉默认加载十条数据
    @Override
    public synchronized void loadTenMessages(final Date date) {
//		currentReloadTime++;
        mHomeAlarmList.showLoad();
        TaskExecutor.getInstance().executeDelay(new Runnable() {

            @Override
            public void run() {
                final List<MessageEventEntity> list = getMessages(
                        date.getTime(), currentCompareTime);
                if (!list.isEmpty()) {
                    MessageEventEntity message = list.get(list.size() - 1);
                    selectedDatime = new Date(StringUtil.toLong(message
                            .getTime()));
                }
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("----------------epdata"
                            + list.get(i).getEpData());
                    entites.add(list.get(i));
                }
                if (selectedDatime.getTime() != tmpCompareTime) {// 避免ui没有跟新完成时多次上滑造成重复加载的现象
                    // add by hxc

                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            entites.addAll(list);
                            mHomeAlarmList.setResultSize(list.size());
                            adapter.swapData(entites);
                            mHomeAlarmList.closeAllLoad();
                            tmpCompareTime = selectedDatime.getTime();
                        }
                    });

                }
            }
        }, 1000);
//		if (currentNumberr < 10 && currentReloadTime < 10) {
//			loadTenMessages(selectedDatime);
//		} else {
//			currentNumberr = 0;
//			currentReloadTime = 0;
//		}
    }


}
