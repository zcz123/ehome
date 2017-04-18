package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * <p>
 * send control : 10:消除报警
 * <p>
 * receive data : (十六进制) <br>
 * 位1～2:数据类型(01:默认) <br>
 * 位3～4:设备数据(01:进入,02:出去,03:拥堵)
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_HUMAN_TRAFFIC}, category = Category.C_ENVIRONMENT)
public class WL_47_Human_Traffic extends SensorableDeviceImpl {
    private String mNumIn;
    private String mNumOut;
    private String mCrowd;
    private ImageView mBigView;
    private ImageView mRefresh;
    private TextView mStatus;
    private TextView mStatusIn;
    private TextView mStatusOut;

    private static final String DATA_STATE_IN_01 = "01";
    private static final String DATA_STATE_OUT_02 = "02";
    private static final String DATA_STATE_BUSY_03 = "03";

    private static final int SMALL_SOME_D = R.drawable.device_human_traffic_busy;
    private static final int SMALL_N_A_D = R.drawable.device_human_traffic_n_a;

    private static final int BIG_SOME_D = R.drawable.device_human_traffic_some;
    private static final int BIG_N_A_D = R.drawable.device_human_traffic_none;

    private String pluginName = "Human-flow-detector.zip";

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    protected final Runnable mRefreshStateRunnable = new Runnable() {
        @Override
        public void run() {

            initViewStatus();

        }
    };

    public WL_47_Human_Traffic(Context context, String type) {
        super(context, type);
    }

    public boolean isOutAndIn() {
        if (epData == null || epData.length() < 4)
            return false;
        return isSameAs(DATA_STATE_BUSY_03, epData.substring(2, 4));
    }

    public boolean isIn() {
        if (epData == null || epData.length() < 4)
            return false;
        return isSameAs(DATA_STATE_IN_01, epData.substring(2, 4));
    }

    public boolean isOut() {
        if (epData == null || epData.length() < 4)
            return false;
        return isSameAs(DATA_STATE_OUT_02, epData.substring(2, 4));
    }

    @Override
    public Drawable getStateSmallIcon() {
        if (isOut() || isIn() || isOutAndIn()) {
            return getDrawable(SMALL_SOME_D);
        } else {
            return getDrawable(SMALL_N_A_D);
        }
    }

    @Override
    public Drawable[] getStateBigPictureArray() {
        Drawable[] drawables = new Drawable[1];
        if (!"0".equals(mNumIn) || !"0".equals(mNumOut) || !"0".equals(mCrowd)) {
            drawables[0] = getDrawable(BIG_SOME_D);
        } else {
            drawables[0] = getDrawable(BIG_N_A_D);
        }
        return drawables;
    }

    @Override
    public CharSequence parseDataWithProtocol(String epData) {

        String stateStr = getString(R.string.device_exception);
        if (isOutAndIn()) {
            stateStr = getString(R.string.device_state_busy);
        } else if (isIn()) {
            stateStr = getString(R.string.device_state_in);
        } else if (isOut()) {
            stateStr = getString(R.string.device_state_out);
        }
        return stateStr;
    }

    @Override
    public boolean isLinkControl() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveState) {
        View view = super.onCreateView(inflater, container, saveState);
        view = inflater
                .inflate(R.layout.device_human_traffic, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);

        mBigView = (ImageView) view.findViewById(R.id.device_human_traffic_big);
        mRefresh = (ImageView) view.findViewById(R.id.device_human_traffic_refresh);
        mStatus = (TextView) view.findViewById(R.id.device_human_traffic_status);
        mStatusIn = (TextView) view.findViewById(R.id.device_human_traffic_status_in);
        mStatusOut = (TextView) view.findViewById(R.id.device_human_traffic_status_out);

        mRefresh.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_refresh);
        mRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                loadHumanTrafficMessage(getStartTime(), getEndTime());
            }
        });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadHumanTrafficMessage(getStartTime(), getEndTime());
    }

    public void initViewStatus() {
        if (mCrowd == null) {
            mCrowd = "0";
        }
        if (mNumIn == null) {
            mNumIn = "0";
        }
        if (mNumOut == null) {
            mNumOut = "0";
        }
        mBigView.setImageDrawable(getStateBigPictureArray()[0]);
        mStatus.setText(mContext.getString(R.string.device_state_busy) + ":" + mCrowd + getString(cc.wulian.smarthomev5.R.string.html_electronic_scale_times));
        mStatusIn.setText(mContext.getString(R.string.device_state_in) + ":" + mNumIn + getString(cc.wulian.app.model.device.R.string.device_state_status_total));
        mStatusOut.setText(mContext.getString(R.string.device_state_out) + ":" + mNumOut + getString(cc.wulian.app.model.device.R.string.device_state_status_total));
        progressDialogManager.dimissDialog("refreshDate", 0);

    }

    @Override
    protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
        List<MenuItem> items = super.getDeviceMenuItems(manager);
        MenuItem settingItem = new MenuItem(mContext) {

            @Override
            public void initSystemState() {
                titleTextView.setText(mContext
                        .getString(cc.wulian.smarthomev5.R.string.device_human_traffic_statistics));
                iconImageView
                        .setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
            }

            @Override
            public void doSomething() {
                getPlugin();
                manager.dismiss();
            }
        };
        if (isDeviceOnLine())
            items.add(settingItem);
        return items;
    }

    private void getPlugin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PluginsManager pm = PluginsManager.getInstance();
                pm.getHtmlPlugin(mContext, pluginName, new PluginsManagerCallback() {

                    @Override
                    public void onGetPluginSuccess(PluginModel model) {
                        File file = new File(model.getFolder(), model.getEntry());
                        String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
                        if (file.exists()) {
                            uri = "file:///" + file.getAbsolutePath();
                        } else if (LanguageUtil.isChina()) {
                            uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                        }

                        Intent intent = new Intent();
                        intent.setClass(mContext, Html5PlusWebViewActvity.class);
                        intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
                        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
                        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onGetPluginFailed(final String hint) {
                        if (hint != null && hint.length() > 0) {
                            Handler handler = new Handler(Looper.getMainLooper());
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

    private Long getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    private Long getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime().getTime();
    }

    public synchronized void loadHumanTrafficMessage(final long starttime,
                                                     final long endtime) {
        progressDialogManager.showDialog("refreshDate", getContext(), null, null);
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getMessages(starttime, endtime);
                mHandler.post(mRefreshStateRunnable);
            }
        });
    }

    public synchronized void getMessages(long starttime,
                                         long endtime) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("gwID", gwID);
            jsonObject.put("devID", devID);
            jsonObject.put("type", "2");
            jsonObject.put("startDate", String.valueOf(starttime));
            jsonObject.put("endDate", String.valueOf(endtime));

            String json = HttpUtil
                    .postWulianCloud(WulianCloudURLManager.getHumanTrafficInfoURL(),
                            jsonObject);

            if (json != null) {
                Logger.debug("json" + json);
                JSONObject obj = JSON.parseObject(json);
                JSONArray array = obj.getJSONArray("retData");
                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject retDataObj = array.getJSONObject(i);
                        JSONObject dayObj = retDataObj.getJSONObject("data");
                        JSONObject stateObj = dayObj.getJSONObject("day");
                        mNumIn = stateObj.getString("in");
                        mNumOut = stateObj.getString("out");
                        mCrowd = stateObj.getString("crowd");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
