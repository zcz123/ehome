package cc.wulian.app.model.device.impls.controlable.toc;

import android.database.Cursor;
import cc.wulian.smarthomev5.databases.entitys.TwoOutputConverterRecord;

public class TwoOutputEntity {

	public String gwID;
	public String devID;
	public String keyID;
	public String keyName;
	public String oneType;
	public String oneValue;
	public String twoType;
	public String twoValue;

	public TwoOutputEntity() {
	}

	public TwoOutputEntity(Cursor cursor) {
		gwID = cursor.getString(TwoOutputConverterRecord.POS_GW_ID);
		devID = cursor.getString(TwoOutputConverterRecord.POS_DEV_ID);
		keyID = cursor.getString(TwoOutputConverterRecord.POS_KEY_ID);
		keyName = cursor.getString(TwoOutputConverterRecord.POS_KEY_NAME);
		oneType = cursor.getString(TwoOutputConverterRecord.POS_ONE_TYPE);
		oneValue = cursor.getString(TwoOutputConverterRecord.POS_ONE_VALUE);
		twoType = cursor.getString(TwoOutputConverterRecord.POS_TWO_TYPE);
		twoValue = cursor.getString(TwoOutputConverterRecord.POS_TWO_VALUE);

	}

	public String getKeyID() {
		return keyID;
	}

	public void setKeyID(String keyID) {
		this.keyID = keyID;
	}

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

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getOneType() {
		return oneType;
	}

	public void setOneType(String oneType) {
		this.oneType = oneType;
	}

	public String getOneValue() {
		return oneValue;
	}

	public void setOneValue(String oneValue) {
		this.oneValue = oneValue;
	}

	public String getTwoType() {
		return twoType;
	}

	public void setTwoType(String twoType) {
		this.twoType = twoType;
	}

	public String getTwoValue() {
		return twoValue;
	}

	public void setTwoValue(String twoValue) {
		this.twoValue = twoValue;
	}

}
