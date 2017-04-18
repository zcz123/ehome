package com.tutk.IOTC;
public interface DecodeListener {
	
	public void decodeAbnormalCondition(Camera mCamera,int channel ,int worngMode,String wrongMethod);
	
	public void handover(int worngMode);
}
