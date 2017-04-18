package cc.wulian.app.model.device.impls.controlable.toc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.toc.DeviceTwoOutputFragment.TwoOutputConverterAddAdapter;
import cc.wulian.ihome.wan.util.StringUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.wheel.toc.ArrayWheelAdapter;
import com.yuantuo.customview.wheel.toc.OnWheelScrollListener;
import com.yuantuo.customview.wheel.toc.WheelView;

public class TwoOutputCreateFragment extends DialogFragment {

	private DeviceTwoOutputFragment mdtoFragment;
	private static String gwID;
	private static String devID;

	private static final String TAG = TwoOutputCreateFragment.class
			.getSimpleName();

	public static void showDeviceDialog(FragmentManager fm,
			FragmentTransaction ft, DeviceTwoOutputFragment dtoFragment,
			WulianDevice deviceInfo) {
		DialogFragment df = (DialogFragment) fm.findFragmentByTag(TAG);
		if (df != null) {
			if (!df.getDialog().isShowing()) {
				ft.remove(df);
			} else {
				return;
			}
		}

		gwID = deviceInfo.getDeviceGwID();
		devID = deviceInfo.getDeviceID();
		TwoOutputCreateFragment fragment = new TwoOutputCreateFragment();
		fragment.mdtoFragment = dtoFragment;
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return createDialog();
	}

	private View tocDialog;
	private String[] keytype;
	private TOCAdapter mAdapter;
	private EditText mkeyname;
	private WheelView onetype;
	private SeekBar oneSeekBar;
	private TextView onevalue;
	private WheelView twotype;
	private SeekBar twoSeekBar;
	private TextView twovalue;
	private WLDialog dialog;

	private Dialog createDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.device_create_key));
		builder.setContentView(createCustomView());
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				TwoOutputEntity entity = new TwoOutputEntity();
				TwoOutputConverterAddAdapter tocAdapter = new TwoOutputConverterAddAdapter(
						getActivity(), null);

				String keyName = mkeyname.getText().toString();
				String oneType = String.valueOf(onetype.getCurrentItem());
				String oneValue = String.valueOf(oneSeekBar.getProgress());
				String twoType = String.valueOf(twotype.getCurrentItem());
				String twoValue = String.valueOf(twoSeekBar.getProgress());

				entity.setGwID(gwID);
				entity.setDevID(devID);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Date().getTime());
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String currentTime = dateFormat.format(c.getTime());
				entity.setKeyID(currentTime);
				entity.setKeyName(keyName);
				entity.setOneType(oneType);
				entity.setOneValue(oneValue);
				entity.setTwoType(twoType);
				entity.setTwoValue(twoValue);

				if (!StringUtil.isNullOrEmpty(keyName)) {
					tocAdapter.addItemData(entity);
					mdtoFragment.loaderTwoOutputListInfo();
					dialog.dismiss();

				} else {
					Toast.makeText(
							getActivity(),
							getResources().getString(
									R.string.device_key_name_not_null),
							Toast.LENGTH_SHORT).show();

				}

			}

			@Override
			public void onClickNegative(View contentViewLayout) {
				dialog.dismiss();

			}
		});

		dialog = builder.create();
		return dialog;
	}

	private View createCustomView() {

		tocDialog = View.inflate(getActivity(),
				R.layout.device_two_output_converter_create_new_dialog, null);

		mkeyname = (EditText) tocDialog
				.findViewById(R.id.device_two_output_converter_key_name);
		// one
		onetype = (WheelView) tocDialog
				.findViewById(R.id.device_two_output_converter_one_type);
		oneSeekBar = (SeekBar) tocDialog
				.findViewById(R.id.device_two_output_converter_one_seekbar);
		oneSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		onevalue = (TextView) tocDialog
				.findViewById(R.id.device_two_output_converter_one_value);
		// two
		twotype = (WheelView) tocDialog
				.findViewById(R.id.device_two_output_converter_two_type);
		twoSeekBar = (SeekBar) tocDialog
				.findViewById(R.id.device_two_output_converter_two_seekbar);
		twoSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		twovalue = (TextView) tocDialog
				.findViewById(R.id.device_two_output_converter_two_value);

		// wheel
		onetype.setVisibleItems(5);
		twotype.setVisibleItems(5);
		keytype = new String[] {
				getResources().getString(R.string.device_two_output_often_open),
				getResources().getString(R.string.device_state_open),
				getResources().getString(R.string.device_two_output_none),
				getResources().getString(R.string.device_state_close),
				getResources()
						.getString(R.string.device_two_output_often_close) };
		mAdapter = new TOCAdapter(getActivity(), keytype);
		onetype.setViewAdapter(mAdapter);
		onetype.setCurrentItem(2);
		oneSeekBar.setEnabled(false);

		twotype.setViewAdapter(mAdapter);
		twotype.setCurrentItem(2);
		twoSeekBar.setEnabled(false);

		onetype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (onetype.getCurrentItem() == 1
						| onetype.getCurrentItem() == 2
						| onetype.getCurrentItem() == 3) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(false);

				} else if (onetype.getCurrentItem() == 0
						| onetype.getCurrentItem() == 4) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(true);

				}
			}
		});

		twotype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (twotype.getCurrentItem() == 1
						| twotype.getCurrentItem() == 2
						| twotype.getCurrentItem() == 3) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(false);

				} else if (twotype.getCurrentItem() == 0
						| twotype.getCurrentItem() == 4) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(true);

				}
			}
		});

		return tocDialog;
	}

	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			if (arg0 == oneSeekBar) {
				onevalue.setText(oneSeekBar.getProgress() + "s");

			} else if (arg0 == twoSeekBar) {
				twovalue.setText(twoSeekBar.getProgress() + "s");

			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

		}
	};

	public class TOCAdapter extends ArrayWheelAdapter<String> {
		// Index of current item
		int currentItem;

		// Index of item to be highlighted
		// int currentValue;

		public TOCAdapter(Context context, String[] items) {
			super(context, items);
			setTextSize(16);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			// if (currentItem == currentValue) {
			// view.setTextColor(0xFF0000F0);
			// }
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

}
