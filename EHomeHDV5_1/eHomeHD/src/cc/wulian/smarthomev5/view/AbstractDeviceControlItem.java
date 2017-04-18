package cc.wulian.smarthomev5.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.interfaces.ControlEPDataListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.Trans2PinYin;

public abstract class AbstractDeviceControlItem implements
		Comparable<AbstractDeviceControlItem> {
	protected static final String UNIT_MORE = "[";
	protected static final String UNIT_LESS = "]";
	protected static final String CONSTANT_COLOR_START = "<font color=#f31961>";
	protected static final String CONSTANT_COLOR_END = "</font>";

	protected LinearLayout lineLayout;
	protected LinearLayout contentLinearLayout;
	protected FrameLayout contentFrameLayout;
	protected ToggleButton mControlSwitch;
	protected ImageView mControlButton;
	protected ImageView mDeviceIconView;
	protected TextView mNameView;
	protected Button mDeleteView;
	protected WulianDevice mDevice;
	protected Resources mResources;
	protected LayoutInflater inflater;
	protected Context mContext;
	protected DeviceCache mDeviceCache;
	protected String deviceName;
	private Dialog contentViewDialog;

	public AbstractDeviceControlItem(Context context) {
		mContext = context;
		mDeviceCache = DeviceCache.getInstance(context);
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		lineLayout = (LinearLayout) inflater.inflate(
				R.layout.item_device_type_control, null);
		contentFrameLayout = (FrameLayout) lineLayout
				.findViewById(R.id.control_item_content_fl);
		contentLinearLayout = (LinearLayout) lineLayout
				.findViewById(R.id.control_item_content_ll);
		mControlSwitch = (ToggleButton) lineLayout
				.findViewById(R.id.control_switch);
		mControlButton = (ImageView) lineLayout
				.findViewById(R.id.control_button);
		mDeviceIconView = (ImageView) lineLayout.findViewById(R.id.device_icon);
		mNameView = (TextView) lineLayout.findViewById(R.id.device_name);
		mDeleteView = (Button) lineLayout
				.findViewById(R.id.control_item_del_iv);
	}

	public void setWulianDevice(WulianDevice device) {
		this.mDevice = device;
		refreshDeviceState(mDevice);
		initViews();
	}

	public View getView() {
		return lineLayout;
	}

	private void initViews() {
		contentViewDialog = mDevice.onCreateChooseContolEpDataView(inflater,getEP(),getEPData());
		if(mDevice instanceof Defenseable){
			initDefenseableView();
		}
		else if (mDevice instanceof Controlable) {
			initControlableView();
		} else {
			initCustomView();
		}
		contentLinearLayout.setOnTouchListener(new SwipeTouchViewListener(
				contentLinearLayout, mDeleteView));
	}

	private void initControlableView() {
		if(contentViewDialog == null){
			final Controlable control = (Controlable) mDevice;
			mControlSwitch.setVisibility(View.VISIBLE);
			if (control.getOpenProtocol().equals(getEPData())) {
				mControlSwitch.setChecked(true);
			} else {
				mControlSwitch.setChecked(false);
			}
			mControlSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							setEpData(control.getOpenProtocol());
						} else {
							setEpData(control.getCloseProtocol());
						}
					}
				});
		}else{
			initCustomView();
		}
	}

	private void initCustomView() {
		if(contentViewDialog == null){
			mControlButton.setVisibility(View.GONE);
			return ;
		}
		mControlButton.setVisibility(View.VISIBLE);
		mControlButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDevice.setControlEPDataListener(new ControlEPDataListener() {

					@Override
					public void onControlEPData(String epData) {
						setEpData(epData);
					}
				});
				contentViewDialog.show();
			}
		});
	}

	private void initDefenseableView() {
		if(contentViewDialog == null){
			final Defenseable defenseable = (Defenseable) mDevice;
			mControlSwitch.setVisibility(View.VISIBLE);
			mControlButton.setVisibility(View.GONE);
			if (defenseable.getDefenseSetupProtocol().equals(getEPData())) {
				mControlSwitch.setChecked(true);
			} else {
				mControlSwitch.setChecked(false);
			}
			mControlSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					if (isChecked) {
						setEpData(defenseable.getDefenseSetupProtocol());
					} else {
						setEpData(defenseable.getDefenseUnSetupProtocol());
					}
				}
			});
		}else{
			initCustomView();
		}
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public abstract void setEpData(String epData);

	public abstract String getEPData();

	private void refreshDeviceState(WulianDevice device) {
		setDeviceIcon(device.getStateSmallIcon());
		setDeviceName();
	}

	private void setDeviceIcon(Drawable icon) {
		// if is offline, make icon grayscale
		if (!mDevice.isDeviceOnLine()) {
			icon = DisplayUtil.toGrayscaleDrawable(mContext, icon);
			if(icon!=null){
				icon.setAlpha(150);
			}
		}
		mDeviceIconView.setImageDrawable(icon);
	}

	private void setDeviceName() {
		Resources resources = mContext.getResources();
		StringBuilder sb = new StringBuilder();
		if (!mDevice.isDeviceOnLine()) {
			sb.append(UNIT_MORE);
			// use spannable String to instead of this
			sb.append(CONSTANT_COLOR_START);
			sb.append(resources.getString(R.string.device_offline));
			sb.append(CONSTANT_COLOR_END);
			sb.append(UNIT_LESS);
		}

		sb.append(DeviceTool.getDeviceShowName(mDevice));
		sb.append("-");
		sb.append(DeviceUtil.ep2IndexString(getEP()));
		deviceName = sb.toString();
		mNameView.setText(mDevice.isDeviceOnLine() ? sb.toString() : Html
				.fromHtml(sb.toString()));
	}

	public abstract String getEP();
	@Override
	public int compareTo(AbstractDeviceControlItem another) {
		int result = 0;
		result = Trans2PinYin.trans2PinYin(this.getDeviceName()).compareTo(Trans2PinYin.trans2PinYin(another.getDeviceName()));
		return result;
	}

	public Button getmDeleteView() {
		return mDeleteView;
	}

}
