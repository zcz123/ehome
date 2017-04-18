/**
 * Project Name:  iCam
 * File Name:     UserInfo.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年11月3日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.wulian.icam.common.APPConfig;
import com.wulian.icam.utils.Utils;

/**
 * @ClassName: UserInfo
 * @Function: 用户信息
 * @Date: 2014年11月3日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class UserInfo {

	private String uuid = "";// 用户uuid
	private String suid = "";// sips用户名
	private String prefixdom="";//前缀
	private String sdomain = "";
	private String avatar = "";
	private String auth = "";// 校验码
	private int expire;// 测试发现，先反射expire 后反射expires
	private int expires;// 校验码失效期
	private int localExpireStart;// 本地计算超时的起始时间
	private String username = "";
	private String email = "";
	private String phone = "";
	private String password = "";

	// JavaBean默认拥有一个无参构造函数，如果有其他有参构造函数，必须补全无参构造函数

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSuid() {
		return suid;
	}

	public String getSipSuidSdomain() {
		return "sip:" + suid + sdomain;
	}

	public void setSuid(String suid) {
		this.suid = suid;
	}

	public String getSdomain() {

		if (TextUtils.isEmpty(sdomain)) {
			return APPConfig.SERVERNAME;
		}
		return sdomain;
	}

	public void setSdomain(String sdomain) {
		this.sdomain = sdomain;
	}
	
	public String getPrefixdom() {

		if (TextUtils.isEmpty(prefixdom)) {
			return "";
		}
	    return prefixdom;
	}

	public void setPrefixdom(String prefixdom) {
		this.prefixdom = prefixdom;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	/**
	 * @Function 基于用户手机时间计算的超时时间
	 * @author Wangjj
	 * @date 2015年5月9日
	 * @return
	 */

	public int getExpires() {
		setExpires(0);// 因为不确定反射的顺序是
						// expire和localExpireStart在expires之前，再次调用的时候，这些值肯定已经反射好了。
		return expires - 120;// 本地未超时，服务器可能已经超时，所以要提前120秒
	}

	public void setExpires(int expires) {
		// this.expires = expires;
		if (expire == 0) {
			expire = 3600;
		}
		if (localExpireStart == 0) {
			localExpireStart = (int) (System.currentTimeMillis() / 1000);
		}
		// bug:如果从sp中的json字符串还原，那么超时时间会根据System.currentTimeMillis()重新计算，相应推迟了,所以要本地化。
		// this.expires = (int) (System.currentTimeMillis() / 1000) + expire;
		this.expires = localExpireStart + expire;
	}

	public int getLocalExpireStart() {
		return localExpireStart;
	}

	public void setLocalExpireStart(int localExpireStart) {
		this.localExpireStart = localExpireStart;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserInfo [uuid=" + uuid + ", suid=" + suid + ", sdomain="
				+ sdomain + ", auth=" + auth + ", expires=" + expires
				+ ", username=" + username + ", email=" + email + ", phone="
				+ phone + ", password=" + password + "]";
	}

	
	public Bitmap getAvatar(Context context){
		return Utils.getBitmap(uuid, context).get();
	}

	/**
	 * avatar.
	 *
	 * @return  the avatar
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * avatar
	 * @param   avatar    the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
