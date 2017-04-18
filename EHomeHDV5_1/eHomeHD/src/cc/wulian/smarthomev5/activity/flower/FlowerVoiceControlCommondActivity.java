package cc.wulian.smarthomev5.activity.flower;

import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.setting.flower.FlowerVoiceControlCommondFragment;

public class FlowerVoiceControlCommondActivity extends EventBusActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    FlowerVoiceControlCommondFragment fragment =  new FlowerVoiceControlCommondFragment();
	fragment.setArguments(getIntent().getExtras());
	getSupportFragmentManager().beginTransaction().add(android.R.id.content,fragment).commit();
  }
  @Override
  public boolean fingerRightFromCenter() {
  	return false;
  }
}