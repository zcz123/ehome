package cc.wulian.smarthomev5.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cc.wulian.smarthomev5.fragment.singin.QRScanFragmentV5;
import cc.wulian.smarthomev5.fragment.singin.QRScanFragmentV5_Horiz;

public class QRScanActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment qrscanFragment=null;
        boolean hitvScanJudge=false;
        Intent intent=getIntent();
        if(intent!=null){
            hitvScanJudge=intent.getBooleanExtra("HitvScanJudge",false);
        }
        if(hitvScanJudge){
            qrscanFragment=new QRScanFragmentV5_Horiz();
        }else {
            qrscanFragment=new QRScanFragmentV5();
        }
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, qrscanFragment).commit();
    }

//	@Override
//	protected boolean finshSelf() {
//		return false;
//	}
    
}