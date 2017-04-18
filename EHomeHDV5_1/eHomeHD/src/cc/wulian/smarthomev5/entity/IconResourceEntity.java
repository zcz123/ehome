package cc.wulian.smarthomev5.entity;

public class IconResourceEntity
{
	public int iconkey;
	public int iconRes;
	public int iconSelectedRes=-1;//选中状态下的res，默认为-1

	@Override
	public boolean equals( Object o ){
		if (o instanceof IconResourceEntity){
			IconResourceEntity entity = (IconResourceEntity) o;
			return entity.iconkey == iconkey;
		}
		else{
			return super.equals(o);
		}
	}
}
