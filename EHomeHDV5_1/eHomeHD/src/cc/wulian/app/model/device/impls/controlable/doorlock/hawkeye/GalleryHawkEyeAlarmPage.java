package cc.wulian.app.model.device.impls.controlable.doorlock.hawkeye;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_89_DoorLock_6;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlListEventReq;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord;
import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.utils.EagleUtil;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.adapter.AlarmListViewAdapter;
import com.wulian.iot.view.base.BasePage;
import com.wulian.iot.widght.VCalendar;
import com.wulian.iot.widght.VCalendar.OnCalendarClickListener;
import com.wulian.iot.widght.VCalendar.OnCalendarDateChangedListener;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

public class GalleryHawkEyeAlarmPage extends BasePage implements OnClickListener, Callback,OnItemClickListener, IRegisterIOTCListener{
    
	private final static String TAG = "IOTCamera";
	private String srarchTime = null;
	private PopupWindows popupWindow;
	private ImageView showPopuDate;
	private ListView alarmListView;
	private LinearLayout mLinPopu;
	private AlarmListViewAdapter mListAdapter;
	private List<GalleryAlarmInfo> mList;
	Context mContext;
	private Handler mHandler=new Handler(this);

	//这里有个问题 何时去注销 camera对象。unregister，没有好的方法。
	private final static int  FAIL_TO_GETDATA = 9;
	@Override
	public boolean handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		byte[] data = bundle.getByteArray("listevent");
		switch (msg.what) {
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP:
			break;
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_RESP:
			Log.i(TAG, "------------data:"+data.length);
			Log.i(TAG, "------------data:要显示页面的");
			if (data.length > 8) {
				mList= EagleUtil.parsePlayBackFileInfo(data);
				if(mList !=null){
					Log.i(TAG, "------------mList !=null");
				    mListAdapter=new AlarmListViewAdapter(mContext, mList);
				    alarmListView.setAdapter(mListAdapter);
				    break;
				}
			}
			WLToast.showToast(mContext, mContext.getResources().getString(R.string.eagle_alarm_null), Toast.LENGTH_SHORT);
			break;
		default:
			break;
		}
		return false;
	}
	
	public GalleryHawkEyeAlarmPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext=context;
	}

	@Override
	public View initView() {
		Log.e(TAG, "initView");
		View v=View.inflate(context, R.layout.page_gallery_infraredalarm, null);
		showPopuDate=(ImageView) v.findViewById(R.id.iv_alarm_date);
		alarmListView=(ListView) v.findViewById(R.id.alarm_show_video_time);
		mLinPopu=(LinearLayout) v.findViewById(R.id.lin_alram_show_popu_parent);
		return v;
	}
	@Override
	public void initEvents() {
		Log.e(TAG, "initEvents");
		showPopuDate.setOnClickListener(this);
		alarmListView.setOnItemClickListener(this);
	}
	@Override
	public void initData() {
		Log.e(TAG, "initData");
		if (WL_89_DoorLock_6.cHelperHawkeye == null) {
			Log.i(TAG, "----------camera is not");
			return;
			
		}else{
			WL_89_DoorLock_6.cHelperHawkeye.registerIOTCListener(this);
		}
	}
	@Override
	public void onClick(View v) {
		if (v==showPopuDate) {
			if (popupWindow==null) {
				popupWindow=new PopupWindows(context);
			}
			popupWindow.show(mLinPopu);
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent mIntent = new Intent(mContext,PlayHawkeyeHistoryVideoAvtivity.class);
		mIntent.putExtra(Config.playEagleVideoTyep, "server");
		mIntent.putExtra("videoPath", mList.get(position));
		mContext.startActivity(mIntent);
		Log.e(TAG, "onItemClick"+mList.get(position));
		Log.e(TAG, "onItemClick");
		
		
	}
	
	
	
	
	// TODO 日期监听ui 可以重构
	public class PopupWindows extends PopupWindow {
		public PopupWindows(Context mContext) {
			View view = View.inflate(mContext,
					R.layout.popupwindow_calendar, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_in));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.calendar_push));
			RelativeLayout mHomeCheckDate = (RelativeLayout) view
					.findViewById(R.id.home_check_date);
			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new ColorDrawable(R.color.white));
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			update();

			final TextView popupwindow_calendar_month = (TextView) view
					.findViewById(R.id.popupwindow_calendar_month);
			final VCalendar calendar = (VCalendar) view
					.findViewById(R.id.popupwindow_calendar);

			popupwindow_calendar_month.setText(calendar.getCalendarYear() + "."
					+ calendar.getCalendarMonth());

			// 监听所选中的日期
			calendar.setOnCalendarClickListener(new OnCalendarClickListener() {
            // TODO dateFormat String 类型的日期
				public void onCalendarClick(int row, int col, String dateFormat) {
					int month = Integer.parseInt(dateFormat.substring(
							dateFormat.indexOf("-") + 1,
							dateFormat.lastIndexOf("-")));

					if (calendar.getCalendarMonth() - month == 1// 跨年跳转
							|| calendar.getCalendarMonth() - month == -11) {
						calendar.lastMonth();

					} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
							|| month - calendar.getCalendarMonth() == -11) {
						calendar.nextMonth();

					} else {
						calendar.removeAllBgColor();
						calendar.setCalendarDayBgColor(dateFormat,
								R.drawable.calendar_datetime_focused);
						SimpleDateFormat selectDayFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						try {
							srarchTime = DateUtil.getFormatIMGTime(selectDayFormat.parse(dateFormat).getTime());
							System.out.println("------>查询时间"+srarchTime);
							
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			});
			// 监听当前月份
			calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
				public void onCalendarDateChanged(int year, int month) {
					popupwindow_calendar_month.setText(year + "." + month);
				}
			});
			// 上月监听按钮
			RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
					.findViewById(R.id.popupwindow_calendar_last_month);
			popupwindow_calendar_last_month
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							calendar.lastMonth();
						}
					});
			// 下月监听按钮
			RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
					.findViewById(R.id.popupwindow_calendar_next_month);
			popupwindow_calendar_next_month
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							calendar.nextMonth();
						}
					});

			// 关闭窗口
			mHomeCheckDate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();					
					Log.i(TAG, "------>查询时间"+srarchTime);
					
					if (srarchTime != null) {
						Log.i("IOTCamera", "-----------有时间选择");
						Log.e("IOTCamera", "---------------getPlayBackFile");  
						//不知为何设备端的搜索时间 有加八的打印，所以想搜索 10点的数据，小时那个地方填写2。
						byte[] startTime =IotUtil.stringToByteforSearchHawk(srarchTime, 0);         
						byte[] endTime =IotUtil.stringToByteforSearchHawk(srarchTime, 1);		
						short year  = (short) Integer.parseInt(srarchTime.substring(0,4));
						
						//输送数据之前一定要进行时间正确性判断、必须优先判断时间的正确与否，完整性。
						//这个地方有数据类型的差异 可能会出错 要警惕。
						byte event = AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONDECT;
						byte status = 0x00;		
						WL_89_DoorLock_6.cHelperHawkeye.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ, SMsgAVIoctrlListEventReq.parseConent(Camera.DEFAULT_AV_CHANNEL, year,startTime, endTime,event,status));							
						
						
					} else {
						Log.i("IOTCamera", "-----------请选择具体时间");
						Message message = mHandler.obtainMessage();
						message.what = GalleryHawkEyeAlarmPage.FAIL_TO_GETDATA;
						mHandler.sendMessage(message);
					}
					
				}
			});
		}
		@SuppressLint("NewApi")
		public void show(View parent) {
			showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm,
			int frameCount, int incompleteFrameCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		
		
		if (WL_89_DoorLock_6.cHelperHawkeye == null)
			return;
		if (WL_89_DoorLock_6.cHelperHawkeye == camera) {
			Bundle bundle = new Bundle();
			bundle.putByteArray("listevent", data);

			Message msg = mHandler.obtainMessage();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
		
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int i, byte[] abyte0, int j, int k, byte[] abyte1,
			boolean flag, int l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showGallery(String date) {
		// TODO Auto-generated method stub
		
	}
}
