package cc.wulian.smarthomev5.activity;


import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.setting.gateway.AccountInformationSettingManagerFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

public class AccountInformationSettingManagerActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new AccountInformationSettingManagerFragment())
			.commit();
		}
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
	@Override
	public void onBackPressed() {
		//如果是从html登陆的 返回的时候要返回到首页不能返回到html
		if("true".equals(SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.IS_LOGIN, ""))){
			Intent intent=new Intent(AccountInformationSettingManagerActivity.this,MainHomeActivity.class);
			startActivity(intent);
		}
		finish();		
	}
}
