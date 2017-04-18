package cc.wulian.smarthomev5.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.utils.DateUtil;

/**
 * Created by Administrator on 2016/10/25.
 */

public class HyphenateManager {

    private static HyphenateManager instance;

    private HyphenateManager() {
    }

    public static HyphenateManager getHyphenateManager() {

        if (instance == null) {
            instance = new HyphenateManager();
        }
        return instance;
    }

    /**
     * 初始化配置，需要在Application中进行初始
     * @param context
     */
    public void initConfig(Context context) {
        initEaseUI(context);
        initEaseMobileSDK(context);
    }

    private void initEaseMobileSDK(Context context) {
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(false);
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(true);
    }

    private void initEaseUI(Context context) {
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(true);
        options.setAutoLogin(false);
        EaseUI.getInstance().init(context, options);
    }

    /**
     * 检验环信账号并进行登录
     * @param gwID
     * @param isAccount
     * @param activity
     * @param onSigninSuccessListener
     */
    public void checkChatAccountMessage(String gwID,boolean isAccount, Activity activity,OnSigninSuccessListener onSigninSuccessListener) {
        String chatCurrentUserName = Preference.getPreferences().getChatCurrentUserName(gwID);
        if (chatCurrentUserName.equals("")) {
            if(StringUtil.isNullOrEmpty(gwID)){
                return;
            }
            //注册聊天账号
            String userName="";
            if(isAccount){
                userName = gwID;
            }else {
                userName = gwID + DateUtil.now();
            }
            String password = "wulian";
            registerChatAccount(gwID, userName, password, activity,onSigninSuccessListener);
        } else {
            signinChatAccount(gwID, activity,onSigninSuccessListener);
        }
    }

    private void registerChatAccount(final String gwID, final String userName, final String pwd, final Activity activity, final OnSigninSuccessListener onSigninSuccessListener) {

        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(userName, pwd);
                    Preference.getPreferences().saveChatCurrentUserName(userName,gwID);
                    Preference.getPreferences().saveChatPassword(pwd);
                    signinChatAccount(gwID, activity,onSigninSuccessListener);
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(activity, activity.getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
//                        }
//                    });

                } catch (final HyphenateException e) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode=e.getErrorCode();
                            if(errorCode== EMError.USER_ALREADY_EXIST){
                                Preference.getPreferences().saveChatCurrentUserName(userName,gwID);
                                Preference.getPreferences().saveChatPassword(pwd);
                                signinChatAccount(gwID, activity,onSigninSuccessListener);
                            }else {
//                            if(errorCode == EMError.NETWORK_ERROR){
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
//                            }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
//                            }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
//                            }else{
//                            Toast.makeText(activity, "注册聊天账号失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * 登录账号
     * @param gwID
     * @param activity
     * @param onSigninSuccessListener
     */
    private void signinChatAccount(final String gwID, final Activity activity, final OnSigninSuccessListener onSigninSuccessListener) {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                if (!Preference.getPreferences().getChatCurrentUserName(gwID).equals("")) {
                    String userName = Preference.getPreferences().getChatCurrentUserName(gwID);
                    String password = Preference.getPreferences().getChatPassword();
                    EMClient.getInstance().logout(true);
                    EMClient.getInstance().login(userName, password, new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            onSigninSuccessListener.doSomeThing();
//                            checkGroupID(gwID);
//                            activity.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(activity, "登录聊天服务器成功", Toast.LENGTH_SHORT).show();
//                                }
//                            });

                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {
                            if(code==200){
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                onSigninSuccessListener.doSomeThing();
                            }
//                            Log.d("main", "登录聊天服务器失败！");
//                            activity.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(activity, "登录聊天服务器失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        }
                    });
                }
            }
        });
    }

    private String groupID = "";

    /**
     * 检验群组信息
     * @param gwID
     * @param checkGroupIDListener
     */
    public void checkGroupID(final String gwID, final CheckGroupIDListener checkGroupIDListener) {
        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {

                creatGroup(gwID);

                try {
                    EMClient.getInstance().groupManager().joinGroup(groupID);
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                }
                checkGroupIDListener.doSomeThing(groupID);

            }
        });
    }

    public interface CheckGroupIDListener{
        public void doSomeThing(String groupID);
    }

    /**
     * 创建群组
     * @param gwID
     */
    private void creatGroup(String gwID) {
        //创建群组
        try {
            EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
            option.maxUsers = 200;
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
            String []menmber={};
            EMGroup emGroup=EMClient.getInstance().groupManager().createGroup(gwID, "", menmber, "", option);
            groupID=emGroup.getGroupId();
        } catch (final HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从服务器获取群组信息
     * @param gwID
     */
    private void getGroupIDFromServer(String gwID) {
        try {
            String cursor = null;
            int pageSize = -1;
            EMCursorResult<EMGroupInfo> result = EMClient.getInstance().groupManager().getPublicGroupsFromServer(pageSize, cursor);//需异步处理
            List<EMGroupInfo> returnGroups = result.getData();
            for (EMGroupInfo emGroupInfo : returnGroups) {
                //TODO
                if (emGroupInfo.getGroupName().equals(gwID)) {
                    groupID = emGroupInfo.getGroupId();
                }
            }
        } catch (final HyphenateException e) {
            e.printStackTrace();
        }
    }

    public interface MessageArrivedListener{
        public void onMessageReceived(List<EMMessage> var1);

        public void onCmdMessageReceived(List<EMMessage> var1);

        public void onMessageReadAckReceived(List<EMMessage> var1);

        public void onMessageDeliveryAckReceived(List<EMMessage> var1);

        public void onMessageChanged(EMMessage var1, Object var2);
    }

    private MessageArrivedListener messageArrivedListener;

    public void setMessageArrivedListener(MessageArrivedListener messageArrivedListener){
        this.messageArrivedListener=messageArrivedListener;
    }

    public void messageArrivedListaner(){
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                messageArrivedListener.onMessageReceived(list);
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {
                messageArrivedListener.onCmdMessageReceived(list);
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {
                messageArrivedListener.onMessageReadAckReceived(list);
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {
                messageArrivedListener.onMessageDeliveryAckReceived(list);
            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {
                messageArrivedListener.onMessageChanged(emMessage,o);
            }
        });
    }

    /**
     * 获取当前群组的群组ID
     * @return
     */
    public String getGroupID(){
        return groupID;
    }

    public void setGroup(String groupID){
        this.groupID=groupID;
    }

    public void logout() {
        groupID="";
    }

    public interface OnSigninSuccessListener{
        public void doSomeThing();
    }


}
