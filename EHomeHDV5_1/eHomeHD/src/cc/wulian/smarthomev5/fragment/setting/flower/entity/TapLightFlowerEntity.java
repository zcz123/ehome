package cc.wulian.smarthomev5.fragment.setting.flower.entity;

public class TapLightFlowerEntity
{
  private String content;
  
  private String tag;
  
  private boolean isCheck = false;

  public boolean getCheck()
  {
    return this.isCheck;
  }

  public String getTag() {
		return tag;
  }
	
  public void setTag(String tag) {
		this.tag = tag;
  }

  public String getContent()
  {
    return this.content;
  }

  public void setCheck(boolean paramBoolean)
  {
    this.isCheck = paramBoolean;
  }

  public void setContent(String paramString)
  {
    this.content = paramString;
  }
}