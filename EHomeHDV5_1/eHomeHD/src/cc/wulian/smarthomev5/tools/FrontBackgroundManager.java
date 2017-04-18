package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

public class FrontBackgroundManager {

	
	private static FrontBackgroundManager instance = new FrontBackgroundManager();
	private List<FrontBackgroundListener> listeners = new ArrayList<FrontBackgroundListener>();
	private FrontBackgroundManager(){
		
	}
	public static FrontBackgroundManager getInstance(){
		return instance;
	}
	public interface FrontBackgroundListener{
		public void onBackground(boolean isBackground);
	}
	public void addFrontBackgroundListener(FrontBackgroundListener listener){
		this.listeners.add(listener);
	}
	public void removeFrongBackgroundListener(FrontBackgroundListener listener){
		this.listeners.remove(listener);
	}
	public void fireFrongBackgroundListener(boolean isBackground){
		for(FrontBackgroundListener listener : listeners){
			listener.onBackground(isBackground);
		}
	}
}
