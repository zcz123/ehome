package cc.wulian.smarthomev5.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCFragment;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCManager;

import com.yuantuo.customview.ui.WLToast;

public class NFCActivity extends EventBusActivity{
	public static final String IS_EXECUTE = "IS_EXECUTE";
	private NFCManager manager = NFCManager.getInstance();
	private NfcAdapter mNfcAdapter;
	private boolean isExecute = true;
	private Intent nfcIntent;
	private PendingIntent mNfcPendingIntent;
	private String[][] mTechLists ;
	private Intent comintIntent;
	private boolean isError = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.debug("NFC Activity onCreate");
		isExecute = getIntent().getBooleanExtra(IS_EXECUTE,true);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {  
			WLToast.showToast(this,this.getResources().getString(R.string.more_nfc_tag_not_support), WLToast.TOAST_SHORT);
            if(isExecute){
            	isError = true;
            	finish();
            	return ;
            }
        }  
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {  
        	WLToast.showToast(this, this.getResources().getString(R.string.more_nfc_tag_not_open), WLToast.TOAST_SHORT);
        	 if(isExecute){
        		 isError = true;
        		 finish();
        		 return ;
        	 }
        } 
        if(!mAccountManager.isConnectedGW()){
        	WLToast.showToast(this, this.getResources().getString(R.string.login_no_network_hint), WLToast.TOAST_SHORT);
        	if(isExecute){
        		 isError = true;
        		 finish();
        		 return ;
        	}
        }
        initNfcIntent();
        if(!isExecute){
        	NFCFragment fragement = new NFCFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content,fragement)
			.commit();
		}
	}
	private void initNfcIntent() {
		try{
			nfcIntent= new Intent(this, this.getClass());
	        mNfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
			mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}, new String[]{MifareUltralight.class.getName()}, new String[]{IsoDep.class.getName()}, new String[]{NfcA.class.getName()}, new String[]{NfcB.class.getName()}, new String[]{NfcF.class.getName()}, new String[]{NfcV.class.getName()}, new String[]{Ndef.class.getName()}, new String[]{NdefFormatable.class.getName()}};
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		comintIntent = intent;
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(isError){
			return ;
		}
		if(comintIntent == null)
			comintIntent = getIntent();
		dispatchOnResume();
		if(!StringUtil.isNullOrEmpty(comintIntent.getAction())){
			manager.fireNFCListeners(comintIntent);
		}
		if(isExecute){
			this.finish();
		 }
	}
	@Override
	protected void onPause() {
		if(isError)
			return ;
		dispatchOnPause();
		comintIntent = null;
		super.onPause();
	}
	private boolean ensureAdapter(){
		return mNfcAdapter != null && mNfcAdapter.isEnabled();
	}
	public void dispatchOnResume(){
		if (ensureAdapter()){
			mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, null, mTechLists);
		}
	}
	public void dispatchOnPause(){
		if (ensureAdapter()){
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
}
