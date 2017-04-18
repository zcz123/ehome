function inputDialog() {
	var self = this;
	var pageCover = null;
	var that = null; //表示dialog最外层控件
	var imgClose = null; //关闭按钮
	var title = null; //对话框标题
	var mainArea = null; //对话框中间部分
	var btnDiv = null;
	var contentDiv = null; //对话框内容部分
	var okBtn = null;
	var canclebtn = null;

	//设置节点属性
	inputDialog.prototype.setItem = function(item, className, id, display, src, content) {
		if(className) item.className = className;
		if(id) item.id = id;
		if(display) item.style.display = display;
		if(src) item.src = src;
		if(content) item.innerHTML = content;
	};

	//创建节点
	inputDialog.prototype.createItem = function() {
		var body = document.getElementsByTagName("body")[0];
		//遮罩
		pageCover = document.createElement("div");
		pageCover.className = "dialog-cover";
		pageCover.style.width = this.getPageWidth() + "px";
		pageCover.style.height = this.getPageHeight() + "px";
		pageCover.addEventListener("touchstart", this.close);
		//最外层
		that = document.createElement("div");
		this.setItem(that, 'dialog', 'dlgTest', 'none', null, null);

		//标题
		title = document.createElement("div");
		this.setItem(title, 'title', null, null, null, "请输入内容");
		//dialog 内容区域
		mainArea = document.createElement("div");
		this.setItem(mainArea, 'main-area', null, null, null, null);

		contentDiv = document.createElement("div");
		this.setItem(contentDiv, 'content-div', null, null, null, null);

		btnDiv = document.createElement("div");
		this.setItem(btnDiv, 'btn-div', null, null, null, null);

		//确认按钮	       
		okBtn = document.createElement("input");
		okBtn.className = "rightBtn";
		okBtn.type = "button";
		okBtn.value = "确认"
			//取消按钮
		canclebtn = document.createElement("input");
		canclebtn.className = "leftBtn";
		canclebtn.type = "button";
		canclebtn.value = "取消";

		btnDiv.appendChild(okBtn);
		btnDiv.appendChild(canclebtn);

		mainArea.appendChild(contentDiv);
		mainArea.appendChild(btnDiv);

		that.appendChild(title);
		that.appendChild(mainArea);
		body.appendChild(pageCover);
		body.appendChild(that);
	};

	//获取页面顶部蜷去的高度
	inputDialog.prototype.getScrollTop = function() {
		return document.documentElement.scrollTop || document.body.scrollTop || window.pageYOffset || window.scrollY || 0;
	};

	//获取页面窗口的高度
	inputDialog.prototype.getWindowHeight = function() {
		return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
	};

	//获取页面窗口的宽度
	inputDialog.prototype.getWindowWidth = function() {
		return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
	};

	//获取页面全文高度
	inputDialog.prototype.getPageHeight = function() {
		return document.body.scrollHeight;
	};

	//获取页面全文宽度
	inputDialog.prototype.getPageWidth = function() {
		return document.body.scrollWidth;
	};

	//获得当前控件距可视区域顶部的距离
	inputDialog.prototype.getToClientTop = function(item) {
		return parseInt(item.offsetTop - this.getScrollTop());
	};

	//获取初始化时x坐标
	inputDialog.prototype.getInitLeft = function() {
		return parseInt((this.getWindowWidth() - that.offsetWidth) / 2);
	};

	//获取初始化时y坐标
	inputDialog.prototype.getInitTop = function() {
		return parseInt(this.getScrollTop() + (this.getWindowHeight() - that.offsetHeight) / 2);
	};

	//设置坐标位置
	inputDialog.prototype.setLocation = function(left, top) {
		if(that) {
			that.style.left = left + "px";
			that.style.top = top + "px";
		}
	};

	//显示dialog
	inputDialog.prototype.showInput = function(titleTxt, defaultTex, btnTxtArray, onSubmit, onCancle, type, max) {
		this.createItem();
		if(that) {
			//输入框	    
			var dlgInput = document.createElement("input");
			dlgInput.className = "txtInput";
			if(type) {
				dlgInput.type = type;
			}
			if(defaultTex == null || typeof(defaultTex) == "undefined") defaultTex = "";
			dlgInput.placeholder = defaultTex;
			if(max) {
				dlgInput.maxLength = max;
			}
			contentDiv.appendChild(dlgInput);
			if(typeof(titleTxt) == "string") {
				title.innerHTML = titleTxt;
			}
			if(btnTxtArray.length >= 2) {
				okBtn.value = btnTxtArray[0];
				canclebtn.value = btnTxtArray[1];
			}
			//设置按钮事件
			setEvent(function() {
				onSubmit(dlgInput.value);
			}, onCancle);
			that.style.display = 'block';
			this.setLocation(this.getInitLeft(), this.getInitTop());
			dlgInput.focus();
		}
	};

	//显示dialog prompt
	inputDialog.prototype.showPrompt = function(titleTxt, defaultTex, btnTxtArray, onSubmit, onCancle) {
		this.createItem();
		if(that) {
			//输入框	    
			var dlgInput = document.createElement("p");
			dlgInput.className = "textDiv";
			if(defaultTex == null || typeof(defaultTex) == "undefined") defaultTex = "";
			dlgInput.textContent = plus.ehomev5.getLang("html_remove_binding_hint"); //"删除后对方将不能登录此网关!";
			//var dlgInput_1 = document.createElement("p");
			//dlgInput_1.textContent = "确定要删除吗？";
			//dlgInput_1.className = "textDiv_1";
			dlgInput.style.border = "0px"
			contentDiv.appendChild(dlgInput);
			//  contentDiv.appendChild(dlgInput_1);
			if(typeof(titleTxt) == "string") {
				title.innerHTML = titleTxt;
			}
			if(btnTxtArray.length >= 2) {
				okBtn.value = btnTxtArray[0];
				canclebtn.value = btnTxtArray[1];
			}
			//设置按钮事件
			setEvent(function() {
				onSubmit();
			}, onCancle);
			that.style.display = 'block';
			this.setLocation(this.getInitLeft(), this.getInitTop());
		}
	};
	inputDialog.prototype.showAdressAndPhoneInput = function(titleTxt, contentview, btnTxtArray, onSubmit, onCancle) {
		this.createItem();
		if(that) {
			//输入框
			contentDiv.appendChild(contentview);
			if(typeof(titleTxt) == "string") {
				title.innerHTML = titleTxt;
			}
			if(btnTxtArray.length >= 2) {
				okBtn.value = btnTxtArray[0];
				canclebtn.value = btnTxtArray[1];
			}
			//			//设置按钮事件
			setMessageEvent(onSubmit, onCancle);
			that.style.display = 'block';
			this.setLocation(this.getInitLeft(), this.getInitTop());
		}
	};

	//设置按钮事件
	function setMessageEvent(onSubmit, onCancle) {
		if(typeof(onCancle) == "function") {
			canclebtn.onclick = function() {
				self.close();
				onCancle();
			}
		} else {
			canclebtn.onclick = self.close;
		}
		if(typeof(onSubmit) == "function") {
			okBtn.onclick = function() {
				if(onSubmit()) {
					self.close();
				}
			}
		}
	}
	//显示dialog
	inputDialog.prototype.showPermissionDialog = function(titleTxt, contentview, btnTxtArray) {
		this.createItem();
		contentDiv.style.height = "130px";
		contentDiv.style.lineHeight = "130px";
		mainArea.removeChild(btnDiv);
		if(that) {
			contentDiv.appendChild(contentview);
			if(typeof(titleTxt) == "string") {
				title.innerHTML = titleTxt;
			}
			if(btnTxtArray.length >= 2) {
				okBtn.value = btnTxtArray[0];
				canclebtn.value = btnTxtArray[1];
			}
			//设置按钮事件
			//			setEvent(function() {
			////				onSubmit(dlgInput.value);
			//			}, onCancle);
			that.style.display = 'block';
			this.setLocation(this.getInitLeft(), this.getInitTop());
		}
	};

	//设置按钮事件
	inputDialog.prototype.cancelPermissionDialogEvent = function(onPermission) {
		if(typeof(onPermission) == "function") {
			self.close();
			onPermission();
		} else {
			self.close;
		}
	}

	//显示dialog
	inputDialog.prototype.confirm = function(titleTxt, contentText, btnTxtArray, onSubmit, onCancle) {
		this.createItem();
		if(that) {
			if(typeof(titleTxt) == "string") {
				title.innerHTML = titleTxt;
			}
			if(typeof(contentText) == "string") {
				contentDiv.innerHTML = contentText;
			}
			if(btnTxtArray.length >= 2) {
				okBtn.value = btnTxtArray[0];
				canclebtn.value = btnTxtArray[1];
			}
			//设置按钮事件
			setEvent(onSubmit, onCancle);
			that.style.display = 'block';
			this.setLocation(this.getInitLeft(), this.getInitTop());
		}
	};

	//设置按钮事件
	function setEvent(onSubmit, onCancle) {
		if(typeof(onCancle) == "function") {
			canclebtn.onclick = function() {
				self.close();
				onCancle();
			}
		} else {
			canclebtn.onclick = self.close;
		}
		if(typeof(onSubmit) == "function") {
			okBtn.onclick = function() {
				self.close();
				onSubmit();
			}
		} else {
			okBtn.onclick = self.close;
		}
	}
	//关闭
	inputDialog.prototype.close = function() {
		that.parentElement.removeChild(that);
		pageCover.parentElement.removeChild(pageCover);
		pageCover = null;
		offset = null;
		that = null;
		imgClose = null;
		title = null;
		mainArea = null; //对话框中间部分
		contentDiv = null; //对话框内容部分
		okBtn = null;
		canclebtn = null;
	};
}

function foreach(items, action) {
	if(items == null || typeof(items) == "undefined") return;
	if(typeof(action) == "function") {
		for(var i = 0; i < items.length; i++) {
			action(items[i]);
		}
	}
}

//	给控件添加触摸效果
function btnOnTouch(buttons) {
	foreach(buttons, function(item) {
		var bgColor = item.style.backgroundColor;
		item.ontouchstart = function() {
			item.style.backgroundColor = "gray";
		}
		item.ontouchend = function() {
			item.style.backgroundColor = bgColor
		}
	});
}

window.addEventListener("load", function() {
	var registBtns = document.getElementsByClassName("register_btn");
	var logoutBtns = document.getElementsByClassName("logout-btn");
	btnOnTouch(registBtns);
	btnOnTouch(logoutBtns);
});

//获取随url传过来的参数，没有时返回空
function getUrlParam(key) {
	if(key == null || typeof(key) == "undefined" || key.length == 0) return "";
	var reg = new RegExp(key + "=([\\[\\]\\{\\}a-zA-Z0-9%:\\.'\",_\\-@]+)&{0,1}");
	var value = window.location.search;
	if(reg.test(value)) {
		value = RegExp.$1;
	} else {
		value = "";
	}
	return value;
}

//获取随url传过来的字符串数组，没有时返回空
function getUrlArrayParams(key) {
	if(key == null || typeof(key) == "undefined" || key.length == 0) return "";
	var reg = new RegExp(key + "=(.+)&{0,1}");
	var value = window.location.search;
	if(reg.test(value)) {
		value = RegExp.$1;
	} else {
		value = "";
	}
	return value;
}

(function() {
	var v = function() {

	}
	v.prototype = {
		//电话号码验证
		isphone: function(value) {
			var reg = /^(1\d{10})$/;
			if(reg.test(value)) {
				return true;
			}
			return false;
		},
		//验证邮件格式
		ismail: function(value) {
			var reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
			if(reg.test(value)) {
				return true;
			}
			return false;
		},
		isgateway: function(value) {
			reg = /^[0-9a-fA-F]{12}$/;
			if(reg.test(value)) {
				return true;
			}
			return false;
		},
		iswuliannum: function(value) {
			if(value.length < 6 || value.length > 20) {
				return false;
			}
			var reg = /^[\d]+$/;
			if(reg.test(value)) {
				return false;
			}
			var reg = /^[a-zA-Z\d\.@_]+$/;
			if(reg.test(value)) {
				return true;
			}
			return false;
		},
		verifyAccount: function(value) {
			if(this.isphone(value)) {
				return true;
			}
			if(this.ismail(value)) {
				return true;
			}
			if(this.iswuliannum(value)) {
				return true;
			}
			return false;
		},
		verifyPwd: function(value) {
			var passwordRex = /^[\S]{6,20}$/;
			if(value.length < 6 || value.length > 20) {
				return false;
			} else if(passwordRex.test(pwd)) {
				return true;
			}
			return false;
		}
	}

	window.verify = new v();
})();

(function() {
	var s = function() {};

	s.prototype = {
		map: {
			"2009": 'html_map_2009_error', //'手机号码已被其他人使用',
			"2900": 'html_map_2900_error', //'验证码已失效',
			"3005": 'html_map_3005_error', //'传参错误',
			"3010": 'html_map_3010_error', //'必要参数为空',
			"3011": 'html_map_3011_error', //'参数长度非法',
			"3012": 'html_map_3012_error', //'参数格式非法',
			"3055": 'html_map_3055_error', //'数据库操作数据异常',
			"404": 'html_map_404_error', //'鉴权失败(无效的三方身份信息),由浏览器返回',
			"2001": 'html_map_2001_error', //'用户名或密码错误，令牌验证失败',
			"2002": 'html_map_2002_error', //'用户名重复,请重新起名',
			"2000": 'html_map_2000_error', //'Token失效,请重新登录',
			"2007": 'html_map_2007_error', //'邮箱已被其他账号认证使用',
			"2005": 'html_map_2005_error', //'不存在该账号',
			"2006": 'html_map_2006_error', //'未登记的mail地址',
			"2008": 'html_map_2008_error', //'未认证的手机号',
			"2010": 'html_map_2010_error', //'用户无权限或身份不合法',
			"2910": 'html_map_2910_error', //'积分规则无效',
			"2915": 'html_map_2915_error', //'使用规则时间受限',
			"2916": 'html_map_2916_error', //'使用规则次数受限',
			"2101": 'html_map_2101_error', //'设备密码错误,不允许绑定',
			"2205": 'html_map_2205_error', //'管理员密码错误'
			"103001": 'html_map_103001_error', //'原密码不能和新密码相同'
			"103002": 'html_map_103002_error', //'账户原密码错误'
		},
		mapping: function(code) {
			if(code != '0') {
				var aa = this.map[code];
				if(aa == undefined) {
					aa = "html_user_hint_unknow_fail";
				}
				var mapLang = plus.ehomev5.getLang(aa);
				return mapLang;
			} else {
				return this.map[code];
			}
		}
	}
	window.statusHelper = new s();
})();

function judgeMd5(passwd) {
	if(passwd && passwd.length != 32) {
		passwd = CryptoJS.MD5(passwd).toString();
	}
	return passwd;
}