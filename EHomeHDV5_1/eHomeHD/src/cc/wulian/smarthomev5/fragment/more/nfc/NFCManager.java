package cc.wulian.smarthomev5.fragment.more.nfc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.NFCDao;
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.event.SocialEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.HyphenateManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DateUtil;
import de.greenrobot.event.EventBus;

public class NFCManager {

	private static NFCManager instance = new NFCManager();
	private MainApplication app = MainApplication.getApplication();
	private boolean isWriteMode = false;
	private boolean isExecute = true;
	private List<NFCListener> listeners = new ArrayList<NFCListener>();
	private List<NFCEntity> sceneNFCEntitys  = new ArrayList<NFCEntity>();
	private List<NFCEntity> deviceNFCEntitys  = new ArrayList<NFCEntity>();
	private NFCDao nfcDao = NFCDao.getInstance();
	private AccountManager accoutManager = AccountManager.getAccountManger();
	private String nfcMessage; //防胁迫消息
	public static NFCManager getInstance(){
		return instance;
	}
	private void createDefaultNFCEntityAboutScene() {
		NFCEntity nfcEntity = new NFCEntity();
		nfcEntity.setID(CmdUtil.ID_UNKNOW);
		nfcEntity.setType(NFCEntity.TYPE_SCENE);
		nfcEntity.setGwID(accoutManager.getmCurrentInfo().getGwID());
		sceneNFCEntitys.add(0,nfcEntity);
	}
	
	public NFCEntity getSceneInfo(){
		if(sceneNFCEntitys.size() <= 0){
			createDefaultNFCEntityAboutScene();
		}
		NFCEntity info =  sceneNFCEntitys.get(0);
		return info;
	}
	public void setSceneInfo(NFCEntity info){
		NFCEntity currentSceneInfo =  getSceneInfo();
		currentSceneInfo.setID(info.getID());
		currentSceneInfo.setType(NFCEntity.TYPE_SCENE);
		currentSceneInfo.setGwID(info.getGwID());
	}
	public void clearDevices(){
		deviceNFCEntitys.clear();
	}
	public void clear(){
		sceneNFCEntitys.clear();
		deviceNFCEntitys.clear();
		nfcMessage=null;
	}
	public void addDeviceInfo(NFCEntity info){
		int i= 0 ;
		while(i < deviceNFCEntitys.size()){
			NFCEntity currentDeviceInfo = deviceNFCEntitys.get(i);
			if(currentDeviceInfo.equals(info))
				break;
			i++;
		}
		if(i == deviceNFCEntitys.size()){
			deviceNFCEntitys.add(info);
		}
	}
	public void removeDeviceInfo(NFCEntity info){
		int i= 0 ;
		while(i < deviceNFCEntitys.size()){
			NFCEntity currentDeviceInfo = deviceNFCEntitys.get(i);
			if(currentDeviceInfo.equals(info))
				break;
			i++;
		}
		if(i < deviceNFCEntitys.size()){
			deviceNFCEntitys.remove(info);
		}
	}
	
	public List<NFCEntity> getDeviceNFCEntitys() {
		return deviceNFCEntitys;
	}
	public List<NFCEntity> getAllNFCEntitys(){
		ArrayList<NFCEntity> results = new ArrayList<NFCEntity>();
		NFCEntity sceneInfo = getSceneInfo();
		results.add(sceneInfo);
		results.addAll(deviceNFCEntitys);
		return results;
	}
	public void fireNFCListeners( Intent intent){
		for(NFCListener l : listeners){
			if(!isWriteMode){
				if(isExecute){
					l.onExecute(intent);
				}else{
					l.onRead(intent);
				}
			}
			else
				l.onWrite(intent);
		}
	}
	public void addNFCListener(NFCListener listener){
		listeners.add(listener);
	}
	public void removeNFCListener(NFCListener listener){
		listeners.remove(listener);
	}
	
	public boolean isWriteMode() {
		return isWriteMode;
	}
	public void setWriteMode(boolean isWriteMode) {
		this.isWriteMode = isWriteMode;
	}
	
	public boolean isExecute() {
		return isExecute;
	}
	public void setExecute(boolean isExecute) {
		this.isExecute = isExecute;
	}
	public void dispatchComingMessage(Intent intent){
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ){
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			byte[] tagId = tagFromIntent.getId();
	        String serialId = StringUtil.bytesToHexString(tagId);
	        if(isWriteMode){
	        	writeData(serialId);
	        }else{
	        	readData(serialId);
	        }
		}

	}
    private void readData(String nfcUID) {
    	try{
	    	NFCEntity arg = new NFCEntity();
	    	arg.setGwID(accoutManager.getmCurrentInfo().getGwID());
	    	arg.setNfcUID(nfcUID);
    		nfcMessage=Preference.getPreferences().getString(IPreferenceKey.P_KEY_NFC_MESSAGE+nfcUID,"");	    	
			List<NFCEntity> entites = nfcDao.findListAll(arg);
			for(NFCEntity e : entites){
				Logger.debug("read nfc data:"+e.toString());
				if(NFCEntity.TYPE_SCENE.equals(e.getType())){
					setSceneInfo(e);
				}else{
					addDeviceInfo(e);
				}
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	private void writeData(String nfcUID) {
		try{
			Preference.getPreferences().putString(IPreferenceKey.P_KEY_NFC_MESSAGE+nfcUID, nfcMessage);
			NFCEntity deleteEntity = new NFCEntity();
			deleteEntity.setGwID(accoutManager.getmCurrentInfo().getGwID());
			deleteEntity.setNfcUID(nfcUID);
			nfcDao.delete(deleteEntity);
			for(NFCEntity entity :  getAllNFCEntitys()){
				try{
					entity.setNfcUID(nfcUID);
					entity.setGwID(accoutManager.getmCurrentInfo().getGwID());
					nfcDao.insert(entity);
					Logger.debug("write nfc data:"+entity.toString());
				}catch(Exception e){
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void parse(){
			NFCEntity sceneInfoNFCEntity = getSceneInfo();
			SceneInfo sceneInfo = app.sceneInfoMap.get(accoutManager.getmCurrentInfo().getGwID() + sceneInfoNFCEntity.getID());
			if (sceneInfo != null){
				SceneInfo newSceneInfo = sceneInfo.clone();
				newSceneInfo.setStatus(CmdUtil.SCENE_USING);
				SceneManager.switchSceneInfo(app, newSceneInfo, false);
			}
			DeviceCache cache = DeviceCache.getInstance(app);
			for(NFCEntity devNFCEntity : getDeviceNFCEntitys()){
				WulianDevice device = cache.getDeviceByID(app, accoutManager.getmCurrentInfo().getGwID(), devNFCEntity.getID());
				if (device != null && device.isDeviceOnLine()){
					String ep = devNFCEntity.getEp();
					String epType = devNFCEntity.getEpType();
					String epData = devNFCEntity.getEpData();
					if(StringUtil.isNullOrEmpty(ep)){
						ep = WulianDevice.EP_0;
					}
					device.controlDevice(ep, epType, epData);
				}
			}
			if(nfcMessage!=null &&nfcMessage.length()>0){
				sendSocialMessage(nfcMessage);
			}		
	}

	//发送社交消息
	private void sendSocialMessage(String data){
		//创建信息体
		EMMessage message = EMMessage.createTxtSendMessage(data, HyphenateManager.getHyphenateManager().getGroupID());
		//设置聊天类型
		message.setChatType(EMMessage.ChatType.GroupChat);
		EMClient.getInstance().chatManager().sendMessage(message);
		EventBus.getDefault().post(new SocialEvent(new SocialEntity()));
	}
	
	public interface NFCListener{
		public void onRead(Intent intent);
		public void onWrite(Intent intent);
		public void onExecute(Intent intent);
	}
	
	public static abstract class AbstractNFCListener implements NFCListener{
		@Override
		public void onRead(Intent intent) {
			
		}

		@Override
		public void onWrite(Intent intent) {
			
		}
		@Override
		public void onExecute(Intent intent) {
			
		}
	}

	public String getNfcMessage() {
		return nfcMessage;
	}
	public void setNfcMessage(String nfcMessage) {
		this.nfcMessage = nfcMessage;
	}
	
	
}
