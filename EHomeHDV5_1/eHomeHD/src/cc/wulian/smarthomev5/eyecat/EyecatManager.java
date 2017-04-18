package cc.wulian.smarthomev5.eyecat;

import android.util.Log;

import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.utils.Method;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;


/**
 * Created by Administrator on 2017/4/18.
 */

public class EyecatManager {
    public static final String DISTRIBUTE_URL = "thirdparty.ecamzone.cc:8443";
    public static final String APPKEY = "FNXiNhNZnRar5QbAWYJ2QX4PNfdkwNNP";
    public static final String KEYID = "a9048a3c38de2d7a";
    private volatile boolean isLogined = false;
    private static EyecatManager instance = new EyecatManager();
    private Map<String,List<PacketListener>> listeners = new HashMap<String,List<PacketListener>>();
    private Map<String,EyecatDevice> devicesMap = new HashMap<String,EyecatDevice>();
    private PacketListener loginPacket = new PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_EQUES_SDK_LOGIN;
        }

        @Override
        public void processPacket(JSONObject object) {
            String code = object.optString("code");
            if("4000".equals(code)){
                isLogined = true;
            }
        }
    };
    private ICVSSListener listener = new ICVSSListener() {
        @Override
        public void onDisconnect(int i) {
            Log.i("eyecat:","disconnect:"+i);
            isLogined = false;
        }

        @Override
        public void onPingPong(int i) {

        }

        @Override
        public void onMeaasgeResponse(JSONObject jsonObject) {
            Log.i("eyecat:",jsonObject.toString());
            String method = jsonObject.optString("method");
            if(listeners.containsKey(method)){
                List<PacketListener> ls = listeners.get(method);
                if(ls != null){
                    for(PacketListener l :ls){
                        l.processPacket(jsonObject);
                    }
                }
            }
        }
    };
    public ICVSSUserInstance icvss = ICVSSUserModule.getInstance(listener).getIcvss();
    public static String username = null;
    static{
        String userId = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.USERID);
        String endPrexix = MD5Util.encrypt(userId+"_$LKl1as34d5fc");
        endPrexix = endPrexix.substring(endPrexix.length()-10);
        username = userId+"_"+endPrexix;
        Log.i("ykang_username:",username);
    }
    public static EyecatManager getInstance(){
        return instance;
    }
    public void login(){
        if(!isLogined){
            EyecatManager.getInstance().getICVSSUserInstance().equesLogin(MainApplication.getApplication(), DISTRIBUTE_URL,username,APPKEY);
            List<PacketListener> loginL = listeners.get(loginPacket.getMenthod());
            if(loginL == null){
                loginL = new ArrayList<PacketListener>();
                listeners.put(loginPacket.getMenthod(),loginL);
            }
            if(!loginL.contains(loginPacket)){
                loginL.add(loginPacket);
            }
        }
    }
    public ICVSSUserInstance getICVSSUserInstance(){
        return icvss;
    }
    public void addPacketListener(PacketListener listener){
        List<PacketListener> ls = listeners.get(listener.getMenthod());
        if(ls == null){
            ls = new ArrayList<PacketListener>();
            listeners.put(listener.getMenthod(),ls);
        }
        ls.add(listener);
    }
    public void removePacketListener(PacketListener listener){
        List<PacketListener> ls = listeners.get(listener.getMenthod());
        if(ls != null){
            ls.remove(listener);
        }
    }

    public void putDevice(EyecatDevice device){
        devicesMap.put(device.getBid(),device);
    }
    public void removeDevice(EyecatDevice device){
        devicesMap.remove(device);
    }
    public EyecatDevice getDevice(String bid){
        return devicesMap.get(bid);
    }
    public static abstract class PacketListener{
        private String menthod;

        public String getMenthod() {
            return menthod;
        }

        public void setMenthod(String menthod) {
            this.menthod = menthod;
        }

        public abstract  void processPacket(JSONObject object);
    }

    public static class EyecatDevice{
        private String uid;
        private String bid;
        private int status;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
