package cc.wulian.smarthomev5.adapter;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.doorlock.EditDoorLock6BindSceneFragment;
import cc.wulian.app.model.device.impls.controlable.doorlock.EditDoorLock6SceneFragment;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.StateDrawableFactory;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.StateDrawableFactory.Builder;

public class DoorLockBindSceneInfoAdapter extends SceneInfoAdapter {
	
	private String currentClickScene="";
	private ActionBarCompat actionBarCompat;

	public DoorLockBindSceneInfoAdapter(BaseActivity context,ActionBarCompat actionBarCompat) {
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
		if(item.getSceneID().equals(EditDoorLock6BindSceneFragment.sceneId)){
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
			
			actionBarCompat.setRightIconText("绑定");
			actionBarCompat.setRightMenuClickListener(new OnRightMenuClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					activity.finish();
					SceneInfo newSceneInfo = item.clone();
					String devID = EditDoorLock6BindSceneFragment.devID;
					String gwID = EditDoorLock6BindSceneFragment.gwID;
					String token=EditDoorLock6BindSceneFragment.token;
					String userID=EditDoorLock6BindSceneFragment.userID;
					String sceneId=EditDoorLock6BindSceneFragment.sceneId;
					String peroid=EditDoorLock6BindSceneFragment.peroid;
					String password=EditDoorLock6BindSceneFragment.password;
					String cname=EditDoorLock6BindSceneFragment.cname;
					String userType=EditDoorLock6BindSceneFragment.userType;
					JSONObject sendData = new JSONObject();
					sendData.put("token", token);
					sendData.put("userID", Integer.parseInt(userID, 16)+"");
					sendData.put("sceneId", newSceneInfo.getSceneID());
					sendData.put("peroid", peroid);
					String passwordStr="";
					for(int i=0;i<password.length();i++){
						if(i%2!=0){
							passwordStr+=password.charAt(i);
						}
					}
					sendData.put("password", passwordStr);
					sendData.put("cname", cname);
					sendData.put("userType", Integer.parseInt(userType)+"");
					NetSDK.sendSetDoorLockData(gwID, devID, "4", sendData);
					activity.finish();
				}
			});
			EditDoorLock6BindSceneFragment.sceneId=item.getSceneID();
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
