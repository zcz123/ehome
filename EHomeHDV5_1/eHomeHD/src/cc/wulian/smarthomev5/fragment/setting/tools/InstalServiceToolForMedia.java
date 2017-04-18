package cc.wulian.smarthomev5.fragment.setting.tools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;

import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

/**
 * Created by yuxiaoxuan on 17/1/11.
 */

public class InstalServiceToolForMedia extends WulianFragment implements H5PlusWebViewContainer {
    private static String webPage_mideaConfig="file:///android_asset/oz_centralairconditioning/mideaCofiguration.html";
    private String pluginName="CentralAir_OZ.zip";
    private H5PlusWebView webView;
    private TextView textView;
    private View rootView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(
                R.layout.setting_instalservicetool_media, container, false);
        ViewUtils.inject(this, rootView);
        initBar();
        return rootView;
    }
    private void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIconText(
                mApplication.getResources().getString(
                        R.string.about_back));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_webview);
        textView= (TextView) view.findViewById(R.id.search_tv);
        textView.setVisibility(View.GONE);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer) this, webView);
        webView.loadUrl(webPage_mideaConfig);
        getSupportActionBar().hide();
    }

    @Override
    public void addH5PlusWebView(H5PlusWebView webview) {

    }

    @Override
    public void destroyContainer() {
        Engine.destroyPager(this);
        this.getActivity().finish();
    }

    @Override
    public ViewGroup getContainerRootView() {
        return (ViewGroup) rootView;
    }
}
