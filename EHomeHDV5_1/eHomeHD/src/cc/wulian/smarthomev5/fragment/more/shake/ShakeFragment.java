package cc.wulian.smarthomev5.fragment.more.shake;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.ShakeAddDeviceActivity;
import cc.wulian.smarthomev5.dao.ShakeDao;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;

public class ShakeFragment extends WulianFragment {

	private SceneList mSceneList;
	private ImageView mSceneImageView;
	private TextView mSceneTextView;
	private LinearLayout mSceneLayout;
	private LinearLayout mDeviceLayout;
	private ShakeDao shakeDao = ShakeDao.getInstance();
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.shake_scene_ll:
				mSceneList.show(v);
				break;
			case R.id.shake_device_ll:
				mActivity.JumpTo(ShakeAddDeviceActivity.class);
				break;
			}

		}
	};
	private final SceneList.OnSceneListItemClickListener mItemClickListener = new SceneList.OnSceneListItemClickListener() {
		@Override
		public void onSceneListItemClicked(SceneList list, int pos,
				SceneInfo info) {
			list.dismiss();
			setSceneInfo(info);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initBar();
		return inflater.inflate(R.layout.more_shake_content, container, false);
	}

	
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_more));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.more_shake_off_function));
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(
						R.string.set_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						saveShake();
						createSaveSuccessDialog();
					}
				});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSceneList = new SceneList(mActivity, true);
		mSceneList.setOnSceneListItemClickListener(mItemClickListener);
		mSceneLayout = (LinearLayout) view.findViewById(R.id.shake_scene_ll);
		mSceneImageView = (ImageView) view.findViewById(R.id.shake_scene_Iv);
		mSceneTextView = (TextView) view.findViewById(R.id.shake_scene_tv);
		mSceneLayout.setOnClickListener(listener);
		mDeviceLayout = (LinearLayout) view.findViewById(R.id.shake_device_ll);
		mDeviceLayout.setOnClickListener(listener);
		if (ShakeManager.shakeEntity.size() > 0) {
			ShakeEntity entity = ShakeManager.shakeEntity.get(0);
			setSceneInfo(mApplication.sceneInfoMap.get(entity.getGwID()
					+ entity.getOperateID()));
		}

	}
	/**
	 * 设置场景信息
	 * 
	 * @param info
	 */
	public void setSceneInfo(SceneInfo info) {
		if (info == null || info.getSceneID() == null ) {
			mSceneTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
		} else if(CmdUtil.SCENE_UNKNOWN.equals(info.getSceneID())){
			mSceneTextView.setText(mApplication.getResources().getString(
					R.string.nav_scene_title));
			mSceneImageView.setImageResource(R.drawable.nav_scene_normal);
			if (ShakeManager.shakeEntity.size() > 0) {
				ShakeEntity mSceneShakeEntity = ShakeManager.shakeEntity.get(0);
				mSceneShakeEntity.operateID = info.getSceneID();
				mSceneShakeEntity.time = String.valueOf(System
						.currentTimeMillis());
			}
		}else {
			mSceneTextView.setText(info.getName());
			Drawable checkedIcon = SceneManager
					.getSceneIconDrawable_Light_Small(
							ShakeFragment.this.getActivity(), info.getIcon());
			mSceneImageView.setImageDrawable(checkedIcon);
			if (ShakeManager.shakeEntity.size() > 0) {
				ShakeEntity mSceneShakeEntity = ShakeManager.shakeEntity.get(0);
				mSceneShakeEntity.operateID = info.getSceneID();
				mSceneShakeEntity.time = String.valueOf(System
						.currentTimeMillis());
			}
		}
	}
	// 保存
	public void saveShake() {
		ShakeEntity deleteEntity = new ShakeEntity();
		deleteEntity.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		shakeDao.delete(deleteEntity);
		for (ShakeEntity entity : ShakeManager.shakeEntity) {
			shakeDao.insert(entity);
		}
	}

	/**
	 * 初始提示的对话框
	 */
	private void createSaveSuccessDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this.getActivity());
		builder.setContentView(R.layout.more_write_shake)
				.setPositiveButton(
						mApplication.getResources().getString(
								R.string.common_ok))
				.setNegativeButton(null).create().show();
	}
}
