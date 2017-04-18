package cc.wulian.smarthomev5.fragment.setting.flower.items;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.FlowerTimeShowActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FlowerTimeShowItem extends AbstractSettingItem
{
  public FlowerTimeShowItem(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.name = paramContext.getResources().getString(paramInt);
  }

  public void doSomethingAboutSystem()
  {
    startBroadcastVoiceActivity();
  }

  public void initSystemState()
  {
    super.initSystemState();
    this.iconImageView.setVisibility(View.GONE);
    this.infoImageView.setVisibility(View.VISIBLE);
    this.infoImageView.setImageResource(R.drawable.voice_remind_right);
    this.infoImageView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        startBroadcastVoiceActivity();
      }
    });
  }
  
  private void startBroadcastVoiceActivity()
  {
    Intent localIntent = new Intent();
    localIntent.setClass(this.mContext, FlowerTimeShowActivity.class);
    this.mContext.startActivity(localIntent);
  }
  
}

