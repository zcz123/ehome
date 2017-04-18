(function() {
	// 声明的JS“扩展插件别名”
	var _BARCODE = 'ehomev5',
		B = window.plus.bridge;
	var ehomev5 = {
		getData: function(name) {
			return B.execSync(_BARCODE, "getData", JSON.stringify([name]));
		},
		setData: function(name, value) {
			return B.execSync(_BARCODE, "setData", JSON.stringify([name, value]));
		},
		closeWebview: function(url) {
			return B.execSync(_BARCODE, "closeWebview", JSON.stringify([url]));
		},
		uploadFile: function(filepath, successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
					successCallback(args);
				}),
				fail = (typeof errorCallback !== 'function' ? null : function(code) {
					errorCallback(code);
				});
			callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.exec(_BARCODE, "uploadFile", JSON.stringify([callbackID, filepath]));
		},
		getLang: function() {
			if(arguments.length > 0) {
				var param = arguments[0];
				return B.execSync(_BARCODE, "getLang", JSON.stringify([param]));
			} else {
				return B.execSync(_BARCODE, "getLang", JSON.stringify([]));
			}
		},
		getCurrentLanguag: function() {
			if(arguments.length > 0) {
				var param = arguments[0];
				return B.execSync(_BARCODE, "getCurrentLanguag", JSON.stringify([param]));
			} else {
				return B.execSync(_BARCODE, "getCurrentLanguag", JSON.stringify([]));
			}
		},
		interfaceBetweenHttpAndCloud: function(param, successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			});
			var fail = (typeof errorCallback !== 'function' ? null : function(code) {
				errorCallback(code);
			});
			var callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.exec(_BARCODE, "interfaceBetweenHttpAndCloud", JSON.stringify([callbackID, param]));
		},
		showWaiting: function(key) {
			return B.exec(_BARCODE, "showWaiting", JSON.stringify([key]));
		},
		closeWaiting: function(key) {
			return B.exec(_BARCODE, "closeWaiting", JSON.stringify([key]));
		},
		setGWLocation:function(cityID,successCallback, errorCallback){
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return plus.bridge.exec(_BARCODE, "setGWLocation", JSON.stringify([callbackID, cityID]));
		},
		passVauleToWebView:function(webID,functionName,param){
			return plus.bridge.exec(_BARCODE, "passVauleToWebView", JSON.stringify([webID, functionName,param]));
		},
		interfaceBetweenAMSHttpAndCloud: function(param, successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			});
			var fail = (typeof errorCallback !== 'function' ? null : function(code) {
				errorCallback(code);
			});
			var callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.exec(_BARCODE, "interfaceBetweenAMSHttpAndCloud", JSON.stringify([callbackID, param]));
		},
	};

	window.plus.ehomev5 = ehomev5;
})();

(function() {
	// 声明的JS“扩展插件别名”
	var _BARCODE = 'cmdControl',
		B = window.plus.bridge;
	var cmdControl = {
		//设置定时
		setDeviceTimed: function(devID,ep, epData, interval, successCallback,errorCallback) {
			//若指定pageType，pageType做callBackID
			var callbackID;
			plus.callbackUtil.putCallback("setDeviceTimed", successCallback, errorCallback);
			callbackID = "setDeviceTimed";
			return plus.bridge.exec(_BARCODE, "setDeviceTimed", JSON.stringify([callbackID, devID,ep, epData,interval]));
		},
		//取消定时
		delDeviceTimed: function(devID,ep,successCallback,errorCallback) {
			//若指定pageType，pageType做callBackID
			var callbackID;
			plus.callbackUtil.putCallback("delDeviceTimed", successCallback, errorCallback);
			callbackID = "delDeviceTimed";
			
			return plus.bridge.exec(_BARCODE, "delDeviceTimed", JSON.stringify([callbackID, devID,ep]));
		},
		//查询定时
		queryDeviceTimed: function(devID,ep,successCallback,errorCallback) {
			//若指定pageType，pageType做callBackID
			var callbackID;
			plus.callbackUtil.putCallback("queryDeviceTimed", successCallback, errorCallback);
			callbackID = "queryDeviceTimed";
			return plus.bridge.exec(_BARCODE, "queryDeviceTimed", JSON.stringify([callbackID, devID,ep]));
		},
	};
	window.plus.cmdControl = cmdControl;
})();

function getLoginBody() {
	var obj = {};
	obj.account = plus.ehomev5.getData($CONSTANTS.USERID);
	obj.password = plus.ehomev5.getData($CONSTANTS.MD5PWD);
	obj.imei = plus.device.getImei();
	obj.appVer = "V5.1.5";
	obj.appLang = plus.ehomev5.getLang();
	return obj;
}
window.$CONSTANTS = {
	MD5PWD: 'md5pwd',
	PASSWORD: 'password',
	USERID: 'userID',
	NICKNAME: 'nickName',
	ACCOUNT: 'account',
	USERNAME: 'userName',
	TOKEN: 'token',
	EMAIL: 'profile_email',
	PHONENUMBER: 'profile_phone',
	PROFILE: 'profile_json',
	DEVICES_JSON: 'devices_json',
	MXZH_DISTRICT: 'mxzh_district',
	PARAM_URLBASE: '_AMS_urlbase',
	PARAM_DIGEST_USERNAME: '_digest_username',
	PARAM_DIGEST_PASSWORD: '_digest_password',
	VERSION: 'V0.0.1',
	TEXT_WILL_SHOW: 'text_will_show',
	GATEWAYID: 'gwID',
	DEVICEID: 'devID',
	EP: "ep",
	EPTYPE: "epType",
	EPDATA: "epData",
	DEVICEPARAM: "deviceparam",
	IS_LOGIN: "is_login"
};

//html页面回退
function goBackListener() {
	plus.webView.canBack(function() {
		history.back(-1);
	}, function() {});
}

//html页面回退和关闭Activity
function goBackAndFinishListener() {
	plus.webView.canBack(function() {
		history.back(-1);
	}, function() {
		if(plus.webView) {
			plus.webView.currentWebview().close();
		}
	});
}

//其他事件处理
(function() {
	plus.plusReady(function() {
		//处理返回按钮
//		var items = document.getElementsByClassName("custom-action-back");
//		for(var i = 0; i < items.length; i++) {
//			items[i].addEventListener("click", goBackAndFinishListener);
//		}
		//处理IOS状态栏
		switch(plus.device.getOsName()) {
			case "Android":
				break;
			case "iOS":
				//				plus.navigator.setStatusBarBackground("#719E19");
				var heads = document.getElementsByClassName("custom-nav");
				foreach(heads, function(item) {
					item.style.height = "64px";
					item.style.paddingTop = "20px";
				});
				var contents = document.getElementsByClassName("mui-content");
				foreach(contents, function(item) {
					item.style.paddingTop = "64px";
				});
				contents = document.getElementsByClassName("custom-head");
				foreach(contents, function(item) {
					item.style.marginTop = "64px";
				});
				break;
			default:
				// 其它平台
				break;
		}
		plus.event.addEventListener(plus.event.KEYCODE_BACK, backClick);

		//处理语言国际化
		var items = document.getElementsByClassName("autoSwitchLanguager");
		foreach(items, function(item) {
			var languagerText = plus.ehomev5.getLang(item.id);
			if(languagerText) {
				if(item.type == "button") {
					item.value = languagerText;
				} else {
					item.innerHTML = languagerText;
				}
			}
		});
	});
})();

function getRequestParam(strParame) {
	var query = location.search.substring(1);
	var pairs = query.split("&");
	for(var i = 0; i < pairs.length; i++) {
		var pos = pairs[i].indexOf('=');
		if(pos == -1) continue;
		var argname = pairs[i].substring(0, pos);
		var value = pairs[i].substring(pos + 1);
		value = decodeURIComponent(value);
		if(argname == strParame) {
			return value;
		}
	}
}

function foreach(items, action) {
	if(!items) return;
	if(typeof(action) == "function") {
		for(var i = 0; i < items.length; i++) {
			action(items[i]);
		}
	}
}

function reloadAbleJSFn(id, newJS) {
	var oldjs = null;
	var t = null;
	var oldjs = document.getElementById(id);
	if(oldjs) oldjs.parentNode.removeChild(oldjs);
	var scriptObj = document.createElement("script");
	scriptObj.src = newJS;
	scriptObj.type = "text/javascript";
	scriptObj.id = id;
	document.getElementsByTagName("head")[0].appendChild(scriptObj);
}

//window.alert = function(param) {
//	plus.ehomev5.toast(param);
//};