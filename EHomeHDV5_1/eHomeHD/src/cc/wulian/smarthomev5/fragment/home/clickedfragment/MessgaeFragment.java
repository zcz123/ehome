package cc.wulian.smarthomev5.fragment.home.clickedfragment;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseChatFragment;

import java.util.List;

import cc.wulian.smarthomev5.fragment.home.HomeFragment;
import cc.wulian.smarthomev5.tools.HyphenateManager;

/**
 * Created by Administrator on 2017/1/23.
 */

public class MessgaeFragment extends EaseChatFragment{

    @Override
    public void onResume() {
        super.onResume();
        HyphenateManager.getHyphenateManager().setMessageArrivedListener(new HyphenateManager.MessageArrivedListener() {

            @Override
            public void onMessageReceived(List<EMMessage> var1) {
                for (EMMessage message : var1) {
                    String userNick=message.getStringAttribute("nickName","noData");
                    String nickSetTime=message.getStringAttribute("nickSetTime","0");
                    if(!userNick.equals("noData")){
                        String localTime=preference.getUserNickNameTime(message.getFrom());
                        if(Double.parseDouble(nickSetTime)>Double.parseDouble(localTime)){
                            preference.saveUserNickName(userNick,message.getFrom());
                        }
                    }
                    String username = null;
                    // group message
                    if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                        username = message.getTo();
                    } else {
                        // single chat message
                        username = message.getFrom();
                    }

                    // if the message is for current conversation
                    if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername)) {
                        messageList.refreshSelectLast();
                        EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                    } else {
                        EaseUI.getInstance().getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> var1) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> var1) {
                if(isMessageListInited) {
                    messageList.refresh();
                }
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> var1) {
                if(isMessageListInited) {
                    messageList.refresh();
                }
            }

            @Override
            public void onMessageChanged(EMMessage var1, Object var2) {
                if(isMessageListInited) {
                    messageList.refresh();
                }
            }
        });
    }
}
