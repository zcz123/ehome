package cc.wulian.smarthomev5.activity.flower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.setting.flower.AddOrEditFlowerShowTimeFragment;

public class AddOrEditFlowerTimeShowActivity extends EventBusActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    AddOrEditFlowerShowTimeFragment localAddOrEditFlowerShowTimeFragment = new AddOrEditFlowerShowTimeFragment();
    localAddOrEditFlowerShowTimeFragment.setArguments(getIntent().getExtras());
    getSupportFragmentManager().beginTransaction().replace(android.R.id.content, localAddOrEditFlowerShowTimeFragment).commit();
  }
  @Override
  public boolean fingerRightFromCenter() {
  	return false;
  }
}