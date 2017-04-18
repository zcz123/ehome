package cc.wulian.smarthomev5.adapter.uei;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

/**
 * 机顶盒各选项卡对应的Fragment
 * Created by yuxiaoxuan on 2016/10/21.
 */

public class TopBoxTabAdapter extends FragmentPagerAdapter {
    private List<WulianFragment> fragments;
    public TopBoxTabAdapter(FragmentManager fm, List<WulianFragment> frags){
        super(fm);
        this.fragments=frags;
    }
    @Override
    public Fragment getItem(int i) {
        if(this.fragments==null){
            return null;
        }else {
            return this.fragments.get(i);
        }
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
