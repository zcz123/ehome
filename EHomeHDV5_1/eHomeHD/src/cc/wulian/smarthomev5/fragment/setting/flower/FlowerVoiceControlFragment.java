package cc.wulian.smarthomev5.fragment.setting.flower;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.FlowerVoiceControlCommondActivity;
import cc.wulian.smarthomev5.adapter.flower.FlowerVoiceControlAdapter;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.FlowerVoiceControlEntity;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FlowerVoiceControlFragment extends WulianFragment {

	public static final String CMD_INDEX_GET_VOICE_CONTROL_LIST_21 = "21";
	public static final String SHOW_DIALOG_KEY = "SHOW_DIALOG_KEY";
	@ViewInject(R.id.dream_flower_lv)
	private SwipeMenuListView voiceControListView;
	private FlowerVoiceControlAdapter voiceControlAdapter;
	private FlowerManager manager = FlowerManager.getInstance();
	private boolean isRequest =false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		voiceControlAdapter = new FlowerVoiceControlAdapter(mActivity, null);
		voiceControlAdapter.setMenuCreator(creatLeftDeleteItem());
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
		getSupportActionBar().setTitle("语音控制");
		getSupportActionBar().setRightIconText("学习");
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						JSONObject object = new JSONObject();
						object.put("index", "1");
						NetSDK.sendSetDreamFlowerConfigMsg(
								mAccountManger.getmCurrentInfo().getGwID(), "22",
								object);
					}
				});

	}

	/**
	 * 创建左划删除item样式
	 */
	private SwipeMenuCreator creatLeftDeleteItem() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu,int position) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(mActivity, 90));
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.gateway_dreamflower_voice_control_content, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		voiceControListView.setAdapter(voiceControlAdapter);
		// 左划删除
		voiceControlAdapter
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						switch (index) {
						case 0:
							clearStudyData(voiceControlAdapter.getItem(position));
							break;
						}
					}
				});
		// 解决左划删除与右划菜单栏冲突
		voiceControListView.setOnOpenOrCloseListener(new OpenOrCloseListener() {

			@Override
			public void isOpen(boolean isOpen) {
				
			}
		});
		voiceControListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posotion,
					long arg3) {
				FlowerVoiceControlEntity entity = voiceControlAdapter.getItem(posotion);
				Bundle bundle = new Bundle();
				bundle.putString(FlowerVoiceControlCommondFragment.CMD_INDEX, entity.getIndex());
				mActivity.JumpTo(FlowerVoiceControlCommondActivity.class, bundle);
			}
		});
	}
	private void clearStudyData(FlowerVoiceControlEntity item) {
		JSONObject object = new JSONObject();
		object.put("index", item.getIndex());
		NetSDK.sendSetDreamFlowerConfigMsg(
				mAccountManger.getmCurrentInfo().getGwID(), "24",
				object);
	}
	@Override
	public void onResume() {
		super.onResume();
		voiceControlAdapter.swapData(manager.getVoiceControlEntities());
		// mDialogManager.showDialog(SHOW_DIALOG_KEY, mActivity, null, null);
		if(!isRequest){
			SendMessage.sendGetFlowerConfigMsg(mAccountManger.getmCurrentInfo().getGwID(), CMD_INDEX_GET_VOICE_CONTROL_LIST_21);
			isRequest = true;
		}
	}

	public void onEventMainThread(FlowerEvent event) {
		// this.mDialogManager.dimissDialog(SHOW_DIALOG_KEY, 0);
		if (FlowerEvent.ACTION_VOICE_CONTROL_GET.equals(event.getAction()) ||FlowerEvent.ACTION_VOICE_CONTROL_CLEAR.equals(event.getAction()) ) {
			voiceControlAdapter.swapData(manager.getVoiceControlEntities());
		}
	}
}
