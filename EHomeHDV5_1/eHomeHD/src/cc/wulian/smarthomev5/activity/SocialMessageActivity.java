package cc.wulian.smarthomev5.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.HomeMenuClickLIstener;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.home.clickedfragment.MessgaeFragment;
import cc.wulian.smarthomev5.fragment.home.clickedfragment.SocialFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.HyphenateManager;
import cc.wulian.smarthomev5.tools.Preference;

public class SocialMessageActivity extends EventBusActivity{

	private MessgaeFragment homeChatFragment;
	private Preference preference;
	public static String GROUP_ID="group_id";
	private SimpleDateFormat simpleDateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//检查账户在子线程中进行
		preference=Preference.getPreferences();
		initBar();
		String groupID=getIntent().getExtras().getString(GROUP_ID);
		creatChatFragment(groupID);
		setChatFragmentListener();
		setDefaultFragment();
//		if (savedInstanceState == null){
//			getSupportFragmentManager()
//			.beginTransaction()
//			.add(android.R.id.content, new SocialFragment())
//			.commit();
//		}
	}

	private void initBar() {
		SocialMessageActivity.this.resetActionMenu();
		getCompatActionBar().setDisplayShowMenuEnabled(false);
		getCompatActionBar().setDisplayShowMenuTextEnabled(false);
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setTitle(
				getResources().getString(R.string.home_return_message_titel));
		getCompatActionBar().setIconText(R.string.nav_home_title);
	}

	private void setDefaultFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(android.R.id.content, homeChatFragment);
		transaction.commit();
	}


	private void creatChatFragment(String groupID) {

		homeChatFragment = new MessgaeFragment();
		Bundle args = new Bundle();
		args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
		args.putString(EaseConstant.EXTRA_USER_ID, groupID);
		homeChatFragment.setArguments(args);
		homeChatFragment.setHomeMenuClickLIstener(new HomeMenuClickLIstener() {
			@Override
			public void changeHomeMenuStatus(final int numberInt, final String tipsString) {
				if(numberInt!=0){
					try{
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								//收到信息回调处理
//								Toast.makeText(SocialMessageActivity.this,tipsString,Toast.LENGTH_SHORT).show();
							}
						});
					}catch (Exception e){
						e.printStackTrace();
					}

				}
			}
		});
//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(android.R.id.content, homeChatFragment)
//                .commit();

	}

	private void setChatFragmentListener() {

		homeChatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {
			@Override
			public void onSetMessageAttributes(EMMessage message) {
				Date date= new Date();
				simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				String dateNowStr = simpleDateFormat.format(date);
				preference.saveUserNickNameTime(dateNowStr,"wlcg"+EMClient.getInstance().getCurrentUser());
				String enterType=Preference.getPreferences().getUserEnterType();
				String nickName="";
				if(enterType.equals("account")){//账户登录
					nickName = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.NICKNAME);
					if(nickName.equals("")){
						nickName=preference.getString(nickName, Build.MODEL);
					}
				}else{//网关直接登录
					nickName=preference.getString(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), Build.MODEL);
				}
				preference.saveUserNickName(nickName,"wlcg"+EMClient.getInstance().getCurrentUser());
				if(!StringUtil.isNullOrEmpty(preference.getUserNickName("wlcg"+EMClient.getInstance().getCurrentUser()))){
					message.setAttribute("nickSetTime",preference.getUserNickNameTime("wlcg"+EMClient.getInstance().getCurrentUser()));
					message.setAttribute("nickName",preference.getUserNickName("wlcg"+EMClient.getInstance().getCurrentUser()));
				}

			}

			@Override
			public void onEnterToChatDetails() {

			}

			@Override
			public void onAvatarClick(String username) {
//				Toast.makeText(SocialMessageActivity.this,"onAvatarClick",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAvatarLongClick(String username) {

			}

			@Override
			public boolean onMessageBubbleClick(EMMessage message) {
				return false;
			}

			@Override
			public void onMessageBubbleLongClick(EMMessage message) {

			}

			@Override
			public boolean onExtendMenuItemClick(int itemId, View view) {
				return false;
			}

			@Override
			public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
				return null;
			}
		});

	}

	
}
