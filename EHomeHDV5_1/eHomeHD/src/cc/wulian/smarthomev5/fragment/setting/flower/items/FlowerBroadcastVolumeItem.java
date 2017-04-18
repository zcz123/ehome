package cc.wulian.smarthomev5.fragment.setting.flower.items;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.FlowerBroadcastVolumeActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FlowerBroadcastVolumeItem extends AbstractSettingItem
{
  public FlowerBroadcastVolumeItem(Context context, int paramInt)
  {
    super(context);
    this.name = context.getResources().getString(paramInt);
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
    Intent intent = new Intent();
    intent.setClass(this.mContext, FlowerBroadcastVolumeActivity.class);
    this.mContext.startActivity(intent);
  }
  
}