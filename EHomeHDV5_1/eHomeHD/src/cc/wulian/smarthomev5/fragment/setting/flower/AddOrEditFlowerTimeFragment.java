package cc.wulian.smarthomev5.fragment.setting.flower;

import java.util.List;
import java.util.TimeZone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.scene.TimingSceneView;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DateUtil;

public class AddOrEditFlowerTimeFragment extends WulianFragment
{
  public static final String FLOWER_TIME_SERIAL = "flower_time_broadcast_serial";
  private static final String SHOW_DIALOG_KEY = "add_edit_flowerTime_show_dialog_key";
  private int position;
  private TimingSceneView timingFlowerView;
  private TimingSceneEntity timingScene = new TimingSceneEntity();
  private FlowerManager timingSceneGroup = FlowerManager.getInstance();
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);
    Bundle localBundle = getArguments();
    if (localBundle != null)position = localBundle.getInt(FLOWER_TIME_SERIAL);
    initBar();
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.timingFlowerView = new TimingSceneView(mActivity);
    if (this.position >= 0)
    {
    	TimingFlowerEntity entity=timingSceneGroup.getFlowerTimingEntities(FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME).get(this.position);
        this.timingScene.setTime(DateUtil.parseTime(entity.getTime()));
        this.timingScene.setWeekDay(DateUtil.changeWeekOrder(DateUtil.Hexconvert2LocalWeekday(entity.getWeekDay())));
        this.timingFlowerView.setmTimingScene(this.timingScene);
    }else{
        this.timingScene = new TimingSceneEntity();
        this.timingFlowerView.setmTimingScene(this.timingScene);
    }
    return this.timingFlowerView;
  }
  
  private void initBar()
  {
    this.mActivity.resetActionMenu();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayIconEnabled(false);
    getSupportActionBar().setDisplayIconTextEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setDisplayShowMenuEnabled(false);
    getSupportActionBar().setDisplayShowMenuTextEnabled(true);
    getSupportActionBar().setIconText(mApplication.getResources().getString(R.string.cancel));
    getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.gateway_dream_flower_timing_broadcast));
    getSupportActionBar().setRightIconText(mApplication.getResources().getString(R.string.set_save));
    getSupportActionBar().setRightMenuClickListener(new ActionBarCompat.OnRightMenuClickListener()
    {
      public void onClick(View paramView)
      {
        TimingSceneEntity entity = timingFlowerView.getTimingScene();
        List<TimingFlowerEntity> newList = timingSceneGroup.getNewTimingEntities(FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME);
        if (position >= 0)
        {
          TimingFlowerEntity tempEntity = newList.get(position);
          tempEntity.setTime(entity.getTime().replace(":", "").substring(0,4));
          tempEntity.setWeekDay(DateUtil.BinaryToHex(DateUtil.changeWeekOrder(entity.getWeekDay())));
          newList.set(position, tempEntity);          
        }else{
          TimingFlowerEntity tempEntity = new TimingFlowerEntity();
          tempEntity.setTime(entity.getTime().replace(":", "").substring(0,4));
          tempEntity.setWeekDay(DateUtil.BinaryToHex(DateUtil.changeWeekOrder(entity.getWeekDay())));
          newList.add(tempEntity);
        }
        mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
        JsonTool.SetFlowerShowTiming(CmdUtil.FLOWER_TIMING_BROADCAST, newList);
      }
    });
  }

  public void onEventMainThread(FlowerEvent event)
  {
    this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
    if(FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME.equals(event.getAction())){
    	this.mActivity.finish();
    }  
  }
}
