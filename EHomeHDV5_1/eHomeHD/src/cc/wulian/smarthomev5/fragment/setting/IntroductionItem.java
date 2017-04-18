package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.view.View;
import cc.wulian.smarthomev5.R;

public class IntroductionItem extends AbstractSettingItem{

	private String introductionStr = "";

	public IntroductionItem(Context context) {
		super(context);
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		view.setBackgroundColor(mContext.getResources().getColor(R.color.trant));
		upLinearLayout.setVisibility(View.GONE);
		downLineLayout.setVisibility(View.VISIBLE);
		descriptionTextView.setVisibility(View.VISIBLE);
		descriptionTextView.setText(introductionStr);

	}

	@Override
	public void doSomethingAboutSystem() {

	}

	public String getIntroductionStr() {
		return introductionStr;
	}

	public void setIntroductionStr(String introductionStr) {
		this.introductionStr = introductionStr;
	}

}
