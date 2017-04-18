package cc.wulian.smarthomev5.fragment.setting.flower;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.AddOrEditFlowerTimeActivity;
import cc.wulian.smarthomev5.adapter.flower.FlowerTimingBroadcastAdapter;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class FlowerTimingBroadcastFragment extends WulianFragment {
	public static final String FLOWER_TIME_SERIAL = "flower_time_broadcast_serial";
	private static final String SHOW_DIALOG_KEY = "flower_Time_broadcase_dialog_key";
	private FlowerTimingBroadcastAdapter mAdapter;
	private Button timingAddButton;
	private ListView timingListView;
	private FlowerManager timingSceneGroup = FlowerManager.getInstance();
	private TextView timingTextView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new FlowerTimingBroadcastAdapter(mActivity, null);
		initBar();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup paramViewGroup,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.setting_flower_timing_broadcast,
				paramViewGroup, false);
	}

	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);
		this.timingTextView = ((TextView) paramView
				.findViewById(R.id.setting_flower_timing_empty_tv));
		this.timingAddButton = ((Button) paramView
				.findViewById(R.id.setting_flower_add_timing_bt));
		this.timingListView = ((ListView) paramView
				.findViewById(R.id.setting_flower_timing_listview));
		this.timingListView.setAdapter(this.mAdapter);
		this.timingListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						jumpToAddOrEditTimeActivity(paramInt);
					}
				});
		this.timingListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(
							AdapterView<?> paramAdapterView, View paramView,
							int paramInt, long paramLong) {
						delateTimingScene(mActivity, paramInt);
						return true;
					}
				});
		this.timingAddButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				jumpToAddOrEditTimeActivity(-1);
			}
		});
		//从服务端获取数据
		SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), CmdUtil.FLOWER_TIMING_BROADCAST);
	}

	public void onResume() {
		super.onResume();
		loadTimingScenes();
	}

	private void delateTimingScene(Context context, final int position) {
		WLDialog.Builder builder = new WLDialog.Builder(this.mActivity);
		builder.setTitle(
				R.string.device_config_edit_dev_area_create_item_delete)
				.setContentView(R.layout.fragment_message_select_delete)
				.setPositiveButton(android.R.string.ok)
				.setNegativeButton(android.R.string.cancel)
				.setListener(new MessageListener() {
					public void onClickNegative(View paramView) {
					}

					public void onClickPositive(View paramView) {
						List<TimingFlowerEntity> list = timingSceneGroup
								.getNewTimingEntities(FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME);
						list.remove(position);
						JsonTool.SetFlowerShowTiming(
								CmdUtil.FLOWER_TIMING_BROADCAST, list);
						mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity,
								null, null);
					}
				});
		builder.create().show();
	}

	private List<TimingFlowerEntity> getSceneTime() {
		return this.timingSceneGroup
				.getFlowerTimingEntities(FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME);
	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText(R.string.gateway_dream_flower);
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.gateway_dream_flower_timing_broadcast));
	}

	/**
	 * paramInt -1标识新增  ，其他标识修改
	 * @param paramInt
	 */
	private void jumpToAddOrEditTimeActivity(int paramInt) {
		Bundle bundle = new Bundle();
		bundle.putInt(FLOWER_TIME_SERIAL, paramInt);
		Intent intent = new Intent(getActivity(),
				AddOrEditFlowerTimeActivity.class);
		if (bundle != null)
			intent.putExtras(bundle);
		startActivity(intent);
	}

	public void loadTimingScenes() {
		List<TimingFlowerEntity> localList = getSceneTime();
		if ((localList == null) || (localList.size() == 0)) {
			this.timingListView.setVisibility(View.INVISIBLE);
			this.timingTextView.setVisibility(View.VISIBLE);
			return;
		}
		this.timingListView.setVisibility(View.VISIBLE);
		this.timingTextView.setVisibility(View.INVISIBLE);
		this.mAdapter.swapData(localList);
	}

	public void onEventMainThread(FlowerEvent event) {
		this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
		if (event != null&& FlowerEvent.ACTION_FLOWER_SET_BROADCAST_TIME.equals(event.getAction())) {
			loadTimingScenes();
		}
	}

}
