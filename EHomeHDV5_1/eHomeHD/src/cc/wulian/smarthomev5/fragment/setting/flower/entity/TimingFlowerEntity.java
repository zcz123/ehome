package cc.wulian.smarthomev5.fragment.setting.flower.entity;

import android.text.TextUtils;

public class TimingFlowerEntity
  implements Cloneable
{
  private String time = "";
  private String weekDay = "";

  protected Object clone()
  {
    TimingFlowerEntity localTimingFlowerEntity = new TimingFlowerEntity();
    localTimingFlowerEntity.setTime(this.time);
    localTimingFlowerEntity.setWeekDay(this.weekDay);
    return localTimingFlowerEntity;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof TimingFlowerEntity))
    {
      TimingFlowerEntity localTimingFlowerEntity = (TimingFlowerEntity)paramObject;
      return (TextUtils.equals(this.time, localTimingFlowerEntity.time)) && (TextUtils.equals(this.weekDay, localTimingFlowerEntity.weekDay));
    }
    return super.equals(paramObject);
  }

  public String getTime()
  {
    return this.time;
  }

  public String getWeekDay()
  {
    return this.weekDay;
  }

  public void setTime(String paramString)
  {
    this.time = paramString;
  }

  public void setWeekDay(String paramString)
  {
    this.weekDay = paramString;
  }
}

/* Location:           C:\Users\Administrator.PC-20150711YNMS\Desktop\反编译\classes_dex2jar.jar
 * Qualified Name:     cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity
 * JD-Core Version:    0.6.0
 */