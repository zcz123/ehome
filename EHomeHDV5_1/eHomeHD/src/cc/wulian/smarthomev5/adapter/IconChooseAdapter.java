package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.IconResourceEntity;

public class IconChooseAdapter extends WLBaseAdapter<IconResourceEntity> {
	private int selectIconKey;
	private boolean selectedChangedBackgroundColor = true;
	private boolean selectedChangedImageDrawable = false;

	public void setSelectedChangedBackgroundColor(
			boolean selectedChangedBackgroundColor) {
		this.selectedChangedBackgroundColor = selectedChangedBackgroundColor;
	}

	public void setSelectedChangedImageDrawable(
			boolean selectedChangedImageDrawable) {
		this.selectedChangedImageDrawable = selectedChangedImageDrawable;
	}

	public IconChooseAdapter(Context context, List<IconResourceEntity> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.scene_add_pop_item, null);
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			IconResourceEntity item) {
		ImageView iconView = (ImageView) view.findViewById(R.id.addScene);
		Drawable icon = mResources.getDrawable(item.iconRes);
		if (this.selectedChangedBackgroundColor) {
			iconView.setImageDrawable(icon);
			if (item.iconkey == selectIconKey) {
				view.setBackgroundColor(mResources
						.getColor(R.color.transparent));// 以前选中为white，修改后只改变图片状态不改变背景色。
			} else {
				view.setBackgroundColor(mResources
						.getColor(R.color.transparent));
			}
		}
		if (this.selectedChangedImageDrawable) {
			if (item.iconkey == selectIconKey && item.iconSelectedRes > 0) {// 选中区域背景图片
				Drawable iconselected = mResources
						.getDrawable(item.iconSelectedRes);
				iconView.setImageDrawable(iconselected);
			} else {
				iconView.setImageDrawable(icon);
			}
		}
	}

	public void setSelectIconKey(int selectIconKey) {
		this.selectIconKey = selectIconKey;
		notifyDataSetChanged();
	}

	public IconResourceEntity getSelectIconEntity() {
		if (getData() == null)
			return null;
		for (IconResourceEntity e : getData()) {
			if (e.iconkey == selectIconKey)
				return e;
		}
		return null;
	}

}
