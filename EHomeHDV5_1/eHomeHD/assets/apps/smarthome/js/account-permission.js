/*
 * 提供账号权限Http请求的方法
 */

var dialogKey="html_dialog_key";


var AccountPermission = {
	
	MAX_ATTEMPTS: 1,
	ACCOUNTPERMISSION_HEADER: "AccountPermission",
	WWW_AUTHENTICATE_HEADER: 'WWW-Authenticate',
	NC: "00000001", //currently nc value is fixed it is not incremented
	HTTP_METHOD: "POST",
	TIME_OUT: 5000,
	REG_HTTP: new RegExp('http://[^/]+/', 'i'),
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
		da.setData = function(data) {
			this.settings.data = data;
		};
		da.setHeader = function(headerName, headerValue) {
			headers.put(headerName, headerValue);
		};
		da.call = function(uri) {
			this.attempts = 0;
			this.loginAttempts = 0;
			this.invokeCall(uri, plus.ehomev5.getData(AccountPermission.ACCOUNTPERMISSION_HEADER));
		};
		da.doSuccess = function(response, status) {
			if (this.settings.sync !== false) {
				plus.ehomev5.closeWaiting(dialogKey);
			}
			this.settings.onSuccess(response, status);
		};
		da.doFailure = function(httpStatus) {
			if (this.settings.sync !== false) {
				plus.ehomev5.closeWaiting(dialogKey);
			}
			this.settings.onFailure(httpStatus);
		};
		da.invokeCall = function(uri, accountPermission) {
			if (this.settings.sync !== false) {
				plus.ehomev5.showWaiting(dialogKey);
			}
			var digestAuth = this;
			var xhr = new plus.net.XMLHttpRequest();
			xhr.onreadystatechange = function() {
				switch (xhr.readyState) {
					case 4:
						if (xhr.status == 200) {
							var headstatus = xhr.getResponseHeader("status");
							if (digestAuth.checkTokenValid(xhr)) {
								digestAuth.doSuccess(xhr.responseText, headstatus);
							} else if (digestAuth.loginAttempts > AccountPermission.MAX_ATTEMPTS) {
								digestAuth.doSuccess(xhr.responseText, headstatus);
							} else {
								digestAuth.relogin(uri, accountPermission);
							}
							return;
						} else {
							if (digestAuth.attempts >= AccountPermission.MAX_ATTEMPTS) {
								digestAuth.doFailure(xhr.status);
								return;
							}
							var authenstr = xhr.getResponseHeader(AccountPermission.WWW_AUTHENTICATE_HEADER);
							if (typeof(authenstr) == "undefined" || authenstr == null || authenstr == '') {
								digestAuth.doFailure(xhr.status);
								return;
							}
							var paramParser = HeaderParamsParser.init();
							var nonce = paramParser.getParam("nonce");
							var realm = paramParser.getParam("realm");
							var qop = paramParser.getParam("qop");
							var urisuffix = uri.replace(AccountPermission.REG_HTTP, '/');
							var cnonce = getRandomString(8);
							var response = digestAuth.calculateResponse(urisuffix, nonce, realm, qop, cnonce);
							var authorizationHeaderValue = digestAuth.generateAuthorizationHeader(paramParser.headerValue, response, urisuffix, cnonce);
							digestAuth.attempts++;
							plus.ehomev5.setData(AccountPermission.ACCOUNTPERMISSION_HEADER, authorizationHeaderValue);
							digestAuth.invokeCall(uri, authorizationHeaderValue);
						}
						break;
					default:
						break;
				}

			};
			xhr.timeout = AccountPermission.TIME_OUT;
			xhr.open(AccountPermission.HTTP_METHOD, uri);
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			if ((typeof accountPermission != 'undefined') && (accountPermission != '')) {
				xhr.setRequestHeader(AccountPermission.ACCOUNTPERMISSION_HEADER, accountPermission);
			}
			headers.each(function(headerName, headerValue, test) {
				xhr.setRequestHeader(headerName, headerValue);

			});
			xhr.send(digestAuth.settings.data);
		};

		return da;
	}

}
