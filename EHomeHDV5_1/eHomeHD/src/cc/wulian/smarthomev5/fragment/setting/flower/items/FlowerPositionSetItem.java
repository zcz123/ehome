package cc.wulian.smarthomev5.fragment.setting.flower.items;


import android.content.Context;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

public class FlowerPositionSetItem extends AbstractSettingItem
{
	
  public FlowerPositionSetItem(Context context, int paramInt)
  {
    super(context);
    this.name = context.getResources().getString(paramInt);
  }

  public void doSomethingAboutSystem()
  {
	  IntentUtil.startHtml5PlusActivity(mContext, URLConstants.LOCAL_BASEURL+"setstation.html");
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
    	  IntentUtil.startHtml5PlusActivity(mContext, URLConstants.LOCAL_BASEURL+"setstation.html");
      }
    });
  }
  public void setInfoText(String txt){
	  this.infoTextView.setVisibility(View.VISIBLE);
	  infoTextView.setPadding(0, 0, 32, 0);
	  infoTextView.setText(txt);
  }
  
  public String getInfoText(){
	  return infoTextView.getText().toString();
  }
  
}
