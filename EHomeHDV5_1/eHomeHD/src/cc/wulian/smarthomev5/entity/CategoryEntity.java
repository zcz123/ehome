package cc.wulian.smarthomev5.entity;

import cc.wulian.app.model.device.category.Category;

public class CategoryEntity
{
	private Category mCategory;
	private String name;
	public CategoryEntity(String name,Category category){
		this.name = name;
		this.mCategory = category;
	}
	public Category getmCategory() {
		return mCategory;
	}
	public void setmCategory(Category mCategory) {
		this.mCategory = mCategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
