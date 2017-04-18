package cc.wulian.smarthomev5.fragment.setting.flower;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import cc.wulian.smarthomev5.activity.flower.AddOrEditFlowerTimeShowActivity;
import cc.wulian.smarthomev5.adapter.flower.FlowerTimeShowAdapter;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class FlowerTimeShowFragment extends WulianFragment {
	public static final String FLOWER_TIME_SHOW_SERIAL = "flower_time_show_serial";
	private static final String SHOW_DIALOG_KEY = "flower_show_time_dialog_key";
	private Button addTimeButton;
	private ListView listView;
	private FlowerTimeShowAdapter mAdapter;
	private TextView textView;
	private FlowerManager timingSceneGroup = FlowerManager.getInstance();

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.mAdapter = new FlowerTimeShowAdapter(this.mActivity, null);
		initBar();
	}

	public View onCreateView(LayoutInflater paramLayoutInflater,
			@Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
		View localView = paramLayoutInflater.inflate(R.layout.flower_time_show,
				paramViewGroup, false);
		ViewUtils.inject(this, localView);
		return localView;
	}

	public void onViewCreated(View paramView, @Nullable Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);
		this.listView = ((ListView) paramView
				.findViewById(R.id.flower_time_show_listview));
		this.textView = ((TextView) paramView
				.findViewById(R.id.flower_time_show_empty_tv));
		this.addTimeButton = ((Button) paramView
				.findViewById(R.id.flower_time_show_add_time_bt));
		this.listView.setAdapter(this.mAdapter);
		this.listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						jumpToAddOrEditTimeActivity(paramInt);
					}
				});
		this.listView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(
							AdapterView<?> paramAdapterView, View paramView,
							int paramInt, long paramLong) {
						delateTimingScene(
								FlowerTimeShowFragment.this.mActivity, paramInt);
						return true;
					}
				});
		this.addTimeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				jumpToAddOrEditTimeActivity(-1);
			}
		});
		//从服务端获取数据
		SendMessage.sendGetFlowerConfigMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),CmdUtil.FLOWER_SET_SHOW_TIME);
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
								.getNewTimingEntities(FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME);
						list.remove(position);
						JsonTool.SetFlowerShowTiming(
								CmdUtil.FLOWER_SET_SHOW_TIME, list);
						mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity,
								null, null);
					}
				});
		builder.create().show();

	}

	private List<TimingFlowerEntity> getSceneTime() {
		return this.timingSceneGroup
				.getFlowerTimingEntities(FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME);
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
						R.string.gateway_dream_flower_time_show));
	}

	/**
	 * 跳转到时间选择页面 paramInt -1 标识 新增 其他标识修改
	 * 
	 * @param paramInt
	 */
	private void jumpToAddOrEditTimeActivity(int paramInt) {
		Bundle bundle = new Bundle();
		bundle.putInt(FLOWER_TIME_SHOW_SERIAL, paramInt);
		Intent intent = new Intent(getActivity(),
				AddOrEditFlowerTimeShowActivity.class);
		if (bundle != null)
			intent.putExtras(bundle);
		startActivity(intent);
	}

	public void loadTimingScenes() {
		List<TimingFlowerEntity> localList = getSceneTime();
		if ((localList == null) || (localList.size() == 0)) {
			this.listView.setVisibility(View.INVISIBLE);
			this.textView.setVisibility(View.VISIBLE);
			return;
		}
		this.listView.setVisibility(View.VISIBLE);
		this.textView.setVisibility(View.INVISIBLE);
		this.mAdapter.swapData(localList);
	}

	public void onEventMainThread(FlowerEvent event) {
		this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
		if ((event != null)&& (FlowerEvent.ACTION_FLOWER_SET_SHOW_TIME.equals(event.getAction())))
			loadTimingScenes();
	}

}