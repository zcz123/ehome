package cc.wulian.smarthomev5.fragment.setting.voice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.fragment.setting.tools.InstalServiceToolItemForMedia;

/**
 * Created by yuxiaoxuan on 2017/1/3.
 */

public class InstalServiceToolFragment  extends WulianFragment{
    private ListView settingManagerListView;
    private SettingManagerAdapter settingManagerAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBar();
        settingManagerAdapter = new SettingManagerAdapter(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_content, container,
                false);
        ViewUtils.inject(this, rootView);
        settingManagerListView= (ListView) rootView.findViewById(R.id.setting_manager_lv);
        return rootView;
    }
    private void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设备调试");
        getSupportActionBar().setIconText(
                mApplication.getResources().getString(
                        R.string.about_back));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingManagerListView.setAdapter(settingManagerAdapter);
    }

    @Override
    public void onShow() {
        super.onShow();
        initBar();
        initSettingItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        initSettingItems();
    }
    private void initSettingItems() {
        List<AbstractSettingItem> items = new ArrayList<AbstractSettingItem>();
        EmptyItem emptyItem1 = new EmptyItem(mActivity);
        emptyItem1.initSystemState();
        items.add(emptyItem1);
        InstalServiceToolItemForMedia toolItemForMedia=new InstalServiceToolItemForMedia(mActivity);
        toolItemForMedia.initSystemState();
        items.add(toolItemForMedia);
        settingManagerAdapter.swapData(items);
    }
}
