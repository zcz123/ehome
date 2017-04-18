package cc.wulian.smarthomev5.fragment.house;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.databases.entitys.AutoTask;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import de.greenrobot.event.EventBus;

public class AutoProgramTaskItem {

	private Context mContext;
	protected LayoutInflater inflater;
	protected Resources mResources;
	protected LinearLayout lineLayout;
	
	
	private ImageView taskImageView;
	private TextView taskName;
//	private TextView taskDescribe;
	private ImageView taskEffect;
	private ProgressDialogManager mDialogManager = ProgressDialogManager.getDialogManager();
	private static final String TASK_KEY = "task_key";
	
	public AutoProgramTaskItem(final Context context,final AutoProgramTaskInfo info){
		mContext = context;
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		
		lineLayout = (LinearLayout)inflater.inflate(R.layout.task_manager_list_item_layout, null);
		taskImageView = (ImageView) lineLayout.findViewById(R.id.task_manager_list_item_imv);
		taskName = (TextView) lineLayout.findViewById(R.id.task_manager_list_item_name);
//		taskDescribe = (TextView) lineLayout.findViewById(R.id.task_manager_list_item_describe);
		taskEffect = (ImageView) lineLayout.findViewById(R.id.task_manager_list_item_switch);
		
		if(StringUtil.equals(info.getStatus(), "2")){
			taskImageView.setSelected(false);
			taskEffect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggle_btn_checked));
			final String status = "1";
			taskEffect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					sendChangeHouseEffect(info, status);
				}
			});
		}else{
			taskImageView.setSelected(true);
			taskEffect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggle_btn_unchecked));
			final String status = "2";
			taskEffect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					sendChangeHouseEffect(info, status);
				}
			});
		}
//		taskEffect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//				String gwID = "";
//				String programID = "";
//				String programName = "";
//				String programDesc = "";
//				String operType = "U";
//				String programType = "2"; 
//				String status = ""; 
//				if(info != null){
//					gwID = info.getGwID();
//					programID = info.getProgramID();
//					programName = info.getProgramName();
//					programDesc = info.getProgramDesc();
//				}
//				if(isChecked){
//					status = "1";
//				}else{
//					status = "0";
//				}
//				NetSDK.sendSetProgramTask(gwID, operType, programID, programName, programDesc,programType, status,null, null, null);
//				EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.QUERY));
//				mDialogManager.showDialog(TASK_KEY, mContext, null,
//						null);
//			
//			}
//		});
		
	}
	
	public View getView(AutoProgramTaskInfo info) {
		taskName.setText(info.getProgramName());
//		taskDescribe.setText(info.getProgramDesc());
		return lineLayout;
	}

	private void sendChangeHouseEffect(AutoProgramTaskInfo info,String status){
		
		if(info != null){
			String gwID = info.getGwID();
			String programID = info.getProgramID();
			String programName = info.getProgramName();
			String programDesc = info.getProgramDesc();
			String operType = AutoTask.AUTO_TASK_OPER_TYPE_STATUS;
			String programType = "2"; 
			NetSDK.sendSetProgramTask(gwID, operType, programID, programName, programDesc,programType, status,null, null, null);
			EventBus.getDefault().post(new AutoTaskEvent(AutoTaskEvent.STATUS));
			mDialogManager.showDialog(TASK_KEY, mContext, null,
					null);
		}
	
	}
}
