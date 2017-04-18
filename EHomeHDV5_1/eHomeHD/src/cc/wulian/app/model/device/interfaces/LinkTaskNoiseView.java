package cc.wulian.app.model.device.interfaces;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class LinkTaskNoiseView extends AbstractSensorLinkTaskView{

	public LinkTaskNoiseView(BaseActivity context, TaskInfo info) {
		super(context, info);
	}
	/**
	 * 设置数值对话框
	 */
	@Override
	public void showValueDialog(final EditText editText) {
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setContentView(createLinkView());
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				editText.setText(mValueLink);
			}
			
			public void onClickNegative(View contentViewLayout) {
				linkValueDialog.dismiss();
			}
		});
		linkValueDialog = builder.create();
		linkValueDialog.show();
	
	}
	
	private View createLinkView() {
		linkValueView = inflater.inflate(R.layout.scene_link_sensorale_value_layout, null);
		valueEditText = (EditText) linkValueView.findViewById(R.id.scene_link_sensor_values_edit);
		valuedegreeText = (TextView) linkValueView.findViewById(R.id.scene_link_sensor_values_degree);
		valueUnitText = (TextView) linkValueView.findViewById(R.id.scene_link_sensor_values_unit);
		valueSeekBar = (SeekBar) linkValueView.findViewById(R.id.scene_link_sensor_values_seekbar);
		valueEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
		valueEditText.setSelection(valueEditText.getText().toString().length());
		valueUnitText.setText("db");
		mValueLink = String.valueOf(0);
		valueSeekBar.setProgress(0);
		valueSeekBar.setMax(65);
		changeTextView();
		valueEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int progress = (int) Math.floor(StringUtil.toInteger(valueEditText.getText().toString()));
				valueSeekBar.setProgress(progress);
				valueEditText.setSelection(valueEditText.getText().toString().length());
			}
		});
		
		valueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				seekBar.setProgress(mSeekProgress);
				mValueLink = String.valueOf(mSeekProgress);
				changeTextView();
				valueEditText.setText(mValueLink);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				seekBar.setProgress(progress);
				mValueLink = String.valueOf(progress);
				changeTextView();
				valueEditText.setText(mValueLink);
			}
		});
		
		return linkValueView;
	}
	
	private void changeTextView(){
		int values = StringUtil.toInteger(mValueLink);
		if(values >= 0 && values <= 35){
			valuedegreeText.setText(context.getString(R.string.device_d4_quiet));
		}else if(values > 35 && values < 65){
			valuedegreeText.setText(context.getString(R.string.scene_normal_hint));
		}else{
			valuedegreeText.setText(context.getString(R.string.device_d4_noisy));
		}
	}
}
