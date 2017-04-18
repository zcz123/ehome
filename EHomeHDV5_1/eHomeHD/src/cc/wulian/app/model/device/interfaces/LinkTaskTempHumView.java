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

public class LinkTaskTempHumView extends AbstractLinkTaskView{
	public static final String  TYPE_TEMP = "1702";
	private static final String TYPE_HUM = "1703";
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
	public LinkTaskTempHumView(BaseActivity context, TaskInfo info) {
		super(context, info);
	}

	private String convertSensorType(String type){
		String result = type;
		if(!TYPE_TEMP.equals(type) && !TYPE_HUM.equals(type)){
			result = TYPE_TEMP;
		}
		return result;
	}
	@Override
	public View onCreateView() {
		rootView = inflater.inflate(R.layout.scene_edit_link_task_temp_hum_content,null);
		ViewUtils.inject(this, rootView);
		this.taskInfo.setSensorType(convertSensorType(this.taskInfo.getSensorType()));
		onViewCreated(rootView);
		return rootView;
	}

	@Override
	protected void onViewCreated(View view) {
		initContentViews();
		initTempHumHeadContents();
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
				if(!this.taskInfo.getSensorType().equals(task.getSensorType())){
					continue;
				}
				task.setSensorData(taskInfo.getSensorData());//88
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
	private void initTempHumHeadContents() {
		clearHead();
		LinearLayout moreHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_temp_hum_head, null);
		moreTaskHead.addView(initSensorHeadValues(moreHeadContent));
		TextView unitCompareMoreTextView = (TextView) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_compare);
		unitCompareMoreTextView.setText(">");
		final EditText unitMoreValueEditText = (EditText) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_value);
		unitMoreValueEditText.setText(taskInfo.getSensorData());
		TextView moreTempUnitTextView = (TextView)moreHeadContent.findViewById(R.id.scene_link_task_temp_unit_name);
		TextView moreHumUnitTextView = (TextView)moreHeadContent.findViewById(R.id.scene_link_task_humidity_unit_name);
		
		
		LinearLayout lessHeadContent = (LinearLayout) inflater.inflate(
				R.layout.scene_edit_link_task_temp_hum_head, null);
		lessTaskHead.addView(initSensorHeadValues(lessHeadContent));
		TextView unitCompareLessTextView = (TextView) lessHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_compare);
		unitCompareLessTextView.setText("<");
		final EditText unitLessValueEditText = (EditText) lessHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_value);
		unitLessValueEditText.setText(taskInfo.getSensorData());
		unitLessValueEditText.setEnabled(false);
		
		unitMoreValueEditText.setInputType(InputType.TYPE_NULL); 
		if(TYPE_HUM.equals(taskInfo.getSensorType())){
			unitMoreValueEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showHumValueDialog(unitMoreValueEditText,"\u0025");
				}
			});
		}else{
			unitMoreValueEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showHumValueDialog(unitMoreValueEditText,"\u2103");
				}
			});	
		}
		
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
		
		moreTempUnitTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadTempLinkTasks();
			}
		});
		moreHumUnitTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadHumLinkTasks();
			}
		});
		
	}

	protected void showTempValueDialog(final EditText editText, String unit) {

		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setContentView(createLinkView(unit));
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
	
	private View createLinkView(final String unit) {
		linkValueView = inflater.inflate(R.layout.scene_link_sensorale_value_layout, null);
		valueEditText = (EditText) linkValueView.findViewById(R.id.scene_link_sensor_values_edit);
		valuedegreeText = (TextView) linkValueView.findViewById(R.id.scene_link_sensor_values_degree);
		valueUnitText = (TextView) linkValueView.findViewById(R.id.scene_link_sensor_values_unit);
		valueSeekBar = (SeekBar) linkValueView.findViewById(R.id.scene_link_sensor_values_seekbar);
		valueEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
		valueEditText.setSelection(valueEditText.getText().toString().length());
		valueUnitText.setText(unit);
		valueSeekBar.setProgress(0);
		mValueLink = String.valueOf(0);
		valueSeekBar.setMax(100);
		if("\u0025".equals(unit)){
			changeTextView();
		}
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
				if("\u0025".equals(unit)){
					changeTextView();
				}
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
				if("\u0025".equals(unit)){
					changeTextView();
				}
				valueEditText.setText(mValueLink);
			}
		});
		
		return linkValueView;
	}
	
	protected void showHumValueDialog(final EditText editText,String unit) {

		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setContentView(createLinkView(unit));
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

	protected void loadHumLinkTasks() {
		this.taskInfo.setSensorType(TYPE_HUM);
		onViewCreated(rootView);
	}

	protected void loadTempLinkTasks() {
		this.taskInfo.setSensorType(TYPE_TEMP);
		onViewCreated(rootView);
	}
	
	private void changeTextView(){
		int values = StringUtil.toInteger(mValueLink);
		if(values >= 0 && values <= 45){
			valuedegreeText.setText(context.getString(R.string.device_link_task_sensor_degree_dry));
		}else if(values > 45 && values <= 60){
			valuedegreeText.setText(context.getString(R.string.device_link_task_sensor_degree_comfortable));
		}else if(values> 60 && values <= 100){
			valuedegreeText.setText(context.getString(R.string.device_link_task_sensor_degree_wet));
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
		TextView unitSignalTextView = (TextView) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_signal);
		EditText unitValueEditText = (EditText) moreHeadContent
				.findViewById(R.id.scene_link_task_sensor_unit_value);
		unitValueEditText.setText(taskInfo.getSensorData());
		
		TextView moreTempUnitTextView = (TextView)moreHeadContent.findViewById(R.id.scene_link_task_temp_unit_name);
		TextView moreHumUnitTextView = (TextView)moreHeadContent.findViewById(R.id.scene_link_task_humidity_unit_name);
		if(TYPE_HUM.equals(this.taskInfo.getSensorType())){
			moreTempUnitTextView.setTextColor(resources.getColor(R.color.black));
			moreHumUnitTextView.setTextColor(resources.getColor(R.color.v5_green_dark));
			unitSignalTextView.setText("\u0025");
		}else if(TYPE_TEMP.equals(this.taskInfo.getSensorType())){
			moreTempUnitTextView.setTextColor(resources.getColor(R.color.v5_green_dark));
			moreHumUnitTextView.setTextColor(resources.getColor(R.color.black));
			unitSignalTextView.setText("\u2103");
		}
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
				taskInfo.setSensorData(info.getSensorData());
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
				taskInfo.setSensorData(info.getSensorData());
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
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable()) || !convertSensorType(info.getSensorType()).equals(this.taskInfo.getSensorType())){
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
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())|| !convertSensorType(info.getSensorType()).equals(this.taskInfo.getSensorType())){
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
