package cc.wulian.app.model.device.impls.controlable.aircondtion;

import android.content.Context;
import android.view.View;

public class Daikin_AirCondition implements AirCondition {
	public String gwID;
	public String devID;
	public String keyID;
	public String keyName;
	// 状态信息
	public String curTemp;
	public String curSetTemp;
	public String curModel;
	public String curRunStatus;
	public String curWindPower;
	public String curWindDirection;
	public String curStadus;
	public String curDevName;
	public String curID;
	// 性能信息
	public boolean hasBlowModel;
	public boolean hasCoolModel;
	public boolean hasHotModel;
	public boolean hasAutoModel;
	public boolean hasArefactionModel;
	public boolean hasWindDirectionSet;
	public String curWindSpeed;
	public boolean hasAirVolumeAdjust;
	public String coolMaxTemp;
	public String coolMinTemp;
	public String hotMaxTemp;
	public String hotMinTemp;

	// 包含不做处理的数据,需要传递整体数据
	public String statusData_1;
	public String statusData_2;
	public String controlData_1;
	public String controlData_2;

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getDevID() {
		return devID;
	}

	public void setDevID(String devID) {
		this.devID = devID;
	}

	public String getKeyID() {
		return keyID;
	}

	public void setKeyID(String keyID) {
		this.keyID = keyID;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getCurTemp() {
		return curTemp;
	}

	public void setCurTemp(String curTemp) {
		this.curTemp = curTemp;
	}

	public String getCurSetTemp() {
		return curSetTemp;
	}

	public void setCurSetTemp(String curSetTemp) {
		this.curSetTemp = curSetTemp;
	}

	public String getCurModel() {
		return curModel;
	}

	public void setCurModel(String curModel) {
		this.curModel = curModel;
	}

	public String getCurRunStatus() {
		return curRunStatus;
	}

	public void setCurRunStatus(String curRunStatus) {
		this.curRunStatus = curRunStatus;
	}

	public String getCurWindPower() {
		return curWindPower;
	}

	public void setCurWindPower(String curWindPower) {
		this.curWindPower = curWindPower;
	}

	public String getCurWindDirection() {
		return curWindDirection;
	}

	public void setCurWindDirection(String curWindDirection) {
		this.curWindDirection = curWindDirection;
	}

	public String getCurStadus() {
		return curStadus;
	}

	public void setCurStadus(String curStadus) {
		this.curStadus = curStadus;
	}

	public String getCurDevName() {
		return curDevName;
	}

	public void setCurDevName(String curDevName) {
		this.curDevName = curDevName;
	}

	public String getCurID() {
		return curID;
	}

	public void setCurID(String curID) {
		this.curID = curID;
	}

	public boolean isHasBlowModel() {
		return hasBlowModel;
	}

	public void setHasBlowModel(boolean hasBlowModel) {
		this.hasBlowModel = hasBlowModel;
	}

	public boolean isHasCoolModel() {
		return hasCoolModel;
	}

	public void setHasCoolModel(boolean hasCoolModel) {
		this.hasCoolModel = hasCoolModel;
	}

	public boolean isHasHotModel() {
		return hasHotModel;
	}

	public void setHasHotModel(boolean hasHotModel) {
		this.hasHotModel = hasHotModel;
	}

	public boolean isHasAutoModel() {
		return hasAutoModel;
	}

	public void setHasAutoModel(boolean hasAutoModel) {
		this.hasAutoModel = hasAutoModel;
	}

	public boolean isHasArefactionModel() {
		return hasArefactionModel;
	}

	public void setHasArefactionModel(boolean hasArefactionModel) {
		this.hasArefactionModel = hasArefactionModel;
	}

	public boolean isHasWindDirectionSet() {
		return hasWindDirectionSet;
	}

	public void setHasWindDirectionSet(boolean hasWindDirectionSet) {
		this.hasWindDirectionSet = hasWindDirectionSet;
	}

	public String getCurWindSpeed() {
		return curWindSpeed;
	}

	public void setCurWindSpeed(String curWindSpeed) {
		this.curWindSpeed = curWindSpeed;
	}

	public boolean isHasAirVolumeAdjust() {
		return hasAirVolumeAdjust;
	}

	public void setHasAirVolumeAdjust(boolean hasAirVolumeAdjust) {
		this.hasAirVolumeAdjust = hasAirVolumeAdjust;
	}

	public String getCoolMaxTemp() {
		return coolMaxTemp;
	}

	public void setCoolMaxTemp(String coolMaxTemp) {
		this.coolMaxTemp = coolMaxTemp;
	}

	public String getCoolMinTemp() {
		return coolMinTemp;
	}

	public void setCoolMinTemp(String coolMinTemp) {
		this.coolMinTemp = coolMinTemp;
	}

	public String getHotMaxTemp() {
		return hotMaxTemp;
	}

	public void setHotMaxTemp(String hotMaxTemp) {
		this.hotMaxTemp = hotMaxTemp;
	}

	public String getHotMinTemp() {
		return hotMinTemp;
	}

	public void setHotMinTemp(String hotMinTemp) {
		this.hotMinTemp = hotMinTemp;
	}

	@Override
	public void open() {

	}

	@Override
	public void close() {

	}

	@Override
	public void addTemp() {

	}

	@Override
	public void reduceTemp() {

	}

	@Override
	public void addWindPower() {

	}

	@Override
	public void reduceWindPower() {

	}

	@Override
	public void setModel(String model) {

	}

	@Override
	public void setWindDirction(String dirction) {

	}

	@Override
	public View getView(Context context) {
		return null;
	}

	public String getStatusData_1() {
		return statusData_1;
	}

	public void setStatusData_1(String statusData_1) {
		this.statusData_1 = statusData_1;
	}

	public String getStatusData_2() {
		return statusData_2;
	}

	public void setStatusData_2(String statusData_2) {
		this.statusData_2 = statusData_2;
	}

	public String getControlData_1() {
		return controlData_1;
	}

	public void setControlData_1(String controlData_1) {
		this.controlData_1 = controlData_1;
	}

	public String getControlData_2() {
		return controlData_2;
	}

	public void setControlData_2(String controlData_2) {
		this.controlData_2 = controlData_2;
	}

}
