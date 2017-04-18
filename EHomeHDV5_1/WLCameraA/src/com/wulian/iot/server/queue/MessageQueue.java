package com.wulian.iot.server.queue;
import android.content.Context;
import android.util.Log;
import com.wulian.iot.utils.IOTResCodeUtil;
import java.util.LinkedList;
import java.util.Queue;
/**
 * Created by syf on 2016/10/18.
 */

public class MessageQueue {
    private static final String TAG = "MessageQueue";
    private Queue<String> msgQueue = null;
    private Context mContext = null;
    private MessageQueue instance = null;
    private String sendMsg = "null";
    public MessageQueue(Context context){
        instance = this;
        instance.mContext = context;
        instance.msgQueue = new LinkedList<String>();
    }
    private void addQueue(String msg){
        instance.msgQueue.offer(msg);
    }
    public String sendMsg(){
        String msg = null;
        while ((msg=msgQueue.poll())!=null){
            return msg;
        }
        return msg;
    }
    public MessageQueue filter(int resultCode,String method){
        // TODO 逻辑处理 给每条消息设置时间间隔
        Log.i(TAG,"resultCode("+resultCode+")("+method+")");
        instance.addQueue(IOTResCodeUtil.transformStr(resultCode,mContext));
        return instance;
    }
    public void ondestroy(){
        if(instance!=null){
            instance.msgQueue = null;
            instance.mContext = null;
            instance = null;
        }
    }
}
