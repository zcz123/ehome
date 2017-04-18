package cc.wulian.smarthomev5.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdhz.hezisdk.bean.HzSDKBean;
import com.hdhz.hezisdk.enums.HzSDKEventType;
import com.hdhz.hezisdk.views.HzSDKBannerView;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.exceptions.HyphenateException;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.AccountInformationSettingManagerActivity;
import cc.wulian.smarthomev5.activity.AlarmMessageActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.activity.MonitorActivity;
import cc.wulian.smarthomev5.activity.SocialMessageActivity;
import cc.wulian.smarthomev5.adapter.AlarmMessageAdapter;
import cc.wulian.smarthomev5.adapter.MessagePushAdapter;
import cc.wulian.smarthomev5.adapter.OtherMessageAdapter;
import cc.wulian.smarthomev5.adapter.SceneQuickInfoAdapter;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.dao.MessageDao;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.AdvertisementEntity;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.event.AlarmEvent;
import cc.wulian.smarthomev5.event.GatewayCityEvent;
import cc.wulian.smarthomev5.event.GatewayEvent;
import cc.wulian.smarthomev5.event.MessageEvent;
import cc.wulian.smarthomev5.event.SceneEvent;
import cc.wulian.smarthomev5.event.SocialEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.fragment.setting.gateway.AccountInformationSettingManagerFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.AnnouncementManager;
import cc.wulian.smarthomev5.tools.AnnouncementManager.Announcement;
import cc.wulian.smarthomev5.tools.FrontBackgroundManager;
import cc.wulian.smarthomev5.tools.FrontBackgroundManager.FrontBackgroundListener;
import cc.wulian.smarthomev5.tools.HyphenateManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.view.CircleImageView;

public class HomeFragment extends WulianFragment implements ICommand406_Result {

    private NavigationFragment navigationFragment;

    private HomeManager homeManager = HomeManager.getInstance();
    private AnnouncementManager announcementManager = AnnouncementManager
            .getInstance();

    private CircleImageView iconImageView;

    @ViewInject(R.id.gridViewShowInfo)
    private GridView fastSceneListView;
    private SceneQuickInfoAdapter mSceneEditAdapter;
    private SceneDao sceneDao = SceneDao.getInstance();

    @ViewInject(R.id.home_alarm_message_icon)
    private ImageView alarmMessageIconImageView;
    @ViewInject(R.id.home_alarm_message_content_lv)
    private ListView alarmMessageListView;
    private AlarmMessageAdapter alarmMessageAdapter;
    @ViewInject(R.id.home_alarm_message_ll)
    private LinearLayout alarmMessageContentLinearLayout;

    @ViewInject(R.id.home_other_message_iv)
    private ImageView otherMessageImageView;
    @ViewInject(R.id.home_other_message_lv)
    private ListView otherMessageListView;
    private MessageDao messageDao = MessageDao.getInstance();
    private OtherMessageAdapter otherMessageAdapter;

    private MessagePushAdapter mSocialInfoAdapter;
    @ViewInject(R.id.home_chat_message)
    private ImageView messageChatImageView;
    @ViewInject(R.id.home_publish_info)
    private Button messagePublishButton;

    @ViewInject(R.id.home_monitor)
    private View monitorView;

    @ViewInject(R.id.advertiseemnt_viewPager)
    private ViewPager advetrtisementViewPager;
    private AdvertisementPagerAdapter advertisementPagerAdapter;
    private boolean isContinue = true;
    @ViewInject(R.id.indicator)
    private CirclePageIndicator circlePageIndicator;
    private LinearLayout flowerAdvertiseLinearLayout;// 梦想之花广告


    //  add by likai

    @ViewInject(R.id.home_scene_message)
    private LinearLayout linScene;
    @ViewInject(R.id.home_alarm_message_ll)
    private LinearLayout linAlarm;
    @ViewInject(R.id.lin_home_message)
    private LinearLayout linMessage;
    @ViewInject(R.id.fragment_message_send_layout)
    private LinearLayout linMessageSend;

    private List<View> views;
    public static EaseChatMessageList messageList;
    FragmentCallBack fragmentCallBack = null;
    private float x_tmp1 = 0 ;
    private float x_tmp2 ;

    private List<AdvertisementEntity> advertisementEntites;
    private int chatType =2;
    // userId you are chat with or group id
    private String toChatUsername = "";

    protected ListView listView;
    protected EaseChatFragment.GroupListener groupListener;
    protected EMConversation conversation;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected int pagesize = 20;
    private Preference mPreference;
    protected boolean isloading;
    protected boolean haveMoreData = true;
    private Command406_DeviceConfigMsg command406 = null;
    public final static String GW_GROUP_ID_KEY="WLChatroomID";
    private boolean isSend406FromHere=false;
    protected List<LinearLayout> linearLayoutViews = new ArrayList<>();

    private Comparator<SceneInfo> sceneComparator = new Comparator<SceneInfo>() {

        @Override
        public int compare(SceneInfo lhs, SceneInfo rhs) {
            String leftSceneName = lhs.getName();
            String leftSceneID = lhs.getSceneID();
            String rightSceneName = rhs.getName();
            String rightSceneID = rhs.getSceneID();
            int result = Trans2PinYin
                    .trans2PinYin(leftSceneName.trim())
                    .toLowerCase()
                    .compareTo(
                            Trans2PinYin.trans2PinYin(rightSceneName.trim())
                                    .toLowerCase());
            if (result != 0) {
                return result;
            } else {
                return leftSceneID.compareTo(rightSceneID);
            }
        }
    };

    private Runnable autoPlayRunnable = new Runnable() {

        @Override
        public void run() {
            if (!isContinue)
                return;
            if (advertisementPagerAdapter == null
                    || advertisementPagerAdapter.getCount() <= 0)
                return;
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    advetrtisementViewPager
                            .setCurrentItem(getCurrentPageIndex(false));
                }
            });
        }
    };

    //网关的图片   右上角
    private Runnable connectingRunnable = new Runnable() {

        @Override
        public void run() {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (iconImageView != null && iconImageView.getTag() != null) {
//						Logger.debug("start connecting......");
                        Object imageRes = iconImageView.getTag();
                        int nextConnectingRes = R.drawable.home_gateway_connecting_1;
                        if (StringUtil.toInteger(imageRes) == R.drawable.home_gateway_connecting_1) {
                            nextConnectingRes = R.drawable.home_gateway_connecting_2;
                        } else if (StringUtil.toInteger(imageRes) == R.drawable.home_gateway_connecting_2) {
                            nextConnectingRes = R.drawable.home_gateway_connecting_3;
                        }
                        iconImageView.setTag(nextConnectingRes);
                        iconImageView.setImageResource(nextConnectingRes);
                    }
                }
            });

        }
    };
    private FrontBackgroundListener frontBackgroundListener = new FrontBackgroundListener() {

        @Override
        public void onBackground(boolean isBackground) {
            Logger.debug("background or front is trigger:" + isBackground);
            if (!isBackground) {
                if (mApplication.getResources().getBoolean(
                        R.bool.use_home_adver)) {
                    long lastTime = Preference.getPreferences().getLong(
                            IPreferenceKey.P_KEY_GO_BACKGROPUND_TIME, 0);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime > 5 * 60 * 1000) {
//						HomeFragment.this.loadAdvertisements();
                        loadAnnouncement();
                    }
                }
                if (mApplication.mBackNotification != null)
                    mApplication.mBackNotification
                            .cancelNotification(R.drawable.app_icon_on);
                homeManager.notifyLogined();
            } else {
                homeManager.notifyExit();
            }
        }
    };
    private InformationWeatherStatusItem informationWeatherStatusItem;
    private LinearLayout informationWeather;
    private String cityID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        informationWeather = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.wwather_layout,null);
        if(!LanguageUtil.isChina()){
            informationWeather.setBackgroundResource(R.drawable.information_weather_en_bg);
        }
        //加载天气信息
        startWeatherModule();
        FragmentManager manager = mActivity.getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
                .getName());
        if (fragment != null) {
            navigationFragment = (NavigationFragment) fragment;
        }
        mSceneEditAdapter = new SceneQuickInfoAdapter(mActivity, null);
        alarmMessageAdapter = new AlarmMessageAdapter(mActivity, null);
        mSocialInfoAdapter = new MessagePushAdapter(mActivity, null);
        advertisementPagerAdapter = new AdvertisementPagerAdapter(
                createDefaultAdvertiseList());
        otherMessageAdapter = new OtherMessageAdapter(mActivity, null);
        FrontBackgroundManager.getInstance().addFrontBackgroundListener(
                frontBackgroundListener);

        loadActionBarData();

    }

    private void loadAnnouncement() {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                if (mApplication.getResources().getBoolean(R.bool.use_about_us)) {
                    // announcementManager.loadAnnouncements(mAccountManger.getRegisterInfo().getAppID(),gwID);
                    announcementManager.loadNoties(mAccountManger
                                    .getRegisterInfo().getAppID(),
                            mAccountManger.getmCurrentInfo().getGwID());
                    announcementManager.checkAnnouncements();
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            navigationFragment.refreshLeftMenuRedDot();
                        }
                    });
                    final Announcement lotterActiveAnnouncement = announcementManager
                            .getShowLotteryAnnouncement();
                    if (lotterActiveAnnouncement != null) {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                startLotteryActive(lotterActiveAnnouncement);
                            }
                        });
                    }
                }
            }
        });

    }

	/**
	 * 加载广告
	 */
	private void loadAdvertisements() {
		if(!mApplication.getResources().getBoolean(R.bool.use_home_adver)) {
			//如果config设置不加载广告，就不发请求
			return;
		}
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				homeManager.setAdvArrivedListener(new HomeManager.AdvArrivedListener() {
					@Override
					public void showAdvPicture() {
						advertisementEntites= homeManager.getAdvertisementEntites();
//						if (advertisementEntites == null || advertisementEntites.isEmpty()) {
//							try {
//								Thread.sleep(4500);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							advertisementEntites=homeManager.getAdvertisementEntites();
//							if(advertisementEntites == null || advertisementEntites.isEmpty()){
//								return;
//							}
//						}
                        if(advertisementEntites == null || advertisementEntites.isEmpty()){
                            return;
                        }
                        for (int i = advertisementEntites.size() - 1; i >= 0; i--) {
                            AdvertisementEntity entity = advertisementEntites.get(i);
                            String fileName = entity.getPictureIndex() + "_"
                                    + entity.getVersion() + ".png";
                            String floder = FileUtil.getAdvertisementPath();
                            if (!FileUtil.checkFileExistedAndAvailable(floder + "/"
                                    + fileName)) {
                                String url = entity.getPictureURL();
                                byte[] bytes = HttpUtil.getPicture(url);
                                if (bytes != null) {
                                    Bitmap bitMap = FileUtil.Bytes2Bitmap(bytes);
                                    FileUtil.saveBitmapToPng(bitMap, floder, fileName);
                                } else {
                                    advertisementEntites.remove(i);
                                }
                            }
                        }
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                showPictures(advertisementEntites);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_message_display,
                container, false);
        messageList=(EaseChatMessageList) view.findViewById(R.id.home_social_infos);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(com.hyphenate.easeui.R.color.holo_blue_bright, com.hyphenate.easeui.R.color.holo_green_light,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);
        fastSceneListView.setAdapter(mSceneEditAdapter);
        alarmMessageListView.setAdapter(alarmMessageAdapter);
        alarmMessageContentLinearLayout
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity,
                                AlarmMessageActivity.class);
                        mActivity.startActivity(intent);

                    }
                });
        alarmMessageListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mActivity,
                        AlarmMessageActivity.class);
                mActivity.startActivity(intent);
            }
        });
        alarmMessageIconImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,
                        AlarmMessageActivity.class);
                mActivity.startActivity(intent);
            }
        });
        otherMessageListView.setAdapter(otherMessageAdapter);

//		socialInfoList.setAdapter(mSocialInfoAdapter);

        messageChatImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToChatActivity();

            }
        });
        messagePublishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToChatActivity();
            }
        });
        monitorView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(UserRightUtil.getInstance().canEnter(UserRightUtil.EntryPoint.CAMERA) == false) {
                    return;
                }
                mActivity.JumpTo(MonitorActivity.class);
            }
        });
        messageList.getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jumpToChatActivity();
            }
        });
        //首页留言的刷新功能
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(com.hyphenate.easeui.R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });


        advetrtisementViewPager.setAdapter(advertisementPagerAdapter);
        circlePageIndicator.setViewPager(advetrtisementViewPager);
        advetrtisementViewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fragmentCallBack.callbackOpean(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        views = new ArrayList<View>();
        views.add(view);
        views.add(linScene);
        views.add(linAlarm);
        views.add(fastSceneListView);
        views.add(linMessage);
        views.add(alarmMessageListView);
        views.add(linMessageSend);
        views.add(messageList);
        viewSetTouch(views);

        loadAdvertisements();

        mPreference=Preference.getPreferences();

    }

    private void jumpToChatActivity() {
        if(!StringUtil.isNullOrEmpty(toChatUsername)){
            Intent intent = new Intent(mActivity,
                    SocialMessageActivity.class);
            intent.putExtra(SocialMessageActivity.GROUP_ID, toChatUsername);
            mActivity.startActivity(intent);
        }
    }

    private void startWeatherModule() {
        informationWeatherStatusItem = new InformationWeatherStatusItem(this.getActivity());
        if (LanguageUtil.isChina()) {
            informationWeather.addView(informationWeatherStatusItem.getView(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            HomeManager.getWeatherMessage(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), new HomeManager.GetWeatherListener(){
                @Override
                public void doSomeThing(WeatherEntity entity) {
                    informationWeather.setBackgroundResource(R.drawable.homepage_weather_bg);
                    if(informationWeather!=null){
                        informationWeatherStatusItem.changeStatus(entity);
                    }
                }
            });
        }
    }

    private void sheduleHomeRunnable() {
        if (navigationFragment.isShownFragment(HomeFragment.class.getName())) {
            TaskExecutor.getInstance().addScheduled(autoPlayRunnable, 0, 5000,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void removeHomeShedule() {
        TaskExecutor.getInstance().removeScheduled(autoPlayRunnable);
    }

    @Override
    public void onShow() {
        super.onShow();
        sheduleHomeRunnable();
        loadActionBarData();
    }

    @Override
    public void onHide() {
        super.onHide();
        removeHomeShedule();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAccountManger.setConnectGatewayCallbackAndActivity(callback, this.getActivity());
        sheduleHomeRunnable();
        loadHomeData();
        loadSocialData();
        messageListaner();
        TaskExecutor.getInstance().executeDelay(new Runnable() {

            @Override
            public void run() {
                loadActionBarData();
            }
        }, 3000);
    }

    public void messageListaner(){
        HyphenateManager.getHyphenateManager().messageArrivedListaner();
        HyphenateManager.getHyphenateManager().setMessageArrivedListener(new HyphenateManager.MessageArrivedListener() {
            @Override
            public void onMessageReceived(List<EMMessage> var1) {
                messageList.refreshSelectLast();
                for (EMMessage message : var1) {
                    String userNick=message.getStringAttribute("nickName","noData");
                    String nickSetTime=message.getStringAttribute("nickSetTime","0");
                    if(!userNick.equals("noData")){
                        String localTime=mPreference.getUserNickNameTime(message.getFrom());
                        if(Double.parseDouble(nickSetTime)>Double.parseDouble(localTime)){
                            mPreference.saveUserNickName(userNick,message.getFrom());
                        }
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> var1) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> var1) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> var1) {

            }

            @Override
            public void onMessageChanged(EMMessage var1, Object var2) {

            }
        });
    }

    public AccountManager.ConnectGatewayCallback callback = new  AccountManager.ConnectGatewayCallback() {

        @Override
        public void connectSucceed() {
            mAccountManger.checkGatewayType(getActivity());
            loadSocialData();
        }

        @Override
        public void connectFailed(int reason) {
            if(reason == ResultUtil.EXC_GW_PASSWORD_WRONG) {
                mAccountManger.exitCurrentGateway(HomeFragment.this.mActivity);
            } else {
                Log.e("MainHomeActivity", "Connect gateway failed:"+reason);
            }
        }

    };

    @Override
    public void onPause() {
        super.onPause();
        removeHomeShedule();
        TaskExecutor.getInstance().removeScheduled(connectingRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccountManger.clearConnectGatewayCallbackAndActivity(callback);
        FrontBackgroundManager.getInstance().removeFrongBackgroundListener(
                frontBackgroundListener);
    }

    private void initBar() {
        if (navigationFragment.getNavAdatper()==null||navigationFragment.isCurrentHome()) {
            mActivity.resetActionMenu();
            getSupportActionBar().setDisplayShowCustomMenuEnable(true);
            getSupportActionBar().setTitle(
                    mApplication.getResources().getString(
                            R.string.nav_home_title));
            getSupportActionBar().setRightMenuClickListener(
                    new OnRightMenuClickListener() {

                        @Override
                        public void onClick(View v) {
                            mAccountManger.signinDefaultAccount();
                            mActivity
                                    .JumpTo(AccountInformationSettingManagerActivity.class);
                        }
                    });
            LinearLayout menuLinearLayout = getSupportActionBar()
                    .getRightMenuCustomLayout();
            menuLinearLayout.removeAllViews();
            iconImageView = new CircleImageView(mActivity, null, 0);
            iconImageView.setLayoutParams(new ViewGroup.LayoutParams(
                    getSupportActionBar().getHeight() * 2 / 3,
                    getSupportActionBar().getHeight() * 4 / 5));
            if (!mAccountManger.isConnectedGW()) {
                if (mAccountManger.isSigning(mAccountManger.getmCurrentInfo()
                        .getGwID())) {
                    iconImageView.setTag(R.drawable.home_gateway_connecting_1);
                    TaskExecutor.getInstance().addScheduled(connectingRunnable,
                            0, 2000, TimeUnit.MILLISECONDS);
                } else {
                    iconImageView.setTag(null);
                    TaskExecutor.getInstance().removeScheduled(
                            connectingRunnable);
                    iconImageView
                            .setImageResource(R.drawable.home_gateway_disconnect);
                }
            } else {
                iconImageView.setTag(null);
                TaskExecutor.getInstance().removeScheduled(connectingRunnable);
                try {
                    String filePath = FileUtil
                            .getGatewayDirectoryPath(mAccountManger.getmCurrentInfo()
                                    .getGwID())
                            + "/"
                            + AccountInformationSettingManagerFragment.PICTURE_GATEWAY_HEAD;
                    if (FileUtil.checkFileExistedAndAvailable(filePath)) {
                        Bitmap bm = BitmapFactory.decodeFile(filePath);
                        if (bm != null) {
                            iconImageView.setImageBitmap(bm);
                        }
                    } else {
                        if (mAccountManger.getmCurrentInfo().isFlowerGatewayNoDisk()
                                || mAccountManger.getmCurrentInfo()
                                .isFlowerGatewayWithDisk()) {
                            iconImageView
                                    .setImageResource(R.drawable.gateway_head_dream_flower);
                        } else
                            iconImageView
                                    .setImageResource(R.drawable.home_gateway_connected);
                    }

                } catch (Exception e) {
                    iconImageView
                            .setImageResource(R.drawable.home_gateway_disconnect);
                }
            }
            menuLinearLayout.addView(iconImageView);
        }
    }

    private void loadHomeData() {
        loadActionBarData();
        loadScenesData();
        loadAlarmMessageData();
        loadOtherMessageData();
    }

    private void loadActionBarData() {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                initBar();
            }
        });
    }

    public void loadScenesData() {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // SceneInfo info = new SceneInfo();
                // info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
                // final List<SceneInfo> infos = sceneDao.findListAll(info);
                final List<SceneInfo> infos = getFavoritySceneList();
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mSceneEditAdapter.swapData(infos);
                    }
                });

            }
        });
    }

    private List<SceneInfo> getFavoritySceneList() {
        List<SceneInfo> infos = new ArrayList<SceneInfo>();
        FavorityEntity mEntity = new FavorityEntity();
        mEntity.setGwID(mAccountManger.getmCurrentInfo().getGwID());
        mEntity.setType(Favority.TYPE_SCENE);
        FavorityDao favorityDao = FavorityDao.getInstance();
        List<FavorityEntity> favorityEntities = favorityDao
                .findListAll(mEntity);
        for (FavorityEntity entity : favorityEntities) {
            SceneInfo sceneInfo = MainApplication.getApplication().sceneInfoMap
                    .get(entity.getGwID() + entity.getOperationID());
            if (sceneInfo != null) {
                infos.add(sceneInfo);
            } else {
                favorityDao.delete(entity);
            }
        }
        Collections.sort(infos, sceneComparator);
        if (infos.isEmpty()) {
            SceneInfo info = new SceneInfo();
            info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
            infos = sceneDao.findListAll(info);
        }
        return infos;
    }

    /**
     * 加载留言信息
     * 1 先检验并登录账户
     * 2 在登录成功的回调里检验公共群组并获取
     * 3 在检验群组的回调里根据群组ID加载群组信息
     */
    private void loadSocialData() {
        messageList.setShowUserNick(true);
        listView = messageList.getListView();
        if (command406 == null||(!command406.getGwID().equals(mAccountManger.getmCurrentInfo().getGwID()))) {
            command406 = new Command406_DeviceConfigMsg(mActivity);
            command406.setConfigMsg(this);
            command406.setDevID("self");
            command406.setGwID(mAccountManger.getmCurrentInfo().getGwID());
        }
        command406.SendCommand_Get(GW_GROUP_ID_KEY);
        isSend406FromHere=true;
    }

    private String getChatAccount(){
        String userID="";
        if(mPreference.isUseAccount()){
            userID=SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.USERID);
        }else {
            userID=mAccountManger.getmCurrentInfo().getGwID();
        }
        if(!StringUtil.isNullOrEmpty(userID)){
            return userID;
        }
        return "";
    }

    protected void onConversationInit(){
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        conversation.markAllMessagesAsRead();
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {

            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
        if(msgCount>0){
            messageList.setVisibility(View.VISIBLE);
            messageChatImageView.setVisibility(View.GONE);
        }else{
            messageList.setVisibility(View.GONE);
            messageChatImageView.setVisibility(View.VISIBLE);
        }
    }

    protected EaseChatFragment.EaseChatFragmentHelper chatFragmentHelper;
    public void setChatFragmentListener(EaseChatFragment.EaseChatFragmentHelper chatFragmentHelper){
        this.chatFragmentHelper = chatFragmentHelper;
    }

    protected void onMessageListInit(){
        messageList.init(toChatUsername, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
//		setListItemClickListener();
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private List<LinearLayout> createDefaultAdvertiseList() {
        List<LinearLayout> defaultImageView = new ArrayList<LinearLayout>();
        ImageView imageView1 = new ImageView(mActivity);
        imageView1.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        imageView1.setAdjustViewBounds(true);
        imageView1.setImageResource(R.drawable.advertisement1);
        BitmapDrawable bitmapDrawable1 = (BitmapDrawable) imageView1
                .getDrawable();
        LinearLayout linearLayout1 = new LinearLayout(mActivity);
        linearLayout1.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        if (Build.VERSION.SDK_INT < 16) {
            linearLayout1.setBackgroundDrawable(bitmapDrawable1);
        } else {
            linearLayout1.setBackground(bitmapDrawable1);
        }
        linearLayout1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                IntentUtil.startBrowser(mActivity, "http://www.wuliangroup.com");
            }
        });
        defaultImageView.add(linearLayout1);

        ImageView imageView2 = new ImageView(mActivity);
        imageView2.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        imageView2.setAdjustViewBounds(true);
        imageView2.setImageResource(R.drawable.advertisement2);
        BitmapDrawable bitmapDrawable2 = (BitmapDrawable) imageView2
                .getDrawable();
        LinearLayout linearLayout2 = new LinearLayout(mActivity);
        linearLayout2.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        if (Build.VERSION.SDK_INT < 16) {
            linearLayout2.setBackgroundDrawable(bitmapDrawable2);
        } else {
            linearLayout2.setBackground(bitmapDrawable2);
        }
        linearLayout2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                IntentUtil.startBrowser(mActivity, "http://www.wuliangroup.com");
            }
        });
        defaultImageView.add(linearLayout2);
        linearLayoutViews.clear();
        linearLayoutViews.addAll(defaultImageView);
        return defaultImageView;
    }

    private int getCurrentPageIndex(boolean isFirst) {
        int index = 0;
        if (isFirst) {
            index = (int) (Math.random() * advertisementPagerAdapter.getCount());
        } else {
            index = advetrtisementViewPager.getCurrentItem() + 1;
            if (index >= advertisementPagerAdapter.getCount()) {
                index = 0;
            }
        }
        return index;
    }

    /**
     * 显示图片
     */
    private void showPictures(List<AdvertisementEntity> advertisementEntites) {
        List<LinearLayout> linearLayouts = new ArrayList<LinearLayout>();
        if (isThereData){
            linearLayouts.add(informationWeather);
        }
        for (AdvertisementEntity entity : advertisementEntites) {
            if(entity.getPictureLinkURL().contains("isActivity=true")){
                HzSDKBannerView bannerView=new HzSDKBannerView(getActivity());
                bannerView.setLayoutParams(new FrameLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                        Gravity.CENTER));
                bannerView.setDefaultLoadingBg(R.drawable.ic_launcher);
                HzSDKBean sdkBean = new HzSDKBean();
                sdkBean.setEvent(HzSDKEventType.LOGIN.getType());
                sdkBean.setPositionKey("6d4b0c4692");
                String appID=AccountManager.getAccountManger().getRegisterInfo().getAppID();
                sdkBean.setUserName(appID);
                sdkBean.setMobile(appID);
                sdkBean.setHzBarBackground(getResources().getColor(R.color.red));
                bannerView.disPlayWithBean(sdkBean);//36494ed311
                LinearLayout heziLinearLayout = new LinearLayout(mActivity);
                heziLinearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                        Gravity.CENTER));
                heziLinearLayout.addView(bannerView);
                linearLayouts.add(heziLinearLayout);
            }else{
                String fileName = FileUtil.getAdvertisementPath() + "/"
                        + entity.getPictureIndex() + "_" + entity.getVersion()
                        + ".png";
                boolean isExit = FileUtil.checkFileExistedAndAvailable(fileName);
                if (isExit) {
                    Uri.Builder builder = new Uri.Builder();
                    builder.path(fileName);
                    LinearLayout linearLayout = makeAdvertisementImageView(entity,
                            builder.build());
                    linearLayouts.add(linearLayout);
                }
            }
        }
        // 梦想之花广告
        if (mAccountManger.getmCurrentInfo().isFlowerGatewayNoDisk()
                || mAccountManger.getmCurrentInfo().isFlowerGatewayWithDisk()) {
            flowerAdvertiseLinearLayout = (LinearLayout) View.inflate(
                    mActivity, R.layout.flower_advertise_layout, null);
            linearLayouts.add(flowerAdvertiseLinearLayout);
        }
        if (!linearLayouts.isEmpty()) {
            advertisementPagerAdapter.swapData(linearLayouts);
            advetrtisementViewPager.setCurrentItem(getCurrentPageIndex(true));
        }
        linearLayoutViews.clear();
        linearLayoutViews.addAll(linearLayouts);
    }

    // 刷新梦想之花广告
    private void refreshFlowerAdvertise(List<MessageEventEntity> entitys) {
        if (flowerAdvertiseLinearLayout == null)
            return;
        for (MessageEventEntity entity : entitys) {
            if (ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_PM2P5
                    .equals(entity.epType)) { // pm2.5
                int ratio = StringUtil.toInteger(entity.epData.substring(0,
                        (entity.epData.length() - 6)), 10);
                String hint = mApplication
                        .getString(R.string.device_link_task_detection_degree_a);
                if (ratio > 80 && ratio <= 180) {
                    hint = mApplication
                            .getString(R.string.device_link_task_detection_degree_b);
                } else if (ratio > 180 && ratio <= 240) {
                    hint = mApplication
                            .getString(R.string.device_link_task_detection_degree_mild);
                } else if (ratio > 240 && ratio <= 320) {
                    hint = mApplication
                            .getString(R.string.device_link_task_detection_degree_moderate);
                } else if (ratio > 320) {
                    hint = mApplication
                            .getString(R.string.device_link_task_detection_degree_severe);
                }
                TextView air = (TextView) flowerAdvertiseLinearLayout
                        .findViewById(R.id.air_quqlity_hint);
                air.setText(hint);
            } else if (ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_VOC
                    .equals(entity.epType)) { // voc
                String hint = mApplication
                        .getString(R.string.device_link_task_detection_degree_b);
                int ratio = StringUtil.toInteger(entity.epData.substring(0,
                        (entity.epData.length() - 4)), 10);
                if (ratio > 100 && ratio <= 200) {
                    hint = mApplication
                            .getString(R.string.house_rule_add_new_link_task_voc_slight);
                } else if (ratio > 200) {
                    hint = mApplication
                            .getString(R.string.house_rule_add_new_link_task_voc_serious);
                }
                TextView voc = (TextView) flowerAdvertiseLinearLayout
                        .findViewById(R.id.voc_hint);
                voc.setText(hint);
            } else if (ConstUtil.DEV_TYPE_FROM_GW_NOISE.equals(entity.epType)) { // 噪音
                String hint = mApplication.getString(R.string.device_d4_quiet);
                int ratio = StringUtil.toInteger(entity.epData.substring(0,
                        (entity.epData.length() - 3)), 10);
                if (ratio > 36 && ratio <= 65) {
                    hint = mApplication.getString(R.string.scene_normal_hint);
                } else if (ratio > 65) {
                    hint = mApplication.getString(R.string.device_d4_noisy);
                }
                TextView noice = (TextView) flowerAdvertiseLinearLayout
                        .findViewById(R.id.noice_hint);
                noice.setText(hint);
            } else if (ConstUtil.DEV_TYPE_FROM_GW_A0.equals(entity.epType)) { // 空气质量
                int ratio = StringUtil.toInteger(entity.epData.substring(0,
                        (entity.epData.length() - 4)), 10); // epData.substring(8,12);
                String hint = mApplication
                        .getString(R.string.device_link_task_detection_degree_a);
                if (ratio > 350 && ratio <= 750) {
                    hint = mApplication
                            .getString(R.string.device_link_task_detection_degree_b);
                } else if (ratio > 750 && ratio <= 1000) {
                    hint = mApplication
                            .getString(R.string.flower_advertise_air_dirty);
                } else if (ratio > 1000 && ratio <= 2500) {
                    hint = mApplication
                            .getString(R.string.flower_advertise_air_hypoxia);
                } else if (ratio > 2500) {
                    hint = mApplication
                            .getString(R.string.flower_advertise_air_severe_hypoxia);
                }
                TextView pm = (TextView) flowerAdvertiseLinearLayout
                        .findViewById(R.id.pm_hint);
                pm.setText(hint);
            }

        }
    }

    /**
     * 创建图片
     *
     * @param entity
     * @param uri
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private LinearLayout makeAdvertisementImageView(
            final AdvertisementEntity entity, Uri uri) {
        LinearLayout linearLayout = new LinearLayout(mActivity);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        ImageView imageView = new ImageView(mActivity);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        imageView.setImageURI(uri);
        imageView.setAdjustViewBounds(true);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView
                .getDrawable();
        if (Build.VERSION.SDK_INT < 16) {
            linearLayout.setBackgroundDrawable(bitmapDrawable);
        } else {
            linearLayout.setBackground(bitmapDrawable);
        }

        linearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                IntentUtil.startBrowser(mActivity, entity.getPictureLinkURL());
            }
        });
        return linearLayout;
    }

    private void loadAlarmMessageData() {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                final List<MessageEventEntity> enties = homeManager
                        .getAlarmMessageEntities();
                final List<MessageEventEntity> filterEnties = new ArrayList<MessageEventEntity>();
                for (MessageEventEntity eventEntity : enties) {
                    if (eventEntity.getEpMsg() != null
                            && (eventEntity.getEpMsg().charAt(0) + "")
                            .equals("N")) {
                        // 过滤epMsg首字母为"N"的报警信息
                        continue;
                    }else {
                        //被授权账号，没有权限的设备不会显示报警信息
                        if(UserRightUtil.getInstance().canSeeDevice(eventEntity.getDevID()) ||
                                UserRightUtil.getInstance().canControlDevice(eventEntity.getDevID())){

                            filterEnties.add(eventEntity);
                        }
                    }
                }
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        alarmMessageAdapter.swapData(filterEnties);
                        if (alarmMessageAdapter.getCount() == 0) {
                            alarmMessageIconImageView
                                    .setVisibility(View.VISIBLE);
                        } else {
                            alarmMessageIconImageView
                                    .setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

    }

    private void loadOtherMessageData() {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                final List<MessageEventEntity> enties = getOtherMessages();
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        refreshFlowerAdvertise(enties); // 刷新梦想之花广告
                        otherMessageAdapter.swapData(enties);
                        if (otherMessageAdapter.getCount() <= 0) {
                            otherMessageImageView.setVisibility(View.VISIBLE);
                        } else {
                            otherMessageImageView.setVisibility(View.INVISIBLE);
                        }

                    }
                });
            }
        });
    }

    private List<MessageEventEntity> getOtherMessages() {
        MessageEventEntity messageEventEntity = new MessageEventEntity();
        messageEventEntity.setGwID(mAccountManger.getmCurrentInfo().getGwID());
        messageEventEntity.setType("'" + Messages.TYPE_DEV_SENSOR_DATA + "'"
                + "," + "'" + Messages.TYPE_DEV_LOW_POWER + "'" + "," + "'"
                + Messages.TYPE_DEV_ONLINE + "'");
        return messageDao.findLastDeviceMessage(messageEventEntity);
    }

    private void startLotteryActive(Announcement entity) {
        // 如果公告中有抽奖活动
        if (entity == null) {
            return;
        }
        String url = entity.getActiveUrl();
        // 首先判断抽奖活动是否有url
        if (!StringUtil.isNullOrEmpty(url)) {
            // 保存已进入抽奖页面记录
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            IntentUtil.startCustomBrowser(mActivity, url, entity
                            .getActiveName(),
                    mApplication.getResources().getString(R.string.about_back));
            // 如果抽奖活动地址为空,调用本地Html显示文本
        } else {
            String texts = entity.getActiveDetail();
            SmarthomeFeatureImpl.setData(
                    SmarthomeFeatureImpl.Constants.TEXT_WILL_SHOW, texts);
            String showURL = URLConstants.LOCAL_BASEURL
                    + "showNoticeContent.html";
            IntentUtil.startHtml5PlusActivity(mActivity, showURL);
        }
    }

    public void onEventMainThread(SceneEvent event) {
        if (homeManager.isHomeRefresh()) {
            loadHomeData();
            homeManager.setHomeRefresh(false);
        } else {
            loadScenesData();
        }
    }

    public void onEventMainThread(AlarmEvent event) {
        if (homeManager.isHomeRefresh()) {
            loadHomeData();
            homeManager.setHomeRefresh(false);
        } else {
            loadAlarmMessageData();
        }
    }

    public void onEventMainThread(MessageEvent event) {
        if (Messages.TYPE_DEV_SENSOR_DATA.equals(event.action)
                || Messages.TYPE_DEV_LOW_POWER.equals(event.action)
                || Messages.TYPE_DEV_ONLINE.equals(event.action)) {
            if (homeManager.isHomeRefresh()) {
                loadHomeData();
                homeManager.setHomeRefresh(false);
            } else {
                loadOtherMessageData();
            }
        }
    }

    public void onEventMainThread(SocialEvent event) {
        if (homeManager.isHomeRefresh()) {
            loadHomeData();
            homeManager.setHomeRefresh(false);
        } else {
            messageList.refreshSelectLast();
        }
    }

    public void onEventMainThread(GatewayEvent event) {
        if (homeManager.isHomeRefresh()) {
            loadHomeData();
            homeManager.setHomeRefresh(false);
        } else {
            loadActionBarData();
        }
    }

    @Override
    public void Reply406Result(Command406Result result) {
        String mode=result.getMode();
        String key=result.getKey();
        if(mode!=null&&mode.equals(Command406_DeviceConfigMsg.mode_get)&&key!=null&&key.equals(GW_GROUP_ID_KEY)&&isSend406FromHere){
            isSend406FromHere=false;
            String id="";
            String json = result.getData();
            JSONObject resultJsonObject = null;
            if (json != null && json.length() > 0) {
                resultJsonObject = JSON.parseObject(json);
            }
            //v:{"id":群组ID,"name":群组名称(网关ID),"owner":群组创建者ID}
            if (resultJsonObject != null) {
                id = resultJsonObject.getString("id");
            }
            final String groupID=id;
            if(StringUtil.isNullOrEmpty(groupID)){
                loadChatView();
            }else{
                HyphenateManager.getHyphenateManager().checkChatAccountMessage(getChatAccount(),mPreference.isUseAccount(), mActivity, new HyphenateManager.OnSigninSuccessListener() {
                    @Override
                    public void doSomeThing() {
                        try {
                            EMClient.getInstance().groupManager().joinGroup(groupID);
                        } catch (final HyphenateException e) {
                            e.printStackTrace();
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(!StringUtil.isNullOrEmpty(groupID)){
                                    HyphenateManager.getHyphenateManager().setGroup(groupID);
                                    toChatUsername=groupID;
                                    onConversationInit();
                                    onMessageListInit();
                                }
                            }
                        });

                    }
                });
            }
        }
    }

    private void setMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }

    public void loadChatView(){
        HyphenateManager.getHyphenateManager().checkChatAccountMessage(getChatAccount(),mPreference.isUseAccount(), mActivity, new HyphenateManager.OnSigninSuccessListener() {
            @Override
            public void doSomeThing() {
                HyphenateManager.getHyphenateManager().checkGroupID(mAccountManger.getmCurrentInfo().getGwID(), new HyphenateManager.CheckGroupIDListener() {
                    @Override
                    public void doSomeThing(final String groupID) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(!StringUtil.isNullOrEmpty(groupID)){
                                    JSONObject jsonObject=new JSONObject();
                                    //v:{"id":群组ID,"name":群组名称(网关ID),"owner":群组创建者ID}
                                    jsonObject.put("id",groupID);
                                    jsonObject.put("name",mAccountManger.getmCurrentInfo().getGwID());
                                    String userID="";
                                    if(mPreference.isUseAccount()){
                                        userID=SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.USERID);
                                    }else {
                                        userID=mAccountManger.getmCurrentInfo().getGwID();
                                    }
                                    String owner=Preference.getPreferences().getChatCurrentUserName(userID);
                                    jsonObject.put("owner",owner);
                                    command406.SendCommand_Add(GW_GROUP_ID_KEY,jsonObject.toString());
                                    toChatUsername=groupID;
                                    onConversationInit();
                                    onMessageListInit();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void Reply406Result(List<Command406Result> results) {

    }

    private class AdvertisementPagerAdapter extends PagerAdapter {

        private List<LinearLayout> viewList;

        public AdvertisementPagerAdapter(List<LinearLayout> list) {
            this.viewList = list;
        }

        @Override
        public int getCount() {
            return viewList != null ? viewList.size() : 0;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager pager = (ViewPager) container;
            View contnetView = (View) object;
            pager.removeView(contnetView);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position), 0);
            return viewList.get(position);
        }

        public void swapData(List<LinearLayout> views) {
            if (views == null)
                return;
            viewList = views;
            notifyDataSetChanged();
        }
    }

    //首页广告与菜单冲突BUG  addby   likai

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentCallBack = (MainHomeActivity) activity;
    }

    public interface FragmentCallBack {
        public void callbackOpean(boolean isOpean);
    }

    public void viewSetTouch(List<View> views){
        for(final View view:views){
            view.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x_tmp1 = x;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            x_tmp2 = x;
                            if (x_tmp1 != 0) {
                                if (x_tmp2 - x_tmp1 > 180) {
                                    fragmentCallBack.callbackOpean(true);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:

                            break;
                    }
                    return false;
                }
            });
        }
    }

    private boolean isThereData = false;

    public void onEventMainThread(GatewayCityEvent event) {
        cityID = event.getGwCityID();
        HomeManager.getWeatherMessage(cityID, new HomeManager.GetWeatherListener() {

            @Override
            public void doSomeThing(WeatherEntity entity) {
                informationWeather.setBackgroundResource(R.drawable.homepage_weather_bg);
                if (informationWeather != null) {
                    isThereData = true;
                    informationWeatherStatusItem.changeStatus(entity);
                }
            }
        });
        if (StringUtil.isNullOrEmpty(cityID) && !isThereData) {
            linearLayoutViews.remove(informationWeather);
        } else {
            linearLayoutViews.remove(informationWeather);
            linearLayoutViews.add(0, informationWeather);
        }
        advertisementPagerAdapter.swapData(linearLayoutViews);
    }


}