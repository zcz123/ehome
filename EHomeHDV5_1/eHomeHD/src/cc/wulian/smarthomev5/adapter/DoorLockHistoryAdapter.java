package cc.wulian.smarthomev5.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class DoorLockHistoryAdapter extends AlarmMessageClickedAdapter {

	private View tocDialog;
	private WLDialog dialog;
	// private ImageView monitorimage;
	private List<CameraInfo> data;
	private CameraDao cameraDao = CameraDao.getInstance();
	private CameraInfo mCurrentInfo;

	private AreaGroupManager areaGroupManager = AreaGroupManager.getInstance();
	private final DeviceCache mCache;
	private boolean mIsEditingMode = false;
	private Map<Integer, State> stateMap = new HashMap<Integer, State>();

	public DoorLockHistoryAdapter(Context context, List<MessageEventEntity> data) {
		super(context, data);
		mCache = DeviceCache.getInstance(context);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.fragment_message_alarm_clicked_item,
				null);
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			final MessageEventEntity item) {
		super.bindView(context, view, pos, item);
		String epData = item.getEpData();
		String userType = epData.substring(4, 6);
		String userNumber = epData.substring(6, 8);
		String openType = epData.substring(8, 10);
		String userName = epData.substring(10);
		final TextView mTextView = (TextView) view
				.findViewById(R.id.detail_message);
		final TextView mTimeView = (TextView) view
				.findViewById(R.id.message_time);
		final ImageView delateimage = (ImageView) view
				.findViewById(R.id.delate_image);
		delateimage.setVisibility(View.VISIBLE);
		if (userName.equals("") || userName == null) {
			switch (userType) {
			case "00":
				mTextView.setText(mContext.getResources().getString(R.string.device_lock_user_manager) + userNumber);
				break;
			case "01":
				mTextView.setText(mContext.getResources().getString(R.string.device_lock_user_common) + userNumber);
				break;
			case "02":
				mTextView.setText(mContext.getResources().getString(R.string.device_lock_user_temp) + userNumber);
				break;
			default:
				mTextView.setText(mContext.getResources().getString(R.string.device_user)+ userNumber);
				break;
			}
		}else{
			switch (userType) {
				case "00":
					mTextView.setText(mContext.getResources().getString(R.string.device_lock_user_manager) + userName);
					break;
				case "01":
					mTextView.setText(mContext.getResources().getString(R.string.device_lock_user_common) + userName);
					break;
				case "02":
					mTextView.setText(mContext.getResources().getString(R.string.device_lock_user_temp) + userName);
					break;
				default:
					mTextView.setText(mContext.getResources().getString(R.string.device_user)+ userName);
					break;
			}
		}
		
		switch (openType) {
		case "00":
			delateimage.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_door_lock_open_pass));
			break;
		case "01":
			delateimage.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_door_lock_open_finger));
			break;
		case "02":
			delateimage.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_door_lock_open_card));
			break;
		case "03":
			delateimage.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_door_lock_open_app));
			break;
		}
		
	}

}
