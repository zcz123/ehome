(function() {
	if (!window.plus) {
		var p = function() {};

		p.prototype = {
			plusReady: function(Callee) {
				if (!this.OnReadyCallees) {
					this.OnReadyCallees = [];
				}
				this.OnReadyCallees[this.OnReadyCallees.length] = Callee;
			},
			onReady: function() {
				if (this.OnReadyCallees) {
					this.OnReadyCallees.forEach(function(item, index, array) {
						if (typeof item == "function") {
							item();
						}
					});
				}
			}
		};

		window.plus = new p();
	}
})();

//bridge
(function() {

	if (!plus.bridge) {
		var b = function() {};
		b.prototype = {
			exec: function(interfaceName, mathodName, param) {
				try {
					if (window.bridge) {
						window.bridge.exec(interfaceName, mathodName, param);
					} else {
						exec(interfaceName, mathodName, param);
					}
				} catch (e) {
					console.log(e)
				}
			},
			execSync: function(interfaceName, mathodName, param) {
				try {
					if (window.bridge) {
						return window.bridge.execSync(interfaceName, mathodName, param);
					} else {
						return execSync(interfaceName, mathodName, param);
					}
				} catch (e) {
					console.log(e);
				}
			}
		};
		window.plus.bridge = new b();
	}
})();

//callback Util
(function() {
	if (!plus.callbackUtil) {
		var ctMap = function() {};

		ctMap.prototype = {
			cMap: {},
			put: function(key, value) {
				this.cMap[key] = value;
			},
			get: function(key) {
				return this.cMap[key];
			},
			remove: function(key) {
				delete this.cMap[key];
			}
		};

		var cbUtil = function() {
			this.initialize();
		};

		cbUtil.prototype = {
			cbMap: {},
			initialize: function() {
				this.cbMap = new ctMap();
				this.TYPE_OK = "ok";
				this.TYPE_ERROR = "error";
			},
			//自动生成key时调用
			getCallbackId: function(onSuccess, onFailed) {
				var success = (typeof onSuccess !== 'function' ? null : function(args) {
					onSuccess(args);
				});
				var fail = (typeof onFailed !== 'function' ? null : function(code) {
					onFailed(code);
				});
				var currentDate = new Date();
				var key = "cm" + currentDate.getTime();
				var value = {};
				value[this.TYPE_OK] = success;
				value[this.TYPE_ERROR] = fail;
				this.cbMap.put(key, value);
				return key;
			},
			//自己指定key时调用
			putCallback: function(key, onSuccess, onFailed) {
				var success = (typeof onSuccess !== 'function' ? null : function(args) {
					onSuccess(args);
				});
				var fail = (typeof onFailed !== 'function' ? null : function(code) {
					onFailed(code);
				});
				var value = {};
				value[this.TYPE_OK] = success;
				value[this.TYPE_ERROR] = fail;
				this.cbMap.put(key, value);
			},
			exec: function(param) {
				var order = param; //JSON.parse(param);
				var key = order.key;
				var result = order.result;
				var remove = order.remove;
				var type = order.type;
				this.execCallback(key, result, type, remove)
			},
			execCallback: function(key, result, type, remove) {
				var callBack = this.cbMap.get(key);
				if (typeof(remove) == "boolean") {
					if (remove) this.cbMap.remove(key);
				}
				if (!callBack) return;
				if (typeof(callBack[type]) == "function") {
					var resultStr = decodeURI(result);
					callBack[type](resultStr);
				}
			}
		};
		window.plus.callbackUtil = new cbUtil();
	}
})();

//key event

(function() {
	if (!plus.event) {
		var e = function() {
			this.initialize();
		};
		e.prototype = {
			eventListenerMap: {},
			initialize: function() {
				this.onKeyDown = "onKeyDown";
				this.onKeyUp = "onKeyUp";
				this.onKeyLongPress = "onKeyLongPress";
				this.onConfigurationChanged = "onConfigurationChanged";
				this.onActivityResume = "onActivityResume";
				this.onCreateOptionMenu = "onCreateOptionMenu";
				this.KEYCODE_MENU = "KEYCODE_MENU";
				this.KEYCODE_HOME = "KEYCODE_HOME";
				this.KEYCODE_BACK = "KEYCODE_BACK";
			},
			addEventListener: function(eventType, callback) {
				if (this.eventListenerMap[eventType]) {
					this.eventListenerMap[eventType].push(callback);
				} else {
					this.eventListenerMap[eventType] = [callback];
				}
				plus.bridge.execSync("eventListener", "addEventListener", eventType);
			},
			fireEventListener: function(result) {
				try {
					var data=result;
					if(typeof data !="object"){
						data= JSON.parse(result);
					}
					var listeners = this.eventListenerMap[data.eventType];
					if (listeners) {
						listeners.forEach(function(item) {
							if (typeof item == "function") {
								item(data.data);
							}
						});
					}
				} catch (e) {
					console.log(e);
				}
			}
		};
		window.plus.event = new e();
	}
})();

//device
(function() {
	if (!plus.device) {
		var d = function() {};

		d.prototype = {
			getImei: function() {
				return plus.bridge.execSync("device", "getImei", "");
			},
			getOsName: function() {
				return plus.bridge.execSync("device", "getOsName", "");
			},
			getPhoneModel: function() {
				return plus.bridge.execSync("device", "getPhoneModel", "");
			}
		};
		window.plus.device = new d();
	}
})();

//Http resuest util
(function() {
	if (!window.plus.net) {
		window.plus.net = {};

		var H = function() {};

		H.prototype = {
			timeout: 5000,
			onreadystatechange: null,
			open: function(method, url) {
				var data = [method, url, this.timeout];
				this.id = plus.bridge.execSync("XMLHttpRequest", "open", JSON.stringify(data));
			},
			close:function(){
				var data = [this.id];
				plus.bridge.execSync("XMLHttpRequest", "close", JSON.stringify(data));
			},
			setRequestHeader: function(head, type) {
				var data = [this.id, head, type];
				plus.bridge.execSync("XMLHttpRequest", "setRequestHeader", JSON.stringify(data));
			},
			getResponseHeader: function(head) {
				var data = [this.id, head];
				return plus.bridge.execSync("XMLHttpRequest", "getResponseHeader", JSON.stringify(data));
			},
			send: function(param) {
				var that = this;
				var callbackID = plus.callbackUtil.getCallbackId(function(data) {
					try {
						var res = JSON.parse(data)
						that.status = res.status;
						that.responseText = res.data;
						if (typeof res.data == "object") {
							that.responseText = JSON.stringify(res.data);
						}
					} catch (e) {
						console.log(e);
					}
					if (typeof that.onreadystatechange == "function") {
						that.onreadystatechange();
					}
					that.close();
				}, function(status) {
					that.status = status;
					if (typeof that.onreadystatechange == "function") {
						that.onreadystatechange();
					}
					that.close();
				});
				if (!param) param = "";
				var data = [callbackID, this.id, param];
				plus.bridge.exec("XMLHttpRequest", "send", JSON.stringify(data));
			},
		};
		window.plus.net.XMLHttpRequest = H;
	}
})();


//webView
(function() {

	if (!plus.webView) {
		var webViewEntity = function(id) {
			this.initialize(id);
		};

		webViewEntity.prototype = {

			initialize: function(id) {
				this.id = id;
			},
			//关闭Webview窗口
			close: function(onsuccess, onfail) {
				var callbackID = plus.callbackUtil.getCallbackId(onsuccess, onfail);
				var data = [callbackID, this.id];
				plus.bridge.exec("webView", "close", JSON.stringify(data));
			},
			//显示Webview窗口
			show: function(onsuccess, onfail) {
				var callbackID = plus.callbackUtil.getCallbackId(onsuccess, onfail);
				var data = [callbackID, this.id];
				plus.bridge.exec("webView", "show", JSON.stringify(data));
			},
			//隐藏Webview窗口
			hide: function(onsuccess, onfail) {
				var callbackID = plus.callbackUtil.getCallbackId(onsuccess, onfail);
				var data = [callbackID, this.id];
				plus.bridge.exec("webView", "hide", JSON.stringify(data));
			}
		};

		var webView = function() {
			this.initialize();
		};

		webView.prototype = {

			initialize: function() {
				this.baseURI = window.location.href.substring(0, window.location.href.lastIndexOf('/') + 1);
			},
			getUrl: function(uri) {
				if (uri) {
					if (uri.indexOf("://") != -1) {
						return uri;
					} else {
						return this.baseURI + uri;
					}
				}
				return "";
			},
			all: function() {
				return plus.bridge.execSync("webView", "all");
			},
			//创建新的Webview窗口
			create: function(uri, id, width, height, onsuccess, onfail) {
				var url = this.getUrl(uri);
				var callbackID = plus.callbackUtil.getCallbackId(onsuccess, onfail);
				var data = [callbackID, url, id, width, height];
				plus.bridge.exec("webView", "create", JSON.stringify(data));
			},
			//创建新的Webview窗口
			closeWebview: function(id) {
				plus.bridge.exec("webView", "closeWebview", id);
			},
			//获取当前窗口的WebviewObject对象
			currentWebview: function() {
				var id = plus.bridge.execSync("webView", "currentWebview");
				if (id) {
					return new webViewEntity(id);
				}
			},
			//查找指定标识的WebviewObject窗口
			getWebviewById: function(id) {
				var id = plus.bridge.execSync("webView", "getWebviewById", id);
				if (id) {
					return new webViewEntity(id);
				}
			},
			//获取应用首页WebviewObject窗口对象
			getLaunchWebview: function() {
				var id = plus.bridge.execSync("webView", "getLaunchWebview");
				if (id) {
					return new webViewEntity(id);
				}
			},
			//创建并打开Webview窗口  
			open: function(uri, id, width, height, onsuccess, onfail) {
				var url = this.getUrl(uri);
				var callbackID = plus.callbackUtil.getCallbackId(onsuccess, onfail);
				var data = [callbackID, url, id, width, height];
				plus.bridge.exec("webView", "open", JSON.stringify(data));
			},
			canBack: function(onCan, onNot) {
				var callbackID = plus.callbackUtil.getCallbackId(onCan, onNot);
				plus.bridge.execSync("webView", "canBack", callbackID);
			}
		};
		window.plus.webView = new webView();
	}
})();