package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import cc.wulian.app.model.device.R;

public class SingleChooseManager {

	private List<ImageView> imageViews = new ArrayList<ImageView>();
	private long checkId;
	private int selectId;
	private int noselectId;
	public SingleChooseManager(int selectId,int noselectId){
		this.selectId = selectId;
		this.noselectId = noselectId;
	}
	public void addImageView(ImageView imageView){
		imageViews.add(imageView);
	}
	
	public void setChecked(long id){
		for(int i = 0; i < imageViews.size(); i++ ){
			final ImageView image = imageViews.get(i);
			if(image.getId() == id){
				image.setImageResource(selectId);
				checkId = image.getId();		
			}else{
				image.setImageResource(noselectId);
			}
		}
	}
	
	public long getCheckID(){
		return checkId;
	}
	
}
