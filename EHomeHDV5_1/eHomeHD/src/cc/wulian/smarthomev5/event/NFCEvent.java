package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.entity.NFCEntity;
/**
 * when we use nfc, will receive three action
 */
public class NFCEvent
{
	private static final String TAG = NFCEvent.class.getSimpleName();


	public final String action;
	public final boolean writeMode;
	public final List<NFCEntity> nfcDataList;
	public final NFCEntity mifareSectorInfo;

	public NFCEvent( String action, boolean writeMode, List<NFCEntity> nfcDataList, NFCEntity mifareSectorInfo )
	{
		this.action = action;
		this.writeMode = writeMode;
		this.nfcDataList = nfcDataList;
		this.mifareSectorInfo = mifareSectorInfo;
	}

	@Override
	public String toString() {
		return TAG + ":{" + "action:{" + action + "}" + ", writeMode:{" + writeMode + "}" + ", nfcDataList:{" + nfcDataList + "}" + ", mifareSectorInfo:{"
				+ mifareSectorInfo + "}" + "}";
	}
}
