package cc.wulian.smarthomev5.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cc.wulian.smarthomev5.fragment.singin.SigninFragmentV5;

public class SigninActivityV5 extends FragmentActivity {
	private MainApplication app;
	private SigninFragmentV5 signinFragmentV5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initApp();
		signinFragmentV5 = new SigninFragmentV5();
		getSupportFragmentManager().beginTransaction().add(android.R.id.content, signinFragmentV5).commit();
	}

	private void initApp() {
		app = MainApplication.getApplication();
		app.stopApplication();
	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		signinFragmentV5.onActivityResult(requestCode, resultCode, data);
	}
}
