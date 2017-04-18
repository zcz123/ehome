package cc.wulian.smarthomev5.fragment.setting.timezone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.ZoneListAdapter;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.entity.ZoneListEntity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.view.AutoRefreshListView;

public class SelectTimeZomeFragment extends WulianFragment {

    private AutoRefreshListView zoneListView;
    private EditText searchEditText;
    private ZoneListAdapter mAdapter;
    private List<ZoneListEntity> mDatas = new ArrayList<ZoneListEntity>();
    private static final String SHOW_DIALOG_KEY = "select_TimeZome_key";
    private final static String GET_DATA_TYPE_SEARCH = "0";
    private final static String GET_DATA_TYPE_INIT_VIEW = "1";
    private int areaCount = 1;
    private int searchCount = 1;
    private boolean isSearch = false;//判断是否是搜索，默认为false 不是搜索到的，true为搜索
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (mDatas == null || mDatas.size() == 0) {
                        zoneListView.setVisibility(View.GONE);
                    } else {
                        //完成主界面更新,拿到数据
                        zoneListView.setVisibility(View.VISIBLE);
                        mAdapter.swapData(mDatas);
                    }
                    break;
            }
        }

    };
    private String searchString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_setting_timezone_select, container,
                false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {


        mAdapter = new ZoneListAdapter(mActivity, mDatas);
        searchEditText = (EditText) view.findViewById(R.id.config_search_et);
        zoneListView = (AutoRefreshListView) view.findViewById(R.id.setting_zone_lv);

        zoneListView.setAdapter(mAdapter);
        zoneListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (mDatas != null && arg2 < mDatas.size()) {
                    ZoneListEntity enter = mDatas.get(arg2);
                    String gwID = AccountManager.getAccountManger().getmCurrentInfo()
                            .getGwID();
                    mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null,
                            null);
                    String zoneName = enter.getCity().replace("(", "/").replace(")", "");
                    String zone = enter.getTimeZone();
                    System.out.println("---------------+" + zoneName + "  " + zone);
                    SendMessage.sendSetTimeZoneConfigMsg(gwID,
                            null, zoneName, zone, null);
                }
            }
        });
        zoneListView.setOnLoadListener(new AutoRefreshListView.OnLoadListener() {
            @Override
            public void onLoad() {
                loadMessages();
                zoneListView.onLoadComplete();
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO
                        || i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_SEND
                        || i == EditorInfo.IME_ACTION_NEXT) {
                    if (searchEditText.getText().toString() == null || searchEditText.getText().toString().equals("")) {
                        mAdapter.swapData(mDatas);
                    } else {
                        getSearchView();
                    }
                }

                return false;
            }
        });
    }

    protected void getSearchView() {
        isSearch = true;
        zoneListView.setVisibility(View.VISIBLE);
        searchString = searchEditText.getText().toString().trim();
        initView(GET_DATA_TYPE_SEARCH, searchString);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView(GET_DATA_TYPE_INIT_VIEW, null);
    }

    private void initView(final String getDataType, final String searchString) {
        mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
        getTimeZoneThread(getDataType, searchString);
    }

    private void getTimeZoneThread(final String getDataType, final String searchString) {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    if (getDataType.equals(GET_DATA_TYPE_SEARCH)) {
                        jsonObject.put("city", searchString);
                    }
                    jsonObject.put("pageSize", "20");
                    if (LanguageUtil.isChina()) {
                        jsonObject.put("lang", "zh-cn");
                    } else if (LanguageUtil.getLanguage().equals("iw")) {
                        jsonObject.put("lang", "he");
                    } else {
                        jsonObject.put("lang", "en");
                    }
                    searchCount = searchCount + 1;
                    String json = HttpUtil.postWulianCloud(
                            WulianCloudURLManager.getTimeZeroURL(), jsonObject);
                    System.out.println(json);
                    if (!StringUtil.isNullOrEmpty(json)) {
                        mDatas.clear();
                        JSONObject obj = JSON.parseObject(json);
                        JSONArray array = obj.getJSONArray("retData");
                        if (array != null) {// 1426831354388(3/20 14:2:34)
                            mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
                            for (int i = 0; i < array.size(); i++) {
                                ZoneListEntity entity = new ZoneListEntity();
                                entity.seteCity(array.getJSONObject(i).getString("eCity"));
                                entity.setCity(array.getJSONObject(i).getString("city"));
                                entity.setTimeZone(array.getJSONObject(i).getString("timeZone"));
                                entity.setGmt(array.getJSONObject(i).getString("gmt"));
                                mDatas.add(entity);
                            }
                            //耗时操作，完成之后发送消息给Handler，完成UI更新；
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getTimeZoneRefushThread() {
        areaCount = areaCount + 1;
        zoneListView.showLoad();
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pageNum", areaCount + "");
                    jsonObject.put("pageSize", "20");
                    if (LanguageUtil.isChina()) {
                        jsonObject.put("lang", "zh-cn");
                    } else if (LanguageUtil.getLanguage().equals("iw")) {
                        jsonObject.put("lang", "he");
                    } else {
                        jsonObject.put("lang", "en");
                    }
                    String json = HttpUtil.postWulianCloud(
                            WulianCloudURLManager.getTimeZeroURL(), jsonObject);
                    System.out.println(json);
                    if (!StringUtil.isNullOrEmpty(json)) {
                        JSONObject obj = JSON.parseObject(json);
                        JSONArray array = obj.getJSONArray("retData");
                        mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
                        if (array != null) {
                            for (int i = 0; i < array.size(); i++) {
                                ZoneListEntity entity = new ZoneListEntity();
                                entity.seteCity(array.getJSONObject(i).getString("eCity"));
                                entity.setCity(array.getJSONObject(i).getString("city"));
                                entity.setTimeZone(array.getJSONObject(i).getString("timeZone"));
                                entity.setGmt(array.getJSONObject(i).getString("gmt"));
                                mDatas.add(entity);
                            }
                            //耗时操作，完成之后发送消息给Handler，完成UI更新；
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getSearchZoneRefushThread(final String searchString) {
        areaCount = areaCount + 1;
        zoneListView.showLoad();
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("city", searchString);
                    jsonObject.put("pageSize", "20");
                    if (LanguageUtil.isChina()) {
                        jsonObject.put("lang", "zh-cn");
                    } else if (LanguageUtil.getLanguage().equals("iw")) {
                        jsonObject.put("lang", "he");
                    } else {
                        jsonObject.put("lang", "en");
                    }
                    String json = HttpUtil.postWulianCloud(
                            WulianCloudURLManager.getTimeZeroURL(), jsonObject);
                    System.out.println(json);
                    if (!StringUtil.isNullOrEmpty(json)) {
                        JSONObject obj = JSON.parseObject(json);
                        JSONArray array = obj.getJSONArray("retData");
                        mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
                        if (array != null) {
                            for (int i = 0; i < array.size(); i++) {
                                ZoneListEntity entity = new ZoneListEntity();
                                entity.seteCity(array.getJSONObject(i).getString("eCity"));
                                entity.setCity(array.getJSONObject(i).getString("city"));
                                entity.setTimeZone(array.getJSONObject(i).getString("timeZone"));
                                entity.setGmt(array.getJSONObject(i).getString("gmt"));
                                mDatas.add(entity);
                            }
                            //耗时操作，完成之后发送消息给Handler，完成UI更新；
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initBar() {
        this.mActivity.resetActionMenu();
        getSupportActionBar().setTitle(
                mApplication.getResources().getString(
                        R.string.gateway_timezone_setting_select_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setIconText(
                mApplication.getResources().getString(
                        R.string.gateway_timezone_setting));
    }

    public synchronized void loadMessages() {
        if (!isSearch) {
            getTimeZoneRefushThread();
        } else {
            getSearchZoneRefushThread(searchString);
        }
    }

    public void onEventMainThread(FlowerEvent event) {
        if (FlowerEvent.ACTION_FLOWER_TIMEZONE_SET.equals(event.getAction())
                || FlowerEvent.ACTION_FLOWER_TIMEZONE_GET.equals(event
                .getAction())) {
            mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
            Intent it = new Intent();
            mActivity.setResult(Activity.RESULT_OK, it);
            mActivity.finish();
        }
    }
}
