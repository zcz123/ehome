package cc.wulian.smarthomev5.activity;

import android.content.Context;
import android.content.Intent;
import cc.wulian.smarthomev5.service.html5plus.core.IOnActivityResultCallback;

/**
 * Created by yanzy on 2016-6-2
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public interface IActivityCallerWithResult {
	public abstract void setOnActivityResultCallback(IOnActivityResultCallback callBack);
	
	public abstract Context getMyContext();
	
	public abstract void myStartActivityForResult (Intent intent, int requestCode);

}
