package cc.wulian.smarthomev5.fragment.setting.flower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;

public class FlowerBroadcastVolumeSetFragment extends WulianFragment
  implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
  private static final String SHOW_DIALOG_KEY = "Flower_set_volume_dialog_key";
  private SeekBar seekbar;
  private LinearLayout setSublayout;
  private ToggleButton switchTogBtn,subTogBtn1,subTogBtn2,subTogBtn3;
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    initBar();   
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(R.layout.flower_broadcast_volume_set, paramViewGroup, false);
    ViewUtils.inject(this, localView);
    return localView;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    initWidget(paramView);    
  }
  
  public void onResume()
  {
    super.onResume();
    getData();  //加载网络数据   
  }
  
  private void initBar()
  {
    this.mActivity.resetActionMenu();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayIconEnabled(true);
    getSupportActionBar().setDisplayIconTextEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setDisplayShowMenuEnabled(false);
    getSupportActionBar().setDisplayShowMenuTextEnabled(false);
    getSupportActionBar().setIconText(R.string.gateway_dream_flower);
    getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.gateway_dream_flower_broadcast_set));  
  }

  private void initWidget(View view)
  {
    this.seekbar = ((SeekBar)view.findViewById(R.id.volume_seekbar));
    this.seekbar.setOnSeekBarChangeListener(this);
    Button auditionButton = (Button)view.findViewById(R.id.audition_btn);
    ImageButton  minBtn= (ImageButton)view.findViewById(R.id.min_volume_btn);
    ImageButton maxBtn = (ImageButton)view.findViewById(R.id.max_volume_btn);
    auditionButton.setOnClickListener(this);
    minBtn.setOnClickListener(this);
    maxBtn.setOnClickListener(this);
    switchTogBtn=(ToggleButton)view.findViewById(R.id.item_switch_btn);
    subTogBtn1=(ToggleButton)view.findViewById(R.id.set_sub_item1);
    subTogBtn2=(ToggleButton)view.findViewById(R.id.set_sub_item2);
    subTogBtn3=(ToggleButton)view.findViewById(R.id.set_sub_item3);
    
    switchTogBtn.setOnClickListener(this);
    subTogBtn1.setOnClickListener(this);
    subTogBtn2.setOnClickListener(this);
    subTogBtn3.setOnClickListener(this);
    this.setSublayout = ((LinearLayout)view.findViewById(R.id.set_sublayout));
  }
  
  //获取数据  
  private void getData()
  {
	  SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_BROADCAST_SWITCH);
	  SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_BROADCAST_CONVENTIONAL);
	  SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_BROADCAST_NETWORK_PROMPT);
	  SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_BROADCAST_AUXILIARY_CUE);
	  SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_BROADCAST_VOLUME);
  }

  //播报设置
  private void setBroadcast(final String cmd,final String param)
  {
	  SendMessage.sendSetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), cmd, param);
  }
  
  public void onEventMainThread(FlowerEvent event)
  {
    this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0); 	
	if(FlowerEvent.ACTION_FLOWER_BROADCAST_SWITCH.equals(event.getAction())){  
		//播报设置-播报开关
		switchTogBtn.setChecked("1".equals(event.getEventStr()));
		if("1".equals(event.getEventStr())){
			setSublayout.setVisibility(View.VISIBLE);
		}else{
			setSublayout.setVisibility(View.GONE);
		}
		
	}else if(FlowerEvent.ACTION_FLOWER_BROADCAST_CONVENTIONAL.equals(event.getAction())){ 
		//播报设置-常规播报
		subTogBtn1.setChecked("1".equals(event.getEventStr()));
	}else if(FlowerEvent.ACTION_FLOWER_BROADCAST_NETWORK_PROMPT.equals(event.getAction())){ 
		//播报设置-网络提示音
		subTogBtn2.setChecked("1".equals(event.getEventStr()));
	}else if(FlowerEvent.ACTION_FLOWER_BROADCAST_AUXILIARY_CUE.equals(event.getAction())){ 
		//播报设置-辅助提示音
		subTogBtn3.setChecked("1".equals(event.getEventStr()));
	}else if(FlowerEvent.ACTION_FLOWER_BROADCAST_VOLUME.equals(event.getAction())){	
		//播报设置-音量设置
		seekbar.setProgress(StringUtil.toInteger(event.getEventStr()));
	}
     
  }
  
  public void onClick(View view)
  {
	  boolean flg=false;
    switch (view.getId())
    {
    case R.id.audition_btn:
    	SendMessage.sendSetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_IMMEDIATELY_BROADCAST, "");
      break;
    case R.id.min_volume_btn:
    	this.seekbar.setProgress(0);
      setBroadcast(CmdUtil.FLOWER_BROADCAST_VOLUME,String.valueOf(seekbar.getProgress()));
      break;
    case R.id.max_volume_btn:
    	this.seekbar.setProgress(100);
    	setBroadcast(CmdUtil.FLOWER_BROADCAST_VOLUME,String.valueOf(seekbar.getProgress()));
    	break;
    case R.id.item_switch_btn:    	
    	 flg=((ToggleButton)view).isChecked();   
	   	 if (flg){
	   	      this.setSublayout.setVisibility(View.VISIBLE);
	   	      setBroadcast(CmdUtil.FLOWER_BROADCAST_SWITCH,String.valueOf(1));
	   	 }else{
	   	     this.setSublayout.setVisibility(View.GONE);
	   	     setBroadcast(CmdUtil.FLOWER_BROADCAST_SWITCH,String.valueOf(0));
	   	 }
   	 break;
   case R.id.set_sub_item1: 
	    flg=((ToggleButton)view).isChecked();   
	    setBroadcast(CmdUtil.FLOWER_BROADCAST_CONVENTIONAL,(flg ? "1" :"0"));
   	break;
   case R.id.set_sub_item2:
	   flg=((ToggleButton)view).isChecked();   
	   setBroadcast(CmdUtil.FLOWER_BROADCAST_NETWORK_PROMPT,(flg ? "1" :"0"));
   	break;
   case R.id.set_sub_item3:
		flg=((ToggleButton)view).isChecked();   
	   	setBroadcast(CmdUtil.FLOWER_BROADCAST_AUXILIARY_CUE,(flg ? "1" :"0"));
	break;
    }    
  }

  public void onDestroy()
  {
    super.onDestroy();
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {	  
  }

  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
  }

  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
	  setBroadcast(CmdUtil.FLOWER_BROADCAST_VOLUME,String.valueOf(seekbar.getProgress()));
  }
  
}
