package cc.wulian.smarthomev5.fragment.setting.flower.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DateUtil;

public class HorizontalWeekDayView extends FrameLayout implements CompoundButton.OnCheckedChangeListener
{
  
  private ToggleButton allBtn;
  private List<ToggleButton> btnList = new ArrayList<ToggleButton>();

  private int[] butId = {
		  R.id.toggle_sun, 
		  R.id.toggle_mon, 
		  R.id.toggle_tur, 
		  R.id.toggle_thre, 
		  R.id.toggle_thu, 
		  R.id.toggle_fir, 
		  R.id.toggle_sat };

  public HorizontalWeekDayView(Context paramContext)
  {
    this(paramContext, null);
  }

  public HorizontalWeekDayView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public HorizontalWeekDayView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    LayoutInflater.from(paramContext).inflate(R.layout.horizontal_weekday, this, true);
    initWidget();
    if (isInEditMode());
  }

  private void initWidget()
  {
    this.allBtn = ((ToggleButton)findViewById(R.id.toggle_all));
    this.allBtn.setOnCheckedChangeListener(this);
    for (int i = 0;i<butId.length ; i++)
    {
      ToggleButton toggleButton = (ToggleButton)findViewById(this.butId[i]);
      btnList.add(toggleButton);
    }
  }

  //获取选中的日期  结果   
  public String getWeekday(){
	  StringBuffer buff=new StringBuffer();
	  for(int i=0;i<btnList.size();i++){
		  ToggleButton btn=btnList.get(i);
		  if(btn.isChecked()){
			  buff.append("1,");
		  }else{
			  buff.append("0,");
		  }
	  }
	  String result=buff.substring(0,buff.length()-1);
	  return result;
  }
  //设置选中日起  weekday like 7F ;
  public void setWeekday(String weekday){
	  char charArry[]=DateUtil.changeWeekOrder(DateUtil.Hexconvert2LocalWeekday(weekday)).replace(",","").toCharArray();
	  int count=0;
	  for(int i=0;i<charArry.length;i++){	
		  ToggleButton btn=btnList.get(i);
		  if(charArry[i]=='1'){
			  count++;
			  btn.setChecked(true);
		  }else{
			  btn.setChecked(false);
		  }
	  }
	  if(count==btnList.size()){
		  this.allBtn.setChecked(true);
	  }
  }
  
  private void setTogBtnsState(boolean paramBoolean)
  {
    for(ToggleButton btn:btnList){
    	btn.setChecked(paramBoolean);
    }
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    switch (paramCompoundButton.getId())
    {
    case R.id.toggle_all:
    	setTogBtnsState(paramBoolean);
    }    
  }
  
}