package cc.wulian.smarthomev5.fragment.more.littlewhite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.wifidirect.utils.DirectUtils;
import com.wulian.icam.wifidirect.utils.WiFiLinker;

import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.CreateQRCodeActivity;
import cc.wulian.smarthomev5.activity.ScanQRCodeActivity;
import cc.wulian.smarthomev5.event.SigninEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.Preference;

import static android.content.Context.MODE_WORLD_READABLE;

/**
 * @function: 小白wifi配置页
 * Created by Administrator on 2016/11/15.
 */

public class LittleWhiteWifiConfigFragment extends WulianFragment implements View.OnClickListener {
    private TextView tv_wifi_name;
    private ImageView iv_change_wifi;
    private TextView tv_tips;
    private RelativeLayout rl_wifi_pwd_input;
    private EditText et_wifi_pwd;
    private CheckBox cb_wifi_pwd_show;
    private CheckBox no_wifi_pwd_checkbox;
    private LinearLayout ll_no_wifi_pwd;
    private ListView lv_wifi_info;
    private Button btn_next_step;

    private WiFiLinker mWiFiLinker;
    private String originSecurity;
    private String originSSid;
    private String originPwd;
    private String wifiPwd;
    private String wifiSsId;
    private String gwId;
    private String gwPwd;
    private static final String company = "wulian";
    private CreateQRCodeFragment createQRCodeFragment = new CreateQRCodeFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.little_white_wifi_config,
                container, false);
        ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
        setListener();
        initBar();
    }

    private void initViews() {
        tv_wifi_name = (TextView) getView().findViewById(R.id.tv_wifi_name);
        tv_tips = (TextView) getView().findViewById(R.id.tv_tips);
        iv_change_wifi = (ImageView) getView().findViewById(R.id.iv_change_wifi);
        rl_wifi_pwd_input = (RelativeLayout) getView().findViewById(R.id.rl_wifi_pwd_input);
        et_wifi_pwd = (EditText) getView().findViewById(R.id.et_wifi_pwd);
        cb_wifi_pwd_show = (CheckBox) getView().findViewById(R.id.cb_wifi_pwd_show);
        no_wifi_pwd_checkbox = (CheckBox) getView().findViewById(R.id.no_wifi_pwd_checkbox);
        ll_no_wifi_pwd = (LinearLayout) getView().findViewById(R.id.ll_no_wifi_pwd);
        lv_wifi_info = (ListView) getView().findViewById(R.id.lv_wifi_list);
        btn_next_step = (Button) getView().findViewById(R.id.btn_next_step);

    }

    private void initData() {
        /** 网络操作初始化 */
        getCurrentWifiInfo();
    }

    private void setListener() {
        tv_wifi_name.setOnClickListener(this);
        tv_tips.setOnClickListener(this);
        iv_change_wifi.setOnClickListener(this);
        rl_wifi_pwd_input.setOnClickListener(this);
        et_wifi_pwd.setOnClickListener(this);
        cb_wifi_pwd_show.setOnClickListener(this);
        ll_no_wifi_pwd.setOnClickListener(this);
        btn_next_step.setOnClickListener(this);
        no_wifi_pwd_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rl_wifi_pwd_input.setVisibility(View.GONE);
                } else {
                    rl_wifi_pwd_input.setVisibility(View.VISIBLE);
                }
            }
        });
        cb_wifi_pwd_show
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            et_wifi_pwd
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            et_wifi_pwd.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    }
                });

    }

    private void initBar() {
        // this.mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(false);
        getSupportActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(
                getResources().getString(R.string.smartLock_wifi_setting));
        getSupportActionBar().setLeftIconClickListener(
                new ActionBarCompat.OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }
                });
    }

    private void getCurrentWifiInfo() {
        mWiFiLinker = new WiFiLinker();
        mWiFiLinker.WifiInit(getActivity());

        if (mWiFiLinker.isWiFiEnable()) {
            WifiInfo info = mWiFiLinker.getWifiInfo();
            String currentSsid = "";
            if (info != null) {
                String ssid = info.getSSID();
                if (!TextUtils.isEmpty(ssid) && !info.getHiddenSSID()
                        && !"<unknown ssid>".equals(ssid)) {
                    currentSsid = ssid.replace("\"", "");
                } else {
                    CustomToast.show(getActivity(), com.wulian.icam.R.string.config_confirm_wifi_hidden, 1000);
                    getActivity().finish();
                    return;
                }
            } else {
                CustomToast.show(getActivity(), com.wulian.icam.R.string.config_open_wifi, 1000);
                getActivity().finish();
                return;
            }

            ScanResult result = null;
            List<ScanResult> scanList = mWiFiLinker.WifiGetScanResults();
            if (scanList == null || scanList.size() == 0) {
                CustomToast.show(getActivity(), com.wulian.icam.R.string.config_no_wifi_scan_result, 1000);
                getActivity().finish();
                return;
            }
            for (ScanResult item : scanList) {
                if (item.SSID.equalsIgnoreCase(currentSsid)) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                CustomToast.show(getActivity(), com.wulian.icam.R.string.config_open_wifi, 1000);
                getActivity().finish();
                return;
            }
            if (DirectUtils.isAdHoc(result.capabilities)) {
                CustomToast.show(getActivity(), com.wulian.icam.R.string.config_adhoc_is_not_suppored, 1000);
                getActivity().finish();
                return;
            }
            if (DirectUtils.isOpenNetwork(result.capabilities)) {
                et_wifi_pwd.setVisibility(View.GONE);
            }
            originSecurity = DirectUtils
                    .getStringSecurityByCap(result.capabilities);
            String localMac = info.getMacAddress();
            if (TextUtils.isEmpty(localMac)) {
                CustomToast.show(getActivity(), com.wulian.icam.R.string.config_wifi_not_allocate_ip, 1000);
                getActivity().finish();
                return;
            }

            originSSid = currentSsid;
            tv_wifi_name.setText(currentSsid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentWifiInfo();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_change_wifi) {
            this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else if (id == R.id.btn_next_step) {
            jumpToCreateBarcode();
        }
    }

    private void jumpToCreateBarcode() {//以下是二维码中需要携带的信息
        gwId = mAccountManger.getmCurrentInfo().getGwID();
        gwPwd = mAccountManger.getmCurrentInfo().getGwPwd();
//        String gwpwd;//未加密的网关pwd
//        gwpwd = Preference.getPreferences().getLittlewhiteGwpwd();
        wifiSsId = tv_wifi_name.getText().toString().trim();
        wifiPwd = et_wifi_pwd.getText().toString().trim();
        JSONObject json = new JSONObject();
        json.put("company", "wulian");
        json.put("ssid", wifiSsId);
        json.put("psw", wifiPwd);
        JSONObject js2 = new JSONObject();
        js2.put("username", gwId);
        js2.put("password", gwPwd);
        json.put("data", js2);
        Intent it = new Intent();
        it.putExtra("json", json.toJSONString());
        System.out.println("------>js" + json.toString());
        if (StringUtil.isNullOrEmpty(wifiPwd) && !no_wifi_pwd_checkbox.isChecked()) {
            CustomToast.show(getActivity(), getResources().getString(R.string.set_password_not_null_hint), 1000);
        } else {
            it.setClass(getActivity(), CreateQRCodeActivity.class);
            startActivity(it);
        }
    }
}
