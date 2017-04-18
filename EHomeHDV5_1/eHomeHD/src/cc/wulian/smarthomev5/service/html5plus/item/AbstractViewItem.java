package cc.wulian.smarthomev5.service.html5plus.item;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class AbstractViewItem implements Parcelable{

	private int  LayoutId;
	
	public AbstractViewItem(int LayoutId){
		this.LayoutId=LayoutId;
	}
	
	public View getView(Context context){
		View view=View.inflate(context, LayoutId, null);
		bindView(view);
		return view;
	}
	
	//需要绑定事件请重写
	public void bindView(View view){
		
	}
	
	public static final Parcelable.Creator<AbstractViewItem> CREATOR = new Creator<AbstractViewItem>() {    
        public AbstractViewItem createFromParcel(Parcel source) {   
        	int id=source.readInt();
        	AbstractViewItem item=new AbstractViewItem(id);
            return item;    
        }    
        public AbstractViewItem[] newArray(int size) {    
            return new AbstractViewItem[size];    
        }    
    };    
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(LayoutId);
	}
	
}
