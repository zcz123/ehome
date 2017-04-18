package cc.wulian.smarthomev5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.wulian.app.model.device.impls.controlable.doorlock.EditDoorLock7SceneFragment;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.StateDrawableFactory;
import cc.wulian.smarthomev5.tools.StateDrawableFactory.Builder;

public class DoorLock7SceneInfoAdapter extends SceneInfoAdapter {

	private String currentClickScene="";
	private ActionBarCompat actionBarCompat;

	public DoorLock7SceneInfoAdapter(BaseActivity context, ActionBarCompat actionBarCompat) {
		super(context);
		this.actionBarCompat=actionBarCompat;
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			SceneEntity item) {
		// TODO Auto-generated method stub
		ImageButton mTimingSceneBt;
		// TextView mTimingSceneTv;
		// TextView mTimingSceneDe;

		ImageView iconImage = (ImageView) view.findViewById(R.id.icon);
		TextView nameText = (TextView) view.findViewById(R.id.name);
		boolean isUsing = false;
		if(item.getSceneID().equals(EditDoorLock7SceneFragment.sceneId)){
			isUsing=true;
		}
		Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(context,
				item.getIcon());
		Drawable checkedIcon = null;
		if (isUsing) {
			checkedIcon = SceneManager.getSceneIconDrawable_Bright(context,
					item.getIcon());
		} else {
			checkedIcon = SceneManager.getSceneIconDrawable_Black(context,
					item.getIcon());
		}
		Builder builder = StateDrawableFactory.makeSimpleStateDrawable(context,
				normalIcon, checkedIcon);

		iconImage.setImageDrawable(builder.create());
		nameText.setText(item.getName());
		state = view.findViewById(R.id.linearLayout_state);
		state.setSelected(isUsing);
		state.setOnClickListener(new NewOnClick(item, iconImage,state));
		state.setOnLongClickListener(null);

		mTimingSceneBt = (ImageButton) view
				.findViewById(R.id.scene_timing_delbt);
		// mTimingSceneTv = (TextView) view.findViewById(R.id.scene_timing_tv);
		// mTimingSceneDe = (TextView)
		// view.findViewById(R.id.scene_timing_delete);

		String sceneID = item.getSceneID();

		boolean houseHasUpgrade = preference.getBoolean(
				IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
		if (!houseHasUpgrade) {
			if (timingSceneGroup.contains(sceneID)) {
				item.setShowClock(true);
			} else {
				item.setShowClock(false);
			}
		} else {
			String programType = getSceneTimingTask(sceneID).getProgramType();
			if (!StringUtil.isNullOrEmpty(programType)
					&& StringUtil.equals(programType, "1")) {
				item.setShowClock(true);
			} else {
				item.setShowClock(false);
			}
		}

		if (item.isShowClock) {
			mTimingSceneBt.setVisibility(View.VISIBLE);
		} else {
			mTimingSceneBt.setVisibility(View.INVISIBLE);
		}

	}

	private final class NewOnClick implements View.OnClickListener {
		private final SceneInfo item;
		private ImageView iconImage;
		private View state;

		public NewOnClick(SceneInfo item, ImageView iconImage, View state) {
			this.item = item;
			this.iconImage = iconImage;
			this.state = state;
		}

		@Override
		public void onClick(View v) {
			
			//更改ActionBar
			actionBarCompat.setRightIconText("绑定");
			actionBarCompat.setRightMenuClickListener(new OnRightMenuClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SceneInfo newSceneInfo = item.clone();
					currentClickScene=item.getSceneID();
					String devID = EditDoorLock7SceneFragment.devID;
					String gwID = EditDoorLock7SceneFragment.gwID;
					JSONArray sendData = new JSONArray();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("ep", "14");
					jsonObject.put("sceneID", newSceneInfo.getSceneID());
					jsonObject.put("bindDevID", "");
					jsonObject.put("bindEp", "");
					jsonObject.put("bindData", "");
					sendData.add(jsonObject);
					NetSDK.sendSetBindSceneMsg(gwID, "1", devID, "Bd", sendData);
					activity.finish();
				}
			});
			EditDoorLock7SceneFragment.sceneId=item.getSceneID();
			// 对除了点击的场景之外的场景进行初始化状态
			initSceneStatus(item);
			// SceneManager.switchSceneInfo(mContext, newSceneInfo, true);
			Drawable normalIcon = SceneManager.getSceneIconDrawable_Black(
					mContext, item.getIcon());
			Drawable checkedIcon = null;
			checkedIcon = SceneManager.getSceneIconDrawable_Bright(mContext,
					item.getIcon());
			Builder builder = StateDrawableFactory.makeSimpleStateDrawable(
					mContext, normalIcon, checkedIcon);
			state.setSelected(true);
			iconImage.setImageDrawable(builder.create());
			notifyDataSetChanged();
		}
	}
}
