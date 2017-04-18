package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

public class RedDotManager {
	private static RedDotManager instance=  new RedDotManager();
	
	public static RedDotManager getInstance(){
		return instance;
	}
	private List<RedDotListener> menuLeftDotListeners = new ArrayList<RedDotListener>();
	public void addMenuLeftDotListener(RedDotListener listener){
		this.menuLeftDotListeners.add(listener);
	}
	public void removeMenuLeftDotListener(RedDotListener listener){
		this.menuLeftDotListeners.remove(listener);
	}
	public boolean fireMenuLeftRedDotChange(){
		for(RedDotListener listener : menuLeftDotListeners){
			if(listener.getState()){
				return true;
			}
		}
		return false;
	}
	
	private List<RedDotListener> contactUsDotListeners = new ArrayList<RedDotListener>();
	public void addContactUsListener(RedDotListener listener){
		this.contactUsDotListeners.add(listener);
	}
	public void removeContactUsListener(RedDotListener listener){
		this.contactUsDotListeners.remove(listener);
	}
	public boolean fireContactUsRedDotChange(){
		for(RedDotListener listener : contactUsDotListeners){
			if(listener.getState()){
				return true;
			}
		}
		return false;
	}
	
	public interface RedDotListener{
		public boolean getState();
	}
}
