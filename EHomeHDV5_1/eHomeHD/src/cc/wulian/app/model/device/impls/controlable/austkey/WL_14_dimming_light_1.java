package cc.wulian.app.model.device.impls.controlable.austkey;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.seekcircle.SeekCircle;
import com.yuantuo.customview.seekcircle.SeekCircle.OnSeekCircleChangeListener;
import com.yuantuo.customview.ui.WLToast;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_AUS_DIMMING_LIGHT }, category = Category.C_OTHER)
public class WL_14_dimming_light_1 extends AbstractAustKeyDevice {
	private static final String[] EP_LIGHT = { EP_14 };
	private static final String[] EP_SEQUENCE = { EP_15 };
	private static String CMD_OPEN = "255";
	private static String CMD_CLOSE = "000";
	private TextView sceneTextView;
	private LinearLayout sceneLineLayout;
	private ImageView sceneImageView;
	private SeekCircle seekBarCircle;
	private TextView processTextView;
	private TextView onTextView;
	private TextView offTextView;
	public WL_14_dimming_light_1(Context context, String type) {
		super(context, type);
	}

	@Override
	public String[] getLightEPInfo() {
		return EP_LIGHT;
	}

	@Override
	public String[] getSceneSwitchEPResources() {
		return EP_SEQUENCE;
	}

	@Override
	public String[] getSceneSwitchEPNames() {
		String ep15Name = getResources().getString(R.string.device_bind_scene);
		return new String[] { ep15Name };
	}

	@Override
	public String[] getSwitchEPName() {
		String ep14Name = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo()
				.getEpName();
		if (StringUtil.isNullOrEmpty(ep14Name)) {
			ep14Name = getResources().getString(
					R.string.device_type_14);
		}
		return new String[] { ep14Name };
	}
	@Override
	public String getOpenSendCmd() {
		return CMD_OPEN;
	}

	@Override
	public String getCloseSendCmd() {
		return CMD_CLOSE;
	}

	@Override
	public String getOpenProtocol() {
		return CMD_OPEN;
	}

	@Override
	public String getCloseProtocol() {
		return CMD_CLOSE;
	}

	@Override
	public boolean isOpened() {
		WulianDevice device = getChildDevice(EP_14);
		if(device instanceof Controlable){
			Controlable control= (Controlable)device;
			return control.isOpened();
		}
		return false;
	}

	@Override
	public boolean isClosed() {
		return !isOpened();
	}
	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_type_14));
		return sb.toString();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle saveState) {
		return inflater.inflate(R.layout.device_aus_dimming_light, null);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		getBindScenesMap();
		sceneLineLayout = (LinearLayout)view.findViewById(R.id.device_aus_scene_ll);
		sceneImageView = (ImageView)view.findViewById(R.id.device_aust_scene_iv);
		seekBarCircle = (SeekCircle)view.findViewById(R.id.device_aus_dimming_light_sc);
		onTextView = (TextView)view.findViewById(R.id.device_aus_on_tv);
		offTextView = (TextView)view.findViewById(R.id.device_aus_off_tv);
		processTextView = (TextView)view.findViewById(R.id.device_aus_process_tv);
		sceneTextView = (TextView)view.findViewById(R.id.device_aus_scene_tv);
		sceneLineLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SceneInfo info = bindScenesMap.get(ep);
				if(info != null){
					SendMessage.sendSetSceneMsg(mContext, info.getGwID(),
							CmdUtil.MODE_SWITCH, info.getSceneID(), null,
							null, CmdUtil.SCENE_USING, true);
				}else{
					WLToast.showToast(mContext, getString(R.string.device_no_bind_scene), WLToast.TOAST_SHORT);
				}
			}
		});
		seekBarCircle.setOnSeekCircleChangeListener(new OnSeekCircleChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekCircle seekCircle) {
				fireWulianDeviceRequestControlSelf();
				WulianDevice device = getChildDevice(EP_14);
				int process = seekCircle.getProgress();
				controlDevice(EP_14, device.getDeviceInfo().getDevEPInfo().getEpType(), StringUtil.appendLeft((int)(process/100.0*255)+"", 3, '0'));
			}

			@Override
			public void onStartTrackingTouch(SeekCircle seekCircle) {
				onTextView.setVisibility(View.GONE);
				offTextView.setVisibility(View.GONE);
				processTextView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onProgressChanged(SeekCircle seekCircle, int progress,
										  boolean fromUser) {
				processTextView.setText(progress+"%");
			}
		});
		onTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fireWulianDeviceRequestControlSelf();
				WulianDevice device = getChildDevice(EP_14);
				controlDevice(EP_14, device.getDeviceInfo().getDevEPInfo().getEpType(), CMD_OPEN);
			}
		});
		offTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fireWulianDeviceRequestControlSelf();
				WulianDevice device = getChildDevice(EP_14);
				controlDevice(EP_14, device.getDeviceInfo().getDevEPInfo().getEpType(),CMD_CLOSE);
			}
		});
		mViewCreated = true;
	}

	@Override
	public void initViewStatus() {
		SceneInfo info = bindScenesMap.get(EP_15);
		if(info != null){
			sceneImageView.setImageDrawable(SceneManager.getSceneIconDrawable_Light_Small(mContext, info.getIcon()));
			sceneTextView.setText(info.getName());
		}else{
			sceneTextView.setText(R.string.device_no_bind_scene);
		}
		WulianDevice device = getChildDevice(EP_14);
		if(isOpened()){
			onTextView.setVisibility(View.GONE);
			offTextView.setVisibility(View.VISIBLE);
			processTextView.setVisibility(View.GONE);
		}else{
			onTextView.setVisibility(View.VISIBLE);
			offTextView.setVisibility(View.GONE);
			processTextView.setVisibility(View.GONE);
		}
		seekBarCircle.setProgress((int)(StringUtil.toInteger(device.getDeviceInfo().getDevEPInfo().getEpData(), 16)/255.0*100));
	}

}
