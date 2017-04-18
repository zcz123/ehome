package cc.wulian.smarthomev5.fragment.scene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.event.TimingSceneEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;
/**
 * 
* @ClassName: AddOrEditTimingSceneTimeFragment 
* @Description: TODO(修改多定时场景与添加多定时场景) 
* @author ylz
* @date 2015-3-25 下午2:10:12 
*
 */
public class AddOrEditTimingSceneTimeFragment extends WulianFragment {

	public static final String SCENE_INFO_SERIAL = "scene_info_serial";
	public static final String SCENE_INFO_TIME_SERIAL = "scene_info_time_serial";
	private TimingSceneView timingSceneView;
	private SceneInfo mCurrentSceneInfo;
	private int position;
	private static final String STOP_KEY = "stop_key";
	private TimingSceneGroupEntity timingSceneGroup = TimingSceneManager.getInstance().getDefaultGroup();
	private TimingSceneEntity mTimingSceneEntity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle arg = getArguments();
		if (arg != null) {
			//跳转时，bundle中存在数据。修改
			mCurrentSceneInfo = (SceneInfo) arg
					.getSerializable(SCENE_INFO_SERIAL);
			position= arg.getInt(SCENE_INFO_TIME_SERIAL);
		} else {
			//跳转时，bundle中数据为空。添加
			mCurrentSceneInfo = new SceneInfo();
			mCurrentSceneInfo
					.setGwID(getAccountManger().getmCurrentInfo().getGwID());
			mCurrentSceneInfo.setSceneID(CmdUtil.SENSOR_DEFAULT);
		}
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		   timingSceneView = new TimingSceneView(mActivity);
		if(position>=0){
			/**
			 * 修改
			 * 将实体类中数据（小时、日期）赋给timingSceneView
			 */
			timingSceneView.setmTimingScene(timingSceneGroup.getTimingSceneEntities(mCurrentSceneInfo.getSceneID()).get(position).clone());
		}
		else{
			/**
			 * 添加
			 * 新建timingSceneView，设置时间为当前时间
			 * 日期为空
			 */
		    mTimingSceneEntity = new TimingSceneEntity();
			mTimingSceneEntity.setSceneID(mCurrentSceneInfo.getSceneID());
//			timingSceneGroup.addTimingSceneEntity(mTimingSceneEntity);
			timingSceneView.setmTimingScene(mTimingSceneEntity);
			}
		return timingSceneView;
	}

	private void initBar() {

		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(false);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.cancel));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.scene_info_timing_scene));
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(R.string.set_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						/**
						 * 通过修改timingSceneGroup中的数据 修改场景时通过添加定时场景命令完成场景修改
						 */
						if (position >= 0) {
							JsonTool.uploadTimingSceneList(CmdUtil.MODE_ADD,
									timingSceneGroup,timingSceneGroup.modifyTimingSceneEntityNewList(timingSceneGroup.getTimingSceneEntities(mCurrentSceneInfo.getSceneID()).get(position),timingSceneView
									.getTimingScene()));
						} else {
							JsonTool.uploadTimingSceneList(CmdUtil.MODE_ADD,
									timingSceneGroup,timingSceneGroup.addTimingSceneEntityNewList(timingSceneView
									.getTimingScene()));
						}

						
						/**
						 * 启动转轮，并将转轮标记为STOP_KEY
						 */
						mDialogManager.showDialog(STOP_KEY, mActivity, null,
								null);
					}
				});

	}
/**
 * 监听到TimingSceneEvent数据有返回时
 * 1.关闭转轮；
 * 2.关闭此活动；
 * @param event
 * 
 */
	public void onEventMainThread(TimingSceneEvent event) {
		mDialogManager.dimissDialog(STOP_KEY, 0);//1
		mActivity.finish();//2
	}
}

