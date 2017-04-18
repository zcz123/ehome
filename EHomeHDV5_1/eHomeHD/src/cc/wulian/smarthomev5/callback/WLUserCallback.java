package cc.wulian.smarthomev5.callback;

import cc.wulian.ihome.wan.sdk.user.UserCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

/**
 * Created by yanzy on 2016-5-12
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public class WLUserCallback implements UserCallback {
	private WLUserCallback() {
		
	}
	
	private static WLUserCallback ins = new WLUserCallback();
	
	public static WLUserCallback getInstance() {
		return ins;
	}
	
	@Override
	public void reloginSuccess(String account, String md5passwd, String newToken) {
		SmarthomeFeatureImpl.userLogin(account, md5passwd, newToken);
	}

}
