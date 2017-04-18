package cc.wulian.smarthomev5.entity;

import java.io.Serializable;

import cc.wulian.smarthomev5.R;

public class IRTypeEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static String TYPE_GENERAL = "00";
	public static String TYPE_AIR_CONDITION = "01";
	public static String TYPE_STB = "02";

	public final String irType;
	public final int showName;

	public IRTypeEntity( String irType, int showName )
	{
		this.irType = irType;
		this.showName = showName;
	}

	public static IRTypeEntity createAirCondition(){
		return new IRTypeEntity(TYPE_AIR_CONDITION, R.string.device_ir_type_air_condition);
	}

	public static IRTypeEntity createSTBCondition(){
		return new IRTypeEntity(TYPE_STB, R.string.device_ir_type_stb);
	}

	public static IRTypeEntity createGeneralCondition(){
		return new IRTypeEntity(TYPE_GENERAL, R.string.device_ir_type_general);
	}
}