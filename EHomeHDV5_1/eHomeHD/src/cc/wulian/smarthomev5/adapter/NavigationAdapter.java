package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.NavigationEntity;
import cc.wulian.smarthomev5.fragment.device.DeviceCommonFragment;
import cc.wulian.smarthomev5.fragment.home.HomeFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperManagerFragment;
import cc.wulian.smarthomev5.fragment.more.MoreManagerFragment;
import cc.wulian.smarthomev5.fragment.scene.SceneFragment;
import cc.wulian.smarthomev5.fragment.setting.SettingManagerFragment;

public class NavigationAdapter extends WLBaseAdapter<NavigationEntity> {

	public NavigationAdapter(Context context) {
		super(context, new ArrayList<NavigationEntity>());
		initMenus();
	}
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.layout_nav_item, null);
	}
	@Override
	protected void bindView(Context context, View view, int pos,
			NavigationEntity item) {
		TextView titleTextView = (TextView) view.findViewById(R.id.nav_title);
		ImageView iconImageView = (ImageView) view.findViewById(R.id.nav_icon);
		titleTextView.setText(item.getTitleId());
		iconImageView.setImageResource(item.getIconId());
	}
	private void initMenus() {
        NavigationEntity entityHome = new NavigationEntity();
        entityHome.setClassName(HomeFragment.class.getName());
        entityHome.setIconId(R.drawable.nav_home);
        entityHome.setTitleId(R.string.nav_home_title);
        entityHome.setId(R.id.nav_home);
        
        NavigationEntity entityScene = new NavigationEntity();
        entityScene.setClassName(SceneFragment.class.getName());
        entityScene.setIconId(R.drawable.nav_scene);
        entityScene.setTitleId(R.string.nav_scene_title);
        entityScene.setId(R.id.nav_scene);
        
        NavigationEntity entityDevice = new NavigationEntity();
        entityDevice.setClassName(DeviceCommonFragment.class.getName());
        entityDevice.setIconId(R.drawable.nav_device);
        entityDevice.setTitleId(R.string.nav_device_title);
        entityDevice.setId(R.id.nav_device);
        
        NavigationEntity entityTask = new NavigationEntity();
        entityTask.setClassName(HouseKeeperManagerFragment.class.getName());
        entityTask.setIconId(R.drawable.nav_config);
        entityTask.setTitleId(R.string.nav_house_title);
        entityTask.setId(R.id.nav_config);
        
        NavigationEntity entityMore = new NavigationEntity();
        entityMore.setClassName(MoreManagerFragment.class.getName());
        entityMore.setIconId(R.drawable.nav_more);
        entityMore.setTitleId(R.string.nav_more);
        entityMore.setId(R.id.nav_more);
        
        NavigationEntity entitySetting = new NavigationEntity();
        entitySetting.setClassName(SettingManagerFragment.class.getName());
        entitySetting.setIconId(R.drawable.nav_setting);
        entitySetting.setTitleId(R.string.set_titel);
        entitySetting.setId(R.id.nav_setting);
        
        getData().add(entityHome);
        getData().add(entityScene);
        getData().add(entityDevice);
        if(mContext.getResources().getBoolean(R.bool.use_house)){
        	 getData().add(entityTask);
        }
        getData().add(entityMore);
        getData().add(entitySetting);
        
	}
	
}
