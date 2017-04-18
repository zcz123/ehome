package cc.wulian.smarthomev5.activity.flower;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.setting.flower.FlowerVoiceControlFragment;

public class FlowerVoiceControlActivity extends EventBusActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getSupportFragmentManager().beginTransaction().add(android.R.id.content, new FlowerVoiceControlFragment()).commit();
  }
  @Override
  public boolean fingerRightFromCenter() {
  	return false;
  }
}