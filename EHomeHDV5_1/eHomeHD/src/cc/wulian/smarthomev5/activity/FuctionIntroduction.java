package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.R;

public class FuctionIntroduction extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		setContentView(R.layout.aboutus_fuction_introduction);
	}

	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.about_us));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.about_function_introduction));
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
}