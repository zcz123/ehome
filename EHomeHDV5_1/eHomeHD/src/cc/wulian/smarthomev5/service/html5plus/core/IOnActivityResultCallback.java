package cc.wulian.smarthomev5.service.html5plus.core;

import android.content.Intent;

/**
* Created by yanzy on 2016-6-2
* Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
**/
public interface IOnActivityResultCallback {
	public void doWhatOnActivityResult(int requestCode, int resultCode,
			Intent data);
}