package cc.wulian.smarthomev5.entity;


public class NFCEntity implements Cloneable
{
	public static final String TYPE_SCENE = "00";
	public static final String TYPE_DEVICE = "01";

	private String nfcUID= "";
	private String gwID;
	private String ID = "";
	private String type = "";
	private String ep = "";
	private String epType = "";
	private String epData = "";

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getID(){
		return ID;
	}

	public void setID( String iD ){
		ID = iD;
	}

	public String getType(){
		return type;
	}

	public void setType( String type ){
		this.type = type;
	}

	public String getEp(){
		return ep;
	}

	public void setEp( String ep ){
		this.ep = ep;
	}

	public String getEpType(){
		return epType;
	}

	public void setEpType( String epType ){
		this.epType = epType;
	}

	public String getNfcUID() {
		return nfcUID;
	}

	public void setNfcUID(String nfcUID) {
		this.nfcUID = nfcUID;
	}

	public String getEpData() {
		return epData;
	}

	public void setEpData(String epData) {
		this.epData = epData;
	}

	@Override
	public boolean equals( Object o ){
		if(!(o instanceof NFCEntity))
			return false;
		return equals((NFCEntity)o);
	}
	@Override
	public String toString() {
		return this.nfcUID+";"+this.ID +";"+this.type+";"+this.ep+";"+this.epType+";"+this.epData;
	}
	public boolean equals( NFCEntity mSectorInfo ){
		String nfcUID = mSectorInfo.getNfcUID();
		String ID = mSectorInfo.getID();
		String type = mSectorInfo.getType();
		String ep = mSectorInfo.getEp();
		String epType = mSectorInfo.getEpType();

		return (this.nfcUID.equals(nfcUID) && this.ID.equals(ID) && this.type.equals(type) && this.ep.equals(ep) && this.epType
				.equals(epType));
	}
}
