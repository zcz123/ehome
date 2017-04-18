package cc.wulian.smarthomev5.receiver.baidu;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.BaiduPushEntity;
import cc.wulian.smarthomev5.receiver.OperBaiduReceiver;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.lidroid.xutils.db.annotation.Id;
import com.wulian.iot.Config;
import com.wulian.iot.view.device.play.PlayEagleActivity;

public class BaiduPushMessageReceiver extends PushMessageReceiver {
	public static final String TAG = "BaiduPushMessageReceiver";
	public Intent mIntent = null;
	@Override
	public void onBind(Context context, int errorCode, String appid,
					   String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);
		if(errorCode==0){
			mIntent = new Intent();
			mIntent.setAction(OperBaiduReceiver.ACTION);
			mIntent.putExtra("MSGTYPE", OperBaiduReceiver.REGISTER);
			mIntent.putExtra("code", errorCode);
			mIntent.putExtra("userId", userId);
			mIntent.putExtra("channelId", channelId);
			context.sendBroadcast(mIntent);
		}
	}
	private String isNUll(String str){
		if(str == null){
			str = "";
		}
		return str;
	}
	/**
	 * 接收透传消息的函数。
	 *
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内荣,为空或者json字符。
	 */
	@Override
	public void onMessage(Context context, String message,
						  String customContentString) {
//		Log.e(TAG, "===onMessage===");
//		Log.e(TAG, message);
//		Log.e(TAG, customContentString);
		String uid =null,eventType = null,eventTime = null;
		BaiduPushEntity baiduPushEntity = null;

		if (!TextUtils.isEmpty(message)) {
			try {
				com.alibaba.fastjson.JSONObject jsonObject= com.alibaba.fastjson.JSONObject.parseObject(message);
				com.alibaba.fastjson.JSONObject contentObject=jsonObject.getJSONObject("custom_content");
				if(contentObject!=null){
					uid = contentObject.getString("uid");
					eventType = contentObject.getString("event_type");
					eventTime = contentObject.getString("event_time");

					String title=contentObject.getString("alert");
					String eventTypeStr="";
					if(eventType!=null&&eventType.equals("1")){
						eventTypeStr=context.getResources().getString(R.string.monitor_eagle_camera)+
								context.getResources().getString(R.string.home_device_alarm_default_voice_detect)
								+context.getResources().getString(R.string.smartLock_Personnel_stay);
						title=context.getResources().getString(R.string.camera_motion_detection);
					}else {
						eventTypeStr=context.getResources().getString(R.string.monitor_eagle_camera)+
								context.getResources().getString(R.string.home_device_alarm_default_voice_detect)
								+context.getResources().getString(R.string.house_rule_condition_device_doorbell_alarm);
						title=context.getResources().getString(R.string.smartLock_doorbell_alarm);
					}
					baiduPushEntity = new BaiduPushEntity(isNUll(uid),isNUll(title),isNUll(eventTypeStr),isNUll(eventTime),isNUll(""),isNUll("admin"));
				}
				mIntent = new Intent();
				mIntent.setAction(OperBaiduReceiver.ACTION);
				mIntent.putExtra("MSGTYPE", OperBaiduReceiver.PUHS_MESSAGE);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable(OperBaiduReceiver.PUST_ENTITY, baiduPushEntity);
				mIntent.putExtras(mBundle);
				context.sendBroadcast(mIntent);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "===onMessage exception===");
				return;
			}
		}
	}
	/*****************************************以下函数没有用到*****************************************************/
	/**
	 * 接收通知到达的函数.
	 *
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标志
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或着json字符。
	 */
	@Override
	public void onNotificationArrived(Context context, String title,
									  String description, String customContentString) {

	}
	/**
	 * 接收通知点击的函数.
	 *
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title,
									  String description, String customContentString) {
		String notifyString = "通知点击 onNotificationClicked title=\"" + title
				+ "\" description=\"" + description + "\" customContent="
				+ customContentString;
		Log.d(TAG, notifyString);

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			String uid =null,eventType = null,eventTime = null;
			BaiduPushEntity baiduPushEntity = null;
			try {
				customJson = new JSONObject(customContentString);
				if (!customJson.isNull("uid")){
					uid=customJson.getString("uid");
				}
				baiduPushEntity = new BaiduPushEntity(isNUll(uid),isNUll(title),isNUll(description),isNUll(eventTime),isNUll(""),isNUll("admin"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Intent resultIntent = new Intent(context.getApplicationContext(), PlayEagleActivity.class);
			resultIntent.putExtra(Config.tutkPwd, baiduPushEntity.getPwd());
			resultIntent.putExtra(Config.tutkUid, baiduPushEntity.getUid());
			resultIntent.putExtra("without", 1);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.getApplicationContext().startActivity(resultIntent);
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}
	/**
	 * setTags() 的回调函数.
	 *
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode,
						  List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	/**
	 * delTags() 的回调函数。
	 *
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode,
						  List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	/**
	 * listTags() 的回调函数。
	 *
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag�?
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
						   String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		Log.d(TAG, responseString);
		if (errorCode == 0) {
			Log.d(TAG, "解绑成功");
		}
	}
}
