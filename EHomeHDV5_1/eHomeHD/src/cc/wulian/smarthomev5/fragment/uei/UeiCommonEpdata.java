package cc.wulian.smarthomev5.fragment.uei;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl.Constants;
import cc.wulian.smarthomev5.tools.SendMessage;

import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLToast;

/*----------------------这个类的组装命令的说明-------------------
 * 
 * 
 * 
 ************************************************************/

/**
 * 用于组装 UEI 控制命令的类
 * @author yuxiaoxuan
 *
 */
public class UeiCommonEpdata {
	private String log_tag="UeiCommonEpdata";
	public UeiCommonEpdata(){
		
	}
	public UeiCommonEpdata(String gwID,String devID,String ep){
		this.gwID=gwID;
		this.devID=devID;
		this.ep=ep;
	}
	private String gwID;
	private String devID;
	private String ep;
	private String eptype="23";//UEI设备，固定为23
	public void setGwID(String gwID){
		this.gwID=gwID;
	}
	public void setDevID(String devID){
		this.devID=devID;
	}
	public void setEp(String ep){
		this.ep=ep;
	}
	/**
	 * 执行12命令
	 * @param epData 需要执行的数据
	 */
	public void sendCommand12(final Context context, String epData){
    	JSONObject jsonObj = new JSONObject();
        jsonObj.put("cmd","12");
		jsonObj.put("gwID", gwID);
		jsonObj.put("devID", devID);
		jsonObj.put("ep", ep);
		jsonObj.put("epType", eptype);
		jsonObj.put("epData", epData.toUpperCase());
        String jsonData = jsonObj.toString();
        com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(jsonData);
        String gwID = msgBody.getString(Constants.GATEWAYID);
//		NetSDK.sendControlDevMsg(gwID,devID,ep,eptype,epData.toUpperCase());
		SendMessage.sendControlDevMsg(gwID,devID,ep,eptype,epData.toUpperCase());
    }
	
	 /**
		 * 根据遥控类型组装数据</br>
		 * 来自：“i-One Integrated QS IRB Product Specification (#2567).doc”中的“4.   Device Type Table”
		 * @param deviceType 遥控器类型 
		 * 
		 * */
	public static String matchModelToString(String deviceType){
	    	String matchCode="";
	    	if(deviceType.equals("T")||deviceType.equals("!")){//电视、投影仪
	    		matchCode = "00";
	    	}else if(deviceType.equals("C")){//机顶盒
	    		matchCode = "01";
	    	}else if(deviceType.equals("N")){//网络电视
	    		matchCode = "02";
	    	}else if(deviceType.equals("S")){//
	    		matchCode = "03";
	    	}else if(deviceType.equals("V")){//VCR
	    		matchCode = "04";
	    	}else if(deviceType.equals("Y")){//DVD
	    		matchCode = "06";
	    	}else if(deviceType.equals("R")||deviceType.equals("M")){//CD
	    		matchCode = "07";
	    	}else if(deviceType.equals("A")){//功放
	    		matchCode = "08";
	    	}else if(deviceType.equals("D")){
	    		matchCode = "09";
	    	}else if(deviceType.equals("H")){
	    		matchCode = "10";
	    	}
			return matchCode;
	    }
	/**
	 * 获取用于UEI遥控中的普通按键发送命令</br>
	 * 来自：“i-One Integrated QS IRB Product Specification (#2567).doc”中的“4.   Device Type Table”
	 * */
	public String getEpData(String deviceCode,String keyCode,String keyFlag){

    	StringBuilder sb = new StringBuilder();
    	sb.append("0A000801");
    	/*String fristCode = deviceCode.substring(0,1);
    	String lastCode = Integer.toHexString(Integer.parseInt(deviceCode.substring(1))).toUpperCase();
    	if(lastCode.length()==1){
    		lastCode = "000"+lastCode;
    	}else if(lastCode.length()==2){
    		lastCode = "00"+lastCode;
    	}else if(lastCode.length()==3){
    		lastCode = "0"+lastCode;
    	}
    	parseDevideCode=UeiCommonEpdata.matchModelToString(fristCode)+lastCode;*/
		String parseDevideCode = getParseDevideCode(deviceCode);
    	sb.append(parseDevideCode);
    	int int10=Integer.parseInt(keyCode);
    	String str16=Integer.toHexString(int10);
    	String str16KeyCode=StringUtil.appendLeft(str16, 2, '0').toUpperCase();
    	sb.append(str16KeyCode);
    	sb.append(keyFlag);
    	sb.append("0000");
    	System.out.println("组装的数据"+sb.toString());
		return sb.toString();
    }
	public String getParseDevideCode(String deviceCode){
		String fristCode = deviceCode.substring(0,1);
		String lastCode = Long.toHexString(Long.parseLong(deviceCode.substring(1))).toUpperCase();
		if(lastCode.length()==1){
			lastCode = "000"+lastCode;
		}else if(lastCode.length()==2){
			lastCode = "00"+lastCode;
		}else if(lastCode.length()==3){
			lastCode = "0"+lastCode;
		}
		String parseDevideCode=UeiCommonEpdata.matchModelToString(fristCode)+lastCode;
		return parseDevideCode;
	}
    /**
	 * 获取用于UEI遥控中的学习按键发送命令</br>
	 * 来自：“i-One Integrated QS IRB Product Specification (#2567).doc”中的“4.   Device Type Table”
	 * */
    public String getEpDataForStudy(String learncode){
    	String epdata="";
    	if(!StringUtil.isNullOrEmpty(learncode)){
    		StringBuilder sb = new StringBuilder();
        	sb.append("0A000801");
        	sb.append("00");//DeviceType，设备类型
        	sb.append("0000");//CodesetNum,码库
        	sb.append("00");//keyCode
        	sb.append("A0");//keyFlag
        	int int10=Integer.parseInt(learncode);
        	String str16=Integer.toHexString(int10);
        	String sendIrCode=StringUtil.appendLeft(str16, 4, '0').toUpperCase();
        	sb.append(sendIrCode);//IRCode
        	epdata=sb.toString();
        	System.out.println("组装的数据"+sb.toString());
    	} 
		return epdata;
    }
    /**
     * 该命令将使UEI处于学习状态
     * @param studyCode 学习码
     * @return
     */
    public String getEpDataForUeiStudy(String studyCode){
    	String epdata=String.format("0A000307%s", StringUtil.appendLeft(studyCode, 4, '0'));
    	return epdata;
    }
	/**
	 * 获取12命令，发送该命令将直接控制空调
	 * @param sendData
	 * @return
	 */
    public String getEpdataForAirRun(String sendData){
    	String epData="";
		if(!StringUtil.isNullOrEmpty(sendData)){
			String sendeDataStr=String.format("10000190%s", sendData);
			String sendeDataLengthStr=Integer.toHexString(sendeDataStr.length()/2);
			epData =String.format("0A00%s%s", sendeDataLengthStr,sendeDataStr);	
		}else{
			Log.d(log_tag, "sendData为空！");
		}
		return epData.toUpperCase();
    }
    /**
     * 获取12命令，该命令将保存空调快捷码及实际需要发送的命令
     * @param sendData 给空调发的命令
     * @param userIndex 空调快捷码
     * @return
     */
    public String getEpdataForAirSave(String sendData,String userIndex){
    	String epData="";
		if(!StringUtil.isNullOrEmpty(sendData)){
			String sendeDataStr=String.format("10000190%s", sendData);	
			String sendeDataLengthStr=Integer.toHexString(sendeDataStr.length()/2);
			epData =String.format("0B00%s%s%s", sendeDataLengthStr,sendeDataStr,userIndex);	
		}else{
			Log.d(log_tag, "sendData为空！");
		}
		return epData.toUpperCase();
    }
}
