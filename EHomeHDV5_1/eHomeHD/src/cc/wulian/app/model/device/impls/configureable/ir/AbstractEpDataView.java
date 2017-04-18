package cc.wulian.app.model.device.impls.configureable.ir;

import android.content.Context;
import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.smarthomev5.activity.MainApplication;

public abstract class AbstractEpDataView {
	protected View rootView;
	protected Context mContext;
	protected LayoutInflater inflater;
	protected MainApplication mApp = MainApplication.getApplication();
	protected DeviceInfo deviceInfo;
	protected Resources resources;
	public static String TYPE_GENERAL = "00";
	public static String TYPE_AIR_CONDITION = "01";
	public static String TYPE_STB = "02";
	
	public static int DIR_LEFT = 0;
	public static int DIR_UP = 1;
	public static int DIR_RIGHT =2;
	public static int DIR_DOWN =3;
	
	protected String epData;
	protected ScrollListener scrollListener;
	private SelectEpDataListener selectEpDataListener;
	protected  GestureDetector mGestureDetector;
	private SimpleOnGestureListener listener = new SimpleOnGestureListener(){

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if(distanceX >30){
				fireScrollListener(DIR_RIGHT);
				return true;
			}else if(distanceX <-30){
				fireScrollListener(DIR_LEFT);
				return true;
			}
			return false;
		}
	};
	public AbstractEpDataView( Context context ,DeviceInfo info,String epData)
	{
		this.mContext = context;
		this.deviceInfo = info;
		inflater = LayoutInflater.from(this.mContext);
		resources = this.mContext.getResources();
		if(epData == null)
			epData = "";
		this.epData = epData;
		mGestureDetector =new GestureDetector(context,listener);
	}
	public abstract View onCreateView();
	public abstract void onViewCreated(View view);
	public String getType(){
		return TYPE_GENERAL;
	}
	public View getView(){
		return this.rootView;
	}
	protected void fireScrollListener(int direction){
		if(scrollListener != null){
			scrollListener.processScroll(direction);
		}
	}
	public ScrollListener getScrollListener() {
		return scrollListener;
	}
	public void setScrollListener(ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}
	protected void fireSelectEpDataListener(String epData){
		if(selectEpDataListener != null){
			selectEpDataListener.onSelectEpData(epData);
		}
	}
	public SelectEpDataListener getSelectEpDataListener() {
		return selectEpDataListener;
	}
	public void setSelectEpDataListener(SelectEpDataListener selectEpDataListener) {
		this.selectEpDataListener = selectEpDataListener;
	}

	public interface ScrollListener{
		public void processScroll(int direction);
	}
	
	public interface SelectEpDataListener{
		public void onSelectEpData(String epData);
	}
}
