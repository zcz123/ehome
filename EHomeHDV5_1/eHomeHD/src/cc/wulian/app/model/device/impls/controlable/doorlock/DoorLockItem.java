package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import cc.wulian.app.model.device.impls.controlable.doorlock.DoorLockAccountItem.IClickDosomething;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class DoorLockItem extends AbstractSettingItem {
	
	private String rightDetailDescribe = "";
	private String nameDescribe = "";
	private IClickDosomething clickDosomething;

	public DoorLockItem(Context context) {
		super(context, null, null);
	}
  
	@Override
	public void initSystemState() {
		super.initSystemState();
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//		((LinearLayout)view).setLayoutParams(lp);
		view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		iconImageView.setVisibility(View.GONE);
		infoTextView.setText(Html.fromHtml(mContext.getResources().getString(R.string.door_lock_account_descripe)));
		nameTextView.setText(getNameDescribe());
	}
	
	@Override
	public void doSomethingAboutSystem() {
		if(clickDosomething!=null){
			clickDosomething.dosomething();
		}
	}

	public void setInfoTextViewVisible(int visible){
		infoTextView.setVisibility(visible);
	}
	
	public void changeViewBackground() {
		view.setBackgroundColor(mContext.getResources().getColor(R.color.trant));
	}

	public String getRightDetailDescribe() {
		return rightDetailDescribe;
	}

	public void setRightDetailDescribe(String rightDetailDescribe) {
		this.rightDetailDescribe = rightDetailDescribe;
	}
	
	public String getNameDescribe() {
		return nameDescribe;
	}

	public void setNameDescribe(String nameDescribe) {
		this.nameDescribe = nameDescribe;
	}
	
	public void setNameSize(int size) {
		nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
	}
	
	public void setCallBack(IClickDosomething clickDosomething) {
		this.clickDosomething = clickDosomething;
	}
}
