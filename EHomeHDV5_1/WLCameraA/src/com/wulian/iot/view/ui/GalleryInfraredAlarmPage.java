package com.wulian.iot.view.ui;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord;
import com.wulian.icam.R;
import com.wulian.icam.utils.StringUtil;
import com.wulian.iot.Config;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.utils.EagleUtil;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.adapter.AlarmListViewAdapter;
import com.wulian.iot.view.base.BasePage;
import com.wulian.iot.view.device.play.PlayEagleVideoAvtivity;
import com.wulian.iot.widght.DatePickerPopWindow;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

public class GalleryInfraredAlarmPage extends BasePage implements OnClickListener, Callback,OnItemClickListener{
    private final static String TAG = "GalleryInfraredAlarmPage";
	private DatePickerPopWindow datePickerPopWindow = null;
	private ImageView showPopuDate;
	private ListView alarmListView;
	private LinearLayout mLinPopu;
	private AlarmListViewAdapter mListAdapter;
	private List<GalleryAlarmInfo> mList;
	private Handler mHandler=new Handler(this);
	private static TKCamHelper mCamera = null;
	private CameraHelper cHelper=null;
	private final int mSelectedChannel = 0;
	private List<GalleryAlarmInfo> galleryAlarmInfoList=new ArrayList<>();
	private CameraHelper.Observer observer = new CameraHelper.Observer() {
		@Override
		public void avIOCtrlOnLine() {
		}
		@Override
		public void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType) {
			Message ms=mHandler.obtainMessage();
			Bundle bundle=new Bundle();
			ms.what=avIOCtrlMsgType;
			ms.setData(bundle);
			bundle.putByteArray("listevent",data);
			mHandler.sendMessage(ms);
		}
		@Override
		public void avIOCtrlMsg(int resCode,String method) {
		}
	};
	@Override
	public boolean handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		byte[] data = bundle.getByteArray("listevent");
		switch (msg.what) {
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP:
			break;
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_RESP:
			if (data.length > 8) {
				mList= EagleUtil.parsePlayBackFileInfo(data);
				if(mList !=null){
//					for(GalleryAlarmInfo info:mList){
//						galleryAlarmInfoList.add(info);
//					}
					galleryAlarmInfoList.addAll(mList);
					swapAdapterData(galleryAlarmInfoList);
				    break;
				}
			}
			WLToast.showToast(context, context.getResources().getString(R.string.eagle_alarm_null), Toast.LENGTH_SHORT);
			break;
		default:
			break;
		}
		return false;
	}
	@SuppressLint("LongLogTag")
	public GalleryInfraredAlarmPage(Context context,CameraHelper mCamHelper) {
		super(context);
		cHelper = mCamHelper;
		Log.e(TAG, "content");
	}
	@SuppressLint("LongLogTag")
	@Override
	public View initView() {
		Log.e(TAG, "initView");
		View v=View.inflate(context, R.layout.page_gallery_infraredalarm, null);
		showPopuDate=(ImageView) v.findViewById(R.id.iv_alarm_date);
		alarmListView=(ListView) v.findViewById(R.id.alarm_show_video_time);
		mLinPopu=(LinearLayout) v.findViewById(R.id.lin_alram_show_popu_parent);
		return v;
	}
	@SuppressLint("LongLogTag")
	@Override
	public void initEvents() {
		Log.e(TAG, "initEvents");
		showPopuDate.setOnClickListener(this);
		alarmListView.setOnItemClickListener(this);
		bindAdapter();
	}
	@SuppressLint("LongLogTag")
	@Override
	public void initData() {
		Log.e(TAG, "initData");
		if(cHelper !=null){
			mCamera = cHelper.getmCamera();
			if(cHelper.checkSession()){
				cHelper.attach(observer);
				showGallery(IotUtil.longToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss").substring(0, 10));
			}
		}
	}
	@Override
	public void onClick(View v) {
		if (v==showPopuDate) {
			if (datePickerPopWindow==null) {
				datePickerPopWindow=new DatePickerPopWindow(context){
					@Override
					public void callBackData(String data) {
						GalleryInfraredAlarmPage.this.showGallery(data);
						datePickerPopWindow.dismiss();
						galleryAlarmInfoList.clear();
					}
				};
			}
			datePickerPopWindow.show(mLinPopu);
		}
	}
	private void bindAdapter(){
		if(mListAdapter == null){
			mListAdapter=new AlarmListViewAdapter(context, null);
			alarmListView.setAdapter(mListAdapter);
		}
	}
	private void swapAdapterData(List<GalleryAlarmInfo> mList){
		if(mListAdapter!=null){
			mListAdapter.swapData(mList);
		}
	}
	@SuppressLint("LongLogTag")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent mIntent = new Intent(context,PlayEagleVideoAvtivity.class);
		mIntent.putExtra(Config.playEagleVideoTyep, "server");
		mIntent.putExtra("videoPath", galleryAlarmInfoList.get(position));
		context.startActivity(mIntent);
		Log.e(TAG, "onItemClick"+galleryAlarmInfoList.get(position));
		Log.e(TAG, "onItemClick");
	}
	private void stopPlayBackFile() {
		// TODO Auto-generated method stub+
		byte[] startTimeAck =new byte[]{0x06,0x04,0x00,0x9,0x31,0x04};            
		short year = 2016;
		mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP, year,startTimeAck));
	}
	private void setPlayBackFile() {
		// TODO Auto-generated method stub
		byte[] startTimeAck =new byte[]{0x06,0x04,0x00,0x9,0x31,0x04};            
		short year = 2016;
		mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,year, startTimeAck));
	}
	@SuppressLint("LongLogTag")
	@Override
	public void showGallery(String date) {
		/*****************测试所用**************************/
		Log.i(TAG,"srarchTime("+date+")" ); //srarchTime(2016-12-15)
		String month="-1",day="-1";
		short year=-1;
		if (!StringUtil.isNullOrEmpty(date)) {
			Map<String, String>map=EagleUtil.getDate(date);
			month=map.get("month");
			day=map.get("day");
			year=((Integer)Integer.parseInt(map.get("year"))).shortValue();
			Log.i(TAG, "time"+year+"--"+"--"+month+"--"+day);
		}else {
			return;
		}
		String starttime=DateUtil.timeToZeroZone(year+"-"+month+"-"+day+" "+"00:00:01");
		String endtime=DateUtil.timeToZeroZone(year+"-"+month+"-"+day+" "+"23:59:59");

//		byte[] startTime =new byte[]{EagleUtil.convertToByte(month),EagleUtil.convertToByte(day),0x00,0x00,0x00,0x01};
//		byte[] endTime =new byte[]{EagleUtil.convertToByte(month),EagleUtil.convertToByte(day),0x00,0x17,0x3b,0x3b};
		byte[] startTime =EagleUtil.timeTobyte(starttime);
		byte[] endTime =EagleUtil.timeTobyte(endtime);
		byte event = AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONDECT;
		byte status = 0x00;
		/*****************测试所用**************************/
		IotSendOrder.findEagleWifiByEvent(mCamera, startTime, endTime, year, event, status);
	}
}
