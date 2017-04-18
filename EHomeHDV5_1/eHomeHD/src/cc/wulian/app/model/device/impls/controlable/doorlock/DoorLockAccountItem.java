package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

public class DoorLockAccountItem {

	protected BaseActivity mActivity;
	private LayoutInflater inflater;

	private LinearLayout linearLayout;
	private Button deleteButton;
	private LinearLayout itemLayout;
	private TextView tvDescribe;
	private TextView tvName;
	private IClickDosomething clickDosomething;

	public DoorLockAccountItem(BaseActivity mActivity, boolean isInvalidation,
			String name) {
		this.mActivity = mActivity;
		inflater = LayoutInflater.from(mActivity);
		linearLayout = (LinearLayout) inflater.inflate(
				R.layout.layout_door_lock_account_swipe_menu, null);

		deleteButton = (Button) linearLayout
				.findViewById(R.id.task_manager_message_item_delete);

		itemLayout = (LinearLayout) linearLayout
				.findViewById(R.id.task_manager_message_item_layout);

		tvDescribe = (TextView) linearLayout
				.findViewById(R.id.door_lock_account_describe_tv);
		tvName = (TextView) linearLayout
				.findViewById(R.id.door_lock_account_name_tv);
		tvName.setText(name);
		itemLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(clickDosomething!=null){
					clickDosomething.dosomething();
				}
			}
		});
		// 主要是设置这个监听
		itemLayout.setOnTouchListener(new SwipeTouchViewListener(itemLayout,
				deleteButton));
		if (isInvalidation) {
			tvDescribe.setVisibility(View.INVISIBLE);
		} else {
			tvDescribe.setVisibility(View.VISIBLE);
		}

	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}

	public LinearLayout getView() {
		return linearLayout;
	}
	
	public void setCallBack(IClickDosomething clickDosomething) {
		this.clickDosomething = clickDosomething;
	}

	public interface IClickDosomething {
		void dosomething();
	}
}
