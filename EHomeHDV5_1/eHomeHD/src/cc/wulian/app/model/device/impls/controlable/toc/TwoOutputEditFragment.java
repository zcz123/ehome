package cc.wulian.app.model.device.impls.controlable.toc;

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
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.controlable.toc.DeviceTwoOutputFragment.TwoOutputConverterAddAdapter;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.wheel.toc.ArrayWheelAdapter;
import com.yuantuo.customview.wheel.toc.OnWheelScrollListener;
import com.yuantuo.customview.wheel.toc.WheelView;

public class TwoOutputEditFragment extends DialogFragment {

	private DeviceTwoOutputFragment mdtoFragment;
	private static TwoOutputEntity mEntity;

	private static final String TAG = TwoOutputEditFragment.class
			.getSimpleName();

	public static void showEditDialog(FragmentManager fm,
			FragmentTransaction ft, DeviceTwoOutputFragment dtoFragment,
			TwoOutputEntity entity, int postion) {
		DialogFragment df = (DialogFragment) fm.findFragmentByTag(TAG);
		if (df != null) {
			if (!df.getDialog().isShowing()) {
				ft.remove(df);
			} else {
				return;
			}
		}
		mEntity = entity;
		TwoOutputEditFragment fragment = new TwoOutputEditFragment();
		fragment.mdtoFragment = dtoFragment;
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return createDialog();
	}

	private View toeDialog;
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

	public Dialog createDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.device_edit));
		builder.setContentView(createCustomView());
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				TwoOutputConverterAddAdapter tocAdapter = new TwoOutputConverterAddAdapter(
						getActivity(), null);
				String onetypestr = String.valueOf(onetype.getCurrentItem());
				String twotypestr = String.valueOf(twotype.getCurrentItem());
				String onevaluestr = String.valueOf(oneSeekBar.getProgress());
				String twovaluestr = String.valueOf(twoSeekBar.getProgress());

				mEntity.setOneType(onetypestr);
				mEntity.setOneValue(onevaluestr);
				mEntity.setTwoType(twotypestr);
				mEntity.setTwoValue(twovaluestr);

				tocAdapter.updateItemData(mEntity);

				dialog.dismiss();

			}

			@Override
			public void onClickNegative(View contentViewLayout) {
				dialog.dismiss();

			}
		});
		dialog = builder.create();
		return dialog;
	}

	public View createCustomView() {
		toeDialog = View.inflate(getActivity(),
				R.layout.device_two_output_converter_create_new_dialog, null);

		mkeyname = (EditText) toeDialog
				.findViewById(R.id.device_two_output_converter_key_name);
		mkeyname.setVisibility(View.GONE);
		// one
		onetype = (WheelView) toeDialog
				.findViewById(R.id.device_two_output_converter_one_type);
		oneSeekBar = (SeekBar) toeDialog
				.findViewById(R.id.device_two_output_converter_one_seekbar);
		oneSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		onevalue = (TextView) toeDialog
				.findViewById(R.id.device_two_output_converter_one_value);
		// two
		twotype = (WheelView) toeDialog
				.findViewById(R.id.device_two_output_converter_two_type);
		twoSeekBar = (SeekBar) toeDialog
				.findViewById(R.id.device_two_output_converter_two_seekbar);
		twoSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		twovalue = (TextView) toeDialog
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
		twotype.setViewAdapter(mAdapter);

		int i = Integer.valueOf(mEntity.getOneType());
		onetype.setCurrentItem(i);
		updateSeekBar(oneSeekBar, i, onevalue, mEntity.getOneValue());
		int j = Integer.valueOf(mEntity.getTwoType());
		twotype.setCurrentItem(j);
		updateSeekBar(twoSeekBar, j, twovalue, mEntity.getTwoValue());

		onetype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {

				boolean oneSame = mEntity.getOneType().equals(
						String.valueOf(onetype.getCurrentItem()));

				if (onetype.getCurrentItem() == 1
						| onetype.getCurrentItem() == 2
						| onetype.getCurrentItem() == 3) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(false);

				} else if (onetype.getCurrentItem() == 0
						| onetype.getCurrentItem() == 4) {

					if (oneSame) {
						oneSeekBar.setProgress(Integer.valueOf(mEntity
								.getOneValue()));
						onevalue.setText(mEntity.getOneValue() + "s");

					} else {
						oneSeekBar.setProgress(0);
						onevalue.setText(0 + "s");

					}
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

				boolean twoSame = mEntity.getTwoType().equals(
						String.valueOf(twotype.getCurrentItem()));

				if (twotype.getCurrentItem() == 1
						| twotype.getCurrentItem() == 2
						| twotype.getCurrentItem() == 3) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(false);

				} else if (twotype.getCurrentItem() == 0
						| twotype.getCurrentItem() == 4) {
					if (twoSame) {
						twoSeekBar.setProgress(Integer.valueOf(mEntity
								.getTwoValue()));
						twovalue.setText(mEntity.getTwoValue() + "s");

					} else {
						twoSeekBar.setProgress(0);
						twovalue.setText(0 + "s");

					}
					twoSeekBar.setEnabled(true);

				}
			}
		});

		return toeDialog;
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

	public void updateSeekBar(SeekBar seekbar, int item, TextView textView,
			String str) {

		switch (item) {
		case 0:
			seekbar.setProgress(Integer.valueOf(str));
			textView.setText(str + "s");
			seekbar.setEnabled(true);
			break;
		case 1:
			seekbar.setProgress(0);
			textView.setText(0 + "s");
			seekbar.setEnabled(false);
			break;
		case 2:
			seekbar.setProgress(0);
			textView.setText(0 + "s");
			seekbar.setEnabled(false);
			break;
		case 3:
			seekbar.setProgress(0);
			textView.setText(0 + "s");
			seekbar.setEnabled(false);
			break;
		case 4:
			seekbar.setProgress(Integer.valueOf(str));
			textView.setText(str + "s");
			seekbar.setEnabled(true);
			break;

		default:
			break;
		}

	}

}
