(function() {
	// 声明的JS“扩展插件别名”
	var _BARCODE = 'ehomev5',
		B = window.plus.bridge;
	var ehomev5 = {
		lang: {},
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
				if(this.lang[param] == undefined) {
					this.lang[param] = B.execSync(_BARCODE, "getLang", JSON.stringify([param]));
				}
				return this.lang[param];
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
		getPicData: function(name, successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			});
			var fail = (typeof errorCallback !== 'function' ? null : function(code) {
				errorCallback(code);
			});
			var callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.execSync(_BARCODE, "getPicData", JSON.stringify([callbackID, name]));
		},
		getDevicePictureData: function(gwId, deviceID, successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			});
			var fail = (typeof errorCallback !== 'function' ? null : function(code) {
				errorCallback(code);
			});
			var callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.execSync(_BARCODE, "getDevicePictureData", JSON.stringify([callbackID, gwId, deviceID]));
		},
		selectPhoto: function(successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			});
			var fail = (typeof errorCallback !== 'function' ? null : function(code) {
				errorCallback(code);
			});
			var callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.exec(_BARCODE, "selectPhoto", JSON.stringify([callbackID]));
		},
		downloadFile: function(fileName, successCallback, errorCallback) {
			var success = (typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			});
			var fail = (typeof errorCallback !== 'function' ? null : function(code) {
				errorCallback(code);
			});
			var callbackID = plus.callbackUtil.getCallbackId(success, fail);
			return B.exec(_BARCODE, "downloadFile", JSON.stringify([callbackID, fileName]));
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
		sendControlDevice: function(param, callbackID, successCallback, errorCallback) {
			if(callbackID == null || typeof(callbackID) == "undefined" || callbackID.length == 0) {
				callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			} else {
				plus.callbackUtil.putCallback(callbackID, successCallback, errorCallback);
			}
			return B.exec(_BARCODE, "sendControlDevice", JSON.stringify([callbackID, param]));
		},
		startActivity: function(param) {
			return B.exec(_BARCODE, "startActivity", JSON.stringify([param]));
		},
		changeGateWay: function(param, callbackID, successCallback, errorCallback) {
			if(callbackID == null || typeof(callbackID) == "undefined" || callbackID.length == 0) {
				callbackID = callBackUtil.getCallbackId(successCallback, errorCallback);
			} else {
				plus.callbackUtil.putCallback(callbackID, successCallback, errorCallback);
			}
			return B.exec(_BARCODE, "changeGateWay", JSON.stringify([callbackID, param]));
		},
		myDialog: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "myDialog", JSON.stringify([callbackID, param]));
		},
		myDialogWithLink: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "myDialogWithLink", JSON.stringify([callbackID, param]));
		},
		registerClientToTutkAndMappingWithDevice: function(param) {
			//			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "registerClientToTutkAndMappingWithDevice", JSON.stringify([param]));
		},
		unMappingWithDevice: function(param) {
			//			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "unMappingWithDevice", JSON.stringify([param]));
		},
		alertDialog: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "alertDialog", JSON.stringify([callbackID, param]));
		},
		popWindow: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "popWindow", JSON.stringify([callbackID, param]));
		},
		addEyeCamera: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "addEyeCamera", JSON.stringify([callbackID, param]));
		},
		goToSetEyeCamera: function(param, isAdmin, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "goToSetEyeCamera", JSON.stringify([callbackID, param, isAdmin]));
		},
		goToEyeCamera: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "goToEyeCamera", JSON.stringify([callbackID, param]));
		},
		addEyeCatYiKang: function(param, successCallback, errorCallback) {
                			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
                			return B.exec(_BARCODE, "addEyeCatYiKang", JSON.stringify([callbackID, param]));
        },
		goToSetEyeCatYiKang: function(param, isAdmin, successCallback, errorCallback) {
        			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
        			return B.exec(_BARCODE, "goToSetEyeCatYiKang", JSON.stringify([callbackID, param, isAdmin]));
        },
        goToEyeCatYiKang: function(param, successCallback, errorCallback) {
        			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
        			return B.exec(_BARCODE, "goToEyeCatYiKang", JSON.stringify([callbackID, param]));
        },
		sendUeiCommand: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "sendUeiCommand", JSON.stringify([callbackID, param]));
		},
		userLogout: function(param) {
			if(!param) {
				param = "";
			}
			return B.execSync(_BARCODE, "userLogout", JSON.stringify([param]));
		},
		getGateWayPic: function(param, successCallback, errorCallback) {
			var callbackID = plus.callbackUtil.getCallbackId(successCallback, errorCallback);
			return B.exec(_BARCODE, "getGateWayPic", JSON.stringify([callbackID, param]));
		},
		toast: function(param) {
			return B.execSync(_BARCODE, "toast", JSON.stringify(param));
		},
		userLogin: function(account, md5passwd, token) {
			return B.execSync(_BARCODE, "userLogin", JSON.stringify([account, md5passwd, token]));
		}
	};

	window.plus.ehomev5 = ehomev5;
})();

function getLoginBody() {
	var obj = {};
	obj.account = plus.ehomev5.getData($CONSTANTS.ACCOUNT);
	obj.password = plus.ehomev5.getData($CONSTANTS.MD5PWD);
	obj.imei = plus.device.getImei();
	obj.appVer = plus.ehomev5.getData("loginAppVer");
	obj.appLang = plus.ehomev5.getCurrentLanguag();
	if(plus.device.getOsName() == "iOS") {
		obj.appToken = plus.ehomev5.getData("iosAppToken");
		obj.appType = plus.ehomev5.getData('appType');
		obj.osType = "1";
		obj.pushType = "2";
	}else{
		obj.appToken = plus.ehomev5.getData("loginAppToken");
		obj.appType = plus.ehomev5.getData('loginAppType');
		obj.osType = "0";
		obj.pushType = plus.ehomev5.getData('loginPushType');
	}
	return obj;
}
window.$CONSTANTS = {
	MD5PWD: 'md5pwd',
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
	PARAM_DIGEST_URLBASE: '_AMS_digest_urlbase',
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

//自定义返回消息触发时的动作
var personalBack;

//html页面回退和关闭Activity
function goBackAndFinishListener() {
	if(personalBack != undefined && (typeof personalBack == "function")) {
		personalBack();
		return;
	}
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
		var items = document.getElementsByClassName("custom-action-back");
		for(var i = 0; i < items.length; i++) {
			items[i].addEventListener("click", goBackAndFinishListener);
		}
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
		plus.event.addEventListener(plus.event.KEYCODE_BACK, goBackAndFinishListener);

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

window.alert = function(param) {
	plus.ehomev5.toast(param);
};