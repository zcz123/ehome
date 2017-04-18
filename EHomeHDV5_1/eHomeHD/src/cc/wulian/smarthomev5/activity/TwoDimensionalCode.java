package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import android.widget.TextView;

import cc.wulian.smarthomev5.R;

public class TwoDimensionalCode extends BaseActivity {
	private TextView codeTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		setContentView(R.layout.aboutus_two_dimensional_code);
		codeTextView= (TextView) findViewById(R.id.code_tv);
		codeTextView.setText(getString(R.string.about_wechat_QR_code_number)+"wuliancg");
	}

	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.about_us));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.about_wechat_QR_code));
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
}
