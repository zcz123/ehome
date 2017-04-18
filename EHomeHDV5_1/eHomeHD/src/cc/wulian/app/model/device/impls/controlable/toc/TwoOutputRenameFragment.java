package cc.wulian.app.model.device.impls.controlable.toc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.controlable.toc.DeviceTwoOutputFragment.TwoOutputConverterAddAdapter;
import cc.wulian.ihome.wan.util.StringUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class TwoOutputRenameFragment extends DialogFragment {

	private DeviceTwoOutputFragment mdtoFragment;
	private static TwoOutputEntity mEntity;
	private static String currentName;
	private static int pos;

	private static final String TAG = TwoOutputRenameFragment.class
			.getSimpleName();

	public static void showRenameDialog(FragmentManager fm,
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
		currentName = entity.keyName;
		pos = postion;
		TwoOutputRenameFragment fragment = new TwoOutputRenameFragment();
		fragment.mdtoFragment = dtoFragment;
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return createDialog();
	}

	private View torDialog;
	private EditText mEditText;
	private WLDialog dialog;

	private Dialog createDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.device_modify_name));
		builder.setContentView(createCustomView());
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {
			TwoOutputConverterAddAdapter tocAdapter = new TwoOutputConverterAddAdapter(
					getActivity(), null);

			@Override
			public void onClickPositive(View contentViewLayout) {
				String str = mEditText.getText().toString();
				if (!StringUtil.isNullOrEmpty(str)) {
					mEntity.setKeyName(str);
					tocAdapter.modifyItemName(mEntity);
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
		torDialog = View.inflate(getActivity(),
				R.layout.device_two_output_converter_rename, null);
		mEditText = (EditText) torDialog
				.findViewById(R.id.device_two_output_converter_rename);
		mEditText.setText(currentName);
		mEditText.setSelectAllOnFocus(true);
		return torDialog;

	}

}
