package cc.wulian.smarthomev5.fragment.more.littlewhite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.LittleWihteWifiConfigActivity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;

/**
 * @Function:小白第三方登录 Created by hxc on 2016/11/15.
 */

public class LittleWhiteFragment extends WulianFragment {

    private RelativeLayout rl_little_white;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBar();
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
                getResources().getString(R.string.gateway_explore_third_party_login));
        getSupportActionBar().setLeftIconClickListener(
                new ActionBarCompat.OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_little_white,
                container, false);
        ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setListener();
    }

    private void initView() {
        rl_little_white = (RelativeLayout) getView().findViewById(R.id.rl_little_white);

    }

    private void setListener() {
        rl_little_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(getActivity(), LittleWihteWifiConfigActivity.class);
                startActivity(it);
            }
        });
    }
}
