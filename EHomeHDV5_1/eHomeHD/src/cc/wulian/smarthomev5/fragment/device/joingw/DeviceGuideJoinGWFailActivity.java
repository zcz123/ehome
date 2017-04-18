package cc.wulian.smarthomev5.fragment.device.joingw;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.event.JoinDeviceEvent;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.viewpagerindicator.CirclePageIndicator;

public class DeviceGuideJoinGWFailActivity extends EventBusActivity implements OnClickListener {
    private List<View> guideItemView = new ArrayList<View>();
    private DeviceGuidePagerAdapter pagerAdapter;
    private View contentView;
    private LayoutInflater inflater;
    private Button mNosetDeviceBtn;
    private String gwVer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        contentView = inflater.inflate(R.layout.fragment_guide_device_join_gw,
                null);
        gwVer = AccountManager.getAccountManger().getmCurrentInfo().getGwVer();
        setContentView(contentView);
        initBar();
        initViewPager();

        mNosetDeviceBtn = (Button) contentView.findViewById(R.id.device_join_gw_noset_btn);
        mNosetDeviceBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initBar() {
        resetActionMenu();
        getCompatActionBar().setDisplayHomeAsUpEnabled(true);
        getCompatActionBar().setIconText(
                getResources().getString(R.string.nav_device_title));
        getCompatActionBar().setTitle(
                getResources().getString(R.string.device_common_new_hint));
        //帮助
        getCompatActionBar().setRightIconText(getString(R.string.device_config_edit_dev_help));
    }

    public void initViewPager() {
        ViewPager viewPager = (ViewPager) contentView
                .findViewById(R.id.device_join_gw_viewPager);

        View failView = inflater.inflate(R.layout.fragment_guide_join_gw_fail,
                null);
        View setView = inflater.inflate(R.layout.fragment_guide_join_gw_set,
                null);
        View successView = inflater.inflate(
                R.layout.fragment_guide_join_gw_success, null);
        // 含有颜色标记的字体
        TextView failTextView = (TextView) failView
                .findViewById(R.id.fragment_guide_join_gw_fail_text);
        ImageView ivFailedHint = (ImageView) failView.findViewById(R.id.iv_failed_hint);
        TextView setTextView = (TextView) setView
                .findViewById(R.id.fragment_guide_join_gw_set_text);
        failTextView.setText(Html.fromHtml(getResources().getString(
                R.string.device_guide_join_gw_fail_hint)));
        setTextView.setText(Html.fromHtml(getResources().getString(
                R.string.device_guide_join_gw_set_hint)));

        setGwVer(ivFailedHint);

        guideItemView.add(failView);
        guideItemView.add(setView);
        guideItemView.add(successView);

        pagerAdapter = new DeviceGuidePagerAdapter(guideItemView);
        viewPager.setAdapter(pagerAdapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) contentView
                .findViewById(R.id.device_join_gw_dot);
        circlePageIndicator.setViewPager(viewPager);
    }

    private void setGwVer(ImageView ivFailedHint) {
        String[] gwVers = gwVer.split("\\.");
        if(gwVers!=null&&gwVers.length>1){
            switch (gwVers[1]) {
                case "1"://竖型[有路由]
                    ivFailedHint.setImageResource(R.drawable.erect_join_gw_key_fail);
                    break;
                case "2"://云家[有路由]
                    ivFailedHint.setImageResource(R.drawable.ceiling_join_gw_key_fail);
                    break;
                case "3"://普通5350  方形网关
                    ivFailedHint.setImageResource(R.drawable.wulian02_join_gw_key_fail);
                    break;
                case "4": //ARM网关
                    ivFailedHint.setImageResource(R.drawable.mini_join_gw_key_fail);//ARM网关还不清楚是什么网关
                    break;
                case "5"://摄像头网关
                    ivFailedHint.setImageResource(R.drawable.look_join_gw_key_fail);
                    break;
                case "6"://梦想之花：6[有路由有硬盘]
                case "7"://梦想之花：7[有路由无硬盘]
                    ivFailedHint.setImageResource(R.drawable.dream_flower_join_gw_key_fail);
                    break;
                case "8"://MINI网关：8[有路由]
                    ivFailedHint.setImageResource(R.drawable.mini_join_gw_key_fail);
                    break;
                case "9"://桌面型摄像机：9[带传感器]
                    ivFailedHint.setImageResource(R.drawable.small_join_gw_key_fail);
                    break;
            }
        }else{
            ivFailedHint.setImageResource(R.drawable.erect_join_gw_key_fail);
        }
    }

    public void jumpToConfigDeviceActivity(String gwID, String devID) {
        Intent intent = new Intent();
        intent.setClass(DeviceGuideJoinGWFailActivity.this,
                DeviceConfigJoinGWActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean fingerRightFromCenter() {
        return false;
    }

    public boolean fingerLeft() {
        return false;
    }

    public void onEventMainThread(JoinDeviceEvent event) {
        jumpToConfigDeviceActivity(event.mGwID, event.mDevID);
    }

    @Override
    public void onClick(View arg0) {
        Intent intent = new Intent(this, DeviceGuideAddNoSetDeviceListActivity.class);
        startActivity(intent);
    }

}
