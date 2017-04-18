package cc.wulian.app.model.device.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.TaskInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.fragment.scene.AddDeviceToLinkTaskFragmentDialog;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class LinkTaskDustView extends AbstractLinkTaskView{

	@ViewInject(R.id.scene_link_task_more_head)
	private LinearLayout moreTaskHead;
	@ViewInject(R.id.scene_link_task_more_content)
	private LinearLayout moreTaskContent;
	@ViewInject(R.id.scene_link_task_add_more_ll)
	private LinearLayout addMoreTaskBtn;

	@ViewInject(R.id.scene_link_task_less_head)
	private LinearLayout lessTaskHead;
	@ViewInject(R.id.scene_link_task_less_content)
	private LinearLayout lessTaskContent;
	@ViewInject(R.id.scene_link_task_add_less_ll)
	private LinearLayout addLessTaskBtn;
	
	private WLDialog linkValueDialog;
	private View linkValueView;
	private TextView valueUnitText;
	private EditText valueEditText;
	private TextView valuedegreeText;
	private SeekBar valueSeekBar;
	private String mValueLink;
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == addMoreTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_OPEN);
				if (sensorDevice instanceof Sensorable) {
					sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_MORE);
				}
				List<TaskInfo> selectTasks = getTasksMore();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
			if (v == addLessTaskBtn) {
				TaskInfo sensorInfo = taskInfo.clone();
				sensorInfo.setContentID(TaskEntity.VALUE_CONTENT_CLOSE);
				if (sensorDevice instanceof Sensorable) {
					sensorInfo.setSensorCond(TaskEntity.VALUE_CONDITION_LESS);
				}
				List<TaskInfo> selectTasks = getTasksLess();
				AddDeviceToLinkTaskFragmentDialog.showDeviceDialog(context
						.getSupportFragmentManager(), context
						.getSupportFragmentManager().beginTransaction(),
						selectTasks, sensorInfo);
			}
		}
	};
	public LinkTaskDustView(BaseActivity context, TaskInfo info) {
		super(context, info);
	}

	@Override
	public View onCreateView() {
		rootView = inflater.inflate(R.layout.scene_edit_link_task_sensor_content,null);
		ViewUtils.inject(this, rootView);
		onViewCreated(rootView);
		return rootView;
	}

	@Override
	protected void onViewCreated(View view) {
		initSensorHeadContents();
		initContentViews();
		initListeners();
	}
	/**
	 * 初始化界面
	 */
	public void initContentViews() {
		initMoreContents();
		initLessContents();
	}

	@Override
	public void save() {
		List<TaskInfo> sensorTasks = linkGroup.getTaskSensorsList(taskInfo.getSensorID(), taskInfo.getSensorEp());
		if(sensorDevice instanceof Sensorable){
			for( int i=0;i<sensorTasks.size();i++){
				TaskInfo task = sensorTasks.get(i);
				task.setSensorData(taskInfo.getSensorData());
				JSONObject obj = new JSONObject();
				if(TaskEntity.VALUE_AVAILABL_YES.equals(task.getAvailable())){
					JsonTool.makeTaskJSONObject(obj,task,CmdUtil.MODE_UPD);
				}else{
					JsonTool.makeTaskJSONObject(obj,task,CmdUtil.MODE_DEL);
				}
				JSONArray array = new JSONArray();
				array.add(obj);
				NetSDK.sendSetTaskMsg(task.getGwID(), task.getSceneID(),
						task.getDevID(), task.getType(), task.getEp(),
						task.getEpType(), array);
			}
		}
	}
	/**
	 * 初始化警报头信息
	 */
	/**
	 * 清除头部信息
	 */
	private void clearHead() {
		moreTaskHead.removeAllViews();
		lessTaskHead.removeAllViews();
	}
	/**
	 * 初始化传感器头信息
	 */
	private void initSensorHeadContents() {
		clearHead();
		LinearLayout moreHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_sensor_head, null);
		TextView unitCompareMoreTextView = (TextView) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_compare);
		moreTaskHead.addView(initSensorHeadValues(moreHeadContent));

		LinearLayout lessHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_sensor_head, null);
		TextView unitCompareLessTextView = (TextView) lessHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_compare);
		lessTaskHead.addView(initSensorHeadValues(lessHeadContent));
		unitCompareMoreTextView.setText(">");
		unitCompareLessTextView.setText("<");
		final EditText unitMoreValueEditText = (EditText) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_value);
		unitMoreValueEditText.setText(taskInfo.getSensorData());
		final EditText unitLessValueEditText = (EditText) lessHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_value);
		unitLessValueEditText.setText(taskInfo.getSensorData());
		unitLessValueEditText.setEnabled(false);
		unitMoreValueEditText.setInputType(InputType.TYPE_NULL); 
		unitMoreValueEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showValueDialog(unitMoreValueEditText);
			}
		});
		unitMoreValueEditText.addTextChangedListener(new TextWatcher() {

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
				unitLessValueEditText.setText(unitMoreValueEditText
						.getText());
				taskInfo.setSensorData(unitMoreValueEditText.getText()
						.toString());
			}
		});
	}

	/**
	 * 设置数值对话框
	 */
	private void showValueDialog(final EditText editText){
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
		valueUnitText.setText("ug/m3");
		mValueLink = String.valueOf(0);
		valueSeekBar.setProgress(0);
		valueSeekBar.setMax(320);
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
		if(values >= 0 && values <= 80){
			valuedegreeText.setText(context.getString(R.string.device_link_task_detection_degree_a));
		}else if(values > 80 && values <= 120){
			valuedegreeText.setText(context.getString(R.string.device_link_task_detection_degree_b));
		}else if(values > 120 && values <= 180){
			valuedegreeText.setText(context.getString(R.string.device_link_task_detection_degree_mild));
		}else if(values > 180 && values <= 240){
			valuedegreeText.setText(context.getString(R.string.device_link_task_detection_degree_moderate));
		}else if(values > 240 && values <= 320){
			valuedegreeText.setText(context.getString(R.string.device_link_task_detection_degree_severe));
		}else{
			valuedegreeText.setText(context.getString(R.string.device_exception));
		}
	}

	/**
	 * 初始化
	 * 
	 * @param MoreHeadContent
	 * @return
	 */
	private LinearLayout initSensorHeadValues(LinearLayout moreHeadContent) {
		Sensorable sensor = (Sensorable) sensorDevice;
		TextView unitNameTextView = (TextView) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_name);
		unitNameTextView.setText(sensor.unitName());
		TextView unitSignalTextView = (TextView) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_signal);
		unitSignalTextView.setText(sensor.unit(sensorDevice.getDeviceInfo().getDevEPInfo().getEp(),sensorDevice.getDeviceInfo().getDevEPInfo().getEpType()));
		EditText unitValueEditText = (EditText) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_value);
		unitValueEditText.setText(taskInfo.getSensorData());
		return moreHeadContent;
	}

	/**
	 * 初始化警报任务列表
	 */
	private void initMoreContents() {
		moreTaskContent.removeAllViews();
		for (TaskInfo info : getTasksMore()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout moreLineLayout = initContentItem(info,
						moreTaskContent);
				moreTaskContent.addView(moreLineLayout);
			}

		}
	}

	/**
	 * 初始化正常任务列表
	 */
	private void initLessContents() {
		lessTaskContent.removeAllViews();
		for (TaskInfo info : getTasksLess()) {
			WulianDevice device = deviceCache.getDeviceByID(context,
					info.getGwID(), info.getDevID());
			if (device != null && device.isDeviceUseable()) {
				LinearLayout lessLineLayout = initContentItem(info,
						lessTaskContent);
				lessTaskContent.addView(lessLineLayout);
			}
		}
	}

	/**
	 * 初始化监听器
	 */
	private void initListeners() {
		addMoreTaskBtn.setOnClickListener(listener);
		addLessTaskBtn.setOnClickListener(listener);
	}
	/**
	 * 报警设备异常
	 * 
	 * @return
	 */
	private List<TaskInfo> getTasksMore() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for(TaskInfo info : linkGroup.getTaskSensorsList(taskInfo.getSensorID(),taskInfo.getSensorEp()) ){
			WulianDevice  device = deviceCache.getDeviceByIDEp(context,info.getGwID(), taskInfo.getSensorID(), taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if (device instanceof Sensorable) {
				if (TaskEntity.VALUE_CONDITION_MORE
						.equals(info.getSensorCond())) {
					tasks.add(info);
				}
			}
		}
		Collections.sort(tasks, compare);
		return tasks;
	}

	/**
	 * 报警设备正常
	 * 
	 * @return
	 */
	private List<TaskInfo> getTasksLess() {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for (TaskInfo info : linkGroup.getTaskSensorsList(
				taskInfo.getSensorID(), taskInfo.getSensorEp())) {
			WulianDevice device = deviceCache.getDeviceByIDEp(context,
					info.getGwID(), taskInfo.getSensorID(),
					taskInfo.getSensorEp());
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				continue;
			}
			if (device instanceof Sensorable) {
				if (TaskEntity.VALUE_CONDITION_LESS
						.equals(info.getSensorCond())) {
					tasks.add(info);
				}
			}
		}
		Collections.sort(tasks, compare);
		return tasks;
	}

}
