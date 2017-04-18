package cc.wulian.smarthomev5.fragment.scene;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.AddOrEditTimingSceneTimeActivity;
import cc.wulian.smarthomev5.adapter.TimingSceneTimeAdapter;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

/**
 * 
 * @ClassName: TimingScenesFragment
 * @Description:(显示某场景下的所有定时任务)
 * @author ylz
 * @date 2015-3-25 下午2:10:51
 * 
 */
public class TimingScenesFragment extends WulianFragment {

	public static final String SCENE_INFO_SERIAL = "scene_info_serial";
	private TimingSceneTimeAdapter mTimingSceneTimeAdapter;
	private SceneInfo mCurrentSceneInfo;
	private TimingSceneGroupEntity timingSceneGroup = TimingSceneManager
			.getInstance().getDefaultGroup();
	private ListView timeListView;
	private TextView emptyTimingTaskTextView;
	private static final String STOP_KEY = "stop_key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle arg = getArguments();
		if (arg != null) {
			mCurrentSceneInfo = (SceneInfo) arg
					.getSerializable(SCENE_INFO_SERIAL);
		} else {
			mCurrentSceneInfo = new SceneInfo();
			mCurrentSceneInfo
					.setGwID(getAccountManger().getmCurrentInfo().getGwID());
			mCurrentSceneInfo.setSceneID(CmdUtil.SENSOR_DEFAULT);
		}
		mTimingSceneTimeAdapter = new TimingSceneTimeAdapter(mActivity,
				null, mCurrentSceneInfo);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.timing_scene_timelist_fragment,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		timeListView = (ListView) view
				.findViewById(R.id.time_scene_time_listview);
		emptyTimingTaskTextView =(TextView) view.findViewById(R.id.time_scene_time_empty_tv);
		timeListView.setAdapter(mTimingSceneTimeAdapter);
		timeListView.setOnItemClickListener(new OnItemClickListener() {
			
			/**
			 * 点击列表中对应item 将mCurrentSceneInfo与position传出
			 * 并跳转至时间编辑界面AddOrEditTimingSceneTimeActivity
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle sceneargs = new Bundle();
				sceneargs.putSerializable(
						AddOrEditTimingSceneTimeFragment.SCENE_INFO_SERIAL,
						mCurrentSceneInfo);
				sceneargs
						.putInt(AddOrEditTimingSceneTimeFragment.SCENE_INFO_TIME_SERIAL,
								position);

				mActivity.JumpTo(AddOrEditTimingSceneTimeActivity.class,
						sceneargs);
			}
		});
		timeListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			/**
			 * 列表长按事件 弹出确定删除按钮 并通过添加方式删除该条定时任务delateTimingScene
			 */
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TimingSceneEntity mTimingSceneEntity = mTimingSceneTimeAdapter
						.getItem(position);
				delateTimingScene(mActivity, mTimingSceneEntity);
				return true;
			}
		});
	}


	private void delateTimingScene(Context context,
			final TimingSceneEntity mTimingSceneEntity) {
		WLDialog.Builder builder = new Builder(mActivity);
		builder.setTitle(R.string.device_config_edit_dev_area_create_item_delete)
				.setContentView(R.layout.fragment_message_select_delete)
				.setPositiveButton(android.R.string.ok)
				.setNegativeButton(android.R.string.cancel)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						
						/**
						 * 将词条数据在缓存中清除 通过添加方式将词条定时任务删除 并显示progressDialog
						 */
						JsonTool.uploadTimingSceneList(CmdUtil.MODE_ADD,
								timingSceneGroup,timingSceneGroup.removeTimingSceneEntitiesNewList(mTimingSceneEntity));
						mDialogManager.showDialog(STOP_KEY, mActivity, null,
								null);
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		WLDialog dialog = builder.create();
		dialog.show();
	}

	private List<TimingSceneEntity> getSceneTime() {
		return timingSceneGroup.getTimingSceneEntities(mCurrentSceneInfo
				.getSceneID());
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.scene_info_timing_scene));
		getSupportActionBar().setIconText(R.string.nav_scene_title);
		getSupportActionBar().setRightIcon(R.drawable.common_use_add);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					
					/**
					 * 定时场景的添加入口 传输的SCENE_INFO_TIME_SERIAL标记为-1便于确定此处为添加操作
					 */
					@Override
					public void onClick(View v) {
						Bundle sceneargs = new Bundle();
						sceneargs
								.putSerializable(
										AddOrEditTimingSceneTimeFragment.SCENE_INFO_SERIAL,
										mCurrentSceneInfo);
						sceneargs
								.putInt(AddOrEditTimingSceneTimeFragment.SCENE_INFO_TIME_SERIAL,
										-1);
						mActivity.JumpTo(
								AddOrEditTimingSceneTimeActivity.class,
								sceneargs);
					}
				});

	}

	/**
	 * 界面跳转后从新加载缓存数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		loadTimingScenes();
	}

	public void loadTimingScenes() {
		List<TimingSceneEntity> entites = getSceneTime();
		if(entites == null || entites.size() == 0){
			timeListView.setVisibility(View.INVISIBLE);
			emptyTimingTaskTextView.setVisibility(View.VISIBLE);
		}else{
			timeListView.setVisibility(View.VISIBLE);
			emptyTimingTaskTextView.setVisibility(View.INVISIBLE);
			mTimingSceneTimeAdapter.swapData(entites);
		}
	}

	/**
	 * 
	 * @Title: onEventMainThread
	 * @Description:(在接收到数据返回时，关闭progressDialog，标明删除完成，并重新刷新界面)
	 * @param @param TimingSceneEvent
	 * @return void
	 * @throws
	 */
	public void onEventMainThread(TimingSceneEvent event) {
		mDialogManager.dimissDialog(STOP_KEY, 0);
		loadTimingScenes();
	}
}
