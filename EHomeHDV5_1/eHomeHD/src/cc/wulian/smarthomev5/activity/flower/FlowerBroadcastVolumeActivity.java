package cc.wulian.smarthomev5.activity.flower;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.setting.flower.FlowerBroadcastVolumeSetFragment;

public class FlowerBroadcastVolumeActivity extends EventBusActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getSupportFragmentManager().beginTransaction().add(android.R.id.content, new FlowerBroadcastVolumeSetFragment()).commit();
  }

@Override
public boolean fingerRightFromCenter() {
	return false;
}
  
  
}