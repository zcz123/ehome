package cc.wulian.h5plus.interfaces;

import android.view.ViewGroup;
import cc.wulian.h5plus.view.H5PlusWebView;

/**
 * Created by Administrator on 2016-7-28
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public interface H5PlusWebViewContainer {
	/**
	 * add H5PlusWebView to container,  
	 * 2016-7-28
	 * @author Administrator
	 * @param webview
	 */
	public void addH5PlusWebView(H5PlusWebView webview);
	
	public void destroyContainer();
	
	public ViewGroup getContainerRootView();
	
}
