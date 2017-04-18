package cc.wulian.smarthomev5.receiver;
import cc.wulian.smarthomev5.entity.BaiduPushEntity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class OperBaiduReceiver extends BroadcastReceiver{
	public final static String ACTION = "ioc_msg";
	public final static String REGISTER = "REGISTER";
	public final static String PUHS_MESSAGE ="PUHS_MESSAGE";
	public final static String PUST_ENTITY = "PUST_ENTITY";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null){
			String msgType = (String) intent.getSerializableExtra("MSGTYPE");
			if(msgType.equals(REGISTER)){
				doReg(intent);
			} else if(msgType.equals(PUHS_MESSAGE)){
				BaiduPushEntity baiduPushEntity =
						(BaiduPushEntity) intent.getSerializableExtra(PUST_ENTITY);
				if(baiduPushEntity!=null){
					doPush(baiduPushEntity);
				}
			}
		}
	}
	public void doReg(Intent mIntent){

	}
	public void doPush(BaiduPushEntity baiduPushEntity){

	}
}
