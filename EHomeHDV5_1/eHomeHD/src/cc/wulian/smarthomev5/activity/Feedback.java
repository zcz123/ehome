package cc.wulian.smarthomev5.activity;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.CommentInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.CommentsInfoAdapter;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.HttpUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
/**
 * 内容：评价体系
 * 
 * 1.想服务器通过http请求该网关是否已进行过评价（1）、是：展示历史评价界面。（2）。否：展示用户评价界面
 * 
 * 2.历史评价界面展示评分与历史评价，可以进行追评。但不可继续打分；
 * 
 * 3.用户评价界面，允许用户打分与评价，评价成功后发送历史评价请求，用以展示劣势评价界面；
 * 
 * *注：用户评价与历史评价界面所发送评价不可为空，以免展示空白评价；
 * @author ylz
 *
 */
public class Feedback extends BaseActivity {
	private TextView sendMessageTextView;//右下角发表评价按钮

	private EditText feedbackEmailEditText;//发送评价输入框
	
	private RatingBar ratingBarForHardDevice;// 硬件评价控件
	private RatingBar ratingBarSoftware;// 软件评价控件
	private RatingBar ratingBarSaleService;// 服务评价控件

	private String hardwareLevel = "5";// 硬件评价等级
	private String softwareLevel = "5";// 软件评价等级
	private String saleServiceLevel = "5";// 服务评价等级

	private ListView feedbackHistoryListView;//历史评价listview
	private CommentsInfoAdapter mCommentsInfoAdapter;//历史评价adapter
	private List<CommentInfo> commentInfoList;//历史评价数据
	
	private TextView deviceGrade;//硬件设备评分数据显示
	private TextView softwareGrade;//软件评分数据显示
	private TextView saleServiceGrade;//服务评分数据显示
	
	private LinearLayout layoutGrade;//未有过评价界面显示
	private LinearLayout layoutGraded;//已有过评价界面显示
	
	private boolean isFirstFeedback = false;//判断是否是第一次评价
	
	private Thread mThread;//子线程，http请求专用
	
	protected ProgressDialogManager dialogManager;
	protected Dialog loadingDialog;
	private WLDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		setContentView(R.layout.aboutus_feedback);
		sendMessageTextView = (TextView) super.findViewById(R.id.feedback_sendmessage_textview);
		
		feedbackEmailEditText = (EditText)super.findViewById(R.id.myEmailbody);
		
		layoutGrade= (LinearLayout)super.findViewById(R.id.aboutus_feedback_grade);
		layoutGraded= (LinearLayout)super.findViewById(R.id.aboutus_feedback_graded);

		ratingBarForHardDevice = (RatingBar) super.findViewById(R.id.feedbackRatingBar1);
		ratingBarSoftware = (RatingBar) super.findViewById(R.id.feedbackRatingBar2);
		ratingBarSaleService = (RatingBar) super.findViewById(R.id.feedbackRatingBar3);
		
		feedbackHistoryListView = (ListView) super.findViewById(R.id.aboutus_feedback_listview);
		
		ratingBarForHardDevice
				.setOnRatingBarChangeListener(new OnRatingBarChangeListenerForHardDevice());
		ratingBarSoftware
				.setOnRatingBarChangeListener(new OnRatingBarChangeListenerSoftware());
		ratingBarSaleService
				.setOnRatingBarChangeListener(new OnRatingBarChangeListenerSaleService());
		
		deviceGrade = (TextView)super.findViewById(R.id.aboutus_feedback_rating_device_grade);		
		softwareGrade = (TextView)super.findViewById(R.id.aboutus_feedback_rating_software_grade);	
		saleServiceGrade = (TextView)super.findViewById(R.id.aboutus_feedback_rating_service_grade);	
		layoutGrade.setVisibility(View.INVISIBLE);
		layoutGraded.setVisibility(View.GONE);
		// LinearLayout bn = (LinearLayout)
		// super.findViewById(R.id.send_message);
		// bn.setOnClickListener(new OnClickListenerbn());
		
		dialogManager = ProgressDialogManager.getDialogManager();
		getComments();
		sendMessageTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isFirstFeedback) {
					if (StringUtil.isNullOrEmpty(feedbackEmailEditText.getText().toString())) {
						WLToast.showToast(Feedback.this, getResources().getString(R.string.home_monitor_cloud_1_not_null),WLToast.TOAST_LONG);
					} else {
						sendComments(feedbackEmailEditText.getText().toString(),"0");
					}
				}else{
					showSendCommentsDialog();
				}
			}
		});
	}

	private void showLoadingDialog() {
		loadingDialog = dialogManager.createLoadingDialog(Feedback.this,getString(R.string.pull_to_refresh_refreshing_label));
		loadingDialog.show();
	}

	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.about_us));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.about_feedback));
	}
	//硬件评分组件
	public class OnRatingBarChangeListenerForHardDevice implements
			OnRatingBarChangeListener {

		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			if(ratingBar.getRating()<1){
				ratingBarForHardDevice.setRating(1);
				hardwareLevel = 1+"";
			}else{
			hardwareLevel = (int)ratingBar.getRating()+"";
			}
		}
	}
	//软件评分组件
	public class OnRatingBarChangeListenerSoftware implements
			OnRatingBarChangeListener {

		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			if (ratingBar.getRating() < 1) {
				ratingBarSoftware.setRating(1);
				softwareLevel = 1 + "";
			}else{
			softwareLevel = (int)ratingBar.getRating()+"";
			}
		}
	}
	//服务评分组件
	public class OnRatingBarChangeListenerSaleService implements
			OnRatingBarChangeListener {

		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			if (ratingBar.getRating() < 1) {
				ratingBarSaleService.setRating(1);
				saleServiceLevel = 1 + "";
			} else {
				saleServiceLevel =(int)ratingBar.getRating()+"";
			}
		}
	}

	/**
	 * 发送评价至服务器 获取发送结果。
	 */
	private void sendComments(final String str,final String type) {
		showLoadingDialog();
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("gwID",
							mAccountManager.getmCurrentInfo().getGwID());
					jsonObject.put("appID",
							mAccountManager.getRegisterInfo().getAppID());
					jsonObject.put("comments", str);
					jsonObject.put("type", type);
					if ("0".equals(type))
					{
						jsonObject.put("hardwareLevel", hardwareLevel);
						jsonObject.put("softLevel", softwareLevel);
						jsonObject.put("saleServiceLevel", saleServiceLevel);
					}
					

					JSONObject json = HttpUtil.postWulianCloudOrigin(
							WulianCloudURLManager.getCommentsSaveURL(), jsonObject);

					String mJson = json.toString();
					Logger.debug("mJsonHeadersave  =" + mJson);
					final JSONObject mjsonObject = JSONObject
							.parseObject(mJson);
					String mJsonHeader = mjsonObject.getString("header");

					Logger.debug("mJsonHeadersave  =" + mJsonHeader);

					JSONObject header = JSONObject.parseObject(mJsonHeader);
					String result = header.getString("retCode");
					// 收录success，标识发送成功
					if ("SUCCESS".equals(result)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								WLToast.showToast(Feedback.this, getResources().getString(R.string.about_feedback_send_comments_success),
										WLToast.TOAST_LONG);
								stopDialogProgress();
								getComments();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								WLToast.showToast(Feedback.this, getResources().getString(R.string.about_feedback_send_comments_fail),
										WLToast.TOAST_LONG);
								stopDialogProgress();
							}
						});
					}
					Logger.debug("commentssave   = " + json.toString());
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							WLToast.showToast(Feedback.this, getResources().getString(R.string.about_feedback_send_comments_fail),
									WLToast.TOAST_LONG);
							stopDialogProgress();
						}
					});
				}
			}
		});
		mThread.start();
	}
	
	/**
	 * 发送查询命令
	 */
	private void getComments() {
		showLoadingDialog();
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("gwID",
							mAccountManager.getmCurrentInfo().getGwID());
					jsonObject.put("appID",
							mAccountManager.getRegisterInfo().getAppID());
					String url = WulianCloudURLManager.getCommentsQueryURL();
					JSONObject json = HttpUtil.postWulianCloudOrigin(
							WulianCloudURLManager.getCommentsQueryURL(), jsonObject);
					String mJson = json.toString();
					Logger.debug("mJsonHeaderquery  =" + mJson);
					final JSONObject mjsonObject = JSONObject
							.parseObject(mJson);
					String mJsonHeader = mjsonObject.getString("header");

					Logger.debug("mJsonHeaderquery  =" + mJsonHeader);

					JSONObject header = JSONObject.parseObject(mJsonHeader);
					String result = header.getString("retCode");
					if ("SUCCESS".equals(result)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								stopDialogProgress();
								String mJsonBody = mjsonObject
										.getString("body");
								commentInfoList = getCommentsJosnToList(mJsonBody);
								showCommentsList();
							}
						});
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								stopDialogProgress();
								WLToast.showToast(Feedback.this, getResources().getString(R.string.home_monitor_request_time_out_hint),
										WLToast.TOAST_LONG);
							}
						});

					}
					Logger.debug("commentsquery   = " + json.toString());
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							stopDialogProgress();
							WLToast.showToast(Feedback.this, getResources().getString(R.string.home_monitor_request_time_out_hint),
									WLToast.TOAST_LONG);
						}
					});
				}
			}
		});
		mThread.start();
	}

	/**
	 * 查询评价内容
	 * 
	 * @param mResult
	 * @return
	 */
	public List<CommentInfo> getCommentsJosnToList(String mResult) {
		List<CommentInfo> list = new ArrayList<CommentInfo>();

		try {
			JSONObject jsonObject = JSONObject.parseObject(mResult);
			JSONArray mJsonData = jsonObject.getJSONArray("retData");
			for (int i = 0; i < mJsonData.size(); i++) {
				CommentInfo mCommentEntity = new CommentInfo();
				JSONObject jsonArrayauth = (JSONObject) mJsonData
						.getJSONObject(i);

				String comments = jsonArrayauth.getString("comments");
				String gwID = jsonArrayauth.getString("gwID");
				String time = jsonArrayauth.getString("date");
				
				String newHardwareLevel = jsonArrayauth.getString("hardwareLevel");
				String newSoftwareLevel = jsonArrayauth.getString("softLevel");
				String newSaleServiceLevel = jsonArrayauth.getString("saleServiceLevel");
				
				if(!StringUtil.isNullOrEmpty(newHardwareLevel)){
					hardwareLevel = newHardwareLevel;
				}
				if(!StringUtil.isNullOrEmpty(newSoftwareLevel)){
					softwareLevel = newSoftwareLevel;
				}
				if(!StringUtil.isNullOrEmpty(newSaleServiceLevel)){
					saleServiceLevel = newSaleServiceLevel;
				}
				
				
				mCommentEntity.setComment(comments);
				mCommentEntity.setGwID(gwID);
				mCommentEntity.setTime(time);
				list.add(mCommentEntity);
			}
		} catch (Exception e) {
		}
		return list;
	}
	//展示历史评价内容
	private void showCommentsList() {
		if(0 != commentInfoList.size()){
			isFirstFeedback = false;
			layoutGrade.setVisibility(View.GONE);
			layoutGraded.setVisibility(View.VISIBLE);
			
			deviceGrade.setText(hardwareLevel);
			softwareGrade.setText(softwareLevel);
			saleServiceGrade.setText(saleServiceLevel);
			
			mCommentsInfoAdapter = new CommentsInfoAdapter(Feedback.this,commentInfoList);
			feedbackHistoryListView.setAdapter(mCommentsInfoAdapter);
		}
		else{
			isFirstFeedback = true;
			layoutGrade.setVisibility(View.VISIBLE);
			layoutGraded.setVisibility(View.GONE);
		}
	}
	/**
	 * 拥有评价时，弹出dialog让用户输入再次评价；
	 */
	private void showSendCommentsDialog() {
		WLDialog.Builder builder = new Builder(Feedback.this);
		builder.setContentView(R.layout.aboutus_feedback_send_comments_dialog)
				.setTitle(R.string.about_feedback_comments)
				.setPositiveButton(
						R.string.common_ok)
				.setNegativeButton(
						R.string.cancel)
				.setDismissAfterDone(false).setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						EditText oldPasswordEditTextView = (EditText) contentViewLayout
								.findViewById(R.id.aboutus_feedback_send_comments_et);
						
						if (StringUtil.isNullOrEmpty(oldPasswordEditTextView.getText().toString())) {
							WLToast.showToast(Feedback.this, getResources().getString(R.string.home_monitor_cloud_1_not_null),WLToast.TOAST_LONG);
						} else {
							sendComments(oldPasswordEditTextView.getText().toString(),"1");
							dialog.dismiss();
						}
					}

					public void onClickNegative(View contentViewLayout) {
						dialog.dismiss();
					}

				});
		dialog = builder.create();
		dialog.show();
	}

	
	public void stopDialogProgress() {
		if(loadingDialog != null){
			loadingDialog.dismiss();
		}
	}
	
	protected boolean finshSelf() {
		return false;
	}

	public boolean fingerRightFromLeft() {
		return false;
	}

	public boolean fingerRightFromCenter() {
		return false;
	}

	public boolean fingerLeft() {
		return false;
	}
}