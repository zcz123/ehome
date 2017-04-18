package cc.wulian.lan;

public interface LanSocketConnectionHandler {
	public void connectionBroken(int reason);
	public void receviedMessage(String msg);
}
