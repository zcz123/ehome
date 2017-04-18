/**
 * Project Name:  iCam
 * File Name:     JsonHandler.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年12月17日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wulian.icam.model.BindingOauthAccountModel;
import com.wulian.icam.model.BindingRequestUser;
import com.wulian.icam.model.FeedbackInfo;
import com.wulian.icam.model.OauthMessage;
import com.wulian.icam.model.OauthUserDetail;

/**
 * @ClassName: JsonHandler
 * @Function: Json解析
 * @Date: 2014年12月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class JsonHandler {
	// 获取授权账户列表
	public static List<BindingOauthAccountModel> getBindingOauthAccoutList(
			String value, String device_id) {
		List<BindingOauthAccountModel> list = new ArrayList<BindingOauthAccountModel>();
		try {
			JSONObject jsonobject = new JSONObject(value);
			JSONArray jsonArray = jsonobject.isNull("data") ? null : jsonobject
					.getJSONArray("data");
			if (jsonArray == null || jsonArray.length() == 0) {
				return list;
			}
			int size = jsonArray.length();
			BindingOauthAccountModel data = null;
			for (int index = 0; index < size; index++) {
				JSONObject item = jsonArray.getJSONObject(index);
				data = new BindingOauthAccountModel();
				data.setUsername(item.isNull("username") ? "" : item
						.getString("username"));
				data.setPhone(item.isNull("phone") ? "" : item
						.getString("phone"));
				data.setEmail(item.isNull("email") ? "" : item
						.getString("email"));
				long timeStamp = 1;

				String updated_at = item.isNull("updated_at") ? "" : item
						.getString("updated_at");
				Date date = new Date();
				if (updated_at.equals("")) {
					timeStamp = date.getTime();
				} else {
					try {
						timeStamp = Long.parseLong(updated_at);
					} catch (NumberFormatException e) {
						timeStamp = date.getTime();
					}
				}
				data.setTimestamp(timeStamp);
				data.setDevice_id(device_id);// 服务器不会返回该字段，需要手动设置
				list.add(data);
			}
		} catch (JSONException e) {
		}
		return list;
	}

	/**
	 * @MethodName: getBindingOauthDetailList
	 * @Function: 获取授权信息详细列表
	 * @author: yuanjs
	 * @date: 2015年7月11日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param value
	 *            其中 count 是查看次数，lasttime是最后查看时间
	 * @param device_id
	 * @return
	 */
	public static List<OauthUserDetail> getBindingOauthDetailList(String value) {
		List<OauthUserDetail> list = new ArrayList<OauthUserDetail>();
		try {
			JSONObject jsonobject = new JSONObject(value);
			JSONArray jsonArray = jsonobject.isNull("data") ? null : jsonobject
					.getJSONArray("data");
			if (jsonArray == null || jsonArray.length() == 0) {
				return list;
			}
			int size = jsonArray.length();
			OauthUserDetail data = null;
			for (int index = 0; index < size; index++) {
				JSONObject item = jsonArray.getJSONObject(index);
				data = new OauthUserDetail();
				data.setUsername(item.isNull("username") ? "" : item
						.getString("username"));
				data.setPhone(item.isNull("phone") ? "" : item
						.getString("phone"));
				data.setEmail(item.isNull("email") ? "" : item
						.getString("email"));
				if (!item.isNull("count")) {
					data.setCount(item.getInt("count"));
				} else {
					data.setCount(0);// 授权用户一次也没有使用摄像机
				}
				long timeStamp = 1;
				long recently_time = 1;
				String updated_at = item.isNull("updated_at") ? "" : item
						.getString("updated_at");
				String recentyTime = item.isNull("lasttime") ? "" : item
						.getString("lasttime");
				Date date = new Date();
				if (updated_at.equals("")) {
					timeStamp = 0;
				} else {
					try {
						timeStamp = Long.parseLong(updated_at);
					} catch (NumberFormatException e) {
						timeStamp = date.getTime();
					}
				}
				if (recentyTime.equals("")) {
					recently_time = date.getTime();
				} else {
					try {
						recently_time = Long.parseLong(recentyTime);
					} catch (NumberFormatException e) {
						recently_time = date.getTime();
					}
				}
				data.setLasttime(recently_time);
				data.setTimestamp(timeStamp);
				list.add(data);
			}
		} catch (JSONException e) {
		}
		return list;
	}

	// 获取授权请求列表
	public static List<BindingRequestUser> getBindingRequestList(String value,
			String device_id) {
		List<BindingRequestUser> list = new ArrayList<BindingRequestUser>();
		try {
			JSONObject jsonobject = new JSONObject(value);
			JSONArray jsonArray = jsonobject.isNull("data") ? null : jsonobject
					.getJSONArray("data");
			if (jsonArray == null || jsonArray.length() == 0) {
				return list;
			}
			int size = jsonArray.length();
			BindingRequestUser data = null;
			for (int index = 0; index < size; index++) {
				JSONObject item = jsonArray.getJSONObject(index);
				data = new BindingRequestUser();
				data.setUsername(item.isNull("username") ? "" : item
						.getString("username"));
				data.setPhone(item.isNull("phone") ? "" : item
						.getString("phone"));
				data.setEmail(item.isNull("email") ? "" : item
						.getString("email"));
				data.setDesc(item.isNull("desc") ? "" : item.getString("desc"));
				long timeStamp = 1;
				String updated_at = item.isNull("updated_at") ? "" : item
						.getString("updated_at");
				Date date = new Date();
				if (updated_at.equals("")) {
					timeStamp = date.getTime();
				} else {
					try {
						timeStamp = Long.parseLong(updated_at);
					} catch (NumberFormatException e) {
						timeStamp = date.getTime();
					}
				}
				data.setTimestamp(timeStamp);
				data.setDevice_id(device_id);
				list.add(data);
			}
		} catch (JSONException e) {
		}
		return list;

	}

	// 获取授权请求列表
	public static List<BindingRequestUser> getBindingRequestList(String value) {
		List<BindingRequestUser> list = new ArrayList<BindingRequestUser>();
		try {
			JSONObject jsonobject = new JSONObject(value);
			JSONArray jsonArray = jsonobject.isNull("data") ? null : jsonobject
					.getJSONArray("data");
			if (jsonArray == null || jsonArray.length() == 0) {
				return list;
			}
			int size = jsonArray.length();
			BindingRequestUser data = null;
			for (int index = 0; index < size; index++) {
				JSONObject item = jsonArray.getJSONObject(index);
				data = new BindingRequestUser();
				data.setUsername(item.isNull("username") ? "" : item
						.getString("username"));
				data.setPhone(item.isNull("phone") ? "" : item
						.getString("phone"));
				data.setEmail(item.isNull("email") ? "" : item
						.getString("email"));
				data.setDesc(item.isNull("desc") ? "" : item.getString("desc"));
				long timeStamp = 1;
				String updated_at = item.isNull("updated_at") ? "" : item
						.getString("updated_at");
				Date date = new Date();
				if (updated_at.equals("")) {
					timeStamp = date.getTime();
				} else {
					try {
						timeStamp = Long.parseLong(updated_at);
					} catch (NumberFormatException e) {
						timeStamp = date.getTime();
					}
				}
				data.setTimestamp(timeStamp);
				data.setDevice_id(item.isNull("device_id") ? "" : item
						.getString("device_id"));
				list.add(data);
			}
		} catch (JSONException e) {
		}
		return list;

	}

	// 获取下载消息请求列表
	public static List<OauthMessage> getBindingNoticeList(String value) {
		List<OauthMessage> list = new ArrayList<OauthMessage>();
		try {
			JSONObject jsonobject = new JSONObject(value);
			JSONArray jsonArray = jsonobject.isNull("data") ? null : jsonobject
					.getJSONArray("data");
			if (jsonArray == null || jsonArray.length() == 0) {
				return list;
			}
			int size = jsonArray.length();
			OauthMessage data = null;
			for (int index = 0; index < size; index++) {
				JSONObject item = jsonArray.getJSONObject(index);
				data = new OauthMessage();
				data.setDevice_id(item.isNull("device_id") ? "" : item
						.getString("device_id"));
				data.setUserName(item.isNull("username") ? "" : item
						.getString("username"));
				data.setPhone(item.isNull("phone") ? "" : item
						.getString("phone"));
				data.setEmail(item.isNull("email") ? "" : item
						.getString("email"));
				data.setDesc(item.isNull("desc") ? "" : item.getString("desc"));
				data.setIsUnread(true);
				data.setAccept(false);
				data.setHandle(false);
				String created_at = item.isNull("created_at") ? "" : item
						.getString("created_at");
				Date date = new Date();
				long timeStamp = 1;
				if (created_at.equals("")) {
					timeStamp = date.getTime();
				} else {
					try {
						timeStamp = Long.parseLong(created_at);
					} catch (NumberFormatException e) {
						timeStamp = date.getTime();
					}
				}
				data.setTime(timeStamp);
				String type = item.isNull("type") ? "" : item.getString("type");
				// 1:request,2:add,3:response_accept,4:response_decline,5:delete,6:addresp_acc,7:addresp_dec,0:unknown
				int typeTamp = 0;
				switch (type) {
				case "request":
					typeTamp = 1;
					break;
				case "add":
					typeTamp = 2;
					break;
				case "response_accept":
					typeTamp = 3;
					break;
				case "response_decline":
					typeTamp = 4;
					break;
				case "delete":
					typeTamp = 5;
					break;
				case "addresp_acc":
					typeTamp = 6;
					break;
				case "addresp_dec":
					typeTamp = 7;
					break;
				default:
					break;
				}
				data.setType(typeTamp);
				list.add(data);
			}
		} catch (JSONException e) {
		}
		return list;

	}

	/**
	 * @MethodName: dataIsNull
	 * @Function: 用于判断接收到的数据是否有内容
	 * @author: yuanjs
	 * @date: 2015年4月10日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param json
	 * @return
	 */
	public static boolean dataIsNull(String json) {
		try {
			JSONObject jsonobject = new JSONObject(json);
			JSONArray jsonArray = jsonobject.isNull("data") ? null : jsonobject
					.getJSONArray("data");
			if (jsonArray == null || jsonArray.length() == 0) {
				return true;
			}
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * @MethodName: generatedBarcode
	 * @Function: 创建二维码解析结果
	 * @author: yuanjs
	 * @date: 2015年7月10日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param json
	 *            {"status": 1,"code": "abcdef"}
	 */
	public static String generatedBarcode(String json) {
		try {
			JSONObject jsonobject = new JSONObject(json);
			int status = jsonobject.optInt("status");
			String code = jsonobject.optString("code");
			if (status != 0 && status == 1 && code != null) {
				return code;
			} else {
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @MethodName: getFeedbackInfo
	 * @Function: 解析意见反馈列表
	 * @author: yuanjs
	 * @date: 2015年7月10日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param json
	 *            { "status": 1, "data":[ { "reply": 1, "feedback":
	 *            "...............", "createdat": 1234567890 }, ... ] }
	 * @return
	 */
	public static List<FeedbackInfo> getFeedbackInfo(String json) {
		List<FeedbackInfo> feedbackInfos = new ArrayList<FeedbackInfo>();
		try {
			JSONObject jObject = new JSONObject(json);
			if (jObject.getInt("status") == 1) {
				JSONArray jArray = jObject.getJSONArray("data");
				if (jArray != null) {
					int len = jArray.length();
					for (int i = 0; i < len; i++) {
						FeedbackInfo feedbackInfo = new FeedbackInfo();
						JSONObject item = jArray.getJSONObject(i);
						feedbackInfo.setType(item.isNull("reply") ? 3 : item
								.getInt("reply"));
						feedbackInfo.setFeedback(item.isNull("feedback") ? ""
								: item.getString("feedback"));
						long timeStamp = 1;

						String updated_at = item.isNull("createdat") ? ""
								: item.getString("createdat");
						Date date = new Date();
						if (updated_at.equals("")) {
							timeStamp = date.getTime();
						} else {
							try {
								timeStamp = Long.parseLong(updated_at);
							} catch (NumberFormatException e) {
								timeStamp = date.getTime();
							}
						}
						feedbackInfo.setCreadet(timeStamp);
						feedbackInfos.add(feedbackInfo);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return feedbackInfos;
	}

}
