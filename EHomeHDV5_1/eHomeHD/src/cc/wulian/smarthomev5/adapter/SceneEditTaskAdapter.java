package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.entity.TaskEntity.TaskGroup;
import cc.wulian.smarthomev5.fragment.scene.AddDeviceToSceneFragmentDialog;
import cc.wulian.smarthomev5.fragment.scene.TaskControlItem;
public class SceneEditTaskAdapter extends WLBaseExpandableAdapter<TaskGroup,TaskControlItem>{

	public SceneEditTaskAdapter(Context context) {
		super(context, new ArrayList<TaskGroup>(),new ArrayList<List<TaskControlItem>>());
	}
	@Override
	protected View newGroupView(Context context, LayoutInflater inflater,
			ViewGroup parent, int groupPosition, boolean isExpanded) {
		return inflater.inflate(R.layout.scene_edit_task_title_item, null);
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final TaskControlItem item = getChild(groupPosition, childPosition);
		return item.getView();
	}
	@Override
	protected void bindGroupView(Context context, View view, int groupPosition,
			boolean isExpanded, final TaskGroup groupItem) {
		TextView nameTextView = (TextView)view.findViewById(R.id.scene_task_group_tv);
		nameTextView.setText(groupItem.getName());
		LinearLayout addLineLayout = (LinearLayout)view.findViewById(R.id.scene_task_group_add_ll);
		addLineLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager manager = ((BaseActivity)mContext).getSupportFragmentManager();
				AddDeviceToSceneFragmentDialog.showDeviceDialog(manager, manager.beginTransaction(), groupItem);
			}
		});
	}
}
