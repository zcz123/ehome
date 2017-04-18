package cc.wulian.smarthomev5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MenuList;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.StateDrawableFactory;
import cc.wulian.smarthomev5.utils.CmdUtil;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class HouseKeeperActionSceneAdapter extends WLBaseAdapter<SceneInfoAdapter.SceneEntity> {
    public View state;
    public BaseActivity activity;
    private int clickTemp = -1;
    public TimingSceneGroupEntity timingSceneGroup = TimingSceneManager
            .getInstance().getDefaultGroup();
    private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
            .getInstance();
    protected AccountManager mAccountManger = AccountManager.getAccountManger();
    public Preference preference = Preference.getPreferences();
    public HouseKeeperActionSceneAdapter(BaseActivity context) {
        super(context, new ArrayList<SceneInfoAdapter.SceneEntity>());
        this.activity = context;
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater,
                           ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.item_scene, parent, false);
    }

    @Override
    protected void bindView(Context context, View view, int pos,
                            SceneInfoAdapter.SceneEntity item) {
        ImageButton mTimingSceneBt;

        ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
        TextView nameText = (TextView) view.findViewById(R.id.name);
        FrameLayout background = (FrameLayout) view.findViewById(R.id.linearLayout_state);

        Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(context,
                item.getIcon());
        Drawable checkedIcon = SceneManager.getSceneIconDrawable_Bright(context,
                item.getIcon());


        if (clickTemp == pos) {
            iconImage.setImageDrawable(checkedIcon);
            background.setBackgroundResource(R.drawable.scene_state_using);
        } else {
            iconImage.setImageDrawable(normalIcon);
            background.setBackgroundResource(R.drawable.scene_state_unuse);
        }

        nameText.setText(item.getName());


        mTimingSceneBt = (ImageButton) view
                .findViewById(R.id.scene_timing_delbt);
        // mTimingSceneTv = (TextView) view.findViewById(R.id.scene_timing_tv);
        // mTimingSceneDe = (TextView)
        // view.findViewById(R.id.scene_timing_delete);

        String sceneID = item.getSceneID();


        boolean houseHasUpgrade = preference.getBoolean(
                IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
        if(!houseHasUpgrade){
            if (timingSceneGroup.contains(sceneID)) {
                item.setShowClock(true);
            } else {
                item.setShowClock(false);
            }
        }else{
            String programType = getSceneTimingTask(sceneID).getProgramType();
            if(!StringUtil.isNullOrEmpty(programType) && StringUtil.equals(programType, "1")){
                item.setShowClock(true);
            }else{
                item.setShowClock(false);
            }
        }

        if (item.isShowClock) {
            mTimingSceneBt.setVisibility(View.VISIBLE);
        } else {
            mTimingSceneBt.setVisibility(View.INVISIBLE);
        }
    }

    public void addAllData(List<SceneInfo> newData) {
        getData().clear();
        if (newData == null) {
            notifyDataSetChanged();
            return;
        }
        for (SceneInfo info : newData) {
            SceneInfoAdapter.SceneEntity entity = new SceneInfoAdapter.SceneEntity();
            entity.setGwID(info.getGwID());
            entity.setGroupID(info.getGroupID());
            entity.setSceneID(info.getSceneID());
            entity.setName(info.getName());
            entity.setGroupName(info.getGroupName());
            entity.setStatus(info.getStatus());
            entity.setIcon(info.getIcon());
            entity.setShowClickOne(false);
            entity.setShowClickLong(false);
            entity.setShowClock(false);
            getData().add(entity);
        }
        notifyDataSetChanged();
    }

    public AutoProgramTaskInfo getSceneTimingTask(String sceneID) {
        AutoProgramTaskInfo info = autoProgramTaskManager.getAutoProgramTypeTime(sceneID);
        if(info == null)
            info = new AutoProgramTaskInfo();
        return info;
    }

    public void setSeclection(int position) {
        clickTemp = position;
    }
}