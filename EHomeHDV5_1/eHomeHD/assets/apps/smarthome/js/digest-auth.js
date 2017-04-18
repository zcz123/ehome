/*
 * A JavaScript implementation of the Digest Authentication
 * Digest Authentication, as defined in RFC 2617.
 */
var getRandomString = function(length) {
	var randStr = '';
	for(var i = 0; i < length; i++) {
		var randi = Math.floor(Math.random() * 62);
		randStr += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(randi);
	}
	return randStr;
};

var dialogKey = "html_dialog_key"; 
var DigestAuthentication = {
	MAX_ATTEMPTS: 1,
	AUTHORIZATION_HEADER: "Authorization",
	WWW_AUTHENTICATE_HEADER: 'WWW-Authenticate',
	NC: "00000001", //currently nc value is fixed it is not incremented
	HTTP_METHOD: "POST",
	TIME_OUT: 5000,
	REG_HTTP: new RegExp('https*://[^/]+/', 'i'),
	/**
	 * settings json:
	 *  - onSuccess - on success callback
	 *  - onFailure - on failure callback
	 *  - username - user name
	 *  - password - user password
	 *  - cnonce - client nonce
	 */
	init: function(settings) {
		var da = {};
		da.settings = settings;
		var headers = new HashMap();
		da.settings.username = plus.ehomev5.getData($CONSTANTS.PARAM_DIGEST_USERNAME);
		da.settings.password = plus.ehomev5.getData($CONSTANTS.PARAM_DIGEST_PASSWORD);
		da.setCredentials = function(username, password) {
			this.settings.username = username;
			this.settings.password = password;
		};
		da.setData = function(data) {
			this.settings.data = data;
		};
		da.setHeader = function(headerName, headerValue) {
			headers.put(headerName, headerValue);
		};
		da.call = function(uri) {
			this.attempts = 0;
			this.loginAttempts = 0;
			this.invokeCall(uri, plus.ehomev5.getData(DigestAuthentication.AUTHORIZATION_HEADER));
		};
		da.checkTokenValid = function(xhr) {
			if(xhr.getResponseHeader("status") != "2000") {
				return true;
			}
			if(plus.ehomev5.getData($CONSTANTS.ACCOUNT) == '' || plus.ehomev5.getData($CONSTANTS.MD5PWD) == '') {
				//用户名或者密码无效,只能退出重新登录
				da.goLoginPage();
			}
		};
		da.relogin = function(uri, authorizationHeader) {
			var digestAuth = this;
			this.loginAttempts++;
			var xhr = new plus.net.XMLHttpRequest();
			xhr.onreadystatechange = function() {
				if(xhr.status == 200) {
					if(xhr.getResponseHeader("status") == 0) {
						//登录成功，重试请求
						var ret = JSON.parse(xhr.responseText);
						//更新cookie中的token，后续请求就能成功了
						plus.ehomev5.setData($CONSTANTS.TOKEN, ret.token);
						//更新本次请求中的token，本次请求才能成功
						da.setHeader($CONSTANTS.TOKEN, ret.token);
						da.invokeCall(uri, authorizationHeader);
					} else if(xhr.getResponseHeader("status") == "2001") {
						//鉴权失败,返回到登录页
						alert(plus.ehomev5.getLang("set_modified_password_login_again"));
						da.goLoginPage();
					} else {
						//登录不成功，但有返回，由业务程序处理
						digestAuth.doSuccess(xhr.responseText, xhr.getResponseHeader("status"));
					}
				} else {
					//http请求异常
					digestAuth.doFailure(xhr.status);
				}
			}
			xhr.timeout = DigestAuthentication.TIME_OUT;
			xhr.open(DigestAuthentication.HTTP_METHOD, plus.ehomev5.getData($CONSTANTS.PARAM_URLBASE) + '/AMS/user/access');
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			if((typeof authorizationHeader != 'undefined') && (authorizationHeader != '')) {
				xhr.setRequestHeader(DigestAuthentication.AUTHORIZATION_HEADER, authorizationHeader);
			}
			xhr.setRequestHeader('cmd', 'userLogin');
			var body = getLoginBody();
			xhr.send(JSON.stringify(body));
		};
		da.doSuccess = function(response, status) {
			if(this.settings.sync !== false) {
				plus.ehomev5.closeWaiting(dialogKey);
			}
			this.settings.onSuccess(response, status);
		};
		da.doFailure = function(httpStatus) {
			if(this.settings.sync !== false) {
				plus.ehomev5.closeWaiting(dialogKey);
			}
			this.settings.onFailure(httpStatus);
		};
		da.goLoginPage = function() {
			plus.ehomev5.userLogout();
			plus.ehomev5.setData($CONSTANTS.MD5PWD, "");
			plus.ehomev5.setData($CONSTANTS.TOKEN, "");
			window.location = "login.html";
		};
		da.invokeCall = function(uri, authorizationHeader) {
			if(this.settings.sync !== false) {
				plus.ehomev5.showWaiting(dialogKey);
			}
			var digestAuth = this;
			var xhr = new plus.net.XMLHttpRequest();
			xhr.onreadystatechange = function() {
				console.log(xhr.status)
				if(xhr.status == 200) {
					var headstatus = xhr.getResponseHeader("status");
					if(digestAuth.checkTokenValid(xhr)) {
						digestAuth.doSuccess(xhr.responseText, headstatus);
					} else if(digestAuth.loginAttempts > DigestAuthentication.MAX_ATTEMPTS) {
						digestAuth.doSuccess(xhr.responseText, headstatus);
					} else {
						digestAuth.relogin(uri, authorizationHeader);
					}
					return;
				} else if(xhr.status == 401 || typeof(xhr.status) == "undefined") {
					if(digestAuth.attempts >= DigestAuthentication.MAX_ATTEMPTS) {
						digestAuth.doFailure(xhr.status);
						return;
					}
					var authenstr = xhr.getResponseHeader(DigestAuthentication.WWW_AUTHENTICATE_HEADER);
					if(typeof(authenstr) == "undefined" || authenstr == null || authenstr == '') {
						digestAuth.doFailure(xhr.status);
						return;
					}
					var paramParser = HeaderParamsParser.init(authenstr);
					var nonce = paramParser.getParam("nonce");
					var realm = paramParser.getParam("realm");
					var qop = paramParser.getParam("qop");
					var urisuffix = uri.replace(DigestAuthentication.REG_HTTP, '/');
					var cnonce = getRandomString(8);
					var response = digestAuth.calculateResponse(urisuffix, nonce, realm, qop, cnonce);
					var authorizationHeaderValue = digestAuth.generateAuthorizationHeader(paramParser.headerValue, response, urisuffix, cnonce);
					digestAuth.attempts++;
					plus.ehomev5.setData(DigestAuthentication.AUTHORIZATION_HEADER, authorizationHeaderValue);
					digestAuth.invokeCall(uri, authorizationHeaderValue);
				} else {
					digestAuth.doFailure(xhr.status);
				}
			};
			xhr.timeout = DigestAuthentication.TIME_OUT;
			xhr.open(DigestAuthentication.HTTP_METHOD, uri);
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			if((typeof authorizationHeader != 'undefined') && (authorizationHeader != '')) {
				xhr.setRequestHeader(DigestAuthentication.AUTHORIZATION_HEADER, authorizationHeader);
			}
			headers.each(function(headerName, headerValue, test) {
				xhr.setRequestHeader(headerName, headerValue);

			});
			xhr.send(digestAuth.settings.data);
		};

		da.calculateResponse = function(uri, nonce, realm, qop, cnonce) {
			var a2 = DigestAuthentication.HTTP_METHOD + ":" + uri;
			var a2Md5 = hex_md5(a2);
			var a1Md5 = hex_md5(this.settings.username + ":" + realm + ":" + this.settings.password);
			var digest = a1Md5 + ":" + nonce + ":" + DigestAuthentication.NC + ":" + cnonce + ":" + qop + ":" + a2Md5;
			return hex_md5(digest);
		};
		da.generateAuthorizationHeader = function(wwwAuthenticationHeader, response, uri, cnonce) {
			return wwwAuthenticationHeader + ', username="' + this.settings.username + '", uri="' +
				uri + '", response="' + response + '", nc=' +
				DigestAuthentication.NC + ', cnonce="' + cnonce + '"';
		}

		return da;
	}
};
var HeaderParamsParser = {
	init: function(headerValue) {
		var hpp = {};
		hpp.headerValue = headerValue;
		hpp.headerParams = hpp.headerValue.split(",");
		hpp.getParam = function(paramName) {
			var paramVal = null;
			for(var i = 0; i < this.headerParams.length; i++) {
				var value = this.headerParams[i];
				if(value.indexOf(paramName) > 0) {
					paramVal = value.split(paramName + "=")[1];
					paramVal = paramVal.substring(1, paramVal.length - 1);
				}
			}
			return paramVal;
		}
		return hpp;
	}
};