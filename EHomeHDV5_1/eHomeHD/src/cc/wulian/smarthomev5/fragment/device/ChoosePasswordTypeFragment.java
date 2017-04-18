package cc.wulian.smarthomev5.fragment.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.OncePasswordActivity;
import cc.wulian.smarthomev5.activity.TempPasswordActivity;
import cc.wulian.smarthomev5.activity.NoClockCommonPwActivity;
import cc.wulian.smarthomev5.activity.NoClockOncePwActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ChoosePasswordTypeFragment extends WulianFragment implements
        android.view.View.OnClickListener {
    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";
    private String gwID;
    private String devID;
    private static DeviceCache deviceCache;
    private static final String OW_DOOR_LOCK_Time_Syn = "OW_DOOR_LOCK_Time_Syn";
    @ViewInject(R.id.time_synchronization)
    private ToggleButton time_synchronization;
    @ViewInject(R.id.common_password)
    private LinearLayout commomPassword;
    @ViewInject(R.id.once_password)
    private LinearLayout oncePassword;
    @ViewInject(R.id.temp_password)
    private LinearLayout tempPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEditDevice();
        initBar();
    }

    private void initEditDevice() {
        gwID = getActivity().getIntent().getStringExtra("gwid");
        devID = getActivity().getIntent().getStringExtra("deviceid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.choose_password_type,
                container, false);
        ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        tempPassword.setOnClickListener(this);
        commomPassword.setOnClickListener(this);
        oncePassword.setOnClickListener(this);
    }

    private void initBar() {
        this.mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(false);
        getSupportActionBar().setIconText(R.string.device_ir_back);
        getSupportActionBar().setTitle(R.string.device_uei_select_type);
        getSupportActionBar().setLeftIconClickListener(
                new OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mActivity.finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == commomPassword) {
            Intent it = new Intent();
            it.setClass(getActivity(), NoClockCommonPwActivity.class);
            it.putExtra(NoClockCommonPwFragment.GWID, gwID);
            it.putExtra(NoClockCommonPwFragment.DEVICEID, devID);
            startActivity(it);
        } else if (v == oncePassword) {
            Intent it2 = new Intent();
            it2.setClass(getActivity(), OncePasswordActivity.class);
            it2.putExtra(OncePasswordFragment.GWID, gwID);
            it2.putExtra(OncePasswordFragment.DEVICEID, devID);
            startActivity(it2);
        } else if (v == tempPassword) {
            Intent it3 = new Intent();
            it3.setClass(getActivity(), TempPasswordActivity.class);
            it3.putExtra(TempPasswordFragment.GWID, gwID);
            it3.putExtra(TempPasswordFragment.DEVICEID, devID);
            startActivity(it3);
        }
    }
}
