package cc.wulian.smarthomev5.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.IconChooseAdapter;
import cc.wulian.smarthomev5.entity.IconResourceEntity;

public class IconChooseView extends LinearLayout
{
	private final IconChooseAdapter adapted;
	private final EditText mInputContent;
	private final CheckedGridView mIconChooseGrid;
	private OnIconClickListener listener;
	
	/**
	 * 选择中时，是否改变背景色
	 * @param selectedChangedBackgroundColor true:改变；false:不改变
	 */
	public void setSelectedChangedBackgroundColor(
			boolean selectedChangedBackgroundColor) {
		adapted.setSelectedChangedBackgroundColor(selectedChangedBackgroundColor);
	}
	/**
	 * 选中时是否改变图片
	 * @param selectedChangedImageDrawable  true:改变；false:不改变
	 */
	public void setSelectedChangedImageDrawable(boolean selectedChangedImageDrawable) {
		adapted.setSelectedChangedImageDrawable(selectedChangedImageDrawable);
	}
//	private boolean msSelect = false;
//	private String mSceneName;
	public IconChooseView( Context context,List<IconResourceEntity> entites)
	{
		super(context);

		this.adapted =new IconChooseAdapter(context, entites);//new IconChooseAdapter(context, null);

		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.common_modify_modul, this);

		mInputContent = (EditText) view.findViewById(R.id.editText_input);
		mInputContent.setSelectAllOnFocus(true);
//		mSceneName = mInputContent.getText().toString().trim();
//		mInputContent.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				msSelect = true;
//			}
//		});
		mIconChooseGrid = (CheckedGridView) view.findViewById(R.id.gridView_choose_icon);
		mIconChooseGrid.setAdapter(adapted);
//		mIconChooseGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
	}

	public void setOnItemClickListener(OnIconClickListener clicklistener){
		this.listener = clicklistener;
		mIconChooseGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				IconResourceEntity entity = adapted.getItem(position);
				setSelectIcon(entity.iconkey);
				if(listener != null){
					listener.onIconClick(entity);
				}
			}
		});
	}
	
	public void setInputTextContent( CharSequence input ){
		mInputContent.setText(input);
	}
	public void setInputHintTextContent( CharSequence input ){
		mInputContent.setHint(input);
	}
	public String getInputHintTextContent(){
		return mInputContent.getHint().toString();
		
	}
	public String getInputTextContent(){
		return mInputContent.getText().toString();
	}

	public void setError( CharSequence error ){
		mInputContent.setError(error);
	}

	public void swapData( List<IconResourceEntity> data ){
		adapted.swapData(data);
	}

	public void setSelectIcon( int iconKey ){
		adapted.setSelectIconKey(iconKey);
	}

	public IconResourceEntity getCheckedItem(){
		return adapted.getSelectIconEntity();
	}
	public interface OnIconClickListener{
		public void onIconClick(IconResourceEntity entity);
	}
}