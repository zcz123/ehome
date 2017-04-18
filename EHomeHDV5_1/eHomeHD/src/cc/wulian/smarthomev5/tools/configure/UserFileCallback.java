package cc.wulian.smarthomev5.tools.configure;

public interface UserFileCallback {
	public void onNormal(UserFileModel olderModer);

	public void onNeedDownload(UserFileModel olderModer, String fileName);
}