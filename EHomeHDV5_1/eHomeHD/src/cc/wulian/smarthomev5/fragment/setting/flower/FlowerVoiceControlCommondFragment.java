package cc.wulian.smarthomev5.fragment.setting.flower;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.FlowerVoiceControlEntity;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneList.OnSceneListItemClickListener;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLToast;

public class FlowerVoiceControlCommondFragment extends WulianFragment {

	public static final String CMD_INDEX = "CMD_INDEX";
	private FlowerManager manager = FlowerManager.getInstance();
	private FlowerVoiceControlEntity voiceControlEntity;
	@ViewInject(R.id.gateway_voice_control_commond_bing_scene)
	private ImageView bingSceneImageView;
	@ViewInject(R.id.gateway_voice_control_commond_bing_scene_name)
	private TextView bindSceneNameTextView;
	@ViewInject(R.id.gateway_voice_control_commond_study)
	private Button stutyButton;
	@ViewInject(R.id.gateway_voice_control_commond_study_stop)
	private Button stopButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String commondIndex = getArguments().getString(CMD_INDEX);
		voiceControlEntity = manager.getVoiceControlEntity(commondIndex);
		initBar();
	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(R.string.gateway_dream_flower);
		getSupportActionBar().setTitle("语音控制学习");
		getSupportActionBar().setRightIconText("保存");
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						JSONObject object = new JSONObject();
						object.put("index", voiceControlEntity.getIndex());
						object.put("bindscene",voiceControlEntity.getBindScene());
						NetSDK.sendSetDreamFlowerConfigMsg(
								mAccountManger.getmCurrentInfo().getGwID(), "25",
								object);
					}
				});

	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.gateway_dreamflower_voice_control_commond_content, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bingSceneImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final SceneList sceneList = new SceneList(mActivity, true);
				sceneList.setOnSceneListItemClickListener(new OnSceneListItemClickListener() {
					
					@Override
					public void onSceneListItemClicked(SceneList list, int pos, SceneInfo info) {
						setSceneInfo(info);
						voiceControlEntity.setBindScene(info.getSceneID());
						sceneList.dismiss();
					}
				});
				sceneList.show(v);
			}
		});
		stutyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopButton.setVisibility(View.VISIBLE);
				stutyButton.setVisibility(View.GONE);
				JSONObject object = new JSONObject();
				object.put("index", voiceControlEntity.getIndex());
				NetSDK.sendSetDreamFlowerConfigMsg(
						mAccountManger.getmCurrentInfo().getGwID(), "22",
						object);
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopButton.setVisibility(View.GONE);
				stutyButton.setVisibility(View.VISIBLE);
				JSONObject object = new JSONObject();
				object.put("index", voiceControlEntity.getIndex());
				NetSDK.sendSetDreamFlowerConfigMsg(
						mAccountManger.getmCurrentInfo().getGwID(), "23",
						object);
			}
		});
	}

	/**
	 * 选中场景时设置
	 * 
	 * @param info
	 */
	public void setSceneInfo(SceneInfo info) {
		if (info == null || info.getSceneID() == null) {
			bingSceneImageView.setImageResource(R.drawable.nav_scene_normal);
			bindSceneNameTextView.setText(R.string.nav_scene_title);
			return ;
		}else if(CmdUtil.SCENE_UNKNOWN.equals(info.getSceneID())){
			bingSceneImageView.setImageResource(R.drawable.nav_scene_normal);
			bindSceneNameTextView.setText(R.string.nav_scene_title);
		}else{
			Drawable checkedIcon = SceneManager.getSceneIconDrawable_Light_Small(
						mActivity, info.getIcon());
			bingSceneImageView.setImageDrawable(checkedIcon);
			bindSceneNameTextView.setText(info.getName());
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		setSceneInfo(mApplication.sceneInfoMap.get(voiceControlEntity.getGwID()+voiceControlEntity.getBindScene()));
	}
	public void onEventMainThread(FlowerEvent event) {
		if (FlowerEvent.ACTION_VOICE_CONTROL_STATE.equals(event.getAction())) {
			stopButton.setVisibility(View.GONE);
			stutyButton.setVisibility(View.VISIBLE);
			if(StringUtil.isNullOrEmpty(event.getEventStr())){
				WLToast.showToast(mActivity, "学习成功", WLToast.TOAST_SHORT);
			}else{
				WLToast.showToast(mActivity, "学习失败"+event.getEventStr(), WLToast.TOAST_SHORT);
			}
		}else if(FlowerEvent.ACTION_VOICE_CONTROL_BIND.equals(event.getAction())){
			if(FlowerVoiceControlEntity.VALUE_UNBINDSCENE.equals(event.getEventStr())){
				WLToast.showToast(mActivity, "绑定失败", WLToast.TOAST_SHORT);
			}else if(FlowerVoiceControlEntity.VALUE_BING_NO_SCENE.equals(event.getEventStr())){
				WLToast.showToast(mActivity, "场景不存在", WLToast.TOAST_SHORT);
			}else{
				WLToast.showToast(mActivity, "绑定成功", WLToast.TOAST_SHORT);
				mActivity.finish();
			}
		}
	}
}
